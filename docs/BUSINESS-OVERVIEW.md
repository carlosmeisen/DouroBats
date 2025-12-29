# DouroBats Training Management System - Business Overview

## What is DouroBats?

DouroBats is a **multi-sport association** that organizes training sessions for various sports including Volleyball, Futsal, Padel, Basketball, and Running. This system helps manage training schedules, track who's attending, and coordinate between athletes and organizers.

**The Goal:** Make it simple for athletes to find and attend training sessions, while giving committee members the tools they need to manage schedules efficiently.

---

## Who Uses the System?

### 1. Athletes
**Who they are:** Members who actively participate in training sessions.

**What they can do:**
- View upcoming training sessions for all sports
- Confirm their attendance for sessions
- Cancel their attendance if plans change
- See their skill level in each sport (Beginner, Intermediate, Advanced)
- Participate in multiple sports with one account

**Example:** João plays both Volleyball and Padel. He logs in once and sees all upcoming sessions for both sports. He's marked as Advanced in Volleyball and Beginner in Padel, which helps him choose appropriate sessions.

---

### 2. Supporters
**Who they are:** Family members, friends, or fans who want to follow the activities.

**What they can do:**
- View training schedules
- See session information (when, where, what level)
- Stay informed about DouroBats activities

**What they CANNOT do:**
- Confirm attendance for sessions (they're not participating)

**Example:** Maria's daughter plays Volleyball. Maria has a Supporter account so she can check the training schedule and know when to pick up her daughter.

---

### 3. Committee Members
**Who they are:** Volunteers who manage DouroBats operations.

**What they can do depends on their assigned privileges:**

#### Manage Sessions Privilege
- Create new training sessions
- Edit existing sessions (change time, venue, capacity)
- Cancel sessions if needed
- Set up recurring session templates (e.g., "Every Tuesday at 8pm")
- Generate weekly schedules from templates

#### Manage Calendar Privilege
- Unlock future dates so sessions can be scheduled
- Control how far ahead sessions can be created (up to 3 months)
- Track who unlocked which periods and when

#### Manage Athletes Privilege
- Update athlete skill levels (Beginner → Intermediate → Advanced)
- View athlete progression history
- Help athletes understand which sessions are appropriate for their level

#### Manage Venues Privilege
- Add new training locations
- Update venue information (address, capacity)
- Tag venues with which sports they support

#### View Analytics Privilege *(Future Feature)*
- See attendance statistics
- Track session popularity
- Analyze athlete participation trends

**Committee Admin:** A committee member with ALL five privileges. They can also assign privileges to other committee members.

**Example:** João is a committee member with "Manage Sessions" privilege. He creates the weekly Volleyball schedule and can cancel sessions if the venue becomes unavailable. However, he cannot change athlete skill levels because he doesn't have "Manage Athletes" privilege.

---

## How the System Works

### Training Sessions

**What is a session?**
A scheduled training event at a specific venue, date, and time.

**Session Details:**
- **Sport**: Volleyball, Futsal, Padel, Basketball, Running, etc.
- **Date & Time**: When the session happens
- **Venue**: Where the session takes place
- **Target Level**: Beginner, Intermediate, Advanced, or All Levels
- **Capacity**: Maximum number of attendees (e.g., 20 people)
- **Status**: Scheduled, Cancelled, or Completed

**Important:** The "Target Level" is a **recommendation**, not a restriction. An Advanced player can attend a Beginner session to help out, and a Beginner can attend an Advanced session if they want to challenge themselves.

---

### Attendance Confirmation

**How it works:**
1. Athlete sees an upcoming session
2. Athlete clicks "Confirm Attendance"
3. System checks if there's still space (capacity limit)
4. Attendance is confirmed, one slot is taken
5. Athlete can cancel anytime, freeing up the slot immediately

**Business Rules:**
- Only athletes can confirm attendance (Supporters cannot)
- Cannot confirm if session is already full
- Cannot confirm for cancelled sessions
- Cannot confirm for sessions that already happened
- One person can only confirm once per session

**Example:** Tuesday's Volleyball session has 20 slots. 15 people have confirmed. Maria tries to confirm - it works (16/20). Later, João tries when 20/20 are confirmed - system says "Session Full." If someone cancels, their slot becomes available immediately.

---

### Calendar Access Control

**Why we need it:**
Without control, committee members could create sessions years in advance, making schedule management chaotic.

**How it works:**
1. By default, the calendar is **locked** - no sessions can be created
2. Committee member with "Manage Calendar" privilege unlocks a period (e.g., "February 1-28")
3. Sessions can now be created for those dates
4. Calendar can be unlocked **up to 3 months ahead**
5. Unlocking must be **sequential** (no gaps - can't unlock March without unlocking February first)
6. Each sport has its own independent calendar

**Example:**
- **January 15:** Committee unlocks February 1-28 for Volleyball
- **January 30:** Committee unlocks March 1-31 for Volleyball (sequential, within 3 months ✅)
- **January 30:** Committee tries to unlock May 1-31 for Volleyball - system blocks it (gap in April ❌)
- **January 30:** Committee unlocks February 1-28 for Padel (independent from Volleyball ✅)

**Audit Trail:** System tracks who unlocked which periods and when. These records cannot be deleted.

---

### Session Templates (Recurring Sessions)

**Why we need them:**
Most sports have the same weekly schedule (e.g., Volleyball every Tuesday at 8pm). Creating each session manually would be tedious.

**How it works:**
1. Committee creates a template: "Tuesday Advanced Volleyball - 8pm at Pavilhão Municipal"
2. When unlocking a new month, committee clicks "Generate from Templates"
3. System creates individual sessions for every Tuesday in that month
4. Each generated session is independent - can be edited or cancelled individually

**Example Templates:**
- "Tuesday Advanced Volleyball" - Every Tuesday 8:00-9:30pm, Pavilhão Municipal, Advanced, 20 people
- "Tuesday Intermediate Volleyball" - Every Tuesday 9:30-11:00pm, Pavilhão Municipal, Intermediate, 20 people
- "Sunday All Levels Volleyball" - Every Sunday 5:00-6:30pm, Pavilhão Municipal, All Levels, 25 people

**Benefit:** Instead of creating 12 sessions manually for each month, committee generates them in seconds.

---

### Athlete Skill Levels

**Purpose:** Help athletes find appropriate sessions and track their progression.

**Levels:**
- **Beginner**: Just starting out, learning basics
- **Intermediate**: Comfortable with fundamentals, developing skills
- **Advanced**: Experienced player, strong technical skills

**Important Points:**
- Levels are **informational only** - they don't restrict which sessions you can attend
- Committee members with "Manage Athletes" privilege can update levels
- Level changes are tracked with history (who changed it, when, why)
- Athletes can have **different levels in different sports**

**Example:** João has been playing Volleyball for 3 years (Advanced) but just started Padel last month (Beginner). His profile shows both levels. When browsing sessions:
- He sees "Tuesday Advanced Volleyball" - marked as matching his level
- He sees "Wednesday Beginner Padel" - marked as matching his level
- He can still attend ANY session regardless of level

---

### Venues

**What are they?**
Pre-defined training locations with consistent information.

**Why we need them:**
Without a venue list, different people might write the same location differently:
- "Pavilhao Municipal"
- "Pavilhão Municipal"
- "Pav. Municipal"
- "Municipal Pavilion"

This causes confusion. With venues, everyone selects from the same list.

**Venue Information:**
- Name (e.g., "Pavilhão Municipal")
- Address (full physical address)
- Capacity (informational - actual session capacity is what matters)
- Which sports use this venue (tags)

**Sport Tagging:**
When creating a Volleyball session, committee only sees venues tagged for Volleyball. This prevents accidentally booking a Padel court for a Volleyball session.

---

## Role Combinations

One person can have multiple roles:

| Combination | Valid? | Example |
|-------------|--------|---------|
| Athlete only | ✅ Yes | Maria - just plays Volleyball |
| Supporter only | ✅ Yes | Ana - watches her son play |
| Committee only | ✅ Yes | Carlos - manages schedules but doesn't play |
| Athlete + Committee | ✅ Yes | João - plays and helps manage sessions |
| Supporter + Committee | ✅ Yes | Rita - watches games and helps with admin |
| Athlete + Supporter | ❌ No | You're either participating or watching, not both |

**Why Athlete and Supporter are exclusive:**
These represent fundamentally different relationships with training sessions. If someone is an active athlete, they should use athlete features, not supporter features.

---

## Common Scenarios

### Scenario 1: Creating the Weekly Schedule

**Who:** Committee member with "Manage Sessions" and "Manage Calendar" privileges

**Steps:**
1. End of January: Unlock February 1-28 calendar for Volleyball
2. Click "Generate from Templates"
3. System creates all recurring sessions:
   - 4 Tuesday Advanced sessions (Feb 6, 13, 20, 27)
   - 4 Tuesday Intermediate sessions
   - 4 Sunday All Levels sessions
4. Review generated sessions, make any adjustments (e.g., Feb 13 venue changed)
5. Publish schedule - athletes can now confirm attendance

**Time saved:** 5 minutes instead of 30+ minutes of manual entry.

---

### Scenario 2: Athlete Confirms Attendance

**Who:** Maria (Athlete, Intermediate Volleyball)

**Steps:**
1. Maria opens the app, sees upcoming sessions
2. Sees "Tuesday February 6 - Advanced Volleyball (15/20 confirmed)"
3. Target is Advanced, but Maria is Intermediate - can she attend? **Yes!** Levels are guidelines.
4. Maria clicks "Confirm" - now shows (16/20 confirmed)
5. Tuesday morning: Maria gets sick, clicks "Cancel"
6. Slot freed immediately - now shows (15/20 confirmed)

---

### Scenario 3: Session Gets Cancelled

**Who:** Committee member (João)

**Steps:**
1. Tuesday morning: Venue calls - heating is broken, gym is closed
2. João (committee) marks Tuesday evening session as "Cancelled"
3. All 16 confirmed athletes receive notification
4. Athletes who try to confirm now see "Session Cancelled - cannot attend"
5. Session capacity slots are freed (not counted against limits)

---

### Scenario 4: Multi-Sport Athlete

**Who:** Pedro (plays Volleyball and Padel)

**Steps:**
1. Pedro creates account, joins as Athlete
2. Committee adds Pedro to Volleyball (Intermediate level)
3. Pedro also joins Padel sessions - committee adds Padel profile (Beginner level)
4. Pedro logs in and sees:
   - Tuesday Volleyball - Intermediate (matches your level ✓)
   - Wednesday Padel - Beginner (matches your level ✓)
   - Sunday Volleyball - Advanced (you can still attend if you want)
5. Pedro confirms for all three sessions with one login

---

### Scenario 5: Updating Athlete Levels

**Who:** Committee member with "Manage Athletes" privilege

**Steps:**
1. After 6 months, committee notices Maria has improved significantly
2. Committee updates Maria's Volleyball level: Intermediate → Advanced
3. System records change:
   - Old Level: Intermediate
   - New Level: Advanced
   - Changed By: João Silva
   - Date: February 15, 2024
   - Reason: "Consistent strong performance in intermediate sessions"
4. Maria now sees Advanced sessions highlighted as matching her level
5. History is permanently saved (cannot be deleted)

---

## Business Rules Summary

### Who Can Do What

| Action | Athlete | Supporter | Committee (with privilege) |
|--------|---------|-----------|---------------------------|
| View sessions | ✅ | ✅ | ✅ |
| Confirm attendance | ✅ | ❌ | ✅ (if also Athlete) |
| Create sessions | ❌ | ❌ | ✅ (Manage Sessions) |
| Unlock calendar | ❌ | ❌ | ✅ (Manage Calendar) |
| Update athlete levels | ❌ | ❌ | ✅ (Manage Athletes) |
| Add venues | ❌ | ❌ | ✅ (Manage Venues) |

### Session Capacity Rules

- **Enforced strictly** - cannot exceed maximum capacity
- **Real-time** - slots are taken/freed immediately
- **One per person** - cannot confirm twice for same session
- **Cancellation frees slots** - available immediately for others

### Calendar Rules

- **Locked by default** - must be explicitly unlocked
- **Sequential unlocking** - no gaps allowed
- **Maximum 3 months ahead** - prevents over-planning
- **Per-sport independence** - Volleyball and Padel have separate calendars
- **Permanent record** - unlock history cannot be deleted

### Attendance Rules

- **Athletes only** - Supporters cannot confirm
- **Not for cancelled sessions** - cannot attend cancelled sessions
- **Not for past sessions** - cannot confirm after session happened
- **Respect capacity** - cannot confirm if session is full
- **Level is a guideline** - target level doesn't restrict attendance

---

## Glossary

| Term | Meaning |
|------|---------|
| **Athlete** | Person who actively participates in training sessions |
| **Supporter** | Person who follows activities but doesn't participate |
| **Committee Member** | Volunteer who manages DouroBats operations |
| **Committee Admin** | Committee member with all five privileges, can assign privileges to others |
| **Session** | Scheduled training event (date, time, venue, sport) |
| **Template** | Recurring session pattern (e.g., "Every Tuesday at 8pm") |
| **Capacity** | Maximum number of people who can attend a session |
| **Target Level** | Recommended skill level for a session (not enforced) |
| **Venue** | Training location (gym, court, field) |
| **Confirm Attendance** | Athlete declares they will attend a session |
| **Calendar Access** | Permission to schedule sessions for specific dates |
| **Privilege** | Specific permission for committee members (e.g., Manage Sessions) |

---

## Questions for Review

As you review this system design, please consider:

1. **Roles & Permissions:**
   - Are the three roles (Athlete, Supporter, Committee) sufficient?
   - Are the five committee privileges appropriately granular?
   - Should any roles have additional capabilities?

2. **Session Management:**
   - Is the capacity enforcement approach correct?
   - Should target levels restrict attendance, or remain as guidelines?
   - Are session statuses (Scheduled, Cancelled, Completed) sufficient?

3. **Calendar Control:**
   - Is the 3-month maximum appropriate?
   - Should sequential unlocking be enforced, or allow gaps?
   - Should calendar access work differently?

4. **Athlete Levels:**
   - Are three levels (Beginner, Intermediate, Advanced) enough?
   - Should levels affect what sessions athletes can attend?
   - How often should levels be reviewed/updated?

5. **Multi-Sport Support:**
   - Does the current approach (one account, multiple sport profiles) work for your needs?
   - Should athletes need separate accounts per sport?

6. **Templates:**
   - Is the template system sufficient for your scheduling needs?
   - Should templates support more complex patterns (bi-weekly, monthly)?

7. **Missing Features:**
   - What important capabilities are missing?
   - What workflows are not covered?
   - What business rules need clarification?

---

## Next Steps

Please review this document and provide feedback on:
- ✅ What works well and matches your expectations
- ⚠️ What needs clarification or seems confusing
- ❌ What doesn't match how DouroBats actually operates
- 💡 What's missing or should be added

Your feedback will help us refine the system design before implementation begins.
