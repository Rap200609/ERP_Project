package edu.univ.erp.domain;

public class EnrolledSection {
    private final int sectionId;
    private final String sectionCode;
    private final String courseTitle;
    private final String instructorName;

    public EnrolledSection(int sectionId, String sectionCode, String courseTitle, String instructorName) {
        this.sectionId = sectionId;
        this.sectionCode = sectionCode;
        this.courseTitle = courseTitle;
        this.instructorName = instructorName;
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
}

