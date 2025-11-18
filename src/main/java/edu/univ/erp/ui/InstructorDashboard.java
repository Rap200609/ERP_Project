package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.univ.erp.ui.theme.AppColors;
import edu.univ.erp.ui.theme.UIStyles;

public class InstructorDashboard extends JFrame {
    private int instructorId;
    private JPanel contentPanel;
    private MaintenanceBanner banner;

    public InstructorDashboard(int instructorId) {
        this.instructorId = instructorId;
        setTitle("Instructor Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 1. Apply Global Theme
        UIStyles.initFrame(this);
        setLayout(new BorderLayout(0, 0));
        
        // 2. Top Section
        JPanel topContainer = new JPanel(new BorderLayout());
        banner = new MaintenanceBanner();
        topContainer.add(banner, BorderLayout.NORTH);
        add(topContainer, BorderLayout.NORTH);
        
        banner.checkAndDisplay();

        // 3. Modern Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        UIStyles.styleSidebarPanel(sidebar);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        logoPanel.setOpaque(false);
        JLabel heading = new JLabel("Instructor Hub");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(AppColors.PRIMARY);
        logoPanel.add(heading);
        sidebar.add(logoPanel);

        sidebar.add(Box.createVerticalStrut(10));

        String[] menuItems = {
            "My Sections",
            "Grade Entry (Live)",
            "Class Stats",
            "Export CSV",
            "Change Password",
            "Logout"
        };

        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(220, 45));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            UIStyles.styleSidebarButton(btn);
            btn.addActionListener(e -> handleMenuClick(item));
            
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalGlue());
        add(sidebar, BorderLayout.WEST);

        // 4. Content Area
        contentPanel = new JPanel(new BorderLayout());
        UIStyles.applyContentBackground(contentPanel);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(contentPanel, BorderLayout.CENTER);

        showPanel("My Sections");
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                banner.checkAndDisplay();
            }
        });
    }

    private void handleMenuClick(String menuItem) {
        if ("Logout".equals(menuItem)) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new edu.univ.erp.ui.LoginFrame().setVisible(true);
            }
        } 
        else if ("Change Password".equals(menuItem)) { 
            new ChangePasswordDialog(this, instructorId).setVisible(true);
        }
        else {
            showPanel(menuItem);
        }
    }

    private void showPanel(String panelName) {
        contentPanel.removeAll();
        banner.checkAndDisplay();
        
        JPanel panel = null;
        switch (panelName) {
            case "My Sections":
                panel = new InstructorSectionsPanel(instructorId);
                break;
            case "Grade Entry (Live)":
                panel = new InstructorGradeEntryPanel(instructorId);
                break;
            case "Class Stats":
                panel = new InstructorStatsPanel(instructorId);
                break;
            case "Export CSV":
                panel = new GradesExportPanel(instructorId);
                break;
        }

        if (panel != null) {
            // Card Wrapper
            JPanel cardWrapper = new JPanel(new BorderLayout());
            cardWrapper.setBackground(AppColors.CARD_BG);
            cardWrapper.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
            ));

            JLabel title = new JLabel(panelName);
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setForeground(AppColors.TEXT_PRIMARY);
            title.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            cardWrapper.add(title, BorderLayout.NORTH);
            
            styleComponentsRecursively(panel);
            panel.setOpaque(false);
            
            cardWrapper.add(panel, BorderLayout.CENTER);
            contentPanel.add(cardWrapper, BorderLayout.CENTER);
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Styling Helper
    private void styleComponentsRecursively(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTable) {
                UIStyles.styleTable((JTable) c);
            } else if (c instanceof JButton) {
                JButton b = (JButton) c;
                if (b.getBackground().equals(new JButton().getBackground()) || b.getBackground().getAlpha() == 0) {
                   UIStyles.primaryButton(b);
                }
            } else if (c instanceof JTextField) {
                UIStyles.inputField((JTextField) c);
            } else if (c instanceof JScrollPane) {
                UIStyles.softenScrollPane((JScrollPane) c);
            } else if (c instanceof Container) {
                styleComponentsRecursively((Container) c);
            }
        }
    }
}