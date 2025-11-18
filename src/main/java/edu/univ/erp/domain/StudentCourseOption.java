package edu.univ.erp.domain;

public class StudentCourseOption {
    private final int sectionId;
    private final String sectionCode;
    private final String courseTitle;

    public StudentCourseOption(int sectionId, String sectionCode, String courseTitle) {
        this.sectionId = sectionId;
        this.sectionCode = sectionCode;
        this.courseTitle = courseTitle;
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

    public String getDisplayName() {
        return courseTitle + " (Section " + sectionCode + ")";
    }
}

