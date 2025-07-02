package com.metical_converter.infrasctruture.utils;

import com.metical_converter.infrasctruture.middleware.LocaleMiddleware;
import com.metical_converter.interfaces.responses.ConversionDetails;
import com.metical_converter.interfaces.responses.ExchangeRateInfo;
import com.metical_converter.services.MessageService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Component
public class DescriptionFormatter {

    private final MessageService messageService;
    private final LocaleMiddleware localeMiddleware;

    public DescriptionFormatter(MessageService messageService, LocaleMiddleware localeMiddleware) {
        this.messageService = messageService;
        this.localeMiddleware = localeMiddleware;
    }

    public String formatConversion(ConversionDetails conversion) {
        Locale currentLocale = localeMiddleware.getActualLocale();

        return switch (conversion.operationType()) {
            case SELL_FOREIGN_TO_MZN -> messageService.getLocalizedMessage(
                    "conversion.sell.foreign.to.mzn",
                    currentLocale,
                    formatAmount(conversion.inputAmount()),
                    conversion.inputCurrency(),
                    formatAmount(conversion.outputAmount())
            );

            case BUY_FOREIGN_WITH_MZN -> messageService.getLocalizedMessage(
                    "conversion.buy.foreign.with.mzn",
                    currentLocale,
                    formatAmount(conversion.outputAmount()),
                    conversion.outputCurrency(),
                    formatAmount(conversion.inputAmount())
            );

            case SELL_MZN_FOR_FOREIGN -> messageService.getLocalizedMessage(
                    "conversion.sell.mzn.for.foreign",
                    currentLocale,
                    formatAmount(conversion.inputAmount()),
                    formatAmount(conversion.outputAmount()),
                    conversion.outputCurrency()
            );
        };
    }

    public String formatDetailedDescription(ConversionDetails conversion, ExchangeRateInfo exchangeInfo) {
        Locale currentLocale = localeMiddleware.getActualLocale();

        String baseDescription = formatConversion(conversion);
        String rateInfo = messageService.getLocalizedMessage(
                "conversion.rate.info",
                currentLocale,
                formatExchangeRate(conversion.exchangeRate())
        );
        String timestamp = messageService.getLocalizedMessage(
                "conversion.timestamp",
                currentLocale,
                exchangeInfo.date(),
                extractTimeFromLastUpdate(exchangeInfo.lastUpdate())
        );

        return String.format("%s. %s. %s", baseDescription, rateInfo, timestamp);
    }

    private String formatAmount(BigDecimal amount) {
        Locale currentLocale = localeMiddleware.getActualLocale();
        NumberFormat formatter = NumberFormat.getNumberInstance(currentLocale);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        return formatter.format(amount);
    }

    private String formatExchangeRate(BigDecimal rate) {
        Locale currentLocale = localeMiddleware.getActualLocale();
        NumberFormat formatter = NumberFormat.getNumberInstance(currentLocale);
        formatter.setMinimumFractionDigits(4);
        formatter.setMaximumFractionDigits(6);
        return formatter.format(rate);
    }

    private String extractTimeFromLastUpdate(String lastUpdate) {
        if (lastUpdate != null && lastUpdate.contains(" ")) {
            return lastUpdate.split(" ")[1];
        }
        return lastUpdate;
    }
}
