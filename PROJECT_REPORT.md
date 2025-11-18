# University ERP System - Project Report

**Project Name**: University ERP (Java + Swing)  
**Course**: CSE201 - Advanced Programming - M2025  
**Team Members**: [Your Name/Team]  
**Date**: _______________

---

## 1. Executive Summary

This report describes the implementation of a University Enterprise Resource Planning (ERP) system built using Java Swing. The system manages courses, sections, student enrollments, and instructor grade entry with role-based access control for three user types: Students, Instructors, and Administrators.

**Key Achievements:**
- Complete implementation of all required features
- Two-database architecture for security (Auth DB + ERP DB)
- Password hashing using bcrypt
- Maintenance mode for system updates
- Backup and restore functionality
- PDF and CSV export capabilities
- Login lockout feature (bonus)

---

## 2. System Architecture

### 2.1 Database Design

The system uses a two-database architecture:

**erp_auth Database:**
- `users_auth` table stores authentication information
- Contains: username, role, password_hash, status, failed_attempts, locked_until
- Passwords stored as bcrypt hashes only

**erp_main Database:**
- `students`, `instructors` - User profiles
- `courses`, `sections` - Academic structure
- `enrollments` - Student registrations
- `grades` - Assessment scores and final grades
- `settings` - System configuration (maintenance mode)

**Key Design Decisions:**
- Separate `day` and `time` columns in sections table for flexible scheduling
- Foreign key constraints ensure data integrity
- UNIQUE constraints prevent duplicate enrollments
- Cascade deletes maintain referential integrity

### 2.2 Package Structure

```
edu.univ.erp/
├── ui/          - All Swing panels and windows (22 panels)
├── auth/        - Authentication service with lockout
├── data/        - Database configuration and connection pooling
├── util/        - Utility classes (MaintenanceManager)
└── App.java     - Main entry point
```

**Architecture Note**: The system follows a layered architecture: UI → API → Service → Data. UI panels never access the database directly. All UI actions call the API layer, which delegates to Services for business logic, and Repositories handle data access via `DatabaseConfig`. This enforces separation of concerns, improves testability, and matches the project specification. See `ARCHITECTURE_DECISION.md` for the full rationale.

### 2.3 Technology Stack

- **UI Framework**: Java Swing with FlatLaf modern look & feel
- **Database**: MySQL 8.0+ with HikariCP connection pooling
- **Password Security**: jBCrypt for password hashing
- **Export**: iText for PDF, built-in PrintWriter for CSV
- **Build Tool**: Maven

---

## 3. Feature Implementation

### 3.1 Student Features

**Course Catalog**: Displays all available courses with code, title, credits, instructor, and capacity information.

**Registration System**: 
- Students can register for sections with available seats
- Duplicate enrollment prevention
- Capacity checking
- Real-time seat availability updates

**Timetable**: Shows enrolled sections with day, time, and room information in a clear table format.

**Grade Viewing**: 
- Displays grade components (quiz, midterm, final, etc.)
- Calculates final grade using weighted formula
- Shows percentage and letter grade

**Transcript Export**: 
- CSV export with all course grades
- PDF export with formatted transcript layout
- Includes student information and calculated grades

### 3.2 Instructor Features

**Section Management**: Instructors see only their assigned sections.

**Grade Entry System**:
- Live grade entry with real-time updates
- Add/delete grade components for entire section
- Per-student score entry
- Automatic final grade calculation

**Class Statistics**: Displays averages, minimum, and maximum scores for each grade component.

**CSV Export**: Export all grades for all students in instructor's sections.

### 3.3 Admin Features

**User Management**: 
- Add/edit/delete users (Students, Instructors, Admins)
- Role-specific profile creation
- Password hashing on user creation

**Course & Section Management**:
- Create, edit, delete courses
- Create sections with scheduling information
- Assign instructors to sections

**Maintenance Mode**:
- Toggle system-wide read-only mode
- Displays banner to all users
- Blocks all student/instructor write operations
- Useful for system updates

**Backup/Restore**:
- Backup both erp_auth and erp_main databases
- Restore from backup files
- File chooser interface for easy operation

### 3.4 Security Features

**Authentication**:
- Password hashing with bcrypt (no plaintext storage)
- Two-database separation (auth vs. ERP data)
- Login lockout after 5 failed attempts (15-minute lockout)

**Access Control**:
- Role-based access (Student, Instructor, Admin)
- Query-level filtering ensures users see only their data
- Maintenance mode enforces read-only access

---

## 4. Final Grade Calculation Rule

The system uses a **weighted average** formula for calculating final grades:

```
Final Grade (%) = Σ(score_i / max_score_i × weight_i)

Where:
- score_i = student's score for component i
- max_score_i = maximum possible score for component i  
- weight_i = weight percentage for component i (e.g., 10, 30, 50)
```

**Example:**
- Quiz 1: 18/20 (weight 10%) → contribution = (18/20) × 10 = 9.0%
- Midterm: 85/100 (weight 30%) → contribution = (85/100) × 30 = 25.5%
- Final: 92/100 (weight 50%) → contribution = (92/100) × 50 = 46.0%
- **Final Grade = 9.0 + 25.5 + 46.0 = 80.5%**

**Letter Grade Mapping:**
- A: 90% and above
- B: 80-89%
- C: 70-79%
- D: 60-69%
- F: Below 60%

This rule applies consistently across all courses and all students.

---

## 5. Role & Maintenance Enforcement

### 5.1 Role-Based Access

**Implementation Strategy:**
1. **Login**: User authenticated via AuthService, role retrieved
2. **Dashboard Selection**: Appropriate dashboard opened based on role
3. **Query Filtering**: All database queries filter by user_id or instructor_id
4. **UI Visibility**: Menu items and panels shown based on role

**Examples:**
- Students: Queries include `WHERE student_id = ?`
- Instructors: Queries include `WHERE instructor_id = ?`
- Admins: Full access with no filtering

### 5.2 Maintenance Mode Enforcement

**Implementation:**
1. **Flag Storage**: Maintenance mode stored in `settings` table
2. **Check Before Writes**: `MaintenanceManager.isMaintenanceModeOn()` called before any data modification
3. **Banner Display**: `MaintenanceBanner` component checks mode and displays warning
4. **Operation Blocking**: All write operations check maintenance mode:
   - Student: Register, Drop
   - Instructor: Save Grades, Add Component, Delete Component
   - Admin: Can still toggle maintenance mode

**Code Pattern:**
```java
if (MaintenanceManager.isMaintenanceModeOn()) {
    JOptionPane.showMessageDialog(this, 
        "Operation disabled. Maintenance mode is active.");
    return;
}
// Proceed with operation
```

---

## 6. Database Schema

### 6.1 Table Structures

See `database/01_schema_auth.sql` and `database/02_schema_main.sql` for complete schemas.

**Key Relationships:**
- `students.user_id` → `users_auth.user_id`
- `instructors.user_id` → `users_auth.user_id`
- `sections.course_id` → `courses.course_id`
- `sections.instructor_id` → `instructors.user_id`
- `enrollments.student_id` → `students.user_id`
- `enrollments.section_id` → `sections.section_id`
- `grades.enrollment_id` → `enrollments.enrollment_id`

### 6.2 Data Integrity Measures

1. **Foreign Keys**: Enforce referential integrity
2. **UNIQUE Constraints**: 
   - Username uniqueness
   - Duplicate enrollment prevention (`unique_enrollment` key)
   - Section uniqueness (course + section_code + semester + year)
3. **ENUM Types**: Ensure valid status values
4. **NOT NULL Constraints**: Required fields enforced

---

## 7. Extras Implemented

### 7.1 Bonus Features

1. **PDF Export**: Professional transcript formatting using iText
2. **Login Lockout**: Account locks after 5 failed attempts (15 minutes)
3. **Change Password**: All users can change their passwords
4. **Backup/Restore**: Full database backup and restore functionality

### 7.2 Additional Enhancements

1. **Modern UI**: FlatLaf look and feel for better appearance
2. **Real-time Refresh**: Panels refresh when shown
3. **Error Handling**: User-friendly error messages throughout
4. **Validation**: Input validation for capacity, required fields, etc.

---

## 8. Testing

### 8.1 Test Approach

Comprehensive testing performed using the test plan in `TEST_PLAN.md`:
- Functional testing for all features
- Edge case testing (full sections, duplicates, etc.)
- Security testing (access control, password hashing)
- Performance testing (load times, responsiveness)

### 8.2 Test Results

See `TEST_SUMMARY.md` for detailed results.

**Summary:**
- All core features tested and verified
- Acceptance tests passed
- Edge cases handled appropriately
- Performance acceptable

---

## 9. Screenshots

[Include screenshots of:]
1. Login screen
2. Student dashboard
3. Course catalog
4. Grade entry interface
5. Admin dashboard
6. Maintenance mode banner
7. Transcript (PDF) example
8. Backup/Restore interface

---

## 10. Challenges & Solutions

### Challenge 1: Database Schema Mismatch
**Issue**: Initial schema used `day_time` but code expected `day` and `time` separately  
**Solution**: Updated schema to match code expectations

### Challenge 2: Real-time Data Updates
**Issue**: Data could become stale when switching between panels  
**Solution**: Added component listeners to refresh data when panels become visible

### Challenge 3: Maintenance Mode Enforcement
**Issue**: Ensuring all write operations check maintenance mode  
**Solution**: Centralized `MaintenanceManager` class with consistent checking pattern

### Challenge 4: PDF Export Formatting
**Issue**: Creating professional-looking PDF transcripts  
**Solution**: Used iText library with proper table formatting and styling

---

## 11. Future Enhancements

Potential improvements for production system:
1. Add prerequisite checking for course registration
2. Implement credit limit enforcement
3. Add attendance tracking
4. Email notifications
5. Audit logging for administrative actions
6. Report generation (attendance, enrollment statistics)

---

## 12. Conclusion

The University ERP system successfully implements all required features with proper security measures, role-based access control, and user-friendly interfaces. The two-database architecture ensures password security while maintaining separation of concerns. All acceptance tests pass, and the system is ready for demonstration.

**Key Strengths:**
- Complete feature implementation
- Security-first design
- Intuitive user interface
- Robust error handling
- Comprehensive testing

---

## Appendix

### A. File Structure
```
ERP_Project/
├── src/main/java/edu/univ/erp/
│   ├── ui/          (22 panels/windows)
│   ├── auth/        (AuthService)
│   ├── data/        (DatabaseConfig)
│   └── util/        (MaintenanceManager)
├── database/        (SQL schema files)
├── pom.xml          (Maven dependencies)
└── Documentation files
```


