package com.aura.wake.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log

import android.app.AlarmManager
import android.app.PendingIntent
import android.os.SystemClock

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val alarmId = intent.getStringExtra("ALARM_ID")
        val challengeType = intent.getStringExtra("CHALLENGE_TYPE")
        
        Log.d("AlarmReceiver", "🔔 Alarm received: $alarmId, Challenge: $challengeType, Action: $action")

        if (action == "ACTION_NOTIFICATION_DELETED") {
             Log.d("AlarmReceiver", "⚠️ Alarm Notification Deleted! Scheduling restart via AlarmManager.")
             scheduleServiceRestart(context, alarmId, challengeType)
             return
        }

        if (action == "ACTION_RESTART_ALARM_SERVICE") {
             Log.d("AlarmReceiver", "⏰ Alarm Manager triggered restart.")
             startAlarmService(context, alarmId, challengeType)
             return
        }

        // Acquire a wake lock to ensure device wakes up
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "AlarmApp:AlarmWakeLock"
        )
        wakeLock.acquire(10 * 60 * 1000L) // 10 minutes max

        try {
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra("ALARM_ID", alarmId)
                putExtra("CHALLENGE_TYPE", challengeType)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d("AlarmReceiver", "✅ AlarmService started")
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "❌ Failed to start AlarmService", e)
        } finally {
            // Release wake lock after a delay (service should acquire its own)
            wakeLock.release()
        }
    }

    private fun startAlarmService(context: Context, alarmId: String?, challengeType: String?) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "AlarmApp:AlarmWakeLockRestart"
        )
        wakeLock.acquire(10 * 60 * 1000L)

        try {
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra("ALARM_ID", alarmId)
                putExtra("CHALLENGE_TYPE", challengeType)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d("AlarmReceiver", "✅ AlarmService started (Restarted)")
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "❌ Failed to restart AlarmService", e)
        } finally {
            wakeLock.release()
        }
    }

    private fun scheduleServiceRestart(context: Context, alarmId: String?, challengeType: String?) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_RESTART_ALARM_SERVICE"
            putExtra("ALARM_ID", alarmId)
            putExtra("CHALLENGE_TYPE", challengeType)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(), // Unique request code per alarm
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = SystemClock.elapsedRealtime() + 100 // 100ms delay
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
             alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }
}

