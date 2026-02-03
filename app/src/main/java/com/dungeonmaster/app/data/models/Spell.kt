package com.dungeonmaster.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Spell(
    val id: String,
    val name: String,
    val level: Int, // 0 for cantrips
    val school: SpellSchool,
    val castingTime: String,
    val range: String,
    val components: SpellComponents,
    val duration: String,
    val description: String,
    val higherLevels: String? = null,
    val classes: List<ClassType>,
    val ritual: Boolean = false,
    val concentration: Boolean = false
)

@Serializable
enum class SpellSchool(val displayName: String) {
    ABJURATION("Abjuration"),
    CONJURATION("Conjuration"),
    DIVINATION("Divination"),
    ENCHANTMENT("Enchantment"),
    EVOCATION("Evocation"),
    ILLUSION("Illusion"),
    NECROMANCY("Necromancy"),
    TRANSMUTATION("Transmutation")
}

@Serializable
data class SpellComponents(
    val verbal: Boolean = false,
    val somatic: Boolean = false,
    val material: Boolean = false,
    val materialDescription: String? = null
) {
    override fun toString(): String {
        val parts = mutableListOf<String>()
        if (verbal) parts.add("V")
        if (somatic) parts.add("S")
        if (material) parts.add("M" + (materialDescription?.let { " ($it)" } ?: ""))
        return parts.joinToString(", ")
    }
}

// Common spell list for the AI DM to reference
object CommonSpells {
    // Cantrips
    val FIRE_BOLT = Spell(
        id = "fire_bolt",
        name = "Fire Bolt",
        level = 0,
        school = SpellSchool.EVOCATION,
        castingTime = "1 action",
        range = "120 feet",
        components = SpellComponents(verbal = true, somatic = true),
        duration = "Instantaneous",
        description = "You hurl a mote of fire at a creature or object within range. Make a ranged spell attack. On hit, the target takes 1d10 fire damage. The spell's damage increases by 1d10 when you reach 5th level (2d10), 11th level (3d10), and 17th level (4d10).",
        classes = listOf(ClassType.SORCERER, ClassType.WIZARD)
    )

    val LIGHT = Spell(
        id = "light",
        name = "Light",
        level = 0,
        school = SpellSchool.EVOCATION,
        castingTime = "1 action",
        range = "Touch",
        components = SpellComponents(verbal = true, material = true, materialDescription = "a firefly or phosphorescent moss"),
        duration = "1 hour",
        description = "You touch one object that is no larger than 10 feet in any dimension. The object sheds bright light in a 20-foot radius and dim light for an additional 20 feet.",
        classes = listOf(ClassType.BARD, ClassType.CLERIC, ClassType.SORCERER, ClassType.WIZARD)
    )

    val MAGE_HAND = Spell(
        id = "mage_hand",
        name = "Mage Hand",
        level = 0,
        school = SpellSchool.CONJURATION,
        castingTime = "1 action",
        range = "30 feet",
        components = SpellComponents(verbal = true, somatic = true),
        duration = "1 minute",
        description = "A spectral, floating hand appears at a point you choose within range. The hand can manipulate objects, open unlocked doors, or perform other simple tasks.",
        classes = listOf(ClassType.BARD, ClassType.SORCERER, ClassType.WARLOCK, ClassType.WIZARD)
    )

    val SACRED_FLAME = Spell(
        id = "sacred_flame",
        name = "Sacred Flame",
        level = 0,
        school = SpellSchool.EVOCATION,
        castingTime = "1 action",
        range = "60 feet",
        components = SpellComponents(verbal = true, somatic = true),
        duration = "Instantaneous",
        description = "Flame-like radiance descends on a creature you can see. The target must succeed on a Dexterity saving throw or take 1d8 radiant damage. The target gains no benefit from cover for this saving throw.",
        classes = listOf(ClassType.CLERIC)
    )

    // 1st Level
    val MAGIC_MISSILE = Spell(
        id = "magic_missile",
        name = "Magic Missile",
        level = 1,
        school = SpellSchool.EVOCATION,
        castingTime = "1 action",
        range = "120 feet",
        components = SpellComponents(verbal = true, somatic = true),
        duration = "Instantaneous",
        description = "You create three glowing darts of magical force. Each dart hits a creature of your choice that you can see within range. A dart deals 1d4+1 force damage.",
        higherLevels = "When cast at 2nd level or higher, the spell creates one more dart for each slot level above 1st.",
        classes = listOf(ClassType.SORCERER, ClassType.WIZARD)
    )

    val CURE_WOUNDS = Spell(
        id = "cure_wounds",
        name = "Cure Wounds",
        level = 1,
        school = SpellSchool.EVOCATION,
        castingTime = "1 action",
        range = "Touch",
        components = SpellComponents(verbal = true, somatic = true),
        duration = "Instantaneous",
        description = "A creature you touch regains hit points equal to 1d8 + your spellcasting ability modifier.",
        higherLevels = "When cast at 2nd level or higher, the healing increases by 1d8 for each slot level above 1st.",
        classes = listOf(ClassType.BARD, ClassType.CLERIC, ClassType.DRUID, ClassType.PALADIN, ClassType.RANGER)
    )

    val SHIELD = Spell(
        id = "shield",
        name = "Shield",
        level = 1,
        school = SpellSchool.ABJURATION,
        castingTime = "1 reaction",
        range = "Self",
        components = SpellComponents(verbal = true, somatic = true),
        duration = "1 round",
        description = "An invisible barrier of magical force appears and protects you. Until the start of your next turn, you have a +5 bonus to AC, including against the triggering attack.",
        classes = listOf(ClassType.SORCERER, ClassType.WIZARD)
    )

    val THUNDERWAVE = Spell(
        id = "thunderwave",
        name = "Thunderwave",
        level = 1,
        school = SpellSchool.EVOCATION,
        castingTime = "1 action",
        range = "Self (15-foot cube)",
        components = SpellComponents(verbal = true, somatic = true),
        duration = "Instantaneous",
        description = "A wave of thunderous force sweeps out from you. Each creature in a 15-foot cube originating from you must make a Constitution saving throw. On a failed save, a creature takes 2d8 thunder damage and is pushed 10 feet away from you. On a success, it takes half as much damage and isn't pushed.",
        higherLevels = "When cast at 2nd level or higher, the damage increases by 1d8 for each slot level above 1st.",
        classes = listOf(ClassType.BARD, ClassType.DRUID, ClassType.SORCERER, ClassType.WIZARD)
    )

    val HEALING_WORD = Spell(
        id = "healing_word",
        name = "Healing Word",
        level = 1,
        school = SpellSchool.EVOCATION,
        castingTime = "1 bonus action",
        range = "60 feet",
        components = SpellComponents(verbal = true),
        duration = "Instantaneous",
        description = "A creature of your choice that you can see within range regains hit points equal to 1d4 + your spellcasting ability modifier.",
        higherLevels = "When cast at 2nd level or higher, the healing increases by 1d4 for each slot level above 1st.",
        classes = listOf(ClassType.BARD, ClassType.CLERIC, ClassType.DRUID)
    )

    // 2nd Level
    val MISTY_STEP = Spell(
        id = "misty_step",
        name = "Misty Step",
        level = 2,
        school = SpellSchool.CONJURATION,
        castingTime = "1 bonus action",
        range = "Self",
        components = SpellComponents(verbal = true),
        duration = "Instantaneous",
        description = "Briefly surrounded by silvery mist, you teleport up to 30 feet to an unoccupied space that you can see.",
        classes = listOf(ClassType.SORCERER, ClassType.WARLOCK, ClassType.WIZARD)
    )

    val HOLD_PERSON = Spell(
        id = "hold_person",
        name = "Hold Person",
        level = 2,
        school = SpellSchool.ENCHANTMENT,
        castingTime = "1 action",
        range = "60 feet",
        components = SpellComponents(verbal = true, somatic = true, material = true, materialDescription = "a small piece of iron"),
        duration = "Concentration, up to 1 minute",
        description = "Choose a humanoid that you can see within range. The target must succeed on a Wisdom saving throw or be paralyzed for the duration.",
        concentration = true,
        classes = listOf(ClassType.BARD, ClassType.CLERIC, ClassType.DRUID, ClassType.SORCERER, ClassType.WARLOCK, ClassType.WIZARD)
    )

    // 3rd Level
    val FIREBALL = Spell(
        id = "fireball",
        name = "Fireball",
        level = 3,
        school = SpellSchool.EVOCATION,
        castingTime = "1 action",
        range = "150 feet",
        components = SpellComponents(verbal = true, somatic = true, material = true, materialDescription = "a tiny ball of bat guano and sulfur"),
        duration = "Instantaneous",
        description = "A bright streak flashes from your pointing finger to a point you choose within range and then blossoms with a low roar into an explosion of flame. Each creature in a 20-foot-radius sphere centered on that point must make a Dexterity saving throw. A target takes 8d6 fire damage on a failed save, or half as much damage on a successful one.",
        higherLevels = "When cast at 4th level or higher, the damage increases by 1d6 for each slot level above 3rd.",
        classes = listOf(ClassType.SORCERER, ClassType.WIZARD)
    )

    val COUNTERSPELL = Spell(
        id = "counterspell",
        name = "Counterspell",
        level = 3,
        school = SpellSchool.ABJURATION,
        castingTime = "1 reaction",
        range = "60 feet",
        components = SpellComponents(somatic = true),
        duration = "Instantaneous",
        description = "You attempt to interrupt a creature in the process of casting a spell. If the creature is casting a spell of 3rd level or lower, its spell fails and has no effect. If it is casting a spell of 4th level or higher, make an ability check using your spellcasting ability. The DC equals 10 + the spell's level. On a success, the creature's spell fails and has no effect.",
        classes = listOf(ClassType.SORCERER, ClassType.WARLOCK, ClassType.WIZARD)
    )

    // Collect all spells for easy access
    val allSpells = listOf(
        FIRE_BOLT, LIGHT, MAGE_HAND, SACRED_FLAME,
        MAGIC_MISSILE, CURE_WOUNDS, SHIELD, THUNDERWAVE, HEALING_WORD,
        MISTY_STEP, HOLD_PERSON,
        FIREBALL, COUNTERSPELL
    )
}
