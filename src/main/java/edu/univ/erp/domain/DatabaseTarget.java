package edu.univ.erp.domain;

public enum DatabaseTarget {
    MAIN("erp_main"),
    AUTH("erp_auth");

    private final String databaseName;

    DatabaseTarget(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
