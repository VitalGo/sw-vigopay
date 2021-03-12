package com.othr.swvigopay.exceptions;

public class NegativeAmountExceptions extends Exception {

    public NegativeAmountExceptions() {
    }

    public NegativeAmountExceptions(String errorMessage) {
        super(errorMessage);
    }
}
