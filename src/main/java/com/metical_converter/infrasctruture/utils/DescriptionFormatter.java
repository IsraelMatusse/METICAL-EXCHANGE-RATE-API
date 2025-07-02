package com.metical_converter.infrasctruture.utils;

import com.metical_converter.interfaces.responses.ConversionDetails;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DescriptionFormatter {

    public static String formatConversion(ConversionDetails conversion) {
        return switch (conversion.operationType()) {
            case SELL_FOREIGN_TO_MZN ->
                    String.format("Vendendo %.2f %s, você recebe %.2f MZN",
                            conversion.inputAmount(), conversion.inputCurrency(), conversion.outputAmount());

            case BUY_FOREIGN_WITH_MZN ->
                    String.format("Para comprar %.2f %s, você precisa de %.2f MZN",
                            conversion.outputAmount(), conversion.outputCurrency(), conversion.inputAmount());

            case SELL_MZN_FOR_FOREIGN ->
                    String.format("Com %.2f MZN, você consegue %.2f %s",
                            conversion.inputAmount(), conversion.outputAmount(), conversion.outputCurrency());

            case CURRENCY_TO_CURRENCY ->
                    String.format("Convertendo %.2f %s para %.2f %s (taxa: %.6f)",
                            conversion.inputAmount(), conversion.inputCurrency(),
                            conversion.outputAmount(), conversion.outputCurrency(),
                            conversion.exchangeRate());
        };
    }
}
