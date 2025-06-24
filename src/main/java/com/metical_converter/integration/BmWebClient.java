package com.metical_converter.integration;

import com.metical_converter.infrasctruture.config.InsecureWebClient;
import com.metical_converter.interfaces.responses.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.SSLException;
import java.util.List;

@Component
public class BmWebClient {

    private final InsecureWebClient insecureWebClient;
    private final ExchangeRateConfig config;
    private final Logger logger = LoggerFactory.getLogger(BmWebClient.class);

    public BmWebClient(InsecureWebClient insecureWebClient, ExchangeRateConfig config) {
        this.insecureWebClient = insecureWebClient;
        this.config = config;
    }

    public ExchangeRateResponse getExchangeRatesClient() throws SSLException {
        WebClient client = insecureWebClient.createInsecureWebClient();
        String uri = config.baseUrl + config.getExchangerates;
        ExchangeRateResponse response = client.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .block();
        assert response != null;
        return response;
    }

    public List<ExchangeRateResponse> getExchangeRatesByCurrency(String currency) throws SSLException {
        WebClient client = insecureWebClient.createInsecureWebClient();
        String uri = config.baseUrl + config.getExchangerates;

        List<ExchangeRateResponse> response = client.get()
                .uri(uri, currency)
                .retrieve()
                .bodyToFlux(ExchangeRateResponse.class)
                .collectList()
                .block();
        assert response != null;
        return response;
    }
    }



