package com.metical_converter.interfaces.responses;

import java.util.List;

public record ExchangeRateResponse(
        String baseCurrency,
        String location,
        String name,
        String type,
        String date,
        String lastUpdate,
        List<RateResponse> rates
) {
}
