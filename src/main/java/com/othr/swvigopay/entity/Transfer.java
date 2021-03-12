package com.othr.swvigopay.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

@Entity
public class Transfer extends BaseEntity {

    // Attributes
    // -----------------------------------------------------
    @ManyToOne
    private Account payerAccount;

    @ManyToOne
    private Account receiverAccount;

    private BigDecimal amount;
    private Timestamp requestTime;
    private Timestamp processedTime;
    private String description;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column(columnDefinition = "boolean default false")
    private boolean fromExternal;

    // Constructors
    // -----------------------------------------------------
    public Transfer() {}

    public Transfer(Account payerAccount, Account receiverAccount, BigDecimal amount, Timestamp requestTime, Timestamp processedTime, String description, State state) {
        this.payerAccount = payerAccount;
        this.receiverAccount = receiverAccount;
        this.amount = amount;
        this.requestTime = requestTime;
        this.processedTime = processedTime;
        this.description = description;
        this.state = state;
    }

    // Methods
    // -----------------------------------------------------
    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public Timestamp getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(Timestamp executeTime) {
        this.processedTime = executeTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);;
    }

    public Account getPayerAccount() {
        return payerAccount;
    }

    public void setPayerAccount(Account payerAccount) {
        this.payerAccount = payerAccount;
    }

    public Account getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(Account receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public boolean isFromExternal() {
        return fromExternal;
    }

    public void setFromExternal(boolean fromExternal) {
        this.fromExternal = fromExternal;
    }
}
