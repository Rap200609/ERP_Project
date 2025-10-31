package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

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

        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT user_id, role, password_hash FROM users_auth WHERE username = ? AND status = 'ACTIVE'"
             )) {

            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                result.success = false;
                result.message = "User not found or inactive.";
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
                result.success = true;
                result.message = "Login successful!";
                result.role = role;
                result.userId = userId;
            } else {
                result.success = false;
                result.message = "Incorrect password.";
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
