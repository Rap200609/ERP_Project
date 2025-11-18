package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;

import edu.univ.erp.ui.theme.AppColors;

public class MaintenanceBanner extends JPanel {
    
    public MaintenanceBanner() {
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setBackground(AppColors.ERROR);
        setBorder(new EmptyBorder(12, 16, 12, 16));
        
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
