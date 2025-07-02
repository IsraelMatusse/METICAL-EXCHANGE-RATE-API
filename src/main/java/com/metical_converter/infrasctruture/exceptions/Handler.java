package com.metical_converter.infrasctruture.exceptions;
import com.metical_converter.interfaces.internal.ResponseApi;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class Handler {
    private final Logger logger= LoggerFactory.getLogger(Handler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseApi handleNotFound(HttpServletRequest request, NotFoundException ex) {
        Map<String, Object> errorDetails = createErrorDetails(request, HttpStatus.NOT_FOUND.name());
        logger.info("Resource not found: {} on path: {}", ex.getMessage(), request.getRequestURI());
        return new ResponseApi(ex.getMessage(), errorDetails);
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseApi handleTooMany(HttpServletRequest request, RateLimitExceededException ex) {
        Map<String, Object> errorDetails = createErrorDetails(request, HttpStatus.TOO_MANY_REQUESTS.name());
        return new ResponseApi(ex.getMessage(), errorDetails);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(java.lang.IllegalArgumentException.class)
    public ResponseApi handleIllegalArgumentException(HttpServletRequest request, java.lang.IllegalArgumentException ex) {
        Map<String, Object> errorDetails = createErrorDetails(request, HttpStatus.UNPROCESSABLE_ENTITY.name());
        logger.warn("Illegal argument: {} on path: {}", ex.getMessage(), request.getRequestURI());
        return new ResponseApi(ex.getMessage(), errorDetails);
    }

    private Map<String, Object> createErrorDetails(HttpServletRequest request, String errorCode) {
        Map<String, Object> details = new HashMap<>();
        details.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        details.put("path", request.getRequestURI());
        details.put("method", request.getMethod());
        details.put("errorCode", errorCode);
        return details;
    }
}



