package com.dungeonmaster.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Race(
    val type: RaceType,
    val subrace: Subrace? = null
) {
    val displayName: String get() = subrace?.displayName ?: type.displayName
    val abilityBonuses: Map<Ability, Int> get() {
        val base = type.abilityBonuses.toMutableMap()
        subrace?.abilityBonuses?.forEach { (ability, bonus) ->
            base[ability] = (base[ability] ?: 0) + bonus
        }
        return base
    }
    val speed: Int get() = subrace?.speed ?: type.speed
    val size: Size get() = type.size
    val traits: List<RacialTrait> get() = type.traits + (subrace?.traits ?: emptyList())
    val languages: List<Language> get() = type.languages + (subrace?.languages ?: emptyList())
}

@Serializable
enum class RaceType(
    val displayName: String,
    val abilityBonuses: Map<Ability, Int>,
    val speed: Int,
    val size: Size,
    val traits: List<RacialTrait>,
    val languages: List<Language>,
    val subraces: List<Subrace>,
    val description: String
) {
    HUMAN(
        displayName = "Human",
        abilityBonuses = mapOf(
            Ability.STRENGTH to 1,
            Ability.DEXTERITY to 1,
            Ability.CONSTITUTION to 1,
            Ability.INTELLIGENCE to 1,
            Ability.WISDOM to 1,
            Ability.CHARISMA to 1
        ),
        speed = 30,
        size = Size.MEDIUM,
        traits = listOf(RacialTrait.EXTRA_LANGUAGE),
        languages = listOf(Language.COMMON),
        subraces = emptyList(),
        description = "Versatile and ambitious, humans are the most adaptable of races."
    ),
    ELF(
        displayName = "Elf",
        abilityBonuses = mapOf(Ability.DEXTERITY to 2),
        speed = 30,
        size = Size.MEDIUM,
        traits = listOf(RacialTrait.DARKVISION, RacialTrait.FEY_ANCESTRY, RacialTrait.TRANCE, RacialTrait.KEEN_SENSES),
        languages = listOf(Language.COMMON, Language.ELVISH),
        subraces = listOf(Subrace.HIGH_ELF, Subrace.WOOD_ELF, Subrace.DARK_ELF),
        description = "Elves are a magical people of otherworldly grace, living in the world but not entirely part of it."
    ),
    DWARF(
        displayName = "Dwarf",
        abilityBonuses = mapOf(Ability.CONSTITUTION to 2),
        speed = 25,
        size = Size.MEDIUM,
        traits = listOf(RacialTrait.DARKVISION, RacialTrait.DWARVEN_RESILIENCE, RacialTrait.DWARVEN_COMBAT_TRAINING, RacialTrait.STONECUNNING),
        languages = listOf(Language.COMMON, Language.DWARVISH),
        subraces = listOf(Subrace.HILL_DWARF, Subrace.MOUNTAIN_DWARF),
        description = "Bold and hardy, dwarves are known as skilled warriors, miners, and workers of stone and metal."
    ),
    HALFLING(
        displayName = "Halfling",
        abilityBonuses = mapOf(Ability.DEXTERITY to 2),
        speed = 25,
        size = Size.SMALL,
        traits = listOf(RacialTrait.LUCKY, RacialTrait.BRAVE, RacialTrait.HALFLING_NIMBLENESS),
        languages = listOf(Language.COMMON, Language.HALFLING),
        subraces = listOf(Subrace.LIGHTFOOT, Subrace.STOUT),
        description = "The diminutive halflings survive in a world full of larger creatures by avoiding notice or, barring that, avoiding offense."
    ),
    DRAGONBORN(
        displayName = "Dragonborn",
        abilityBonuses = mapOf(Ability.STRENGTH to 2, Ability.CHARISMA to 1),
        speed = 30,
        size = Size.MEDIUM,
        traits = listOf(RacialTrait.DRACONIC_ANCESTRY, RacialTrait.BREATH_WEAPON, RacialTrait.DAMAGE_RESISTANCE),
        languages = listOf(Language.COMMON, Language.DRACONIC),
        subraces = emptyList(),
        description = "Born of dragons, dragonborn walk proudly through a world that greets them with fearful incomprehension."
    ),
    GNOME(
        displayName = "Gnome",
        abilityBonuses = mapOf(Ability.INTELLIGENCE to 2),
        speed = 25,
        size = Size.SMALL,
        traits = listOf(RacialTrait.DARKVISION, RacialTrait.GNOME_CUNNING),
        languages = listOf(Language.COMMON, Language.GNOMISH),
        subraces = listOf(Subrace.FOREST_GNOME, Subrace.ROCK_GNOME),
        description = "A gnome's energy and enthusiasm for living shines through every inch of their tiny bodies."
    ),
    HALF_ELF(
        displayName = "Half-Elf",
        abilityBonuses = mapOf(Ability.CHARISMA to 2),
        speed = 30,
        size = Size.MEDIUM,
        traits = listOf(RacialTrait.DARKVISION, RacialTrait.FEY_ANCESTRY, RacialTrait.SKILL_VERSATILITY),
        languages = listOf(Language.COMMON, Language.ELVISH),
        subraces = emptyList(),
        description = "Half-elves combine what some say are the best qualities of their elf and human parents."
    ),
    HALF_ORC(
        displayName = "Half-Orc",
        abilityBonuses = mapOf(Ability.STRENGTH to 2, Ability.CONSTITUTION to 1),
        speed = 30,
        size = Size.MEDIUM,
        traits = listOf(RacialTrait.DARKVISION, RacialTrait.MENACING, RacialTrait.RELENTLESS_ENDURANCE, RacialTrait.SAVAGE_ATTACKS),
        languages = listOf(Language.COMMON, Language.ORC),
        subraces = emptyList(),
        description = "Half-orcs' grayish pigmentation, sloping foreheads, jutting jaws, prominent teeth, and towering builds make their orcish heritage plain for all to see."
    ),
    TIEFLING(
        displayName = "Tiefling",
        abilityBonuses = mapOf(Ability.CHARISMA to 2, Ability.INTELLIGENCE to 1),
        speed = 30,
        size = Size.MEDIUM,
        traits = listOf(RacialTrait.DARKVISION, RacialTrait.HELLISH_RESISTANCE, RacialTrait.INFERNAL_LEGACY),
        languages = listOf(Language.COMMON, Language.INFERNAL),
        subraces = emptyList(),
        description = "To be greeted with stares and whispers, to suffer violence and insult on the street, to see mistrust and fear in every eye: this is the lot of the tiefling."
    )
}

@Serializable
enum class Subrace(
    val displayName: String,
    val abilityBonuses: Map<Ability, Int>,
    val speed: Int? = null,
    val traits: List<RacialTrait>,
    val languages: List<Language> = emptyList()
) {
    // Elf Subraces
    HIGH_ELF(
        displayName = "High Elf",
        abilityBonuses = mapOf(Ability.INTELLIGENCE to 1),
        traits = listOf(RacialTrait.ELF_WEAPON_TRAINING, RacialTrait.CANTRIP, RacialTrait.EXTRA_LANGUAGE)
    ),
    WOOD_ELF(
        displayName = "Wood Elf",
        abilityBonuses = mapOf(Ability.WISDOM to 1),
        speed = 35,
        traits = listOf(RacialTrait.ELF_WEAPON_TRAINING, RacialTrait.FLEET_OF_FOOT, RacialTrait.MASK_OF_THE_WILD)
    ),
    DARK_ELF(
        displayName = "Dark Elf (Drow)",
        abilityBonuses = mapOf(Ability.CHARISMA to 1),
        traits = listOf(RacialTrait.SUPERIOR_DARKVISION, RacialTrait.SUNLIGHT_SENSITIVITY, RacialTrait.DROW_MAGIC, RacialTrait.DROW_WEAPON_TRAINING)
    ),

    // Dwarf Subraces
    HILL_DWARF(
        displayName = "Hill Dwarf",
        abilityBonuses = mapOf(Ability.WISDOM to 1),
        traits = listOf(RacialTrait.DWARVEN_TOUGHNESS)
    ),
    MOUNTAIN_DWARF(
        displayName = "Mountain Dwarf",
        abilityBonuses = mapOf(Ability.STRENGTH to 2),
        traits = listOf(RacialTrait.DWARVEN_ARMOR_TRAINING)
    ),

    // Halfling Subraces
    LIGHTFOOT(
        displayName = "Lightfoot Halfling",
        abilityBonuses = mapOf(Ability.CHARISMA to 1),
        traits = listOf(RacialTrait.NATURALLY_STEALTHY)
    ),
    STOUT(
        displayName = "Stout Halfling",
        abilityBonuses = mapOf(Ability.CONSTITUTION to 1),
        traits = listOf(RacialTrait.STOUT_RESILIENCE)
    ),

    // Gnome Subraces
    FOREST_GNOME(
        displayName = "Forest Gnome",
        abilityBonuses = mapOf(Ability.DEXTERITY to 1),
        traits = listOf(RacialTrait.NATURAL_ILLUSIONIST, RacialTrait.SPEAK_WITH_SMALL_BEASTS)
    ),
    ROCK_GNOME(
        displayName = "Rock Gnome",
        abilityBonuses = mapOf(Ability.CONSTITUTION to 1),
        traits = listOf(RacialTrait.ARTIFICERS_LORE, RacialTrait.TINKER)
    )
}

@Serializable
enum class RacialTrait(val displayName: String, val description: String) {
    DARKVISION("Darkvision", "You can see in dim light within 60 feet as if it were bright light, and in darkness as if it were dim light."),
    SUPERIOR_DARKVISION("Superior Darkvision", "You can see in dim light within 120 feet as if it were bright light, and in darkness as if it were dim light."),
    FEY_ANCESTRY("Fey Ancestry", "You have advantage on saving throws against being charmed, and magic can't put you to sleep."),
    TRANCE("Trance", "You don't need to sleep. Instead, you meditate deeply for 4 hours a day."),
    KEEN_SENSES("Keen Senses", "You have proficiency in the Perception skill."),
    DWARVEN_RESILIENCE("Dwarven Resilience", "You have advantage on saving throws against poison, and resistance to poison damage."),
    DWARVEN_COMBAT_TRAINING("Dwarven Combat Training", "You have proficiency with battleaxe, handaxe, light hammer, and warhammer."),
    STONECUNNING("Stonecunning", "Whenever you make a History check related to the origin of stonework, you are considered proficient and add double your proficiency bonus."),
    LUCKY("Lucky", "When you roll a 1 on an attack roll, ability check, or saving throw, you can reroll and must use the new roll."),
    BRAVE("Brave", "You have advantage on saving throws against being frightened."),
    HALFLING_NIMBLENESS("Halfling Nimbleness", "You can move through the space of any creature that is of a size larger than yours."),
    DRACONIC_ANCESTRY("Draconic Ancestry", "You have draconic ancestry. Choose one type of dragon; your breath weapon and damage resistance are determined by the dragon type."),
    BREATH_WEAPON("Breath Weapon", "You can use your action to exhale destructive energy. Your draconic ancestry determines the size, shape, and damage type."),
    DAMAGE_RESISTANCE("Damage Resistance", "You have resistance to the damage type associated with your draconic ancestry."),
    GNOME_CUNNING("Gnome Cunning", "You have advantage on all Intelligence, Wisdom, and Charisma saving throws against magic."),
    SKILL_VERSATILITY("Skill Versatility", "You gain proficiency in two skills of your choice."),
    MENACING("Menacing", "You gain proficiency in the Intimidation skill."),
    RELENTLESS_ENDURANCE("Relentless Endurance", "When you are reduced to 0 hit points but not killed outright, you can drop to 1 hit point instead. You can't use this feature again until you finish a long rest."),
    SAVAGE_ATTACKS("Savage Attacks", "When you score a critical hit with a melee weapon attack, you can roll one of the weapon's damage dice one additional time."),
    HELLISH_RESISTANCE("Hellish Resistance", "You have resistance to fire damage."),
    INFERNAL_LEGACY("Infernal Legacy", "You know the thaumaturgy cantrip. At 3rd level, you can cast hellish rebuke once per day. At 5th level, you can cast darkness once per day."),
    ELF_WEAPON_TRAINING("Elf Weapon Training", "You have proficiency with the longsword, shortsword, shortbow, and longbow."),
    CANTRIP("Cantrip", "You know one cantrip of your choice from the wizard spell list."),
    EXTRA_LANGUAGE("Extra Language", "You can speak, read, and write one extra language of your choice."),
    FLEET_OF_FOOT("Fleet of Foot", "Your base walking speed increases to 35 feet."),
    MASK_OF_THE_WILD("Mask of the Wild", "You can attempt to hide even when you are only lightly obscured by foliage, heavy rain, falling snow, mist, and other natural phenomena."),
    SUNLIGHT_SENSITIVITY("Sunlight Sensitivity", "You have disadvantage on attack rolls and Perception checks that rely on sight when you, the target, or what you are trying to perceive is in direct sunlight."),
    DROW_MAGIC("Drow Magic", "You know the dancing lights cantrip. At 3rd level, you can cast faerie fire once per day. At 5th level, you can cast darkness once per day."),
    DROW_WEAPON_TRAINING("Drow Weapon Training", "You have proficiency with rapiers, shortswords, and hand crossbows."),
    DWARVEN_TOUGHNESS("Dwarven Toughness", "Your hit point maximum increases by 1, and it increases by 1 every time you gain a level."),
    DWARVEN_ARMOR_TRAINING("Dwarven Armor Training", "You have proficiency with light and medium armor."),
    NATURALLY_STEALTHY("Naturally Stealthy", "You can attempt to hide even when you are obscured only by a creature that is at least one size larger than you."),
    STOUT_RESILIENCE("Stout Resilience", "You have advantage on saving throws against poison, and you have resistance against poison damage."),
    NATURAL_ILLUSIONIST("Natural Illusionist", "You know the minor illusion cantrip."),
    SPEAK_WITH_SMALL_BEASTS("Speak with Small Beasts", "Through sounds and gestures, you can communicate simple ideas with Small or smaller beasts."),
    ARTIFICERS_LORE("Artificer's Lore", "Whenever you make a History check related to magic items, alchemical objects, or technological devices, you can add twice your proficiency bonus."),
    TINKER("Tinker", "You have proficiency with artisan's tools (tinker's tools). Using these tools, you can spend 1 hour and 10 gp worth of materials to construct a Tiny clockwork device.")
}

@Serializable
enum class Size(val displayName: String) {
    TINY("Tiny"),
    SMALL("Small"),
    MEDIUM("Medium"),
    LARGE("Large"),
    HUGE("Huge"),
    GARGANTUAN("Gargantuan")
}

@Serializable
enum class Language(val displayName: String) {
    COMMON("Common"),
    DWARVISH("Dwarvish"),
    ELVISH("Elvish"),
    GIANT("Giant"),
    GNOMISH("Gnomish"),
    GOBLIN("Goblin"),
    HALFLING("Halfling"),
    ORC("Orc"),
    ABYSSAL("Abyssal"),
    CELESTIAL("Celestial"),
    DRACONIC("Draconic"),
    DEEP_SPEECH("Deep Speech"),
    INFERNAL("Infernal"),
    PRIMORDIAL("Primordial"),
    SYLVAN("Sylvan"),
    UNDERCOMMON("Undercommon")
}
