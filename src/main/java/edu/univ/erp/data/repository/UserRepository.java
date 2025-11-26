// Done
package edu.univ.erp.data.repository;

import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.domain.UserAccount;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final DataSource authDataSource;

    public UserRepository() {
        this(DatabaseConfig.getAuthDataSource());
    }

    public UserRepository(DataSource authDataSource) {
        this.authDataSource = authDataSource;
    }

    public List<UserAccount> findAll() throws Exception {
        List<UserAccount> users = new ArrayList<>();
        try (Connection conn = authDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT user_id, username, role, status FROM users_auth")) {
            while (rs.next()) {
                users.add(new UserAccount(rs.getInt("user_id"),rs.getString("username"),rs.getString("role"),rs.getString("status")));
            }
        }
        return users;
    }

    public int createUser(String username, String role, String passwordHash) throws Exception {
        String sql = "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = authDataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.setString(3, passwordHash);
            stmt.executeUpdate();
            // Here we retrieve the generated user_id
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new IllegalStateException("Failed to create user_auth row");
    }

    public void updateUsernameAndPassword(int userId, String username, String passwordHash) throws Exception {
        String sql = "UPDATE users_auth SET username=?, password_hash=? WHERE user_id=?";
        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.setInt(3, userId);
            stmt.executeUpdate();
        }
    }

    public void updateUsername(int userId, String username) throws Exception {
        String sql = "UPDATE users_auth SET username=? WHERE user_id=?";
        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public Optional<UserAccount> findById(int userId) throws Exception {
        String sql = "SELECT user_id, username, role, status FROM users_auth WHERE user_id=?";
        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new UserAccount(    rs.getInt("user_id"),    rs.getString("username"),    rs.getString("role"),    rs.getString("status")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public void deleteUser(int userId) throws Exception {
        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users_auth WHERE user_id=?")) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    public Optional<String> getPasswordHash(int userId) throws Exception {
        String sql = "SELECT password_hash FROM users_auth WHERE user_id=?";
        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("password_hash"));
                }
            }
        }
        return Optional.empty();
    }

    public void updatePassword(int userId, String passwordHash) throws Exception {
        String sql = "UPDATE users_auth SET password_hash=? WHERE user_id=?";
        try (Connection conn = authDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, passwordHash);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
}

