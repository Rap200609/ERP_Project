package edu.univ.erp.api.admin;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.service.admin.DropDeadlineService;

import java.time.LocalDate;

public class AdminDropDeadlineApi {
    private final DropDeadlineService deadlineService;

    public AdminDropDeadlineApi() {
        this(new DropDeadlineService());
    }

    public AdminDropDeadlineApi(DropDeadlineService deadlineService) {
        this.deadlineService = deadlineService;
    }

    public LocalDate getDropDeadline() throws Exception {
        return deadlineService.getDropDeadline();
    }

    public ApiResponse setDropDeadline(LocalDate deadline) {
        try {
            deadlineService.setDropDeadline(deadline);
            return ApiResponse.success("Drop deadline updated successfully.");
        } catch (Exception ex) {
            return ApiResponse.failure("Failed to update drop deadline: " + ex.getMessage());
        }
    }
}
