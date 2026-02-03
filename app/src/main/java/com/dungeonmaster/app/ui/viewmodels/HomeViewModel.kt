package com.dungeonmaster.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungeonmaster.app.data.models.Character
import com.dungeonmaster.app.data.models.GameSession
import com.dungeonmaster.app.data.repository.GameRepository
import com.dungeonmaster.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val characters: StateFlow<List<Character>> = gameRepository.getAllCharacters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSessions: StateFlow<List<GameSession>> = gameRepository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasApiKey: StateFlow<Boolean> = settingsRepository.geminiApiKey
        .map { it.isNotBlank() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun deleteCharacter(character: Character) {
        viewModelScope.launch {
            gameRepository.deleteCharacter(character)
        }
    }

    fun deleteSession(session: GameSession) {
        viewModelScope.launch {
            gameRepository.deleteSession(session)
        }
    }
}
