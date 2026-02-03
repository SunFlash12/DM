package com.dungeonmaster.app.utils

import com.dungeonmaster.app.data.models.Ability
import com.dungeonmaster.app.data.models.Character
import com.dungeonmaster.app.data.models.DiceRoll
import com.dungeonmaster.app.data.models.DiceType
import com.dungeonmaster.app.data.models.Skill
import kotlin.random.Random

object DiceRoller {

    /**
     * Roll a single die of the specified type
     */
    fun rollDie(diceType: DiceType): Int {
        return Random.nextInt(1, diceType.sides + 1)
    }

    /**
     * Roll multiple dice and return all results
     */
    fun rollDice(diceType: DiceType, count: Int): List<Int> {
        return (1..count).map { rollDie(diceType) }
    }

    /**
     * Roll dice with modifier and return a DiceRoll object
     */
    fun roll(
        diceType: DiceType,
        count: Int = 1,
        modifier: Int = 0,
        purpose: String = "",
        advantage: Boolean = false,
        disadvantage: Boolean = false
    ): DiceRoll {
        val results = if (diceType == DiceType.D20 && (advantage || disadvantage)) {
            // Roll twice for advantage/disadvantage
            val roll1 = rollDie(diceType)
            val roll2 = rollDie(diceType)
            if (advantage) {
                listOf(maxOf(roll1, roll2))
            } else {
                listOf(minOf(roll1, roll2))
            }
        } else {
            rollDice(diceType, count)
        }

        val total = results.sum() + modifier
        val isNat20 = diceType == DiceType.D20 && results.first() == 20
        val isNat1 = diceType == DiceType.D20 && results.first() == 1

        return DiceRoll(
            diceType = diceType,
            count = count,
            modifier = modifier,
            results = results,
            total = total,
            purpose = purpose,
            advantage = advantage,
            disadvantage = disadvantage,
            isNatural20 = isNat20,
            isNatural1 = isNat1
        )
    }

    /**
     * Parse and roll dice notation (e.g., "2d6+3", "1d20", "4d8-2")
     */
    fun parseAndRoll(notation: String, purpose: String = ""): DiceRoll {
        val regex = """(\d+)d(\d+)([+-]\d+)?""".toRegex(RegexOption.IGNORE_CASE)
        val match = regex.find(notation.replace(" ", ""))
            ?: throw IllegalArgumentException("Invalid dice notation: $notation")

        val count = match.groupValues[1].toInt()
        val sides = match.groupValues[2].toInt()
        val modifier = match.groupValues[3].takeIf { it.isNotEmpty() }?.toInt() ?: 0

        val diceType = DiceType.entries.find { it.sides == sides }
            ?: throw IllegalArgumentException("Invalid dice type: d$sides")

        return roll(diceType, count, modifier, purpose)
    }

    /**
     * Roll for ability scores using 4d6 drop lowest
     */
    fun rollAbilityScore(): Int {
        val rolls = rollDice(DiceType.D6, 4).sorted()
        return rolls.drop(1).sum() // Drop lowest, sum remaining 3
    }

    /**
     * Roll a complete set of ability scores
     */
    fun rollAbilityScores(): Map<Ability, Int> {
        return Ability.entries.associateWith { rollAbilityScore() }
    }

    /**
     * Roll initiative for a character
     */
    fun rollInitiative(character: Character, advantage: Boolean = false): DiceRoll {
        return roll(
            diceType = DiceType.D20,
            modifier = character.dexterityModifier,
            purpose = "Initiative",
            advantage = advantage
        )
    }

    /**
     * Roll an attack
     */
    fun rollAttack(
        attackBonus: Int,
        advantage: Boolean = false,
        disadvantage: Boolean = false,
        targetName: String = ""
    ): DiceRoll {
        return roll(
            diceType = DiceType.D20,
            modifier = attackBonus,
            purpose = "Attack${if (targetName.isNotEmpty()) " against $targetName" else ""}",
            advantage = advantage,
            disadvantage = disadvantage
        )
    }

    /**
     * Roll damage
     */
    fun rollDamage(
        damageNotation: String,
        critical: Boolean = false,
        damageType: String = ""
    ): DiceRoll {
        val regex = """(\d+)d(\d+)([+-]\d+)?""".toRegex()
        val match = regex.find(damageNotation.replace(" ", ""))
            ?: throw IllegalArgumentException("Invalid damage notation: $damageNotation")

        val count = match.groupValues[1].toInt()
        val sides = match.groupValues[2].toInt()
        val modifier = match.groupValues[3].takeIf { it.isNotEmpty() }?.toInt() ?: 0

        val diceType = DiceType.entries.find { it.sides == sides }
            ?: throw IllegalArgumentException("Invalid dice type: d$sides")

        val actualCount = if (critical) count * 2 else count

        return roll(
            diceType = diceType,
            count = actualCount,
            modifier = modifier,
            purpose = "Damage${if (damageType.isNotEmpty()) " ($damageType)" else ""}${if (critical) " [CRITICAL]" else ""}"
        )
    }

    /**
     * Roll a skill check for a character
     */
    fun rollSkillCheck(
        character: Character,
        skill: Skill,
        advantage: Boolean = false,
        disadvantage: Boolean = false
    ): DiceRoll {
        return roll(
            diceType = DiceType.D20,
            modifier = character.getSkillModifier(skill),
            purpose = "${skill.displayName} check",
            advantage = advantage,
            disadvantage = disadvantage
        )
    }

    /**
     * Roll a saving throw for a character
     */
    fun rollSavingThrow(
        character: Character,
        ability: Ability,
        advantage: Boolean = false,
        disadvantage: Boolean = false
    ): DiceRoll {
        return roll(
            diceType = DiceType.D20,
            modifier = character.getSavingThrowModifier(ability),
            purpose = "${ability.displayName} saving throw",
            advantage = advantage,
            disadvantage = disadvantage
        )
    }

    /**
     * Roll an ability check for a character
     */
    fun rollAbilityCheck(
        character: Character,
        ability: Ability,
        advantage: Boolean = false,
        disadvantage: Boolean = false
    ): DiceRoll {
        return roll(
            diceType = DiceType.D20,
            modifier = character.getAbilityModifier(ability),
            purpose = "${ability.displayName} check",
            advantage = advantage,
            disadvantage = disadvantage
        )
    }

    /**
     * Format a dice roll result for display
     */
    fun formatRollResult(roll: DiceRoll): String {
        val rollsStr = roll.results.joinToString(" + ")
        val modStr = when {
            roll.modifier > 0 -> " + ${roll.modifier}"
            roll.modifier < 0 -> " - ${-roll.modifier}"
            else -> ""
        }

        val specialStr = when {
            roll.isNatural20 -> " (Natural 20!)"
            roll.isNatural1 -> " (Natural 1!)"
            else -> ""
        }

        val advantageStr = when {
            roll.advantage -> " [Advantage]"
            roll.disadvantage -> " [Disadvantage]"
            else -> ""
        }

        return buildString {
            if (roll.purpose.isNotEmpty()) {
                append("${roll.purpose}: ")
            }
            append("${roll.count}${roll.diceType.displayName}$modStr = ")
            if (roll.count > 1 || roll.modifier != 0) {
                append("($rollsStr$modStr) = ")
            }
            append("${roll.total}$specialStr$advantageStr")
        }
    }
}
