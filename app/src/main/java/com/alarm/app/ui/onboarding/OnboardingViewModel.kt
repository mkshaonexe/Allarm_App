package com.alarm.app.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alarm.app.data.alarm.AlarmScheduler
import com.alarm.app.data.model.Alarm
import com.alarm.app.data.model.ChallengeType
import com.alarm.app.data.repository.AlarmRepository
import com.alarm.app.data.repository.SettingsRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class OnboardingViewModel(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Default to current time + 1 hour or 7:00 AM? User request image showed 07:00 AM.
    // Let's default to 7:00 AM.
    var selectedHour by mutableIntStateOf(7)
    var selectedMinute by mutableIntStateOf(0)
    
    // Default Sound - "Orkney" as per image, but we just need a string or resource.
    // We'll store the name.
    var selectedSound by mutableStateOf("Orkney")
    
    // Default Mission
    var selectedChallenge by mutableStateOf(ChallengeType.NONE)

    fun updateTime(hour: Int, minute: Int) {
        selectedHour = hour
        selectedMinute = minute
    }

    fun updateSound(soundName: String) {
        selectedSound = soundName
    }

    fun updateChallenge(challenge: ChallengeType) {
        selectedChallenge = challenge
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Create the alarm
            val alarm = Alarm(
                hour = selectedHour,
                minute = selectedMinute,
                isEnabled = true,
                daysOfWeek = emptySet(), // One-time alarm for now? Or Daily? Image didn't specify, but usually onboarding sets a daily or one-off. Let's make it daily for "Everyday" or just enabled. 
                // Let's assume enabled repeating for all days or just enabled one shot. 
                // Let's make it Simple Enabled (one shot if days empty, but we can default to Mon-Fri or All days if user wants 'Wake up').
                // Let's stick to emptySet (one-off) for start, or all days.
                // Actually, let's make it ALL DAYS (Daily) to be useful.
                // daysOfWeek = (1..7).toSet(), 
                // For now, let's stick to default empty (one-time) to avoid being too aggressive, or user can edit later.
                challengeType = selectedChallenge,
                label = "Wake Up" // Default label
                // sound logic needs to be handled in Alarm object if we support it. 
                // Looking at Alarm.kt, there is no sound field. It might be using system default or not implemented yet in model.
                // Accessing Alarm.kt from file view earlier... 
                //     val label: String? = null,
                //     val challengeType: ChallengeType = ChallengeType.NONE,
                //     val challengeConfig: String? = null 
                // No sound field in Alarm entity. I will skip saving sound for now and just save the alarm.
            )
            
            alarmRepository.insertAlarm(alarm)
            alarmScheduler.schedule(alarm)
            
            // Mark first run complete
            settingsRepository.setFirstRunCompleted()
            
            onSuccess()
        }
    }
}
