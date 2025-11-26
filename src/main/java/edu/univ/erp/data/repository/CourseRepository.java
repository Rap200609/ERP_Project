package edu.univ.erp.data.repository;

import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.domain.CourseCatalogEntry;
import edu.univ.erp.domain.CourseDetail;
import edu.univ.erp.domain.CourseOption;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseRepository {
    private final DataSource mainDataSource;

    public CourseRepository() {
        this(DatabaseConfig.getMainDataSource());
    }

    public CourseRepository(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    // I addded this method to also show section along with the course details in the catalog panel of student dashboard.
    public List<CourseCatalogEntry> fetchCatalogEntries() throws Exception {
        // Left join to include each course even if it has no matching section or instructor.
        String sql = "SELECT c.code, c.title, c.credits, s.section_code, i.employee_id, s.capacity " + "FROM courses c " + "LEFT JOIN sections s ON c.course_id = s.course_id " + "LEFT JOIN instructors i ON s.instructor_id = i.user_id " + "ORDER BY c.code, s.section_code";
        List<CourseCatalogEntry> entries = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                entries.add(new CourseCatalogEntry(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getInt(6)));
            }
        }
        return entries;
    }

    public List<CourseDetail> findAllCourses() throws Exception {
        String sql = "SELECT course_id, code, title, credits, description FROM courses";
        List<CourseDetail> courses = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(new CourseDetail(rs.getInt("course_id"),rs.getString("code"),rs.getString("title"),rs.getInt("credits"),rs.getString("description")));
            }
        }
        return courses;
    }

    public Optional<CourseDetail> findCourseById(int courseId) throws Exception {
        String sql = "SELECT course_id, code, title, credits, description FROM courses WHERE course_id=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new CourseDetail(rs.getInt("course_id"),rs.getString("code"),rs.getString("title"),rs.getInt("credits"),rs.getString("description")));
                }
            }
        }
        return Optional.empty();
    }

    public List<CourseOption> fetchCourseOptions() throws Exception {
        String sql = "SELECT course_id, code, title FROM courses ORDER BY title";
        List<CourseOption> options = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                options.add(new CourseOption(rs.getInt("course_id"),rs.getString("code"),rs.getString("title")));
            }
        }
        return options;
    }

    public List<String> fetchAllCourseCodes() throws Exception {
        List<String> codes = new ArrayList<>();
        try (Connection conn = mainDataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT code FROM courses ORDER BY code")) {
            while (rs.next()) {
                codes.add(rs.getString("code"));
            }
        }
        return codes;
    }

    public int createCourse(String code, String title, int credits, String description) throws Exception {
        String sql = "INSERT INTO courses (code, title, credits, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            stmt.setString(4, description);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        // IllegalStateException is used because its a runtime exception used to indicate that a method has not been invoked at correct time.
        throw new IllegalStateException("Failed to insert course");
    }

    public void updateCourse(int courseId, String code, String title, int credits, String description) throws Exception {
        String sql = "UPDATE courses SET code=?, title=?, credits=?, description=? WHERE course_id=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            stmt.setString(2, title);
            stmt.setInt(3, credits);
            stmt.setString(4, description);
            stmt.setInt(5, courseId);
            stmt.executeUpdate();
        }
    }

    public void deleteCourse(int courseId) throws Exception {
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM courses WHERE course_id=?")) {
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
        }
    }
}

