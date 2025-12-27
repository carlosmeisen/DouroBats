# Data Flow Architecture

## Clean Architecture Pattern

This diagram illustrates how data flows through the application layers following Clean Architecture principles.

```mermaid
flowchart LR
    subgraph Presentation
        UI[Compose UI]
        VM[ViewModel]
    end

    subgraph Domain
        UC[Use Case]
        Repo[Repository Interface]
    end

    subgraph Data
        RepoImpl[Repository Impl]
        Remote[Remote Data Source]
        Local[Local Data Source]
    end

    UI -->|User Action| VM
    VM -->|Invoke| UC
    UC -->|Call| Repo
    Repo -.->|Implements| RepoImpl
    RepoImpl --> Remote
    RepoImpl --> Local

    Local -.->|Cache| RepoImpl
    Remote -.->|API Data| RepoImpl
    RepoImpl -.->|Domain Model| UC
    UC -.->|Result| VM
    VM -.->|State| UI

    style UI fill:#e3f2fd
    style VM fill:#bbdefb
    style UC fill:#fff3e0
    style Repo fill:#fff3e0
    style RepoImpl fill:#f3e5f5
    style Remote fill:#e8f5e9
    style Local fill:#fce4ec
```

## Layer Responsibilities

### Presentation Layer
- **Compose UI**: Displays data and captures user interactions
- **ViewModel**: Manages UI state and handles user actions

### Domain Layer
- **Use Case**: Contains business logic (e.g., GetTrainingSessionsUseCase)
- **Repository Interface**: Defines contracts for data operations
- Pure Kotlin, no platform dependencies

### Data Layer
- **Repository Implementation**: Implements domain repository interfaces
- **Remote Data Source**: Fetches data from API (Ktor)
- **Local Data Source**: Caches data locally (Room/SQLDelight)
- Handles data mapping between DTOs and domain models

## Data Flow

### Request Flow (→)
1. User interacts with UI
2. UI triggers ViewModel action
3. ViewModel invokes Use Case
4. Use Case calls Repository Interface
5. Repository Implementation fetches from Remote/Local sources

### Response Flow (-.→)
1. Data sources return DTOs
2. Repository maps DTOs to Domain Models
3. Use Case processes and returns Result
4. ViewModel updates UI State
5. UI recomposes with new state
