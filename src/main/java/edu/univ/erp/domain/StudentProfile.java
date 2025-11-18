package edu.univ.erp.domain;

public class StudentProfile {
    private final int userId;
    private final String rollNo;
    private final String program;
    private final int year;
    private final String email;

    public StudentProfile(int userId, String rollNo, String program, int year, String email) {
        this.userId = userId;
        this.rollNo = rollNo;
        this.program = program;
        this.year = year;
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getProgram() {
        return program;
    }

    public int getYear() {
        return year;
    }

    public String getEmail() {
        return email;
    }
}

