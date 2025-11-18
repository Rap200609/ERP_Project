# Test Plan - University ERP System

## Test Environment

- **Java Version**: JDK 8 or higher
- **MySQL Version**: 8.0 or higher
- **Operating System**: Windows 10/11
- **Test Accounts**: See Database Setup section

## Test Accounts

| Username | Password | Role | Purpose |
|----------|----------|------|---------|
| admin | admin123 | Admin | Full system access testing |
| inst1 | inst1_1 | Instructor | Instructor features testing |
| stu1 | stu1_1 | Student | Student features testing |
| stu2 | stu2_2 | Student | Additional student testing |

## Test Data

The seed data (`database/03_seed_data.sql`) includes:
- 1 Admin user
- 1 Instructor user  
- 2 Student users
- 5 Sample courses (CSE101, CSE201, CSE301, MTH301, ENG101)
- 7 Sample sections
- Sample enrollments for stu1 and stu2
- Sample grades for testing grade calculations

---

## A. Login & Roles Tests

### Test Case L1: Successful Login
**Steps:**
1. Launch application
2. Enter username: `admin`
3. Enter password: `admin`
4. Click "Login"

**Expected Result:**
- ✅ Login successful message displayed
- ✅ Admin Dashboard opens with correct menu items
- ✅ User can see admin-specific features

### Test Case L2: Wrong Password Rejected
**Steps:**
1. Launch application
2. Enter username: `admin`
3. Enter password: `wrongpassword`
4. Click "Login"

**Expected Result:**
- ✅ Error message: "Incorrect username or password."
- ✅ Login fails
- ✅ Remains on login screen

### Test Case L3: Wrong Username Rejected
**Steps:**
1. Launch application
2. Enter username: `nonexistent`
3. Enter password: `anypassword`
4. Click "Login"

**Expected Result:**
- ✅ Error message: "Incorrect username or password." (same message for security)
- ✅ Login fails

### Test Case L4: Role-Based Dashboards
**Steps:**
1. Login as `admin` → Verify Admin Dashboard opens
2. Logout, login as `inst1` → Verify Instructor Dashboard opens
3. Logout, login as `stu1` → Verify Student Dashboard opens

**Expected Result:**
- ✅ Each role sees appropriate dashboard
- ✅ Menu items match role capabilities
- ✅ Role-specific panels are accessible

### Test Case L5: Login Lockout (Bonus Feature)
**Steps:**
1. Login with wrong password 5 times for `admin`
2. Try to login again with correct password

**Expected Result:**
- ✅ After 5 failed attempts: "Too many failed login attempts. Account locked for 15 minutes."
- ✅ Cannot login even with correct password during lockout period
- ✅ After 15 minutes, can login successfully

---

## B. Student Tests

### Test Case S1: View Course Catalog
**Steps:**
1. Login as `stu1`
2. Navigate to "Course Catalog"

**Expected Result:**
- ✅ Table displays courses with columns: Code, Title, Credits, Instructor, Capacity
- ✅ All available courses are listed
- ✅ Information is accurate

### Test Case S2: Register for Section
**Steps:**
1. Login as `stu1`
2. Navigate to "Register Section"
3. Select a section with available seats (checkbox)
4. Click "Register Selected Sections"

**Expected Result:**
- ✅ Success message displayed
- ✅ Section appears in "My Timetable"
- ✅ Section appears in "Drop Section" list
- ✅ Available seats count decreases

### Test Case S3: Cannot Register Same Section Twice
**Steps:**
1. Login as `stu1`
2. Try to register for a section already enrolled in

**Expected Result:**
- ✅ Error message: "Already enrolled in this exact section"
- ✅ Registration is blocked
- ✅ Enrollment status unchanged

### Test Case S4: Cannot Register in Full Section
**Steps:**
1. Login as `stu1`
2. Find a section with 0 available seats (marked as "FULL")
3. Try to register

**Expected Result:**
- ✅ Error message: "Section is full"
- ✅ Registration is blocked

### Test Case S5: Drop Section
**Steps:**
1. Login as `stu1`
2. Navigate to "Drop Section"
3. Select an enrolled section (checkbox)
4. Click "Drop Selected Sections"

**Expected Result:**
- ✅ Success message displayed
- ✅ Section removed from "My Timetable"
- ✅ Section removed from "Drop Section" list
- ✅ Can register for same section again

### Test Case S6: View Timetable
**Steps:**
1. Login as `stu1`
2. Navigate to "My Timetable"

**Expected Result:**
- ✅ Table shows: Section, Course, Day, Time, Room
- ✅ Only enrolled sections are displayed
- ✅ Schedule information is correct

### Test Case S7: View Grades
**Steps:**
1. Login as `stu1`
2. Navigate to "View Grades"
3. Select a course from dropdown

**Expected Result:**
- ✅ Grade components displayed: Component, Score, Max Score, Weight, Percentage
- ✅ Final grade calculated and displayed (e.g., "Final Grade: 85.50% (B)")
- ✅ Letter grade shown correctly

### Test Case S8: Download Transcript (CSV)
**Steps:**
1. Login as `stu1`
2. Navigate to "Download Transcript"
3. Click "Download Transcript (CSV)"
4. Choose save location
5. Open the CSV file

**Expected Result:**
- ✅ File saves successfully
- ✅ CSV contains: Section, Course, Final Percentage, Letter Grade
- ✅ All enrolled courses are included
- ✅ Grades are calculated correctly

### Test Case S9: Download Transcript (PDF)
**Steps:**
1. Login as `stu1`
2. Navigate to "Download Transcript"
3. Click "Download Transcript (PDF)"
4. Choose save location
5. Open the PDF file

**Expected Result:**
- ✅ PDF file saves successfully
- ✅ PDF contains student name, roll number
- ✅ Table shows: Section, Course, Percentage, Grade
- ✅ Formatting is clean and readable

---

## C. Instructor Tests

### Test Case I1: View Own Sections
**Steps:**
1. Login as `inst1`
2. Navigate to "My Sections"

**Expected Result:**
- ✅ Table shows only sections assigned to `inst1`
- ✅ Columns: Section, Course, Semester, Year, Capacity, Room, Day, Time
- ✅ No other instructor's sections visible

### Test Case I2: Enter Scores
**Steps:**
1. Login as `inst1`
2. Navigate to "Grade Entry (Live)"
3. Select a section from dropdown
4. Select a student from list
5. Enter/modify scores in the table
6. Click "Save Grades"

**Expected Result:**
- ✅ Scores saved successfully
- ✅ Success message displayed
- ✅ Grades persist after refresh

### Test Case I3: Add Grade Component
**Steps:**
1. Login as `inst1`
2. Navigate to "Grade Entry (Live)"
3. Select a section
4. Click "Add Component"
5. Enter component name, max score, weight
6. Click OK

**Expected Result:**
- ✅ Component added to all students in section
- ✅ Component appears in grade table
- ✅ Initial scores set to 0.0

### Test Case I4: Compute Final Grade
**Steps:**
1. Login as `inst1`
2. Enter scores for all components (e.g., Quiz: 18/20 weight 10%, Midterm: 85/100 weight 30%, Final: 92/100 weight 50%)
3. Verify final grade calculation

**Expected Result:**
- ✅ Final grade calculated correctly using weighted formula
- ✅ Formula: (score1/max1 * weight1) + (score2/max2 * weight2) + ...
- ✅ Final grade displayed appropriately

### Test Case I5: View Class Stats
**Steps:**
1. Login as `inst1`
2. Navigate to "Class Stats"
3. Select a section from dropdown
4. Click "Show Stats"

**Expected Result:**
- ✅ Statistics displayed: Component, Avg, Min, Max
- ✅ Calculations are accurate
- ✅ All grade components included

### Test Case I6: Export CSV
**Steps:**
1. Login as `inst1`
2. Navigate to "Export CSV"
3. Click "Export Grades as CSV"
4. Choose save location
5. Open CSV file

**Expected Result:**
- ✅ CSV file saved successfully
- ✅ Contains: Section, Student Roll No., Component, Score, Final Grade
- ✅ All students' grades included

### Test Case I7: Cannot Access Other Instructor's Section
**Steps:**
1. Login as `inst1`
2. Try to access grade entry for a section not assigned to `inst1`

**Expected Result:**
- ✅ Cannot see sections from other instructors
- ✅ No unauthorized access possible

---

## D. Admin Tests

### Test Case A1: Create Student User
**Steps:**
1. Login as `admin`
2. Navigate to "Manage Users"
3. Enter username, password, select role "STUDENT"
4. Fill student fields: Roll No., Program, Year, Email
5. Click "Add User"

**Expected Result:**
- ✅ User created successfully
- ✅ Appears in users table
- ✅ Can login with new credentials
- ✅ Student profile created in ERP DB

### Test Case A2: Create Instructor User
**Steps:**
1. Login as `admin`
2. Navigate to "Manage Users"
3. Enter username, password, select role "INSTRUCTOR"
4. Fill instructor fields: Employee ID, Department, Email
5. Click "Add User"

**Expected Result:**
- ✅ User created successfully
- ✅ Instructor profile created
- ✅ Can login as instructor

### Test Case A3: Create Course
**Steps:**
1. Login as `admin`
2. Navigate to "Manage Courses"
3. Enter: Code, Title, Credits, Description
4. Click "Add Course"

**Expected Result:**
- ✅ Course added to table
- ✅ Appears in course list
- ✅ Can be selected when creating sections

### Test Case A4: Create Section
**Steps:**
1. Login as `admin`
2. Navigate to "Manage Sections"
3. Fill all fields: Section Code, Course, Day, Time, Room, Semester, Year, Capacity
4. Click "Add Section"

**Expected Result:**
- ✅ Section created successfully
- ✅ Appears in sections table
- ✅ Available for student registration

### Test Case A5: Assign Instructor to Section
**Steps:**
1. Login as `admin`
2. Navigate to "Assign Instructor"
3. Click "Update" button for a section
4. Select an instructor from dropdown
5. Click OK

**Expected Result:**
- ✅ Instructor assigned successfully
- ✅ Section shows updated instructor
- ✅ Instructor can see section in their dashboard

### Test Case A6: Toggle Maintenance Mode ON
**Steps:**
1. Login as `admin`
2. Navigate to "Maintenance Mode"
3. Click "Enable Maintenance Mode"

**Expected Result:**
- ✅ Status shows "MAINTENANCE MODE ACTIVE"
- ✅ Banner appears on student/instructor dashboards
- ✅ Student/instructor cannot make changes (test register/drop blocked)
- ✅ View-only access for students/instructors

### Test Case A7: Toggle Maintenance Mode OFF
**Steps:**
1. With maintenance ON, click "Disable Maintenance Mode"

**Expected Result:**
- ✅ Status shows "Maintenance Mode is OFF"
- ✅ Banner disappears
- ✅ Normal functionality restored
- ✅ Students/instructors can make changes again

### Test Case A8: Backup Database
**Steps:**
1. Login as `admin`
2. Navigate to "Backup/Restore"
3. Click "Backup ERP Databases"
4. Choose save location

**Expected Result:**
- ✅ Backup file created successfully
- ✅ SQL dump file is valid
- ✅ Success message displayed

### Test Case A9: Restore Database
**Steps:**
1. Make a change to database (e.g., add a course)
2. Navigate to "Backup/Restore"
3. Click "Restore ERP Databases"
4. Select previous backup file
5. Confirm restore

**Expected Result:**
- ✅ Restore completes successfully
- ✅ Database reverted to backup state
- ✅ Changes made after backup are undone

---

## E. Edge & Negative Tests

### Test Case E1: Negative Capacity
**Steps:**
1. Login as `admin`
2. Try to create section with capacity = -5

**Expected Result:**
- ✅ Error message displayed
- ✅ Section not created
- ✅ Validation prevents invalid data

### Test Case E2: Empty Required Fields
**Steps:**
1. Try to create course without title
2. Try to create user without username

**Expected Result:**
- ✅ Error messages for missing required fields
- ✅ Operation blocked until fields filled

### Test Case E3: Duplicate Enrollment Prevention
**Steps:**
1. Login as `stu1`
2. Register for a section
3. Try to register for same section again

**Expected Result:**
- ✅ Error: "Already enrolled in this exact section"
- ✅ Duplicate prevented at database level (UNIQUE constraint)

### Test Case E4: Student Cannot Access Another Student's Data
**Steps:**
1. Login as `stu1`
2. Check "View Grades" - should only see own grades
3. Check "Timetable" - should only see own enrollments

**Expected Result:**
- ✅ Only `stu1`'s data visible
- ✅ No access to `stu2`'s data
- ✅ Queries filtered by student_id

### Test Case E5: Instructor Cannot Grade Other Sections
**Steps:**
1. Login as `inst1`
2. Navigate to "Grade Entry"
3. Check section dropdown - should only show own sections

**Expected Result:**
- ✅ Only `inst1`'s sections available
- ✅ Cannot access other instructors' sections

### Test Case E6: Maintenance Mode Blocks All Writes
**Steps:**
1. Admin enables maintenance mode
2. As student: Try to register
3. As student: Try to drop
4. As instructor: Try to save grades
5. As instructor: Try to add component

**Expected Result:**
- ✅ All write operations blocked with message
- ✅ Clear message: "Maintenance mode is active"
- ✅ Only read/view operations allowed

---

## F. Data Integrity Tests

### Test Case F1: Foreign Key Constraints
**Steps:**
1. Try to delete a course that has sections
2. Try to delete a student that has enrollments

**Expected Result:**
- ✅ Foreign key constraints prevent orphaned records
- ✅ Appropriate error or cascade behavior

### Test Case F2: Unique Constraints
**Steps:**
1. Try to create duplicate username
2. Try to create duplicate section code (same course, semester, year)

**Expected Result:**
- ✅ Database prevents duplicates
- ✅ Error message displayed

---

## G. UI/UX Tests

### Test Case G1: Clear Buttons and Labels
**Steps:**
1. Navigate through all panels
2. Check button labels and field labels

**Expected Result:**
- ✅ All buttons clearly labeled
- ✅ All fields have descriptive labels
- ✅ No confusing terminology

### Test Case G2: Friendly Error Messages
**Steps:**
1. Trigger various errors (wrong password, full section, etc.)

**Expected Result:**
- ✅ Error messages are clear and helpful
- ✅ Messages explain what went wrong
- ✅ No technical jargon in user-facing messages

### Test Case G3: Tables for Lists
**Steps:**
1. Check course catalog, user list, section list

**Expected Result:**
- ✅ Data displayed in tables
- ✅ Columns are sortable (if implemented)
- ✅ Easy to read and navigate

---

## H. Performance Tests

### Test Case P1: Catalog Loads Quickly
**Steps:**
1. Login as student
2. Navigate to "Course Catalog"
3. Measure load time

**Expected Result:**
- ✅ Catalog loads in < 3 seconds
- ✅ No freezing or hanging

### Test Case P2: Application Starts Without Hangs
**Steps:**
1. Launch application
2. Observe startup time

**Expected Result:**
- ✅ Application starts quickly
- ✅ No long pauses or crashes
- ✅ Login screen appears promptly

---

## I. Security Tests

### Test Case SEC1: Password Hashing
**Steps:**
1. Check database: `SELECT password_hash FROM users_auth WHERE username='admin1'`

**Expected Result:**
- ✅ Passwords stored as bcrypt hashes (starting with $2a$)
- ✅ No plaintext passwords in database

### Test Case SEC2: Two-Database Separation
**Steps:**
1. Verify passwords only in `erp_auth` database
2. Verify student/instructor data only in `erp_main` database

**Expected Result:**
- ✅ Clear separation maintained
- ✅ No password data in `erp_main`
- ✅ User_id links both databases

### Test Case SEC3: Access Rules Enforced
**Steps:**
1. Try to perform unauthorized actions through UI
2. Verify database constraints

**Expected Result:**
- ✅ UI prevents unauthorized actions
- ✅ Database enforces constraints
- ✅ Clear error messages shown

---

## Test Summary Template

After running tests, fill this out:

| Test Category | Total Tests | Passed | Failed | Notes |
|---------------|-------------|--------|--------|-------|
| Login & Roles | 5 | | | |
| Student Features | 9 | | | |
| Instructor Features | 7 | | | |
| Admin Features | 9 | | | |
| Edge Cases | 6 | | | |
| Data Integrity | 2 | | | |
| UI/UX | 3 | | | |
| Performance | 2 | | | |
| Security | 3 | | | |
| **Total** | **46** | | | |

**Known Issues:**
[List any bugs or issues found]

**Test Environment:**
- Java Version: _______
- MySQL Version: _______
- OS: _______
- Test Date: _______

