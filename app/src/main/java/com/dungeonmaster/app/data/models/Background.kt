package com.dungeonmaster.app.data.models

import kotlinx.serialization.Serializable

@Serializable
enum class Background(
    val displayName: String,
    val description: String,
    val skillProficiencies: List<Skill>,
    val toolProficiencies: List<String>,
    val languages: Int,
    val equipment: List<String>,
    val gold: Int,
    val feature: BackgroundFeature
) {
    ACOLYTE(
        displayName = "Acolyte",
        description = "You have spent your life in service of a temple to a specific god or pantheon of gods.",
        skillProficiencies = listOf(Skill.INSIGHT, Skill.RELIGION),
        toolProficiencies = emptyList(),
        languages = 2,
        equipment = listOf("Holy symbol", "Prayer book or prayer wheel", "5 sticks of incense", "Vestments", "Common clothes"),
        gold = 15,
        feature = BackgroundFeature.SHELTER_OF_THE_FAITHFUL
    ),
    CHARLATAN(
        displayName = "Charlatan",
        description = "You have always had a way with people. You know what makes them tick.",
        skillProficiencies = listOf(Skill.DECEPTION, Skill.SLEIGHT_OF_HAND),
        toolProficiencies = listOf("Disguise kit", "Forgery kit"),
        languages = 0,
        equipment = listOf("Fine clothes", "Disguise kit", "Tools of the con"),
        gold = 15,
        feature = BackgroundFeature.FALSE_IDENTITY
    ),
    CRIMINAL(
        displayName = "Criminal",
        description = "You are an experienced criminal with a history of breaking the law.",
        skillProficiencies = listOf(Skill.DECEPTION, Skill.STEALTH),
        toolProficiencies = listOf("Gaming set", "Thieves' tools"),
        languages = 0,
        equipment = listOf("Crowbar", "Dark common clothes with hood"),
        gold = 15,
        feature = BackgroundFeature.CRIMINAL_CONTACT
    ),
    ENTERTAINER(
        displayName = "Entertainer",
        description = "You thrive in front of an audience. You know how to entrance them.",
        skillProficiencies = listOf(Skill.ACROBATICS, Skill.PERFORMANCE),
        toolProficiencies = listOf("Disguise kit", "Musical instrument"),
        languages = 0,
        equipment = listOf("Musical instrument", "Favor of an admirer", "Costume"),
        gold = 15,
        feature = BackgroundFeature.BY_POPULAR_DEMAND
    ),
    FOLK_HERO(
        displayName = "Folk Hero",
        description = "You come from a humble social rank, but you are destined for so much more.",
        skillProficiencies = listOf(Skill.ANIMAL_HANDLING, Skill.SURVIVAL),
        toolProficiencies = listOf("Artisan's tools", "Vehicles (land)"),
        languages = 0,
        equipment = listOf("Artisan's tools", "Shovel", "Iron pot", "Common clothes"),
        gold = 10,
        feature = BackgroundFeature.RUSTIC_HOSPITALITY
    ),
    GUILD_ARTISAN(
        displayName = "Guild Artisan",
        description = "You are a member of an artisan's guild, skilled in a particular field.",
        skillProficiencies = listOf(Skill.INSIGHT, Skill.PERSUASION),
        toolProficiencies = listOf("Artisan's tools"),
        languages = 1,
        equipment = listOf("Artisan's tools", "Letter of introduction from guild", "Traveler's clothes"),
        gold = 15,
        feature = BackgroundFeature.GUILD_MEMBERSHIP
    ),
    HERMIT(
        displayName = "Hermit",
        description = "You lived in seclusion for a formative part of your life.",
        skillProficiencies = listOf(Skill.MEDICINE, Skill.RELIGION),
        toolProficiencies = listOf("Herbalism kit"),
        languages = 1,
        equipment = listOf("Scroll case with notes", "Winter blanket", "Common clothes", "Herbalism kit"),
        gold = 5,
        feature = BackgroundFeature.DISCOVERY
    ),
    NOBLE(
        displayName = "Noble",
        description = "You understand wealth, power, and privilege.",
        skillProficiencies = listOf(Skill.HISTORY, Skill.PERSUASION),
        toolProficiencies = listOf("Gaming set"),
        languages = 1,
        equipment = listOf("Fine clothes", "Signet ring", "Scroll of pedigree"),
        gold = 25,
        feature = BackgroundFeature.POSITION_OF_PRIVILEGE
    ),
    OUTLANDER(
        displayName = "Outlander",
        description = "You grew up in the wilds, far from civilization.",
        skillProficiencies = listOf(Skill.ATHLETICS, Skill.SURVIVAL),
        toolProficiencies = listOf("Musical instrument"),
        languages = 1,
        equipment = listOf("Staff", "Hunting trap", "Trophy from animal", "Traveler's clothes"),
        gold = 10,
        feature = BackgroundFeature.WANDERER
    ),
    SAGE(
        displayName = "Sage",
        description = "You spent years learning the lore of the multiverse.",
        skillProficiencies = listOf(Skill.ARCANA, Skill.HISTORY),
        toolProficiencies = emptyList(),
        languages = 2,
        equipment = listOf("Bottle of black ink", "Quill", "Small knife", "Letter from colleague", "Common clothes"),
        gold = 10,
        feature = BackgroundFeature.RESEARCHER
    ),
    SAILOR(
        displayName = "Sailor",
        description = "You sailed on a seagoing vessel for years.",
        skillProficiencies = listOf(Skill.ATHLETICS, Skill.PERCEPTION),
        toolProficiencies = listOf("Navigator's tools", "Vehicles (water)"),
        languages = 0,
        equipment = listOf("Belaying pin (club)", "50 feet of silk rope", "Lucky charm", "Common clothes"),
        gold = 10,
        feature = BackgroundFeature.SHIPS_PASSAGE
    ),
    SOLDIER(
        displayName = "Soldier",
        description = "War has been your life for as long as you care to remember.",
        skillProficiencies = listOf(Skill.ATHLETICS, Skill.INTIMIDATION),
        toolProficiencies = listOf("Gaming set", "Vehicles (land)"),
        languages = 0,
        equipment = listOf("Insignia of rank", "Trophy from fallen enemy", "Gaming set", "Common clothes"),
        gold = 10,
        feature = BackgroundFeature.MILITARY_RANK
    ),
    URCHIN(
        displayName = "Urchin",
        description = "You grew up on the streets alone, orphaned, and poor.",
        skillProficiencies = listOf(Skill.SLEIGHT_OF_HAND, Skill.STEALTH),
        toolProficiencies = listOf("Disguise kit", "Thieves' tools"),
        languages = 0,
        equipment = listOf("Small knife", "Map of your home city", "Pet mouse", "Token of your parents", "Common clothes"),
        gold = 10,
        feature = BackgroundFeature.CITY_SECRETS
    )
}

@Serializable
enum class BackgroundFeature(val displayName: String, val description: String) {
    SHELTER_OF_THE_FAITHFUL(
        "Shelter of the Faithful",
        "You can perform religious ceremonies. You and your companions can receive free healing and care at a temple of your faith."
    ),
    FALSE_IDENTITY(
        "False Identity",
        "You have created a second identity that includes documentation, established acquaintances, and disguises."
    ),
    CRIMINAL_CONTACT(
        "Criminal Contact",
        "You have a reliable and trustworthy contact who acts as your liaison to a network of other criminals."
    ),
    BY_POPULAR_DEMAND(
        "By Popular Demand",
        "You can always find a place to perform. Your performances earn you free lodging and food."
    ),
    RUSTIC_HOSPITALITY(
        "Rustic Hospitality",
        "Common folk will provide you shelter and aid unless you pose a danger to them."
    ),
    GUILD_MEMBERSHIP(
        "Guild Membership",
        "Your guild offers lodging and support. You have access to the guild hall and can call on political connections."
    ),
    DISCOVERY(
        "Discovery",
        "You have a unique and powerful discovery. The nature of this revelation depends on your story."
    ),
    POSITION_OF_PRIVILEGE(
        "Position of Privilege",
        "People assume you have the right to be wherever you are. Common folk try to accommodate you."
    ),
    WANDERER(
        "Wanderer",
        "You have an excellent memory for maps and geography. You can always find food and fresh water for yourself and up to five others."
    ),
    RESEARCHER(
        "Researcher",
        "When you attempt to learn a piece of lore, you often know where and from whom you can obtain it."
    ),
    SHIPS_PASSAGE(
        "Ship's Passage",
        "You can secure free passage on a sailing ship for yourself and your companions."
    ),
    MILITARY_RANK(
        "Military Rank",
        "You have a military rank from your career. Soldiers loyal to your former organization still recognize your authority."
    ),
    CITY_SECRETS(
        "City Secrets",
        "You know secret patterns and flow of cities. You can find passages through the urban sprawl twice as fast."
    )
}
