package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.sql.Timestamp;

public class AuthService {

    public static class AuthResult {
        public boolean success;
        public String role;
        public int userId;
        public String message;
    }

    public static AuthResult authenticate(String username, String password, javax.sql.DataSource authDataSource) {
        AuthResult result = new AuthResult();

        if (username == null || username.trim().isEmpty() || password == null) {
            result.success = false;
            result.message = "Username or password cannot be empty.";
            return result;
        }

        try (Connection conn = authDataSource.getConnection()) {
            // Check for account lockout first (bonus feature)
            boolean hasLockoutColumns = false;
            int failedAttempts = 0;
            try {
                PreparedStatement checkLockStmt = conn.prepareStatement("SELECT failed_attempts, locked_until FROM users_auth WHERE username = ?");
                checkLockStmt.setString(1, username.trim());
                ResultSet lockRs = checkLockStmt.executeQuery();
                if (lockRs.next()) {
                    hasLockoutColumns = true;
                    failedAttempts = lockRs.getInt("failed_attempts");
                    Timestamp lockedUntil = lockRs.getTimestamp("locked_until");
                    
                    // Check if account is locked
                    if (lockedUntil != null && lockedUntil.after(new Timestamp(System.currentTimeMillis()))) {
                        result.success = false;
                        long minutesLeft = (lockedUntil.getTime() - System.currentTimeMillis()) / 60000;
                        result.message = "Account temporarily locked due to too many failed attempts. Please try again in " + (minutesLeft + 1) + " minute(s).";
                        return result;
                    } else if (lockedUntil != null) {
                        // Lock expired, reset it
                        PreparedStatement resetStmt = conn.prepareStatement("UPDATE users_auth SET failed_attempts = 0, locked_until = NULL WHERE username = ?");
                        resetStmt.setString(1, username.trim());
                        resetStmt.executeUpdate();
                        failedAttempts = 0;
                    }
                }
            } catch (SQLException e) {
                // Columns don't exist - lockout feature not available, continue normally
                hasLockoutColumns = false;
            }
            
            PreparedStatement stmt = conn.prepareStatement("SELECT user_id, role, password_hash FROM users_auth WHERE username = ? AND status = 'ACTIVE'");

            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                result.success = false;
                result.message = "Incorrect username or password.";
                return result;
            }

            int userId = rs.getInt("user_id");
            String role = rs.getString("role");
            String storedHash = rs.getString("password_hash");

            // Handle bcrypt and plain-text fallback gracefully
            boolean passwordMatches = false;
            try {
                if (storedHash != null && storedHash.startsWith("$2a$")) {
                    passwordMatches = BCrypt.checkpw(password, storedHash);
                } else {
                    // Fallback (legacy plaintext, useful for testing)
                    passwordMatches = password.equals(storedHash);
                }
            } catch (Exception ex) {
                passwordMatches = password.equals(storedHash);
            }

            if (passwordMatches) {
                // Successful login - reset failed attempts
                if (hasLockoutColumns) {
                    PreparedStatement resetStmt = conn.prepareStatement("UPDATE users_auth SET failed_attempts = 0, locked_until = NULL WHERE username = ?");
                    resetStmt.setString(1, username.trim());
                    resetStmt.executeUpdate();
                }
                
                result.success = true;
                result.message = "Login successful!";
                result.role = role;
                result.userId = userId;
            } else {
                // Failed login - increment attempts
                if (hasLockoutColumns) {
                    failedAttempts++;
                    if (failedAttempts >= 5) {
                        // Lock account for 15 minutes
                        Timestamp lockUntil = new Timestamp(System.currentTimeMillis() + (15 * 60 * 1000));
                        PreparedStatement lockStmt = conn.prepareStatement("UPDATE users_auth SET failed_attempts = ?, locked_until = ? WHERE username = ?");
                        lockStmt.setInt(1, failedAttempts);
                        lockStmt.setTimestamp(2, lockUntil);
                        lockStmt.setString(3, username.trim());
                        lockStmt.executeUpdate();
                        
                        result.success = false;
                        result.message = "Too many failed login attempts. Account locked for 15 minutes.";
                    } else {
                        // Update failed attempts count
                        PreparedStatement updateStmt = conn.prepareStatement("UPDATE users_auth SET failed_attempts = ? WHERE username = ?");
                        updateStmt.setInt(1, failedAttempts);
                        updateStmt.setString(2, username.trim());
                        updateStmt.executeUpdate();
                        
                        result.success = false;
                        int remaining = 5 - failedAttempts;
                        result.message = "Incorrect username or password. " + remaining + " attempt(s) remaining before account lockout.";
                    }
                } else {
                    result.success = false;
                    result.message = "Incorrect username or password.";
                }
            }

        } catch (SQLException e) {
            result.success = false;
            result.message = "Database error: " + e.getMessage();
        } catch (Exception e) {
            result.success = false;
            result.message = "Unexpected error: " + e.getMessage();
        }

        return result;
    }
}
