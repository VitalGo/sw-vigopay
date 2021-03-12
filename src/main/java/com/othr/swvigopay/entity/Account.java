package com.othr.swvigopay.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
public class Account extends BaseEntity {

    // Attributes
    // -----------------------------------------------------
    @OneToMany(mappedBy="payerAccount")
    private List<Transfer> payers;

    @OneToMany(mappedBy="receiverAccount")
    private List<Transfer> receivers;

    @OneToOne
    private User user;

    @Column(columnDefinition = "boolean default true")
    private boolean isActive;

    private BigDecimal balance;

    // Constructors
    // -----------------------------------------------------
    public Account() {
        this.isActive = true;
    }

    public Account(User user) {
        this();
        this.user = user;
        this.setBalance(new BigDecimal(500.00));
    }

    // Methods
    // -----------------------------------------------------
    public List<Transfer> getPayerTransfers() {
        return payers;
    }

    public List<Transfer> getReceiverTransfers() {
        return receivers;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getBalance() {

        return this.balance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance.setScale(2, RoundingMode.HALF_EVEN);
    }
}
