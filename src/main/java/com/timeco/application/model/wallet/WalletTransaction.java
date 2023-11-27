package com.timeco.application.model.wallet;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn (name = "wallet_id")
    private Wallet wallet;

    private double amount;

    private String transactionType; // You can use an enum for transaction types: REFERRAL, RETURN, CANCEL, etc.

    private LocalDateTime transactionTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public WalletTransaction() {
    }

    public WalletTransaction(Wallet wallet, double amount, String transactionType, LocalDateTime transactionTime) {
        this.wallet = wallet;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionTime = transactionTime;
    }
}
