package edu.univ.erp.domain;

public class SectionAssignment {
    private final int sectionId;
    private final String sectionCode;
    private final String courseCode;
    private final String courseTitle;
    private final String semester;
    private final int year;
    private final Integer instructorId;

    public SectionAssignment(int sectionId,
                             String sectionCode,
                             String courseCode,
                             String courseTitle,
                             String semester,
                             int year,
                             Integer instructorId) {
        this.sectionId = sectionId;
        this.sectionCode = sectionCode;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.semester = semester;
        this.year = year;
        this.instructorId = instructorId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getSemester() {
        return semester;
    }

    public int getYear() {
        return year;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public String getCourseDisplay() {
        return courseCode + " - " + courseTitle;
    }
}