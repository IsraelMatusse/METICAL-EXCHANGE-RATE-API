package com.metical_converter.interfaces.responses;

public record RateResponse(
        String currency,
        String location,
        String name,
        String lastUpdate,
        double buy,
        double sell
) {
}
