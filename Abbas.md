# University ERP – Simple Deep Explanation (For Demo)

## What this project does
- Helps a university manage courses, sections, students, instructors, and grades.
- Three roles: Admin, Instructor, Student.
- Secure login with hashed passwords and account lockout on too many wrong attempts.

## How the system is organized (Layers)
- UI (what you see) → API (receives requests) → Service (business rules) → Data/Repository (SQL).
- UI never talks to the database directly. It always goes through API → Service → Repository.
- Benefit: cleaner code, easier to test, safer and more maintainable.

## Databases (Two DBs for security)
- erp_auth: only login stuff (username, role, password hash, lockout info).
- erp_main: actual ERP data (students, instructors, courses, sections, enrollments, grades, settings).
- Why two? Passwords live separately and more securely.

## High-level flow when you log in
1) User enters username/password in the Login screen (UI).
2) UI calls the API → Service checks `erp_auth.users_auth`, compares bcrypt hash.
3) If too many wrong attempts, account is temporarily locked.
4) On success, we learn the user’s role and open the correct dashboard.

## What each role can do
- Student:
  - See course catalog, register/drop sections, view timetable and grades, download transcript (CSV/PDF).
- Instructor:
  - See only own sections, add grade components, enter/update scores, view class statistics, export grades (CSV).
- Admin:
  - Manage users, courses, sections; assign instructors; enable/disable maintenance mode; backup/restore databases.

## Maintenance mode (read-only)
- Admin can switch the system to maintenance.
- When on: students/instructors can only view data; all “write” actions are blocked with a clear message.
- This is checked centrally before any change is saved.

## How a typical action travels through layers
Example: Student registers for a section
1) UI: Student clicks “Register”.
2) API: Receives a request like “register(studentId, sectionId)”.
3) Service:
   - Checks maintenance mode (block if ON).
   - Validates “not already enrolled”.
   - Checks capacity > 0.
   - If valid, asks Repository to insert enrollment and update seats.
4) Repository:
   - Runs the SQL against `erp_main` safely (prepared statements).
5) Result:
   - Service returns success/failure → API → UI shows a message and updates the table.

## Grades and final grade calculation
- Each section can have multiple components (Quiz, Midterm, Final), each with a weight.
- Final grade % = sum over components of (score/max_score × weight).
- UI shows both percentage and letter grade.

## Exports
- Student transcript: CSV and PDF.
- Instructor grades: CSV export for sections they teach.

## Backup/Restore (Admin)
- From the UI, Admin can back up both databases and restore them later.
- Useful before big changes or maintenance.

## Where important code lives (quick map)
- `ui/` – all screens and panels (Login, Dashboards, Course/Section/Grades UIs).
- `api/` – endpoints called by UI (one per feature area).
- `service/` – business rules (validations, role checks, maintenance checks, calculations).
- `data/repository/` – SQL queries to databases (no UI logic here).
- `auth/` – login logic, bcrypt hashing, lockout handling.
- `util/MaintenanceManager` – global maintenance flag checks.
- `database/*.sql` – scripts to create schema and seed sample data.

## How to explain it in the demo (short version)
- “We built a layered ERP: UI → API → Service → Repository → Database.”
- “Two databases: one for logins (secure) and one for ERP data.”
- “Each action (like register or grade entry) goes UI → API → Service checks → Repository SQL → back to UI.”
- “Maintenance mode turns the system read-only for safe updates.”
- “We use bcrypt for passwords, lockout after many failed logins, and role-based access everywhere.”

If you remember just three things:
1) Clean layers: UI never hits DB directly.
2) Two databases: security for passwords vs. ERP data.
3) Every important operation has validations in the Service layer, then safe SQL in the Repository.


