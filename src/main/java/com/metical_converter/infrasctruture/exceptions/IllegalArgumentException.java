package com.metical_converter.infrasctruture.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class IllegalArgumentException extends Exception {
    private String statusCode;

    public IllegalArgumentException(String message) {
        super(message);
        this.statusCode = String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY);
    }


}
