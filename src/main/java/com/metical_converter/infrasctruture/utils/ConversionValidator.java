package com.metical_converter.infrasctruture.utils;

import com.metical_converter.infrasctruture.exceptions.IllegalArgumentException;
import com.metical_converter.infrasctruture.middleware.LocaleMiddleware;
import com.metical_converter.interfaces.enums.ConversionType;
import com.metical_converter.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ConversionValidator {

    @Value("${application.base.currency}")
    public String appBaseCurrency;
    private final MessageService messageService;
    private final LocaleMiddleware localeMiddleware;
    public ConversionType determineConversionType(String fromCurrency, String toCurrency) throws IllegalArgumentException {
        boolean isFromMZN = fromCurrency.equals(appBaseCurrency);
        boolean isToMZN = toCurrency.equals(appBaseCurrency);

        if (!isFromMZN && isToMZN) return ConversionType.SELL_FOREIGN_TO_MZN;
        if (isFromMZN && !isToMZN) return ConversionType.SELL_MZN_FOR_FOREIGN;
        if (!isFromMZN && !isToMZN) return ConversionType.CROSS_CURRENCY;

        throw new IllegalArgumentException(
                messageService.getLocalizedMessage("invalid.conversion.params", localeMiddleware.getActualLocale())
        );
    }

    public void validateConversionParams(BigDecimal amount, String currency) throws IllegalArgumentException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    messageService.getLocalizedMessage("validation.amount.positive",
                            localeMiddleware.getActualLocale())
            );
        }
        validateCurrency(currency);
    }
    public void validateCurrency(String currency) throws IllegalArgumentException {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    messageService.getLocalizedMessage("validation.currency.required",
                            localeMiddleware.getActualLocale())
            );
        }
    }
}
