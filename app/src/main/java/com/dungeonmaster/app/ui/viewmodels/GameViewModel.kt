package com.dungeonmaster.app.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dungeonmaster.app.ai.*
import com.dungeonmaster.app.data.models.*
import com.dungeonmaster.app.data.repository.GameRepository
import com.dungeonmaster.app.data.repository.SettingsRepository
import com.dungeonmaster.app.utils.DiceRoller
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val session: GameSession? = null,
    val character: Character? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isAiThinking: Boolean = false,
    val error: String? = null,
    val combatState: CombatState = CombatState(),
    val pendingRoll: DiceRollRequest? = null,
    val lastRollResult: DiceRoll? = null
)

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val gameRepository: GameRepository,
    private val settingsRepository: SettingsRepository,
    private val dungeonMasterAI: DungeonMasterAI
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<Long>("sessionId") ?: 0L

    private val _uiState = MutableStateFlow(GameUiState(isLoading = true))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadSession()
        initializeAI()
    }

    private fun loadSession() {
        viewModelScope.launch {
            try {
                val session = gameRepository.getSessionById(sessionId)
                if (session != null) {
                    val character = gameRepository.getCharacterById(session.characterId)

                    gameRepository.getMessagesForSession(sessionId)
                        .collect { messages ->
                            _uiState.update {
                                it.copy(
                                    session = session,
                                    character = character,
                                    messages = messages,
                                    isLoading = false
                                )
                            }
                        }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Session not found") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
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

    fun sendMessage(content: String) {
        val character = _uiState.value.character ?: return
        val session = _uiState.value.session ?: return

        viewModelScope.launch {
            // Add user message
            gameRepository.addUserMessage(sessionId, content)

            _uiState.update { it.copy(isAiThinking = true) }

            try {
                // Get AI response
                val context = GameContext(
                    currentLocation = session.currentLocation,
                    currentSituation = session.currentScenario,
                    inCombat = _uiState.value.combatState.isActive,
                    combatRound = _uiState.value.combatState.round,
                    enemies = _uiState.value.combatState.enemies
                )

                val response = dungeonMasterAI.processPlayerAction(content, character, context)

                // Add DM response
                gameRepository.addDMMessage(sessionId, response.narrative)

                // Handle any game state changes
                handleDMResponse(response, character)

            } catch (e: Exception) {
                gameRepository.addSystemMessage(sessionId, "Error: ${e.message}")
            } finally {
                _uiState.update { it.copy(isAiThinking = false) }
            }
        }
    }

    private suspend fun handleDMResponse(response: DMResponse, character: Character) {
        // Handle damage
        response.damageTaken?.let { damage ->
            val newHp = (character.currentHitPoints - damage).coerceAtLeast(0)
            gameRepository.updateCharacterHitPoints(character.id, newHp)

            if (newHp <= 0) {
                gameRepository.addSystemMessage(sessionId, "${character.name} has fallen unconscious!")
            }
        }

        // Handle healing
        response.healingReceived?.let { healing ->
            val newHp = (character.currentHitPoints + healing).coerceAtMost(character.maxHitPoints)
            gameRepository.updateCharacterHitPoints(character.id, newHp)
        }

        // Handle experience
        response.experienceGained?.let { xp ->
            val newXp = character.experiencePoints + xp
            val newLevel = calculateLevel(newXp)
            gameRepository.updateCharacterExperience(character.id, newXp, newLevel)

            if (newLevel > character.level) {
                gameRepository.addSystemMessage(sessionId, "Level Up! ${character.name} is now level $newLevel!")
            }
        }

        // Handle requested rolls
        response.requestedRoll?.let { rollType ->
            requestRoll(rollType, character)
        }

        // Handle combat initiation
        if (response.combatInitiated) {
            initiateCombat()
        }
    }

    private fun calculateLevel(xp: Int): Int {
        return when {
            xp >= 355000 -> 20
            xp >= 305000 -> 19
            xp >= 265000 -> 18
            xp >= 225000 -> 17
            xp >= 195000 -> 16
            xp >= 165000 -> 15
            xp >= 140000 -> 14
            xp >= 120000 -> 13
            xp >= 100000 -> 12
            xp >= 85000 -> 11
            xp >= 64000 -> 10
            xp >= 48000 -> 9
            xp >= 34000 -> 8
            xp >= 23000 -> 7
            xp >= 14000 -> 6
            xp >= 6500 -> 5
            xp >= 2700 -> 4
            xp >= 900 -> 3
            xp >= 300 -> 2
            else -> 1
        }
    }

    private suspend fun requestRoll(rollType: String, character: Character) {
        val request = when (rollType.lowercase()) {
            "perception" -> DiceRollRequest(
                RollType.SKILL_CHECK,
                "1d20+${character.getSkillModifier(Skill.PERCEPTION)}",
                "Perception Check",
                ""
            )
            "stealth" -> DiceRollRequest(
                RollType.SKILL_CHECK,
                "1d20+${character.getSkillModifier(Skill.STEALTH)}",
                "Stealth Check",
                ""
            )
            "attack" -> DiceRollRequest(
                RollType.ATTACK,
                "1d20+${character.strengthModifier + character.proficiencyBonus}",
                "Attack Roll",
                ""
            )
            "initiative" -> DiceRollRequest(
                RollType.INITIATIVE,
                "1d20+${character.dexterityModifier}",
                "Initiative",
                ""
            )
            else -> DiceRollRequest(
                RollType.SKILL_CHECK,
                "1d20",
                "$rollType Check",
                ""
            )
        }

        _uiState.update { it.copy(pendingRoll = request) }
    }

    fun rollDice(request: DiceRollRequest) {
        val character = _uiState.value.character ?: return

        viewModelScope.launch {
            try {
                val roll = DiceRoller.parseAndRoll(request.diceNotation, request.purpose)
                _uiState.update { it.copy(lastRollResult = roll, pendingRoll = null) }

                // Add roll result to chat
                val rollMessage = DiceRoller.formatRollResult(roll)
                gameRepository.addSystemMessage(sessionId, rollMessage)

                // Get AI interpretation of the roll
                _uiState.update { it.copy(isAiThinking = true) }

                val context = GameContext(
                    currentLocation = _uiState.value.session?.currentLocation ?: "Unknown",
                    currentSituation = _uiState.value.session?.currentScenario ?: "",
                    inCombat = _uiState.value.combatState.isActive,
                    combatRound = _uiState.value.combatState.round,
                    enemies = _uiState.value.combatState.enemies
                )

                val interpretation = dungeonMasterAI.processRollResult(roll, character, context)
                gameRepository.addDMMessage(sessionId, interpretation)

            } catch (e: Exception) {
                gameRepository.addSystemMessage(sessionId, "Roll error: ${e.message}")
            } finally {
                _uiState.update { it.copy(isAiThinking = false) }
            }
        }
    }

    fun dismissPendingRoll() {
        _uiState.update { it.copy(pendingRoll = null) }
    }

    private fun initiateCombat() {
        _uiState.update {
            it.copy(
                combatState = CombatState(
                    isActive = true,
                    round = 1
                )
            )
        }
    }

    fun endCombat() {
        _uiState.update {
            it.copy(combatState = CombatState())
        }

        viewModelScope.launch {
            gameRepository.addSystemMessage(sessionId, "Combat has ended.")
        }
    }

    fun quickRoll(diceType: DiceType, count: Int = 1, modifier: Int = 0) {
        viewModelScope.launch {
            val roll = DiceRoller.roll(diceType, count, modifier)
            val message = DiceRoller.formatRollResult(roll)
            gameRepository.addSystemMessage(sessionId, message)
            _uiState.update { it.copy(lastRollResult = roll) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
