package com.wallet.dao;

import com.wallet.model.Transaction;
import com.wallet.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    /**
     * Retrieves the top 5 most recent transactions for a wallet.
     */
    public List<Transaction> getRecentTransactions(Long walletId) throws SQLException {
        String sql = "SELECT * FROM ("
                + "  SELECT TRANSACTION_ID, TRANSACTION_REF, SENDER_WALLET_ID, RECEIVER_WALLET_ID, "
                + "         TRANSACTION_TYPE, AMOUNT, DESCRIPTION, TRANSACTION_STATUS, CREATED_AT "
                + "  FROM TRANSACTIONS "
                + "  WHERE (SENDER_WALLET_ID = ? AND TRANSACTION_TYPE = 'TRANSFER_SENT') "
                + "     OR (RECEIVER_WALLET_ID = ? AND TRANSACTION_TYPE IN ('TOPUP', 'TRANSFER_RECEIVED')) "
                + "  ORDER BY CREATED_AT DESC"
                + ") WHERE ROWNUM <= 5";

        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, walletId);
            ps.setLong(2, walletId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs, walletId));
                }
            }
        }
        return list;
    }

    /**
     * Calculates total money added (TOPUP) for a wallet.
     */
    public BigDecimal getTotalAdded(Long walletId) throws SQLException {
        String sql = "SELECT NVL(SUM(AMOUNT), 0) FROM TRANSACTIONS WHERE RECEIVER_WALLET_ID = ? AND TRANSACTION_TYPE = 'TOPUP' AND TRANSACTION_STATUS = 'SUCCESS'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, walletId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calculates total money transferred out (TRANSFER_SENT) for a wallet.
     */
    public BigDecimal getTotalTransferred(Long walletId) throws SQLException {
        String sql = "SELECT NVL(SUM(AMOUNT), 0) FROM TRANSACTIONS WHERE SENDER_WALLET_ID = ? AND TRANSACTION_TYPE = 'TRANSFER_SENT' AND TRANSACTION_STATUS = 'SUCCESS'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, walletId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Retrieves filtered and paginated transactions for a wallet.
     */
    public List<Transaction> getFilteredTransactions(Long walletId, String typeFilter, int offset, int limit) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( ");
        sql.append("  SELECT a.*, ROWNUM rnum FROM ( ");
        sql.append("    SELECT TRANSACTION_ID, TRANSACTION_REF, SENDER_WALLET_ID, RECEIVER_WALLET_ID, ");
        sql.append("           TRANSACTION_TYPE, AMOUNT, DESCRIPTION, TRANSACTION_STATUS, CREATED_AT ");
        sql.append("    FROM TRANSACTIONS ");
        sql.append("    WHERE ((SENDER_WALLET_ID = ? AND TRANSACTION_TYPE = 'TRANSFER_SENT') ");
        sql.append("       OR (RECEIVER_WALLET_ID = ? AND TRANSACTION_TYPE IN ('TOPUP', 'TRANSFER_RECEIVED'))) ");

        if (typeFilter != null && !typeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(typeFilter)) {
            sql.append("      AND TRANSACTION_TYPE = ? ");
        }

        sql.append("    ORDER BY CREATED_AT DESC ");
        sql.append("  ) a WHERE ROWNUM <= ? ");
        sql.append(") WHERE rnum > ?");

        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            ps.setLong(p++, walletId);
            ps.setLong(p++, walletId);

            if (typeFilter != null && !typeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(typeFilter)) {
                ps.setString(p++, typeFilter.trim());
            }

            ps.setInt(p++, offset + limit);
            ps.setInt(p++, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs, walletId));
                }
            }
        }
        return list;
    }

    /**
     * Counts filtered transactions for pagination.
     */
    public int countFilteredTransactions(Long walletId, String typeFilter) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(1) FROM TRANSACTIONS ");
        sql.append("WHERE ((SENDER_WALLET_ID = ? AND TRANSACTION_TYPE = 'TRANSFER_SENT') ");
        sql.append("   OR (RECEIVER_WALLET_ID = ? AND TRANSACTION_TYPE IN ('TOPUP', 'TRANSFER_RECEIVED'))) ");

        if (typeFilter != null && !typeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(typeFilter)) {
            sql.append("  AND TRANSACTION_TYPE = ? ");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int p = 1;
            ps.setLong(p++, walletId);
            ps.setLong(p++, walletId);

            if (typeFilter != null && !typeFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(typeFilter)) {
                ps.setString(p++, typeFilter.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Transaction mapTransaction(ResultSet rs, Long myWalletId) throws SQLException {
        Transaction t = new Transaction();
        t.setTransactionId(rs.getLong("TRANSACTION_ID"));
        t.setTransactionRef(rs.getString("TRANSACTION_REF"));
        long sId = rs.getLong("SENDER_WALLET_ID");
        t.setSenderWalletId(rs.wasNull() ? null : sId);
        long rId = rs.getLong("RECEIVER_WALLET_ID");
        t.setReceiverWalletId(rs.wasNull() ? null : rId);
        t.setTransactionType(rs.getString("TRANSACTION_TYPE"));
        t.setAmount(rs.getBigDecimal("AMOUNT"));
        t.setDescription(rs.getString("DESCRIPTION"));
        t.setTransactionStatus(rs.getString("TRANSACTION_STATUS"));
        t.setCreatedAt(rs.getTimestamp("CREATED_AT"));

        // Set direction helper relative to current user's wallet
        if ("TOPUP".equalsIgnoreCase(t.getTransactionType()) || "TRANSFER_RECEIVED".equalsIgnoreCase(t.getTransactionType())) {
            t.setDirection("CREDIT");
        } else {
            t.setDirection("DEBIT");
        }

        return t;
    }
}
