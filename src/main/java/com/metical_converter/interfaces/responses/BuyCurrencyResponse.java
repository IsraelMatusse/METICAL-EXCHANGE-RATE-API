package com.metical_converter.interfaces.responses;

import java.math.BigDecimal;

public record BuyCurrencyResponse(
        String baseCurrency,
        String location,
        String name,
        String type,
        String date,
        String lastUpdate,
        String foreignCurrency,
        double exchangeRate,
        BigDecimal foreignCurrencyAmount,
        BigDecimal meticalNeeded,
        String description

) {

    public BuyCurrencyResponse(ExchangeRateResponse exchangeRateResponse, BigDecimal amount, BigDecimal totalPurchased, String currency, double exchangeRate, String description) {
        this(exchangeRateResponse.baseCurrency(),
                exchangeRateResponse.location(),
                exchangeRateResponse.name(),
                exchangeRateResponse.type(),
                exchangeRateResponse.date(),
                exchangeRateResponse.lastUpdate(),
                currency,
                exchangeRate,
                amount,
                totalPurchased,
                description
        );
    }


}
