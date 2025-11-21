// Done
package edu.univ.erp.api.common;

public class ApiResponse {
    private final boolean success;
    private final String message;

    private ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ApiResponse of(boolean success, String message) {
        return new ApiResponse(success, message);
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }

    public static ApiResponse failure(String message) {
        return new ApiResponse(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
