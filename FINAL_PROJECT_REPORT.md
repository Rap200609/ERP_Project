# University ERP System – Final Project Report

**Project Name:** University ERP System  
**Date:** November 26, 2025  
**Version:** 1.0  
**Team:** Solo Development  

---

## Table of Contents

1. [Executive Summary](#executive-summary)
2. [Final Grade Weighting Rule](#final-grade-weighting-rule)
3. [Role-Based Access Control & Enforcement](#role-based-access-control--enforcement)
4. [Maintenance Mode Implementation](#maintenance-mode-implementation)
5. [Database Schema Overview](#database-schema-overview)
6. [Additional Features & Enhancements](#additional-features--enhancements)
7. [Conclusion](#conclusion)

---

## Executive Summary

The University ERP System is a comprehensive Java-based enterprise resource planning application designed to manage student enrollments, grade tracking, instructor assignments, and administrative functions for a university. The system employs a two-database architecture with role-based access control, real-time grade calculation, and a maintenance mode for system updates.

**Key Statistics:**
- **Total Database Tables:** 15 (auth DB: 2, main DB: 13)
- **Role Types:** 3 (Student, Instructor, Admin)
- **Major Features:** Course Management, Section Enrollment, Grade Entry & Export, User Management, Maintenance Mode, Backup/Restore
- **Technology Stack:** Java 8+, Swing GUI, MySQL 8.0+, Maven

---

## Final Grade Weighting Rule

### Formula

The system calculates the final grade percentage using a **weighted average** of all grade components:

$$\text{Final Percentage} = \frac{\sum_{i=1}^{n} \left(\frac{\text{Score}_i}{\text{MaxScore}_i}\right) \times \text{Weight}_i}{\sum_{i=1}^{n} \text{Weight}_i} \times 100$$

### Components

- **Score_i**: Student's earned score on component i
- **MaxScore_i**: Maximum possible score for component i
- **Weight_i**: Weight assigned to component i (typically 0–100)
- **n**: Total number of grade components for a section

### Implementation Details

**File:** `GradeComponent.java`

```java
public double getWeightedContribution() {
    return maxScore > 0 ? (score / maxScore) * weight : 0.0;
}
```

**Grade Aggregation:** `GradeService.java`

```java
double totalWeight = components.stream().mapToDouble(GradeComponent::getWeight).sum();
double weightedScore = components.stream().mapToDouble(GradeComponent::getWeightedContribution).sum();
// Final percentage = (weightedScore / totalWeight) * 100
```

### Example Calculation

| Component | Score | Max Score | Weight | Percentage | Weighted Contribution |
|-----------|-------|-----------|--------|------------|----------------------|
| Assignment | 18 | 20 | 20 | 90% | 18 |
| Midterm | 24 | 30 | 30 | 80% | 24 |
| Final Exam | 42 | 50 | 50 | 84% | 42 |
| **Total** | **84** | **100** | **100** | — | **84** |

**Final Grade:** 84 % → Letter Grade: **B** ( $\geq$ 80% and $\lt$ 90%)

### Letter Grade Mapping

| Percentage Range | Letter Grade |
|------------------|-------------|
| $\geq$ 90% | A |
| $\geq$ 80% and $\lt$ 90% | B |
| $\geq$ 70% and $\lt$ 80% | C |
| $\geq$ 60% and $\lt$ 70% | D |
| $\lt$ 60% | F |

**Key Features:**
- **Flexible weights:** Weights need not sum to 100 (system normalizes by dividing by total weight)
- **Zero handling:** Components with `maxScore = 0` contribute 0 to weighted score
- **Real-time calculation:** Grades recalculate dynamically as scores are entered
- **Export consistency:** CSV and PDF exports use the same formula

---

## Role-Based Access Control & Enforcement

### Three-Tier Role Architecture

The system enforces three distinct roles with specific permissions and dashboards:

#### 1. **Student Role**

**Capabilities:**
- View course catalog
- Register for course sections (respecting capacity, prerequisites, constraints)
- Withdraw from sections (before drop deadline)
- View personal timetable
- View grades and grade components
- Download transcript (CSV/PDF)
- View student-specific data only

**Database Access:**
- Read-only access to `Course`, `CourseCatalogEntry`, `Section`, `SectionAvailability`
- Full access to own `EnrolledSection`, `TimetableEntry`, `TranscriptRow`
- No access to other students' data

**Authentication & Authorization:**
- User logs in via `LoginFrame` with username/password
- Credentials validated against `erp_auth.users_auth` (bcrypt hashed)
- User role retrieved from `erp_main.user_account` (mapped by `user_id`)
- Upon successful login, `StudentDashboard` displayed with role-specific menu items

**File:** `StudentApi.java`
```java
public ApiResponse enrollSection(int studentId, int sectionId) {
    // Checks: maintenance mode, capacity, prerequisites, conflicts
    // Creates EnrolledSection record, updates SectionAvailability
}
```

#### 2. **Instructor Role**

**Capabilities:**
- View assigned sections only
- Enter and edit grades for own sections
- Add grade components to sections
- View class statistics (average, min, max per component)
- Export grades as CSV
- Cannot create courses, sections, or manage other instructors' sections

**Database Access:**
- Read-only access to `Course`, `Section`, `StudentProfile` (for own sections)
- Full access to `GradeComponent`, grade scores (for own sections only)
- `SectionAssignment` enforces ownership

**Enforcement Mechanism:**
```java
// File: InstructorApi.java
public List<Section> loadOwnSections(int instructorId) {
    return sectionRepository.findByInstructorId(instructorId);
}

public ApiResponse submitGrades(int instructorId, int sectionId, List<GradeComponent> grades) {
    // 1. Verify instructor is assigned to section
    SectionAssignment assignment = findAssignment(instructorId, sectionId);
    if (assignment == null) {
        throw new UnauthorizedException("Not assigned to this section");
    }
    // 2. Persist grades (only for this section)
}
```

#### 3. **Admin Role**

**Capabilities:**
- Full CRUD access to all resources
- Manage users (create, edit, delete students/instructors)
- Create and edit courses
- Create and edit sections
- Assign instructors to sections
- Set drop deadlines
- Toggle maintenance mode
- Backup and restore databases
- Run maintenance tasks

**Database Access:**
- Full access to both `erp_auth` and `erp_main` databases
- Can insert/update/delete any record

**Administrative Dashboard:**
```java
// File: AdminApi.java
public ApiResponse addUser(AddUserCommand cmd) {
    // 1. Insert into erp_auth.users_auth
    // 2. Insert into erp_main.user_account
    // 3. Insert role-specific profile (StudentProfile / InstructorProfile)
}

public ApiResponse toggleMaintenanceMode(boolean enabled) {
    // Sets global flag, persisted to database
}
```

### Authorization Enforcement Layers

#### Layer 1: API Request Validation
```java
// All API endpoints check caller's role before processing
public ApiResponse viewGrades(int userId, int sectionId) {
    UserAccount caller = getCurrentUser(userId);
    if (!caller.getRole().equals("INSTRUCTOR")) {
        return error("Only instructors can view grades");
    }
    // ... proceed
}
```

#### Layer 2: Database-Level Filtering
```java
// Queries automatically filtered by ownership
public List<Section> getMyAssignedSections(int instructorId) {
    return db.query(
        "SELECT * FROM section WHERE section_id IN " +
        "(SELECT section_id FROM section_assignment WHERE instructor_id = ?)",
        instructorId
    );
}
```

#### Layer 3: UI-Level Restrictions
```java
// File: InstructorDashboard.java
// Only show menu items for instructor's own sections
// Grade entry panel validates section ownership before loading
```

---

## Maintenance Mode Implementation

### Purpose & Design

Maintenance Mode is a system-wide toggle that:
- **Disables all write operations** for students and instructors
- **Allows read-only access** (view timetable, grades, etc.)
- **Permits admin operations** (backups, migrations, cleanup tasks)
- **Displays prominent warning banner** to non-admin users

### Implementation

**Manager Class:** `MaintenanceManager.java`
```java
public class MaintenanceManager {
    private static boolean maintenanceMode = false;
    
    public static synchronized boolean isMaintenance() {
        return maintenanceMode;
    }
    
    public static synchronized void setMaintenance(boolean enabled) {
        maintenanceMode = enabled;
        // Persist to database: erp_main.maintenance_flag
        persistToDatabase(enabled);
    }
}
```

**Enforcement Points:**

1. **Enrollment Operations**
```java
// File: SectionService.java
public ApiResponse enrollStudent(int studentId, int sectionId) {
    if (MaintenanceManager.isMaintenance()) {
        return ApiResponse.error("503", "Maintenance mode is active. Try again later.");
    }
    // ... proceed with enrollment
}
```

2. **Grade Entry**
```java
// File: InstructorApi.java
public ApiResponse submitGrades(int instructorId, int sectionId, List<GradeComponent> grades) {
    if (MaintenanceManager.isMaintenance()) {
        return ApiResponse.error("503", "System in maintenance. Grades cannot be modified now.");
    }
    // ... proceed with grade entry
}
```

3. **UI Banner Display**
```java
// File: MaintenanceBanner.java & InstructorDashboard.java
if (MaintenanceManager.isMaintenance()) {
    banner.setText("⚠ SYSTEM MAINTENANCE IN PROGRESS ⚠");
    banner.setBackground(Color.YELLOW);
    disableWriteButtons(true); // Disable Register, Drop, Save Grades, etc.
}
```

### Admin Controls

**File:** `AdminMaintenanceApi.java`
```java
public ApiResponse toggleMaintenance(boolean enabled) {
    MaintenanceManager.setMaintenance(enabled);
    return ApiResponse.success("Maintenance mode " + (enabled ? "enabled" : "disabled"));
}
```

**Admin Panel:** `MaintenanceModePanel.java`
- Shows current maintenance status
- Buttons to enable/disable with confirmation dialog
- Logs all maintenance toggles

### User Experience

| Mode | Student View | Instructor View | Admin View |
|------|--------------|-----------------|-----------|
| **Normal** | Can enroll, drop, view grades | Can enter grades, export | Full access |
| **Maintenance** | Read-only with warning banner | Read-only with warning banner | Full write access |

---

## Database Schema Overview

### Database Architecture

The system uses **two-database separation** for security and scalability:
- **`erp_auth`:** Authentication & authorization (passwords, credentials)
- **`erp_main`:** Business logic (courses, enrollments, grades, users)

### Authentication Database: `erp_auth`

#### Table: `users_auth`
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `user_id` | INT | PRIMARY | Unique user identifier (linked to erp_main) |
| `username` | VARCHAR(100) | UNIQUE | Login username |
| `password_hash` | VARCHAR(255) | — | bcrypt hashed password |
| `created_at` | TIMESTAMP | — | Account creation timestamp |
| `updated_at` | TIMESTAMP | — | Last password change |
| `failed_login_attempts` | INT | — | Failed login counter (for lockout feature) |
| `locked_until` | TIMESTAMP | — | Account lockout expiration time |

#### Table: `password_history` (Audit Trail)
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `history_id` | INT | PRIMARY | Unique record ID |
| `user_id` | INT | FOREIGN | Reference to user_id |
| `old_password_hash` | VARCHAR(255) | — | Previous password (hashed) |
| `changed_at` | TIMESTAMP | — | When password was changed |

### Main ERP Database: `erp_main`

#### 1. User Management Tables

**Table: `user_account`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `user_id` | INT | PRIMARY | Unique user (links to erp_auth) |
| `role` | ENUM('STUDENT','INSTRUCTOR','ADMIN') | — | User role |
| `status` | ENUM('ACTIVE','INACTIVE','SUSPENDED') | — | Account status |
| `created_at` | TIMESTAMP | — | Creation time |

**Table: `student_profile`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `student_id` | INT | PRIMARY/FOREIGN | Links to user_id |
| `roll_no` | VARCHAR(50) | UNIQUE | Student roll number |
| `program` | VARCHAR(100) | — | Program/major (e.g., "CS", "ENG") |
| `year` | INT | — | Current academic year (1–4) |
| `email` | VARCHAR(100) | — | Student email |

**Table: `instructor_profile`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `instructor_id` | INT | PRIMARY/FOREIGN | Links to user_id |
| `employee_id` | VARCHAR(50) | UNIQUE | Instructor employee ID |
| `department` | VARCHAR(100) | — | Department (e.g., "CSE", "MTH") |
| `email` | VARCHAR(100) | — | Instructor email |

#### 2. Course & Section Tables

**Table: `course`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `course_id` | INT | PRIMARY | Unique course |
| `course_code` | VARCHAR(50) | UNIQUE | Course code (e.g., "CSE101") |
| `course_title` | VARCHAR(255) | — | Course title |
| `credits` | INT | — | Credit hours |
| `description` | TEXT | — | Course description |

**Table: `course_catalog_entry`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `catalog_id` | INT | PRIMARY | Unique catalog entry |
| `course_id` | INT | FOREIGN | Reference to course |
| `term` | VARCHAR(50) | — | Academic term (e.g., "Fall 2025") |
| `description` | TEXT | — | Term-specific description |

**Table: `section`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `section_id` | INT | PRIMARY | Unique section |
| `course_id` | INT | FOREIGN | Reference to course |
| `section_code` | VARCHAR(50) | UNIQUE | Section identifier (e.g., "A", "B") |
| `term` | VARCHAR(50) | — | Academic term |
| `capacity` | INT | — | Max students allowed |
| `room` | VARCHAR(50) | — | Room/location |
| `day` | VARCHAR(20) | — | Meeting day (e.g., "MWF") |
| `time` | VARCHAR(10) | — | Meeting time (e.g., "09:00-10:30") |

**Table: `section_availability`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `availability_id` | INT | PRIMARY | Unique record |
| `section_id` | INT | FOREIGN | Reference to section |
| `seats_available` | INT | — | Current available seats |
| `last_updated` | TIMESTAMP | — | Last update timestamp |

**Table: `section_assignment`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `assignment_id` | INT | PRIMARY | Unique assignment |
| `section_id` | INT | FOREIGN | Reference to section |
| `instructor_id` | INT | FOREIGN | Reference to instructor_profile |
| `assigned_at` | TIMESTAMP | — | Assignment timestamp |

#### 3. Enrollment & Grading Tables

**Table: `enrolled_section`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `enrollment_id` | INT | PRIMARY | Unique enrollment |
| `section_id` | INT | FOREIGN | Reference to section |
| `student_id` | INT | FOREIGN | Reference to student_profile |
| `enrollment_status` | ENUM('ENROLLED','DROPPED','WITHDRAWN') | — | Current status |
| `enrolled_at` | TIMESTAMP | — | Enrollment timestamp |
| `final_grade` | DECIMAL(5,2) | — | Final calculated percentage |
| `grade_finalized` | BOOLEAN | — | Whether grades are locked |

**Table: `grade_component`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `component_id` | INT | PRIMARY | Unique component |
| `section_id` | INT | FOREIGN | Reference to section |
| `component_name` | VARCHAR(100) | — | Name (e.g., "Assignment 1", "Midterm") |
| `max_score` | DECIMAL(10,2) | — | Maximum possible points |
| `weight` | DECIMAL(5,2) | — | Weight in final grade (0–100+) |
| `created_at` | TIMESTAMP | — | Component creation time |

**Table: `grade_entry`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `grade_entry_id` | INT | PRIMARY | Unique grade entry |
| `enrollment_id` | INT | FOREIGN | Reference to enrollment |
| `component_id` | INT | FOREIGN | Reference to component |
| `score` | DECIMAL(10,2) | — | Student's score |
| `entered_at` | TIMESTAMP | — | When score was entered |
| `entered_by` | INT | FOREIGN | Instructor who entered |

**Table: `transcript_row`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `transcript_id` | INT | PRIMARY | Unique transcript entry |
| `student_id` | INT | FOREIGN | Reference to student_profile |
| `course_id` | INT | FOREIGN | Reference to course |
| `section_id` | INT | FOREIGN | Reference to section |
| `term` | VARCHAR(50) | — | Academic term |
| `final_percentage` | DECIMAL(5,2) | — | Final grade percentage |
| `letter_grade` | CHAR(1) | — | Letter grade (A–F) |
| `created_at` | TIMESTAMP | — | When transcript was finalized |

#### 4. Administrative Tables

**Table: `drop_deadline`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `deadline_id` | INT | PRIMARY | Unique deadline record |
| `term` | VARCHAR(50) | — | Academic term |
| `drop_deadline` | DATE | — | Last day to drop without W |
| `set_by_admin` | INT | FOREIGN | Admin who set deadline |
| `created_at` | TIMESTAMP | — | When deadline was set |

**Table: `maintenance_flag`**
| Column | Type | Key | Description |
|--------|------|-----|-------------|
| `flag_id` | INT | PRIMARY | Unique flag record |
| `is_maintenance` | BOOLEAN | — | Current maintenance mode state |
| `toggled_by` | INT | FOREIGN | Admin who toggled |
| `toggled_at` | TIMESTAMP | — | When toggle occurred |
| `reason` | TEXT | — | Reason for maintenance (optional) |

### Key Relationships

```
user_account (1) ──→ (1) student_profile
user_account (1) ──→ (1) instructor_profile

course (1) ──→ (*) course_catalog_entry
course (1) ──→ (*) section

section (1) ──→ (*) section_availability
section (1) ──→ (*) section_assignment
section (1) ──→ (*) grade_component
section (1) ──→ (*) enrolled_section

instructor_profile (1) ──→ (*) section_assignment
student_profile (1) ──→ (*) enrolled_section
student_profile (1) ──→ (*) transcript_row

enrolled_section (1) ──→ (*) grade_entry
grade_component (1) ──→ (*) grade_entry
```

---

## Additional Features & Enhancements

### 1. **Two-Database Separation (Security)**
- **Purpose:** Isolate authentication credentials from business data
- **Implementation:** 
  - `erp_auth`: Contains only passwords (bcrypt hashed), usernames, login audit
  - `erp_main`: Contains all business logic, no passwords
  - Queries link databases via `user_id`
- **Benefit:** If main DB is breached, passwords remain secure in separate auth DB

### 2. **Bcrypt Password Hashing**

- No plaintext passwords ever stored or transmitted
- File: `AuthService.java`
```java
public static String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt(10));
}
```

### 3. **Login Lockout Feature**
- Failed login attempts tracked (`failed_login_attempts`)
- After 5 failed attempts: account locked for 15 minutes
- Prevents brute-force attacks
- File: `LoginFrame.java`

### 4. **Real-Time Grade Calculation**
- Grades calculated dynamically as scores are entered
- No need to manually finalize (though option exists)
- Weighted average formula ensures consistency across exports
- CSV and PDF exports use identical calculation

### 5. **Grade Export (CSV & PDF)**
- **CSV:** For data analysis, easy to import to spreadsheets
- **PDF:** Professional transcript format with student info
- Both use same grade calculation formula
- File: `GradesExportPanel.java`, `TranscriptService.java`

### 6. **Backup & Restore System**
- Admin can backup both `erp_auth` and `erp_main` databases
- Backup files: SQL dumps with full schema and data
- Restore functionality for disaster recovery
- File: `AdminBackupApi.java`

### 7. **Audit Logging**
- All administrative actions logged (user creation, section edits, maintenance toggles)
- Tracks who, what, when
- File: `audit_log` table, logged in API endpoints

### 8. **Drop Deadline Management**
- Admins set term-specific drop deadlines
- Students can drop before deadline without grade impact
- Enforced in enrollment service
- File: `DropDeadlinePanel.java`

### 9. **UI Theme System**
- Consistent color scheme across all panels
- Professional appearance with custom styling
- Accessibility: clear labels and button text
- File: `UITheme.java`

### 10. **Responsive Role-Based UI**
- Each role sees only relevant menu items
- Dashboards dynamically hide/show components
- Students cannot see instructor/admin panels
- File: `StudentDashboard.java`, `InstructorDashboard.java`, `AdminDashboard.java`

### 11. **Capacity & Availability Tracking**
- Seats decrement in real-time on enrollment
- Increment on withdrawal
- Prevents overbooking
- File: `SectionAvailability` table, `SectionService.java`

### 12. **Timetable View**
- Students see visual/tabular timetable of enrolled sections
- Includes room, time, instructor
- Helps detect schedule conflicts (checked on enrollment)
- File: `StudentTimetablePanel.java`
