package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder; // Fixed Import

import edu.univ.erp.ui.theme.AppColors;
import edu.univ.erp.ui.theme.UIStyles;

public class StudentDashboard extends JFrame {
    private int studentId;
    private JPanel contentPanel;
    private MaintenanceBanner banner;

    public StudentDashboard(int studentId) {
        this.studentId = studentId;
        setTitle("Student Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Use the updated UIStyles
        UIStyles.initFrame(this);
        setLayout(new BorderLayout(0, 0));

        // Add maintenance banner
        banner = new MaintenanceBanner();
        // We wrap the banner to prevent it from breaking the layout
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(banner, BorderLayout.NORTH);
        add(topContainer, BorderLayout.NORTH);
        
        banner.checkAndDisplay();

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        UIStyles.styleSidebarPanel(sidebar);

        // Sidebar Header
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        logoPanel.setOpaque(false);
        JLabel heading = new JLabel("Student Portal");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(AppColors.PRIMARY);
        logoPanel.add(heading);
        sidebar.add(logoPanel);

        sidebar.add(Box.createVerticalStrut(10));

        String[] menuItems = {
            "Course Catalog",
            "Register Section",
            "Drop Section",
            "My Timetable",
            "View Grades",
            "Download Transcript",
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

        // Content Area
        contentPanel = new JPanel(new BorderLayout());
        UIStyles.applyContentBackground(contentPanel);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add content to center (underneath topContainer if using BorderLayout)
        // Since we added topContainer to NORTH, we add this to CENTER
        add(contentPanel, BorderLayout.CENTER);

        showPanel("Course Catalog");
        
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
            new ChangePasswordDialog(this, studentId).setVisible(true);
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
            case "Course Catalog":
                panel = new StudentCourseCatalogPanel();
                break;
            case "Register Section":
                panel = new StudentRegisterPanel(studentId);
                break;
            case "Drop Section":
                panel = new StudentDropPanel(studentId);
                break;
            case "My Timetable":
                panel = new StudentTimetablePanel(studentId);
                break;
            case "View Grades":
                panel = new StudentGradesPanel(studentId);
                break;
            case "Download Transcript":
                panel = new StudentTranscriptPanel(studentId);
                break;
        }

        if (panel != null) {
            // Create a Card Wrapper
            JPanel cardWrapper = new JPanel(new BorderLayout());
            cardWrapper.setBackground(Color.WHITE);
            cardWrapper.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(AppColors.BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
            ));

            JLabel title = new JLabel(panelName);
            title.setFont(new Font("Segoe UI", Font.BOLD, 24));
            title.setForeground(AppColors.TEXT_PRIMARY);
            title.setBorder(new EmptyBorder(0, 0, 20, 0));
            
            cardWrapper.add(title, BorderLayout.NORTH);
            
            // Apply styles to the loaded panel
            styleComponentsRecursively(panel);
            
            // Ensure the loaded panel is transparent so the white card shows
            panel.setOpaque(false);
            
            cardWrapper.add(panel, BorderLayout.CENTER);
            contentPanel.add(cardWrapper, BorderLayout.CENTER);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Recursive styling method (Fixed)
    private void styleComponentsRecursively(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JTable) {
                UIStyles.styleTable((JTable) c);
            } else if (c instanceof JButton) {
                // Only style if not already styled (simple check)
                JButton b = (JButton) c;
                if (b.getBackground().equals(new JButton().getBackground())) {
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