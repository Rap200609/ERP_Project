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
        setLayout(new BorderLayout());
        String[] cols = {"Course Code", "Title", "Credits", "Instructor", "Capacity"};
        model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
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
                    entry.getInstructorIdentifier(),
                    entry.getCapacity()
            });
        }
    }
}
