package edu.univ.erp.domain;

public class InstructorProfile {
    private final int userId;
    private final String employeeId;
    private final String department;
    private final String email;

    public InstructorProfile(int userId, String employeeId, String department, String email) {
        this.userId = userId;
        this.employeeId = employeeId;
        this.department = department;
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }
}

