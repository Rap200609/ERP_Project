package edu.univ.erp.domain;

public class CourseOption {
    private final int courseId;
    private final String code;
    private final String title;

    public CourseOption(int courseId, String code, String title) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplayName() {
        return code + " - " + title;
    }
}