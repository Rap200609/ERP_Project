package edu.univ.erp;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import edu.univ.erp.ui.LoginFrame;
import edu.univ.erp.ui.theme.AppColors;

public class App 
{
    public static void main( String[] args )
    {
        try {
            // Global Theme Setup
            FlatLightLaf.setup();
            
            // Override specific defaults for a softer look
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("TabbedPane.showTabSeparators", true);
            
            // Set global font
            java.awt.Font font = new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14);
            javax.swing.plaf.FontUIResource f = new javax.swing.plaf.FontUIResource(font);
            java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, f);
                }
            }
        } 
        catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}