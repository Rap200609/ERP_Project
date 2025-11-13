package edu.univ.erp.domain;

public class SectionDetail {
    private final int sectionId;
    private final int courseId;
    private final String courseCode;
    private final String courseTitle;
    private final String sectionCode;
    private final String day;
    private final String time;
    private final String room;
    private final String semester;
    private final int year;
    private final int capacity;

    public SectionDetail(int sectionId,
                         int courseId,
                         String courseCode,
                         String courseTitle,
                         String sectionCode,
                         String day,
                         String time,
                         String room,
                         String semester,
                         int year,
                         int capacity) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.sectionCode = sectionCode;
        this.day = day;
        this.time = time;
        this.room = room;
        this.semester = semester;
        this.year = year;
        this.capacity = capacity;
    }

    public int getSectionId() {
        return sectionId;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getRoom() {
        return room;
    }

    public String getSemester() {
        return semester;
    }

    public int getYear() {
        return year;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getCourseDisplay() {
        return courseCode + " - " + courseTitle;
    }
}