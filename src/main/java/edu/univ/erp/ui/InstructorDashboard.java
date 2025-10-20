package edu.univ.erp.ui;

import javax.swing.*;

public class InstructorDashboard extends JFrame {
    public InstructorDashboard() {
        setTitle("Instructor Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel("Welcome, Instructor!");
        add(label);
    }
}
