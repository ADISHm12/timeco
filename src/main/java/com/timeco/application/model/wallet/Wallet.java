package com.timeco.application.model.wallet;

import com.timeco.application.model.user.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wallet {

    @Id
    @GeneratedValue (strategy = GenerationType .IDENTITY)
    @Column (name = "wallet_id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User  user;

    private double balance;



    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        if (this.balance >= amount) {
            this.balance -= amount;
        } else {

        }
    }

    public List<WalletTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<WalletTransaction> transactions) {
        this.transactions = transactions;
    }

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List <WalletTransaction> transactions = new ArrayList<>() ;

    public void recordTransaction(double amount, String transactionType) {
        WalletTransaction transaction = new WalletTransaction();
        transaction.setWallet(this);
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionTime(LocalDateTime.now());

        transactions.add(transaction);
    }

    public Wallet(User user, double balance, List<WalletTransaction> transactions) {
        this.user = user;
        this.balance = balance;
        this.transactions = transactions;
    }

    public Wallet() {
        super();
    }
    public Wallet(User user, double balance) {
        this.user = user;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
