package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private int instructorId;

    public InstructorDashboard(int instructorId) {
        this.instructorId = instructorId;
        setTitle("Instructor Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(180, 0));
        JButton mySectionsBtn = new JButton("My Sections");
        JButton gradeEntryBtn = new JButton("Grade Entry (Live)");
        JButton statsBtn = new JButton("Class Stats");
        JButton exportBtn = new JButton("Export CSV");
        JButton logoutBtn = new JButton("Logout");

        sidebar.add(mySectionsBtn);
        sidebar.add(gradeEntryBtn);
        sidebar.add(statsBtn);
        sidebar.add(exportBtn);
        sidebar.add(logoutBtn);

        // Card Layout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        InstructorSectionsPanel sectionsPanel = new InstructorSectionsPanel(instructorId);
        InstructorGradeEntryPanel gradeEntryPanel = new InstructorGradeEntryPanel(instructorId);
        InstructorStatsPanel statsPanel = new InstructorStatsPanel(instructorId);
        GradesExportPanel exportPanel = new GradesExportPanel(instructorId);

        contentPanel.add(sectionsPanel, "SECTIONS");
        contentPanel.add(gradeEntryPanel, "GRADE_ENTRY");
        contentPanel.add(statsPanel, "STATS");
        contentPanel.add(exportPanel, "EXPORT");

        // Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // Handlers
        mySectionsBtn.addActionListener(e -> cardLayout.show(contentPanel, "SECTIONS"));
        gradeEntryBtn.addActionListener(e -> cardLayout.show(contentPanel, "GRADE_ENTRY"));
        statsBtn.addActionListener(e -> cardLayout.show(contentPanel, "STATS"));
        exportBtn.addActionListener(e -> cardLayout.show(contentPanel, "EXPORT"));
        logoutBtn.addActionListener(e -> {
            dispose();
            System.exit(0);
        });

        cardLayout.show(contentPanel, "SECTIONS");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InstructorDashboard(2).setVisible(true));
    }
}
