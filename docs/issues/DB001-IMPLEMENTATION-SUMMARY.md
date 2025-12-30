# Dynamic Language Switching Implementation

## Overview

This document describes the implementation of runtime language switching in the DouroBats Compose Multiplatform application. The feature allows users to change the app's display language without requiring an app restart.

## Supported Languages

- 🇺🇸 English (US) - `en-US`
- 🇬🇧 English (UK) - `en-GB`
- 🇧🇷 Português (Brasil) - `pt-BR`
- 🇵🇹 Português (Portugal) - `pt-PT`
- 🇪🇸 Español - `es-ES`

## Architecture

### Components

```
┌─────────────────────────────────────────────────────────────┐
│                         User Interface                       │
│  ┌────────────────┐              ┌──────────────────────┐   │
│  │ SettingsScreen │──selects────▶│ LanguageSelectionDialog│  │
│  └────────────────┘              └──────────────────────┘   │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      ViewModel Layer                         │
│              ┌────────────────────────┐                      │
│              │   SettingsViewModel    │                      │
│              │  - currentLanguage     │                      │
│              │  - setLanguage()       │                      │
│              └───────────┬────────────┘                      │
└──────────────────────────┼──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     Repository Layer                         │
│              ┌────────────────────────┐                      │
│              │  SettingsRepository    │                      │
│              │  - languageFlow        │                      │
│              │  - setLanguage()       │                      │
│              │  - getLanguage()       │                      │
│              └───────────┬────────────┘                      │
└──────────────────────────┼──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Persistence Layer                         │
│              ┌────────────────────────┐                      │
│              │   DataStore (Okio)     │                      │
│              │  Key: "language"       │                      │
│              │  Value: Language.code  │                      │
│              └────────────────────────┘                      │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  Localization Layer                          │
│  ┌──────────────────┐        ┌────────────────────────┐     │
│  │ App.kt           │        │ changeLanguage()       │     │
│  │ - Loads language │───────▶│ (expect/actual)        │     │
│  │ - Provides via   │        │                        │     │
│  │   CompositionLocal│        │ Android: Locale.setDefault()│
│  └──────────────────┘        │ iOS: NSUserDefaults    │     │
│                              └────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

### Directory Structure

```
core/
├── domain/
│   └── src/commonMain/kotlin/.../domain/
│       ├── model/
│       │   └── Language.kt              # Language enum with BCP 47 tags
│       └── repository/
│           └── SettingsRepository.kt    # Repository interface
│
├── data/
│   └── src/
│       ├── commonMain/kotlin/.../data/
│       │   ├── repository/
│       │   │   └── SettingsRepositoryImpl.kt  # DataStore implementation
│       │   ├── preferences/
│       │   │   └── DataStoreFactory.kt        # DataStore factory (expect)
│       │   └── di/
│       │       └── DataModule.kt              # Koin dependency injection
│       ├── androidMain/kotlin/.../data/
│       │   └── preferences/
│       │       └── DataStoreFactory.android.kt
│       └── iosMain/kotlin/.../data/
│           └── preferences/
│               └── DataStoreFactory.ios.kt
│
└── ui/
    └── src/
        ├── commonMain/kotlin/.../ui/localization/
        │   └── LanguageChanger.kt       # LocalLanguage + changeLanguage()
        ├── androidMain/kotlin/.../ui/localization/
        │   └── LanguageChanger.android.kt
        └── iosMain/kotlin/.../ui/localization/
            └── LanguageChanger.ios.kt

features/
└── settings/
    └── src/commonMain/kotlin/.../settings/
        ├── SettingsScreen.kt            # Settings UI
        ├── SettingsViewModel.kt         # Settings business logic
        ├── LanguageSelectionDialog.kt   # Language picker dialog
        └── di/
            └── SettingsModule.kt        # Koin module for settings

composeApp/
└── src/
    ├── commonMain/kotlin/.../app/
    │   └── App.kt                       # Main app with language provider
    └── androidMain/kotlin/.../app/
        └── DouroBatsApplication.kt      # Android app initialization
```

## Implementation Details

### 1. Language Model

**Location:** `core/domain/src/commonMain/kotlin/.../Language.kt`

```kotlin
enum class Language(
    val code: String,              // For persistence (BCP 47)
    val resourceQualifier: String, // Android resource format (e.g., "en-rGB")
    val bcp47Tag: String,          // For Locale API (e.g., "en-GB")
    val displayName: String        // User-facing name
) {
    ENGLISH_US("en", "en", "en-US", "English (US)"),
    ENGLISH_GB("en-GB", "en-rGB", "en-GB", "English (UK)"),
    PORTUGUESE_BR("pt-BR", "pt-rBR", "pt-BR", "Português (Brasil)"),
    PORTUGUESE_PT("pt-PT", "pt-rPT", "pt-PT", "Português (Portugal)"),
    SPANISH("es", "es-rES", "es-ES", "Español")
}
```

**Why three different formats?**
- `code`: Saved to DataStore (BCP 47 standard)
- `resourceQualifier`: Matches Android resource folder names (`values-pt-rBR`)
- `bcp47Tag`: Used with `Locale.forLanguageTag()` API

### 2. Persistence with DataStore

**Location:** `core/data/src/commonMain/kotlin/.../SettingsRepositoryImpl.kt`

```kotlin
class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private val languageKey = stringPreferencesKey("language")

    override val languageFlow: Flow<Language> = dataStore.data.map { preferences ->
        val code = preferences[languageKey]
        if (code != null) Language.fromCode(code) else Language.getSystemDefault()
    }

    override suspend fun setLanguage(language: Language) {
        dataStore.edit { preferences ->
            preferences[languageKey] = language.code
        }
    }
}
```

**DataStore Path Configuration:**
- **Android:** `context.filesDir.resolve("datastore/settings.preferences_pb")`
- **iOS:** `NSDocumentDirectory/settings.preferences_pb` (via okio.Path)

### 3. Platform-Specific Locale Change

**Common Declaration:**
```kotlin
// core/ui/src/commonMain/.../LanguageChanger.kt
expect fun changeLanguage(language: Language)
```

**Android Implementation:**
```kotlin
// core/ui/src/androidMain/.../LanguageChanger.android.kt
actual fun changeLanguage(language: Language) {
    val locale = Locale.forLanguageTag(language.bcp47Tag)
    Locale.setDefault(locale)
}
```

**iOS Implementation:**
```kotlin
// core/ui/src/iosMain/.../LanguageChanger.ios.kt
actual fun changeLanguage(language: Language) {
    NSUserDefaults.standardUserDefaults.setObject(
        listOf(language.bcp47Tag),
        forKey = "AppleLanguages"
    )
    NSUserDefaults.standardUserDefaults.synchronize()
}
```

### 4. CompositionLocal Pattern

**Location:** `composeApp/src/commonMain/.../App.kt`

```kotlin
@Composable
private fun AppContent() {
    val settingsRepository: SettingsRepository = koinInject()

    // Load saved language from DataStore
    val savedLanguage by settingsRepository.languageFlow.collectAsState(
        initial = Language.ENGLISH_US
    )

    // Track language state for triggering recomposition
    var currentLanguage by remember { mutableStateOf(Language.ENGLISH_US) }

    // Update when saved language changes
    LaunchedEffect(savedLanguage) {
        if (currentLanguage != savedLanguage) {
            currentLanguage = savedLanguage
            changeLanguage(savedLanguage)  // Update system locale
        }
    }

    // Initialize on first composition
    LaunchedEffect(Unit) {
        changeLanguage(savedLanguage)
    }

    AppTheme {
        // Provide language via CompositionLocal - triggers recomposition
        CompositionLocalProvider(LocalLanguage provides currentLanguage) {
            // App content
        }
    }
}
```

### 5. User Interface

**Language Selection Dialog:**
```kotlin
// features/settings/.../LanguageSelectionDialog.kt
@Composable
fun LanguageSelectionDialog(
    currentLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = { Text("Select Language") },
        text = {
            LazyColumn {
                items(Language.entries) { language ->
                    LanguageItem(
                        language = language,
                        isSelected = language == currentLanguage,
                        onClick = { onLanguageSelected(language) }
                    )
                }
            }
        }
    )
}
```

## How It Works - Step by Step

### Initial Load

1. **App starts** → `DouroBatsApplication` initializes Koin
2. **App.kt renders** → Injects `SettingsRepository`
3. **DataStore loads** → Reads saved language preference
4. **`changeLanguage()` called** → Sets `Locale.setDefault()`
5. **CompositionLocal provides** → `LocalLanguage` available to all composables
6. **UI renders** → `stringResource()` uses the configured locale

### Language Change Flow

```
User Clicks Language
        ↓
LanguageSelectionDialog
        ↓
viewModel.setLanguage(language)
        ↓
SettingsRepository.setLanguage()
        ↓
DataStore saves language.code
        ↓
languageFlow emits new value
        ↓
collectAsState updates savedLanguage
        ↓
LaunchedEffect detects change
        ↓
changeLanguage(language) called
        ↓
Locale.setDefault(locale)    [Platform-specific]
        ↓
currentLanguage state updated
        ↓
CompositionLocalProvider recomposes
        ↓
All composables recompose
        ↓
stringResource() picks new locale
        ↓
UI updates with new language ✨
```

## Resource Organization

### Folder Structure

```
features/settings/src/commonMain/composeResources/
├── values/                    # Default (English US)
│   └── strings.xml
├── values-en-rGB/            # English UK
│   └── strings.xml
├── values-pt-rBR/            # Portuguese Brazil
│   └── strings.xml
├── values-pt-rPT/            # Portuguese Portugal
│   └── strings.xml
└── values-es-rES/            # Spanish
    └── strings.xml
```

### String Resource Example

```xml
<!-- values/strings.xml -->
<resources>
    <string name="settings_title">Settings</string>
    <string name="settings_language">Language</string>
</resources>

<!-- values-pt-rBR/strings.xml -->
<resources>
    <string name="settings_title">Configurações</string>
    <string name="settings_language">Idioma</string>
</resources>
```

### Usage in Code

```kotlin
Text(text = stringResource(Res.string.settings_title))
```

The `stringResource()` function automatically picks the correct string based on `Locale.getDefault()`.

## Dependency Injection (Koin)

### Data Module

```kotlin
val dataModule = module {
    single { createDataStore() }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}
```

### Settings Module

```kotlin
val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}
```

### Application Setup (Android)

```kotlin
class DouroBatsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initDataStore(this)
        startKoin {
            androidContext(this@DouroBatsApplication)
            modules(dataModule, settingsModule)
        }
    }
}
```

## Key Design Decisions

### Why `Locale.setDefault()` instead of Context Configuration?

**Attempted Approach:**
- Initially tried wrapping Android Context with `createConfigurationContext()`
- Problem: Compose Multiplatform resources don't use Android's Context locale

**Current Approach:**
- Uses `Locale.setDefault()` which updates the JVM's default locale
- Compose Multiplatform's `stringResource()` respects `Locale.getDefault()`
- Simpler, cross-platform, no app restart needed

### Why CompositionLocal for Language State?

- Triggers recomposition when language changes
- All composables reading `stringResource()` re-render
- Ensures UI consistency across the entire app

### Why Three Language Tag Formats?

- `code` (BCP 47): Standard format for persistence and APIs
- `resourceQualifier`: Matches Compose Multiplatform resource folder naming
- `bcp47Tag`: Required by `Locale.forLanguageTag()` for proper locale parsing

## Testing

### Manual Testing Checklist

- [ ] Open app → Should load saved language preference
- [ ] Select new language → UI updates immediately
- [ ] Navigate between screens → All text in new language
- [ ] Close and reopen app → Language persists
- [ ] Test all 5 supported languages
- [ ] Verify strings in different modules (core.ui, features.settings, etc.)

### Edge Cases Handled

1. **No saved preference:** Falls back to `Language.ENGLISH_US`
2. **Invalid language code:** `Language.fromCode()` returns `ENGLISH_US`
3. **First app launch:** Initializes with system default (currently hardcoded to EN-US)

## Future Improvements

### Potential Enhancements

1. **Auto-detect system language:**
   ```kotlin
   fun getSystemDefault(): Language {
       val systemLocale = Locale.getDefault()
       return entries.find { it.bcp47Tag == systemLocale.toLanguageTag() }
           ?: ENGLISH_US
   }
   ```

2. **RTL language support:** Add Arabic, Hebrew with proper layout direction

3. **Plurals and formatting:** Handle quantity strings, dates, currencies

4. **Translation coverage:** Add missing strings for new languages

5. **iOS proper implementation:** Currently iOS `changeLanguage()` updates preferences but may require app restart

## Troubleshooting

### Language not changing?

1. Check DataStore is properly initialized
2. Verify `changeLanguage()` is being called
3. Check `Locale.getDefault()` in logs
4. Ensure resource files exist for the language

### Build errors on iOS?

- Verify `okio.Path.Companion.toPath` import (not `kotlin.io.path.Path`)
- Check iOS DataStore path configuration

### Strings showing in wrong language?

- Verify resource folder names match `resourceQualifier` format
- Check `stringResource()` is used (not hardcoded strings)
- Ensure `LocalLanguage` is provided via `CompositionLocalProvider`

## References

### Documentation
- [Compose Multiplatform Resources](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources.html)
- [DataStore Multiplatform](https://developer.android.com/kotlin/multiplatform/datastore)
- [Koin Dependency Injection](https://insert-koin.io/)

### Implementation Based On
- Article: "Language Switching in Compose Multiplatform"
- Approach: `Locale.setDefault()` + CompositionLocal recomposition trigger

---

**Last Updated:** December 29, 2025
**Feature Status:** ✅ Production Ready (Android) | ⚠️  Needs Testing (iOS)
