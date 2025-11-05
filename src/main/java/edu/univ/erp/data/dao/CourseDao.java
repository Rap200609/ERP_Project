package edu.univ.erp.data.dao;

import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.domain.Course;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT course_id, code, title FROM courses")) {
            while (rs.next()) {
                courses.add(new Course(
                    rs.getInt("course_id"),
                    rs.getString("code"),
                    rs.getString("title")
                ));
            }
        } catch (Exception ignored) { }
        return courses;
    }
}


