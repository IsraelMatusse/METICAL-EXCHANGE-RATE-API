package com.metical_converter.services;

import com.metical_converter.infrasctruture.exceptions.IllegalArgumentException;
import com.metical_converter.infrasctruture.exceptions.NotFoundException;
import com.metical_converter.infrasctruture.middleware.LocaleMiddleware;
import com.metical_converter.infrasctruture.utils.ConversionValidator;
import com.metical_converter.infrasctruture.utils.CurrencyCalculator;
import com.metical_converter.infrasctruture.utils.DateUtils;
import com.metical_converter.infrasctruture.utils.DescriptionFormatter;
import com.metical_converter.integration.BmWebClient;
import com.metical_converter.interfaces.enums.ConversionType;
import com.metical_converter.interfaces.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExchangeRateService {

    @Value("${application.base.currency}")
    public String appBaseCurrency;
    private final BmWebClient bmWebClient;
    private final MessageService messageService;
    private final LocaleMiddleware localeMiddleware;
    private final DescriptionFormatter descriptionFormatter;
    private final ConversionValidator conversionValidator;

    @Lazy
    @Autowired
    private ExchangeRateService self;
    public ExchangeRateService(BmWebClient bmWebClient, MessageService messageService, LocaleMiddleware localeMiddleware, DescriptionFormatter descriptionFormatter, ConversionValidator conversionValidator) {
        this.bmWebClient = bmWebClient;
        this.messageService = messageService;
        this.localeMiddleware = localeMiddleware;
        this.descriptionFormatter = descriptionFormatter;
        this.conversionValidator = conversionValidator;
    }

    @Cacheable(value = "exchangeRates", key = "'exchange-rates'")
    public ExchangeRateResponse getExchangeRates() throws SSLException {
        ExchangeRateResponse originalResponse = bmWebClient.getExchangeRatesClient();
        return formatExchangeRateResponse(originalResponse);
    }
    @Cacheable(value = "exchangeRatesByCurrency", keyGenerator = "customKeyGeneration")
    public RateResponse getExchangeRatesByCurrency(String currency) throws SSLException, NotFoundException {
        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        return findCurrencyRate(exchangeRateResponse, currency);
    }

    @Cacheable(value = "currencies", key = "'currencies'")
    public List<CurrencyResponse> getCurrencies() throws SSLException {
        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        return exchangeRateResponse.rates().stream()
                .map(CurrencyResponse::new)
                .toList();
    }

    @Cacheable(value = "sellForeignCurrency", keyGenerator = "customKeyGeneration")
    public CurrencyConversionResponse sellForeignCurrency(BigDecimal foreignAmount, String currency)
            throws SSLException, NotFoundException, IllegalArgumentException {

        conversionValidator.validateConversionParams(foreignAmount, currency);

        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        RateResponse rateResponse = findCurrencyRate(exchangeRateResponse, currency);

        BigDecimal exchangeRate = BigDecimal.valueOf(rateResponse.buy());
        BigDecimal meticalObtained = CurrencyCalculator.multiply(foreignAmount, exchangeRate);

        ConversionDetails conversion = new ConversionDetails(
                foreignAmount,
                currency.toUpperCase(),
                meticalObtained,
                appBaseCurrency,
                exchangeRate,
                ConversionType.SELL_FOREIGN_TO_MZN
        );

        ExchangeRateInfo exchangeInfo = ExchangeRateInfo.from(exchangeRateResponse);

        String description = descriptionFormatter.formatDetailedDescription(conversion, exchangeInfo);

        return new CurrencyConversionResponse(
                ExchangeRateInfo.from(exchangeRateResponse),
                conversion,
                description
        );
    }

    @Cacheable(value = "sellMetical", keyGenerator = "customKeyGeneration")
    public CurrencyConversionResponse sellMetical(BigDecimal meticalAmount, String targetCurrency)
            throws NotFoundException, SSLException, IllegalArgumentException {

        conversionValidator.validateConversionParams(meticalAmount, targetCurrency);

        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        RateResponse rateResponse = findCurrencyRate(exchangeRateResponse, targetCurrency);

        BigDecimal sellRate = BigDecimal.valueOf(rateResponse.sell());
        BigDecimal foreignCurrencyObtained = CurrencyCalculator.divide(meticalAmount, sellRate);

        ConversionDetails conversion = new ConversionDetails(
                meticalAmount,
                appBaseCurrency,
                foreignCurrencyObtained,
                targetCurrency.toUpperCase(),
                sellRate,
                ConversionType.SELL_MZN_FOR_FOREIGN
        );
        ExchangeRateInfo exchangeInfo = ExchangeRateInfo.from(exchangeRateResponse);

        String description = descriptionFormatter.formatDetailedDescription(conversion, exchangeInfo);

        return new CurrencyConversionResponse(
                ExchangeRateInfo.from(exchangeRateResponse),
                conversion,
                description
        );
    }

    @Cacheable(value = "currencyConversion", keyGenerator = "customKeyGeneration")
    public CurrencyConversionResponse convertCurrency(String from, String to, BigDecimal amount)
            throws SSLException, NotFoundException, IllegalArgumentException {

        conversionValidator.validateConversionParams(amount, from);
        conversionValidator.validateConversionParams(amount, to);

        String fromCurrency = from.trim().toUpperCase();
        String toCurrency = to.trim().toUpperCase();

        if (fromCurrency.equals(toCurrency)) {
            throw new IllegalArgumentException(
                    messageService.getLocalizedMessage("conflict.currency", localeMiddleware.getActualLocale())
            );
        }

        ConversionType conversionType = conversionValidator.determineConversionType(fromCurrency, toCurrency);

        return switch (conversionType) {
            case SELL_FOREIGN_TO_MZN -> sellForeignCurrency(amount, fromCurrency);
            case SELL_MZN_FOR_FOREIGN -> sellMetical(amount, toCurrency);
            case CROSS_CURRENCY -> convertViaBaseCurrency(fromCurrency, toCurrency, amount);
        };
    }
    private CurrencyConversionResponse convertViaBaseCurrency(String fromCurrency, String toCurrency, BigDecimal amount)
            throws SSLException, NotFoundException, IllegalArgumentException {

        CurrencyConversionResponse firstStep = sellForeignCurrency(amount, fromCurrency);
        BigDecimal mznAmount = firstStep.conversion().outputAmount();

        CurrencyConversionResponse secondStep = sellMetical(mznAmount, toCurrency);

        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        ExchangeRateInfo exchangeInfo = ExchangeRateInfo.from(exchangeRateResponse);

        BigDecimal effectiveRate = secondStep.conversion().outputAmount().divide(amount, 2, RoundingMode.HALF_UP);
        ConversionDetails conversion = new ConversionDetails(
                amount,
                fromCurrency,
                secondStep.conversion().outputAmount(),
                toCurrency,
                effectiveRate,
                ConversionType.CROSS_CURRENCY
        );

        String description = descriptionFormatter.formatDetailedDescription(conversion, exchangeInfo);
        return new CurrencyConversionResponse(exchangeInfo, conversion, description);
    }


    private RateResponse findCurrencyRate(ExchangeRateResponse exchangeRateResponse, String currency)
            throws NotFoundException {
        return exchangeRateResponse.rates().stream()
                .filter(rateResponse -> rateResponse.currency().equalsIgnoreCase(currency.trim()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        messageService.getLocalizedMessage("currency.not.found",
                                localeMiddleware.getActualLocale())
                ));
    }


    private ExchangeRateResponse formatExchangeRateResponse(ExchangeRateResponse originalResponse) {
        String formattedDate = DateUtils.formatDate(
                originalResponse.date(),
                DateTimeFormatter.ofPattern("yyyyMMdd"),
                "dd-MM-yyyy"
        );

        String formattedLastUpdate = DateUtils.formatDateTime(
                originalResponse.lastUpdate(),
                DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"),
                "dd-MM-yyyy HH:mm:ss"
        );

        return new ExchangeRateResponse(
                originalResponse.baseCurrency(),
                originalResponse.location(),
                originalResponse.name(),
                originalResponse.type(),
                formattedDate,
                formattedLastUpdate,
                originalResponse.rates()
        );
    }

}

