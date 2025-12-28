# DouroBats Domain Design

## Overview

This document defines the core domain entities, relationships, and business rules for the DouroBats training attendance management system. This represents our **Minimum Viable Domain (MVD)** - a focused, production-ready foundation that can be extended over time.

## Domain Context

**DouroBats** is a multi-sport association managing training sessions for various sports (Volleyball, Futsal, Padel, etc.). The core challenge is managing **who can attend which training sessions** with proper authorization and capacity control.

### Key Stakeholders

- **Athletes**: Can view and confirm attendance for training sessions
- **Supporters**: Can view information but cannot confirm attendance
- **Committee Members**: Can manage training sessions (create, update, delete)

---

## Core Domain Entities

### 1. User

The fundamental identity in the system representing any person.

**Attributes:**
- `id`: Unique identifier (UUID)
- `name`: Full name
- `email`: Email address (unique)
- `status`: Account status (Active, Inactive, Suspended)

**Responsibilities:**
- Authentication and identity management
- Profile association
- Activity tracking

---

### 2. Profile (Role)

Defines what a user can do in the system.

**Types:**
- **Athlete**: Can view and confirm attendance for training sessions
- **Supporter**: Can view information only (read-only access)
- **Committee**: Can manage training sessions and view all data

**Attributes:**
- `id`: Unique identifier
- `userId`: Reference to User
- `type`: Profile type (Athlete, Supporter, Committee)
- `createdAt`: Profile creation timestamp

**Responsibilities:**
- Authorization and permission checking
- Role-based access control

**Business Rule:** A User must have at least one Profile. A User can have multiple Profiles (e.g., both Athlete and Committee).

---

### 3. Sport

Represents a specific sport or activity category.

**Attributes:**
- `id`: Unique identifier
- `name`: Sport name (e.g., "Volleyball", "Futsal", "Padel")
- `description`: Brief description of the sport
- `isActive`: Whether the sport is currently active

**Responsibilities:**
- Categorization of training sessions
- Sport-specific configuration

**Examples:**
- Volleyball (Voleibol)
- Futsal
- Padel
- Basketball

---

### 4. TrainingSession

A specific scheduled training event for a sport.

**Attributes:**
- `id`: Unique identifier
- `sportId`: Reference to Sport
- `date`: Session date and time
- `location`: Training location/venue
- `capacity`: Maximum number of attendees
- `status`: Session status (Scheduled, Cancelled, Completed)
- `notes`: Optional notes for the session
- `createdBy`: Committee member who created it
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp

**Responsibilities:**
- Session scheduling and management
- Capacity tracking
- Session lifecycle management

**Business Rules:**
- Only Committee members can create/update/delete sessions
- Cannot confirm attendance for cancelled sessions
- Cannot exceed capacity (enforced by Attendance logic)

---

### 5. Attendance

The bridge entity managing the many-to-many relationship between Users (Athletes) and Training Sessions.

**Attributes:**
- `id`: Unique identifier
- `userId`: Reference to User (must be an Athlete)
- `sessionId`: Reference to TrainingSession
- `status`: Confirmation status (Confirmed, Cancelled, Pending)
- `confirmedAt`: Timestamp when attendance was confirmed
- `cancelledAt`: Timestamp if attendance was cancelled
- `notes`: Optional notes from the athlete

**Responsibilities:**
- Tracking who attends which session
- Managing confirmation and cancellation
- Capacity enforcement

**Business Rules:**
- Only users with Athlete profile can confirm attendance
- Cannot confirm if session is at capacity (unless replacing a cancelled attendance)
- Cannot confirm for past sessions
- Supporters are blocked from creating Attendance records

---

## Entity Relationships

### Cardinality

```
User (1) ---- (1..N) Profile
   User can have one or more Profiles

Sport (1) ---- (0..N) TrainingSession
   A Sport can have many Training Sessions

TrainingSession (1) ---- (0..N) Attendance
   A Session can have many Attendance records

User (1) ---- (0..N) Attendance
   A User can attend many Sessions (via Attendance)
```

### Many-to-Many Relationship

**TrainingSession ↔ User** is managed through the **Attendance** entity:
- Many Athletes can attend many Training Sessions
- The Attendance entity adds metadata (status, timestamps, notes)

---

## Business Rules

### Authorization Rules

#### 1. Supporter Restriction Rule
**Rule:** Supporters cannot confirm attendance for training sessions.

**Implementation:**
```kotlin
fun confirmAttendance(userId: UUID, sessionId: UUID) {
    val profile = getProfile(userId)

    if (profile.type == ProfileType.SUPPORTER) {
        throw UnauthorizedException("Supporters cannot confirm attendance")
    }

    // Continue with confirmation logic
}
```

#### 2. Committee Management Rule
**Rule:** Only Committee members can create, update, or delete training sessions.

**Implementation:**
```kotlin
fun createSession(userId: UUID, sessionData: SessionData) {
    val profile = getProfile(userId)

    if (profile.type != ProfileType.COMMITTEE) {
        throw UnauthorizedException("Only Committee members can manage sessions")
    }

    // Continue with session creation
}
```

### Capacity Rules

#### 3. Session Capacity Rule
**Rule:** Cannot confirm attendance if session is at full capacity.

**Implementation:**
```kotlin
fun confirmAttendance(userId: UUID, sessionId: UUID) {
    val session = getSession(sessionId)
    val currentAttendance = getConfirmedCount(sessionId)

    if (currentAttendance >= session.capacity) {
        throw CapacityExceededException("Session is at full capacity")
    }

    // Continue with confirmation
}
```

#### 4. Cancelled Session Rule
**Rule:** Cannot confirm attendance for cancelled sessions.

### Temporal Rules

#### 5. Past Session Rule
**Rule:** Cannot confirm attendance for sessions that have already occurred.

---

## Use Cases

### UC-01: Athlete Confirms Attendance

**Actor:** Athlete

**Preconditions:**
- User has Athlete profile
- Session is not cancelled
- Session is not at capacity
- Session is in the future

**Flow:**
1. Athlete views upcoming training sessions
2. Athlete selects a session to attend
3. System validates user is an Athlete
4. System checks session capacity
5. System creates Attendance record with status "Confirmed"
6. System sends confirmation notification

**Postconditions:**
- Attendance record created
- Session capacity updated

---

### UC-02: Athlete Cancels Attendance

**Actor:** Athlete

**Preconditions:**
- User has confirmed attendance for the session

**Flow:**
1. Athlete views their confirmed sessions
2. Athlete cancels attendance
3. System updates Attendance status to "Cancelled"
4. System frees up capacity slot

**Postconditions:**
- Attendance status updated to Cancelled
- Session capacity slot freed

---

### UC-03: Committee Creates Training Session

**Actor:** Committee Member

**Preconditions:**
- User has Committee profile

**Flow:**
1. Committee member navigates to session management
2. Committee member fills session details (sport, date, location, capacity)
3. System validates user is Committee member
4. System creates TrainingSession
5. System notifies relevant athletes

**Postconditions:**
- New TrainingSession created
- Athletes notified of new session

---

### UC-04: Committee Views Attendance List

**Actor:** Committee Member

**Preconditions:**
- User has Committee profile
- Training session exists

**Flow:**
1. Committee member selects a training session
2. System displays list of confirmed athletes
3. Committee member can view attendance statistics

**Postconditions:**
- None (read-only operation)

---

### UC-05: Supporter Views Training Schedule

**Actor:** Supporter

**Preconditions:**
- User has Supporter profile

**Flow:**
1. Supporter views training schedule
2. System displays all upcoming sessions (read-only)
3. Supporter cannot confirm attendance

**Postconditions:**
- None (read-only operation)

---

## Domain Invariants

**Invariants** are rules that must always be true:

1. **User Profile Requirement**: Every User must have at least one Profile
2. **Athlete Attendance Only**: Only Athlete profiles can have Attendance records
3. **Valid Session Status**: A TrainingSession can only be Scheduled, Cancelled, or Completed
4. **Capacity Constraint**: Confirmed attendance count ≤ session capacity
5. **Unique Email**: Each User email must be unique in the system
6. **Future Sessions Only**: Cannot create sessions in the past
7. **Valid Attendance Status**: Attendance can only be Confirmed, Cancelled, or Pending

---

## Future Extensions

This MVD can be extended with additional domains:

### Planned Extensions

1. **Team/Squad Management**
   - Athletes belong to teams (e.g., Men's Team, Youth Team)
   - Sessions can be team-specific
   - Team statistics and performance tracking

2. **Payment Management**
   - Membership fees
   - Session fees
   - Payment history
   - Subscription management

3. **Notification System**
   - Email/SMS notifications
   - Push notifications
   - Notification preferences per user

4. **Venue Management**
   - Multiple locations/venues
   - Venue booking and availability
   - Facility information

5. **Tournament Management**
   - Competition scheduling
   - Match results
   - Standings and rankings

6. **Equipment/Inventory**
   - Sports equipment tracking
   - Borrowing system
   - Maintenance records

### Why Start Small?

This focused domain allows us to:
- ✅ Build a working MVP quickly
- ✅ Validate core functionality with real users
- ✅ Maintain clean architecture
- ✅ Add features incrementally without breaking existing code
- ✅ Iterate based on user feedback

---

## Implementation Notes

### Repository Pattern

Each entity will have a corresponding repository interface:

```kotlin
interface UserRepository {
    suspend fun findById(id: UUID): User?
    suspend fun findByEmail(email: String): User?
    suspend fun save(user: User): User
}

interface TrainingSessionRepository {
    suspend fun findById(id: UUID): TrainingSession?
    suspend fun findUpcoming(sportId: UUID?): List<TrainingSession>
    suspend fun save(session: TrainingSession): TrainingSession
}

interface AttendanceRepository {
    suspend fun findBySession(sessionId: UUID): List<Attendance>
    suspend fun findByUser(userId: UUID): List<Attendance>
    suspend fun save(attendance: Attendance): Attendance
}
```

### Use Case Pattern

Business logic will be encapsulated in Use Cases:

```kotlin
class ConfirmAttendanceUseCase(
    private val userRepository: UserRepository,
    private val sessionRepository: TrainingSessionRepository,
    private val attendanceRepository: AttendanceRepository
) {
    suspend fun execute(userId: UUID, sessionId: UUID): Attendance {
        // 1. Validate user is athlete
        // 2. Validate session exists and is not cancelled
        // 3. Check capacity
        // 4. Create attendance record
        // 5. Return result
    }
}
```

---

## Diagrams

For visual representations of this domain design, see:
- [Class Diagram](./class-diagram.md) - Entity structure and relationships
- [ER Diagram](./er-diagram.md) - Database/entity relationships
- [Use Case Diagram](./use-cases.md) - Actor interactions

---

## Glossary

- **MVD**: Minimum Viable Domain - focused core domain model
- **Profile**: User role determining permissions
- **Attendance**: Confirmation of user participation in a training session
- **Capacity**: Maximum number of athletes allowed in a session
- **Committee**: Administrative role with session management privileges
- **Supporter**: Read-only role without attendance confirmation rights
