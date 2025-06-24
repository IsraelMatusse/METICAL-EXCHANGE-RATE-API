package com.metical_converter.interfaces.controllers;

import com.metical_converter.infrasctruture.exceptions.NotFoundException;
import com.metical_converter.interfaces.internal.ApiResponse;
import com.metical_converter.interfaces.responses.CurrencyResponse;
import com.metical_converter.interfaces.responses.ExchangeRateByAmount;
import com.metical_converter.interfaces.responses.ExchangeRateResponse;
import com.metical_converter.interfaces.responses.RateResponse;
import com.metical_converter.services.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.SSLException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exchange-rates")
@Tag(name = "Taxas de Câmbio")
public class ExchangeRatesController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping
    @Operation(summary = "Listar Taxas de Câmbio")
    public ExchangeRateResponse getExchangeRates() throws SSLException {
        return exchangeRateService.getExchangeRates();
    }
    @GetMapping("/currencys")
    @Operation(summary = "Listar moedas Disponíveis")
    public ResponseEntity<ApiResponse<List<CurrencyResponse>>> getCurrencys() throws SSLException {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Moedas Buscadas", exchangeRateService.getCurrencys()));
    }

    @GetMapping("/currency/{currency}")
    @Operation(summary = "Buscar Taxa de Câmbio por moeda")
    public ResponseEntity<ApiResponse<RateResponse>> getExchangeRatesByCurrency(@PathVariable(value = "currency" ) String currency) throws SSLException, NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Taxa de Cambuo por moeda", exchangeRateService.getExchangeRatesByCurrency(currency)));
    }

    @GetMapping("/currency/{currency}/amount/{amount}")
    @Operation(summary = "Buscar Taxa de Câmbio por moeda e Valor")
    public ResponseEntity<ApiResponse<ExchangeRateByAmount>> getExchangeRateByAmountAndCurrency(
            @PathVariable("amount") String amountStr,
            @PathVariable("currency") String currency
    ) throws SSLException, NotFoundException {
        BigDecimal amount = convertFormattedAmount(amountStr);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("Taxa de Cambuo por moeda e valor", exchangeRateService.getExchangeRateByAmountAndCurrency(amount, currency)));
    }

    public  BigDecimal convertFormattedAmount(String amountStr) {
        String cleaned = amountStr.replace(",", "").trim();
        return new BigDecimal(cleaned);
    }

}
