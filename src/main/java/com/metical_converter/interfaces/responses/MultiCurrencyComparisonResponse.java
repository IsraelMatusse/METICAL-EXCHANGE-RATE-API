package com.metical_converter.interfaces.responses;

import java.math.BigDecimal;
import java.util.List;

public record MultiCurrencyComparisonResponse(
        BigDecimal meticalAmount,
        List<CurrencyComparison> comparisons
) {
    public record CurrencyComparison(
            String currency,
            BigDecimal amountObtained,
            double exchangeRate
    ) {}

    // Encontrar a melhor opção (maior quantidade obtida)
    public CurrencyComparison getBestOption() {
        return comparisons.stream()
                .max((c1, c2) -> c1.amountObtained().compareTo(c2.amountObtained()))
                .orElse(null);
    }
}
