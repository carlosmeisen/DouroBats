# Entity-Relationship Diagram

This diagram shows the database/persistence perspective of our domain model.

## ER Diagram

```mermaid
erDiagram
    USER ||--o{ USER_ROLE : has
    USER ||--o| ATHLETE_PROFILE : has
    ATHLETE_PROFILE ||--o{ ATHLETE_PROFILE_HISTORY : tracks
    SPORT ||--o{ TRAINING_SESSION : contains
    SPORT ||--o{ CALENDAR_ACCESS : controls
    SPORT ||--o{ SESSION_TEMPLATE : defines
    VENUE ||--o{ TRAINING_SESSION : hosts
    SESSION_TEMPLATE ||--o{ TRAINING_SESSION : generates
    TRAINING_SESSION ||--o{ ATTENDANCE : tracks
    USER ||--o{ ATTENDANCE : creates
    USER ||--o{ TRAINING_SESSION : creates

    USER {
        uuid id PK
        string name
        string email UK "unique"
        string status "ACTIVE|INACTIVE|SUSPENDED"
        timestamp created_at
        timestamp updated_at
    }

    USER_ROLE {
        uuid id PK
        uuid user_id FK
        string role "ATHLETE|SUPPORTER|COMMITTEE"
        json privileges "CommitteePrivilege[]"
        uuid assigned_by FK
        timestamp assigned_at
        timestamp created_at
    }

    ATHLETE_PROFILE {
        uuid id PK
        uuid user_id FK UK "unique"
        string level "BEGINNER|INTERMEDIATE|ADVANCED"
        timestamp created_at
        timestamp updated_at
    }

    ATHLETE_PROFILE_HISTORY {
        uuid id PK
        uuid athlete_profile_id FK
        string old_level "BEGINNER|INTERMEDIATE|ADVANCED|NULL"
        string new_level "BEGINNER|INTERMEDIATE|ADVANCED"
        uuid changed_by FK
        timestamp changed_at
        string reason
    }

    SPORT {
        uuid id PK
        string name UK "unique"
        string description
        boolean is_active
        timestamp created_at
    }

    VENUE {
        uuid id PK
        string name UK "unique"
        string address
        int capacity
        json sport_tags "UUID[]"
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    CALENDAR_ACCESS {
        uuid id PK
        uuid sport_id FK
        date start_date
        date end_date
        uuid unlocked_by FK
        timestamp unlocked_at
        timestamp created_at
    }

    SESSION_TEMPLATE {
        uuid id PK
        uuid sport_id FK
        string name
        string day_of_week "MONDAY-SUNDAY"
        time start_time
        time end_time
        uuid venue_id FK
        string target_level "BEGINNER|INTERMEDIATE|ADVANCED|ALL_LEVELS"
        int capacity
        boolean is_active
        uuid created_by FK
        timestamp created_at
        timestamp updated_at
    }

    TRAINING_SESSION {
        uuid id PK
        uuid sport_id FK
        uuid venue_id FK
        timestamp date
        string target_level "BEGINNER|INTERMEDIATE|ADVANCED|ALL_LEVELS"
        int capacity
        string status "SCHEDULED|CANCELLED|COMPLETED"
        string notes
        uuid created_by FK
        timestamp created_at
        timestamp updated_at
    }

    ATTENDANCE {
        uuid id PK
        uuid user_id FK
        uuid session_id FK
        string status "PENDING|CONFIRMED|CANCELLED"
        timestamp confirmed_at
        timestamp cancelled_at
        string notes
    }
```

## Table Specifications

### USER Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique user identifier |
| name | VARCHAR(255) | NOT NULL | User's full name |
| email | VARCHAR(255) | UNIQUE, NOT NULL | User's email address |
| status | VARCHAR(20) | NOT NULL | Account status (ACTIVE, INACTIVE, SUSPENDED) |
| created_at | TIMESTAMP | NOT NULL | Account creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |

**Indexes:**
- Primary: `id`
- Unique: `email`
- Index: `status` (for filtering active users)

---

### USER_ROLE Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique role identifier |
| user_id | UUID | FOREIGN KEY (USER), NOT NULL | Reference to user |
| role | VARCHAR(20) | NOT NULL | Role type (ATHLETE, SUPPORTER, COMMITTEE) |
| privileges | JSON | | Committee privileges array (only for COMMITTEE role) |
| assigned_by | UUID | FOREIGN KEY (USER) | Who assigned this role (audit trail) |
| assigned_at | TIMESTAMP | NOT NULL | When role was assigned |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

**Indexes:**
- Primary: `id`
- Foreign Key: `user_id` → `USER(id)`
- Foreign Key: `assigned_by` → `USER(id)`
- Composite Index: `(user_id, role)` (for checking user permissions)
- Index: `role` (for filtering by role type)

**Constraints:**
- `(user_id, role)` should be unique (one role of each type per user)
- `privileges` is JSON array of CommitteePrivilege values
- User must have at least one role
- Cannot have both ATHLETE and SUPPORTER roles

---

### ATHLETE_PROFILE Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique profile identifier |
| user_id | UUID | FOREIGN KEY (USER), UNIQUE, NOT NULL | Reference to user (one-to-one) |
| level | VARCHAR(20) | NOT NULL | Athlete skill level (BEGINNER, INTERMEDIATE, ADVANCED) |
| created_at | TIMESTAMP | NOT NULL | Profile creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |

**Indexes:**
- Primary: `id`
- Foreign Key: `user_id` → `USER(id)`
- Unique: `user_id` (one profile per user)
- Index: `level` (for filtering by level)

**Constraints:**
- User must have ATHLETE role to have AthleteProfile
- Level is informational only - doesn't restrict session attendance
- One profile per user

---

### ATHLETE_PROFILE_HISTORY Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique history record identifier |
| athlete_profile_id | UUID | FOREIGN KEY (ATHLETE_PROFILE), NOT NULL | Reference to athlete profile |
| old_level | VARCHAR(20) | | Previous level (NULL for first entry) |
| new_level | VARCHAR(20) | NOT NULL | New level assigned |
| changed_by | UUID | FOREIGN KEY (USER), NOT NULL | Committee member who made change |
| changed_at | TIMESTAMP | NOT NULL | When change occurred |
| reason | TEXT | | Optional explanation for level change |

**Indexes:**
- Primary: `id`
- Foreign Key: `athlete_profile_id` → `ATHLETE_PROFILE(id)`
- Foreign Key: `changed_by` → `USER(id)`
- Index: `athlete_profile_id` (for querying athlete history)
- Index: `changed_at` (for temporal queries)

**Constraints:**
- Cannot be deleted (immutable audit trail)
- Automatically created on level changes
- `changed_by` must be Committee member with MANAGE_ATHLETES privilege

---

### SPORT Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique sport identifier |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Sport name |
| description | TEXT | | Sport description |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Whether sport is currently active |
| created_at | TIMESTAMP | NOT NULL | Sport creation timestamp |

**Indexes:**
- Primary: `id`
- Unique: `name`
- Index: `is_active` (for filtering active sports)

---

### VENUE Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique venue identifier |
| name | VARCHAR(255) | UNIQUE, NOT NULL | Venue name |
| address | TEXT | NOT NULL | Full physical address |
| capacity | INTEGER | NOT NULL | Informational capacity (not enforced) |
| sport_tags | JSON | NOT NULL | Array of Sport IDs (sport filtering) |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Whether venue is currently available |
| created_at | TIMESTAMP | NOT NULL | Venue creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |

**Indexes:**
- Primary: `id`
- Unique: `name`
- Index: `is_active` (for filtering active venues)
- GIN Index: `sport_tags` (for JSON array queries - PostgreSQL)

**Constraints:**
- `sport_tags` is JSON array of UUID strings
- Capacity is informational only - session capacity is enforced
- Only Committee with MANAGE_VENUES privilege can create/edit

---

### CALENDAR_ACCESS Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique calendar access identifier |
| sport_id | UUID | FOREIGN KEY (SPORT), NOT NULL | Reference to sport (per-sport locking) |
| start_date | DATE | NOT NULL | Beginning of unlocked period |
| end_date | DATE | NOT NULL | End of unlocked period |
| unlocked_by | UUID | FOREIGN KEY (USER), NOT NULL | Committee member who unlocked |
| unlocked_at | TIMESTAMP | NOT NULL | When period was unlocked |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

**Indexes:**
- Primary: `id`
- Foreign Key: `sport_id` → `SPORT(id)`
- Foreign Key: `unlocked_by` → `USER(id)`
- Composite Index: `(sport_id, start_date, end_date)` (for date range queries)
- Index: `sport_id` (for sport-specific queries)

**Constraints:**
- `end_date` must be >= `start_date`
- Cannot delete records (immutable audit trail)
- Must be sequential (no gaps) per sport
- Maximum 3 months ahead of current date
- Only Committee with MANAGE_CALENDAR privilege can create

---

### SESSION_TEMPLATE Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique template identifier |
| sport_id | UUID | FOREIGN KEY (SPORT), NOT NULL | Reference to sport |
| name | VARCHAR(255) | NOT NULL | Template name (e.g., "Tuesday Advanced Volleyball") |
| day_of_week | VARCHAR(10) | NOT NULL | Day of week (MONDAY through SUNDAY) |
| start_time | TIME | NOT NULL | Session start time |
| end_time | TIME | NOT NULL | Session end time |
| venue_id | UUID | FOREIGN KEY (VENUE), NOT NULL | Reference to venue |
| target_level | VARCHAR(20) | NOT NULL | Target skill level |
| capacity | INTEGER | NOT NULL, CHECK > 0 | Maximum attendees |
| is_active | BOOLEAN | NOT NULL, DEFAULT TRUE | Whether template is currently used |
| created_by | UUID | FOREIGN KEY (USER), NOT NULL | Committee member who created |
| created_at | TIMESTAMP | NOT NULL | Template creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |

**Indexes:**
- Primary: `id`
- Foreign Key: `sport_id` → `SPORT(id)`
- Foreign Key: `venue_id` → `VENUE(id)`
- Foreign Key: `created_by` → `USER(id)`
- Composite Index: `(sport_id, day_of_week)` (for template queries)
- Index: `is_active` (for filtering active templates)

**Constraints:**
- `end_time` must be > `start_time`
- `capacity` must be greater than 0
- Venue must have sport in `sport_tags`
- Only Committee with MANAGE_SESSIONS privilege can create/edit
- Critical for MVP - enables quick weekly schedule population

---

### TRAINING_SESSION Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique session identifier |
| sport_id | UUID | FOREIGN KEY (SPORT), NOT NULL | Reference to sport |
| venue_id | UUID | FOREIGN KEY (VENUE), NOT NULL | Reference to venue |
| date | TIMESTAMP | NOT NULL | Session date and time |
| target_level | VARCHAR(20) | NOT NULL | Target skill level (BEGINNER, INTERMEDIATE, ADVANCED, ALL_LEVELS) |
| capacity | INTEGER | NOT NULL, CHECK (capacity > 0) | Maximum attendees (enforced) |
| status | VARCHAR(20) | NOT NULL | Session status (SCHEDULED, CANCELLED, COMPLETED) |
| notes | TEXT | | Optional session notes |
| created_by | UUID | FOREIGN KEY (USER), NOT NULL | Committee member who created it |
| created_at | TIMESTAMP | NOT NULL | Session creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |

**Indexes:**
- Primary: `id`
- Foreign Key: `sport_id` → `SPORT(id)`
- Foreign Key: `venue_id` → `VENUE(id)`
- Foreign Key: `created_by` → `USER(id)`
- Index: `date` (for querying upcoming sessions)
- Composite Index: `(sport_id, date)` (for sport-specific schedule)
- Composite Index: `(venue_id, date)` (for venue availability)
- Index: `status` (for filtering by status)
- Index: `target_level` (for filtering by level)

**Constraints:**
- `capacity` must be greater than 0
- `created_by` must reference a USER with COMMITTEE role and MANAGE_SESSIONS privilege
- Venue must have sport in `sport_tags`
- Session date must be within CalendarAccess unlocked period
- Target level is informational - doesn't restrict attendance

---

### ATTENDANCE Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PRIMARY KEY | Unique attendance identifier |
| user_id | UUID | FOREIGN KEY (USER), NOT NULL | Reference to user (must have ATHLETE role) |
| session_id | UUID | FOREIGN KEY (TRAINING_SESSION), NOT NULL | Reference to session |
| status | VARCHAR(20) | NOT NULL | Attendance status (PENDING, CONFIRMED, CANCELLED) |
| confirmed_at | TIMESTAMP | | Timestamp when confirmed |
| cancelled_at | TIMESTAMP | | Timestamp when cancelled |
| notes | TEXT | | Optional notes from athlete |

**Indexes:**
- Primary: `id`
- Foreign Key: `user_id` → `USER(id)`
- Foreign Key: `session_id` → `TRAINING_SESSION(id)`
- Unique Composite: `(user_id, session_id)` (one attendance per user per session)
- Index: `session_id` (for querying session attendees)
- Index: `user_id` (for querying user's sessions)
- Index: `status` (for filtering by status)

**Constraints:**
- `(user_id, session_id)` must be unique (prevent duplicate attendance)
- `user_id` must reference a USER with ATHLETE role
- `confirmed_at` is required when status = CONFIRMED
- `cancelled_at` is required when status = CANCELLED
- Cannot confirm if session at capacity
- Cannot confirm for cancelled or past sessions

---

## Database Constraints

### Foreign Key Constraints

```sql
-- USER_ROLE references USER
ALTER TABLE USER_ROLE
ADD CONSTRAINT fk_user_role_user
FOREIGN KEY (user_id) REFERENCES USER(id)
ON DELETE CASCADE;

ALTER TABLE USER_ROLE
ADD CONSTRAINT fk_user_role_assigned_by
FOREIGN KEY (assigned_by) REFERENCES USER(id)
ON DELETE SET NULL;

-- ATHLETE_PROFILE references USER
ALTER TABLE ATHLETE_PROFILE
ADD CONSTRAINT fk_athlete_profile_user
FOREIGN KEY (user_id) REFERENCES USER(id)
ON DELETE CASCADE;

-- ATHLETE_PROFILE_HISTORY references ATHLETE_PROFILE
ALTER TABLE ATHLETE_PROFILE_HISTORY
ADD CONSTRAINT fk_athlete_history_profile
FOREIGN KEY (athlete_profile_id) REFERENCES ATHLETE_PROFILE(id)
ON DELETE RESTRICT;

ALTER TABLE ATHLETE_PROFILE_HISTORY
ADD CONSTRAINT fk_athlete_history_changed_by
FOREIGN KEY (changed_by) REFERENCES USER(id)
ON DELETE RESTRICT;

-- CALENDAR_ACCESS references SPORT
ALTER TABLE CALENDAR_ACCESS
ADD CONSTRAINT fk_calendar_access_sport
FOREIGN KEY (sport_id) REFERENCES SPORT(id)
ON DELETE RESTRICT;

ALTER TABLE CALENDAR_ACCESS
ADD CONSTRAINT fk_calendar_access_unlocked_by
FOREIGN KEY (unlocked_by) REFERENCES USER(id)
ON DELETE RESTRICT;

-- SESSION_TEMPLATE references SPORT and VENUE
ALTER TABLE SESSION_TEMPLATE
ADD CONSTRAINT fk_template_sport
FOREIGN KEY (sport_id) REFERENCES SPORT(id)
ON DELETE RESTRICT;

ALTER TABLE SESSION_TEMPLATE
ADD CONSTRAINT fk_template_venue
FOREIGN KEY (venue_id) REFERENCES VENUE(id)
ON DELETE RESTRICT;

ALTER TABLE SESSION_TEMPLATE
ADD CONSTRAINT fk_template_created_by
FOREIGN KEY (created_by) REFERENCES USER(id)
ON DELETE RESTRICT;

-- TRAINING_SESSION references SPORT and VENUE
ALTER TABLE TRAINING_SESSION
ADD CONSTRAINT fk_session_sport
FOREIGN KEY (sport_id) REFERENCES SPORT(id)
ON DELETE RESTRICT;

ALTER TABLE TRAINING_SESSION
ADD CONSTRAINT fk_session_venue
FOREIGN KEY (venue_id) REFERENCES VENUE(id)
ON DELETE RESTRICT;

ALTER TABLE TRAINING_SESSION
ADD CONSTRAINT fk_session_creator
FOREIGN KEY (created_by) REFERENCES USER(id)
ON DELETE RESTRICT;

-- ATTENDANCE references USER and TRAINING_SESSION
ALTER TABLE ATTENDANCE
ADD CONSTRAINT fk_attendance_user
FOREIGN KEY (user_id) REFERENCES USER(id)
ON DELETE CASCADE;

ALTER TABLE ATTENDANCE
ADD CONSTRAINT fk_attendance_session
FOREIGN KEY (session_id) REFERENCES TRAINING_SESSION(id)
ON DELETE CASCADE;
```

### Unique Constraints

```sql
-- Email must be unique
ALTER TABLE USER
ADD CONSTRAINT uk_user_email UNIQUE (email);

-- User can have only one role of each type
ALTER TABLE USER_ROLE
ADD CONSTRAINT uk_user_role_type UNIQUE (user_id, role);

-- User can have only one athlete profile
ALTER TABLE ATHLETE_PROFILE
ADD CONSTRAINT uk_athlete_profile_user UNIQUE (user_id);

-- Sport name must be unique
ALTER TABLE SPORT
ADD CONSTRAINT uk_sport_name UNIQUE (name);

-- Venue name must be unique
ALTER TABLE VENUE
ADD CONSTRAINT uk_venue_name UNIQUE (name);

-- User can only have one attendance per session
ALTER TABLE ATTENDANCE
ADD CONSTRAINT uk_attendance_user_session UNIQUE (user_id, session_id);
```

### Check Constraints

```sql
-- User status must be valid
ALTER TABLE USER
ADD CONSTRAINT chk_user_status
CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED'));

-- Role type must be valid
ALTER TABLE USER_ROLE
ADD CONSTRAINT chk_user_role
CHECK (role IN ('ATHLETE', 'SUPPORTER', 'COMMITTEE'));

-- Athlete level must be valid
ALTER TABLE ATHLETE_PROFILE
ADD CONSTRAINT chk_athlete_level
CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'));

-- Athlete history levels must be valid
ALTER TABLE ATHLETE_PROFILE_HISTORY
ADD CONSTRAINT chk_history_old_level
CHECK (old_level IS NULL OR old_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'));

ALTER TABLE ATHLETE_PROFILE_HISTORY
ADD CONSTRAINT chk_history_new_level
CHECK (new_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'));

-- Venue capacity must be positive (informational)
ALTER TABLE VENUE
ADD CONSTRAINT chk_venue_capacity_positive
CHECK (capacity > 0);

-- Calendar date range must be valid
ALTER TABLE CALENDAR_ACCESS
ADD CONSTRAINT chk_calendar_date_range
CHECK (end_date >= start_date);

-- Session template time range must be valid
ALTER TABLE SESSION_TEMPLATE
ADD CONSTRAINT chk_template_time_range
CHECK (end_time > start_time);

ALTER TABLE SESSION_TEMPLATE
ADD CONSTRAINT chk_template_capacity_positive
CHECK (capacity > 0);

ALTER TABLE SESSION_TEMPLATE
ADD CONSTRAINT chk_template_target_level
CHECK (target_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'ALL_LEVELS'));

ALTER TABLE SESSION_TEMPLATE
ADD CONSTRAINT chk_template_day_of_week
CHECK (day_of_week IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'));

-- Training session capacity must be positive
ALTER TABLE TRAINING_SESSION
ADD CONSTRAINT chk_session_capacity_positive
CHECK (capacity > 0);

ALTER TABLE TRAINING_SESSION
ADD CONSTRAINT chk_session_status
CHECK (status IN ('SCHEDULED', 'CANCELLED', 'COMPLETED'));

ALTER TABLE TRAINING_SESSION
ADD CONSTRAINT chk_session_target_level
CHECK (target_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'ALL_LEVELS'));

-- Attendance status must be valid
ALTER TABLE ATTENDANCE
ADD CONSTRAINT chk_attendance_status
CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'));
```

---

## Query Examples

### Find Upcoming Sessions for a Sport with Venue

```sql
SELECT
    ts.id,
    ts.date,
    ts.target_level,
    v.name as venue_name,
    v.address as venue_address,
    ts.capacity,
    COUNT(a.id) as confirmed_count,
    (ts.capacity - COUNT(a.id)) as available_slots
FROM TRAINING_SESSION ts
JOIN VENUE v ON ts.venue_id = v.id
LEFT JOIN ATTENDANCE a ON ts.id = a.session_id
    AND a.status = 'CONFIRMED'
WHERE ts.sport_id = :sportId
    AND ts.date > NOW()
    AND ts.status = 'SCHEDULED'
GROUP BY ts.id, v.name, v.address
ORDER BY ts.date ASC;
```

### Check if User Has Specific Role

```sql
SELECT EXISTS(
    SELECT 1
    FROM USER_ROLE
    WHERE user_id = :userId
        AND role = :role
) as has_role;
```

### Check if User Has Committee Privilege

```sql
SELECT EXISTS(
    SELECT 1
    FROM USER_ROLE
    WHERE user_id = :userId
        AND role = 'COMMITTEE'
        AND privileges::jsonb ? :privilege
) as has_privilege;
```

### Find Venues for a Sport

```sql
SELECT
    v.id,
    v.name,
    v.address,
    v.capacity
FROM VENUE v
WHERE v.is_active = true
    AND v.sport_tags::jsonb ? :sportId::text
ORDER BY v.name ASC;
```

### Check if Date is Unlocked for a Sport

```sql
SELECT EXISTS(
    SELECT 1
    FROM CALENDAR_ACCESS
    WHERE sport_id = :sportId
        AND :date BETWEEN start_date AND end_date
) as is_unlocked;
```

### Get Active Session Templates for a Sport

```sql
SELECT
    st.id,
    st.name,
    st.day_of_week,
    st.start_time,
    st.end_time,
    st.target_level,
    v.name as venue_name
FROM SESSION_TEMPLATE st
JOIN VENUE v ON st.venue_id = v.id
WHERE st.sport_id = :sportId
    AND st.is_active = true
ORDER BY
    CASE st.day_of_week
        WHEN 'MONDAY' THEN 1
        WHEN 'TUESDAY' THEN 2
        WHEN 'WEDNESDAY' THEN 3
        WHEN 'THURSDAY' THEN 4
        WHEN 'FRIDAY' THEN 5
        WHEN 'SATURDAY' THEN 6
        WHEN 'SUNDAY' THEN 7
    END,
    st.start_time ASC;
```

### Get Athlete Profile with Level History

```sql
SELECT
    ap.id,
    ap.level as current_level,
    u.name as athlete_name,
    json_agg(
        json_build_object(
            'old_level', aph.old_level,
            'new_level', aph.new_level,
            'changed_by', u2.name,
            'changed_at', aph.changed_at,
            'reason', aph.reason
        ) ORDER BY aph.changed_at DESC
    ) as level_history
FROM ATHLETE_PROFILE ap
JOIN USER u ON ap.user_id = u.id
LEFT JOIN ATHLETE_PROFILE_HISTORY aph ON ap.id = aph.athlete_profile_id
LEFT JOIN USER u2 ON aph.changed_by = u2.id
WHERE ap.user_id = :userId
GROUP BY ap.id, ap.level, u.name;
```

### Find User's Confirmed Sessions

```sql
SELECT
    ts.id,
    ts.date,
    ts.location,
    s.name as sport_name,
    a.confirmed_at
FROM ATTENDANCE a
JOIN TRAINING_SESSION ts ON a.session_id = ts.id
JOIN SPORT s ON ts.sport_id = s.id
WHERE a.user_id = :userId
    AND a.status = 'CONFIRMED'
    AND ts.date > NOW()
ORDER BY ts.date ASC;
```

### Check Session Capacity

```sql
SELECT
    ts.capacity,
    COUNT(a.id) as confirmed_count,
    (ts.capacity - COUNT(a.id)) as available_slots,
    CASE
        WHEN COUNT(a.id) >= ts.capacity THEN true
        ELSE false
    END as is_full
FROM TRAINING_SESSION ts
LEFT JOIN ATTENDANCE a ON ts.id = a.session_id
    AND a.status = 'CONFIRMED'
WHERE ts.id = :sessionId
GROUP BY ts.id, ts.capacity;
```

### Find Athletes for a Session

```sql
SELECT
    u.id,
    u.name,
    u.email,
    a.status,
    a.confirmed_at
FROM ATTENDANCE a
JOIN USER u ON a.user_id = u.id
WHERE a.session_id = :sessionId
    AND a.status IN ('CONFIRMED', 'PENDING')
ORDER BY a.confirmed_at ASC;
```

---

## Data Integrity Rules

### 1. Referential Integrity
- All foreign keys must reference valid records
- CASCADE deletes for dependent entities (Profile, Attendance)
- RESTRICT deletes for referenced entities (Sport, TrainingSession by FK)

### 2. Domain Integrity
- All enum fields must have valid values
- Timestamps must be valid and logical (created_at ≤ updated_at)
- Capacity must be positive

### 3. Business Logic Integrity
Enforced at application layer:
- Only Athletes can have Attendance records
- Only Committee can create TrainingSessions
- Cannot exceed session capacity
- Cannot modify past sessions

---

## Database Platform Considerations

### PostgreSQL (Recommended for KMP)
- Native UUID support
- Excellent JSON support for future extensions
- Full text search capabilities
- Robust constraint enforcement

### SQLDelight (for KMP)
```kotlin
// Example SQLDelight query
interface TrainingSessionQueries {
    @Query("SELECT * FROM TRAINING_SESSION WHERE date > :now AND status = 'SCHEDULED' ORDER BY date ASC")
    fun findUpcomingSessions(now: Long): List<TrainingSession>

    @Query("SELECT COUNT(*) FROM ATTENDANCE WHERE session_id = :sessionId AND status = 'CONFIRMED'")
    fun getConfirmedCount(sessionId: String): Long
}
```

---

## Migration Strategy

### Phase 1: Core Tables
1. Create USER table
2. Create PROFILE table
3. Create SPORT table

### Phase 2: Session Management
4. Create TRAINING_SESSION table
5. Add foreign keys and constraints

### Phase 3: Attendance Tracking
6. Create ATTENDANCE table
7. Add composite indexes
8. Add business rule triggers (optional)

### Phase 4: Optimization
9. Add additional indexes based on query patterns
10. Add materialized views for statistics (if needed)

---

## Next Steps

See also:
- [Class Diagram](./class-diagram.md) - Object-oriented perspective
- [Use Cases](./use-cases.md) - Interaction flows
- [Domain README](./README.md) - Complete domain overview
