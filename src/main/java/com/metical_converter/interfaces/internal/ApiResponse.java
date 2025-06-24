package com.metical_converter.interfaces.internal;

public record ApiResponse<T>(
        String message,
        T data
) {
}
