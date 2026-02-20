package com.example.assignmenttracker.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val COURSE = stringPreferencesKey("course")
        val ACADEMIC_YEAR = stringPreferencesKey("academic_year")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                userName = preferences[PreferencesKeys.USER_NAME] ?: "",
                course = preferences[PreferencesKeys.COURSE] ?: "",
                academicYear = preferences[PreferencesKeys.ACADEMIC_YEAR] ?: "",
                themeMode = ThemeMode.valueOf(
                    preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
                ),
                hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
            )
        }

    suspend fun updateUserPreferences(
        userName: String? = null,
        course: String? = null,
        academicYear: String? = null,
        themeMode: ThemeMode? = null,
        hasCompletedOnboarding: Boolean? = null
    ) {
        context.dataStore.edit { preferences ->
            userName?.let { preferences[PreferencesKeys.USER_NAME] = it }
            course?.let { preferences[PreferencesKeys.COURSE] = it }
            academicYear?.let { preferences[PreferencesKeys.ACADEMIC_YEAR] = it }
            themeMode?.let { preferences[PreferencesKeys.THEME_MODE] = it.name }
            hasCompletedOnboarding?.let { preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = it }
        }
    }
}
