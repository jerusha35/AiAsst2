package com.wallet.dao;

import com.wallet.model.User;
import com.wallet.model.Wallet;
import com.wallet.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Registers a new user and creates their default wallet (balance = 0) atomically.
     * Uses JDBC transaction commit/rollback.
     */
    public boolean registerUserWithWallet(User user) throws SQLException {
        String insertUserSql = "INSERT INTO USERS (USER_ID, FULL_NAME, EMAIL, PHONE, PASSWORD_HASH, STATUS) "
                + "VALUES (SEQ_USERS.NEXTVAL, ?, ?, ?, ?, 'ACTIVE')";
        String getUserIdSql = "SELECT SEQ_USERS.CURRVAL FROM DUAL";
        String insertWalletSql = "INSERT INTO WALLETS (WALLET_ID, USER_ID, BALANCE, STATUS) "
                + "VALUES (SEQ_WALLETS.NEXTVAL, ?, 0.00, 'ACTIVE')";

        Connection conn = null;
        PreparedStatement psUser = null;
        PreparedStatement psGetId = null;
        PreparedStatement psWallet = null;
        ResultSet rsId = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Insert User
            psUser = conn.prepareStatement(insertUserSql);
            psUser.setString(1, user.getFullName());
            psUser.setString(2, user.getEmail().toLowerCase().trim());
            psUser.setString(3, user.getPhone());
            psUser.setString(4, user.getPasswordHash());

            int rowsUser = psUser.executeUpdate();
            if (rowsUser == 0) {
                conn.rollback();
                return false;
            }

            // 2. Retrieve generated User ID
            psGetId = conn.prepareStatement(getUserIdSql);
            rsId = psGetId.executeQuery();
            long newUserId = 0;
            if (rsId.next()) {
                newUserId = rsId.getLong(1);
            } else {
                conn.rollback();
                return false;
            }

            // 3. Insert Wallet for this user
            psWallet = conn.prepareStatement(insertWalletSql);
            psWallet.setLong(1, newUserId);
            int rowsWallet = psWallet.executeUpdate();
            if (rowsWallet == 0) {
                conn.rollback();
                return false;
            }

            conn.commit(); // Atomically committed
            user.setUserId(newUserId);
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback error during registration: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (rsId != null) try { rsId.close(); } catch (SQLException ignored) {}
            if (psWallet != null) try { psWallet.close(); } catch (SQLException ignored) {}
            if (psGetId != null) try { psGetId.close(); } catch (SQLException ignored) {}
            if (psUser != null) try { psUser.close(); } catch (SQLException ignored) {}
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Checks if an email is already registered in the USERS table.
     */
    public boolean isEmailRegistered(String email) throws SQLException {
        String sql = "SELECT COUNT(1) FROM USERS WHERE LOWER(EMAIL) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Finds a user by email.
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT USER_ID, FULL_NAME, EMAIL, PHONE, PASSWORD_HASH, CREATED_AT, STATUS FROM USERS WHERE LOWER(EMAIL) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds a user by primary key ID.
     */
    public User findById(Long userId) throws SQLException {
        String sql = "SELECT USER_ID, FULL_NAME, EMAIL, PHONE, PASSWORD_HASH, CREATED_AT, STATUS FROM USERS WHERE USER_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates profile details (Full name and phone).
     */
    public boolean updateProfile(Long userId, String fullName, String phone) throws SQLException {
        String sql = "UPDATE USERS SET FULL_NAME = ?, PHONE = ? WHERE USER_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName.trim());
            ps.setString(2, phone != null ? phone.trim() : null);
            ps.setLong(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates password hash.
     */
    public boolean updatePassword(Long userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE USERS SET PASSWORD_HASH = ? WHERE USER_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getLong("USER_ID"));
        u.setFullName(rs.getString("FULL_NAME"));
        u.setEmail(rs.getString("EMAIL"));
        u.setPhone(rs.getString("PHONE"));
        u.setPasswordHash(rs.getString("PASSWORD_HASH"));
        u.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        u.setStatus(rs.getString("STATUS"));
        return u;
    }
}
