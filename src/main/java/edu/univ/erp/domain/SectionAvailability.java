package edu.univ.erp.domain;

public class SectionAvailability {
    private final int sectionId;
    private final String sectionCode;
    private final String courseTitle;
    private final String instructorName;
    private final int capacity;
    private final int enrolled;

    public SectionAvailability(int sectionId, String sectionCode, String courseTitle,
                               String instructorName, int capacity, int enrolled) {
        this.sectionId = sectionId;
        this.sectionCode = sectionCode;
        this.courseTitle = courseTitle;
        this.instructorName = instructorName;
        this.capacity = capacity;
        this.enrolled = enrolled;
    }

    public int getSectionId() {
        return sectionId;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public int getAvailableSeats() {
        return capacity - enrolled;
    }
}

