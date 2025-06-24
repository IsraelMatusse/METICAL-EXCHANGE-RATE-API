package com.metical_converter.interfaces.responses;

import java.math.BigDecimal;

public record ExchangeRateByAmount(
        String baseCurrency,
        String location,
        String name,
        String type,
        String date,
        String lastUpdate,
        String exchangeCurrency,
        double buyRate,
        double sellRate,
        BigDecimal amount,
        BigDecimal amountBuy,
        BigDecimal amountSell

) {
    public ExchangeRateByAmount(ExchangeRateResponse exchangeRateResponse, BigDecimal amount, BigDecimal amountBuy, BigDecimal amountSell, String currency, double buy, double sell){
        this(exchangeRateResponse.baseCurrency(),
                exchangeRateResponse.location(),
                exchangeRateResponse.name(),
                exchangeRateResponse.type(),
                exchangeRateResponse.date(),
                exchangeRateResponse.lastUpdate(),
                currency,
                buy,
                sell,
                amount,
                amountBuy,
                amountSell
        );
    }
}
