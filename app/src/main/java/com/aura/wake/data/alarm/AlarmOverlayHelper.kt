package com.aura.wake.data.alarm

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.aura.wake.data.model.ChallengeType
import com.aura.wake.ui.ring.AlarmRingingContent
import com.aura.wake.R

class AlarmOverlayHelper(private val context: Context) : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner, OnBackPressedDispatcherOwner {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    
    // Lifecycle Management for Compose
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val internalViewModelStore = ViewModelStore()
    private val dispatcher = OnBackPressedDispatcher {
        // Fallback action: if back is pressed and not handled by UI, remove overlay?
        // For an alarm, we typically want to block back, or maybe dismiss?
        // Let's safe-guard by just doing nothing or maybe logging. 
        // If we want it unskippable, do nothing.
    }

    init {
        savedStateRegistryController.performRestore(null)
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = internalViewModelStore
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val onBackPressedDispatcher: OnBackPressedDispatcher get() = dispatcher

    fun showOverlay(alarmId: String?, challengeTypeStr: String?) {
        try {
            if (overlayView != null) return // Already showing

            lifecycleRegistry.currentState = Lifecycle.State.CREATED
            lifecycleRegistry.currentState = Lifecycle.State.STARTED

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, // Full screen
                PixelFormat.TRANSLUCENT
            )
            
            // Critical: Allow touch events
            layoutParams.flags = layoutParams.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()

            // Use themed context to avoid crash
            val themedContext = ContextThemeWrapper(context, R.style.Theme_AllarmApp)
            val composeView = ComposeView(themedContext)

            // Attach Lifecycle Owners BEFORE setContent to avoid crash
            composeView.setViewTreeLifecycleOwner(this)
            composeView.setViewTreeViewModelStoreOwner(this)
            composeView.setViewTreeSavedStateRegistryOwner(this)
            // Note: ViewTreeOnBackPressedDispatcherOwner isn't a standard extension yet in all versions or libs, 
            // but we provide it via CompositionLocal below which is what Composable BackHandler needs.
            // If needed: composeView.setViewTreeOnBackPressedDispatcherOwner(this) (check imports)

            composeView.setContent {
                CompositionLocalProvider(LocalOnBackPressedDispatcherOwner provides this) {
                    val challengeType = try {
                        if (challengeTypeStr != null) ChallengeType.valueOf(challengeTypeStr) else ChallengeType.NONE
                    } catch (e: Exception) { ChallengeType.NONE }

                    AlarmRingingContent(
                        challengeType = challengeType,
                        startChallengeImmediately = false,
                        isPreview = false,
                        onSnooze = {
                            // Handle Snooze
                            if (alarmId != null) {
                                val intent = Intent(context, SnoozeReceiver::class.java).apply {
                                    action = "ACTION_SNOOZE"
                                    putExtra("ALARM_ID", alarmId)
                                }
                                context.sendBroadcast(intent)
                            } else {
                                context.stopService(Intent(context, AlarmService::class.java))
                            }
                            removeOverlay() // Close overlay on snooze
                        },
                        onDismiss = {
                            // Handle Dismiss
                            context.stopService(Intent(context, AlarmService::class.java))
                            removeOverlay()
                        },
                        onClosePreview = { /* Not applicable */ }
                    )
                }
            }

            lifecycleRegistry.currentState = Lifecycle.State.RESUMED

            windowManager.addView(composeView, layoutParams)
            overlayView = composeView
        } catch (e: Exception) {
            e.printStackTrace()
            // Ensure we don't leave the helper in a broken state
            overlayView = null
        }
    }

    fun removeOverlay() {
        if (overlayView != null) {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
            try {
                windowManager.removeView(overlayView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            overlayView = null
        }
    }
}
