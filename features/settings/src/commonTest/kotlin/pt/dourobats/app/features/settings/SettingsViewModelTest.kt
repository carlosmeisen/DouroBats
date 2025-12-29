package pt.dourobats.app.features.settings

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import pt.dourobats.app.core.domain.model.Language
import pt.dourobats.app.core.domain.repository.SettingsRepository
import pt.dourobats.app.core.test.fakes.fakeSettingsRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = fakeSettingsRepository {
            initialLanguage = Language.ENGLISH_US
        }
        viewModel = SettingsViewModel(repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `currentLanguage is ENGLISH_US when initialized`() = runTest(testDispatcher) {
        // Arrange (done in setup)
        // Act
        val language = viewModel.currentLanguage.value

        // Assert
        assertEquals(Language.ENGLISH_US, language)
    }

    @Test
    fun `repository is updated when setLanguage called`() = runTest(testDispatcher) {
        // Arrange
        val targetLanguage = Language.PORTUGUESE_BR

        // Act
        viewModel.setLanguage(targetLanguage)
        advanceUntilIdle()

        // Assert
        val language = repository.getLanguage()
        assertEquals(targetLanguage, language)
    }

    @Test
    fun `currentLanguage is updated when setLanguage called`() = runTest(testDispatcher) {
        // Arrange
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }
        val targetLanguage = Language.SPANISH

        // Act
        viewModel.setLanguage(targetLanguage)
        advanceUntilIdle()

        // Assert
        assertEquals(targetLanguage, viewModel.currentLanguage.value)
        collectorJob.cancel()
    }

    @Test
    fun `currentLanguage is updated when repository changes`() = runTest(testDispatcher) {
        // Arrange
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }
        val targetLanguage = Language.ENGLISH_GB

        // Act
        repository.setLanguage(targetLanguage)
        advanceUntilIdle()

        // Assert
        assertEquals(targetLanguage, viewModel.currentLanguage.value)
        collectorJob.cancel()
    }

    @Test
    fun `currentLanguage is correct when multiple setLanguage called`() = runTest(testDispatcher) {
        // Arrange
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }

        // Act & Assert
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
    fun `setLanguage is successful when all languages tested`() = runTest(testDispatcher) {
        // Arrange
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }

        // Act & Assert
        Language.entries.forEach { language ->
            viewModel.setLanguage(language)
            advanceUntilIdle()

            val current = viewModel.currentLanguage.value
            assertEquals(language, current, "Language $language should be set correctly")
        }

        collectorJob.cancel()
    }

    @Test
    fun `currentLanguage is updated when same language set twice`() = runTest(testDispatcher) {
        // Arrange
        val collectorJob = launch {
            viewModel.currentLanguage.collect {}
        }
        val targetLanguage = Language.PORTUGUESE_PT

        // Act
        viewModel.setLanguage(targetLanguage)
        advanceUntilIdle()
        assertEquals(targetLanguage, viewModel.currentLanguage.value)

        viewModel.setLanguage(targetLanguage)
        advanceUntilIdle()

        // Assert
        assertEquals(targetLanguage, viewModel.currentLanguage.value)
        collectorJob.cancel()
    }
}
