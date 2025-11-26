package edu.univ.erp.domain;

public class TimetableEntry {
    private final String sectionCode;
    private final String courseTitle;
    private final String day;
    private final String time;
    private final String room;

    public TimetableEntry(String sectionCode, String courseTitle, String day, String time, String room) {
        this.sectionCode = sectionCode;
        this.courseTitle = courseTitle;
        this.day = day;
        this.time = time;
        this.room = room;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getCourseTitle() {
        return courseTitle;
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
}