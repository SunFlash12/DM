package com.dungeonmaster.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CharacterClass(
    val type: ClassType,
    val subclass: String? = null
) {
    val displayName: String get() = type.displayName
    val hitDie: Int get() = type.hitDie
    val primaryAbility: List<Ability> get() = type.primaryAbility
    val savingThrowProficiencies: List<Ability> get() = type.savingThrowProficiencies
    val armorProficiencies: List<ArmorType> get() = type.armorProficiencies
    val weaponProficiencies: List<WeaponProficiency> get() = type.weaponProficiencies
    val skillChoices: Int get() = type.skillChoices
    val availableSkills: List<Skill> get() = type.availableSkills
    val isSpellcaster: Boolean get() = type.isSpellcaster
    val spellcastingAbility: Ability? get() = type.spellcastingAbility
}

@Serializable
enum class ClassType(
    val displayName: String,
    val hitDie: Int,
    val primaryAbility: List<Ability>,
    val savingThrowProficiencies: List<Ability>,
    val armorProficiencies: List<ArmorType>,
    val weaponProficiencies: List<WeaponProficiency>,
    val skillChoices: Int,
    val availableSkills: List<Skill>,
    val isSpellcaster: Boolean,
    val spellcastingAbility: Ability?,
    val description: String
) {
    BARBARIAN(
        displayName = "Barbarian",
        hitDie = 12,
        primaryAbility = listOf(Ability.STRENGTH),
        savingThrowProficiencies = listOf(Ability.STRENGTH, Ability.CONSTITUTION),
        armorProficiencies = listOf(ArmorType.LIGHT, ArmorType.MEDIUM, ArmorType.SHIELD),
        weaponProficiencies = listOf(WeaponProficiency.SIMPLE, WeaponProficiency.MARTIAL),
        skillChoices = 2,
        availableSkills = listOf(Skill.ANIMAL_HANDLING, Skill.ATHLETICS, Skill.INTIMIDATION, Skill.NATURE, Skill.PERCEPTION, Skill.SURVIVAL),
        isSpellcaster = false,
        spellcastingAbility = null,
        description = "A fierce warrior who can enter a battle rage"
    ),
    BARD(
        displayName = "Bard",
        hitDie = 8,
        primaryAbility = listOf(Ability.CHARISMA),
        savingThrowProficiencies = listOf(Ability.DEXTERITY, Ability.CHARISMA),
        armorProficiencies = listOf(ArmorType.LIGHT),
        weaponProficiencies = listOf(WeaponProficiency.SIMPLE, WeaponProficiency.HAND_CROSSBOW, WeaponProficiency.LONGSWORD, WeaponProficiency.RAPIER, WeaponProficiency.SHORTSWORD),
        skillChoices = 3,
        availableSkills = Skill.entries,
        isSpellcaster = true,
        spellcastingAbility = Ability.CHARISMA,
        description = "An inspiring magician whose power echoes the music of creation"
    ),
    CLERIC(
        displayName = "Cleric",
        hitDie = 8,
        primaryAbility = listOf(Ability.WISDOM),
        savingThrowProficiencies = listOf(Ability.WISDOM, Ability.CHARISMA),
        armorProficiencies = listOf(ArmorType.LIGHT, ArmorType.MEDIUM, ArmorType.SHIELD),
        weaponProficiencies = listOf(WeaponProficiency.SIMPLE),
        skillChoices = 2,
        availableSkills = listOf(Skill.HISTORY, Skill.INSIGHT, Skill.MEDICINE, Skill.PERSUASION, Skill.RELIGION),
        isSpellcaster = true,
        spellcastingAbility = Ability.WISDOM,
        description = "A priestly champion who wields divine magic in service of a higher power"
    ),
    DRUID(
        displayName = "Druid",
        hitDie = 8,
        primaryAbility = listOf(Ability.WISDOM),
        savingThrowProficiencies = listOf(Ability.INTELLIGENCE, Ability.WISDOM),
        armorProficiencies = listOf(ArmorType.LIGHT, ArmorType.MEDIUM, ArmorType.SHIELD),
        weaponProficiencies = listOf(WeaponProficiency.CLUB, WeaponProficiency.DAGGER, WeaponProficiency.DART, WeaponProficiency.JAVELIN, WeaponProficiency.MACE, WeaponProficiency.QUARTERSTAFF, WeaponProficiency.SCIMITAR, WeaponProficiency.SICKLE, WeaponProficiency.SLING, WeaponProficiency.SPEAR),
        skillChoices = 2,
        availableSkills = listOf(Skill.ARCANA, Skill.ANIMAL_HANDLING, Skill.INSIGHT, Skill.MEDICINE, Skill.NATURE, Skill.PERCEPTION, Skill.RELIGION, Skill.SURVIVAL),
        isSpellcaster = true,
        spellcastingAbility = Ability.WISDOM,
        description = "A priest of the Old Faith, wielding the powers of nature"
    ),
    FIGHTER(
        displayName = "Fighter",
        hitDie = 10,
        primaryAbility = listOf(Ability.STRENGTH, Ability.DEXTERITY),
        savingThrowProficiencies = listOf(Ability.STRENGTH, Ability.CONSTITUTION),
        armorProficiencies = listOf(ArmorType.LIGHT, ArmorType.MEDIUM, ArmorType.HEAVY, ArmorType.SHIELD),
        weaponProficiencies = listOf(WeaponProficiency.SIMPLE, WeaponProficiency.MARTIAL),
        skillChoices = 2,
        availableSkills = listOf(Skill.ACROBATICS, Skill.ANIMAL_HANDLING, Skill.ATHLETICS, Skill.HISTORY, Skill.INSIGHT, Skill.INTIMIDATION, Skill.PERCEPTION, Skill.SURVIVAL),
        isSpellcaster = false,
        spellcastingAbility = null,
        description = "A master of martial combat, skilled with a variety of weapons and armor"
    ),
    MONK(
        displayName = "Monk",
        hitDie = 8,
        primaryAbility = listOf(Ability.DEXTERITY, Ability.WISDOM),
        savingThrowProficiencies = listOf(Ability.STRENGTH, Ability.DEXTERITY),
        armorProficiencies = emptyList(),
        weaponProficiencies = listOf(WeaponProficiency.SIMPLE, WeaponProficiency.SHORTSWORD),
        skillChoices = 2,
        availableSkills = listOf(Skill.ACROBATICS, Skill.ATHLETICS, Skill.HISTORY, Skill.INSIGHT, Skill.RELIGION, Skill.STEALTH),
        isSpellcaster = false,
        spellcastingAbility = null,
        description = "A master of martial arts, harnessing the power of the body in pursuit of physical and spiritual perfection"
    ),
    PALADIN(
        displayName = "Paladin",
        hitDie = 10,
        primaryAbility = listOf(Ability.STRENGTH, Ability.CHARISMA),
        savingThrowProficiencies = listOf(Ability.WISDOM, Ability.CHARISMA),
        armorProficiencies = listOf(ArmorType.LIGHT, ArmorType.MEDIUM, ArmorType.HEAVY, ArmorType.SHIELD),
        weaponProficiencies = listOf(WeaponProficiency.SIMPLE, WeaponProficiency.MARTIAL),
        skillChoices = 2,
        availableSkills = listOf(Skill.ATHLETICS, Skill.INSIGHT, Skill.INTIMIDATION, Skill.MEDICINE, Skill.PERSUASION, Skill.RELIGION),
        isSpellcaster = true,
        spellcastingAbility = Ability.CHARISMA,
        description = "A holy warrior bound to a sacred oath"
    ),
    RANGER(
        displayName = "Ranger",
        hitDie = 10,
        primaryAbility = listOf(Ability.DEXTERITY, Ability.WISDOM),
        savingThrowProficiencies = listOf(Ability.STRENGTH, Ability.DEXTERITY),
        armorProficiencies = listOf(ArmorType.LIGHT, ArmorType.MEDIUM, ArmorType.SHIELD),
        weaponProficiencies = listOf(WeaponProficiency.SIMPLE, WeaponProficiency.MARTIAL),
        skillChoices = 3,
        availableSkills = listOf(Skill.ANIMAL_HANDLING, Skill.ATHLETICS, Skill.INSIGHT, Skill.INVESTIGATION, Skill.NATURE, Skill.PERCEPTION, Skill.STEALTH, Skill.SURVIVAL),
        isSpellcaster = true,
        spellcastingAbility = Ability.WISDOM,
        description = "A warrior who combats threats on the edges of civilization"
    ),
    ROGUE(
        displayName = "Rogue",
        hitDie = 8,
        primaryAbility = listOf(Ability.DEXTERITY),
        savingThrowProficiencies = listOf(Ability.DEXTERITY, Ability.INTELLIGENCE),
        armorProficiencies = listOf(ArmorType.LIGHT),
        weaponProficiencies = listOf(WeaponProficiency.SIMPLE, WeaponProficiency.HAND_CROSSBOW, WeaponProficiency.LONGSWORD, WeaponProficiency.RAPIER, WeaponProficiency.SHORTSWORD),
        skillChoices = 4,
        availableSkills = listOf(Skill.ACROBATICS, Skill.ATHLETICS, Skill.DECEPTION, Skill.INSIGHT, Skill.INTIMIDATION, Skill.INVESTIGATION, Skill.PERCEPTION, Skill.PERFORMANCE, Skill.PERSUASION, Skill.SLEIGHT_OF_HAND, Skill.STEALTH),
        isSpellcaster = false,
        spellcastingAbility = null,
        description = "A scoundrel who uses stealth and trickery to overcome obstacles and enemies"
    ),
    SORCERER(
        displayName = "Sorcerer",
        hitDie = 6,
        primaryAbility = listOf(Ability.CHARISMA),
        savingThrowProficiencies = listOf(Ability.CONSTITUTION, Ability.CHARISMA),
        armorProficiencies = emptyList(),
        weaponProficiencies = listOf(WeaponProficiency.DAGGER, WeaponProficiency.DART, WeaponProficiency.SLING, WeaponProficiency.QUARTERSTAFF, WeaponProficiency.LIGHT_CROSSBOW),
        skillChoices = 2,
        availableSkills = listOf(Skill.ARCANA, Skill.DECEPTION, Skill.INSIGHT, Skill.INTIMIDATION, Skill.PERSUASION, Skill.RELIGION),
        isSpellcaster = true,
        spellcastingAbility = Ability.CHARISMA,
        description = "A spellcaster who draws on inherent magic from a gift or bloodline"
    ),
    WARLOCK(
        displayName = "Warlock",
        hitDie = 8,
        primaryAbility = listOf(Ability.CHARISMA),
        savingThrowProficiencies = listOf(Ability.WISDOM, Ability.CHARISMA),
        armorProficiencies = listOf(ArmorType.LIGHT),
        weaponProficiencies = listOf(WeaponProficiency.SIMPLE),
        skillChoices = 2,
        availableSkills = listOf(Skill.ARCANA, Skill.DECEPTION, Skill.HISTORY, Skill.INTIMIDATION, Skill.INVESTIGATION, Skill.NATURE, Skill.RELIGION),
        isSpellcaster = true,
        spellcastingAbility = Ability.CHARISMA,
        description = "A wielder of magic that is derived from a bargain with an extraplanar entity"
    ),
    WIZARD(
        displayName = "Wizard",
        hitDie = 6,
        primaryAbility = listOf(Ability.INTELLIGENCE),
        savingThrowProficiencies = listOf(Ability.INTELLIGENCE, Ability.WISDOM),
        armorProficiencies = emptyList(),
        weaponProficiencies = listOf(WeaponProficiency.DAGGER, WeaponProficiency.DART, WeaponProficiency.SLING, WeaponProficiency.QUARTERSTAFF, WeaponProficiency.LIGHT_CROSSBOW),
        skillChoices = 2,
        availableSkills = listOf(Skill.ARCANA, Skill.HISTORY, Skill.INSIGHT, Skill.INVESTIGATION, Skill.MEDICINE, Skill.RELIGION),
        isSpellcaster = true,
        spellcastingAbility = Ability.INTELLIGENCE,
        description = "A scholarly magic-user capable of manipulating the structures of reality"
    )
}

@Serializable
enum class ArmorType(val displayName: String) {
    LIGHT("Light Armor"),
    MEDIUM("Medium Armor"),
    HEAVY("Heavy Armor"),
    SHIELD("Shields")
}

@Serializable
enum class WeaponProficiency(val displayName: String) {
    SIMPLE("Simple Weapons"),
    MARTIAL("Martial Weapons"),
    // Individual weapons
    CLUB("Club"),
    DAGGER("Dagger"),
    DART("Dart"),
    JAVELIN("Javelin"),
    MACE("Mace"),
    QUARTERSTAFF("Quarterstaff"),
    SCIMITAR("Scimitar"),
    SICKLE("Sickle"),
    SLING("Sling"),
    SPEAR("Spear"),
    LIGHT_CROSSBOW("Light Crossbow"),
    HAND_CROSSBOW("Hand Crossbow"),
    LONGSWORD("Longsword"),
    RAPIER("Rapier"),
    SHORTSWORD("Shortsword")
}

// Class features by level
@Serializable
data class ClassFeature(
    val name: String,
    val level: Int,
    val description: String,
    val classType: ClassType
)

// Starting equipment options
@Serializable
data class StartingEquipment(
    val classType: ClassType,
    val guaranteedItems: List<String>,
    val choiceGroups: List<EquipmentChoice>
)

@Serializable
data class EquipmentChoice(
    val options: List<List<String>>
)
