package edu.univ.erp.ui;

import edu.univ.erp.api.admin.AdminDropDeadlineApi;
import edu.univ.erp.api.common.ApiResponse;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DropDeadlinePanel extends JPanel {
    private final AdminDropDeadlineApi deadlineApi = new AdminDropDeadlineApi();
    private final JLabel deadlineLabel;
    private final JSpinner dateSpinner;
    private final JButton updateButton;

    public DropDeadlinePanel() {
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        UITheme.styleCardPanel(centerPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("Drop Section Deadline");
        titleLabel.setFont(UITheme.FONT_HEADING);
        titleLabel.setForeground(UITheme.PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(titleLabel, gbc);

        // Current Deadline Display
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(30, 20, 10, 20);
        JLabel currentLabel = new JLabel("Current Deadline:");
        currentLabel.setFont(UITheme.FONT_BODY_BOLD);
        centerPanel.add(currentLabel, gbc);

        deadlineLabel = new JLabel();
        deadlineLabel.setFont(UITheme.FONT_BODY);
        deadlineLabel.setForeground(UITheme.ACCENT_WARNING);
        gbc.gridx = 1;
        centerPanel.add(deadlineLabel, gbc);

        // Date Picker
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 20, 10, 20);
        JLabel selectLabel = new JLabel("Set New Deadline:");
        selectLabel.setFont(UITheme.FONT_BODY_BOLD);
        centerPanel.add(selectLabel, gbc);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateModel.setValue(java.util.Date.from(LocalDate.now().plusDays(30).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(200, 36));
        gbc.gridx = 1;
        centerPanel.add(dateSpinner, gbc);

        // Update Button
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 20, 20, 20);
        updateButton = new JButton("Update Deadline");
        UITheme.stylePrimaryButton(updateButton);
        updateButton.addActionListener(e -> updateDeadline());
        centerPanel.add(updateButton, gbc);

        // Info panel
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 20, 20);
        JLabel infoLabel = new JLabel("<html><b>Note:</b> Students can only drop sections before this deadline. After the deadline passes, drop section functionality will be disabled.</html>");
        infoLabel.setFont(UITheme.FONT_SMALL);
        infoLabel.setForeground(UITheme.TEXT_SECONDARY);
        centerPanel.add(infoLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                refreshDeadline();
            }
        });

        refreshDeadline();
    }

    private void refreshDeadline() {
        try {
            LocalDate deadline = deadlineApi.getDropDeadline();
            deadlineLabel.setText(deadline.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            dateSpinner.setValue(java.util.Date.from(deadline.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        } catch (Exception ex) {
            deadlineLabel.setText("Error loading deadline");
            deadlineLabel.setForeground(UITheme.ACCENT_ERROR);
        }
    }

    private void updateDeadline() {
        try {
            java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
            LocalDate newDeadline = selectedDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();

            ApiResponse response = deadlineApi.setDropDeadline(newDeadline);
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(this, 
                    "Drop deadline updated to " + newDeadline.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshDeadline();
            } else {
                JOptionPane.showMessageDialog(this, 
                    response.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error updating deadline: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
