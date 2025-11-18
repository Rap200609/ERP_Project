package edu.univ.erp.ui;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.SectionDetail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class InstructorSectionsPanel extends JPanel {
    private final InstructorApi instructorApi = new InstructorApi();
    private final DefaultTableModel model;
    private final JTable table;

    public InstructorSectionsPanel(int instructorId) {
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        String[] cols = { "Section", "Course", "Semester", "Year", "Capacity", "Room", "Day", "Time" };
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        UITheme.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
        loadSections(instructorId);
    }

    private void loadSections(int instructorId) {
        model.setRowCount(0);
        for (SectionDetail section : instructorApi.listSections(instructorId)) {
            model.addRow(new Object[]{
                    section.getSectionCode(),
                    section.getCourseDisplay(),
                    section.getSemester(),
                    section.getYear(),
                    section.getCapacity(),
                    section.getRoom(),
                    section.getDay(),
                    section.getTime()
            });
        }
    }
}
