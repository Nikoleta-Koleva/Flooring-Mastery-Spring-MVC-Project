package com.sg.flooringmastery.exceptions;

public class OrderPersistenceException extends Exception {
    public OrderPersistenceException(String message) {
        super(message);
    }

    public OrderPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}