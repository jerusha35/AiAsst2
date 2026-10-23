package com.wallet.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centralized JDBC Connection Utility for Oracle Database.
 * Loads configuration from db.properties if present, with safe fallbacks.
 *
 * NOTE FOR LEARNING & SECURITY:
 * Connecting as SYSTEM is used here for local learning convenience.
 * In a real production deployment, create a dedicated application user
 * (e.g., WALLET_APP) with least privileges.
 */
public class DBConnection {

    private static String driver = "oracle.jdbc.OracleDriver";
    private static String url = "jdbc:oracle:thin:@localhost:1521/orcl";
    private static String username = "system";
    private static String password = "livewire";

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                if (props.getProperty("db.driver") != null) {
                    driver = props.getProperty("db.driver").trim();
                }
                if (props.getProperty("db.url") != null) {
                    url = props.getProperty("db.url").trim();
                }
                if (props.getProperty("db.username") != null) {
                    username = props.getProperty("db.username").trim();
                }
                if (props.getProperty("db.password") != null) {
                    password = props.getProperty("db.password").trim();
                }
            }
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] Oracle JDBC Driver not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[DBConnection] Warning loading db.properties: " + e.getMessage());
        }
    }

    /**
     * Gets a new database connection.
     * Callers must close connections or use try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Helper to close a connection safely without throwing exceptions.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
