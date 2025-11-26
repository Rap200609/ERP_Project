package edu.univ.erp.api.admin;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.DatabaseTarget;
import edu.univ.erp.service.admin.BackupService;

import java.io.File;

public class AdminBackupApi {
    private final BackupService backupService;

    public AdminBackupApi() {
        this(new BackupService());
    }

    public AdminBackupApi(BackupService backupService) {
        this.backupService = backupService;
    }

    public ApiResponse backup(DatabaseTarget target, File destination) {
        try {
            BackupService.ProcessResult result = backupService.backupDatabase(target, destination);
            return result.isSuccess() ? ApiResponse.success(result.getMessage()) : ApiResponse.failure(result.getMessage());
        } catch (Exception ex) {
            return ApiResponse.failure("Backup failed: " + ex.getMessage());
        }
    }


}
