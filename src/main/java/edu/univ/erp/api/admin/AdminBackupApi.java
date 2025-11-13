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

    public ApiResponse restore(DatabaseTarget target, File source) {
        try {
            BackupService.ProcessResult result = backupService.restoreDatabase(target, source);
            return result.isSuccess() ? ApiResponse.success(result.getMessage()) : ApiResponse.failure(result.getMessage());
        } catch (Exception ex) {
            return ApiResponse.failure("Restore failed: " + ex.getMessage());
        }
    }

    public ApiResponse backupBoth(File mainDestination, File authDestination) {
        ApiResponse main = backup(DatabaseTarget.MAIN, mainDestination);
        if (!main.isSuccess()) {
            return main;
        }
        return backup(DatabaseTarget.AUTH, authDestination);
    }

    public ApiResponse restoreBoth(File mainSource, File authSource) {
        ApiResponse main = restore(DatabaseTarget.MAIN, mainSource);
        if (!main.isSuccess()) {
            return main;
        }
        return restore(DatabaseTarget.AUTH, authSource);
    }
}
