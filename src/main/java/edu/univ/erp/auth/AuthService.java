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
        try (Connection conn = authDataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT user_id, role, password_hash FROM users_auth WHERE username = ? AND status = 'ACTIVE'"
            );
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                if (BCrypt.checkpw(password, hash)) {
                    result.success = true;
                    result.role = rs.getString("role");
                    result.userId = rs.getInt("user_id");
                    result.message = "Login successful!";
                } else {
                    result.success = false;
                    result.message = "Incorrect password.";
                }
            } else {
                result.success = false;
                result.message = "User not found or inactive.";
            }
        } catch (Exception e) {
            result.success = false;
            result.message = "Error: " + e.getMessage();
        }
        return result;
    }
}
