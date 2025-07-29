// app/src/main/java/com/goaltrackr/data/repository/UserPreferencesRepository.kt
package com.goaltrackr.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// DataStore için Preferences anahtarları
val Context.dataStore by preferencesDataStore(name = "user_preferences")

// Kullanıcı tercihlerini (örneğin tema seçimi, email, token) yöneten Repository.
// DataStore kullanarak verileri kalıcı olarak saklar.
@Singleton
class UserPreferencesRepository @Inject constructor(
    private val context: Context // Context, DataStore'a erişim için gereklidir
) {
    // Tema tercihini saklamak için anahtar
    private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme_enabled")
    // Kullanıcı email'ini saklamak için anahtar
    private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
    // API yetkilendirme token'ını saklamak için anahtar
    private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")

    // Tema tercihini okuma (Flow olarak gerçek zamanlı güncellemeler)
    val isDarkThemeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_THEME_KEY] ?: false // Varsayılan olarak açık tema
        }

    // Tema tercihini kaydetme
    suspend fun setDarkThemeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    // Kullanıcı email'ini okuma
    val userEmail: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_EMAIL_KEY]
        }

    // Kullanıcı email'ini kaydetme
    suspend fun setUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL_KEY] = email
        }
    }

    // Yetkilendirme token'ını okuma
    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[AUTH_TOKEN_KEY]
        }

    // Yetkilendirme token'ını kaydetme
    suspend fun setAuthToken(token: String?) {
        context.dataStore.edit { preferences ->
            if (token != null) {
                preferences[AUTH_TOKEN_KEY] = token
            } else {
                preferences.remove(AUTH_TOKEN_KEY) // Token null ise sil
            }
        }
    }
}
