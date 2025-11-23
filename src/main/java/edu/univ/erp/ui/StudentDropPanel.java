package edu.univ.erp.ui;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.EnrolledSection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudentDropPanel extends JPanel {
    private final int studentId;
    private final StudentApi studentApi;
    private final DefaultTableModel enrolledModel;
    private final JButton dropBtn;
    private final JLabel deadlineLabel;

    public StudentDropPanel(int studentId) {
        this(studentId, new StudentApi());
    }

    public StudentDropPanel(int studentId, StudentApi studentApi) {
        this.studentId = studentId;
        this.studentApi = studentApi;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        // Top panel with deadline info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BG_MAIN);
        topPanel.setBorder(new javax.swing.border.EmptyBorder(15, 15, 15, 15));
        
        JLabel infoLabel = new JLabel("Drop Section Deadline:");
        infoLabel.setFont(UITheme.FONT_BODY_BOLD);
        infoLabel.setForeground(UITheme.TEXT_PRIMARY);
        
        deadlineLabel = new JLabel();
        deadlineLabel.setFont(UITheme.FONT_BODY);
        
        topPanel.add(infoLabel, BorderLayout.WEST);
        topPanel.add(deadlineLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        enrolledModel = new DefaultTableModel(new String[]{"Section Code", "Course", "Instructor", "Drop"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        JTable enrolledTable = new JTable(enrolledModel);
        UITheme.styleTable(enrolledTable);
        JScrollPane scrollPane = new JScrollPane(enrolledTable);
        UITheme.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UITheme.BG_MAIN);
        bottomPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        dropBtn = new JButton("Drop Selected Sections");
        UITheme.stylePrimaryButton(dropBtn);
        dropBtn.addActionListener(e -> dropSelectedSections());
        bottomPanel.add(dropBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        loadEnrolledSections();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadEnrolledSections();
            }
        });
    }

    private void loadEnrolledSections() {
        enrolledModel.setRowCount(0);
        List<EnrolledSection> sections = studentApi.loadEnrolledSections(studentId);
        for (EnrolledSection section : sections) {
            enrolledModel.addRow(new Object[]{
                    section.getSectionCode(),
                    section.getCourseTitle(),
                    section.getInstructorName(),
                    false
            });
        }
        
        // Update deadline info
        updateDeadlineInfo();
    }

    private void updateDeadlineInfo() {
        LocalDate deadline = studentApi.getDropDeadline();
        if (deadline != null) {
            deadlineLabel.setText(deadline.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            
            if (studentApi.isDropDeadlinePassed()) {
                deadlineLabel.setForeground(UITheme.ACCENT_ERROR);
                dropBtn.setEnabled(false);
                dropBtn.setText("Drop Deadline Passed - Cannot Drop Sections");
                dropBtn.setToolTipText("The drop deadline has passed. No more sections can be dropped.");
            } else {
                deadlineLabel.setForeground(UITheme.ACCENT_SUCCESS);
                dropBtn.setEnabled(true);
                dropBtn.setText("Drop Selected Sections");
                dropBtn.setToolTipText("Drop sections before the deadline");
            }
        } else {
            deadlineLabel.setText("Unable to load deadline");
            deadlineLabel.setForeground(UITheme.ACCENT_ERROR);
        }
    }

    private void dropSelectedSections() {
        List<Integer> sectionIds = new ArrayList<>();
        for (int i = 0; i < enrolledModel.getRowCount(); i++) {
            Boolean selected = (Boolean) enrolledModel.getValueAt(i, 3);
            if (Boolean.TRUE.equals(selected)) {
                String sectionCode = (String) enrolledModel.getValueAt(i, 0);
                String courseTitle = (String) enrolledModel.getValueAt(i, 1);
                sectionIds.add(findSectionId(sectionCode, courseTitle));
            }
        }
        ApiResponse response = studentApi.dropSections(studentId, sectionIds);
        JOptionPane.showMessageDialog(this, response.getMessage(),
                response.isSuccess() ? "Drop Successful" : "Drop Error",
                response.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        loadEnrolledSections();
    }

    private int findSectionId(String sectionCode, String courseTitle) {
        List<EnrolledSection> sections = studentApi.loadEnrolledSections(studentId);
        for (EnrolledSection section : sections) {
            if (section.getSectionCode().equals(sectionCode) && section.getCourseTitle().equals(courseTitle)) {
                return section.getSectionId();
            }
        }
        return -1;
    }
}
