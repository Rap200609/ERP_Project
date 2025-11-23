package edu.univ.erp.service.admin;

import edu.univ.erp.data.repository.SettingsRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DropDeadlineService {
    private static final String DROP_DEADLINE_KEY = "drop_deadline";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SettingsRepository settingsRepository;

    public DropDeadlineService() {
        this(new SettingsRepository());
    }

    public DropDeadlineService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public LocalDate getDropDeadline() throws Exception {
        return settingsRepository.getSettingValue(DROP_DEADLINE_KEY)
                .map(value -> LocalDate.parse(value, DATE_FORMAT))
                .orElse(LocalDate.now().plusDays(30));
    }

    public void setDropDeadline(LocalDate deadline) throws Exception {
        settingsRepository.updateSettingValue(DROP_DEADLINE_KEY, deadline.format(DATE_FORMAT));
    }

    public boolean isDropDeadlinePassed() throws Exception {
        LocalDate deadline = getDropDeadline();
        return LocalDate.now().isAfter(deadline);
    }
}
