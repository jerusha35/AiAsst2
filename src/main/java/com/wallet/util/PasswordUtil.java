package com.wallet.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying passwords securely using BCrypt.
 */
public class PasswordUtil {

    private static final int WORKLOAD = 12;

    /**
     * Hash a raw password using BCrypt with a randomly generated salt.
     */
    public static String hashPassword(String passwordPlaintext) {
        if (passwordPlaintext == null || passwordPlaintext.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(passwordPlaintext, salt);
    }

    /**
     * Verify whether a raw password matches the stored BCrypt hash.
     */
    public static boolean checkPassword(String passwordPlaintext, String storedHash) {
        if (passwordPlaintext == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(passwordPlaintext, storedHash);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }
}
