# Domain Design Updates

## Changes Based on Real-World Requirements

### 1. Role System Improvements

**Problem:** Committee members are also Athletes or Supporters, not a separate category.

**Solution:** Multi-role system with constraints

**Before:**
- Profile with type: ATHLETE | SUPPORTER | COMMITTEE

**After:**
- UserRole entity
- User can have multiple roles
- ATHLETE and SUPPORTER are mutually exclusive
- COMMITTEE can be combined with ATHLETE or SUPPORTER

**Valid Combinations:**
- ✅ Athlete only
- ✅ Supporter only
- ✅ Athlete + Committee
- ✅ Supporter + Committee
- ❌ Athlete + Supporter (mutually exclusive)

---

### 2. Athlete Levels

**New Requirement:** Athletes have skill levels that help organize sessions but don't restrict attendance.

**New Entity: AthleteProfile**
- Linked to users with ATHLETE role
- Contains: level (BEGINNER, INTERMEDIATE, ADVANCED)
- Informational only - athletes can join any session regardless of level

**Use Case:**
- Intermediate athlete can join Advanced session
- Advanced athlete can help in Beginner session
- Sunday "All Levels" sessions welcome everyone

---

### 3. Session Target Levels

**New Requirement:** Sessions target specific levels but allow cross-level attendance.

**TrainingSession Enhancement:**
- Add `targetLevel`: BEGINNER | INTERMEDIATE | ADVANCED | ALL_LEVELS
- Target level is a guideline, not a restriction
- Useful for athletes to find appropriate sessions

**Example (Volleyball Tuesday):**
- 8:00 PM - 9:30 PM: Target = ADVANCED
- 9:30 PM - 11:00 PM: Target = INTERMEDIATE
- Athletes can attend either regardless of their level

---

### 4. Recurring Sessions & Flexible Scheduling

**New Requirement:** Sessions often recur weekly with specific patterns that can change.

**Current Volleyball Schedule:**
- **Tuesday:**
  - 8:00 PM - 9:30 PM → Advanced
  - 9:30 PM - 11:00 PM → Intermediate
- **Wednesday:**
  - First slot → Intermediate
  - Second slot → Beginner
- **Thursday:**
  - First slot → Beginner
  - Second slot → Advanced
- **Sunday:**
  - 5:00 PM - 6:30 PM → All Levels

**Solution:**
Add optional recurrence support while keeping flexibility for schedule changes.

**New Entity: SessionRecurrence (Optional)**
- Pattern: WEEKLY | BIWEEKLY | MONTHLY
- DayOfWeek
- StartTime, EndTime
- Can be modified or cancelled

**Design Philosophy:**
- Sessions can be one-time or recurring
- Recurring pattern is a helper, not a constraint
- Committee can always create/modify individual sessions
- Keeps flexibility for schedule adjustments

---

## Updated Entity List

### Core Entities (6 total)

1. **User** - Identity and authentication
2. **UserRole** - Role assignment (ATHLETE, SUPPORTER, COMMITTEE)
3. **AthleteProfile** - Athlete-specific data (level, stats)
4. **Sport** - Sport categories
5. **TrainingSession** - Scheduled events with target level
6. **Attendance** - Session participation tracking

### New Enumerations

**Role:**
- ATHLETE
- SUPPORTER
- COMMITTEE

**AthleteLevel:**
- BEGINNER
- INTERMEDIATE
- ADVANCED

**SessionLevel:**
- BEGINNER
- INTERMEDIATE
- ADVANCED
- ALL_LEVELS

---

## Updated Business Rules

### Role Assignment Rules

1. **Minimum Role Rule**: User must have at least one role
2. **Mutual Exclusivity Rule**: User cannot be both ATHLETE and SUPPORTER
3. **Committee Combination Rule**: COMMITTEE can be combined with ATHLETE or SUPPORTER
4. **Athlete Profile Rule**: Only users with ATHLETE role can have an AthleteProfile

### Attendance Rules (Updated)

1. **Athlete-Only Rule**: Only users with ATHLETE role can confirm attendance
2. **Level Flexibility Rule**: Athletes can attend sessions of any target level
3. **Capacity Rule**: Cannot exceed session capacity (unchanged)
4. **Status Rule**: Cannot confirm for cancelled/past sessions (unchanged)

### Session Management Rules (Updated)

1. **Committee-Only Rule**: Only users with COMMITTEE role can manage sessions
2. **Target Level Rule**: Sessions should specify target level for athlete guidance
3. **Flexible Attendance Rule**: Target level is informational, not restrictive

---

## Migration Path

### From Old Model to New Model

**Profile Migration:**
```
Old: User → Profile (type: ATHLETE)
New: User → UserRole (role: ATHLETE) + AthleteProfile (level: INTERMEDIATE)

Old: User → Profile (type: COMMITTEE)
New: User → UserRole (role: ATHLETE) + UserRole (role: COMMITTEE) + AthleteProfile

Old: User → Profile (type: SUPPORTER)
New: User → UserRole (role: SUPPORTER)
```

**Session Migration:**
```
Old: TrainingSession (no level info)
New: TrainingSession (targetLevel: INTERMEDIATE)
```

---

## Examples with Real Data

### Example 1: Volleyball Committee Member who Plays

**User:** João Silva
**Roles:**
- ATHLETE
- COMMITTEE

**Athlete Profile:**
- Level: ADVANCED

**Capabilities:**
- ✅ Can manage sessions (Committee)
- ✅ Can confirm attendance (Athlete)
- ✅ Can attend any session regardless of target level

---

### Example 2: Tuesday Volleyball Sessions

**Sport:** Volleyball

**Session 1:**
- Day: Tuesday
- Time: 20:00 - 21:30
- Target Level: ADVANCED
- Capacity: 20
- Location: Pavilhão Municipal

**Session 2:**
- Day: Tuesday
- Time: 21:30 - 23:00
- Target Level: INTERMEDIATE
- Capacity: 20
- Location: Pavilhão Municipal

**Attendance Scenario:**
- Maria (INTERMEDIATE) confirms for Session 1 (Advanced) ✅ Allowed
- Pedro (ADVANCED) confirms for Session 2 (Intermediate) ✅ Allowed
- Both can help each other improve!

---

### Example 3: Sunday Open Session

**Session:**
- Day: Sunday
- Time: 17:00 - 18:30
- Target Level: ALL_LEVELS
- Capacity: 30
- Location: Pavilhão Municipal

**Purpose:**
- Mixed-level practice
- Social gathering
- New members welcome
- Everyone learns together

---

## Implementation Impact

### New Tables Needed

```sql
-- Replace PROFILE table with USER_ROLE
CREATE TABLE USER_ROLE (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES USER(id),
    role VARCHAR(20) NOT NULL, -- ATHLETE, SUPPORTER, COMMITTEE
    created_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, role)
);

-- New table for athlete-specific data
CREATE TABLE ATHLETE_PROFILE (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES USER(id),
    level VARCHAR(20) NOT NULL, -- BEGINNER, INTERMEDIATE, ADVANCED
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE(user_id)
);

-- Add constraint: user_id must have ATHLETE role
```

### Updated Training Session

```sql
ALTER TABLE TRAINING_SESSION
ADD COLUMN target_level VARCHAR(20) NOT NULL DEFAULT 'ALL_LEVELS';
-- Values: BEGINNER, INTERMEDIATE, ADVANCED, ALL_LEVELS
```

---

## Next Steps

1. Review and approve these changes
2. Update all diagram files
3. Update implementation examples
4. Add migration notes
5. Update use cases with level scenarios

---

---

## 5. Venue Management

**New Requirement:** Pre-defined venues for consistent location data.

**New Entity: Venue**
```
Venue
  - id: UUID
  - name: String ("Pavilhão Municipal", "Campo de Padel")
  - address: String (full address)
  - capacity: Int (informational only - no enforcement)
  - sportTags: List<UUID> (which sports use this venue)
  - isActive: Boolean
  - createdAt: Timestamp
```

**Key Features:**
- Sport-based filtering (only show relevant venues)
- Capacity is informational only
- Reduces data inconsistency

---

## 6. Calendar Access Control

**New Entity: CalendarAccess**
```
CalendarAccess
  - id: UUID
  - sportId: UUID (per-sport locking)
  - startDate: Date
  - endDate: Date
  - unlockedBy: UUID (audit trail)
  - unlockedAt: Timestamp
```

**Rules:**
- Locked by default
- Sequential unlocking (no gaps)
- Max 3 months ahead
- Audit trail

---

## 7. Committee Privilege System

**Enhanced UserRole:**
```
UserRole (when role = COMMITTEE)
  - privileges: List<CommitteePrivilege>
  - assignedBy: UUID (audit)
  - assignedAt: Timestamp
```

**Privileges:**
- MANAGE_SESSIONS
- MANAGE_CALENDAR
- MANAGE_ATHLETES
- MANAGE_VENUES
- VIEW_ANALYTICS

**Admin:** Has all 5 privileges

---

## 8. Session Templates (MVP - Important!)

**New Entity: SessionTemplate**
```
SessionTemplate
  - id: UUID
  - sportId: UUID
  - name: String ("Tuesday Advanced Volleyball")
  - dayOfWeek: DayOfWeek
  - startTime: Time
  - endTime: Time
  - venueId: UUID
  - targetLevel: SessionLevel
  - capacity: Int
  - isActive: Boolean
  - createdBy: UUID
```

**Purpose:** Quickly generate recurring weekly sessions

**Use Case:**
1. Committee creates templates for regular schedule
2. When calendar unlocked, generate sessions from templates
3. Creates individual TrainingSession records
4. Can still edit/cancel individual sessions

**Example Templates (Volleyball):**
- Tuesday Advanced: 8pm-9:30pm
- Tuesday Intermediate: 9:30pm-11pm
- Sunday All Levels: 5pm-6:30pm

---

## 9. Athlete Level History (MVP Data Model)

**New Entity: AthleteProfileHistory**
```
AthleteProfileHistory
  - id: UUID
  - athleteProfileId: UUID
  - oldLevel: AthleteLevel (null if first)
  - newLevel: AthleteLevel
  - changedBy: UUID (Committee member)
  - changedAt: Timestamp
  - reason: String (optional)
```

**Purpose:** Track athlete progression
**MVP:** Store data, UI for viewing history later

---

## Final Entity List (10 Entities)

1. **User** - Identity
2. **UserRole** - Roles with Committee privileges
3. **AthleteProfile** - Athlete level
4. **AthleteProfileHistory** - Level change tracking
5. **Sport** - Sport categories
6. **Venue** - Pre-defined locations
7. **CalendarAccess** - Scheduling control
8. **SessionTemplate** - Recurring patterns
9. **TrainingSession** - Scheduled events
10. **Attendance** - Participation tracking

---

## MVP Scope Decisions

### ✅ Included in MVP:
- Multi-role system with Committee privileges
- Athlete levels with history tracking
- Venue management with sport tags
- Calendar access control (per sport, 3-month limit)
- Session templates for recurring schedules
- Audit trails (calendar unlock, privilege assignment, level changes)

### 📅 Future Features (Not MVP):
- Notification preferences (uncertain)
- Waiting lists (not needed)
- Attendance statistics/reports
- Auto-notifications for calendar unlock
- UI for viewing athlete level history

---

## Questions Resolved

1. ✅ Multi-role system
2. ✅ Athlete levels (Beginner, Intermediate, Advanced)
3. ✅ Level history tracking (data model in MVP)
4. ✅ Venue capacity (informational only)
5. ✅ Venue sport tags (filtering)
6. ✅ Calendar audit trail
7. ✅ Committee privileges with Admin
8. ✅ Session templates (important for MVP!)
9. ✅ Max unlock period (3 months)
10. ✅ Sequential unlocking required
