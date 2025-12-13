package com.alarm.app.ui.mission

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.alarm.app.data.model.Difficulty
import com.alarm.app.data.model.MathMissionConfig
import com.alarm.app.data.model.QrMissionConfig
import com.alarm.app.data.model.TypingMissionConfig
import com.alarm.app.data.repository.SettingsRepository

class MissionSettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Math Mission State
    var mathDifficulty by mutableStateOf(Difficulty.MEDIUM)
    var mathProblemCount by mutableStateOf(3)
    
    // Typing Mission State
    var typingSentences by mutableStateOf<List<String>>(emptyList())
    var typingWordCount by mutableStateOf(5)
    
    // QR Mission State
    var qrContent by mutableStateOf<String?>(null)
    var qrLabel by mutableStateOf<String?>(null)

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val mathConfig = settingsRepository.getMathMissionConfig()
        mathDifficulty = mathConfig.difficulty
        mathProblemCount = mathConfig.problemCount
        
        val typingConfig = settingsRepository.getTypingMissionConfig()
        typingSentences = typingConfig.sentences
        typingWordCount = typingConfig.wordCount
        
        val qrConfig = settingsRepository.getQrMissionConfig()
        qrContent = qrConfig.qrContent
        qrLabel = qrConfig.qrLabel
    }

    fun saveMathSettings(difficulty: Difficulty, count: Int) {
        mathDifficulty = difficulty
        mathProblemCount = count
        settingsRepository.saveMathMissionConfig(MathMissionConfig(difficulty, count))
    }

    fun saveTypingSettings(sentences: List<String>, wordCount: Int) {
        typingSentences = sentences
        typingWordCount = wordCount
        settingsRepository.saveTypingMissionConfig(TypingMissionConfig(sentences, wordCount))
    }

    fun saveQrSettings(content: String, label: String) {
        qrContent = content
        qrLabel = label
        settingsRepository.saveQrMissionConfig(QrMissionConfig(content, label))
    }
}
