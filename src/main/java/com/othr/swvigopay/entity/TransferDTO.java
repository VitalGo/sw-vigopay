package com.othr.swvigopay.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransferDTO implements Serializable {

    // Attributes
    // -----------------------------------------------------
    private String payerEmail;
    private String receiverEmail;
    private BigDecimal amount;
    private String description;
    private State state;

    // Constructors
    // -----------------------------------------------------
    public TransferDTO() {

    }

    public TransferDTO(String payerEmail, String receiverEmail, BigDecimal amount, String description) {
        this.payerEmail = payerEmail;
        this.receiverEmail = receiverEmail;
        this.amount = amount;
        this.description = description;
    }

    // Methods
    // -----------------------------------------------------
    public String getPayerEmail() {
        return payerEmail;
    }

    public void setPayerEmail(String payerEmail) {
        this.payerEmail = payerEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
