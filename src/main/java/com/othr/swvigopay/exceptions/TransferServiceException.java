package com.othr.swvigopay.exceptions;

public class TransferServiceException extends Exception {

    public TransferServiceException() {
    }

    public TransferServiceException(String errorMessage) {
        super(errorMessage);
    }
}