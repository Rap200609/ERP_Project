package edu.univ.erp;

import javax.swing.SwingUtilities;
import edu.univ.erp.ui.LoginFrame;
import edu.univ.erp.ui.theme.UniversityThemeLaf;

/**
 * University ERP System - Main Entry Point
 */
public class App 
{
    public static void main( String[] args )
    {
        // Set modern look and feel
        try {
            UniversityThemeLaf.setup();
        } 
        catch (Exception e) {
            System.err.println("Failed to set University theme: " + e.getMessage());
            // Continue with default look and feel
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
