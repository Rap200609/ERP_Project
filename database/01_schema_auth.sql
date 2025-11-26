-- Done
-- ============================================
-- ERP Auth Database Schema
-- University ERP System
-- ============================================

CREATE DATABASE IF NOT EXISTS erp_auth;
USE erp_auth;

-- Users Authentication Table
CREATE TABLE IF NOT EXISTS users_auth (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    role ENUM('STUDENT', 'INSTRUCTOR', 'ADMIN') NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Login lockout columns (bonus feature)
    failed_attempts INT DEFAULT 0,
    locked_until TIMESTAMP NULL
);

-- Index for faster username lookups
CREATE INDEX idx_username ON users_auth(username);
CREATE INDEX idx_status ON users_auth(status);

