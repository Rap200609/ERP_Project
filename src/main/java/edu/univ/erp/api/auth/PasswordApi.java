package edu.univ.erp.api.auth;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.service.auth.PasswordService;

public class PasswordApi {
    private final PasswordService passwordService;

    public PasswordApi() {
        this(new PasswordService());
    }

    public PasswordApi(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    public ApiResponse changePassword(int userId, String currentPassword, String newPassword) {
        try {
            if (!passwordService.verifyCurrentPassword(userId, currentPassword)) {
                return ApiResponse.failure("Current password is incorrect!");
            }
            passwordService.changePassword(userId, newPassword);
            return ApiResponse.success("Password changed successfully!");
        } catch (Exception ex) {
            return ApiResponse.failure("Error changing password: " + ex.getMessage());
        }
    }
}

