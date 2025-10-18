package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariDataSource authDataSource;
    private static HikariDataSource erpDataSource;

    static {
        HikariConfig authConfig = new HikariConfig();
        authConfig.setJdbcUrl("jdbc:mysql://localhost:3306/erp_auth");
        authConfig.setUsername("root");
        authConfig.setPassword("Rap@2006"); // Use your updated password!
        authConfig.setMaximumPoolSize(10);
        authDataSource = new HikariDataSource(authConfig);

        HikariConfig erpConfig = new HikariConfig();
        erpConfig.setJdbcUrl("jdbc:mysql://localhost:3306/erp_main");
        erpConfig.setUsername("root");
        erpConfig.setPassword("Rap@2006"); // Use your updated password!
        erpConfig.setMaximumPoolSize(10);
        erpDataSource = new HikariDataSource(erpConfig);
    }

    public static DataSource getAuthDataSource() {
        return authDataSource;
    }
    public static DataSource getErpDataSource() {
        return erpDataSource;
    }
}
