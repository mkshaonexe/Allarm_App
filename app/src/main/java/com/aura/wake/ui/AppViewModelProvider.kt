package com.aura.wake.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.aura.wake.AlarmApplication
import com.aura.wake.ui.alarm.AlarmViewModel
import com.aura.wake.data.repository.SettingsRepository

object AppViewModelProvider {
    val Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as AlarmApplication
            val repository = application.container.alarmRepository
            val scheduler = application.container.alarmScheduler
            
            val settingsRepository = application.container.settingsRepository
            
            return when {
                modelClass.isAssignableFrom(AlarmViewModel::class.java) -> 
                    AlarmViewModel(repository, scheduler) as T
                modelClass.isAssignableFrom(com.aura.wake.ui.onboarding.OnboardingViewModel::class.java) ->
                    com.aura.wake.ui.onboarding.OnboardingViewModel(repository, scheduler, settingsRepository) as T
                modelClass.isAssignableFrom(com.aura.wake.ui.mission.MissionSettingsViewModel::class.java) ->
                    com.aura.wake.ui.mission.MissionSettingsViewModel(settingsRepository) as T
                else -> throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}

