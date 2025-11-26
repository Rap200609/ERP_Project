package edu.univ.erp.data.repository;

import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.domain.InstructorProfile;
import edu.univ.erp.domain.InstructorOption;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InstructorRepository {
    private final DataSource mainDataSource;

    public InstructorRepository() {
        this(DatabaseConfig.getMainDataSource());
    }

    public InstructorRepository(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    public void createInstructorProfile(int userId, String employeeId, String department, String email) throws Exception {
        String sql = "INSERT INTO instructors (user_id, employee_id, department, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, employeeId);
            stmt.setString(3, department);
            stmt.setString(4, email);
            stmt.executeUpdate();
        }
    }

    public Optional<InstructorProfile> findByUserId(int userId) throws Exception {
        String sql = "SELECT employee_id, department, email FROM instructors WHERE user_id=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new InstructorProfile(userId, rs.getString("employee_id"), rs.getString("department"), rs.getString("email")));
                }
            }
        }
        return Optional.empty();
    }

    public void updateInstructorProfile(InstructorProfile profile) throws Exception {
        String sql = "UPDATE instructors SET employee_id=?, department=?, email=? WHERE user_id=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profile.getEmployeeId());
            stmt.setString(2, profile.getDepartment());
            stmt.setString(3, profile.getEmail());
            stmt.setInt(4, profile.getUserId());
            stmt.executeUpdate();
        }
    }

    public void deleteByUserId(int userId) throws Exception {
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM instructors WHERE user_id=?")) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    public List<InstructorOption> fetchInstructorOptions() throws Exception {
        String sql = "SELECT user_id, employee_id, department FROM instructors ORDER BY employee_id";
        List<InstructorOption> instructors = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                instructors.add(new InstructorOption(rs.getInt("user_id"),rs.getString("employee_id"),rs.getString("department")));
            }
        }
        return instructors;
    }
}

