package com.wallet.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Wallet implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long walletId;
    private Long userId;
    private BigDecimal balance;
    private Timestamp createdAt;
    private String status;

    public Wallet() {
        this.balance = BigDecimal.ZERO;
    }

    public Wallet(Long walletId, Long userId, BigDecimal balance, Timestamp createdAt, String status) {
        this.walletId = walletId;
        this.userId = userId;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
