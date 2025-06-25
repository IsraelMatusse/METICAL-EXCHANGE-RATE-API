package com.metical_converter.infrasctruture.exceptions;

public class RateLimitExceededException extends Exception {
    public RateLimitExceededException(String message){
        super(message);
    }
}
