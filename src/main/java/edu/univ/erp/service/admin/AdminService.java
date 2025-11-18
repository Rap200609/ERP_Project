package edu.univ.erp.service.admin;

import edu.univ.erp.data.repository.InstructorRepository;
import edu.univ.erp.data.repository.StudentRepository;
import edu.univ.erp.data.repository.UserRepository;
import edu.univ.erp.domain.InstructorProfile;
import edu.univ.erp.domain.StudentProfile;
import edu.univ.erp.domain.UserAccount;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

public class AdminService {

    public enum Role {
        STUDENT, INSTRUCTOR, ADMIN;

        public static Role fromString(String value) {
            return Role.valueOf(value.toUpperCase());
        }
    }

    public static class AddUserRequest {
        public String username;
        public String password;
        public Role role;
        public String rollNo;
        public String program;
        public Integer year;
        public String studentEmail;
        public String employeeId;
        public String department;
        public String instructorEmail;
    }

    public static class UpdateUserRequest {
        public int userId;
        public String username;
        // Optional is a special Java class that can hold either a value of type T or be empty/null.
        public Optional<String> newPassword = Optional.empty();
        public Role role;
        public StudentProfile studentProfile;
        public InstructorProfile instructorProfile;
    }

    public static class UserDetails {
        public UserAccount account;
        public Optional<StudentProfile> studentProfile = Optional.empty();
        public Optional<InstructorProfile> instructorProfile = Optional.empty();
    }
    // Repositories are the class that provides methods to interact with the tables in the database. 
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;

    public AdminService() {
        this(new UserRepository(), new StudentRepository(), new InstructorRepository());
    }

    public AdminService(UserRepository userRepository,StudentRepository studentRepository, InstructorRepository instructorRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
    }

    public List<UserAccount> listUsers() throws Exception {
        return userRepository.findAll();
    }

    public int addUser(AddUserRequest request) throws Exception {
        String passwordHash = BCrypt.hashpw(request.password, BCrypt.gensalt(10));
        // Creates a new user in the database.
        int userId = userRepository.createUser(request.username, request.role.name(), passwordHash);

        switch (request.role) {       
            case STUDENT -> {
                validateStudentRequest(request);
                studentRepository.createStudentProfile(userId, request.rollNo, request.program, request.year, request.studentEmail);
            }
            case INSTRUCTOR -> {
                validateInstructorRequest(request);
                instructorRepository.createInstructorProfile(userId, request.employeeId, request.department, request.instructorEmail);
            }
            case ADMIN -> {
                // no-op
            }
        }
        return userId;
    }

    private void validateStudentRequest(AddUserRequest request) {
        if (request.rollNo == null || request.rollNo.isBlank() || request.program == null || request.program.isBlank() || request.year == null) {
            throw new IllegalArgumentException("All student fields are required.");
        }
    }

    private void validateInstructorRequest(AddUserRequest request) {
        if (request.employeeId == null || request.employeeId.isBlank() || request.department == null || request.department.isBlank()) {
            throw new IllegalArgumentException("All instructor fields are required.");
        }
    }

    public void deleteUser(int userId, Role role) throws Exception {
        userRepository.deleteUser(userId);
        switch (role) {
            case STUDENT -> studentRepository.deleteByUserId(userId);
            case INSTRUCTOR -> instructorRepository.deleteByUserId(userId);
            case ADMIN -> { /* nothing */ }
        }
    }

    public UserDetails loadUserDetails(int userId) throws Exception {
        UserDetails details = new UserDetails();
        UserAccount account = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        details.account = account;
        Role role = Role.fromString(account.getRole());
        if (role == Role.STUDENT) {
            details.studentProfile = studentRepository.findByUserId(userId);
        } else if (role == Role.INSTRUCTOR) {
            details.instructorProfile = instructorRepository.findByUserId(userId);
        }
        return details;
    }

    public void updateUser(UpdateUserRequest request) throws Exception {
        if (request.newPassword.isPresent() && !request.newPassword.get().isBlank()) {
            String hash = BCrypt.hashpw(request.newPassword.get(), BCrypt.gensalt(10));
            userRepository.updateUsernameAndPassword(request.userId, request.username, hash);
        } else {
            userRepository.updateUsername(request.userId, request.username);
        }

        if (request.role == Role.STUDENT && request.studentProfile != null) {
            studentRepository.updateStudentProfile(request.studentProfile);
        } else if (request.role == Role.INSTRUCTOR && request.instructorProfile != null) {
            instructorRepository.updateInstructorProfile(request.instructorProfile);
        }
    }
}

