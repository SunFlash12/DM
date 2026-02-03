package com.dungeonmaster.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dungeonmaster.app.data.database.Converters
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "game_sessions")
@TypeConverters(Converters::class)
data class GameSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val characterId: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val lastPlayedAt: Long = System.currentTimeMillis(),
    val currentLocation: String = "Unknown",
    val currentScenario: String = "",
    val campaignSummary: String = ""
)

@Serializable
@Entity(tableName = "chat_messages")
@TypeConverters(Converters::class)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: MessageMetadata? = null
)

@Serializable
enum class MessageRole {
    USER,      // Player input
    DM,        // AI Dungeon Master
    SYSTEM,    // Game mechanics, dice rolls, etc.
    NARRATOR   // Story narration
}

@Serializable
data class MessageMetadata(
    val diceRolls: List<DiceRoll>? = null,
    val combatAction: CombatAction? = null,
    val itemsGained: List<String>? = null,
    val itemsLost: List<String>? = null,
    val experienceGained: Int? = null,
    val goldChange: Int? = null,
    val healthChange: Int? = null,
    val conditionsApplied: List<Condition>? = null,
    val conditionsRemoved: List<Condition>? = null
)

@Serializable
data class DiceRoll(
    val diceType: DiceType,
    val count: Int,
    val modifier: Int = 0,
    val results: List<Int>,
    val total: Int,
    val purpose: String = "",
    val advantage: Boolean = false,
    val disadvantage: Boolean = false,
    val isNatural20: Boolean = false,
    val isNatural1: Boolean = false
)

@Serializable
enum class DiceType(val sides: Int, val displayName: String) {
    D4(4, "d4"),
    D6(6, "d6"),
    D8(8, "d8"),
    D10(10, "d10"),
    D12(12, "d12"),
    D20(20, "d20"),
    D100(100, "d100")
}

@Serializable
data class CombatAction(
    val actionType: ActionType,
    val targetName: String? = null,
    val attackRoll: DiceRoll? = null,
    val damageRoll: DiceRoll? = null,
    val savingThrow: SavingThrowResult? = null,
    val description: String = ""
)

@Serializable
enum class ActionType {
    ATTACK,
    CAST_SPELL,
    DASH,
    DISENGAGE,
    DODGE,
    HELP,
    HIDE,
    READY,
    SEARCH,
    USE_OBJECT,
    OTHER
}

@Serializable
data class SavingThrowResult(
    val ability: Ability,
    val dc: Int,
    val roll: DiceRoll,
    val success: Boolean
)

// Combat state management
@Serializable
data class CombatState(
    val isActive: Boolean = false,
    val round: Int = 0,
    val turnOrder: List<CombatantInfo> = emptyList(),
    val currentTurnIndex: Int = 0,
    val enemies: List<Enemy> = emptyList()
)

@Serializable
data class CombatantInfo(
    val name: String,
    val initiative: Int,
    val isPlayer: Boolean,
    val currentHP: Int,
    val maxHP: Int,
    val armorClass: Int,
    val conditions: List<Condition> = emptyList()
)

@Serializable
data class Enemy(
    val id: String,
    val name: String,
    val type: String,
    val challengeRating: String,
    val armorClass: Int,
    val maxHitPoints: Int,
    val currentHitPoints: Int,
    val speed: Int,
    val abilities: Map<Ability, Int>,
    val attacks: List<EnemyAttack>,
    val specialAbilities: List<String> = emptyList(),
    val conditions: List<Condition> = emptyList()
)

@Serializable
data class EnemyAttack(
    val name: String,
    val attackBonus: Int,
    val damage: String,
    val damageType: DamageType,
    val description: String = ""
)

// Commonly used enemies for the AI DM
object CommonEnemies {
    val GOBLIN = Enemy(
        id = "goblin",
        name = "Goblin",
        type = "Humanoid",
        challengeRating = "1/4",
        armorClass = 15,
        maxHitPoints = 7,
        currentHitPoints = 7,
        speed = 30,
        abilities = mapOf(
            Ability.STRENGTH to 8,
            Ability.DEXTERITY to 14,
            Ability.CONSTITUTION to 10,
            Ability.INTELLIGENCE to 10,
            Ability.WISDOM to 8,
            Ability.CHARISMA to 8
        ),
        attacks = listOf(
            EnemyAttack("Scimitar", 4, "1d6+2", DamageType.SLASHING),
            EnemyAttack("Shortbow", 4, "1d6+2", DamageType.PIERCING)
        ),
        specialAbilities = listOf("Nimble Escape: Can Disengage or Hide as a bonus action")
    )

    val SKELETON = Enemy(
        id = "skeleton",
        name = "Skeleton",
        type = "Undead",
        challengeRating = "1/4",
        armorClass = 13,
        maxHitPoints = 13,
        currentHitPoints = 13,
        speed = 30,
        abilities = mapOf(
            Ability.STRENGTH to 10,
            Ability.DEXTERITY to 14,
            Ability.CONSTITUTION to 15,
            Ability.INTELLIGENCE to 6,
            Ability.WISDOM to 8,
            Ability.CHARISMA to 5
        ),
        attacks = listOf(
            EnemyAttack("Shortsword", 4, "1d6+2", DamageType.PIERCING),
            EnemyAttack("Shortbow", 4, "1d6+2", DamageType.PIERCING)
        ),
        specialAbilities = listOf("Vulnerable to bludgeoning damage", "Immune to poison and exhaustion")
    )

    val ORC = Enemy(
        id = "orc",
        name = "Orc",
        type = "Humanoid",
        challengeRating = "1/2",
        armorClass = 13,
        maxHitPoints = 15,
        currentHitPoints = 15,
        speed = 30,
        abilities = mapOf(
            Ability.STRENGTH to 16,
            Ability.DEXTERITY to 12,
            Ability.CONSTITUTION to 16,
            Ability.INTELLIGENCE to 7,
            Ability.WISDOM to 11,
            Ability.CHARISMA to 10
        ),
        attacks = listOf(
            EnemyAttack("Greataxe", 5, "1d12+3", DamageType.SLASHING),
            EnemyAttack("Javelin", 5, "1d6+3", DamageType.PIERCING)
        ),
        specialAbilities = listOf("Aggressive: Can move up to speed toward hostile creature as bonus action")
    )

    val WOLF = Enemy(
        id = "wolf",
        name = "Wolf",
        type = "Beast",
        challengeRating = "1/4",
        armorClass = 13,
        maxHitPoints = 11,
        currentHitPoints = 11,
        speed = 40,
        abilities = mapOf(
            Ability.STRENGTH to 12,
            Ability.DEXTERITY to 15,
            Ability.CONSTITUTION to 12,
            Ability.INTELLIGENCE to 3,
            Ability.WISDOM to 12,
            Ability.CHARISMA to 6
        ),
        attacks = listOf(
            EnemyAttack("Bite", 4, "2d4+2", DamageType.PIERCING, "Target must succeed DC 11 STR save or be knocked prone")
        ),
        specialAbilities = listOf("Pack Tactics: Advantage on attack if ally is within 5ft of target")
    )
}
