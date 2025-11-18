package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

public class DatabaseConfig {
    // DataSource for erp_auth (user authentication)
    private static final HikariDataSource authDataSource;
    // DataSource for erp_main (all ERP data)
    private static final HikariDataSource mainDataSource;

    static {
        try {
            // Auth database DataSource (erp_auth)
            HikariConfig authConfig = new HikariConfig();
            authConfig.setJdbcUrl("jdbc:mysql://localhost:3306/erp_auth");
            authConfig.setUsername("root");
            authConfig.setPassword("Rap@2006"); // TODO: Update this with your MySQL root password
            authConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            authConfig.setConnectionTimeout(5000); // 5 second timeout
            authConfig.setMaximumPoolSize(10);
            authDataSource = new HikariDataSource(authConfig);

            // Main ERP database DataSource (erp_main)
            HikariConfig mainConfig = new HikariConfig();
            mainConfig.setJdbcUrl("jdbc:mysql://localhost:3306/erp_main");
            mainConfig.setUsername("root");
            mainConfig.setPassword("Rap@2006"); // TODO: Update this with your MySQL root password
            mainConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
            mainConfig.setConnectionTimeout(5000); // 5 second timeout
            mainConfig.setMaximumPoolSize(10);
            mainDataSource = new HikariDataSource(mainConfig);
        } catch (Exception e) {
            String errorMsg = "Failed to connect to MySQL database!\n\n" +
                    "Please check:\n" +
                    "1. MySQL server is running\n" +
                    "2. Username and password in DatabaseConfig.java are correct\n" +
                    "3. Databases 'erp_auth' and 'erp_main' exist\n" +
                    "4. User has proper permissions\n\n" +
                    "Error: " + e.getMessage() + "\n\n" +
                    "Edit: src/main/java/edu/univ/erp/data/DatabaseConfig.java";
            
            System.err.println(errorMsg);
            e.printStackTrace();
            
            // Show error dialog if possible (may not work if Swing not initialized)
            try {
                JOptionPane.showMessageDialog(null, errorMsg, 
                    "Database Connection Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ignored) {
                // Swing not initialized yet, just print to console
            }
            
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static DataSource getAuthDataSource() {
        return authDataSource;
    }

    public static DataSource getMainDataSource() {
        return mainDataSource;
    }
}
