package pt.dourobats.app.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.dourobats.app.core.domain.model.Language
import pt.dourobats.app.core.domain.model.Theme
import pt.dourobats.app.core.domain.model.UserProfile
import pt.dourobats.app.core.domain.repository.SettingsRepository

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Edit state for profile fields
    private val _editState = MutableStateFlow(ProfileEditState())
    val editState: StateFlow<ProfileEditState> = _editState

    // Combine all flows into single UI state
    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.userProfileFlow,
        settingsRepository.languageFlow,
        settingsRepository.themeFlow
    ) { profile, language, theme ->
        SettingsUiState(
            userProfile = profile,
            currentLanguage = language,
            currentTheme = theme,
            isLoading = false,
            validationErrors = SettingsUiState.ValidationErrors()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    // Separate StateFlow for backward compatibility
    val currentLanguage: StateFlow<Language> = settingsRepository.languageFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Language.ENGLISH_US
        )

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
        }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun enterEditMode() {
        val currentProfile = uiState.value.userProfile
        _editState.value = ProfileEditState(
            displayName = currentProfile.displayName,
            phoneNumber = currentProfile.phoneNumber,
            profileImageUrl = currentProfile.profileImageUrl
        )
    }

    fun updateDisplayName(displayName: String) {
        _editState.value = _editState.value.copy(displayName = displayName)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        _editState.value = _editState.value.copy(phoneNumber = phoneNumber)
    }

    fun saveProfile() {
        val editState = _editState.value
        val errors = validateProfile(editState)

        if (errors.hasErrors) {
            // Validation failed - errors are already in state
            return
        }

        viewModelScope.launch {
            val currentProfile = uiState.value.userProfile
            val updatedProfile = currentProfile.copy(
                displayName = editState.displayName.trim(),
                phoneNumber = editState.phoneNumber.trim()
            )
            settingsRepository.updateUserProfile(updatedProfile)
        }
    }

    fun cancelEdit() {
        _editState.value = ProfileEditState()
    }

    private fun validateProfile(editState: ProfileEditState): SettingsUiState.ValidationErrors {
        val displayNameError = when {
            editState.displayName.isBlank() -> "Display name cannot be empty"
            editState.displayName.length < 2 -> "Display name must be at least 2 characters"
            else -> null
        }

        val phoneNumberError = when {
            editState.phoneNumber.isBlank() -> "Phone number cannot be empty"
            !isValidPhoneNumber(editState.phoneNumber) -> "Invalid phone number format"
            else -> null
        }

        return SettingsUiState.ValidationErrors(
            displayName = displayNameError,
            phoneNumber = phoneNumberError
        )
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        // Basic validation - checks if there are 9-15 digits
        val digitsOnly = phone.replace(Regex("[^0-9]"), "")
        return digitsOnly.length in 9..15
    }

    fun logout() {
        // TODO: Implement logout logic when auth system ready
        // - Clear user session
        // - Navigate to login screen
        // - Clear cached data
        viewModelScope.launch {
            // Placeholder - no action yet
        }
    }

    fun deleteAccount() {
        // TODO: Implement account deletion when backend ready
        // - Show confirmation dialog (implemented in UI)
        // - Call API to delete account
        // - Clear all local data
        // - Navigate to login screen
        viewModelScope.launch {
            // Placeholder - no action yet
        }
    }
}
