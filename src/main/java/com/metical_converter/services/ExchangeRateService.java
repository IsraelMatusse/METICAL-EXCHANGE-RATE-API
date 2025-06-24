package com.metical_converter.services;

import com.metical_converter.infrasctruture.exceptions.NotFoundException;
import com.metical_converter.integration.BmWebClient;
import com.metical_converter.interfaces.responses.CurrencyResponse;
import com.metical_converter.interfaces.responses.ExchangeRateByAmount;
import com.metical_converter.interfaces.responses.ExchangeRateResponse;
import com.metical_converter.interfaces.responses.RateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ExchangeRateService {

    private final BmWebClient bmWebClient;

    @Lazy
    @Autowired
    private ExchangeRateService self;
    public ExchangeRateService(BmWebClient bmWebClient) {
        this.bmWebClient = bmWebClient;
    }


    @Cacheable(value = "exchangeRates", key = "'exchange-rates'")
    public ExchangeRateResponse getExchangeRates() throws SSLException {
        return bmWebClient.getExchangeRatesClient();
    }

    @Cacheable(value = "exchangeRatesByCurrency", keyGenerator = "customKeyGeneration")
    public RateResponse getExchangeRatesByCurrency(String currency) throws SSLException, NotFoundException {
        ExchangeRateResponse exchangeRateResponse = self.getExchangeRates();
        return exchangeRateResponse.rates().stream()
                .filter(rateResponse -> rateResponse.currency().equals(currency))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Moeda não encontrada: " + currency));
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
                .orElseThrow(() -> new NotFoundException("Moeda não encontrada: " + currency));

        BigDecimal buyRate = new BigDecimal(rateResponse.buy());
        BigDecimal sellRate = new BigDecimal(rateResponse.sell());

        BigDecimal amountBuy = amount.multiply(buyRate).setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal amountSell = amount.multiply(sellRate).setScale(2, RoundingMode.HALF_EVEN);

        return new ExchangeRateByAmount(exchangeRateResponse, amount, amountBuy, amountSell, currency, rateResponse.buy(), rateResponse.sell());
    }
}

