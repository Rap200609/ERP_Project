package edu.univ.erp.data.repository;

import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.domain.EnrolledSection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrollmentRepository {
    private final DataSource mainDataSource;

    public EnrollmentRepository() {
        this(DatabaseConfig.getMainDataSource());
    }

    public EnrollmentRepository(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    public boolean isStudentAlreadyEnrolled(int studentId, int sectionId) throws Exception {
        String sql = "SELECT enrollment_id FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isStudentEnrolledInCourse(int studentId, int courseId) throws Exception {
        String sql = """
                SELECT enrollment_id FROM enrollments e
                JOIN sections s ON e.section_id = s.section_id
                WHERE e.student_id = ? AND s.course_id = ? AND e.status = 'ENROLLED'
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int countEnrolledInSection(int sectionId) throws Exception {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE section_id = ? AND status = 'ENROLLED'";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void enrollStudent(int studentId, int sectionId) throws Exception {
        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'ENROLLED')";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.executeUpdate();
        }
    }

    public List<EnrolledSection> findEnrolledSections(int studentId) throws Exception {
        String sql = """
                SELECT s.section_id,
                       s.section_code,
                       c.title,
                       COALESCE(u.username, 'TBA') AS instructor
                FROM enrollments e
                JOIN sections s ON e.section_id = s.section_id
                JOIN courses c ON s.course_id = c.course_id
                LEFT JOIN instructors i ON s.instructor_id = i.user_id
                LEFT JOIN erp_auth.users_auth u ON i.user_id = u.user_id
                WHERE e.student_id = ? AND e.status = 'ENROLLED'
                ORDER BY c.title
                """;
        List<EnrolledSection> list = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new EnrolledSection(
                            rs.getInt("section_id"),
                            rs.getString("section_code"),
                            rs.getString("title"),
                            rs.getString("instructor")
                    ));
                }
            }
        }
        return list;
    }

    public List<edu.univ.erp.domain.TimetableEntry> findTimetableEntries(int studentId) throws Exception {
        String sql = """
                SELECT s.section_code,
                       c.title,
                       s.day,
                       s.time,
                       s.room
                FROM enrollments e
                JOIN sections s ON e.section_id = s.section_id
                JOIN courses c ON s.course_id = c.course_id
                WHERE e.student_id = ? AND e.status = 'ENROLLED'
                """;
        List<edu.univ.erp.domain.TimetableEntry> entries = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(new edu.univ.erp.domain.TimetableEntry(
                            rs.getString("section_code"),
                            rs.getString("title"),
                            rs.getString("day"),
                            rs.getString("time"),
                            rs.getString("room")
                    ));
                }
            }
        }
        return entries;
    }

    public List<edu.univ.erp.domain.StudentCourseOption> findCourseOptions(int studentId) throws Exception {
        String sql = """
                SELECT s.section_id,
                       s.section_code,
                       c.title
                FROM enrollments e
                JOIN sections s ON e.section_id = s.section_id
                JOIN courses c ON s.course_id = c.course_id
                WHERE e.student_id = ? AND e.status = 'ENROLLED'
                ORDER BY c.title, s.section_code
                """;
        List<edu.univ.erp.domain.StudentCourseOption> options = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    options.add(new edu.univ.erp.domain.StudentCourseOption(
                            rs.getInt("section_id"),
                            rs.getString("section_code"),
                            rs.getString("title")
                    ));
                }
            }
        }
        return options;
    }

    public int dropEnrollment(int studentId, String sectionCode) throws Exception {
        String sql = """
                DELETE e FROM enrollments e
                JOIN sections s ON e.section_id = s.section_id
                WHERE e.student_id = ? AND s.section_code = ?
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setString(2, sectionCode);
            return stmt.executeUpdate();
        }
    }

    public int dropEnrollment(int studentId, int sectionId) throws Exception {
        String sql = """
                DELETE FROM enrollments
                WHERE student_id = ? AND section_id = ?
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            return stmt.executeUpdate();
        }
    }

    public List<edu.univ.erp.domain.StudentProfile> findStudentsInSection(int sectionId) throws Exception {
        String sql = """
                SELECT st.user_id,
                       st.roll_no,
                       st.program,
                       st.year,
                       st.email
                FROM enrollments e
                JOIN students st ON e.student_id = st.user_id
                WHERE e.section_id = ? AND e.status = 'ENROLLED'
                ORDER BY st.roll_no
                """;
        List<edu.univ.erp.domain.StudentProfile> students = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new edu.univ.erp.domain.StudentProfile(
                            rs.getInt("user_id"),
                            rs.getString("roll_no"),
                            rs.getString("program"),
                            rs.getInt("year"),
                            rs.getString("email")
                    ));
                }
            }
        }
        return students;
    }

    public Optional<Integer> findEnrollmentId(int studentId, int sectionId) throws Exception {
        String sql = "SELECT enrollment_id FROM enrollments WHERE student_id=? AND section_id=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("enrollment_id"));
                }
            }
        }
        return Optional.empty();
    }
}
