package com.metical_converter.interfaces.responses;

public record ExchangeRateInfo(
        String baseCurrency,
        String location,
        String name,
        String type,
        String date,
        String lastUpdate
) {
    public static ExchangeRateInfo from(ExchangeRateResponse response) {
        return new ExchangeRateInfo(
                response.baseCurrency(),
                response.location(),
                response.name(),
                response.type(),
                response.date(),
                response.lastUpdate()
        );
    }
}