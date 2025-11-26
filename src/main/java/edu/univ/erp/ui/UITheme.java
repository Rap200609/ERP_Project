package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Centralized UI Theme and Styling Utility
 * Provides consistent institutional styling across the ERP application
 */
public class UITheme {
    
    /**
     *  COLOR PALETTE 
     */

    // Primary Institutional Colors - Teal Theme
    public static final Color PRIMARY_DARK = new Color(0, 77, 77);       // Deep Teal
    public static final Color PRIMARY = new Color(0, 128, 128);          // Medium Teal
    public static final Color PRIMARY_LIGHT = new Color(0, 153, 153);    // Light Teal
    public static final Color PRIMARY_VERY_LIGHT = new Color(230, 250, 250); // Very Light Teal
    
    // Secondary Colors
    public static final Color SECONDARY = new Color(52, 73, 94);         // Slate Gray
    public static final Color SECONDARY_LIGHT = new Color(108, 117, 125); // Light Gray
    
    // Background Colors
    public static final Color BG_MAIN = new Color(248, 249, 250);        // Off-White
    public static final Color BG_PANEL = Color.WHITE;                    // Pure White
    public static final Color BG_SIDEBAR = new Color(0, 77, 77);         // Dark Teal Sidebar
    public static final Color BG_HOVER = new Color(240, 242, 245);       // Hover Gray
    
    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);      // Dark Text
    public static final Color TEXT_SECONDARY = new Color(73, 80, 87);    // Medium Text
    public static final Color TEXT_LIGHT = new Color(108, 117, 125);     // Light Text
    public static final Color TEXT_WHITE = Color.WHITE;                  // White Text
    
    // Accent Colors
    public static final Color ACCENT_SUCCESS = new Color(40, 167, 69);   // Green
    public static final Color ACCENT_WARNING = new Color(255, 193, 7);   // Yellow
    public static final Color ACCENT_ERROR = new Color(220, 53, 69);     // Red
    public static final Color ACCENT_INFO = new Color(23, 162, 184);     // Cyan
    
    // Border Colors
    public static final Color BORDER_LIGHT = new Color(222, 226, 230);   // Light Border
    public static final Color BORDER_MEDIUM = new Color(206, 212, 218);  // Medium Border
    public static final Color BORDER_DARK = new Color(173, 181, 189);    // Dark Border
    
    // Table Colors
    public static final Color TABLE_HEADER_BG = new Color(0, 77, 77);    // Dark Teal Header
    public static final Color TABLE_HEADER_FG = Color.WHITE;             // White Header Text
    public static final Color TABLE_ROW_EVEN = Color.WHITE;              // Even Row
    public static final Color TABLE_ROW_ODD = new Color(248, 249, 250);  // Odd Row
    public static final Color TABLE_ROW_HOVER = new Color(240, 242, 245); // Row Hover
    
    /**
     * FONTS
     */

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    
    /**
     *  BORDERS 
     */
    public static final Border BORDER_NONE = new EmptyBorder(0, 0, 0, 0);
    public static final Border BORDER_PANEL = new CompoundBorder(
        new LineBorder(BORDER_LIGHT, 1, true),
        new EmptyBorder(15, 15, 15, 15)
    );
    public static final Border BORDER_FIELD = new CompoundBorder(
        new LineBorder(BORDER_MEDIUM, 1),
        new EmptyBorder(8, 10, 8, 10)
    );
    
    /**
     * BUTTON STYLING
     */
    
    /**
     * Styles a button with the primary theme
     */
    public static void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY);
        button.setForeground(TEXT_WHITE);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 38));
        button.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY_DARK, 1),
            new EmptyBorder(8, 20, 8, 20)
        ));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY_LIGHT);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY);
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY_DARK);
            }
        });
    }
    
    /**
     * Styles a secondary button
     */
    public static void styleSecondaryButton(JButton button) {
        button.setBackground(BG_PANEL);
        button.setForeground(PRIMARY);
        button.setFont(FONT_BUTTON);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
            new LineBorder(PRIMARY, 1),
            new EmptyBorder(8, 20, 8, 20)
        ));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(PRIMARY_VERY_LIGHT);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(BG_PANEL);
            }
        });
    }
    
    /**
     * Styles a sidebar menu button
     */
    public static void styleSidebarButton(JButton button) {
        button.setBackground(new Color(0, 0, 0, 0)); // Transparent
        button.setForeground(TEXT_WHITE);
        button.setFont(FONT_BODY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(255, 255, 255, 20)); // Semi-transparent white
                button.setForeground(Color.WHITE);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 0, 0, 0));
                button.setForeground(TEXT_WHITE);
            }
            
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(255, 255, 255, 30));
            }
        });
    }
    
    /**
     *   TEXT FIELD STYLING 
     */
    
    /**
     * Styles a text field with the theme
     */
    public static void styleTextField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBackground(BG_PANEL);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BORDER_FIELD);
        field.setPreferredSize(new Dimension(200, 36));
    }
    
    /**
     * Styles a password field
     */
    public static void stylePasswordField(JPasswordField field) {
        styleTextField(field);
    }
    
    /**
     * Styles a combo box
     */
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(FONT_BODY);
        combo.setBackground(BG_PANEL);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BORDER_FIELD);
        combo.setPreferredSize(new Dimension(200, 36));
    }
    
    /** 
     * LABEL STYLING 
     */
    
    /**
     * Styles a label with the theme
     */
    public static void styleLabel(JLabel label, boolean bold) {
        label.setFont(bold ? FONT_BODY_BOLD : FONT_BODY);
        label.setForeground(TEXT_PRIMARY);
    }
    
    /**
     * Styles a heading label
     */
    public static void styleHeadingLabel(JLabel label) {
        label.setFont(FONT_SUBHEADING);
        label.setForeground(PRIMARY_DARK);
    }
    
    /**
     * TABLE STYLING
     */ 
    
    /**
     * Styles a JTable with the institutional theme
     */
    public static void styleTable(JTable table) {
        // Table appearance
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setGridColor(BORDER_LIGHT);
        table.setSelectionBackground(PRIMARY_VERY_LIGHT);
        table.setSelectionForeground(PRIMARY_DARK);
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_PRIMARY);
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TABLE_HEADER_FG);
        header.setFont(FONT_BODY_BOLD);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(TABLE_ROW_EVEN);
                    } else {
                        c.setBackground(TABLE_ROW_ODD);
                    }
                }
                
                c.setFont(FONT_BODY);
                return c;
            }
        });
    }
    
    /**
     * Styles a scroll pane for tables
     */
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(new LineBorder(BORDER_LIGHT, 1));
        scrollPane.getViewport().setBackground(BG_PANEL);
    }
    
    /**
     *  PANEL STYLING
     */ 
    
    /**
     * Styles a content panel
     */
    public static void styleContentPanel(JPanel panel) {
        panel.setBackground(BG_MAIN);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
    }
    
    /**
     * Styles a white card panel
     */
    public static void styleCardPanel(JPanel panel) {
        panel.setBackground(BG_PANEL);
        panel.setBorder(BORDER_PANEL);
    }
    
    /**
     *  UTILITY METHOD
     */ 
    
    /**
     * Creates a styled separator
     */
    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(BORDER_LIGHT);
        sep.setBackground(BORDER_LIGHT);
        return sep;
    }
    
    /**
     * Creates a vertical spacer
     */
    public static Component createVerticalSpacer(int height) {
        return Box.createRigidArea(new Dimension(0, height));
    }
    
    /**
     * Creates a horizontal spacer
     */
    public static Component createHorizontalSpacer(int width) {
        return Box.createRigidArea(new Dimension(width, 0));
    }
}