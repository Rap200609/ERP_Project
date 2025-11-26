package edu.univ.erp.domain;

public class InstructorOption {
    private final int instructorId;
    private final String employeeId;
    private final String department;

    public InstructorOption(int instructorId, String employeeId, String department) {
        this.instructorId = instructorId;
        this.employeeId = employeeId;
        this.department = department;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public String getDisplayName() {
        return employeeId + " (" + department + ")";
    }
}