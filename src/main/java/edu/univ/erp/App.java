package edu.univ.erp;

import javax.swing.SwingUtilities;
import edu.univ.erp.ui.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * University ERP System - Main Entry Point
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            FlatLightLaf.setup();
        } 
        catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
