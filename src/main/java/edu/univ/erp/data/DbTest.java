package edu.univ.erp.data;

import java.sql.Connection;

public class DbTest {
    public static void main(String[] args) {
        try (
            Connection connAuth = DatabaseConfig.getAuthDataSource().getConnection();
            Connection connMain = DatabaseConfig.getMainDataSource().getConnection()
        ) {
            System.out.println("Auth DB connection: SUCCESS!");
            System.out.println("ERP Main DB connection: SUCCESS!");
        } catch (Exception e) {
            System.out.println("Database connection error:");
            e.printStackTrace();
        }
    }
}
