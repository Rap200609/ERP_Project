// Done
package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {
    // DataSource for erp_auth (user authentication)
    private static final HikariDataSource authDataSource;
    // DataSource for erp_main (all ERP data)
    private static final HikariDataSource mainDataSource;

    static {
        // Auth database DataSource (erp_auth)
        HikariConfig authConfig = new HikariConfig();
        authConfig.setJdbcUrl("jdbc:mysql://localhost:3306/erp_auth");
        authConfig.setUsername("root");
        authConfig.setPassword("Rap@2006");
        authConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        authDataSource = new HikariDataSource(authConfig);

        // Main ERP database DataSource (erp_main)
        HikariConfig mainConfig = new HikariConfig();
        mainConfig.setJdbcUrl("jdbc:mysql://localhost:3306/erp_main");
        mainConfig.setUsername("root");
        mainConfig.setPassword("Rap@2006");
        mainConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        mainDataSource = new HikariDataSource(mainConfig);
    }

    public static DataSource getAuthDataSource() {
        return authDataSource;
    }

    public static DataSource getMainDataSource() {
        return mainDataSource;
    }
}
