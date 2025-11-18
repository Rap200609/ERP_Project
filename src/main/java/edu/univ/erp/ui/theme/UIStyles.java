package edu.univ.erp.ui.theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UIStyles {

    // Apply to the main JFrame
    public static void initFrame(JFrame frame) {
        frame.getContentPane().setBackground(AppColors.BACKGROUND);
        frame.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    // Style a primary action button (Blue, Rounded)
    public static void primaryButton(JButton btn) {
        btn.setBackground(AppColors.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // FlatLaf specific: Rounded corners
        btn.putClientProperty("JButton.buttonType", "roundRect"); 
    }

    // Style a sidebar/secondary button
    public static void sidebarButton(JButton btn) {
        btn.setBackground(AppColors.CARD_BG);
        btn.setForeground(AppColors.TEXT_DARK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 15, 12, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
    }

    // Style a text field
    public static void inputField(JTextField field) {
        field.putClientProperty("JComponent.roundRect", true);
        field.putClientProperty("JComponent.outline", AppColors.PRIMARY);
        field.setBorder(new CompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    // Create a Title Label
    public static JLabel headerLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(AppColors.PRIMARY);
        return lbl;
    }

    // Style a Table
    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(AppColors.BACKGROUND);
        table.getTableHeader().setForeground(AppColors.TEXT_DARK);
        table.setSelectionBackground(AppColors.PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
    }
}