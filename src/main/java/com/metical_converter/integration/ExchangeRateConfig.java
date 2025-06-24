package com.metical_converter.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration

public class ExchangeRateConfig {

    @Value("${bm.baseurl}")
    public String baseUrl;
    @Value("${bm.exchangerates}")
    public String getExchangerates;
    @Value("${bm.exchangeratesbycurrency}")
    public String getGetExchangeratesbycurrency;





}
