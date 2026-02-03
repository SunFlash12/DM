package com.dungeonmaster.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.dungeonmaster.app.data.database.Converters
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "characters")
@TypeConverters(Converters::class)
data class Character(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val race: Race,
    val characterClass: CharacterClass,
    val level: Int = 1,
    val experiencePoints: Int = 0,
    val background: Background,
    val alignment: Alignment = Alignment.TRUE_NEUTRAL,

    // Ability Scores
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,

    // Combat Stats
    val maxHitPoints: Int,
    val currentHitPoints: Int,
    val temporaryHitPoints: Int = 0,
    val armorClass: Int = 10,
    val initiative: Int = 0,
    val speed: Int = 30,

    // Proficiencies
    val proficiencyBonus: Int = 2,
    val savingThrowProficiencies: List<Ability> = emptyList(),
    val skillProficiencies: List<Skill> = emptyList(),

    // Equipment
    val equippedItems: List<Item> = emptyList(),
    val inventory: List<Item> = emptyList(),
    val gold: Int = 0,

    // Spellcasting (if applicable)
    val spellSlots: Map<Int, Int> = emptyMap(),
    val currentSpellSlots: Map<Int, Int> = emptyMap(),
    val knownSpells: List<Spell> = emptyList(),
    val preparedSpells: List<Spell> = emptyList(),

    // Character Details
    val personalityTraits: String = "",
    val ideals: String = "",
    val bonds: String = "",
    val flaws: String = "",
    val backstory: String = "",

    // Conditions and Effects
    val conditions: List<Condition> = emptyList(),
    val deathSaveSuccesses: Int = 0,
    val deathSaveFailures: Int = 0,

    // Session tracking
    val createdAt: Long = System.currentTimeMillis(),
    val lastPlayedAt: Long = System.currentTimeMillis()
) {
    // Ability Modifiers
    val strengthModifier: Int get() = calculateModifier(strength)
    val dexterityModifier: Int get() = calculateModifier(dexterity)
    val constitutionModifier: Int get() = calculateModifier(constitution)
    val intelligenceModifier: Int get() = calculateModifier(intelligence)
    val wisdomModifier: Int get() = calculateModifier(wisdom)
    val charismaModifier: Int get() = calculateModifier(charisma)

    fun getAbilityScore(ability: Ability): Int = when (ability) {
        Ability.STRENGTH -> strength
        Ability.DEXTERITY -> dexterity
        Ability.CONSTITUTION -> constitution
        Ability.INTELLIGENCE -> intelligence
        Ability.WISDOM -> wisdom
        Ability.CHARISMA -> charisma
    }

    fun getAbilityModifier(ability: Ability): Int = calculateModifier(getAbilityScore(ability))

    fun getSkillModifier(skill: Skill): Int {
        val abilityMod = getAbilityModifier(skill.ability)
        val profBonus = if (skillProficiencies.contains(skill)) proficiencyBonus else 0
        return abilityMod + profBonus
    }

    fun getSavingThrowModifier(ability: Ability): Int {
        val abilityMod = getAbilityModifier(ability)
        val profBonus = if (savingThrowProficiencies.contains(ability)) proficiencyBonus else 0
        return abilityMod + profBonus
    }

    companion object {
        fun calculateModifier(score: Int): Int = (score - 10) / 2

        fun calculateProficiencyBonus(level: Int): Int = when {
            level <= 4 -> 2
            level <= 8 -> 3
            level <= 12 -> 4
            level <= 16 -> 5
            else -> 6
        }

        fun experienceForLevel(level: Int): Int = when (level) {
            1 -> 0
            2 -> 300
            3 -> 900
            4 -> 2700
            5 -> 6500
            6 -> 14000
            7 -> 23000
            8 -> 34000
            9 -> 48000
            10 -> 64000
            11 -> 85000
            12 -> 100000
            13 -> 120000
            14 -> 140000
            15 -> 165000
            16 -> 195000
            17 -> 225000
            18 -> 265000
            19 -> 305000
            20 -> 355000
            else -> 0
        }
    }
}

@Serializable
enum class Ability(val displayName: String, val abbreviation: String) {
    STRENGTH("Strength", "STR"),
    DEXTERITY("Dexterity", "DEX"),
    CONSTITUTION("Constitution", "CON"),
    INTELLIGENCE("Intelligence", "INT"),
    WISDOM("Wisdom", "WIS"),
    CHARISMA("Charisma", "CHA")
}

@Serializable
enum class Skill(val displayName: String, val ability: Ability) {
    ACROBATICS("Acrobatics", Ability.DEXTERITY),
    ANIMAL_HANDLING("Animal Handling", Ability.WISDOM),
    ARCANA("Arcana", Ability.INTELLIGENCE),
    ATHLETICS("Athletics", Ability.STRENGTH),
    DECEPTION("Deception", Ability.CHARISMA),
    HISTORY("History", Ability.INTELLIGENCE),
    INSIGHT("Insight", Ability.WISDOM),
    INTIMIDATION("Intimidation", Ability.CHARISMA),
    INVESTIGATION("Investigation", Ability.INTELLIGENCE),
    MEDICINE("Medicine", Ability.WISDOM),
    NATURE("Nature", Ability.INTELLIGENCE),
    PERCEPTION("Perception", Ability.WISDOM),
    PERFORMANCE("Performance", Ability.CHARISMA),
    PERSUASION("Persuasion", Ability.CHARISMA),
    RELIGION("Religion", Ability.INTELLIGENCE),
    SLEIGHT_OF_HAND("Sleight of Hand", Ability.DEXTERITY),
    STEALTH("Stealth", Ability.DEXTERITY),
    SURVIVAL("Survival", Ability.WISDOM)
}

@Serializable
enum class Alignment(val displayName: String) {
    LAWFUL_GOOD("Lawful Good"),
    NEUTRAL_GOOD("Neutral Good"),
    CHAOTIC_GOOD("Chaotic Good"),
    LAWFUL_NEUTRAL("Lawful Neutral"),
    TRUE_NEUTRAL("True Neutral"),
    CHAOTIC_NEUTRAL("Chaotic Neutral"),
    LAWFUL_EVIL("Lawful Evil"),
    NEUTRAL_EVIL("Neutral Evil"),
    CHAOTIC_EVIL("Chaotic Evil")
}

@Serializable
enum class Condition(val displayName: String, val description: String) {
    BLINDED("Blinded", "Can't see, auto-fails sight checks, attacks have disadvantage, attacks against have advantage"),
    CHARMED("Charmed", "Can't attack charmer, charmer has advantage on social checks"),
    DEAFENED("Deafened", "Can't hear, auto-fails hearing checks"),
    FRIGHTENED("Frightened", "Disadvantage while source is visible, can't move closer to source"),
    GRAPPLED("Grappled", "Speed is 0, ends if grappler incapacitated or removed from reach"),
    INCAPACITATED("Incapacitated", "Can't take actions or reactions"),
    INVISIBLE("Invisible", "Impossible to see without special sense, attacks have advantage, attacks against have disadvantage"),
    PARALYZED("Paralyzed", "Incapacitated, can't move or speak, auto-fails STR/DEX saves, attacks have advantage, hits within 5ft are crits"),
    PETRIFIED("Petrified", "Transformed to stone, incapacitated, resistant to all damage, immune to poison/disease"),
    POISONED("Poisoned", "Disadvantage on attacks and ability checks"),
    PRONE("Prone", "Disadvantage on attacks, attacks within 5ft have advantage, ranged attacks have disadvantage"),
    RESTRAINED("Restrained", "Speed 0, attacks have disadvantage, attacks against have advantage, disadvantage on DEX saves"),
    STUNNED("Stunned", "Incapacitated, can't move, can speak only falteringly, auto-fails STR/DEX saves, attacks have advantage"),
    UNCONSCIOUS("Unconscious", "Incapacitated, can't move or speak, unaware of surroundings, drops held items, falls prone")
}
