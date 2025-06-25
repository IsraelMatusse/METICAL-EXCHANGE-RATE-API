package com.metical_converter.infrasctruture.exceptions;
import com.metical_converter.interfaces.internal.ResponseApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class Handler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseApi handleModelNotFound(NotFoundException e){
        return new ResponseApi( e.getMessage(), null);
    }
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseApi handleTooMany(HttpServletRequest request, RateLimitExceededException ex) {
        return new ResponseApi(ex.getMessage(),  null);
    }
}
