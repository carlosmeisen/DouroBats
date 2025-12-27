# DouroBats MVP - Kotlin Multiplatform

## Project Overview
A professional volleyball management app for the DouroBats association.
Built using **Compose Multiplatform (Android/iOS)** following **SOLID** principles and **Clean Architecture**.

## Tech Stack (Stable 2025)
- **UI:** Compose Multiplatform 1.9.3 (Material 3)
- **Architecture:** MVVM + Clean Architecture
- **DI:** Koin 4.1.0-Beta1 (Multiplatform)
- **Navigation:** Navigation Compose 2.8.5 (androidx.navigation)
- **State:** Kotlin Flows & StateFlow
- **Test Framework:** JUnit 4.13.2 (Android/Common) + Kotlin Coroutines Test 1.10.2
- **Kotlin:** 2.3.0
- **Serialization:** kotlinx-serialization 1.8.0
- **DateTime:** kotlinx-datetime 0.6.1

## Dependency Management
This project uses **Gradle Version Catalogs** (TOML) for centralized dependency management.

### Version Catalog Location
All dependencies are defined in `gradle/libs.versions.toml`:
- **[versions]**: Version numbers for all libraries and plugins
- **[libraries]**: Library dependencies with their coordinates
- **[plugins]**: Gradle plugins

### Adding New Dependencies
1. Add the version in `[versions]` section:
   ```toml
   myLibrary = "1.0.0"
   ```
2. Add the library in `[libraries]` section:
   ```toml
   my-library = { module = "com.example:library", version.ref = "myLibrary" }
   ```
3. Use in your module's `build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation(libs.my.library)
   }
   ```

### Current Dependencies
| Category | Libraries |
|----------|-----------|
| **Core** | Kotlin 2.3.0, Coroutines 1.10.2 |
| **UI** | Compose Multiplatform 1.9.3, Material 3 |
| **Navigation** | Navigation Compose 2.8.5 |
| **DI** | Koin 4.1.0-Beta1 (Core, Compose, ViewModel) |
| **AndroidX** | Lifecycle 2.9.6, Activity 1.12.2, Core 1.17.0 |
| **Serialization** | kotlinx-serialization 1.8.0 |
| **DateTime** | kotlinx-datetime 0.6.1 |
| **Testing** | JUnit 4.13.2, Kotlin Test, Coroutines Test |

## Build Configuration
This project uses **convention plugins** for centralized build logic.

### Build-Logic Structure
```
build-logic/
├── settings.gradle.kts
└── convention/
    ├── build.gradle.kts
    └── src/main/kotlin/
        ├── KotlinMultiplatformConventionPlugin.kt
        ├── AndroidApplicationConventionPlugin.kt
        └── ComposeMultiplatformConventionPlugin.kt
```

### Convention Plugins
- **pt.dourobats.app.kmp**: Kotlin Multiplatform configuration (Android + iOS targets, JVM 11)
- **pt.dourobats.app.android.application**: Android application configuration (SDK versions, packaging)
- **pt.dourobats.app.compose**: Compose Multiplatform setup with all UI dependencies

### Benefits
✅ Centralized configuration across modules
✅ Consistent build settings
✅ Easier maintenance and updates
✅ Reduced boilerplate in module build files

## Project Structure (shared/commonMain)
- `pt.dourobats.app.core.domain`: Interfaces, Models, and UseCases.
- `pt.dourobats.app.core.data`: Repository implementations and Data Sources.
- `pt.dourobats.app.features`: Feature-based UI (Home, Schedule, Settings).

## Development Rules
1. **SOLID Principles:** Use Interfaces for all Repositories and Services to allow easy swapping from Local to Remote API later.
2. **Design Patterns:** Use the Repository Pattern and Factory pattern for Fakes in tests.
3. **UI:** Use Material 3 Adaptive for responsive layouts (Phone/Tablet).
4. **Testing:** - Use the **AAA (Arrange-Act-Assert)** pattern.
    - **No Mocking Frameworks:** Use "Fake" implementation builders as defined in the examples.
    - **Naming Convention:** `fun <method> is <result> when <condition>` (using backticks).

## MVP Feature Scopes
1. **Auth Gate:** Splash screen determines if user reaches `LoginScreen` or `HomeScreen`.
2. **Home:** High-level welcome view.
3. **Schedule:** Interactive Calendar using a shared Domain Model `TrainingSession`.
4. **Settings:** Profile viewing and local session clearing (Logout).

## Reference Example for Tests (Fakes)
Tests must follow the pattern of creating a `FakeBuilder` to generate an anonymous implementation of the interface.

```kotlin
// Example Factory Pattern for Tests
fun fakeTrainingRepository(builder: FakeTrainingRepositoryBuilder.() -> Unit): TrainingRepository {
    return FakeTrainingRepositoryBuilder().apply(builder).build()
}