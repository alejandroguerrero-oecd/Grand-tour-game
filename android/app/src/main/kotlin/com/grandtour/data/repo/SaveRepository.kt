package com.grandtour.data.repo

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.grandtour.data.json.AppJson
import com.grandtour.data.model.GameState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "grand_tour")

@Singleton
class SaveRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val activeGameKey = stringPreferencesKey("active_game")
    private val completedChaptersKey = stringSetPreferencesKey("completed_chapters")
    private val soundEnabledKey = booleanPreferencesKey("sound_enabled")

    suspend fun saveActiveGame(state: GameState) {
        context.dataStore.edit { prefs ->
            prefs[activeGameKey] = AppJson.encodeToString(GameState.serializer(), state)
        }
    }

    suspend fun loadActiveGame(): GameState? {
        val raw = context.dataStore.data.map { it[activeGameKey] }.first() ?: return null
        return runCatching { AppJson.decodeFromString(GameState.serializer(), raw) }.getOrNull()
    }

    suspend fun clearActiveGame() {
        context.dataStore.edit { it.remove(activeGameKey) }
    }

    val activeGameFlow: Flow<GameState?> = context.dataStore.data.map { prefs ->
        prefs[activeGameKey]?.let {
            runCatching { AppJson.decodeFromString(GameState.serializer(), it) }.getOrNull()
        }
    }

    suspend fun markChapterCompleted(chapterId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[completedChaptersKey] ?: emptySet()
            prefs[completedChaptersKey] = current + chapterId
        }
    }

    val completedChaptersFlow: Flow<Set<String>> =
        context.dataStore.data.map { it[completedChaptersKey] ?: emptySet() }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[soundEnabledKey] = enabled }
    }

    val soundEnabledFlow: Flow<Boolean> =
        context.dataStore.data.map { it[soundEnabledKey] ?: true }
}
