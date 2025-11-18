package edu.univ.erp.ui;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.EnrolledSection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDropPanel extends JPanel {
    private final int studentId;
    private final StudentApi studentApi;
    private final DefaultTableModel enrolledModel;

    public StudentDropPanel(int studentId) {
        this(studentId, new StudentApi());
    }

    public StudentDropPanel(int studentId, StudentApi studentApi) {
        this.studentId = studentId;
        this.studentApi = studentApi;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

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
        JButton dropBtn = new JButton("Drop Selected Sections");
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
    }

    private void dropSelectedSections() {
        List<String> sectionCodes = new ArrayList<>();
        for (int i = 0; i < enrolledModel.getRowCount(); i++) {
            Boolean selected = (Boolean) enrolledModel.getValueAt(i, 3);
            if (Boolean.TRUE.equals(selected)) {
                sectionCodes.add((String) enrolledModel.getValueAt(i, 0));
            }
        }
        ApiResponse response = studentApi.dropSections(studentId, sectionCodes);
        JOptionPane.showMessageDialog(this, response.getMessage(),
                response.isSuccess() ? "Drop Successful" : "Drop Error",
                response.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        loadEnrolledSections();
    }
}
