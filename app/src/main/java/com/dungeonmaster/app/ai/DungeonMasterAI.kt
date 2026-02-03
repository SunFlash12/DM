package com.dungeonmaster.app.ai

import com.dungeonmaster.app.data.models.*
import com.dungeonmaster.app.utils.DiceRoller
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DungeonMasterAI @Inject constructor() {

    private var generativeModel: GenerativeModel? = null
    private var chatHistory = mutableListOf<com.google.ai.client.generativeai.type.Content>()

    fun initialize(apiKey: String) {
        if (apiKey.isBlank()) return

        generativeModel = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.9f
                topK = 40
                topP = 0.95f
                maxOutputTokens = 2048
            }
        )
    }

    fun isInitialized(): Boolean = generativeModel != null

    suspend fun startNewAdventure(
        character: Character,
        setting: AdventureSetting = AdventureSetting.CLASSIC_FANTASY
    ): String = withContext(Dispatchers.IO) {
        chatHistory.clear()

        val systemPrompt = buildSystemPrompt(character, setting)
        val startPrompt = """
            |Begin a new adventure for ${character.name}, a level ${character.level} ${character.race.displayName} ${character.characterClass.displayName}.
            |
            |Start with an engaging opening scene that:
            |1. Sets the atmosphere and location
            |2. Introduces an immediate hook or situation
            |3. Ends with a clear prompt for player action
            |
            |Keep the response under 300 words. Be vivid and immersive.
        """.trimMargin()

        val response = generateResponse(systemPrompt, startPrompt)
        response
    }

    suspend fun processPlayerAction(
        action: String,
        character: Character,
        gameContext: GameContext
    ): DMResponse = withContext(Dispatchers.IO) {
        val contextPrompt = buildContextPrompt(character, gameContext)

        val prompt = """
            |$contextPrompt
            |
            |Player Action: $action
            |
            |As the Dungeon Master, respond to this action following D&D 5e rules.
            |If dice rolls are needed, specify them clearly (e.g., "Roll a Perception check" or "Roll 1d20+5 for attack").
            |Keep combat exciting and descriptive. Track HP changes.
            |Provide clear consequences and advance the story.
            |End with options or a prompt for the next action when appropriate.
            |Keep response under 250 words unless combat requires more detail.
        """.trimMargin()

        val response = generateResponse(null, prompt)
        parseResponse(response, character)
    }

    suspend fun requestDiceRoll(
        rollType: RollType,
        character: Character,
        context: String = ""
    ): DiceRollRequest = withContext(Dispatchers.IO) {
        when (rollType) {
            RollType.ATTACK -> {
                val attackBonus = character.strengthModifier + character.proficiencyBonus
                DiceRollRequest(
                    rollType = rollType,
                    diceNotation = "1d20+$attackBonus",
                    purpose = "Attack Roll",
                    description = context
                )
            }
            RollType.DAMAGE -> {
                DiceRollRequest(
                    rollType = rollType,
                    diceNotation = "1d8+${character.strengthModifier}",
                    purpose = "Damage Roll",
                    description = context
                )
            }
            RollType.SKILL_CHECK -> {
                DiceRollRequest(
                    rollType = rollType,
                    diceNotation = "1d20",
                    purpose = "Skill Check",
                    description = context
                )
            }
            RollType.SAVING_THROW -> {
                DiceRollRequest(
                    rollType = rollType,
                    diceNotation = "1d20",
                    purpose = "Saving Throw",
                    description = context
                )
            }
            RollType.INITIATIVE -> {
                val initBonus = character.dexterityModifier
                DiceRollRequest(
                    rollType = rollType,
                    diceNotation = "1d20+$initBonus",
                    purpose = "Initiative",
                    description = "Rolling for combat order"
                )
            }
            RollType.CUSTOM -> {
                DiceRollRequest(
                    rollType = rollType,
                    diceNotation = context,
                    purpose = "Custom Roll",
                    description = ""
                )
            }
        }
    }

    suspend fun processRollResult(
        roll: DiceRoll,
        character: Character,
        gameContext: GameContext
    ): String = withContext(Dispatchers.IO) {
        val rollDescription = DiceRoller.formatRollResult(roll)

        val prompt = """
            |The player just rolled: $rollDescription
            |
            |Character: ${character.name} (${character.race.displayName} ${character.characterClass.displayName}, Level ${character.level})
            |Current HP: ${character.currentHitPoints}/${character.maxHitPoints}
            |Current situation: ${gameContext.currentSituation}
            |
            |Describe the outcome of this roll in the context of the current situation.
            |Be specific about successes or failures and their consequences.
            |If this was an attack that hit, describe the damage dealt.
            |If this was a skill check, describe what the character learns or accomplishes.
            |Keep response under 150 words.
        """.trimMargin()

        generateResponse(null, prompt)
    }

    suspend fun generateCombatNarration(
        combatState: CombatState,
        character: Character,
        lastAction: String
    ): String = withContext(Dispatchers.IO) {
        val enemyStatus = combatState.enemies.joinToString("\n") { enemy ->
            "- ${enemy.name}: ${enemy.currentHitPoints}/${enemy.maxHitPoints} HP"
        }

        val prompt = """
            |COMBAT - Round ${combatState.round}
            |
            |Player: ${character.name} - ${character.currentHitPoints}/${character.maxHitPoints} HP
            |Enemies:
            |$enemyStatus
            |
            |Last Action: $lastAction
            |
            |Narrate the current state of combat. Describe enemy reactions and set up the next turn.
            |Keep it dynamic and exciting. Under 100 words.
        """.trimMargin()

        generateResponse(null, prompt)
    }

    private suspend fun generateResponse(systemPrompt: String?, userPrompt: String): String {
        val model = generativeModel ?: return "AI not initialized. Please set your Gemini API key in settings."

        return try {
            val fullPrompt = if (systemPrompt != null) {
                "$systemPrompt\n\n$userPrompt"
            } else {
                userPrompt
            }

            val response = model.generateContent(fullPrompt)
            response.text ?: "The Dungeon Master ponders silently..."
        } catch (e: Exception) {
            "The mystical connection wavers... (Error: ${e.message})"
        }
    }

    private fun buildSystemPrompt(character: Character, setting: AdventureSetting): String {
        return """
            |You are an expert Dungeon Master running a D&D 5th Edition adventure.
            |
            |SETTING: ${setting.description}
            |
            |PLAYER CHARACTER:
            |Name: ${character.name}
            |Race: ${character.race.displayName}
            |Class: ${character.characterClass.displayName}
            |Level: ${character.level}
            |Background: ${character.background.displayName}
            |
            |ABILITY SCORES:
            |STR: ${character.strength} (${formatModifier(character.strengthModifier)})
            |DEX: ${character.dexterity} (${formatModifier(character.dexterityModifier)})
            |CON: ${character.constitution} (${formatModifier(character.constitutionModifier)})
            |INT: ${character.intelligence} (${formatModifier(character.intelligenceModifier)})
            |WIS: ${character.wisdom} (${formatModifier(character.wisdomModifier)})
            |CHA: ${character.charisma} (${formatModifier(character.charismaModifier)})
            |
            |HP: ${character.currentHitPoints}/${character.maxHitPoints}
            |AC: ${character.armorClass}
            |
            |YOUR RESPONSIBILITIES:
            |1. Create an immersive, engaging narrative
            |2. Follow D&D 5e rules accurately
            |3. Request appropriate dice rolls for actions
            |4. Track combat fairly using proper mechanics
            |5. Provide meaningful choices and consequences
            |6. Balance challenge with fun
            |7. Describe scenes vividly but concisely
            |
            |STYLE:
            |- Use second person ("You see...", "You feel...")
            |- Be descriptive but not verbose
            |- Create memorable NPCs with distinct personalities
            |- Include sensory details (sights, sounds, smells)
            |- Maintain tension and pacing
        """.trimMargin()
    }

    private fun buildContextPrompt(character: Character, context: GameContext): String {
        return """
            |CURRENT STATE:
            |Location: ${context.currentLocation}
            |Situation: ${context.currentSituation}
            |${if (context.inCombat) "IN COMBAT - Round ${context.combatRound}" else ""}
            |
            |CHARACTER STATUS:
            |${character.name} - HP: ${character.currentHitPoints}/${character.maxHitPoints}
            |Conditions: ${character.conditions.joinToString(", ") { it.displayName }.ifEmpty { "None" }}
            |
            |${if (context.enemies.isNotEmpty()) "ENEMIES:\n${context.enemies.joinToString("\n") { "- ${it.name}: ${it.currentHitPoints}/${it.maxHitPoints} HP" }}" else ""}
        """.trimMargin()
    }

    private fun parseResponse(response: String, character: Character): DMResponse {
        // Check for combat indicators
        val inCombat = response.contains("initiative", ignoreCase = true) ||
                response.contains("attack", ignoreCase = true) ||
                response.contains("combat", ignoreCase = true)

        // Check for roll requests
        val rollPattern = Regex("""(?:roll|make)\s+(?:a\s+)?(\w+)\s+(?:check|save|saving throw)?""", RegexOption.IGNORE_CASE)
        val rollMatch = rollPattern.find(response)
        val requestedRoll = rollMatch?.groupValues?.get(1)

        // Check for damage
        val damagePattern = Regex("""(\d+)\s*(?:points?\s+of\s+)?damage""", RegexOption.IGNORE_CASE)
        val damageMatch = damagePattern.find(response)
        val damageTaken = damageMatch?.groupValues?.get(1)?.toIntOrNull()

        // Check for healing
        val healPattern = Regex("""(?:heal|restore|regain)\s+(\d+)""", RegexOption.IGNORE_CASE)
        val healMatch = healPattern.find(response)
        val healing = healMatch?.groupValues?.get(1)?.toIntOrNull()

        return DMResponse(
            narrative = response,
            requestedRoll = requestedRoll,
            combatInitiated = inCombat && !response.contains("ends", ignoreCase = true),
            damageTaken = damageTaken,
            healingReceived = healing,
            experienceGained = null,
            itemsFound = emptyList()
        )
    }

    private fun formatModifier(mod: Int): String = if (mod >= 0) "+$mod" else "$mod"
}

data class DMResponse(
    val narrative: String,
    val requestedRoll: String? = null,
    val combatInitiated: Boolean = false,
    val damageTaken: Int? = null,
    val healingReceived: Int? = null,
    val experienceGained: Int? = null,
    val itemsFound: List<String> = emptyList()
)

data class GameContext(
    val currentLocation: String,
    val currentSituation: String,
    val inCombat: Boolean = false,
    val combatRound: Int = 0,
    val enemies: List<Enemy> = emptyList(),
    val recentEvents: List<String> = emptyList()
)

data class DiceRollRequest(
    val rollType: RollType,
    val diceNotation: String,
    val purpose: String,
    val description: String
)

enum class RollType {
    ATTACK,
    DAMAGE,
    SKILL_CHECK,
    SAVING_THROW,
    INITIATIVE,
    CUSTOM
}

enum class AdventureSetting(val displayName: String, val description: String) {
    CLASSIC_FANTASY(
        "Classic Fantasy",
        "A traditional medieval fantasy world with castles, dragons, and magic. Knights, wizards, and rogues adventure through ancient dungeons and enchanted forests."
    ),
    DARK_GOTHIC(
        "Dark Gothic",
        "A realm of shadows and horror. Vampires lurk in crumbling castles, werewolves prowl misty moors, and dark magic corrupts the land."
    ),
    HIGH_MAGIC(
        "High Magic",
        "A world where magic is commonplace. Flying cities, magical academies, and arcane wonders fill every corner of civilization."
    ),
    SWORD_AND_SORCERY(
        "Sword and Sorcery",
        "A gritty world of cunning warriors and mysterious sorcerers. Ancient evils threaten civilization, and heroes must rise."
    ),
    SEAFARING(
        "Seafaring Adventure",
        "Endless oceans filled with pirates, sea monsters, and mysterious islands. Naval combat and treasure hunting await."
    )
}
