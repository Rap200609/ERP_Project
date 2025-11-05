package edu.univ.erp.data.dao;

import edu.univ.erp.data.DatabaseConfig;
import edu.univ.erp.domain.Section;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionDao {
    public List<Section> findAll() throws Exception {
        List<Section> sections = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT section_id, course_id, section_code, day, time, room, semester, year, capacity FROM sections")) {
            while (rs.next()) {
                sections.add(map(rs));
            }
        }
        return sections;
    }

    public Section findById(int sectionId) throws Exception {
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT section_id, course_id, section_code, day, time, room, semester, year, capacity FROM sections WHERE section_id=?")) {
            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return map(rs);
            return null;
        }
    }

    public void insert(Section s) throws Exception {
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO sections (course_id, section_code, day, time, room, semester, year, capacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, s.getCourseId());
            stmt.setString(2, s.getSectionCode());
            stmt.setString(3, s.getDay());
            stmt.setString(4, s.getTime());
            stmt.setString(5, s.getRoom());
            stmt.setString(6, s.getSemester());
            stmt.setInt(7, s.getYear());
            stmt.setInt(8, s.getCapacity());
            stmt.executeUpdate();
        }
    }

    public void update(Section s) throws Exception {
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "UPDATE sections SET course_id=?, section_code=?, day=?, time=?, room=?, semester=?, year=?, capacity=? WHERE section_id=?")) {
            stmt.setInt(1, s.getCourseId());
            stmt.setString(2, s.getSectionCode());
            stmt.setString(3, s.getDay());
            stmt.setString(4, s.getTime());
            stmt.setString(5, s.getRoom());
            stmt.setString(6, s.getSemester());
            stmt.setInt(7, s.getYear());
            stmt.setInt(8, s.getCapacity());
            stmt.setInt(9, s.getSectionId());
            stmt.executeUpdate();
        }
    }

    public void delete(int sectionId) throws Exception {
        try (Connection conn = DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM sections WHERE section_id=?")) {
            stmt.setInt(1, sectionId);
            stmt.executeUpdate();
        }
    }

    private Section map(ResultSet rs) throws Exception {
        return new Section(
            rs.getInt("section_id"),
            rs.getInt("course_id"),
            rs.getString("section_code"),
            rs.getString("day"),
            rs.getString("time"),
            rs.getString("room"),
            rs.getString("semester"),
            rs.getInt("year"),
            rs.getInt("capacity")
        );
    }
}


