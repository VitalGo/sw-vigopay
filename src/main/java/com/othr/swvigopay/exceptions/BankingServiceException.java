package com.othr.swvigopay.exceptions;

public class BankingServiceException extends Exception {

    public BankingServiceException() {
    }

    public BankingServiceException(String errorMessage) {
        super(errorMessage);
    }
}
