package edu.univ.erp.api.admin;

import edu.univ.erp.api.common.ApiResponse;
import edu.univ.erp.domain.InstructorProfile;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.domain.UserAccount;
import edu.univ.erp.service.admin.AdminService;

import java.util.List;
import java.util.Optional;

public class AdminApi {

    public static class AddUserCommand {
        public String username;
        public String password;
        public String role;
        public String rollNo;
        public String program;
        public Integer year;
        public String studentEmail;
        public String employeeId;
        public String department;
        public String instructorEmail;
    }

    public static class UpdateUserCommand {
        public int userId;
        public String username;
        public String role;
        public String newPassword;
        public StudentProfile studentProfile;
        public InstructorProfile instructorProfile;
    }

    public static class UserDetailsView {
        public UserAccount account;
        public StudentProfile studentProfile;
        public InstructorProfile instructorProfile;
    }

    private final AdminService adminService;

    public AdminApi() {
        this(new AdminService());
    }

    public AdminApi(AdminService adminService) {
        this.adminService = adminService;
    }

    public List<UserAccount> listUsers() throws Exception {
        return adminService.listUsers();
    }

    public ApiResponse addUser(AddUserCommand command) {
        try {
            if (command.username == null || command.username.isBlank()
                    || command.password == null || command.password.isBlank()) {
                return ApiResponse.failure("Username and password required.");
            }
            AdminService.AddUserRequest request = new AdminService.AddUserRequest();
            request.username = command.username;
            request.password = command.password;
            request.role = AdminService.Role.fromString(command.role);
            request.rollNo = command.rollNo;
            request.program = command.program;
            request.year = command.year;
            request.studentEmail = command.studentEmail;
            request.employeeId = command.employeeId;
            request.department = command.department;
            request.instructorEmail = command.instructorEmail;
            adminService.addUser(request);
            return ApiResponse.success("User added successfully!");
        } catch (IllegalArgumentException ex) {
            return ApiResponse.failure(ex.getMessage());
        } catch (Exception ex) {
            return ApiResponse.failure("Error adding user: " + ex.getMessage());
        }
    }

    public ApiResponse deleteUser(int userId, String role) {
        try {
            adminService.deleteUser(userId, AdminService.Role.fromString(role));
            return ApiResponse.success("User deleted");
        } catch (Exception ex) {
            return ApiResponse.failure("Error deleting user: " + ex.getMessage());
        }
    }

    public Optional<UserDetailsView> loadUserDetails(int userId) {
        try {
            AdminService.UserDetails details = adminService.loadUserDetails(userId);
            UserDetailsView view = new UserDetailsView();
            view.account = details.account;
            view.studentProfile = details.studentProfile.orElse(null);
            view.instructorProfile = details.instructorProfile.orElse(null);
            return Optional.of(view);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public ApiResponse updateUser(UpdateUserCommand command) {
        try {
            AdminService.UpdateUserRequest request = new AdminService.UpdateUserRequest();
            request.userId = command.userId;
            request.username = command.username;
            request.role = AdminService.Role.fromString(command.role);
            request.newPassword = Optional.ofNullable(command.newPassword).filter(p -> !p.isBlank());
            request.studentProfile = command.studentProfile;
            request.instructorProfile = command.instructorProfile;
            adminService.updateUser(request);
            return ApiResponse.success("Updated successfully!");
        } catch (Exception ex) {
            return ApiResponse.failure("Error updating user: " + ex.getMessage());
        }
    }
}

