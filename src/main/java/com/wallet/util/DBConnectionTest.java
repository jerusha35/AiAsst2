package com.wallet.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Standalone test program to verify Oracle Database connectivity and schema readiness.
 * Run this class directly or from command-line.
 */
public class DBConnectionTest {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" DIGITAL WALLET SIMULATOR - DATABASE HEALTH TEST");
        System.out.println("=================================================");
        System.out.println("Testing connection to Oracle database...");

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESS: Connected to Oracle Database successfully!");

                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Database Product Name   : " + meta.getDatabaseProductName());
                System.out.println("Database Product Version: " + meta.getDatabaseProductVersion());
                System.out.println("JDBC Driver Name        : " + meta.getDriverName());
                System.out.println("JDBC Driver Version     : " + meta.getDriverVersion());
                System.out.println("User Connected          : " + meta.getUserName());

                System.out.println("\nChecking for application tables...");
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(
                             "SELECT table_name FROM user_tables WHERE table_name IN ('USERS', 'WALLETS', 'TRANSACTIONS') ORDER BY table_name")) {
                    int count = 0;
                    while (rs.next()) {
                        System.out.println("  [FOUND] Table: " + rs.getString("table_name"));
                        count++;
                    }
                    if (count == 3) {
                        System.out.println("All 3 application tables are present and ready!");
                    } else if (count == 0) {
                        System.out.println("Warning: Tables not yet created. Please run database.sql in Oracle SQL Developer first.");
                    } else {
                        System.out.println("Warning: Only " + count + " of 3 tables found. Please run database.sql to complete setup.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("\nFAILURE: Could not connect to Oracle database.");
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("\nTroubleshooting tips:");
            System.err.println("1. Verify Oracle service is running (e.g., OracleServiceORCL and OracleOraDB...TNSListener in services.msc).");
            System.err.println("2. Verify credentials in src/main/resources/db.properties (system / livewire).");
            System.err.println("3. Ensure the port 1521 and SID/Service 'orcl' match your local Oracle instance.");
        }
        System.out.println("=================================================");
    }
}
