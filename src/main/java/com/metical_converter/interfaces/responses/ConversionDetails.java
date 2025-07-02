package com.metical_converter.interfaces.responses;

import com.metical_converter.interfaces.enums.ConversionType;

import java.math.BigDecimal;
import java.util.List;

public record ConversionDetails(
        BigDecimal inputAmount,
        String inputCurrency,
        BigDecimal outputAmount,
        String outputCurrency,
        BigDecimal exchangeRate,
        ConversionType operationType
) {}
