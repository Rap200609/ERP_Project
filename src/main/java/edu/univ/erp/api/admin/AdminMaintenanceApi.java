package edu.univ.erp.api.admin;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.service.admin.MaintenanceService;

public class AdminMaintenanceApi {
    private final MaintenanceService maintenanceService;

    public AdminMaintenanceApi() {
        this(new MaintenanceService());
    }

    public AdminMaintenanceApi(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    public boolean isMaintenanceModeOn() {
        try {
            return maintenanceService.isMaintenanceModeOn();
        } catch (Exception ex) {
            return false;
        }
    }

    public ApiResponse setMaintenanceMode(boolean modeOn) {
        try {
            maintenanceService.setMaintenanceMode(modeOn);
            return ApiResponse.success(modeOn ? "Maintenance mode enabled." : "Maintenance mode disabled.");
        } catch (Exception ex) {
            return ApiResponse.failure("Failed to update maintenance mode: " + ex.getMessage());
        }
    }

    public ApiResponse toggleMaintenanceMode() {
        try {
            boolean newState = maintenanceService.toggleMaintenanceMode();
            return ApiResponse.success(newState ? "Maintenance mode enabled." : "Maintenance mode disabled.");
        } catch (Exception ex) {
            return ApiResponse.failure("Failed to toggle maintenance mode: " + ex.getMessage());
        }
    }
}
