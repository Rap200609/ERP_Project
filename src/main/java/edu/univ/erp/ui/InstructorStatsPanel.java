package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InstructorStatsPanel extends JPanel {
    private JComboBox<String> sectionBox;
    private JTextArea statsArea;
    private int instructorId;

    public InstructorStatsPanel(int instructorId) {
        this.instructorId = instructorId;
        setLayout(new BorderLayout());

        sectionBox = new JComboBox<>();
        statsArea = new JTextArea(10, 60);
        statsArea.setEditable(false);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Section:"));
        topPanel.add(sectionBox);
        JButton refreshBtn = new JButton("Show Stats");
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(statsArea), BorderLayout.CENTER);

        loadSections();

        refreshBtn.addActionListener(e -> showStats());

        if (sectionBox.getItemCount() > 0) showStats();
    }

    private void loadSections() {
        sectionBox.removeAllItems();
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT section_id, section_code FROM sections WHERE instructor_id=?")) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sectionBox.addItem(rs.getInt(1) + ":" + rs.getString(2));
            }
        } catch (Exception ex) { }
    }

    private void showStats() {
        statsArea.setText("");
        if (sectionBox.getSelectedItem() == null) return;
        String secStr = (String) sectionBox.getSelectedItem();
        int sectionId = Integer.parseInt(secStr.split(":")[0].trim());

        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT component, AVG(score) AS avg_score, MIN(score) AS min_score, MAX(score) AS max_score " +
                "FROM grades WHERE enrollment_id IN (SELECT enrollment_id FROM enrollments WHERE section_id=?) " +
                "GROUP BY component"
            );
            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            statsArea.append("Component | Avg | Min | Max\n");
            statsArea.append("-----------------------------------\n");
            while (rs.next()) {
                statsArea.append(rs.getString("component") + " | " +
                        rs.getDouble("avg_score") + " | " +
                        rs.getDouble("min_score") + " | " +
                        rs.getDouble("max_score") + "\n");
            }
        } catch (Exception ex) {
            statsArea.append("Error: " + ex.getMessage());
        }
    }
}
