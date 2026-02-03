package com.dungeonmaster.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val DICE_ANIMATION = booleanPreferencesKey("dice_animation")
        val AUTO_ROLL = booleanPreferencesKey("auto_roll")
        val ADVENTURE_SETTING = stringPreferencesKey("adventure_setting")
        val NARRATOR_VOICE = booleanPreferencesKey("narrator_voice")
    }

    val geminiApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.GEMINI_API_KEY] ?: ""
    }

    val darkTheme: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_THEME] ?: true
    }

    val soundEffectsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SOUND_EFFECTS] ?: true
    }

    val diceAnimationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DICE_ANIMATION] ?: true
    }

    val autoRollEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.AUTO_ROLL] ?: false
    }

    val adventureSetting: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ADVENTURE_SETTING] ?: "CLASSIC_FANTASY"
    }

    val narratorVoiceEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NARRATOR_VOICE] ?: false
    }

    suspend fun setGeminiApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GEMINI_API_KEY] = apiKey
        }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME] = enabled
        }
    }

    suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_EFFECTS] = enabled
        }
    }

    suspend fun setDiceAnimationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DICE_ANIMATION] = enabled
        }
    }

    suspend fun setAutoRollEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_ROLL] = enabled
        }
    }

    suspend fun setAdventureSetting(setting: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ADVENTURE_SETTING] = setting
        }
    }

    suspend fun setNarratorVoiceEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NARRATOR_VOICE] = enabled
        }
    }
}
