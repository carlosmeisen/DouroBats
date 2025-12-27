# DouroBats Architecture

## Multi-Module Structure

```mermaid
graph TD
    App[composeApp<br/>Android + iOS]

    subgraph Features
        Home[features:home]
        Schedule[features:schedule]
        Settings[features:settings]
    end

    subgraph Core
        Domain[core:domain<br/>Models, UseCases, Interfaces]
        Data[core:data<br/>Repositories]
        Network[core:network<br/>API Clients]
        UI[core:ui<br/>Design System]
    end

    App --> Home
    App --> Schedule
    App --> Settings

    Home --> Domain
    Schedule --> Domain
    Settings --> Domain

    Home --> UI
    Schedule --> UI
    Settings --> UI

    Data --> Domain
    Data --> Network
    Network --> Domain

    style App fill:#e1f5ff
    style Domain fill:#fff3e0
    style Data fill:#f3e5f5
    style Network fill:#e8f5e9
    style UI fill:#fce4ec
```

## Module Responsibilities

### 🎯 composeApp (Application Module)
- Android + iOS entry points
- App-level navigation
- Dependency injection setup (Koin modules)
- MainActivity (Android) / iOSApp (iOS)

### 🏗️ core:domain (Pure Kotlin)
- **Domain Models**: `TrainingSession`, `User`, `Team`
- **Use Cases**: Business logic (e.g., `GetTrainingSessionsUseCase`)
- **Repository Interfaces**: Contracts for data layer
- **No Android/iOS dependencies**

### 📦 core:data (KMP)
- **Repository Implementations**: Implements domain interfaces
- **Data Sources**: Local and Remote data sources
- **Mappers**: DTO ↔ Domain model conversion
- Future: Room/SQLDelight for local storage

### 🌐 core:network (KMP)
- **API Clients**: Ktor HTTP client
- **DTOs**: API response models
- **Endpoints**: API endpoint definitions
- Future: Authentication interceptors

### 🎨 core:ui (KMP - Compose)
- **Design System**: Material 3 theme, colors, typography
- **Shared Components**: Buttons, Cards, Loading states
- **Common Composables**: Reusable UI elements

### 🏠 features:home (KMP - Compose)
- Home screen UI
- HomeViewModel
- Home-specific composables

### 📅 features:schedule (KMP - Compose)
- Schedule/Calendar screen
- ScheduleViewModel
- Interactive calendar component

### ⚙️ features:settings (KMP - Compose)
- Settings screen
- SettingsViewModel
- Profile management

---

## MVP Feature Set

```mermaid
mindmap
    root((DouroBats MVP))
        Auth
            Splash Screen
            Login/Logout
            Session Management
        Home
            Welcome View
            Quick Stats
            Recent Activity
        Training Sessions
            Calendar View
            Session List
            Session Details
            Schedule Training
        Settings
            Profile View
            User Details
            Logout
```

### Core Features for MVP

#### Authentication Flow
- Splash screen on app launch
- Login/Logout functionality
- Session persistence and management

#### Home Screen
- Welcome message and dashboard
- Quick stats overview
- Recent training activity

#### Training Sessions
- View all training sessions in calendar format
- Browse session list
- View detailed session information
- Schedule new training sessions

#### Settings
- View user profile and details
- Manage preferences
- Logout functionality
