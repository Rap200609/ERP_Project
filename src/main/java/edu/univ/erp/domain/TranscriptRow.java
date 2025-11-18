package edu.univ.erp.domain;

public class TranscriptRow {
    private final String sectionCode;
    private final String courseTitle;
    private final double finalPercentage;
    private final String letterGrade;

    public TranscriptRow(String sectionCode, String courseTitle, double finalPercentage, String letterGrade) {
        this.sectionCode = sectionCode;
        this.courseTitle = courseTitle;
        this.finalPercentage = finalPercentage;
        this.letterGrade = letterGrade;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public double getFinalPercentage() {
        return finalPercentage;
    }

    public String getLetterGrade() {
        return letterGrade;
    }
}

