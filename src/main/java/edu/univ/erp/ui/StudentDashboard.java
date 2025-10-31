package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private int studentId;

    public StudentDashboard(int studentId) {
        this.studentId = studentId;
        setTitle("Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        JPanel sidebar = new JPanel(new GridLayout(0, 1, 0, 10));
        sidebar.setPreferredSize(new Dimension(180, 0));
        JButton browseBtn = new JButton("Course Catalog");
        JButton registerBtn = new JButton("Register Section");
        JButton dropBtn = new JButton("Drop Section");
        JButton timetableBtn = new JButton("My Timetable");
        JButton gradesBtn = new JButton("View Grades");
        JButton transcriptBtn = new JButton("Download Transcript");
        JButton logoutBtn = new JButton("Logout");

        sidebar.add(browseBtn);
        sidebar.add(registerBtn);
        sidebar.add(dropBtn);
        sidebar.add(timetableBtn);
        sidebar.add(gradesBtn);
        sidebar.add(transcriptBtn);
        sidebar.add(logoutBtn);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        StudentCourseCatalogPanel coursePanel = new StudentCourseCatalogPanel();
        StudentRegisterPanel registerPanel = new StudentRegisterPanel(studentId);
        StudentDropPanel dropPanel = new StudentDropPanel(studentId);
        StudentTimetablePanel timetablePanel = new StudentTimetablePanel(studentId);
        StudentGradesPanel gradesPanel = new StudentGradesPanel(studentId);
        StudentTranscriptPanel transcriptPanel = new StudentTranscriptPanel(studentId);

        contentPanel.add(coursePanel, "BROWSE");
        contentPanel.add(registerPanel, "REGISTER");
        contentPanel.add(dropPanel, "DROP");
        contentPanel.add(timetablePanel, "TIMETABLE");
        contentPanel.add(gradesPanel, "GRADES");
        contentPanel.add(transcriptPanel, "TRANSCRIPT");

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        browseBtn.addActionListener(e -> cardLayout.show(contentPanel, "BROWSE"));
        registerBtn.addActionListener(e -> cardLayout.show(contentPanel, "REGISTER"));
        dropBtn.addActionListener(e -> cardLayout.show(contentPanel, "DROP"));
        timetableBtn.addActionListener(e -> cardLayout.show(contentPanel, "TIMETABLE"));
        gradesBtn.addActionListener(e -> cardLayout.show(contentPanel, "GRADES"));
        transcriptBtn.addActionListener(e -> cardLayout.show(contentPanel, "TRANSCRIPT"));
        logoutBtn.addActionListener(e -> { dispose(); System.exit(0); });

        cardLayout.show(contentPanel, "BROWSE");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard(3).setVisible(true));
    }
}
