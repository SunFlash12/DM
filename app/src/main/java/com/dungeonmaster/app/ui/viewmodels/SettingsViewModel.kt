package com.dungeonmaster.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungeonmaster.app.ai.AdventureSetting
import com.dungeonmaster.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val apiKey: String = "",
    val darkTheme: Boolean = true,
    val soundEffects: Boolean = true,
    val diceAnimation: Boolean = true,
    val autoRoll: Boolean = false,
    val adventureSetting: AdventureSetting = AdventureSetting.CLASSIC_FANTASY,
    val narratorVoice: Boolean = false,
    val isApiKeyVisible: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.geminiApiKey,
                settingsRepository.darkTheme,
                settingsRepository.soundEffectsEnabled,
                settingsRepository.diceAnimationEnabled,
                settingsRepository.autoRollEnabled,
                settingsRepository.adventureSetting,
                settingsRepository.narratorVoiceEnabled
            ) { values ->
                SettingsState(
                    apiKey = values[0] as String,
                    darkTheme = values[1] as Boolean,
                    soundEffects = values[2] as Boolean,
                    diceAnimation = values[3] as Boolean,
                    autoRoll = values[4] as Boolean,
                    adventureSetting = try {
                        AdventureSetting.valueOf(values[5] as String)
                    } catch (e: Exception) {
                        AdventureSetting.CLASSIC_FANTASY
                    },
                    narratorVoice = values[6] as Boolean
                )
            }.collect { settings ->
                _state.value = settings
            }
        }
    }

    fun setApiKey(apiKey: String) {
        viewModelScope.launch {
            settingsRepository.setGeminiApiKey(apiKey)
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDarkTheme(enabled)
        }
    }

    fun setSoundEffects(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSoundEffectsEnabled(enabled)
        }
    }

    fun setDiceAnimation(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDiceAnimationEnabled(enabled)
        }
    }

    fun setAutoRoll(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setAutoRollEnabled(enabled)
        }
    }

    fun setAdventureSetting(setting: AdventureSetting) {
        viewModelScope.launch {
            settingsRepository.setAdventureSetting(setting.name)
        }
    }

    fun setNarratorVoice(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNarratorVoiceEnabled(enabled)
        }
    }

    fun toggleApiKeyVisibility() {
        _state.update { it.copy(isApiKeyVisible = !it.isApiKeyVisible) }
    }
}
