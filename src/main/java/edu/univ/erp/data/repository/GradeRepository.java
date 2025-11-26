package edu.univ.erp.data.repository;

import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.domain.GradeComponent;
import edu.univ.erp.domain.ComponentStats;
import edu.univ.erp.domain.GradeExportRow;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GradeRepository {
    private final DataSource mainDataSource;

    public GradeRepository() {
        this(DatabaseConfig.getMainDataSource());
    }

    public GradeRepository(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
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

    public List<GradeComponent> findGradesForEnrollment(int enrollmentId) throws Exception {
        String sql = "SELECT component, score, max_score, weight FROM grades WHERE enrollment_id=? ORDER BY component";
        List<GradeComponent> components = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    components.add(new GradeComponent(rs.getString("component"),rs.getDouble("score"),rs.getDouble("max_score"),rs.getDouble("weight")));
                }
            }
        }
        return components;
    }

    public void updateGradeScore(int enrollmentId, String component, double score) throws Exception {
        String sql = "UPDATE grades SET score=? WHERE enrollment_id=? AND component=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, score);
            stmt.setInt(2, enrollmentId);
            stmt.setString(3, component);
            stmt.executeUpdate();
        }
    }

    public int insertComponentForSection(int sectionId, String component, double maxScore, double weight) throws Exception {
        String sql = """
                INSERT INTO grades (enrollment_id, component, score, max_score, weight)
                SELECT e.enrollment_id, ?, 0.0, ?, ?
                FROM enrollments e
                WHERE e.section_id = ? AND e.status = 'ENROLLED'
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, component);
            stmt.setDouble(2, maxScore);
            stmt.setDouble(3, weight);
            stmt.setInt(4, sectionId);
            return stmt.executeUpdate();
        }
    }

    public int deleteComponentForSection(int sectionId, String component) throws Exception {
        String sql = """
                DELETE g FROM grades g
                JOIN enrollments e ON g.enrollment_id = e.enrollment_id
                WHERE e.section_id = ? AND g.component = ?
                """;
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            stmt.setString(2, component);
            return stmt.executeUpdate();
        }
    }

    public List<String> findComponentNamesForSection(int sectionId) throws Exception {
        String sql = """
                SELECT DISTINCT g.component
                FROM grades g
                JOIN enrollments e ON g.enrollment_id = e.enrollment_id
                WHERE e.section_id = ?
                ORDER BY g.component
                """;
        List<String> components = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    components.add(rs.getString("component"));
                }
            }
        }
        return components;
    }

    public List<ComponentStats> findComponentStatsForSection(int sectionId) throws Exception {
        String sql = """
                SELECT g.component,
                       AVG(g.score) AS avg_score,
                       MIN(g.score) AS min_score,
                       MAX(g.score) AS max_score
                FROM grades g
                JOIN enrollments e ON g.enrollment_id = e.enrollment_id
                WHERE e.section_id = ?
                GROUP BY g.component
                ORDER BY g.component
                """;
        List<ComponentStats> stats = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sectionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stats.add(new ComponentStats(rs.getString("component"),rs.getDouble("avg_score"),rs.getDouble("min_score"),rs.getDouble("max_score")));
                }
            }
        }
        return stats;
    }

    public List<GradeExportRow> findGradesForExport(int instructorId) throws Exception {
        String sql = """
                SELECT s.section_code, st.roll_no, g.component, g.score, g.final_grade
                FROM grades g
                JOIN enrollments e ON g.enrollment_id = e.enrollment_id
                JOIN students st ON e.student_id = st.user_id
                JOIN sections s ON e.section_id = s.section_id
                WHERE s.instructor_id = ?
                ORDER BY s.section_code, st.roll_no, g.component
                """;
        List<GradeExportRow> rows = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new GradeExportRow(rs.getString("section_code"),rs.getString("roll_no"),rs.getString("component"),rs.getDouble("score"),rs.getObject("final_grade") != null ? rs.getDouble("final_grade") : null));
                }
            }
        }
        return rows;
    }
}

