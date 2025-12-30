package pt.dourobats.app.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pt.dourobats.app.core.domain.model.Language
import pt.dourobats.app.core.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private val languageKey = stringPreferencesKey("language")

    override val languageFlow: Flow<Language> = dataStore.data.map { preferences ->
        val languageCode = preferences[languageKey]
        if (languageCode != null) {
            Language.fromCode(languageCode)
        } else {
            Language.getSystemDefault()
        }
    }

    override suspend fun setLanguage(language: Language) {
        dataStore.edit { preferences ->
            preferences[languageKey] = language.code
        }
    }

    override suspend fun getLanguage(): Language {
        return languageFlow.first()
    }
}
