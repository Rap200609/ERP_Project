package edu.univ.erp.domain;

public class CourseDetail {
    private final int courseId;
    private final String code;
    private final String title;
    private final int credits;
    private final String description;

    public CourseDetail(int courseId, String code, String title, int credits, String description) {
        this.courseId = courseId;
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.description = description;
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

    public int getCredits() {
        return credits;
    }

    public String getDescription() {
        return description;
    }

    public String getCourseDisplay() {
        return code + " - " + title;
    }
}

