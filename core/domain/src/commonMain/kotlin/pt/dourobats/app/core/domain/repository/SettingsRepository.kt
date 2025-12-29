package pt.dourobats.app.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.dourobats.app.core.domain.model.Language

/**
 * Repository for managing application settings.
 */
interface SettingsRepository {
    /**
     * Flow of the current language setting.
     */
    val languageFlow: Flow<Language>

    /**
     * Save the selected language.
     */
    suspend fun setLanguage(language: Language)

    /**
     * Get the current language synchronously.
     */
    suspend fun getLanguage(): Language
}
