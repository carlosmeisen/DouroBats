# Domain Class Diagram

This diagram shows the structure of our core domain entities, their attributes, and relationships.

## Class Diagram

```mermaid
classDiagram
    class User {
        +UUID id
        +String name
        +String email
        +UserStatus status
        +DateTime createdAt
        +DateTime updatedAt
        +hasRole(Role) boolean
        +getRoles() List~UserRole~
        +isCommitteeAdmin() boolean
    }

    class UserRole {
        +UUID id
        +UUID userId
        +Role role
        +List~CommitteePrivilege~ privileges
        +UUID assignedBy
        +DateTime assignedAt
        +DateTime createdAt
        +hasPrivilege(CommitteePrivilege) boolean
        +isAthlete() boolean
        +isCommittee() boolean
        +isSupporter() boolean
    }

    class AthleteProfile {
        +UUID id
        +UUID userId
        +AthleteLevel level
        +DateTime createdAt
        +DateTime updatedAt
        +getHistory() List~AthleteProfileHistory~
    }

    class AthleteProfileHistory {
        +UUID id
        +UUID athleteProfileId
        +AthleteLevel oldLevel
        +AthleteLevel newLevel
        +UUID changedBy
        +DateTime changedAt
        +String reason
    }

    class Sport {
        +UUID id
        +String name
        +String description
        +Boolean isActive
        +DateTime createdAt
        +getSessions() List~TrainingSession~
        +getVenues() List~Venue~
        +getTemplates() List~SessionTemplate~
    }

    class Venue {
        +UUID id
        +String name
        +String address
        +Int capacity
        +List~UUID~ sportTags
        +Boolean isActive
        +DateTime createdAt
        +DateTime updatedAt
        +supportsSport(UUID) boolean
        +getSessions() List~TrainingSession~
    }

    class CalendarAccess {
        +UUID id
        +UUID sportId
        +Date startDate
        +Date endDate
        +UUID unlockedBy
        +DateTime unlockedAt
        +DateTime createdAt
        +isDateUnlocked(Date) boolean
        +isSequential(CalendarAccess) boolean
    }

    class SessionTemplate {
        +UUID id
        +UUID sportId
        +String name
        +DayOfWeek dayOfWeek
        +Time startTime
        +Time endTime
        +UUID venueId
        +SessionLevel targetLevel
        +Int capacity
        +Boolean isActive
        +UUID createdBy
        +DateTime createdAt
        +DateTime updatedAt
        +generateSessions(DateRange) List~TrainingSession~
    }

    class TrainingSession {
        +UUID id
        +UUID sportId
        +UUID venueId
        +DateTime date
        +SessionLevel targetLevel
        +Int capacity
        +SessionStatus status
        +String notes
        +UUID createdBy
        +DateTime createdAt
        +DateTime updatedAt
        +getConfirmedCount() Int
        +hasCapacity() Boolean
        +isFull() Boolean
        +isCancelled() Boolean
        +isPast() Boolean
        +isWithinUnlockedPeriod() Boolean
    }

    class Attendance {
        +UUID id
        +UUID userId
        +UUID sessionId
        +AttendanceStatus status
        +DateTime confirmedAt
        +DateTime cancelledAt
        +String notes
        +confirm() void
        +cancel() void
        +isConfirmed() Boolean
    }

    class UserStatus {
        <<enumeration>>
        ACTIVE
        INACTIVE
        SUSPENDED
    }

    class Role {
        <<enumeration>>
        ATHLETE
        SUPPORTER
        COMMITTEE
    }

    class CommitteePrivilege {
        <<enumeration>>
        MANAGE_SESSIONS
        MANAGE_CALENDAR
        MANAGE_ATHLETES
        MANAGE_VENUES
        VIEW_ANALYTICS
    }

    class AthleteLevel {
        <<enumeration>>
        BEGINNER
        INTERMEDIATE
        ADVANCED
    }

    class SessionLevel {
        <<enumeration>>
        BEGINNER
        INTERMEDIATE
        ADVANCED
        ALL_LEVELS
    }

    class SessionStatus {
        <<enumeration>>
        SCHEDULED
        CANCELLED
        COMPLETED
    }

    class AttendanceStatus {
        <<enumeration>>
        PENDING
        CONFIRMED
        CANCELLED
    }

    %% Relationships
    User "1" --> "1..*" UserRole : has
    User "1" --> "0..1" AthleteProfile : has
    AthleteProfile "1" --> "0..*" AthleteProfileHistory : tracks
    Sport "1" --> "0..*" TrainingSession : contains
    Sport "1" --> "0..*" CalendarAccess : controls
    Sport "1" --> "0..*" SessionTemplate : defines
    Venue "1" --> "0..*" TrainingSession : hosts
    SessionTemplate "1" -.-> "0..*" TrainingSession : generates
    TrainingSession "1" --> "0..*" Attendance : has
    User "1" --> "0..*" Attendance : participates

    User --> UserStatus : status
    UserRole --> Role : role
    UserRole --> CommitteePrivilege : privileges
    AthleteProfile --> AthleteLevel : level
    AthleteProfileHistory --> AthleteLevel : oldLevel/newLevel
    TrainingSession --> SessionStatus : status
    TrainingSession --> SessionLevel : targetLevel
    TrainingSession --> Sport : belongs to
    TrainingSession --> Venue : located at
    Attendance --> AttendanceStatus : status
    Attendance --> User : athlete
    Attendance --> TrainingSession : session
```

## Entity Details

### User
**Identity entity** representing any person in the system.

**Key Methods:**
- `hasRole(Role)`: Check if user has specific role
- `getRoles()`: Get all roles for this user
- `isCommitteeAdmin()`: Check if user has all Committee privileges

**Invariants:**
- Email must be unique
- Must have at least one UserRole
- Status cannot be null

---

### UserRole
**Entity** defining user permissions through role assignment with granular Committee privileges.

**Key Methods:**
- `hasPrivilege(CommitteePrivilege)`: Check if Committee role has specific privilege
- `isAthlete()`: Check if role is Athlete type
- `isCommittee()`: Check if role is Committee type
- `isSupporter()`: Check if role is Supporter type

**Business Rules:**
- One user can have multiple roles
- ATHLETE and SUPPORTER are mutually exclusive
- COMMITTEE can combine with ATHLETE or SUPPORTER
- Only COMMITTEE roles have privileges list
- Role type determines available actions

---

### AthleteProfile
**Entity** storing athlete-specific information like skill level.

**Key Methods:**
- `getHistory()`: Get all level change history records

**Invariants:**
- User must have ATHLETE role to have AthleteProfile
- Level is informational - doesn't restrict session attendance
- One profile per user

**Business Rules:**
- Only Committee with MANAGE_ATHLETES privilege can modify level
- Level changes automatically create history records

---

### AthleteProfileHistory
**Entity** providing audit trail for athlete level changes.

**Key Methods:**
- None - immutable record

**Invariants:**
- Cannot be deleted (permanent audit trail)
- Automatically created on level changes
- changedBy must be Committee member

**Business Rules:**
- Read-only after creation
- First record has null oldLevel

---

### Sport
**Entity** representing a sport category.

**Key Methods:**
- `getSessions()`: Get all training sessions for this sport
- `getVenues()`: Get all venues tagged with this sport
- `getTemplates()`: Get all session templates for this sport

**Invariants:**
- Name must be unique
- isActive determines if sport is currently offered

**Business Rules:**
- Inactive sports remain in system for historical data
- Each sport has independent calendar access control

---

### Venue
**Entity** representing pre-defined training locations with sport-based filtering.

**Key Methods:**
- `supportsSport(UUID)`: Check if venue is tagged with a sport
- `getSessions()`: Get all sessions at this venue

**Invariants:**
- Name must be unique
- Capacity is informational only (not enforced)
- sportTags list determines which sports can use venue

**Business Rules:**
- Only Committee with MANAGE_VENUES privilege can create/edit
- Sport-based filtering shown when creating sessions
- Provides consistent location data

---

### CalendarAccess
**Entity** controlling when training sessions can be scheduled, per sport.

**Key Methods:**
- `isDateUnlocked(Date)`: Check if specific date is within unlocked period
- `isSequential(CalendarAccess)`: Validate sequential unlocking with previous period

**Invariants:**
- Calendar locked by default
- Must be sequential (no gaps)
- Maximum 3 months ahead
- Per-sport access control

**Business Rules:**
- Only Committee with MANAGE_CALENDAR privilege can unlock
- Immutable audit trail (cannot delete records)
- Each sport has independent calendar access

---

### SessionTemplate
**Entity** defining recurring weekly training session patterns for calendar generation.

**Key Methods:**
- `generateSessions(DateRange)`: Create TrainingSession records for all weeks in date range

**Invariants:**
- Templates are patterns, not live sessions
- Generated sessions are independent (can be edited/cancelled)
- One template per day/sport/time combination

**Business Rules:**
- Only Committee with MANAGE_SESSIONS privilege can create/edit
- Critical for MVP - enables quick weekly schedule population
- Active templates used for generation
- Inactive templates retained for historical reference

---

### TrainingSession
**Entity** representing a scheduled training event with venue and target level.

**Key Methods:**
- `getConfirmedCount()`: Count confirmed attendances
- `hasCapacity()`: Check if session has available slots
- `isFull()`: Check if session is at capacity
- `isCancelled()`: Check if session is cancelled
- `isPast()`: Check if session date has passed
- `isWithinUnlockedPeriod()`: Verify session date is within CalendarAccess period

**Invariants:**
- Must have venue with matching sport tag
- Must be within unlocked calendar period
- Target level is informational (doesn't restrict attendance)

**Business Rules:**
- Only Committee with MANAGE_SESSIONS privilege can create/update/delete
- Cannot exceed capacity (enforced)
- Cannot modify past sessions
- Venue must support session's sport (via sportTags)
- Can be generated from templates or created manually

---

### Attendance
**Entity** managing the many-to-many relationship between Users (Athletes) and TrainingSessions.

**Key Methods:**
- `confirm()`: Mark attendance as confirmed
- `cancel()`: Cancel confirmed attendance
- `isConfirmed()`: Check if attendance is confirmed

**Invariants:**
- User must have ATHLETE role
- One attendance per user per session
- Cancelled attendances free capacity immediately

**Business Rules:**
- Only users with ATHLETE role can have Attendance records
- Cannot confirm if session is full
- Cannot confirm for cancelled sessions
- Cannot confirm for past sessions
- Users with only SUPPORTER role are blocked

---

## Enumeration Values

### UserStatus
- **ACTIVE**: User can access the system
- **INACTIVE**: User account is inactive
- **SUSPENDED**: User is temporarily suspended

### Role
- **ATHLETE**: Can confirm attendance for training sessions, has AthleteProfile
- **SUPPORTER**: Read-only access, cannot confirm attendance
- **COMMITTEE**: Can manage system features (based on privileges)

### CommitteePrivilege
- **MANAGE_SESSIONS**: Create, edit, cancel training sessions
- **MANAGE_CALENDAR**: Unlock/lock scheduling periods per sport
- **MANAGE_ATHLETES**: Edit athlete levels, manage athlete profiles
- **MANAGE_VENUES**: Add and edit venue information
- **VIEW_ANALYTICS**: Access reports and statistics (future feature)

**Note:** Committee member with all 5 privileges = COMMITTEE_ADMIN (can assign privileges)

### AthleteLevel
- **BEGINNER**: Entry-level athlete
- **INTERMEDIATE**: Mid-level athlete
- **ADVANCED**: Experienced athlete

**Note:** Level is informational only - doesn't restrict session attendance

### SessionLevel
- **BEGINNER**: Session targets beginner athletes
- **INTERMEDIATE**: Session targets intermediate athletes
- **ADVANCED**: Session targets advanced athletes
- **ALL_LEVELS**: Session welcomes all skill levels

**Note:** Target level is a guideline, not a restriction

### SessionStatus
- **SCHEDULED**: Session is planned and accepting confirmations
- **CANCELLED**: Session is cancelled, no confirmations allowed
- **COMPLETED**: Session has occurred (historical record)

### AttendanceStatus
- **PENDING**: Attendance created but not yet confirmed
- **CONFIRMED**: User confirmed they will attend
- **CANCELLED**: User cancelled their attendance

---

## Relationship Cardinality

| Relationship | Cardinality | Description |
|--------------|-------------|-------------|
| User → UserRole | 1 to many | A user must have at least one role, can have multiple |
| User → AthleteProfile | 1 to 0..1 | A user with ATHLETE role has one AthleteProfile |
| AthleteProfile → AthleteProfileHistory | 1 to many | Profile can have many level change records |
| Sport → TrainingSession | 1 to many | A sport can have zero or more training sessions |
| Sport → CalendarAccess | 1 to many | Each sport has independent calendar access records |
| Sport → SessionTemplate | 1 to many | Sport can have many recurring templates |
| Sport → Venue | many to many | Many sports can use many venues (via sportTags) |
| Venue → TrainingSession | 1 to many | Venue can host many sessions |
| SessionTemplate → TrainingSession | 1 to many | Template can generate many sessions (conceptually) |
| TrainingSession → Attendance | 1 to many | A session can have zero or more attendance records |
| User → Attendance | 1 to many | A user can attend zero or more sessions |
| TrainingSession → Sport | many to 1 | Each session belongs to exactly one sport |
| TrainingSession → Venue | many to 1 | Each session is at exactly one venue |
| Attendance → User | many to 1 | Each attendance is for exactly one user |
| Attendance → TrainingSession | many to 1 | Each attendance is for exactly one session |

---

## Design Patterns Used

### 1. Entity Pattern
All domain objects with unique identity follow the Entity pattern with:
- Unique ID (UUID)
- Equality based on ID
- Mutable state
- Lifecycle management

**Entities in this domain:**
- User, UserRole, AthleteProfile, AthleteProfileHistory
- Sport, Venue, CalendarAccess, SessionTemplate
- TrainingSession, Attendance

### 2. Enumeration Pattern
All status and type fields use enumerations for:
- Type safety
- Limited valid states
- Clear domain vocabulary

**Enumerations:** UserStatus, Role, CommitteePrivilege, AthleteLevel, SessionLevel, SessionStatus, AttendanceStatus

### 3. Audit Trail Pattern
Several entities track who made changes and when:
- UserRole: assignedBy, assignedAt
- AthleteProfileHistory: changedBy, changedAt
- CalendarAccess: unlockedBy, unlockedAt

### 4. Aggregate Pattern
**User + UserRoles + AthleteProfile** form an aggregate:
- User is the aggregate root
- UserRoles and AthleteProfile are accessed through User
- Role mutual exclusivity enforced at aggregate boundary
- Consistency boundary maintained

**TrainingSession + Attendance** form an aggregate:
- TrainingSession is the aggregate root
- Attendance records managed through Session
- Capacity rules enforced at aggregate boundary

**AthleteProfile + AthleteProfileHistory** form an aggregate:
- AthleteProfile is the aggregate root
- History records automatically created on profile changes
- Immutable audit trail maintained

### 5. Template Pattern
SessionTemplate uses the template pattern:
- Defines structure for recurring sessions
- Generates concrete TrainingSession instances
- Generated sessions are independent copies (not live-linked)

---

## Object Lifecycle

### User Lifecycle
```
[New] → ACTIVE → INACTIVE
           ↓
       SUSPENDED → ACTIVE
```

### UserRole Lifecycle
```
[New] → [Active] → [Deleted]
(Assigned by COMMITTEE_ADMIN)
```

### AthleteProfile Lifecycle
```
[New: BEGINNER] → INTERMEDIATE → ADVANCED
                      ↓              ↓
                  History Record Created
```

### CalendarAccess Lifecycle
```
[Locked by default] → [Unlocked by Committee] → [Immutable Record]
(Cannot be deleted - permanent audit trail)
```

### SessionTemplate Lifecycle
```
[New] → ACTIVE → INACTIVE
(Generates sessions when active)
```

### TrainingSession Lifecycle
```
[New] → SCHEDULED → COMPLETED
           ↓
       CANCELLED
(Can be generated from template or created manually)
```

### Attendance Lifecycle
```
[New] → PENDING → CONFIRMED → CANCELLED
                    ↑_____________|
```

---

## Next Steps

See also:
- [ER Diagram](./er-diagram.md) - Database perspective
- [Use Cases](./use-cases.md) - Interaction flows
- [Domain README](./README.md) - Complete domain overview
