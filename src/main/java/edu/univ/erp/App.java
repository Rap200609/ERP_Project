package edu.univ.erp;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.ui.LoginFrame;

// Main entry point
public class App 
{
    public static void main(String[] args )
    {
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to set FlatLaf look and feel: " + e.getMessage());
        }
        
        // Runs all GUI related code in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
