package com.metical_converter.interfaces.responses;

import java.math.BigDecimal;

public record SellCurrencyResponse(
        String baseCurrency,
        String location,
        String name,
        String type,
        String date,
        String lastUpdate,
        double exchangeRate,
        BigDecimal meticalAvailable,
        BigDecimal foreignCurrencyObtained,
        String targetCurrency,
        String description

) {

    public SellCurrencyResponse(ExchangeRateResponse exchangeRateResponse,  BigDecimal meticalAvailable, BigDecimal foreignCurrencyObtained, String targetCurrency, double exchangeRate, String description) {
        this(exchangeRateResponse.baseCurrency(),
                exchangeRateResponse.location(),
                exchangeRateResponse.name(),
                exchangeRateResponse.type(),
                exchangeRateResponse.date(),
                exchangeRateResponse.lastUpdate(),
                exchangeRate,
                meticalAvailable,
                foreignCurrencyObtained,
                targetCurrency,
                description
        );
    }
}
