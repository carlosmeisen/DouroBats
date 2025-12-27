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

### How to Use Convention Plugins in New Modules
When creating a new module, apply the appropriate convention plugins:

```kotlin
// For a KMP library module with Compose
plugins {
    id("pt.dourobats.app.kmp")
    id("pt.dourobats.app.compose")
}
```

```kotlin
// For an Android application module
plugins {
    id("pt.dourobats.app.android.application")
    id("pt.dourobats.app.kmp")
    id("pt.dourobats.app.compose")
}
```

The convention plugins automatically configure:
- Target platforms (Android, iOS)
- JVM target (11)
- Common dependencies for each layer
- SDK versions (from version catalog)
- Build configurations

## Building the Project
```bash
# Build Android app
./gradlew :composeApp:assembleDebug

# Build release APK
./gradlew :composeApp:assembleRelease

# Build specific module
./gradlew :core:domain:build

# Run tests for all modules
./gradlew test

# Run tests for specific module
./gradlew :core:domain:test

# Clean build
./gradlew clean build
```

## Adding New Modules

### Create a new feature module:
1. Create directory: `features/new-feature/`
2. Add `build.gradle.kts`:
```kotlin
plugins {
    id("pt.dourobats.app.android.library")
    id("pt.dourobats.app.kmp")
    id("pt.dourobats.app.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.domain)
            implementation(projects.core.ui)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
        }
    }
}

android {
    namespace = "pt.dourobats.app.features.newfeature"
}
```
3. Add to `settings.gradle.kts`:
```kotlin
include(":features:new-feature")
```
4. Add dependency in `composeApp/build.gradle.kts`:
```kotlin
implementation(projects.features.newFeature)
```

## Multi-Module Architecture

This project follows a **multi-module architecture** for better separation of concerns, scalability, and testability.

### 📊 Architecture Diagrams
View detailed architecture and UI flow diagrams in the `/docs` folder:
- **[Architecture Overview](docs/architecture.md)**: Module structure and dependencies (Mermaid diagrams)
- **[UI Flow](docs/ui-flow.md)**: User navigation and data flow diagrams

### Module Structure
```
DouroBats/
├── composeApp/              # Main Android/iOS application
├── core/
│   ├── domain/              # Domain models, use cases, repository interfaces
│   ├── data/                # Repository implementations
│   ├── network/             # API clients and DTOs
│   └── ui/                  # Shared UI components and theme
├── features/
│   ├── home/                # Home feature module
│   ├── schedule/            # Schedule/Calendar feature
│   └── settings/            # Settings feature
└── build-logic/             # Convention plugins
```

### Module Responsibilities

#### 🎯 composeApp (Application)
- App entry point (Android + iOS)
- App-level navigation
- Dependency injection configuration
- Aggregates all feature modules

#### 🏗️ core:domain (Pure Kotlin)
- Domain models (`TrainingSession`, `User`, `Team`)
- Repository interfaces
- Use cases (business logic)
- **No platform dependencies**

#### 📦 core:data (KMP)
- Repository implementations
- Data source coordination (local/remote)
- Domain model mappers

#### 🌐 core:network (KMP)
- API clients (future: Ktor)
- Network DTOs
- API endpoint definitions

#### 🎨 core:ui (KMP - Compose)
- Material 3 theme (`DouroBatsTheme`)
- Shared composable components
- Design system (colors, typography, spacing)

#### 🏠 features:* (KMP - Compose)
- Feature-specific UI and ViewModels
- Isolated feature logic
- Can be developed independently

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