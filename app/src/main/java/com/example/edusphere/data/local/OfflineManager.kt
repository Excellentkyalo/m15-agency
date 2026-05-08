package com.example.edusphere.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// ✅ Top-level property for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "edu_prefs")

class OfflineManager(private val context: Context) {

    private object Keys {
        val NOTES_CACHE = stringPreferencesKey("notes_cache")
        val IS_OFFLINE_MODE = booleanPreferencesKey("is_offline")
    }

    // ✅ FIXED: Using updateData instead of edit to bypass missing import issues
    suspend fun saveNotesCache(jsonData: String) {
        context.dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                this[Keys.NOTES_CACHE] = jsonData
            }
        }
    }

    // ✅ FIXED: Using catch to handle IO exceptions safely
    val notesCache: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[Keys.NOTES_CACHE]
        }

    // ✅ FIXED: Using updateData instead of edit
    suspend fun setOfflineMode(isOffline: Boolean) {
        context.dataStore.updateData { currentPreferences ->
            currentPreferences.toMutablePreferences().apply {
                this[Keys.IS_OFFLINE_MODE] = isOffline
            }
        }
    }
}