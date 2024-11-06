package mobappdev.example.nback_cimpl.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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
            preferences[SIZE] ?: 0
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
            preferences[EVENT_INTERVAL] ?: 0
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
            preferences[N_BACK] ?: 0
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
            preferences[VISUAL_COMBINATIONS] ?: 0
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
            preferences[AUDIO_COMBINAIONS] ?: 0
        }

    suspend fun saveHighScore(score: Int) {
        dataStore.edit { preferences ->
            preferences[HIGHSCORE] = score
        }
    }

    suspend fun saveSettings(size: Int, eventInterval: Long, nBack: Int, visualCombinations: Int, audioCombinations: Int) {
        dataStore.edit { preferences ->
            preferences[SIZE] = size
        }
        dataStore.edit { preferences ->
            preferences[EVENT_INTERVAL] = eventInterval
        }
        dataStore.edit { preferences ->
            preferences[N_BACK] = nBack
        }
        dataStore.edit { preferences ->
            preferences[VISUAL_COMBINATIONS] = visualCombinations
        }
        dataStore.edit { preferences ->
            preferences[AUDIO_COMBINAIONS] = audioCombinations
        }
    }
}