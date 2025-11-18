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
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 22));
        statusLabel.setForeground(Color.RED);

        toggleButton = new JButton();

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(statusLabel, gbc);
        gbc.gridy = 1;
        add(toggleButton, gbc);

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
            toggleButton.setText("Disable Maintenance Mode");
        } else {
            statusLabel.setText("Maintenance Mode is OFF");
            toggleButton.setText("Enable Maintenance Mode");
        }
    }
}
