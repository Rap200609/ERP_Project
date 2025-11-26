-- ============================================
-- ERP Main Database Schema
-- University ERP System
-- ============================================

CREATE DATABASE IF NOT EXISTS erp_main;
USE erp_main;

-- Students Table
CREATE TABLE IF NOT EXISTS students (
    user_id INT PRIMARY KEY,
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    program VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES erp_auth.users_auth(user_id) ON DELETE CASCADE
);

-- Instructors Table
CREATE TABLE IF NOT EXISTS instructors (
    user_id INT PRIMARY KEY,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    department VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES erp_auth.users_auth(user_id) ON DELETE CASCADE
);

-- Courses Table
CREATE TABLE IF NOT EXISTS courses (
    course_id INT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(10) UNIQUE NOT NULL,
    title VARCHAR(100) NOT NULL,
    credits INT NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sections Table
-- Note: day and time are separate columns as used in the code
CREATE TABLE IF NOT EXISTS sections (
    section_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    instructor_id INT NULL,
    section_code VARCHAR(10) NOT NULL,
    day VARCHAR(20) NOT NULL,
    time VARCHAR(20) NOT NULL,
    room VARCHAR(20),
    capacity INT DEFAULT 30,
    semester VARCHAR(10) NOT NULL,
    year INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (instructor_id) REFERENCES instructors(user_id) ON DELETE SET NULL,
    UNIQUE KEY unique_section (course_id, section_code, semester, year)
);

-- Enrollments Table
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    section_id INT NOT NULL,
    status ENUM('ENROLLED', 'DROPPED', 'COMPLETED') DEFAULT 'ENROLLED',
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
    FOREIGN KEY (section_id) REFERENCES sections(section_id) ON DELETE CASCADE,
    UNIQUE KEY unique_enrollment (student_id, section_id)
);

-- Grades Table
CREATE TABLE IF NOT EXISTS grades (
    grade_id INT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id INT NOT NULL,
    component VARCHAR(50) NOT NULL,
    score DECIMAL(10,2) DEFAULT 0.0,
    max_score DECIMAL(10,2) DEFAULT 0.0,
    weight DECIMAL(5,2) DEFAULT 0.0,
    final_grade VARCHAR(2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (enrollment_id) REFERENCES enrollments(enrollment_id) ON DELETE CASCADE,
    UNIQUE KEY unique_grade_component (enrollment_id, component)
);

-- Settings Table
CREATE TABLE IF NOT EXISTS settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(255),
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert default settings
INSERT INTO settings (setting_key, setting_value, description) VALUES 
('maintenance_mode', 'OFF', 'System maintenance mode flag (ON/OFF)'),
('drop_deadline', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'Drop section deadline (YYYY-MM-DD format)')
ON DUPLICATE KEY UPDATE setting_value = setting_value;

