package edu.univ.erp.ui;

import edu.univ.erp.api.catalog.CatalogApi;
import edu.univ.erp.domain.CourseCatalogEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentCourseCatalogPanel extends JPanel {
    private final DefaultTableModel model;
    private final CatalogApi catalogApi;

    public StudentCourseCatalogPanel() {
        this(new CatalogApi());
    }

    public StudentCourseCatalogPanel(CatalogApi catalogApi) {
        this.catalogApi = catalogApi;
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(UITheme.BG_MAIN);
        headerPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 15, 0));
        JLabel titleLabel = new JLabel("Course Catalog");
        UITheme.styleHeadingLabel(titleLabel);
        titleLabel.setFont(UITheme.FONT_HEADING);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // Table panel
        String[] cols = {"Course Code", "Title", "Credits", "Section", "Instructor", "Capacity"};
        model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        JScrollPane scrollPane = new JScrollPane(table);
        UITheme.styleScrollPane(scrollPane);
        add(scrollPane, BorderLayout.CENTER);
        
        loadCatalog();
    }

    private void loadCatalog() {
        model.setRowCount(0);
        List<CourseCatalogEntry> entries = catalogApi.loadCatalog();
        for (CourseCatalogEntry entry : entries) {
            model.addRow(new Object[]{
                    entry.getCourseCode(),
                    entry.getTitle(),
                    entry.getCredits(),
                    entry.getSectionCode() != null ? entry.getSectionCode() : "-",
                    entry.getInstructorIdentifier(),
                    entry.getCapacity()
            });
        }
    }
}
