package edu.univ.erp.domain;

public class ComponentStats {
    private final String component;
    private final double average;
    private final double minimum;
    private final double maximum;

    public ComponentStats(String component, double average, double minimum, double maximum) {
        this.component = component;
        this.average = average;
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public String getComponent() {
        return component;
    }

    public double getAverage() {
        return average;
    }

    public double getMinimum() {
        return minimum;
    }

    public double getMaximum() {
        return maximum;
    }
}
