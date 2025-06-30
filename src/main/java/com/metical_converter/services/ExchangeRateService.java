package com.metical_converter.services;

import com.metical_converter.infrasctruture.exceptions.NotFoundException;
import com.metical_converter.infrasctruture.middleware.LocaleMiddleware;
import com.metical_converter.infrasctruture.utils.DateUtils;
import com.metical_converter.integration.BmWebClient;
import com.metical_converter.interfaces.responses.*;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Cacheable(value = "exchangeRatesByCurrency", keyGenerator = "customKeyGeneration")
    public RateResponse getExchangeRatesByCurrency(String currency) throws SSLException, NotFoundException {
        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        return exchangeRateResponse.rates().stream()
                .filter(rateResponse -> rateResponse.currency().equals(currency))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(messageService.getLocalizedMessage("currency.not.found", localeMiddleware.getActualLocale())));
    }

    @Cacheable(value = "currencys", key = "'currencys'")
    public List<CurrencyResponse> getCurrencys() throws SSLException {
        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        return exchangeRateResponse.rates().stream()
                .map(CurrencyResponse::new)
                .toList();
    }

    @Cacheable(value = "exchangeRateByAmountAndCurrency", keyGenerator = "customKeyGeneration")
    public ExchangeRateByAmount getExchangeRateByAmountAndCurrency(BigDecimal amount, String currency) throws SSLException, NotFoundException {
        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        RateResponse rateResponse = exchangeRateResponse.rates().stream()
                .filter(rate -> rate.currency().equals(currency))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(messageService.getLocalizedMessage("currency.not.found", localeMiddleware.getActualLocale())));

        BigDecimal buyRate = new BigDecimal(rateResponse.buy());
        BigDecimal sellRate = new BigDecimal(rateResponse.sell());

        BigDecimal amountBuy = amount.multiply(buyRate).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal amountSell = amount.multiply(sellRate).setScale(2, RoundingMode.HALF_EVEN);

        return new ExchangeRateByAmount(exchangeRateResponse, amount, amountBuy, amountSell, currency, rateResponse.buy(), rateResponse.sell());
    }

    @Cacheable(value = "buyCurrency", keyGenerator = "customKeyGeneration")
    public BuyCurrencyResponse buyCurrency(BigDecimal foreignAmount, String currency) throws NotFoundException, SSLException {
        if (foreignAmount == null || foreignAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    messageService.getLocalizedMessage("validation.amount.positive", localeMiddleware.getActualLocale())
            );
        }

        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    messageService.getLocalizedMessage("validation.currency.required", localeMiddleware.getActualLocale())
            );
        }

        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        RateResponse rateResponse = exchangeRateResponse.rates().stream()
                .filter(rate -> rate.currency().equals(currency.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        messageService.getLocalizedMessage("currency.not.found", localeMiddleware.getActualLocale())
                ));

        double sellRate = rateResponse.sell();

        BigDecimal meticalNeeded = foreignAmount.multiply(BigDecimal.valueOf(sellRate))
                .setScale(2, RoundingMode.HALF_EVEN);

        String description = getBuyDescription(foreignAmount, currency, meticalNeeded);
        return new BuyCurrencyResponse(
                exchangeRateResponse,
                foreignAmount,
                meticalNeeded,
                currency.toUpperCase(),
                sellRate,
                description
        );
    }

    @Cacheable(value = "sellCurrency", keyGenerator = "customKeyGeneration")
    public SellCurrencyResponse sellCurrency(BigDecimal meticalAmount, String targetCurrency) throws NotFoundException, SSLException {
        if (meticalAmount == null || meticalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    messageService.getLocalizedMessage("validation.amount.positive", localeMiddleware.getActualLocale())
            );
        }

        if (targetCurrency == null || targetCurrency.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    messageService.getLocalizedMessage("validation.currency.required", localeMiddleware.getActualLocale())
            );
        }

        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        RateResponse rateResponse = exchangeRateResponse.rates().stream()
                .filter(rate -> rate.currency().equals(targetCurrency.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        messageService.getLocalizedMessage("currency.not.found", localeMiddleware.getActualLocale())
                ));

        double buyRate = rateResponse.buy();

        BigDecimal foreignCurrencyObtained = meticalAmount.divide(BigDecimal.valueOf(buyRate), 2, RoundingMode.HALF_EVEN);
        String description= getSellDescription(meticalAmount, foreignCurrencyObtained, targetCurrency);
        return new SellCurrencyResponse(
                exchangeRateResponse,
                meticalAmount,
                foreignCurrencyObtained,
                targetCurrency,
                buyRate,
                description
        );
    }


    public MultiCurrencyComparisonResponse compareMultipleCurrencies(BigDecimal meticalAmount, List<String> currencies) throws SSLException {
        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();

        List<MultiCurrencyComparisonResponse.CurrencyComparison> comparisons = currencies.stream()
                .map(currency -> {
                    try {
                        SellCurrencyResponse response = sellCurrency(meticalAmount, currency);
                        return new MultiCurrencyComparisonResponse.CurrencyComparison(
                                currency,
                                response.foreignCurrencyObtained(),
                                response.exchangeRate()
                        );
                    } catch (Exception e) {
                        return new MultiCurrencyComparisonResponse.CurrencyComparison(currency, BigDecimal.ZERO, 0.0);
                    }
                })
                .toList();

        return new MultiCurrencyComparisonResponse(meticalAmount, comparisons);
    }



    public String getBuyDescription(BigDecimal foreignCurrencyAmount, String foreignCurrency, BigDecimal meticalNeeded) {
        return String.format("Para comprar %s %s, você precisa de %s MZN",
                foreignCurrencyAmount, foreignCurrency, meticalNeeded);
    }

    public String getSellDescription(BigDecimal meticalAvailable, BigDecimal foreignCurrencyObtained, String targetCurrency) {
        return String.format("Com %s MZN, você consegue obter %s %s",
                meticalAvailable, foreignCurrencyObtained, targetCurrency);
    }


}

