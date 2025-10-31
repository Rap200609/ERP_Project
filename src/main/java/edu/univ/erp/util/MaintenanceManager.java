package edu.univ.erp.util;

import java.sql.*;

public class MaintenanceManager {
    
    /**
     * Check if maintenance mode is currently ON
     */
    public static boolean isMaintenanceModeOn() {
        try (Connection conn = edu.univ.erp.data.DatabaseConfig.getMainDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT setting_value FROM settings WHERE setting_key = 'maintenance_mode'")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return "ON".equalsIgnoreCase(rs.getString("setting_value"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Default to OFF if error
    }
}
