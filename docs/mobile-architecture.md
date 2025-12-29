# DouroBats Mobile Architecture

## Overview

This document defines the architecture for the DouroBats mobile application (Kotlin Multiplatform). The app follows **Clean Architecture** principles with a clear separation between domain, data, and presentation layers.

## Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Compose UI, ViewModels, Navigation)   │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          Domain Layer                   │
│  (Entities, Use Cases, Repositories*)   │
│         *interfaces only                │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           Data Layer                    │
│  (Repository Impl, API, Local Storage)  │
└─────────────────────────────────────────┘
```

### 1. Presentation Layer (UI)

**Responsibilities:**
- Display data to users
- Collect user input
- Navigate between screens
- Show/hide features based on user roles

**Technologies:**
- Jetpack Compose (Android) / Compose Multiplatform
- ViewModels for state management
- Navigation Component

**Example:**
```kotlin
@Composable
fun TrainingScheduleScreen(
    viewModel: TrainingScheduleViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn {
        items(uiState.sessions) { session ->
            SessionCard(
                session = session,
                onConfirmClick = { viewModel.confirmAttendance(session.id) }
            )
        }
    }
}
```

### 2. Domain Layer (Business Logic)

**Responsibilities:**
- Define domain entities
- Define repository interfaces (contracts)
- Implement use cases
- No dependencies on UI or data implementation

**Technologies:**
- Pure Kotlin (multiplatform)
- No Android/iOS dependencies

**Example:**
```kotlin
// Domain entity
data class TrainingSession(
    val id: UUID,
    val sportId: UUID,
    val venueId: UUID,
    val date: Instant,
    val targetLevel: SessionLevel,
    val capacity: Int,
    val status: SessionStatus
)

// Repository interface (domain layer)
interface TrainingSessionRepository {
    suspend fun findById(id: UUID): TrainingSession?
    suspend fun findUpcoming(sportId: UUID?): List<TrainingSession>
    suspend fun save(session: TrainingSession): TrainingSession
}

// Use case
class ConfirmAttendanceUseCase(
    private val attendanceRepository: AttendanceRepository,
    private val sessionRepository: TrainingSessionRepository,
    private val userRepository: UserRepository
) {
    suspend fun execute(userId: UUID, sessionId: UUID): Result<Attendance> {
        // 1. Client-side validation for fast UX feedback
        val session = sessionRepository.findById(sessionId)
            ?: return Result.failure(SessionNotFoundException())

        if (session.status == SessionStatus.CANCELLED) {
            return Result.failure(SessionCancelledException())
        }

        // 2. Send to backend (backend validates again - source of truth)
        return try {
            val attendance = attendanceRepository.confirm(userId, sessionId)
            Result.success(attendance)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 3. Data Layer (Implementation)

**Responsibilities:**
- Implement repository interfaces
- Handle data sources (API, local storage)
- Map between API models and domain entities

**Technologies:**
- Ktor (HTTP client)
- SQLDelight (local database)
- Kotlinx.serialization

---

## Repository Pattern: Local → Remote Transition

The key to our phased development approach is the **Repository Pattern**. We define interfaces in the domain layer and can swap implementations without changing business logic.

### Phase 1: Local Implementation (Current)

Use local storage while backend is being developed:

```kotlin
// data/repository/LocalTrainingSessionRepository.kt
class LocalTrainingSessionRepository(
    private val database: Database
) : TrainingSessionRepository {

    override suspend fun findById(id: UUID): TrainingSession? {
        return database.trainingSessionQueries
            .findById(id.toString())
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override suspend fun findUpcoming(sportId: UUID?): List<TrainingSession> {
        return database.trainingSessionQueries
            .findUpcoming(sportId?.toString())
            .executeAsList()
            .map { it.toDomain() }
    }

    override suspend fun save(session: TrainingSession): TrainingSession {
        database.trainingSessionQueries.insert(
            id = session.id.toString(),
            sportId = session.sportId.toString(),
            // ... other fields
        )
        return session
    }
}
```

**Local Storage Options:**
- **SQLDelight**: Recommended for complex queries and relationships
- **SharedPreferences/DataStore**: Simple key-value storage
- **In-Memory**: For quick prototyping (data lost on app restart)

### Phase 2: Remote Implementation (When Backend Ready)

Swap to API calls - **same interface, different implementation**:

```kotlin
// data/repository/RemoteTrainingSessionRepository.kt
class RemoteTrainingSessionRepository(
    private val apiClient: HttpClient
) : TrainingSessionRepository {

    override suspend fun findById(id: UUID): TrainingSession? {
        return try {
            apiClient.get("/api/training-sessions/$id")
                .body<TrainingSessionDto>()
                .toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun findUpcoming(sportId: UUID?): List<TrainingSession> {
        val url = if (sportId != null) {
            "/api/training-sessions?sportId=$sportId&status=SCHEDULED"
        } else {
            "/api/training-sessions?status=SCHEDULED"
        }

        return apiClient.get(url)
            .body<List<TrainingSessionDto>>()
            .map { it.toDomain() }
    }

    override suspend fun save(session: TrainingSession): TrainingSession {
        return apiClient.post("/api/training-sessions") {
            setBody(session.toDto())
        }.body<TrainingSessionDto>().toDomain()
    }
}
```

### Dependency Injection Configuration

Use **Koin** to swap implementations:

```kotlin
// di/RepositoryModule.kt

// Phase 1: Local implementation
val localRepositoryModule = module {
    single<TrainingSessionRepository> {
        LocalTrainingSessionRepository(get())
    }
    single<AttendanceRepository> {
        LocalAttendanceRepository(get())
    }
    single<UserRepository> {
        LocalUserRepository(get())
    }
}

// Phase 2: Remote implementation (when backend ready)
val remoteRepositoryModule = module {
    single<TrainingSessionRepository> {
        RemoteTrainingSessionRepository(get())
    }
    single<AttendanceRepository> {
        RemoteAttendanceRepository(get())
    }
    single<UserRepository> {
        RemoteUserRepository(get())
    }
}

// Switch between implementations by changing the module:
fun initKoin(useRemote: Boolean = false) {
    startKoin {
        modules(
            if (useRemote) remoteRepositoryModule else localRepositoryModule,
            useCaseModule,
            viewModelModule
        )
    }
}
```

---

## Client-Side Validation Strategy

Mobile app validates for **fast UX feedback**, but backend is the **source of truth**.

### Validation Levels

| Rule | Client | Backend | Reason |
|------|--------|---------|--------|
| **Required fields** | ✅ Show errors immediately | ✅ Validate again | Fast UX |
| **Email format** | ✅ Regex check | ✅ Validate again | Fast UX |
| **Session capacity** | ✅ Check before submit | ✅ Enforce (source of truth) | Prevent wasted API calls |
| **Role permissions** | ✅ Hide UI elements | ✅ Authorize endpoints | Security + UX |
| **Calendar unlocking** | ⚠️ Optional pre-check | ✅ Enforce sequential + 3-month | Backend owns business logic |
| **Unique constraints** | ❌ Skip | ✅ Database constraints | Race conditions possible |

### Example: Client-Side Validation

```kotlin
// ViewModel validates before calling use case
class ConfirmAttendanceViewModel(
    private val confirmAttendanceUseCase: ConfirmAttendanceUseCase
) : ViewModel() {

    fun confirmAttendance(sessionId: UUID) {
        viewModelScope.launch {
            // 1. Client-side validation (fast UX feedback)
            val session = sessionState.value ?: return@launch

            if (session.status == SessionStatus.CANCELLED) {
                _uiState.value = UiState.Error("Session is cancelled")
                return@launch
            }

            if (session.confirmedCount >= session.capacity) {
                _uiState.value = UiState.Error("Session is full")
                return@launch
            }

            // 2. Call use case (which calls backend)
            _uiState.value = UiState.Loading

            val result = confirmAttendanceUseCase.execute(
                userId = currentUserId,
                sessionId = sessionId
            )

            // 3. Backend might still reject (e.g., race condition)
            _uiState.value = when {
                result.isSuccess -> UiState.Success(result.getOrNull()!!)
                else -> UiState.Error(result.exceptionOrNull()?.message ?: "Failed")
            }
        }
    }
}
```

---

## Role-Based UI

Show/hide features based on user roles and Committee privileges:

```kotlin
@Composable
fun HomeScreen(
    currentUser: User,
    userRoles: List<UserRole>
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Everyone sees schedule
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Schedule, "Schedule") },
                    label = { Text("Schedule") },
                    selected = true,
                    onClick = { /* Navigate */ }
                )

                // Only athletes see attendance
                if (userRoles.any { it.role == Role.ATHLETE }) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.CheckCircle, "My Attendance") },
                        label = { Text("Attendance") },
                        selected = false,
                        onClick = { /* Navigate */ }
                    )
                }

                // Only committee sees management
                if (userRoles.any { it.role == Role.COMMITTEE }) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, "Manage") },
                        label = { Text("Manage") },
                        selected = false,
                        onClick = { /* Navigate */ }
                    )
                }
            }
        }
    ) { padding ->
        // Content
    }
}

// Granular Committee privilege checks
@Composable
fun ManagementScreen(
    userRoles: List<UserRole>
) {
    val committeeRole = userRoles.find { it.role == Role.COMMITTEE }

    Column {
        // Only show if user has MANAGE_SESSIONS privilege
        if (committeeRole?.hasPrivilege(CommitteePrivilege.MANAGE_SESSIONS) == true) {
            Button(onClick = { /* Create session */ }) {
                Text("Create Training Session")
            }
        }

        // Only show if user has MANAGE_CALENDAR privilege
        if (committeeRole?.hasPrivilege(CommitteePrivilege.MANAGE_CALENDAR) == true) {
            Button(onClick = { /* Unlock calendar */ }) {
                Text("Unlock Calendar")
            }
        }

        // Only show if user has MANAGE_ATHLETES privilege
        if (committeeRole?.hasPrivilege(CommitteePrivilege.MANAGE_ATHLETES) == true) {
            Button(onClick = { /* Update levels */ }) {
                Text("Manage Athlete Levels")
            }
        }
    }
}
```

---

## Testing Strategy

### 1. Unit Tests (Domain Layer)

Test use cases with **fake repositories**:

```kotlin
class FakeTrainingSessionRepository : TrainingSessionRepository {
    private val sessions = mutableListOf<TrainingSession>()

    override suspend fun findById(id: UUID) = sessions.find { it.id == id }
    override suspend fun findUpcoming(sportId: UUID?) = sessions.filter { /* logic */ }
    override suspend fun save(session: TrainingSession): TrainingSession {
        sessions.add(session)
        return session
    }
}

class ConfirmAttendanceUseCaseTest {
    @Test
    fun `should fail when session is cancelled`() = runTest {
        val fakeRepo = FakeTrainingSessionRepository()
        val useCase = ConfirmAttendanceUseCase(fakeRepo, /* ... */)

        // Test logic
    }
}
```

### 2. Integration Tests (Data Layer)

Test repository implementations:

```kotlin
class LocalTrainingSessionRepositoryTest {
    @Test
    fun `should save and retrieve session from SQLite`() = runTest {
        val database = createInMemoryDatabase()
        val repository = LocalTrainingSessionRepository(database)

        // Test logic
    }
}
```

### 3. UI Tests (Presentation Layer)

Test composables and ViewModels:

```kotlin
class TrainingScheduleScreenTest {
    @Test
    fun `should display sessions when loaded`() {
        composeTestRule.setContent {
            TrainingScheduleScreen(viewModel = testViewModel)
        }

        composeTestRule.onNodeWithText("Tuesday Advanced").assertExists()
    }
}
```

---

## API Integration (Phase 2)

When backend is ready, API contracts should match domain model:

### Example API Contract

```http
GET /api/training-sessions?sportId={uuid}&status=SCHEDULED
Authorization: Bearer {token}

Response 200 OK:
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "sportId": "660e8400-e29b-41d4-a716-446655440000",
    "venueId": "770e8400-e29b-41d4-a716-446655440000",
    "date": "2024-02-15T20:00:00Z",
    "targetLevel": "ADVANCED",
    "capacity": 20,
    "confirmedCount": 15,
    "status": "SCHEDULED"
  }
]
```

### DTO Mapping

```kotlin
// API model (data layer)
@Serializable
data class TrainingSessionDto(
    val id: String,
    val sportId: String,
    val venueId: String,
    val date: String,
    val targetLevel: String,
    val capacity: Int,
    val confirmedCount: Int,
    val status: String
)

// Map to domain model
fun TrainingSessionDto.toDomain() = TrainingSession(
    id = UUID.fromString(id),
    sportId = UUID.fromString(sportId),
    venueId = UUID.fromString(venueId),
    date = Instant.parse(date),
    targetLevel = SessionLevel.valueOf(targetLevel),
    capacity = capacity,
    status = SessionStatus.valueOf(status)
)
```

---

## Navigation Structure

```
┌─────────────────────────────────────┐
│           Home/Schedule             │
│  (All users see upcoming sessions)  │
└──────┬──────────────┬───────────────┘
       │              │
       ▼              ▼
┌─────────────┐  ┌──────────────────┐
│ My Attendance│  │  Management      │
│  (Athletes)  │  │  (Committee)     │
└─────────────┘  └──────┬───────────┘
                        │
         ┌──────────────┼──────────────┬─────────────┐
         ▼              ▼              ▼             ▼
  ┌─────────────┐ ┌──────────┐ ┌────────────┐ ┌─────────┐
  │   Create    │ │  Unlock  │ │   Manage   │ │ Manage  │
  │   Session   │ │ Calendar │ │  Athletes  │ │ Venues  │
  └─────────────┘ └──────────┘ └────────────┘ └─────────┘
```

---

## Next Steps

1. ✅ Set up Kotlin Multiplatform project structure
2. ✅ Define domain entities and repository interfaces
3. ✅ Implement local repositories (SQLDelight)
4. ✅ Create use cases
5. ✅ Build UI with Compose
6. ⏳ Integrate with backend API (when ready)
7. ⏳ Add error handling and loading states
8. ⏳ Implement authentication flow

---

## Related Documentation

- [Domain Design](./domain/README.md) - Entities, business rules, use cases
- [UI Flow](./ui-flow.md) - Screen navigation and user journeys
- [API Contracts](./api/) - Backend endpoint specifications (when available)
