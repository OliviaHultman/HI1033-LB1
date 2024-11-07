package mobappdev.example.nback_cimpl.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import mobappdev.example.nback_cimpl.ui.viewmodels.Settings
import java.io.IOException

/**
 * This repository provides a way to interact with the DataStore api,
 * with this API you can save key:value pairs
 *
 * Currently this file contains only one thing: getting the highscore as a flow
 * and writing to the highscore preference.
 * (a flow is like a waterpipe; if you put something different in the start,
 * the end automatically updates as long as the pipe is open)
 *
 * Date: 25-08-2023
 * Version: Skeleton code version 1.0
 * Author: Yeetivity
 *
 */

class UserPreferencesRepository (
    private val dataStore: DataStore<Preferences>
){
    private companion object {
        val HIGHSCORE = intPreferencesKey("highscore")
        val GAME_TYPE = stringPreferencesKey("gameType")
        val SIZE = intPreferencesKey("size")
        val EVENT_INTERVAL = longPreferencesKey("eventInterval")
        val N_BACK = intPreferencesKey("nBack")
        val VISUAL_COMBINATIONS = intPreferencesKey("visualCombinations")
        val AUDIO_COMBINAIONS = intPreferencesKey("audioCombinations")
        const val TAG = "UserPreferencesRepo"
    }

    val highscore: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[HIGHSCORE] ?: 0
        }

    val gameType: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[GAME_TYPE] ?: "Visual"
        }

    val size: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[SIZE] ?: 10
        }
    val eventInterval: Flow<Long> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[EVENT_INTERVAL] ?: 2000L
        }
    val nBack: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[N_BACK] ?: 2
        }
    val visualCombinations: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[VISUAL_COMBINATIONS] ?: 9
        }
    val audioCombinations: Flow<Int> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[AUDIO_COMBINAIONS] ?: 9
        }

    suspend fun saveHighScore(score: Int) {
        dataStore.edit { preferences ->
            preferences[HIGHSCORE] = score
        }
    }

    suspend fun saveSettings(settings: Settings) {
        dataStore.edit { preferences ->
            preferences[GAME_TYPE] = settings.gameType.toString()
        }
        dataStore.edit { preferences ->
            preferences[SIZE] = settings.size
        }
        dataStore.edit { preferences ->
            preferences[EVENT_INTERVAL] = settings.eventInterval
        }
        dataStore.edit { preferences ->
            preferences[N_BACK] = settings.nBack
        }
        dataStore.edit { preferences ->
            preferences[VISUAL_COMBINATIONS] = settings.visualCombinations
        }
        dataStore.edit { preferences ->
            preferences[AUDIO_COMBINAIONS] = settings.audioCombinations
        }
    }
}