package edu.univ.erp.domain;

public class GradeExportRow {
    private final String sectionCode;
    private final String rollNo;
    private final String component;
    private final double score;
    private final Double finalGrade;

    public GradeExportRow(String sectionCode, String rollNo, String component, double score, Double finalGrade) {
        this.sectionCode = sectionCode;
        this.rollNo = rollNo;
        this.component = component;
        this.score = score;
        this.finalGrade = finalGrade;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getComponent() {
        return component;
    }

    public double getScore() {
        return score;
    }

    public Double getFinalGrade() {
        return finalGrade;
    }
}

