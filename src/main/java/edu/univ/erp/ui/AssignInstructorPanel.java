package edu.univ.erp.ui;

import edu.univ.erp.api.admin.AdminSectionApi;
import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.InstructorOption;
import edu.univ.erp.domain.SectionAssignment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AssignInstructorPanel extends JPanel {
    private final AdminSectionApi sectionApi = new AdminSectionApi();
    private final JTable table;
    private final DefaultTableModel tableModel;
    private List<InstructorOption> instructorOptions = List.of();

    public AssignInstructorPanel() {
        setLayout(new BorderLayout());
        String[] cols = {
                "Section ID",
                "Section",
                "Course",
                "Semester",
                "Year",
                "Instructor",
                "Update"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        table = new JTable(tableModel);
        table.getColumn("Update").setCellRenderer(new ButtonRenderer());
        table.getColumn("Update").setCellEditor(new ButtonEditor(new JCheckBox()));
        add(new JScrollPane(table), BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                loadData();
            }
        });

        loadData();
    }

    private void loadData() {
        instructorOptions = sectionApi.listInstructorOptions();
        tableModel.setRowCount(0);
        for (SectionAssignment assignment : sectionApi.listSectionAssignments()) {
            tableModel.addRow(new Object[]{
                    assignment.getSectionId(),
                    assignment.getSectionCode(),
                    assignment.getCourseDisplay(),
                    assignment.getSemester(),
                    assignment.getYear(),
                    resolveInstructorName(assignment.getInstructorId()),
                    "Update"
            });
        }
    }

    private String resolveInstructorName(Integer instructorId) {
        if (instructorId == null) {
            return "(none)";
        }
        return instructorOptions.stream()
                .filter(option -> option.getInstructorId() == instructorId.intValue())
                .map(InstructorOption::getDisplayName)
                .findFirst()
                .orElse("(none)");
    }

    private void assignInstructor(int row) {
        int sectionId = (int) tableModel.getValueAt(row, 0);
        JComboBox<String> combo = new JComboBox<>(
                instructorOptions.stream()
                        .map(InstructorOption::getDisplayName)
                        .toArray(String[]::new));
        combo.insertItemAt("(Clear Assignment)", 0);
        combo.setSelectedIndex(0);
        int option = JOptionPane.showConfirmDialog(
                this,
                combo,
                "Select Instructor",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }
        Integer instructorId = null;
        int selectedIndex = combo.getSelectedIndex();
        if (selectedIndex > 0) {
            instructorId = instructorOptions.get(selectedIndex - 1).getInstructorId();
        }
        ApiResponse response = sectionApi.assignInstructor(sectionId, instructorId);
        if (!response.isSuccess()) {
            JOptionPane.showMessageDialog(this, response.getMessage());
        }
        loadData();
    }

    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        ButtonRenderer() {
            super("Update");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int row;

        ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Update");
            button.addActionListener(e -> {
                assignInstructor(row);
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table,
                Object value,
                boolean isSelected,
                int row,
                int column) {
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "Update";
        }
    }
}

