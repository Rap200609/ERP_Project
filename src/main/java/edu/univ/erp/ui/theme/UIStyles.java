package edu.univ.erp.ui.theme;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class UIStyles {

    // --- Frames & Panels ---

    public static void initFrame(JFrame frame) {
        frame.getContentPane().setBackground(AppColors.BACKGROUND);
        frame.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }
    
    // Alias for compatibility
    public static void applyFrameBackground(JFrame frame) {
        initFrame(frame);
    }

    public static void applyContentBackground(JComponent component) {
        component.setBackground(AppColors.BACKGROUND);
        component.setOpaque(true);
    }

    public static void styleSidebarPanel(JPanel panel) {
        panel.setBackground(AppColors.SIDEBAR);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppColors.BORDER));
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(AppColors.CARD_BG);
        panel.setBorder(new CompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(20, 24, 20, 24)));
        panel.putClientProperty("JComponent.roundRect", true);
        panel.putClientProperty("JComponent.arc", 12);
        return panel;
    }

    // --- Buttons ---

    public static void primaryButton(AbstractButton btn) {
        btn.setBackground(AppColors.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect"); 
    }
    
    // Used by LoginFrame/Theme
    public static void stylePrimaryButton(AbstractButton btn) {
        primaryButton(btn);
    }

    public static void styleSidebarButton(AbstractButton btn) {
        btn.setBackground(AppColors.SIDEBAR);
        btn.setForeground(AppColors.TEXT_DARK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(12, 15, 12, 15));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("JButton.buttonType", "roundRect");
    }

    // --- Inputs ---

    public static void inputField(JTextComponent field) {
        field.putClientProperty("JComponent.roundRect", true);
        field.putClientProperty("JComponent.outline", AppColors.PRIMARY);
        field.setBorder(new CompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
    }
    
    // Alias for compatibility
    public static void styleTextField(JTextComponent field) {
        inputField(field);
    }

    // --- Tables & Misc ---

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

    public static void softenScrollPane(JComponent scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(AppColors.BACKGROUND);
    }

    public static JLabel headerLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl.setForeground(AppColors.PRIMARY);
        return lbl;
    }
}