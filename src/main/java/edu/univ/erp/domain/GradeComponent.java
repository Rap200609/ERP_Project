package edu.univ.erp.domain;

public class GradeComponent {
    private final String componentName;
    private final double score;
    private final double maxScore;
    private final double weight;

    public GradeComponent(String componentName, double score, double maxScore, double weight) {
        this.componentName = componentName;
        this.score = score;
        this.maxScore = maxScore;
        this.weight = weight;
    }

    public String getComponentName() {
        return componentName;
    }

    public double getScore() {
        return score;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public double getWeight() {
        return weight;
    }

    public double getPercentage() {
        return maxScore > 0 ? (score / maxScore) * 100.0 : 0.0;
    }

    public double getWeightedContribution() {
        return maxScore > 0 ? (score / maxScore) * weight : 0.0;
    }
}

