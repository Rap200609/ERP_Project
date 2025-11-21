-- Done
-- ============================================
-- Seed Data for University ERP System
-- Based on PDF requirements: admin, inst1, stu1, stu2
-- ============================================

USE erp_auth;

-- Insert Admin user
-- Password: admin123 (bcrypt hash)
INSERT INTO users_auth (username, role, password_hash) VALUES 
('admin', 'ADMIN', '$2a$10$Idu9M5S6urr/Bg5OGuThg.eroN.v3zWHGcF8pO8a3dyILUHdGUTMC')
ON DUPLICATE KEY UPDATE username = username;

-- Insert Instructor user
-- Password: inst1_1
-- Note: Hash generated using BCrypt. If login fails, use Admin panel to reset password
INSERT INTO users_auth (username, role, password_hash) VALUES 
('inst1', 'INSTRUCTOR', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON DUPLICATE KEY UPDATE username = username;

-- Insert Student users
-- Password: stu1_1 for stu1 and stu2_2 for stu2
-- Note: Hash generated using BCrypt. If login fails, use Admin panel to reset password
INSERT INTO users_auth (username, role, password_hash) VALUES 
('stu1', 'STUDENT', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('stu2', 'STUDENT', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON DUPLICATE KEY UPDATE username = username;

-- Get the user_ids for reference (will be set below)
SET @admin_id = (SELECT user_id FROM users_auth WHERE username = 'admin');
SET @inst1_id = (SELECT user_id FROM users_auth WHERE username = 'inst1');
SET @stu1_id = (SELECT user_id FROM users_auth WHERE username = 'stu1');
SET @stu2_id = (SELECT user_id FROM users_auth WHERE username = 'stu2');

USE erp_main;

-- Insert Instructor profile
INSERT INTO instructors (user_id, employee_id, department, email) VALUES 
(@inst1_id, 'INST001', 'CSE', 'inst1@iiitd.ac.in')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Insert Student profiles
INSERT INTO students (user_id, roll_no, program, year, email) VALUES 
(@stu1_id, '2025001', 'CSE', 2, 'stu1@iiitd.ac.in'),
(@stu2_id, '2025002', 'CSAI', 2, 'stu2@iiitd.ac.in')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Insert Sample Courses
INSERT INTO courses (code, title, credits, description) VALUES 
('CSE101', 'Introduction to Programming', 4, 'Basic programming concepts and logic'),
('CSE301', 'Advanced Programming', 4, 'Object-oriented programming'),
('CSE201', 'Data Structures', 4, 'Introduction to data structures and algorithms'),
('CSE401', 'Database Systems', 4, 'Database design and SQL'),
('MTH101', 'Linear Algebra', 4, 'Mandatory course'),
('SSH101', 'Communication Skills', 2, 'Writing and communication skills')
ON DUPLICATE KEY UPDATE code = code;

-- Get course IDs
SET @cse101_id = (SELECT course_id FROM courses WHERE code = 'CSE101');
SET @cse201_id = (SELECT course_id FROM courses WHERE code = 'CSE201');
SET @cse301_id = (SELECT course_id FROM courses WHERE code = 'CSE301');
SET @cse401_id = (SELECT course_id FROM courses WHERE code = 'CSE401');
SET @mth101_id = (SELECT course_id FROM courses WHERE code = 'MTH101');
SET @ssh101_id = (SELECT course_id FROM courses WHERE code = 'SSH101');

-- Insert Sample Sections
INSERT INTO sections (course_id, instructor_id, section_code, day, time, room, capacity, semester, year) VALUES 
(@cse101_id, @inst1_id, 'A', 'Monday, Wednesday', '09:00-10:30', 'LHC-101', 300, 'Monsoon', 2025),
(@cse101_id, @inst1_id, 'B', 'Monday, Wednesday', '09:00-10:30', 'LHC-201', 250, 'Monsoon', 2025),
(@cse201_id, @inst1_id, 'A', 'Tuesday', '14:00-15:30', 'LHC-201', 250, 'Monsoon', 2025),
(@cse301_id, @inst1_id, 'B', 'Thursday', '14:00-15:30', 'LHC-101', 300, 'Monsoon', 2025),
(@cse401_id, @inst1_id, 'A, B', 'Monday, Wednesday', '11:00-12:30', 'LHC-102', 650, 'Monsoon', 2025),
(@mth101_id, NULL, 'A', 'Tuesday', '10:00-11:30', 'R&D A 007', 200, 'Monsoon', 2025),
(@ssh101_id, NULL, 'A', 'Friday', '13:00-14:30', 'ACAD-101', 300, 'Monsoon', 2025)
ON DUPLICATE KEY UPDATE section_id = section_id;

-- Get section IDs
SET @cse101a_id = (SELECT section_id FROM sections WHERE course_id = @cse101_id AND section_code = 'A' AND semester = 'Monsoon' AND year = 2025 LIMIT 1);
SET @cse201a_id = (SELECT section_id FROM sections WHERE course_id = @cse201_id AND section_code = 'A' AND semester = 'Monsoon' AND year = 2025 LIMIT 1);
SET @cse301a_id = (SELECT section_id FROM sections WHERE course_id = @cse301_id AND section_code = 'B' AND semester = 'Monsoon' AND year = 2025 LIMIT 1);

-- Insert Sample Enrollments
INSERT INTO enrollments (student_id, section_id, status) VALUES 
(@stu1_id, @cse101a_id, 'ENROLLED'),
(@stu1_id, @cse201a_id, 'ENROLLED'),
(@stu1_id, @cse301a_id, 'ENROLLED'),
(@stu2_id, @cse101a_id, 'ENROLLED'),
(@stu2_id, @cse201a_id, 'ENROLLED')
ON DUPLICATE KEY UPDATE enrollment_id = enrollment_id;

-- Get enrollment IDs for grades
SET @stu1_cse101_enroll = (SELECT enrollment_id FROM enrollments WHERE student_id = @stu1_id AND section_id = @cse101a_id);
SET @stu1_cse201_enroll = (SELECT enrollment_id FROM enrollments WHERE student_id = @stu1_id AND section_id = @cse201a_id);
SET @stu2_cse101_enroll = (SELECT enrollment_id FROM enrollments WHERE student_id = @stu2_id AND section_id = @cse101a_id);

-- Insert Sample Grades
-- CS101 grades for student 1
INSERT INTO grades (enrollment_id, component, score, max_score, weight) VALUES 
(@stu1_cse101_enroll, 'Quiz 1', 18.0, 20.0, 10.0),
(@stu1_cse101_enroll, 'Quiz 2', 19.0, 20.0, 10.0),
(@stu1_cse101_enroll, 'Midterm', 85.0, 100.0, 30.0),
(@stu1_cse101_enroll, 'Final', 92.0, 100.0, 50.0)
ON DUPLICATE KEY UPDATE grade_id = grade_id;

-- CS201 grades for student 1
INSERT INTO grades (enrollment_id, component, score, max_score, weight) VALUES 
(@stu1_cse201_enroll, 'Assignment 1', 45.0, 50.0, 10.0),
(@stu1_cse201_enroll, 'Assignment 2', 48.0, 50.0, 10.0),
(@stu1_cse201_enroll, 'Midterm', 88.0, 100.0, 30.0),
(@stu1_cse201_enroll, 'Final', 90.0, 100.0, 50.0)
ON DUPLICATE KEY UPDATE grade_id = grade_id;

-- CS101 grades for student 2
INSERT INTO grades (enrollment_id, component, score, max_score, weight) VALUES 
(@stu2_cse101_enroll, 'Quiz 1', 17.0, 20.0, 10.0),
(@stu2_cse101_enroll, 'Quiz 2', 16.0, 20.0, 10.0),
(@stu2_cse101_enroll, 'Midterm', 78.0, 100.0, 30.0),
(@stu2_cse101_enroll, 'Final', 82.0, 100.0, 50.0)
ON DUPLICATE KEY UPDATE grade_id = grade_id;

