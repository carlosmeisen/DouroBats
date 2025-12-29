package pt.dourobats.app.features.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pt.dourobats.app.core.domain.model.Language
import pt.dourobats.app.core.domain.repository.SettingsRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeSettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeSettingsRepository()
        viewModel = SettingsViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `currentLanguage initial value is ENGLISH_US`() = runTest(testDispatcher) {
        // StateFlow has immediate value, no need to collect
        assertEquals(Language.ENGLISH_US, viewModel.currentLanguage.value)
    }

    @Test
    fun `setLanguage updates repository`() = runTest(testDispatcher) {
        viewModel.setLanguage(Language.PORTUGUESE_BR)
        advanceUntilIdle()

        val language = repository.getLanguage()
        assertEquals(Language.PORTUGUESE_BR, language)
    }

    @Test
    fun `setLanguage updates currentLanguage flow`() = runTest(testDispatcher) {
        // Start collecting to activate the StateFlow
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }

        viewModel.setLanguage(Language.SPANISH)
        advanceUntilIdle()

        // StateFlow updates when repository updates
        assertEquals(Language.SPANISH, viewModel.currentLanguage.value)
        collectorJob.cancel()
    }

    @Test
    fun `currentLanguage reflects repository changes`() = runTest(testDispatcher) {
        // Start collecting to activate the StateFlow
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }

        repository.setLanguage(Language.ENGLISH_GB)
        advanceUntilIdle()

        assertEquals(Language.ENGLISH_GB, viewModel.currentLanguage.value)
        collectorJob.cancel()
    }

    @Test
    fun `multiple setLanguage calls update correctly`() = runTest(testDispatcher) {
        // Start collecting to activate the StateFlow
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }

        viewModel.setLanguage(Language.PORTUGUESE_BR)
        advanceUntilIdle()
        assertEquals(Language.PORTUGUESE_BR, viewModel.currentLanguage.value)

        viewModel.setLanguage(Language.SPANISH)
        advanceUntilIdle()
        assertEquals(Language.SPANISH, viewModel.currentLanguage.value)

        viewModel.setLanguage(Language.ENGLISH_GB)
        advanceUntilIdle()
        assertEquals(Language.ENGLISH_GB, viewModel.currentLanguage.value)

        collectorJob.cancel()
    }

    @Test
    fun `all languages can be set through viewModel`() = runTest(testDispatcher) {
        // Start collecting to activate the StateFlow
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }

        Language.entries.forEach { language ->
            viewModel.setLanguage(language)
            advanceUntilIdle()

            val current = viewModel.currentLanguage.value
            assertEquals(language, current, "Language $language should be set correctly")
        }

        collectorJob.cancel()
    }

    @Test
    fun `setLanguage with same language still updates`() = runTest(testDispatcher) {
        // Start collecting to activate the StateFlow
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }

        viewModel.setLanguage(Language.PORTUGUESE_PT)
        advanceUntilIdle()
        assertEquals(Language.PORTUGUESE_PT, viewModel.currentLanguage.value)

        // Set the same language again
        viewModel.setLanguage(Language.PORTUGUESE_PT)
        advanceUntilIdle()
        assertEquals(Language.PORTUGUESE_PT, viewModel.currentLanguage.value)

        collectorJob.cancel()
    }
}

/**
 * Fake implementation of SettingsRepository for testing purposes.
 * Uses MutableStateFlow to simulate DataStore behavior.
 */
class FakeSettingsRepository : SettingsRepository {
    private val _languageFlow = MutableStateFlow(Language.ENGLISH_US)

    override val languageFlow: Flow<Language> = _languageFlow

    override suspend fun setLanguage(language: Language) {
        _languageFlow.value = language
    }

    override suspend fun getLanguage(): Language {
        return _languageFlow.value
    }
}
