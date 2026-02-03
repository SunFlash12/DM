package com.dungeonmaster.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: String,
    val name: String,
    val type: ItemType,
    val description: String = "",
    val weight: Float = 0f,
    val value: Int = 0, // in copper pieces
    val quantity: Int = 1,
    val properties: ItemProperties? = null,
    val isEquipped: Boolean = false,
    val isMagical: Boolean = false,
    val attunementRequired: Boolean = false,
    val isAttuned: Boolean = false
)

@Serializable
enum class ItemType(val displayName: String) {
    WEAPON("Weapon"),
    ARMOR("Armor"),
    SHIELD("Shield"),
    AMMUNITION("Ammunition"),
    POTION("Potion"),
    SCROLL("Scroll"),
    WAND("Wand"),
    RING("Ring"),
    WONDROUS("Wondrous Item"),
    TOOL("Tool"),
    GEAR("Adventuring Gear"),
    CONSUMABLE("Consumable"),
    TREASURE("Treasure"),
    CONTAINER("Container"),
    CLOTHING("Clothing")
}

@Serializable
data class ItemProperties(
    // Weapon properties
    val damage: String? = null,
    val damageType: DamageType? = null,
    val weaponType: WeaponType? = null,
    val weaponProperties: List<WeaponProperty> = emptyList(),
    val range: String? = null,

    // Armor properties
    val armorType: ArmorType? = null,
    val baseAC: Int? = null,
    val maxDexBonus: Int? = null,
    val stealthDisadvantage: Boolean = false,
    val strengthRequirement: Int? = null,

    // Spell scroll/wand properties
    val spellName: String? = null,
    val spellLevel: Int? = null,
    val charges: Int? = null,
    val maxCharges: Int? = null,

    // Potion/consumable properties
    val effect: String? = null,
    val duration: String? = null
)

@Serializable
enum class WeaponType(val displayName: String) {
    SIMPLE_MELEE("Simple Melee"),
    SIMPLE_RANGED("Simple Ranged"),
    MARTIAL_MELEE("Martial Melee"),
    MARTIAL_RANGED("Martial Ranged")
}

@Serializable
enum class WeaponProperty(val displayName: String, val description: String) {
    AMMUNITION("Ammunition", "Requires ammunition to make ranged attacks"),
    FINESSE("Finesse", "Can use DEX instead of STR for attack and damage"),
    HEAVY("Heavy", "Small creatures have disadvantage"),
    LIGHT("Light", "Ideal for two-weapon fighting"),
    LOADING("Loading", "Can only fire once per action regardless of attacks"),
    RANGE("Range", "Can make ranged attacks"),
    REACH("Reach", "Adds 5 feet to attack reach"),
    SPECIAL("Special", "Has special rules"),
    THROWN("Thrown", "Can be thrown for ranged attack"),
    TWO_HANDED("Two-Handed", "Requires two hands to wield"),
    VERSATILE("Versatile", "Can be used with one or two hands")
}

@Serializable
enum class DamageType(val displayName: String) {
    ACID("Acid"),
    BLUDGEONING("Bludgeoning"),
    COLD("Cold"),
    FIRE("Fire"),
    FORCE("Force"),
    LIGHTNING("Lightning"),
    NECROTIC("Necrotic"),
    PIERCING("Piercing"),
    POISON("Poison"),
    PSYCHIC("Psychic"),
    RADIANT("Radiant"),
    SLASHING("Slashing"),
    THUNDER("Thunder")
}

// Predefined common items
object CommonItems {
    val LONGSWORD = Item(
        id = "longsword",
        name = "Longsword",
        type = ItemType.WEAPON,
        description = "A versatile blade favored by many warriors",
        weight = 3f,
        value = 1500, // 15 gp
        properties = ItemProperties(
            damage = "1d8",
            damageType = DamageType.SLASHING,
            weaponType = WeaponType.MARTIAL_MELEE,
            weaponProperties = listOf(WeaponProperty.VERSATILE)
        )
    )

    val SHORTBOW = Item(
        id = "shortbow",
        name = "Shortbow",
        type = ItemType.WEAPON,
        description = "A simple wooden bow",
        weight = 2f,
        value = 2500,
        properties = ItemProperties(
            damage = "1d6",
            damageType = DamageType.PIERCING,
            weaponType = WeaponType.SIMPLE_RANGED,
            weaponProperties = listOf(WeaponProperty.AMMUNITION, WeaponProperty.TWO_HANDED, WeaponProperty.RANGE),
            range = "80/320"
        )
    )

    val DAGGER = Item(
        id = "dagger",
        name = "Dagger",
        type = ItemType.WEAPON,
        description = "A simple blade for close combat or throwing",
        weight = 1f,
        value = 200,
        properties = ItemProperties(
            damage = "1d4",
            damageType = DamageType.PIERCING,
            weaponType = WeaponType.SIMPLE_MELEE,
            weaponProperties = listOf(WeaponProperty.FINESSE, WeaponProperty.LIGHT, WeaponProperty.THROWN),
            range = "20/60"
        )
    )

    val LEATHER_ARMOR = Item(
        id = "leather_armor",
        name = "Leather Armor",
        type = ItemType.ARMOR,
        description = "Made of supple leather, this armor provides basic protection",
        weight = 10f,
        value = 1000,
        properties = ItemProperties(
            armorType = ArmorType.LIGHT,
            baseAC = 11
        )
    )

    val CHAIN_MAIL = Item(
        id = "chain_mail",
        name = "Chain Mail",
        type = ItemType.ARMOR,
        description = "Interlocking metal rings form this heavy armor",
        weight = 55f,
        value = 7500,
        properties = ItemProperties(
            armorType = ArmorType.HEAVY,
            baseAC = 16,
            maxDexBonus = 0,
            stealthDisadvantage = true,
            strengthRequirement = 13
        )
    )

    val SHIELD = Item(
        id = "shield",
        name = "Shield",
        type = ItemType.SHIELD,
        description = "A wooden or metal shield (+2 AC)",
        weight = 6f,
        value = 1000,
        properties = ItemProperties(
            baseAC = 2
        )
    )

    val HEALING_POTION = Item(
        id = "healing_potion",
        name = "Potion of Healing",
        type = ItemType.POTION,
        description = "A red potion that restores health when consumed",
        weight = 0.5f,
        value = 5000,
        properties = ItemProperties(
            effect = "Regain 2d4+2 hit points"
        )
    )

    val TORCH = Item(
        id = "torch",
        name = "Torch",
        type = ItemType.GEAR,
        description = "Provides bright light in 20-foot radius, dim light for 20 feet more. Burns for 1 hour.",
        weight = 1f,
        value = 1
    )

    val ROPE = Item(
        id = "rope_50ft",
        name = "Rope (50 ft)",
        type = ItemType.GEAR,
        description = "Hemp rope with many uses",
        weight = 10f,
        value = 100
    )

    val BACKPACK = Item(
        id = "backpack",
        name = "Backpack",
        type = ItemType.CONTAINER,
        description = "Can hold 30 pounds or 1 cubic foot of gear",
        weight = 5f,
        value = 200
    )
}
