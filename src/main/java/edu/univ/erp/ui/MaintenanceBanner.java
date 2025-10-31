package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;

public class MaintenanceBanner extends JPanel {
    
    public MaintenanceBanner() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBackground(new Color(255, 84, 89)); // Red background
        
        JLabel warningLabel = new JLabel("⚠️ MAINTENANCE MODE ACTIVE - READ-ONLY ACCESS ⚠️");
        warningLabel.setFont(new Font("Arial", Font.BOLD, 16));
        warningLabel.setForeground(Color.WHITE);
        
        add(warningLabel);
        setVisible(false); // Hidden by default
    }
    
    public void checkAndDisplay() {
        boolean maintenanceOn = edu.univ.erp.util.MaintenanceManager.isMaintenanceModeOn();
        setVisible(maintenanceOn);
    }
}
