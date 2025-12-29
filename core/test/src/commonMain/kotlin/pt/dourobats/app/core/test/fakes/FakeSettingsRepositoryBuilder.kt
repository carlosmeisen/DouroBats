package pt.dourobats.app.core.test.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.dourobats.app.core.domain.model.Language
import pt.dourobats.app.core.domain.repository.SettingsRepository

/**
 * Builder for creating fake SettingsRepository instances in tests.
 * Follows the Factory Pattern as defined in project README.
 *
 * Usage:
 * ```
 * val repository = fakeSettingsRepository {
 *     initialLanguage = Language.PORTUGUESE_BR
 * }
 * ```
 */
class FakeSettingsRepositoryBuilder {
    /**
     * The initial language for the repository.
     * Defaults to ENGLISH_US if not specified.
     */
    var initialLanguage: Language = Language.ENGLISH_US

    /**
     * Builds the fake repository instance.
     */
    fun build(): SettingsRepository {
        return FakeSettingsRepositoryImpl(initialLanguage)
    }

    /**
     * Internal implementation of the fake repository.
     */
    private class FakeSettingsRepositoryImpl(
        initialLanguage: Language
    ) : SettingsRepository {
        private val _languageFlow = MutableStateFlow(initialLanguage)

        override val languageFlow: Flow<Language> = _languageFlow

        override suspend fun setLanguage(language: Language) {
            _languageFlow.value = language
        }

        override suspend fun getLanguage(): Language {
            return _languageFlow.value
        }
    }
}

/**
 * Factory function for creating fake SettingsRepository instances.
 * Uses the builder pattern for flexible test configuration.
 *
 * @param builder Lambda with receiver for configuring the repository
 * @return Configured fake SettingsRepository instance
 *
 * Example:
 * ```
 * val repository = fakeSettingsRepository {
 *     initialLanguage = Language.SPANISH
 * }
 * ```
 */
fun fakeSettingsRepository(
    builder: FakeSettingsRepositoryBuilder.() -> Unit = {}
): SettingsRepository {
    return FakeSettingsRepositoryBuilder().apply(builder).build()
}
