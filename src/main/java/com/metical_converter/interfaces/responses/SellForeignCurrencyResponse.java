package com.metical_converter.interfaces.responses;

import java.math.BigDecimal;

public record SellForeignCurrencyResponse(
        String baseCurrency,
        String location,
        String name,
        String type,
        String date,
        String lastUpdate,
        BigDecimal foreignAmount,
        BigDecimal meticalObtained,
        String foreignCurrency,
        double exchangeRate,
        String description
) {
    public  SellForeignCurrencyResponse(ExchangeRateResponse exchangeRateResponse, BigDecimal foreignAmount, BigDecimal meticalObtained, String foreignCurrency, double exchangeRate, String description) {
        this(exchangeRateResponse.baseCurrency(),
                exchangeRateResponse.location(),
                exchangeRateResponse.name(),
                exchangeRateResponse.type(),
                exchangeRateResponse.date(),
                exchangeRateResponse.lastUpdate(),
                foreignAmount,
                meticalObtained,
                foreignCurrency,
                exchangeRate,
                description
        );
    }
}
