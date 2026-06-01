package com.example.tegram.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tegram.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesDataStore(
	private val dataStore: DataStore<Preferences>
) {
	private companion object {
		val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
		val CURRENT_USER_EMAIL = stringPreferencesKey("current_user_email")
		val CURRENT_USER_NAME = stringPreferencesKey("current_user_name")
		val CURRENT_USER_PROVIDER = stringPreferencesKey("current_user_provider")
		val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
		val AUTH_TOKEN = stringPreferencesKey("auth_token")
		val REMINDER_TIME = stringPreferencesKey("reminder_time")
	}

	val reminderTimeFlow: Flow<String?> = dataStore.data.map { preferences ->
		preferences[REMINDER_TIME]
	}

	suspend fun saveReminderTime(time: String) {
		dataStore.edit { preferences ->
			preferences[REMINDER_TIME] = time
		}
	}

	val currentUserIdFlow: Flow<String?> = dataStore.data.map { preferences ->
		preferences[CURRENT_USER_ID]
	}

	suspend fun saveCurrentUser(user: UserProfile) {
		dataStore.edit { preferences ->
			preferences[CURRENT_USER_ID] = user.uid
			preferences[CURRENT_USER_EMAIL] = user.email
			preferences[CURRENT_USER_NAME] = user.fullName
			preferences[CURRENT_USER_PROVIDER] = user.provider
			preferences[IS_LOGGED_IN] = true
		}
	}

	val authTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
		preferences[AUTH_TOKEN]
	}

	suspend fun saveAuthToken(token: String) {
		dataStore.edit { preferences ->
			preferences[AUTH_TOKEN] = token
		}
	}

	suspend fun clearCurrentUser() {
		dataStore.edit { preferences ->
			preferences.clear()
		}
	}
}
