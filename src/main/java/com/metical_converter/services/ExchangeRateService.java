package com.metical_converter.services;

import com.metical_converter.infrasctruture.exceptions.NotFoundException;
import com.metical_converter.infrasctruture.middleware.LocaleMiddleware;
import com.metical_converter.infrasctruture.utils.CurrencyCalculator;
import com.metical_converter.infrasctruture.utils.DateUtils;
import com.metical_converter.infrasctruture.utils.DescriptionFormatter;
import com.metical_converter.integration.BmWebClient;
import com.metical_converter.interfaces.enums.ConversionType;
import com.metical_converter.interfaces.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExchangeRateService {

    private final BmWebClient bmWebClient;
    private final MessageService messageService;
    private final LocaleMiddleware localeMiddleware;

    @Lazy
    @Autowired
    private ExchangeRateService self;
    public ExchangeRateService(BmWebClient bmWebClient, MessageService messageService, LocaleMiddleware localeMiddleware) {
        this.bmWebClient = bmWebClient;
        this.messageService = messageService;
        this.localeMiddleware = localeMiddleware;
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

    @Cacheable(value = "currencys", key = "'currencys'")
    public List<CurrencyResponse> getCurrencys() throws SSLException {
        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        return exchangeRateResponse.rates().stream()
                .map(CurrencyResponse::new)
                .toList();
    }

    @Cacheable(value = "sellForeignCurrency", keyGenerator = "customKeyGeneration")
    public CurrencyConversionResponse sellForeignCurrency(BigDecimal foreignAmount, String currency)
            throws SSLException, NotFoundException {

        validateConversionParams(foreignAmount, currency);

        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        RateResponse rateResponse = findCurrencyRate(exchangeRateResponse, currency);

        BigDecimal exchangeRate = BigDecimal.valueOf(rateResponse.buy());
        BigDecimal meticalObtained = CurrencyCalculator.multiply(foreignAmount, exchangeRate);

        ConversionDetails conversion = new ConversionDetails(
                foreignAmount,
                currency.toUpperCase(),
                meticalObtained,
                "MZN",
                exchangeRate,
                ConversionType.SELL_FOREIGN_TO_MZN
        );

        String description = DescriptionFormatter.formatConversion(conversion);

        return new CurrencyConversionResponse(
                ExchangeRateInfo.from(exchangeRateResponse),
                conversion,
                description
        );
    }
    @Cacheable(value = "buyForeignCurrency", keyGenerator = "customKeyGeneration")
    public CurrencyConversionResponse buyForeignCurrency(BigDecimal foreignAmount, String currency)
            throws NotFoundException, SSLException {

        validateConversionParams(foreignAmount, currency);

        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        RateResponse rateResponse = findCurrencyRate(exchangeRateResponse, currency);

        BigDecimal sellRate = BigDecimal.valueOf(rateResponse.sell());
        BigDecimal meticalNeeded = CurrencyCalculator.multiply(foreignAmount, sellRate);

        ConversionDetails conversion = new ConversionDetails(
                meticalNeeded,
                "MZN",
                foreignAmount,
                currency.toUpperCase(),
                sellRate,
                ConversionType.BUY_FOREIGN_WITH_MZN
        );

        String description = DescriptionFormatter.formatConversion(conversion);

        return new CurrencyConversionResponse(
                ExchangeRateInfo.from(exchangeRateResponse),
                conversion,
                description
        );
    }

    @Cacheable(value = "sellMetical", keyGenerator = "customKeyGeneration")
    public CurrencyConversionResponse sellMetical(BigDecimal meticalAmount, String targetCurrency)
            throws NotFoundException, SSLException {

        validateConversionParams(meticalAmount, targetCurrency);

        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        RateResponse rateResponse = findCurrencyRate(exchangeRateResponse, targetCurrency);

        BigDecimal buyRate = BigDecimal.valueOf(rateResponse.buy());
        BigDecimal foreignCurrencyObtained = CurrencyCalculator.divide(meticalAmount, buyRate);

        ConversionDetails conversion = new ConversionDetails(
                meticalAmount,
                "MZN",
                foreignCurrencyObtained,
                targetCurrency.toUpperCase(),
                buyRate,
                ConversionType.SELL_MZN_FOR_FOREIGN
        );

        String description = DescriptionFormatter.formatConversion(conversion);

        return new CurrencyConversionResponse(
                ExchangeRateInfo.from(exchangeRateResponse),
                conversion,
                description
        );
    }


    public String getBuyDescription(BigDecimal foreignCurrencyAmount, String foreignCurrency, BigDecimal meticalNeeded) {
        return String.format("Para comprar %s %s, você precisa de %s MZN",
                foreignCurrencyAmount, foreignCurrency, meticalNeeded);
    }

    public String getSellDescription(BigDecimal meticalAvailable, BigDecimal foreignCurrencyObtained, String targetCurrency) {
        return String.format("Com %s MZN, você consegue obter %s %s",
                meticalAvailable, foreignCurrencyObtained, targetCurrency);
    }

    public String sellForeignCurrencyDescription(BigDecimal meticalObtained, BigDecimal foreignAmout, String currency){
        return String.format("Com %s %s, Você consegue obter %s MZN",
                foreignAmout, currency, meticalObtained);
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

    private void validateConversionParams(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    messageService.getLocalizedMessage("validation.amount.positive",
                            localeMiddleware.getActualLocale())
            );
        }
        validateCurrency(currency);
    }
    private void validateCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    messageService.getLocalizedMessage("validation.currency.required",
                            localeMiddleware.getActualLocale())
            );
        }
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

