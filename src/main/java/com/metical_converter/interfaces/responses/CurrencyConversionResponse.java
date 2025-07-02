package com.metical_converter.interfaces.responses;
public record CurrencyConversionResponse(
        ExchangeRateInfo exchangeInfo,
        ConversionDetails conversion,
        String description
) {}
