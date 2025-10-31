package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MaintenanceModePanel extends JPanel {
    private JLabel statusLabel;
    private JButton toggleButton;

    public MaintenanceModePanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 22));
        statusLabel.setForeground(Color.RED);

        toggleButton = new JButton();

        gbc.gridx = 0; gbc.gridy = 0;
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
            boolean mode = getCurrentMode();
            setMaintenanceMode(!mode);
            refreshStatus();
        });

        refreshStatus();
    }

    private boolean getCurrentMode() {
        boolean mode = false;
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT setting_value FROM settings WHERE setting_key='maintenance_mode'")) {
            if (rs.next()) {
                mode = "ON".equalsIgnoreCase(rs.getString("setting_value"));
            }
        } catch (Exception ex) { }
        return mode;
    }

    private void setMaintenanceMode(boolean modeOn) {
        String valueStr = modeOn ? "ON" : "OFF";
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE settings SET setting_value=? WHERE setting_key='maintenance_mode'")) {
            stmt.setString(1, valueStr);
            stmt.executeUpdate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to update mode: " + ex.getMessage());
        }
    }

    private void refreshStatus() {
        if (getCurrentMode()) {
            statusLabel.setText("MAINTENANCE MODE ACTIVE");
            toggleButton.setText("Disable Maintenance Mode");
        } else {
            statusLabel.setText("Maintenance Mode is OFF");
            toggleButton.setText("Enable Maintenance Mode");
        }
    }
}
