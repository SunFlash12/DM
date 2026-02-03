package com.dungeonmaster.app.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungeonmaster.app.ai.AdventureSetting
import com.dungeonmaster.app.ai.DungeonMasterAI
import com.dungeonmaster.app.data.models.Character
import com.dungeonmaster.app.data.models.GameSession
import com.dungeonmaster.app.data.repository.GameRepository
import com.dungeonmaster.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewGameState(
    val character: Character? = null,
    val selectedSetting: AdventureSetting = AdventureSetting.CLASSIC_FANTASY,
    val sessionName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class NewGameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository,
    private val dungeonMasterAI: DungeonMasterAI
) : ViewModel() {
    private val characterId: Long = savedStateHandle.get<Long>("characterId") ?: 0L

    private val _state = MutableStateFlow(NewGameState())
    val state: StateFlow<NewGameState> = _state.asStateFlow()

    init {
        loadCharacter()
        initializeAI()
    }

    private fun loadCharacter() {
        viewModelScope.launch {
            val character = gameRepository.getCharacterById(characterId)
            _state.update {
                it.copy(
                    character = character,
                    sessionName = "${character?.name}'s Adventure"
                )
            }
        }
    }

    private fun initializeAI() {
        viewModelScope.launch {
            settingsRepository.geminiApiKey.collect { apiKey ->
                if (apiKey.isNotBlank()) {
                    dungeonMasterAI.initialize(apiKey)
                }
            }
        }
    }

    fun setSessionName(name: String) {
        _state.update { it.copy(sessionName = name) }
    }

    fun selectSetting(setting: AdventureSetting) {
        _state.update { it.copy(selectedSetting = setting) }
    }

    fun startGame(onGameStarted: (Long) -> Unit) {
        val character = _state.value.character ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Create session
                val session = GameSession(
                    name = _state.value.sessionName.ifBlank { "${character.name}'s Adventure" },
                    characterId = characterId,
                    currentLocation = "Starting Point"
                )
                val sessionId = gameRepository.createSession(session)

                // Generate initial adventure text
                val openingNarration = dungeonMasterAI.startNewAdventure(
                    character = character,
                    setting = _state.value.selectedSetting
                )

                // Save the opening narration
                gameRepository.addDMMessage(sessionId, openingNarration)

                _state.update { it.copy(isLoading = false) }
                onGameStarted(sessionId)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScreen(
    characterId: Long,
    onGameStarted: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: NewGameViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Adventure") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Character Info
            item {
                state.character?.let { character ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Playing as",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = character.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Level ${character.level} ${character.race.displayName} ${character.characterClass.displayName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            // Session Name
            item {
                Text(
                    text = "Adventure Name",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.sessionName,
                    onValueChange = viewModel::setSessionName,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter adventure name") },
                    singleLine = true
                )
            }

            // Setting Selection
            item {
                Text(
                    text = "Choose Your Setting",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(AdventureSetting.entries) { setting ->
                SettingCard(
                    setting = setting,
                    isSelected = state.selectedSetting == setting,
                    onSelect = { viewModel.selectSetting(setting) }
                )
            }

            // Start Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.startGame(onGameStarted) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Preparing Adventure...")
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Begin Adventure")
                    }
                }
            }

            // Error display
            state.error?.let { error ->
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingCard(
    setting: AdventureSetting,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(12.dp)
                ) else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = setting.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = setting.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
