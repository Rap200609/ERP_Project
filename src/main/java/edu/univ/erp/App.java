package edu.univ.erp;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.ui.LoginFrame;

/**
 * University ERP System - Main Entry Point
 */
public class App 
{
    public static void main( String[] args )
    {
        // Set modern look and feel
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf look and feel: " + e.getMessage());
            // Continue with default look and feel
        }
        
        // Runs all GUI related code in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
