package edu.univ.erp.ui.theme;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;

/**
 * Helper methods for applying a consistent visual style across the Swing UI.
 */
public final class UIStyles {

    private UIStyles() {
        // utility
    }

    public static void applyFrameBackground(JFrame frame) {
        frame.getContentPane().setBackground(AppColors.BACKGROUND);
    }

    public static void stylePrimaryButton(AbstractButton button) {
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.putClientProperty("JComponent.minimumWidth", 140);
        button.setBackground(AppColors.PRIMARY);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(10, 18, 10, 18));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void styleSidebarButton(AbstractButton button) {
        stylePrimaryButton(button);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
    }

    public static void styleSecondaryButton(AbstractButton button) {
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBackground(AppColors.SIDEBAR);
        button.setForeground(AppColors.TEXT_PRIMARY);
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(224, 230, 241), 1, true),
                new EmptyBorder(20, 24, 20, 24)));
        panel.putClientProperty("JComponent.roundRect", true);
        panel.putClientProperty("JComponent.arc", 18);
        return panel;
    }

    public static void styleSidebarPanel(JPanel panel) {
        panel.setBackground(AppColors.SIDEBAR);
        panel.setBorder(new EmptyBorder(24, 20, 24, 20));
        panel.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.putClientProperty("JComponent.roundRect", true);
        panel.putClientProperty("JComponent.arc", 18);
        panel.setMinimumSize(new Dimension(220, 0));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setMaximumSize(new Dimension(220, Integer.MAX_VALUE));
        panel.setOpaque(true);
    }

    public static void applyContentBackground(JComponent component) {
        component.setBackground(AppColors.BACKGROUND);
        component.setOpaque(true);
    }

    public static void styleTextField(JTextComponent field) {
        field.putClientProperty("JComponent.roundRect", true);
        field.putClientProperty("JComponent.arc", 14);
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 218, 232), 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        field.setOpaque(true);
        field.setEnabled(true);
        field.setEditable(true);
        field.setFocusable(true);
        field.setPreferredSize(new Dimension(220, 36));
        field.setBackground(Color.WHITE);
        field.setForeground(AppColors.TEXT_PRIMARY);
        field.setCaretColor(AppColors.PRIMARY_DARK);
    }

    public static void softenScrollPane(JComponent scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
    }
}


