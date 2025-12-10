package com.alarm.app.data.repository

import android.content.Context
import androidx.core.content.edit

interface SettingsRepository {
    fun isFirstRun(): Boolean
    fun setFirstRunCompleted()
}

class SharedPreferencesSettingsRepository(private val context: Context) : SettingsRepository {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    override fun isFirstRun(): Boolean {
        // Default to true. If key doesn't exist, it's the first run (or we just added this feature).
        return prefs.getBoolean("is_first_run", true)
    }

    override fun setFirstRunCompleted() {
        prefs.edit {
            putBoolean("is_first_run", false)
        }
    }
}
