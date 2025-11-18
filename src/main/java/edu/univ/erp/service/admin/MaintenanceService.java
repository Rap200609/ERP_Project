package edu.univ.erp.service.admin;

import edu.univ.erp.data.repository.SettingsRepository;

public class MaintenanceService {
    private static final String MAINTENANCE_KEY = "maintenance_mode";

    private final SettingsRepository settingsRepository;

    public MaintenanceService() {
        this(new SettingsRepository());
    }

    public MaintenanceService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public boolean isMaintenanceModeOn() throws Exception {
        return settingsRepository.getSettingValue(MAINTENANCE_KEY)
                .map(value -> "ON".equalsIgnoreCase(value))
                .orElse(false);
    }

    public void setMaintenanceMode(boolean on) throws Exception {
        settingsRepository.updateSettingValue(MAINTENANCE_KEY, on ? "ON" : "OFF");
    }

    public boolean toggleMaintenanceMode() throws Exception {
        boolean current = isMaintenanceModeOn();
        setMaintenanceMode(!current);
        return !current;
    }
}
