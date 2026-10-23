package com.wallet.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long transactionId;
    private String transactionRef;
    private Long senderWalletId;
    private Long receiverWalletId;
    private String transactionType; // 'TOPUP', 'TRANSFER_SENT', 'TRANSFER_RECEIVED'
    private BigDecimal amount;
    private String description;
    private String transactionStatus; // 'SUCCESS', 'FAILED', 'PENDING'
    private Timestamp createdAt;

    // Derived display helpers
    private String direction; // "CREDIT" or "DEBIT"
    private String counterpartyInfo; // Name or email of sender/receiver

    public Transaction() {
    }

    public Transaction(Long transactionId, String transactionRef, Long senderWalletId, Long receiverWalletId,
                       String transactionType, BigDecimal amount, String description, String transactionStatus,
                       Timestamp createdAt) {
        this.transactionId = transactionId;
        this.transactionRef = transactionRef;
        this.senderWalletId = senderWalletId;
        this.receiverWalletId = receiverWalletId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.transactionStatus = transactionStatus;
        this.createdAt = createdAt;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionRef() {
        return transactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
    }

    public Long getSenderWalletId() {
        return senderWalletId;
    }

    public void setSenderWalletId(Long senderWalletId) {
        this.senderWalletId = senderWalletId;
    }

    public Long getReceiverWalletId() {
        return receiverWalletId;
    }

    public void setReceiverWalletId(Long receiverWalletId) {
        this.receiverWalletId = receiverWalletId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCounterpartyInfo() {
        return counterpartyInfo;
    }

    public void setCounterpartyInfo(String counterpartyInfo) {
        this.counterpartyInfo = counterpartyInfo;
    }
}
