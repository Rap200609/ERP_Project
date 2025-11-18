package edu.univ.erp.ui;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.ComponentStats;
import edu.univ.erp.domain.SectionDetail;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class InstructorStatsPanel extends JPanel {
    private final InstructorApi instructorApi = new InstructorApi();
    private final JComboBox<String> sectionBox;
    private final JTextArea statsArea;
    private final int instructorId;
    private final Map<String, Integer> sectionIdMap = new HashMap<>();

    public InstructorStatsPanel(int instructorId) {
        this.instructorId = instructorId;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());

        sectionBox = new JComboBox<>();
        UITheme.styleComboBox(sectionBox);
        statsArea = new JTextArea(10, 60);
        statsArea.setEditable(false);
        statsArea.setFont(UITheme.FONT_BODY);
        statsArea.setBackground(UITheme.BG_PANEL);
        statsArea.setBorder(UITheme.BORDER_FIELD);

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
        JScrollPane scrollPane = new JScrollPane(statsArea);
        UITheme.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);

        loadSections();

        refreshBtn.addActionListener(e -> showStats());

        if (sectionBox.getItemCount() > 0) {
            showStats();
        }
    }

    private void loadSections() {
        sectionBox.removeAllItems();
        sectionIdMap.clear();
        for (SectionDetail section : instructorApi.listSections(instructorId)) {
            String display = section.getSectionId() + ":" + section.getSectionCode();
            sectionIdMap.put(display, section.getSectionId());
            sectionBox.addItem(display);
        }
    }

    private void showStats() {
        statsArea.setText("");
        if (sectionBox.getSelectedItem() == null) {
            return;
        }
        String selected = (String) sectionBox.getSelectedItem();
        Integer sectionId = sectionIdMap.get(selected);
        if (sectionId == null) {
            statsArea.append("Error: Invalid section selection");
            return;
        }

        statsArea.append("Component | Avg | Min | Max\n");
        statsArea.append("-----------------------------------\n");
        for (ComponentStats stat : instructorApi.loadComponentStats(sectionId)) {
            statsArea.append(String.format("%s | %.2f | %.2f | %.2f\n",
                    stat.getComponent(),
                    stat.getAverage(),
                    stat.getMinimum(),
                    stat.getMaximum()));
        }
    }
}
