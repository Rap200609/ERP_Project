package edu.univ.erp.data.repository;

import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.domain.SectionAvailability;
import edu.univ.erp.domain.SectionDetail;
import edu.univ.erp.domain.SectionSummary;
import edu.univ.erp.domain.SectionAssignment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SectionRepository {
    private final DataSource mainDataSource;

    public SectionRepository() {
        this(DatabaseConfig.getMainDataSource());
    }

    public SectionRepository(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    public List<SectionAvailability> fetchSectionAvailability() throws Exception {
        String sql = """
                SELECT s.section_id, s.section_code, c.title, u.username AS instructor, s.capacity,
                (SELECT COUNT(*) FROM enrollments e WHERE e.section_id = s.section_id AND e.status = 'ENROLLED') AS enrolled
                FROM sections s
                JOIN courses c ON s.course_id = c.course_id
                LEFT JOIN instructors i ON s.instructor_id = i.user_id
                LEFT JOIN erp_auth.users_auth u ON i.user_id = u.user_id
                ORDER BY c.title, s.section_code
                """;
        List<SectionAvailability> list = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new SectionAvailability(rs.getInt("section_id"), rs.getString("section_code"), rs.getString("title"), rs.getString("instructor"), rs.getInt("capacity"), rs.getInt("enrolled")));
            }
        }
        return list;
    }

    public Optional<SectionSummary> findByCode(String sectionCode) throws Exception {
        String sql = """
                SELECT s.section_id, s.course_id, s.section_code, s.capacity, c.title
                FROM sections s
                JOIN courses c ON s.course_id = c.course_id
                WHERE s.section_code = ?
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sectionCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new SectionSummary(rs.getInt("section_id"), rs.getInt("course_id"), rs.getString("section_code"), rs.getString("title"), rs.getInt("capacity")));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<SectionSummary> findById(int sectionId) throws Exception {
        String sql = """
                SELECT s.section_id, s.course_id, s.section_code, s.capacity, c.title
                FROM sections s
                JOIN courses c ON s.course_id = c.course_id
                WHERE s.section_id = ?
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new SectionSummary(rs.getInt("section_id"), rs.getInt("course_id"), rs.getString("section_code"), rs.getString("title"), rs.getInt("capacity")));
                }
            }
        }
        return Optional.empty();
    }

    public List<SectionDetail> findAllSectionDetails() throws Exception {
        String sql = """
                SELECT s.section_id, s.course_id, c.code, c.title, s.section_code, s.day, s.time, s.room, s.semester, s.year, s.capacity
                FROM sections s
                JOIN courses c ON s.course_id = c.course_id
                ORDER BY c.title, s.section_code
                """;
        List<SectionDetail> list = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new SectionDetail(rs.getInt("section_id"), rs.getInt("course_id"), rs.getString("code"), rs.getString("title"), rs.getString("section_code"), rs.getString("day"), rs.getString("time"), rs.getString("room"), rs.getString("semester"), rs.getInt("year"), rs.getInt("capacity")));
            }
        }
        return list;
    }

    public Optional<SectionDetail> findSectionDetailById(int sectionId) throws Exception {
        String sql = """
                SELECT s.section_id, s.course_id, c.code, c.title, s.section_code, s.day, s.time, s.room, s.semester, s.year, s.capacity
                FROM sections s
                JOIN courses c ON s.course_id = c.course_id
                WHERE s.section_id = ?
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new SectionDetail(rs.getInt("section_id"), rs.getInt("course_id"), rs.getString("code"), rs.getString("title"), rs.getString("section_code"), rs.getString("day"), rs.getString("time"), rs.getString("room"), rs.getString("semester"), rs.getInt("year"), rs.getInt("capacity")));
                }
            }
        }
        return Optional.empty();
    }

    public void createSection(int courseId, String sectionCode, String day, String time, String room, String semester, int year, int capacity) throws Exception {
        String sql = """
                INSERT INTO sections (course_id, section_code, day, time, room, semester, year, capacity)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setString(2, sectionCode);
            stmt.setString(3, day);
            stmt.setString(4, time);
            stmt.setString(5, room);
            stmt.setString(6, semester);
            stmt.setInt(7, year);
            stmt.setInt(8, capacity);
            stmt.executeUpdate();
        }
    }

    public void updateSection(int sectionId, int courseId, String sectionCode, String day, String time, String room, String semester, int year, int capacity) throws Exception {
        String sql = """
                UPDATE sections
                SET course_id=?, section_code=?, day=?, time=?, room=?, semester=?, year=?, capacity=?
                WHERE section_id=?
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.setString(2, sectionCode);
            stmt.setString(3, day);
            stmt.setString(4, time);
            stmt.setString(5, room);
            stmt.setString(6, semester);
            stmt.setInt(7, year);
            stmt.setInt(8, capacity);
            stmt.setInt(9, sectionId);
            stmt.executeUpdate();
        }
    }

    public void deleteSection(int sectionId) throws Exception {
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM sections WHERE section_id=?")) {
            stmt.setInt(1, sectionId);
            stmt.executeUpdate();
        }
    }

    public List<SectionAssignment> fetchSectionAssignments() throws Exception {
        String sql = """
                SELECT s.section_id, s.section_code, c.code AS course_code, c.title AS course_title, s.semester, s.year, s.instructor_id
                FROM sections s
                JOIN courses c ON s.course_id = c.course_id
                ORDER BY c.title, s.section_code
                """;
        List<SectionAssignment> assignments = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer instructorId = rs.getObject("instructor_id") != null ? rs.getInt("instructor_id") : null;
                assignments.add(new SectionAssignment( rs.getInt("section_id"), rs.getString("section_code"), rs.getString("course_code"), rs.getString("course_title"), rs.getString("semester"), rs.getInt("year"), instructorId));
            }
        }
        return assignments;
    }

    public void assignInstructor(int sectionId, Integer instructorId) throws Exception {
        String sql = "UPDATE sections SET instructor_id=? WHERE section_id=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (instructorId == null) {
                // JDBC does not know what type null is, so we have to specify it NULL INTEGER.
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, instructorId);
            }
            stmt.setInt(2, sectionId);
            stmt.executeUpdate();
        }
    }

    public List<SectionDetail> findSectionsByInstructor(int instructorId) throws Exception {
        String sql = """
                SELECT s.section_id, s.course_id, c.code, c.title, s.section_code, s.day, s.time, s.room, s.semester, s.year, s.capacity
                FROM sections s
                JOIN courses c ON s.course_id = c.course_id
                WHERE s.instructor_id = ?
                ORDER BY s.section_code
                """;
        List<SectionDetail> list = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new SectionDetail(rs.getInt("section_id"), rs.getInt("course_id"), rs.getString("code"), rs.getString("title"), rs.getString("section_code"), rs.getString("day"), rs.getString("time"), rs.getString("room"), rs.getString("semester"), rs.getInt("year"), rs.getInt("capacity")));
                }
            }
        }
        return list;
    }
}

