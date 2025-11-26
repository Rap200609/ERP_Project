package edu.univ.erp.util;

import edu.univ.erp.service.admin.MaintenanceService;

public final class MaintenanceManager {

    private static final MaintenanceService maintenanceService = new MaintenanceService();

    private MaintenanceManager() {
    }

    public static boolean isMaintenanceModeOn() {
        try {
            return maintenanceService.isMaintenanceModeOn();
        } catch (Exception e) {
            System.err.println("Failed to check maintenance mode: " + e.getMessage());
            return false;
        }
    }
}
