package com.wallet.dao;

import com.wallet.model.Wallet;
import com.wallet.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class WalletDAO {

    /**
     * Finds the wallet associated with a user ID.
     */
    public Wallet findByUserId(Long userId) throws SQLException {
        String sql = "SELECT WALLET_ID, USER_ID, BALANCE, CREATED_AT, STATUS FROM WALLETS WHERE USER_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapWallet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Finds a wallet by wallet ID.
     */
    public Wallet findById(Long walletId) throws SQLException {
        String sql = "SELECT WALLET_ID, USER_ID, BALANCE, CREATED_AT, STATUS FROM WALLETS WHERE WALLET_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, walletId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapWallet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Atomically adds money (Top-up) to a user's wallet with row locking and inserts a transaction.
     * Transaction logic:
     * 1. Lock wallet row FOR UPDATE.
     * 2. Increase balance.
     * 3. Insert transaction record (sender is NULL, receiver is user's wallet).
     * 4. Commit or Rollback.
     *
     * @return generated transaction reference string, or null on failure.
     */
    public String addMoney(Long walletId, BigDecimal amount, String description) throws SQLException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Top-up amount must be positive.");
        }

        String lockSql = "SELECT WALLET_ID, BALANCE, STATUS FROM WALLETS WHERE WALLET_ID = ? FOR UPDATE";
        String updateWalletSql = "UPDATE WALLETS SET BALANCE = BALANCE + ? WHERE WALLET_ID = ?";
        String insertTxnSql = "INSERT INTO TRANSACTIONS (TRANSACTION_ID, TRANSACTION_REF, SENDER_WALLET_ID, "
                + "RECEIVER_WALLET_ID, TRANSACTION_TYPE, AMOUNT, DESCRIPTION, TRANSACTION_STATUS) "
                + "VALUES (SEQ_TRANSACTIONS.NEXTVAL, ?, NULL, ?, 'TOPUP', ?, ?, 'SUCCESS')";

        Connection conn = null;
        PreparedStatement psLock = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psTxn = null;
        ResultSet rs = null;

        String txnRef = "TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 9000 + 1000);

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Lock row
            psLock = conn.prepareStatement(lockSql);
            psLock.setLong(1, walletId);
            rs = psLock.executeQuery();
            if (!rs.next()) {
                conn.rollback();
                return null;
            }
            String status = rs.getString("STATUS");
            if (!"ACTIVE".equalsIgnoreCase(status)) {
                conn.rollback();
                throw new IllegalStateException("Wallet is not active.");
            }

            // 2. Update balance
            psUpdate = conn.prepareStatement(updateWalletSql);
            psUpdate.setBigDecimal(1, amount);
            psUpdate.setLong(2, walletId);
            int rowsUpdated = psUpdate.executeUpdate();
            if (rowsUpdated == 0) {
                conn.rollback();
                return null;
            }

            // 3. Insert transaction
            psTxn = conn.prepareStatement(insertTxnSql);
            psTxn.setString(1, txnRef);
            psTxn.setLong(2, walletId);
            psTxn.setBigDecimal(3, amount);
            psTxn.setString(4, (description == null || description.trim().isEmpty()) ? "Simulated Wallet Top-Up" : description.trim());
            int rowsTxn = psTxn.executeUpdate();
            if (rowsTxn == 0) {
                conn.rollback();
                return null;
            }

            conn.commit();
            return txnRef;

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException ignored) {}
            if (psTxn != null) try { psTxn.close(); } catch (SQLException ignored) {}
            if (psUpdate != null) try { psUpdate.close(); } catch (SQLException ignored) {}
            if (psLock != null) try { psLock.close(); } catch (SQLException ignored) {}
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Atomically transfers money between two wallets with deadlock-free row locking.
     * Row locking order: always lock the smaller wallet_id first, then the larger wallet_id.
     * Records both TRANSFER_SENT and TRANSFER_RECEIVED with the same transaction reference.
     *
     * @return transaction reference if successful, throws exception with descriptive error on failure.
     */
    public String transferMoney(Long senderWalletId, Long receiverWalletId, BigDecimal amount, String description) throws SQLException {
        if (senderWalletId.equals(receiverWalletId)) {
            throw new IllegalArgumentException("Cannot transfer money to the same wallet.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be strictly positive.");
        }

        // Deadlock prevention: order locks by wallet ID
        long firstLockId = Math.min(senderWalletId, receiverWalletId);
        long secondLockId = Math.max(senderWalletId, receiverWalletId);

        String lockSql = "SELECT WALLET_ID, BALANCE, STATUS FROM WALLETS WHERE WALLET_ID = ? FOR UPDATE";
        String debitSql = "UPDATE WALLETS SET BALANCE = BALANCE - ? WHERE WALLET_ID = ?";
        String creditSql = "UPDATE WALLETS SET BALANCE = BALANCE + ? WHERE WALLET_ID = ?";
        String insertTxnSql = "INSERT INTO TRANSACTIONS (TRANSACTION_ID, TRANSACTION_REF, SENDER_WALLET_ID, "
                + "RECEIVER_WALLET_ID, TRANSACTION_TYPE, AMOUNT, DESCRIPTION, TRANSACTION_STATUS) "
                + "VALUES (SEQ_TRANSACTIONS.NEXTVAL, ?, ?, ?, ?, ?, ?, 'SUCCESS')";

        Connection conn = null;
        PreparedStatement psLock1 = null;
        PreparedStatement psLock2 = null;
        PreparedStatement psDebit = null;
        PreparedStatement psCredit = null;
        PreparedStatement psTxnSender = null;
        PreparedStatement psTxnReceiver = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;

        String txnRef = "TRF-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 9000 + 1000);

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Lock first wallet
            psLock1 = conn.prepareStatement(lockSql);
            psLock1.setLong(1, firstLockId);
            rs1 = psLock1.executeQuery();
            if (!rs1.next()) {
                conn.rollback();
                throw new IllegalStateException("Wallet not found: " + firstLockId);
            }
            String status1 = rs1.getString("STATUS");
            BigDecimal balance1 = rs1.getBigDecimal("BALANCE");

            // 2. Lock second wallet
            psLock2 = conn.prepareStatement(lockSql);
            psLock2.setLong(1, secondLockId);
            rs2 = psLock2.executeQuery();
            if (!rs2.next()) {
                conn.rollback();
                throw new IllegalStateException("Wallet not found: " + secondLockId);
            }
            String status2 = rs2.getString("STATUS");
            BigDecimal balance2 = rs2.getBigDecimal("BALANCE");

            // Identify sender balance & status
            BigDecimal senderBalance = (senderWalletId.equals(firstLockId)) ? balance1 : balance2;
            String senderStatus = (senderWalletId.equals(firstLockId)) ? status1 : status2;
            String receiverStatus = (receiverWalletId.equals(firstLockId)) ? status1 : status2;

            if (!"ACTIVE".equalsIgnoreCase(senderStatus)) {
                conn.rollback();
                throw new IllegalStateException("Your wallet is not active.");
            }
            if (!"ACTIVE".equalsIgnoreCase(receiverStatus)) {
                conn.rollback();
                throw new IllegalStateException("Receiver wallet is not active.");
            }

            // Verify sufficient balance
            if (senderBalance.compareTo(amount) < 0) {
                conn.rollback();
                throw new IllegalStateException("Insufficient wallet balance.");
            }

            // 3. Debit sender
            psDebit = conn.prepareStatement(debitSql);
            psDebit.setBigDecimal(1, amount);
            psDebit.setLong(2, senderWalletId);
            int rowsDebit = psDebit.executeUpdate();
            if (rowsDebit == 0) {
                conn.rollback();
                throw new SQLException("Failed to debit sender wallet.");
            }

            // 4. Credit receiver
            psCredit = conn.prepareStatement(creditSql);
            psCredit.setBigDecimal(1, amount);
            psCredit.setLong(2, receiverWalletId);
            int rowsCredit = psCredit.executeUpdate();
            if (rowsCredit == 0) {
                conn.rollback();
                throw new SQLException("Failed to credit receiver wallet.");
            }

            String desc = (description == null || description.trim().isEmpty()) ? "Direct Wallet Transfer" : description.trim();

            // 5. Insert sender transaction entry (TRANSFER_SENT)
            psTxnSender = conn.prepareStatement(insertTxnSql);
            psTxnSender.setString(1, txnRef + "-S");
            psTxnSender.setLong(2, senderWalletId);
            psTxnSender.setLong(3, receiverWalletId);
            psTxnSender.setString(4, "TRANSFER_SENT");
            psTxnSender.setBigDecimal(5, amount);
            psTxnSender.setString(6, desc);
            psTxnSender.executeUpdate();

            // 6. Insert receiver transaction entry (TRANSFER_RECEIVED) with matching base ref
            psTxnReceiver = conn.prepareStatement(insertTxnSql);
            psTxnReceiver.setString(1, txnRef + "-R");
            psTxnReceiver.setLong(2, senderWalletId);
            psTxnReceiver.setLong(3, receiverWalletId);
            psTxnReceiver.setString(4, "TRANSFER_RECEIVED");
            psTxnReceiver.setBigDecimal(5, amount);
            psTxnReceiver.setString(6, desc);
            psTxnReceiver.executeUpdate();

            conn.commit();
            return txnRef;

        } catch (SQLException | RuntimeException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw e;
        } finally {
            if (rs1 != null) try { rs1.close(); } catch (SQLException ignored) {}
            if (rs2 != null) try { rs2.close(); } catch (SQLException ignored) {}
            if (psLock1 != null) try { psLock1.close(); } catch (SQLException ignored) {}
            if (psLock2 != null) try { psLock2.close(); } catch (SQLException ignored) {}
            if (psDebit != null) try { psDebit.close(); } catch (SQLException ignored) {}
            if (psCredit != null) try { psCredit.close(); } catch (SQLException ignored) {}
            if (psTxnSender != null) try { psTxnSender.close(); } catch (SQLException ignored) {}
            if (psTxnReceiver != null) try { psTxnReceiver.close(); } catch (SQLException ignored) {}
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    private Wallet mapWallet(ResultSet rs) throws SQLException {
        Wallet w = new Wallet();
        w.setWalletId(rs.getLong("WALLET_ID"));
        w.setUserId(rs.getLong("USER_ID"));
        w.setBalance(rs.getBigDecimal("BALANCE"));
        w.setCreatedAt(rs.getTimestamp("CREATED_AT"));
        w.setStatus(rs.getString("STATUS"));
        return w;
    }
}
