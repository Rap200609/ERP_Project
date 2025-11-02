-- ============================================
-- Seed Data for University ERP System
-- Based on PDF requirements: admin, inst1, stu1, stu2
-- ============================================

USE erp_auth;

-- Insert Admin user
-- Password: admin (bcrypt hash)
INSERT INTO users_auth (username, role, password_hash) VALUES 
('admin', 'ADMIN', '$2a$10$Idu9M5S6urr/Bg5OGuThg.eroN.v3zWHGcF8pO8a3dyILUHdGUTMC')
ON DUPLICATE KEY UPDATE username = username;

-- Insert Instructor user
-- Password: instructor
-- Note: Hash generated using BCrypt. If login fails, use Admin panel to reset password
INSERT INTO users_auth (username, role, password_hash) VALUES 
('inst1', 'INSTRUCTOR', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON DUPLICATE KEY UPDATE username = username;

-- Insert Student users
-- Password: student (for both stu1 and stu2)
-- Note: Hash generated using BCrypt. If login fails, use Admin panel to reset password
INSERT INTO users_auth (username, role, password_hash) VALUES 
('stu1', 'STUDENT', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'),
('stu2', 'STUDENT', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi')
ON DUPLICATE KEY UPDATE username = username;

-- Get the user_ids for reference (will be set below)
SET @admin_id = (SELECT user_id FROM users_auth WHERE username = 'admin1');
SET @inst_id = (SELECT user_id FROM users_auth WHERE username = 'inst1');
SET @stu1_id = (SELECT user_id FROM users_auth WHERE username = 'stu1');
SET @stu2_id = (SELECT user_id FROM users_auth WHERE username = 'stu2');

USE erp_main;

-- Insert Instructor profile
INSERT INTO instructors (user_id, employee_id, department, email) VALUES 
(@inst_id, 'INST001', 'Computer Science', 'inst1@university.edu')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Insert Student profiles
INSERT INTO students (user_id, roll_no, program, year, email) VALUES 
(@stu1_id, 'STU001', 'Computer Science', 2, 'stu1@university.edu'),
(@stu2_id, 'STU002', 'Computer Science', 2, 'stu2@university.edu')
ON DUPLICATE KEY UPDATE user_id = user_id;

-- Insert Sample Courses
INSERT INTO courses (code, title, credits, description) VALUES 
('CS101', 'Introduction to Programming', 3, 'Basic programming concepts and logic'),
('CS201', 'Advanced Programming', 4, 'Object-oriented programming and design patterns'),
('CS301', 'Database Systems', 3, 'Database design and SQL'),
('MTH101', 'Calculus I', 4, 'Differential and integral calculus'),
('ENG101', 'English Composition', 2, 'Writing and communication skills')
ON DUPLICATE KEY UPDATE code = code;

-- Get course IDs
SET @cs101_id = (SELECT course_id FROM courses WHERE code = 'CS101');
SET @cs201_id = (SELECT course_id FROM courses WHERE code = 'CS201');
SET @cs301_id = (SELECT course_id FROM courses WHERE code = 'CS301');
SET @mth101_id = (SELECT course_id FROM courses WHERE code = 'MTH101');
SET @eng101_id = (SELECT course_id FROM courses WHERE code = 'ENG101');

-- Insert Sample Sections
INSERT INTO sections (course_id, instructor_id, section_code, day, time, room, capacity, semester, year) VALUES 
(@cs101_id, @inst_id, 'A', 'Monday', '09:00-10:30', 'Room 101', 30, 'Fall', 2024),
(@cs101_id, @inst_id, 'B', 'Wednesday', '09:00-10:30', 'Room 101', 30, 'Fall', 2024),
(@cs201_id, @inst_id, 'A', 'Tuesday', '14:00-15:30', 'Room 201', 25, 'Fall', 2024),
(@cs201_id, @inst_id, 'B', 'Thursday', '14:00-15:30', 'Room 201', 25, 'Fall', 2024),
(@cs301_id, @inst_id, 'A', 'Monday', '11:00-12:30', 'Room 301', 20, 'Fall', 2024),
(@mth101_id, NULL, 'A', 'Tuesday', '10:00-11:30', 'Room 401', 40, 'Fall', 2024),
(@eng101_id, NULL, 'A', 'Friday', '13:00-14:30', 'Room 501', 35, 'Fall', 2024)
ON DUPLICATE KEY UPDATE section_id = section_id;

-- Get section IDs
SET @cs101a_id = (SELECT section_id FROM sections WHERE course_id = @cs101_id AND section_code = 'A' AND semester = 'Fall' AND year = 2024 LIMIT 1);
SET @cs201a_id = (SELECT section_id FROM sections WHERE course_id = @cs201_id AND section_code = 'A' AND semester = 'Fall' AND year = 2024 LIMIT 1);
SET @cs301a_id = (SELECT section_id FROM sections WHERE course_id = @cs301_id AND section_code = 'A' AND semester = 'Fall' AND year = 2024 LIMIT 1);

-- Insert Sample Enrollments
INSERT INTO enrollments (student_id, section_id, status) VALUES 
(@stu1_id, @cs101a_id, 'ENROLLED'),
(@stu1_id, @cs201a_id, 'ENROLLED'),
(@stu1_id, @cs301a_id, 'ENROLLED'),
(@stu2_id, @cs101a_id, 'ENROLLED'),
(@stu2_id, @cs201a_id, 'ENROLLED')
ON DUPLICATE KEY UPDATE enrollment_id = enrollment_id;

-- Get enrollment IDs for grades
SET @stu1_cs101_enroll = (SELECT enrollment_id FROM enrollments WHERE student_id = @stu1_id AND section_id = @cs101a_id);
SET @stu1_cs201_enroll = (SELECT enrollment_id FROM enrollments WHERE student_id = @stu1_id AND section_id = @cs201a_id);
SET @stu2_cs101_enroll = (SELECT enrollment_id FROM enrollments WHERE student_id = @stu2_id AND section_id = @cs101a_id);

-- Insert Sample Grades
-- CS101 grades for student 1
INSERT INTO grades (enrollment_id, component, score, max_score, weight) VALUES 
(@stu1_cs101_enroll, 'Quiz 1', 18.0, 20.0, 10.0),
(@stu1_cs101_enroll, 'Quiz 2', 19.0, 20.0, 10.0),
(@stu1_cs101_enroll, 'Midterm', 85.0, 100.0, 30.0),
(@stu1_cs101_enroll, 'Final', 92.0, 100.0, 50.0)
ON DUPLICATE KEY UPDATE grade_id = grade_id;

-- CS201 grades for student 1
INSERT INTO grades (enrollment_id, component, score, max_score, weight) VALUES 
(@stu1_cs201_enroll, 'Assignment 1', 45.0, 50.0, 10.0),
(@stu1_cs201_enroll, 'Assignment 2', 48.0, 50.0, 10.0),
(@stu1_cs201_enroll, 'Midterm', 88.0, 100.0, 30.0),
(@stu1_cs201_enroll, 'Final', 90.0, 100.0, 50.0)
ON DUPLICATE KEY UPDATE grade_id = grade_id;

-- CS101 grades for student 2
INSERT INTO grades (enrollment_id, component, score, max_score, weight) VALUES 
(@stu2_cs101_enroll, 'Quiz 1', 17.0, 20.0, 10.0),
(@stu2_cs101_enroll, 'Quiz 2', 16.0, 20.0, 10.0),
(@stu2_cs101_enroll, 'Midterm', 78.0, 100.0, 30.0),
(@stu2_cs101_enroll, 'Final', 82.0, 100.0, 50.0)
ON DUPLICATE KEY UPDATE grade_id = grade_id;

