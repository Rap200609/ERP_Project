package edu.univ.erp.ui;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.SectionAvailability;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentRegisterPanel extends JPanel {
    private final int studentId;
    private final StudentApi studentApi;
    private final JTable catalogTable;
    private final DefaultTableModel catalogModel;

    public StudentRegisterPanel(int studentId) {
        this(studentId, new StudentApi());
    }

    public StudentRegisterPanel(int studentId, StudentApi studentApi) {
        this.studentId = studentId;
        this.studentApi = studentApi;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        catalogModel = new DefaultTableModel(new String[]{"Section Code", "Course", "Instructor", "Available Seats", "Enroll"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 4 ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        catalogTable = new JTable(catalogModel);
        catalogTable.setRowHeight(25);
        add(new JScrollPane(catalogTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton registerBtn = new JButton("Register Selected Sections");
        registerBtn.addActionListener(e -> registerSelectedSections());
        bottomPanel.add(registerBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        loadCatalog();

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadCatalog();
            }
        });
    }

    private void loadCatalog() {
        catalogModel.setRowCount(0);
        List<SectionAvailability> sections = studentApi.loadRegistrationCatalog();
        for (SectionAvailability section : sections) {
            String seatsDisplay = section.getAvailableSeats() > 0
                    ? String.valueOf(section.getAvailableSeats())
                    : "FULL";
            catalogModel.addRow(new Object[]{
                    section.getSectionCode(),
                    section.getCourseTitle(),
                    section.getInstructorName(),
                    seatsDisplay,
                    false
            });
        }
    }

    private void registerSelectedSections() {
        List<String> sectionCodes = new ArrayList<>();
        for (int i = 0; i < catalogModel.getRowCount(); i++) {
            Boolean selected = (Boolean) catalogModel.getValueAt(i, 4);
            if (Boolean.TRUE.equals(selected)) {
                sectionCodes.add((String) catalogModel.getValueAt(i, 0));
            }
        }
        ApiResponse response = studentApi.registerSections(studentId, sectionCodes);
        JOptionPane.showMessageDialog(this, response.getMessage(),
                "Registration Results",
                response.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        loadCatalog();
    }
}
