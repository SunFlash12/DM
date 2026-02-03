package com.dungeonmaster.app.data.database

import androidx.room.TypeConverter
import com.dungeonmaster.app.data.models.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    // Race converters
    @TypeConverter
    fun fromRace(race: Race): String = json.encodeToString(race)

    @TypeConverter
    fun toRace(value: String): Race = json.decodeFromString(value)

    // CharacterClass converters
    @TypeConverter
    fun fromCharacterClass(characterClass: CharacterClass): String = json.encodeToString(characterClass)

    @TypeConverter
    fun toCharacterClass(value: String): CharacterClass = json.decodeFromString(value)

    // Background converters
    @TypeConverter
    fun fromBackground(background: Background): String = json.encodeToString(background)

    @TypeConverter
    fun toBackground(value: String): Background = json.decodeFromString(value)

    // Alignment converters
    @TypeConverter
    fun fromAlignment(alignment: Alignment): String = alignment.name

    @TypeConverter
    fun toAlignment(value: String): Alignment = Alignment.valueOf(value)

    // List<Ability> converters
    @TypeConverter
    fun fromAbilityList(abilities: List<Ability>): String = json.encodeToString(abilities)

    @TypeConverter
    fun toAbilityList(value: String): List<Ability> = json.decodeFromString(value)

    // List<Skill> converters
    @TypeConverter
    fun fromSkillList(skills: List<Skill>): String = json.encodeToString(skills)

    @TypeConverter
    fun toSkillList(value: String): List<Skill> = json.decodeFromString(value)

    // List<Item> converters
    @TypeConverter
    fun fromItemList(items: List<Item>): String = json.encodeToString(items)

    @TypeConverter
    fun toItemList(value: String): List<Item> = json.decodeFromString(value)

    // List<Spell> converters
    @TypeConverter
    fun fromSpellList(spells: List<Spell>): String = json.encodeToString(spells)

    @TypeConverter
    fun toSpellList(value: String): List<Spell> = json.decodeFromString(value)

    // List<Condition> converters
    @TypeConverter
    fun fromConditionList(conditions: List<Condition>): String = json.encodeToString(conditions)

    @TypeConverter
    fun toConditionList(value: String): List<Condition> = json.decodeFromString(value)

    // Map<Int, Int> converters (for spell slots)
    @TypeConverter
    fun fromIntIntMap(map: Map<Int, Int>): String = json.encodeToString(map)

    @TypeConverter
    fun toIntIntMap(value: String): Map<Int, Int> = json.decodeFromString(value)

    // MessageRole converters
    @TypeConverter
    fun fromMessageRole(role: MessageRole): String = role.name

    @TypeConverter
    fun toMessageRole(value: String): MessageRole = MessageRole.valueOf(value)

    // MessageMetadata converters
    @TypeConverter
    fun fromMessageMetadata(metadata: MessageMetadata?): String? = metadata?.let { json.encodeToString(it) }

    @TypeConverter
    fun toMessageMetadata(value: String?): MessageMetadata? = value?.let { json.decodeFromString(it) }
}
