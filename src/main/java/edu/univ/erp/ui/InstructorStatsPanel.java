package edu.univ.erp.ui;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.ComponentStats;
import edu.univ.erp.domain.SectionDetail;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstructorStatsPanel extends JPanel {
    private final InstructorApi instructorApi = new InstructorApi();
    private final JComboBox<String> sectionBox;
    private final JLabel sectionSummaryLabel;
    private final DefaultTableModel statsModel;
    private final JTable statsTable;
    private final int instructorId;
    private final Map<String, Integer> sectionIdMap = new HashMap<>();

    public InstructorStatsPanel(int instructorId) {
        this.instructorId = instructorId;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        sectionBox = new JComboBox<>();
        UITheme.styleComboBox(sectionBox);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(UITheme.BG_MAIN);
        topPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        JLabel sectionLabel = new JLabel("Section:");
        UITheme.styleLabel(sectionLabel, true);
        topPanel.add(sectionLabel);
        topPanel.add(sectionBox);
        JButton refreshBtn = new JButton("Show Stats");
        UITheme.stylePrimaryButton(refreshBtn);
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        sectionSummaryLabel = new JLabel("Select a section to view statistics");
        UITheme.styleLabel(sectionSummaryLabel, true);

        statsModel = new DefaultTableModel(new String[]{"Component", "Average", "Minimum", "Maximum"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        statsTable = new JTable(statsModel);
        UITheme.styleTable(statsTable);
        JScrollPane tableScroll = new JScrollPane(statsTable);
        UITheme.styleScrollPane(tableScroll);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(UITheme.BG_MAIN);
        centerPanel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        centerPanel.add(sectionSummaryLabel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        loadSections();

        refreshBtn.addActionListener(e -> showStats());
        sectionBox.addActionListener(e -> showStats());
    }

    private void loadSections() {
        sectionBox.removeAllItems();
        sectionIdMap.clear();
        List<SectionDetail> sections = instructorApi.listSections(instructorId);
        for (SectionDetail section : sections) {
            String display = String.format("%s - %s", section.getCourseDisplay(), section.getSectionCode());
            sectionIdMap.put(display, section.getSectionId());
            sectionBox.addItem(display);
        }
        if (sectionBox.getItemCount() == 0) {
            sectionSummaryLabel.setText("No sections assigned.");
        }
    }

    private void showStats() {
        statsModel.setRowCount(0);
        String selected = (String) sectionBox.getSelectedItem();
        if (selected == null) {
            sectionSummaryLabel.setText("Select a section to view statistics");
            return;
        }
        Integer sectionId = sectionIdMap.get(selected);
        if (sectionId == null) {
            sectionSummaryLabel.setText("Invalid section selection");
            return;
        }
        sectionSummaryLabel.setText("Statistics for: " + selected);

        List<ComponentStats> stats = instructorApi.loadComponentStats(sectionId);
        if (stats.isEmpty()) {
            sectionSummaryLabel.setText("No statistics available for: " + selected);
            return;
        }
        for (ComponentStats stat : stats) {
            statsModel.addRow(new Object[]{
                    stat.getComponent(),
                    String.format("%.2f", stat.getAverage()),
                    String.format("%.2f", stat.getMinimum()),
                    String.format("%.2f", stat.getMaximum())
            });
        }
    }
}
