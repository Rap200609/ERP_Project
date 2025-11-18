package edu.univ.erp.ui;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.TimetableEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentTimetablePanel extends JPanel {
    private final int studentId;
    private final StudentApi studentApi;
    private final DefaultTableModel model;

    public StudentTimetablePanel(int studentId) {
        this(studentId, new StudentApi());
    }

    public StudentTimetablePanel(int studentId, StudentApi studentApi) {
        this.studentId = studentId;
        this.studentApi = studentApi;
        setLayout(new BorderLayout());
        String[] cols = {"Section", "Course", "Day", "Time", "Room"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadTimetable();
            }
        });

        loadTimetable();
    }

    private void loadTimetable() {
        model.setRowCount(0);
        List<TimetableEntry> entries = studentApi.loadTimetable(studentId);
        for (TimetableEntry entry : entries) {
            model.addRow(new Object[]{
                    entry.getSectionCode(),
                    entry.getCourseTitle(),
                    entry.getDay(),
                    entry.getTime(),
                    entry.getRoom()
            });
        }
    }
}
