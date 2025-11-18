package edu.univ.erp.ui;

import edu.univ.erp.api.admin.AdminMaintenanceApi;
import edu.univ.erp.api.common.ApiResponse;

import javax.swing.*;
import java.awt.*;

public class MaintenanceModePanel extends JPanel {
    private final AdminMaintenanceApi maintenanceApi = new AdminMaintenanceApi();

    private final JLabel statusLabel;
    private final JButton toggleButton;

    public MaintenanceModePanel() {
        setBackground(UITheme.BG_MAIN);
        setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        UITheme.styleCardPanel(centerPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(30, 30, 30, 30);
        gbc.anchor = GridBagConstraints.CENTER;

        statusLabel = new JLabel();
        statusLabel.setFont(UITheme.FONT_HEADING);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        toggleButton = new JButton();
        toggleButton.setPreferredSize(new Dimension(250, 45));

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(statusLabel, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 30, 30, 30);
        centerPanel.add(toggleButton, gbc);
        
        add(centerPanel, BorderLayout.CENTER);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                refreshStatus();
            }
        });

        toggleButton.addActionListener(e -> {
            boolean desiredState = !maintenanceApi.isMaintenanceModeOn();
            ApiResponse response = maintenanceApi.setMaintenanceMode(desiredState);
            if (!response.isSuccess()) {
                JOptionPane.showMessageDialog(MaintenanceModePanel.this, response.getMessage(),
                        "Maintenance Mode", JOptionPane.ERROR_MESSAGE);
            }
            refreshStatus();
        });

        refreshStatus();
    }

    private void refreshStatus() {
        boolean modeOn = maintenanceApi.isMaintenanceModeOn();
        if (modeOn) {
            statusLabel.setText("MAINTENANCE MODE ACTIVE");
            statusLabel.setForeground(UITheme.ACCENT_WARNING);
            toggleButton.setText("Disable Maintenance Mode");
            UITheme.styleSecondaryButton(toggleButton);
        } else {
            statusLabel.setText("Maintenance Mode is OFF");
            statusLabel.setForeground(UITheme.ACCENT_SUCCESS);
            toggleButton.setText("Enable Maintenance Mode");
            UITheme.stylePrimaryButton(toggleButton);
        }
    }
}
