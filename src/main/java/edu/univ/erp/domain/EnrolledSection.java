package edu.univ.erp.domain;

public class EnrolledSection {
    private final String sectionCode;
    private final String courseTitle;
    private final String instructorName;

    public EnrolledSection(String sectionCode, String courseTitle, String instructorName) {
        this.sectionCode = sectionCode;
        this.courseTitle = courseTitle;
        this.instructorName = instructorName;
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
}

