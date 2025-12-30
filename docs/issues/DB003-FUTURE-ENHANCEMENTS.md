# DB003: User Settings Screen - Future Enhancements

## Overview
This document tracks planned enhancements and TODO items for the User Settings screen implementation. All items listed here are post-MVP features that should be implemented once the corresponding backend/infrastructure is ready.

**Related Issue:** GitHub Issue #3
**Feature Branch:** `feature/DB003-user-settings-screen`
**Status:** Core implementation complete ✓

---

## 🔴 High Priority (Blocked by Backend/Infrastructure)

### 1. Profile Image Picker Integration
**Status:** Placeholder implemented (URL field only)
**Blocked by:** Image picker library integration
**Estimated effort:** 4-6 hours

**Current State:**
- `UserProfile.profileImageUrl` is a nullable String
- UI shows placeholder icon (👤) when no image URL
- TODO comment in ProfileHeader.kt line 52

**Implementation Plan:**
1. Add Peekaboo library (or similar multiplatform image picker)
   ```kotlin
   // Add to version catalog
   peekaboo = "0.5.2"
   peekaboo-ui = { module = "io.github.onseok:peekaboo-ui", version.ref = "peekaboo" }
   peekaboo-image-picker = { module = "io.github.onseok:peekaboo-image-picker", version.ref = "peekaboo" }
   ```

2. Update ProfileHeader.kt
   - Add image picker button/overlay
   - Handle image selection
   - Update ViewModel with selected image URI

3. Add ViewModel method
   ```kotlin
   fun updateProfileImage(imageUri: String) {
       viewModelScope.launch {
           // Upload image to backend
           // Get image URL
           // Update profile with new URL
       }
   }
   ```

4. Handle permissions (Android/iOS)
   - Add permission requests
   - Handle permission denials gracefully

**Files to modify:**
- `features/settings/build.gradle.kts` - Add dependencies
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/components/ProfileHeader.kt`
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsViewModel.kt`

---

### 2. Logout Functionality
**Status:** Placeholder with TODO
**Blocked by:** Authentication system implementation
**Estimated effort:** 2-3 hours

**Current State:**
- Logout button exists in UI (SettingsScreen.kt line 162)
- Empty function with TODO in ViewModel (line 128-135)

**Implementation Plan:**
1. Clear user session/tokens
   ```kotlin
   fun logout() {
       viewModelScope.launch {
           // Clear auth tokens from secure storage
           authRepository.clearSession()

           // Clear user data from DataStore
           settingsRepository.clearUserData()

           // Navigate to login screen
           navigationController.navigateToLogin()
       }
   }
   ```

2. Add navigation to login screen
3. Clear all cached user data
4. Consider showing confirmation dialog

**Files to modify:**
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsViewModel.kt`
- Will need AuthRepository (not yet implemented)
- Will need NavigationController (not yet implemented)

**Dependencies:**
- AuthRepository implementation
- Navigation system
- Secure token storage

---

### 3. Delete Account Functionality
**Status:** Confirmation dialog implemented, no backend call
**Blocked by:** Backend API endpoint
**Estimated effort:** 3-4 hours

**Current State:**
- Delete confirmation dialog exists (SettingsScreen.kt line 226-241)
- Empty function with TODO in ViewModel (line 138-146)

**Implementation Plan:**
1. Implement API call
   ```kotlin
   fun deleteAccount() {
       viewModelScope.launch {
           try {
               // Show loading state
               _uiState.update { it.copy(isDeleting = true) }

               // Call backend API
               val result = userRepository.deleteAccount()

               if (result.isSuccess) {
                   // Clear all local data
                   settingsRepository.clearAllData()
                   authRepository.clearSession()

                   // Navigate to login/welcome screen
                   navigationController.navigateToWelcome()
               } else {
                   // Show error to user
                   _uiState.update { it.copy(
                       deleteError = "Failed to delete account"
                   )}
               }
           } catch (e: Exception) {
               // Handle error
               _uiState.update { it.copy(
                   deleteError = e.message
               )}
           } finally {
               _uiState.update { it.copy(isDeleting = false) }
           }
       }
   }
   ```

2. Add loading state during deletion
3. Add error handling with user-friendly messages
4. Add "Account Deleted" success state

**Files to modify:**
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsViewModel.kt`
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsUiState.kt` - Add loading/error states
- Will need UserRepository with deleteAccount() method

**Dependencies:**
- Backend API endpoint: `DELETE /api/users/me`
- UserRepository implementation
- Navigation system

---

### 4. Email Change Flow
**Status:** Email field is read-only
**Blocked by:** Email verification system
**Estimated effort:** 6-8 hours

**Current State:**
- Email field shows in ProfileEditDialog but is disabled (line 67-72)
- No change functionality

**Implementation Plan:**
1. Add "Change Email" button/flow
2. Create EmailChangeDialog
   - Current email (read-only)
   - New email input
   - Confirmation code input

3. Backend flow:
   - Send verification code to new email
   - User enters code
   - Verify code and update email

4. Add ViewModel methods:
   ```kotlin
   fun requestEmailChange(newEmail: String) {
       viewModelScope.launch {
           // Validate email format
           // Send verification code
           // Show verification dialog
       }
   }

   fun verifyEmailChange(code: String) {
       viewModelScope.launch {
           // Verify code
           // Update email
           // Update UI
       }
   }
   ```

**Files to create:**
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/components/EmailChangeDialog.kt`

**Files to modify:**
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsScreen.kt`
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsViewModel.kt`
- String resources (all 5 languages)

**Dependencies:**
- Backend email verification endpoint
- Email service configured
- Verification code generation/validation

---

## 🟡 Medium Priority (UX Improvements)

### 5. Loading States & Shimmer Effects
**Status:** Not implemented
**Effort:** 2-3 hours

**What to add:**
1. Loading shimmer while fetching user profile
2. Loading state during profile save
3. Loading state during theme/language change (if needed)

**Implementation:**
```kotlin
// In SettingsUiState
data class SettingsUiState(
    val userProfile: UserProfile = UserProfile.empty(),
    val currentLanguage: Language = Language.ENGLISH_US,
    val currentTheme: Theme = Theme.SYSTEM,
    val validationErrors: ValidationErrors = ValidationErrors(),
    val isLoading: Boolean = false,  // NEW
    val isSaving: Boolean = false     // NEW
)
```

**Files to modify:**
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsUiState.kt`
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsScreen.kt` - Add shimmer placeholders
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsViewModel.kt`

---

### 6. Password Change / Security Settings
**Status:** Not implemented
**Blocked by:** Authentication system
**Effort:** 4-6 hours

**Implementation Plan:**
1. Add new section in Settings: "Security"
2. Add "Change Password" option
3. Create PasswordChangeDialog
   - Current password
   - New password
   - Confirm new password
4. Add password strength indicator
5. Add "Two-Factor Authentication" option (future)

**Files to create:**
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/components/PasswordChangeDialog.kt`

**Files to modify:**
- `features/settings/src/commonMain/kotlin/pt/dourobats/app/features/settings/SettingsScreen.kt`
- String resources

---

### 7. Profile Image Upload to Backend
**Status:** Not implemented
**Blocked by:** File upload API & storage solution
**Effort:** 4-5 hours

**Implementation Plan:**
1. Compress/resize image before upload (especially for mobile)
2. Upload to cloud storage (S3, Firebase Storage, etc.)
3. Get back URL from backend
4. Update user profile with new URL

```kotlin
suspend fun uploadProfileImage(imageUri: String): Result<String> {
    // Compress image
    val compressedImage = imageCompressor.compress(imageUri)

    // Upload to backend
    val response = apiClient.uploadFile(
        endpoint = "/api/users/me/avatar",
        file = compressedImage
    )

    // Return URL
    return response.imageUrl
}
```

**Dependencies:**
- Backend file upload endpoint
- Cloud storage service (AWS S3, Google Cloud Storage, etc.)
- Image compression library
- Multipart upload handling

---

### 8. Offline Support & Conflict Resolution
**Status:** Partial (uses DataStore but no sync)
**Effort:** 6-8 hours

**Current Behavior:**
- Changes are saved locally to DataStore
- No sync with backend

**Improvement Plan:**
1. Queue changes when offline
2. Sync when connection restored
3. Handle conflicts (server-side changes vs local changes)
4. Show sync status indicator

**Implementation:**
```kotlin
sealed class SyncStatus {
    object Synced : SyncStatus()
    object Syncing : SyncStatus()
    object Offline : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}
```

---

## 🟢 Low Priority (Nice to Have)

### 9. Profile Completeness Indicator
**Effort:** 1-2 hours

Show user how complete their profile is:
- Display name: 25%
- Email: 25%
- Phone number: 25%
- Profile image: 25%

Add visual progress bar in ProfileHeader.

---

### 10. Activity Log
**Effort:** 4-5 hours

Show user their recent account activities:
- Profile changes
- Login history
- Settings changes

**New screen:** SettingsActivityScreen.kt

---

### 11. Export User Data (GDPR Compliance)
**Effort:** 3-4 hours

Add "Download My Data" option:
- Generate JSON/PDF with all user data
- Include profile, settings, training data, etc.
- Follow GDPR requirements

---

### 12. Account Deactivation (vs Deletion)
**Effort:** 2-3 hours

Add option to temporarily deactivate account instead of deleting:
- Hide from public searches
- Preserve data
- Can be reactivated later

---

## 📋 Code Quality Improvements

### 13. Replace Deprecated Divider with HorizontalDivider
**Status:** Deprecation warnings present
**Effort:** 15 minutes

**Current warnings:**
```
SettingsScreen.kt:77 - 'Divider' is deprecated. Renamed to HorizontalDivider.
SettingsScreen.kt:89 - 'Divider' is deprecated. Renamed to HorizontalDivider.
SettingsScreen.kt:125 - 'Divider' is deprecated. Renamed to HorizontalDivider.
SettingsScreen.kt:152 - 'Divider' is deprecated. Renamed to HorizontalDivider.
```

**Fix:**
```kotlin
// Change all instances
Divider() → HorizontalDivider()
```

---

### 14. Improve Phone Number Validation
**Current:** Simple digit count validation (9-15 digits)
**Improvement:** Use libphonenumber library for proper international validation

**Implementation:**
```kotlin
dependencies {
    implementation("io.michaelrocks:libphonenumber-android:8.13.26")
}

private fun isValidPhoneNumber(phone: String): Boolean {
    return try {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val number = phoneUtil.parse(phone, "US") // Or get region from user
        phoneUtil.isValidNumber(number)
    } catch (e: Exception) {
        false
    }
}
```

---

### 15. Add Accessibility Labels
**Effort:** 1 hour

Add proper content descriptions for screen readers:
- Profile image
- Edit buttons
- Section headers
- Form fields

---

## 🧪 Testing Improvements

### 16. UI Tests for ProfileEditDialog
**Effort:** 2-3 hours

Current: Only ViewModel tests exist
Add: Compose UI tests

```kotlin
@Test
fun `typing in display name updates field`() {
    composeTestRule.setContent {
        ProfileEditDialog(...)
    }

    composeTestRule
        .onNodeWithText("Display Name")
        .performTextInput("John Doe")

    composeTestRule
        .onNodeWithText("John Doe")
        .assertExists()
}
```

---

### 17. Screenshot Tests
**Effort:** 2-3 hours

Add screenshot tests for:
- Settings screen in all 5 languages
- Light and dark themes
- Different screen sizes

Use Paparazzi or similar library.

---

## 📱 Platform-Specific Enhancements

### 18. iOS Haptic Feedback
Add haptic feedback for:
- Button taps
- Success/error states
- Validation errors

### 19. Android Biometric Authentication for Sensitive Actions
Add fingerprint/face auth before:
- Deleting account
- Changing email
- Logging out

---

## 🎨 Design Enhancements

### 20. Animations
**Effort:** 2-3 hours

Add smooth animations for:
- Dialog appearances
- Theme switching
- Section expansion
- Success states

### 21. Custom Error Messages with Icons
Replace plain text errors with Material icons + text

---

## 📊 Analytics & Monitoring

### 22. Track User Settings Changes
**Effort:** 1-2 hours

Send analytics events for:
- Language changes
- Theme changes
- Profile updates
- Account deletion attempts

Useful for understanding user preferences.

---

## Implementation Priority Order

### Phase 1 (When Auth System Ready)
1. Logout functionality
2. Delete account functionality

### Phase 2 (When Backend Ready)
3. Profile image picker
4. Email change flow
5. Profile image upload

### Phase 3 (UX Polish)
6. Loading states & shimmer
7. Password change
8. Replace deprecated Divider

### Phase 4 (Enhanced Features)
9. Offline support
10. Phone number validation improvements
11. Profile completeness indicator

### Phase 5 (Compliance & Testing)
12. Export user data (GDPR)
13. UI tests
14. Screenshot tests
15. Accessibility improvements

---

## Notes

### Validation Improvements Needed
Current implementation uses hardcoded error messages. Future improvements:
- Use string resources for validation errors (already added to XML)
- Add real-time validation (as user types)
- Show validation hints before errors

### String Resources
All necessary string resources have been added for the 5 supported languages:
- English US (en-US)
- English GB (en-GB)
- Portuguese Brazil (pt-BR)
- Portuguese Portugal (pt-PT)
- Spanish Spain (es-ES)

Any new features should follow the same pattern and add strings to all language files.

### Architecture Notes
The current implementation follows Clean Architecture:
- **Domain Layer:** UserProfile, Theme models
- **Data Layer:** SettingsRepository with DataStore
- **Presentation Layer:** ViewModel with state management
- **UI Layer:** Compose components

Maintain this pattern for all future enhancements.

---

## Dependencies to Add (When Implementing)

```kotlin
// build-logic/convention/src/main/kotlin/LibraryVersions.kt or version catalog

// Image Picker
peekaboo = "0.5.2"

// Phone Number Validation
libphonenumber = "8.13.26"

// Image Compression
coil = "3.0.0"  // or similar

// File Upload
ktor-client-multipart = "2.3.7"

// Analytics (if not already added)
firebase-analytics = "..."
```

---

**Last Updated:** 2025-12-30
**Document Owner:** Development Team
**Related Docs:**
- `/docs/DB003-implementation-plan.md` (if exists)
- `/docs/architecture.md` (if exists)
