package edu.univ.erp.domain;

public class SectionSummary {
    private final int sectionId;
    private final String sectionCode;
    private final String courseTitle;
    private final int capacity;

    public SectionSummary(int sectionId, String sectionCode, String courseTitle, int capacity) {
        this.sectionId = sectionId;
        this.sectionCode = sectionCode;
        this.courseTitle = courseTitle;
        this.capacity = capacity;
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

    public int getCapacity() {
        return capacity;
    }
}

