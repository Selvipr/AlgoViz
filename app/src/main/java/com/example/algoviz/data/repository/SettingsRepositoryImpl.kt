package com.example.algoviz.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.algoviz.domain.repository.AppTheme
import com.example.algoviz.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val dataStore = context.dataStore

    companion object {
        val THEME_KEY = stringPreferencesKey("app_theme")
    }

    override val theme: Flow<AppTheme> = dataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_KEY] ?: AppTheme.SYSTEM.name
            try {
                AppTheme.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                AppTheme.SYSTEM
            }
        }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }
}
