package com.aura.wake.data.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsManager(context: Context) {

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    companion object {
        private const val TAG = "AnalyticsManager"
        
        // Event Names
        const val EVENT_SCREEN_VIEW = FirebaseAnalytics.Event.SCREEN_VIEW
        const val EVENT_ALARM_CREATED = "alarm_created"
        const val EVENT_ALARM_TOGGLED = "alarm_toggled"
        const val EVENT_ALARM_DELETED = "alarm_deleted"
        const val EVENT_ALARM_DUPLICATED = "alarm_duplicated"
        
        const val EVENT_PERMISSION_STATUS = "permission_status"
        const val EVENT_PERMISSION_GRANT_CLICK = "permission_grant_click"

        // Param Names
        const val PARAM_SCREEN_NAME = FirebaseAnalytics.Param.SCREEN_NAME
        const val PARAM_SCREEN_CLASS = FirebaseAnalytics.Param.SCREEN_CLASS
        const val PARAM_ALARM_ID = "alarm_id"
        const val PARAM_CHALLENGE_TYPE = "challenge_type"
        const val PARAM_ENABLED = "enabled"
        const val PARAM_PERMISSION_NAME = "permission_name"
        const val PARAM_STATUS = "status"
    }

    fun logScreenView(screenName: String) {
        Log.d(TAG, "Logging Screen View: $screenName")
        val bundle = Bundle().apply {
            putString(PARAM_SCREEN_NAME, screenName)
            putString(PARAM_SCREEN_CLASS, screenName) // Using screen name as class for simplicity in Compose
        }
        firebaseAnalytics.logEvent(EVENT_SCREEN_VIEW, bundle)
    }

    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        Log.d(TAG, "Logging Event: $eventName, Params: $params")
        val bundle = Bundle()
        for ((key, value) in params) {
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Double -> bundle.putDouble(key, value)
                else -> bundle.putString(key, value.toString())
            }
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun setUserProperty(name: String, value: String) {
        Log.d(TAG, "Setting User Property: $name = $value")
        firebaseAnalytics.setUserProperty(name, value)
    }
    
    // --- Helper Methods ---

    fun logAlarmCreated(challengeType: String) {
        logEvent(EVENT_ALARM_CREATED, mapOf(
            PARAM_CHALLENGE_TYPE to challengeType
        ))
    }

    fun logAlarmToggled(alarmId: String, isEnabled: Boolean) {
        logEvent(EVENT_ALARM_TOGGLED, mapOf(
            PARAM_ALARM_ID to alarmId,
            PARAM_ENABLED to isEnabled
        ))
    }

    fun logAlarmDeleted(alarmId: String) {
         logEvent(EVENT_ALARM_DELETED, mapOf(
            PARAM_ALARM_ID to alarmId
        ))
    }
    
    fun logPermissionStatus(permission: String, isGranted: Boolean) {
        // Log as user property for sticky status
        val status = if (isGranted) "granted" else "denied"
        setUserProperty("perm_$permission", status)
        
        // Also log as event for timeline
        logEvent(EVENT_PERMISSION_STATUS, mapOf(
            PARAM_PERMISSION_NAME to permission,
            PARAM_STATUS to status
        ))
    }
}
