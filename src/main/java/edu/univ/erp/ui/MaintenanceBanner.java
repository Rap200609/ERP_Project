package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class MaintenanceBanner extends JPanel {
    
    public MaintenanceBanner() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 12));
        setBackground(UITheme.ACCENT_WARNING);
        setBorder(new javax.swing.border.EmptyBorder(8, 0, 8, 0));
        
        JLabel iconLabel = new JLabel("⚠");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        iconLabel.setForeground(UITheme.TEXT_PRIMARY);
        add(iconLabel);
        
        JLabel warningLabel = new JLabel("MAINTENANCE MODE ACTIVE - System is in read-only mode");
        warningLabel.setFont(UITheme.FONT_BODY_BOLD);
        warningLabel.setForeground(UITheme.TEXT_PRIMARY);
        add(warningLabel);
        
        setVisible(false); // Hidden by default
    }
    
    public void checkAndDisplay() {
        boolean maintenanceOn = edu.univ.erp.util.MaintenanceManager.isMaintenanceModeOn();
        setVisible(maintenanceOn);
    }
}
