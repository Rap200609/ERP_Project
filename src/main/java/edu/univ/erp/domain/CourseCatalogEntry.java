package edu.univ.erp.domain;

public class CourseCatalogEntry {
    private final String courseCode;
    private final String title;
    private final int credits;
    private final String instructorIdentifier;
    private final int capacity;

    public CourseCatalogEntry(String courseCode, String title, int credits, String instructorIdentifier, int capacity) {
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
        this.instructorIdentifier = instructorIdentifier;
        this.capacity = capacity;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public String getInstructorIdentifier() {
        return instructorIdentifier;
    }

    public int getCapacity() {
        return capacity;
    }
}

