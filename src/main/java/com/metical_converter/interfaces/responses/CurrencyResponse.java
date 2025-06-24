package com.metical_converter.interfaces.responses;

public record CurrencyResponse(
        String currency,
        String location,
        String name
) {
    public CurrencyResponse(RateResponse response){
        this(response.currency(), response.location(), response.name());
    }
}
