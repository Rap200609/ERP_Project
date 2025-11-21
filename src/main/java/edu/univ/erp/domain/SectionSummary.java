// Done
package edu.univ.erp.domain;

public class SectionSummary {
    private final int sectionId;
    private final int courseId;
    private final String sectionCode;
    private final String courseTitle;
    private final int capacity;

    public SectionSummary(int sectionId, int courseId, String sectionCode, String courseTitle, int capacity) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.sectionCode = sectionCode;
        this.courseTitle = courseTitle;
        this.capacity = capacity;
    }

    public int getSectionId() {
        return sectionId;
    }

    public int getCourseId() {
        return courseId;
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

