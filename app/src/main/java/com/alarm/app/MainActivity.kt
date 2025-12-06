package com.alarm.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alarm.app.ui.home.HomeScreen
import com.alarm.app.ui.theme.AllarmAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Show over lockscreen
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        enableEdgeToEdge()
        setContent {
            AllarmAppTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

                // Permission State
                var hasNotificationPermission by remember { mutableStateOf(false) }
                var hasOverlayPermission by remember { mutableStateOf(false) }

                // Function to check permissions
                fun checkPermissions() {
                    hasNotificationPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }

                    hasOverlayPermission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        android.provider.Settings.canDrawOverlays(context)
                    } else {
                        true
                    }
                }

                // Initial check
                LaunchedEffect(Unit) {
                    checkPermissions()
                }

                // Identify if we need to show the overlay permission screen
                // We show it if we have notification permission (or asked for it) BUT miss overlay permission.
                // However, the prompt says "ask first notification ... then the overlay".
                // So if we are creating the start destination, we need to be careful.

                // Lifecycle observer to re-check permission on resume (e.g. coming back from settings)
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            checkPermissions()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                // Request Notification Permission on startup (Android 13+)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    val permissionState = rememberPermissionState(
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) { isGranted ->
                        hasNotificationPermission = isGranted
                        // If granted, we re-check logic implicitly via state update
                    }
                    LaunchedEffect(Unit) {
                        if (!permissionState.status.isGranted) {
                            permissionState.launchPermissionRequest()
                        }
                    }
                }

                // Logic to determine initial screen
                val ringingParams = getRingingParams(intent)
                val isRinging = intent.getBooleanExtra("SHOW_ALARM_SCREEN", false)
                
                // If ringing, go to ringing.
                // If not ringing:
                // 1. If Overlay permission missing, go to "overlay_permission".
                // 2. Otherwise "home".
                // calculate startDestination only once or remember it? 
                // Navigation components don't like dynamic startDestination changes easily without logic.
                
                // We can use a Splash/Loading route or just logic. 
                // Let's stick to the logic: If we don't have overlay permission, that's the "blocking" screen.
                // Notification permission is asked via system dialog on top of whatever screen we are on.
                
                val startDestination = if (isRinging) {
                    "ringing"
                } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && 
                           !android.provider.Settings.canDrawOverlays(context)) { 
                           // Use raw check for initial state to avoid race condition with state var
                    "overlay_permission" 
                } else {
                    "home"
                }

                // Auto-navigation from Overlay to Home if permission granted
                LaunchedEffect(hasOverlayPermission) {
                    if (hasOverlayPermission) {
                         // Check if we are currently on overlay_permission screen to avoid random jumps
                         // But for simplicity, if we have permission, we shouldn't be on that screen.
                         val currentRoute = navController.currentDestination?.route
                         if (currentRoute == "overlay_permission") {
                             navController.navigate("home") {
                                 popUpTo("overlay_permission") { inclusive = true }
                             }
                         }
                    }
                }

                NavHost(navController = navController, startDestination = startDestination) {
                    composable("home") {
                        HomeScreen(navController = navController)
                    }
                    composable("profile") {
                        com.alarm.app.ui.profile.ProfileScreen(navController = navController)
                    }
                    composable("overlay_permission") {
                        com.alarm.app.ui.permission.OverlayPermissionScreen(
                            onGoToSettings = {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                    val intent = Intent(
                                        android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                        android.net.Uri.parse("package:$packageName")
                                    )
                                    startActivity(intent)
                                }
                            },
                            onClose = {
                                // User skipped or returned, navigate home? Or re-check?
                                // For now, let's navigate home to not block completely if they refuse
                                navController.navigate("home") {
                                    popUpTo("overlay_permission") { inclusive = true }
                                }
                            }
                        )
                    }
                     // Lifecycle observer to re-check permission on resume could be useful here, but simple button/nav works for now.
                     // A cleaner way is to use `LifecycleEventObserver` to check permission on `ON_RESUME`.
                     
                    composable("create_alarm") {
                        com.alarm.app.ui.alarm.AlarmScreen(navController = navController)
                    }
                    composable("edit_alarm/{alarmId}") { backStackEntry ->
                        val alarmId = backStackEntry.arguments?.getString("alarmId")
                        com.alarm.app.ui.alarm.AlarmScreen(navController = navController, alarmId = alarmId)
                    }
                    composable("ringing") {
                        // Pass parameters to the ringing screen
                        com.alarm.app.ui.ring.AlarmRingingScreen(
                            navController = navController,
                            alarmId = ringingParams.first,
                            initialChallengeTypeStr = ringingParams.second,
                            startChallengeImmediately = ringingParams.third
                        )
                    }
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update the intent 
        // Always recreate if we need to show alarm screen to ensure fresh state
        if (intent.getBooleanExtra("SHOW_ALARM_SCREEN", false)) {
            android.util.Log.d("MainActivity", "🔔 onNewIntent - navigating to ringing screen")
            recreate()
        }
    }

    private fun determineStartDestination(intent: Intent?): String {
        return if (intent?.getBooleanExtra("SHOW_ALARM_SCREEN", false) == true) {
            "ringing"
        } else {
            "home"
        }
    }
    
    // Returns Triple(alarmId, challengeType, startChallengeImmediately)
    private fun getRingingParams(intent: Intent?): Triple<String?, String?, Boolean> {
        val alarmId = intent?.getStringExtra("ALARM_ID")
        val challengeType = intent?.getStringExtra("CHALLENGE_TYPE")
        val startImmediate = intent?.getBooleanExtra("START_CHALLENGE", false) ?: false
        return Triple(alarmId, challengeType, startImmediate)
    }
}