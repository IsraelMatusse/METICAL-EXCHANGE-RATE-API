package com.metical_converter.infrasctruture.utils;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyCalculator {
    public static BigDecimal multiply(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal divide(BigDecimal amount, BigDecimal rate) {
        return amount.divide(rate, 2, RoundingMode.HALF_EVEN);
    }
}
