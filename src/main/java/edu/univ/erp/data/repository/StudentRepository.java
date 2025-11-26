package edu.univ.erp.data.repository;

import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.domain.StudentProfile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class StudentRepository {
    private final DataSource mainDataSource;

    public StudentRepository() {
        this(DatabaseConfig.getMainDataSource());
    }

    public StudentRepository(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    public void createStudentProfile(int userId, String rollNo, String program, int year, String email) throws Exception {
        String sql = "INSERT INTO students (user_id, roll_no, program, year, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, rollNo);
            stmt.setString(3, program);
            stmt.setInt(4, year);
            stmt.setString(5, email);
            stmt.executeUpdate();
        }
    }

    public Optional<StudentProfile> findByUserId(int userId) throws Exception {
        String sql = "SELECT roll_no, program, year, email FROM students WHERE user_id=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new StudentProfile(userId,rs.getString("roll_no"),rs.getString("program"),rs.getInt("year"),rs.getString("email")));
                }
            }
        }
        return Optional.empty();
    }

    public void updateStudentProfile(StudentProfile profile) throws Exception {
        String sql = "UPDATE students SET roll_no=?, program=?, year=?, email=? WHERE user_id=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, profile.getRollNo());
            stmt.setString(2, profile.getProgram());
            stmt.setInt(3, profile.getYear());
            stmt.setString(4, profile.getEmail());
            stmt.setInt(5, profile.getUserId());
            stmt.executeUpdate();
        }
    }

    public void deleteByUserId(int userId) throws Exception {
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM students WHERE user_id=?")) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }
}

