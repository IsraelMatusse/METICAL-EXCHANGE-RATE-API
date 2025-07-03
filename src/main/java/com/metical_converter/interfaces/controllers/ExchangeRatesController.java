package com.metical_converter.interfaces.controllers;

import com.metical_converter.infrasctruture.config.RateLimited;
import com.metical_converter.infrasctruture.exceptions.IllegalArgumentException;
import com.metical_converter.infrasctruture.exceptions.NotFoundException;
import com.metical_converter.infrasctruture.middleware.LocaleMiddleware;
import com.metical_converter.interfaces.internal.ApiResponse;
import com.metical_converter.interfaces.responses.*;
import com.metical_converter.services.ExchangeRateService;
import com.metical_converter.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.SSLException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Taxas de Câmbio")
public class ExchangeRatesController {

    private final ExchangeRateService exchangeRateService;
    private final MessageService messageService;
    private final LocaleMiddleware localeMiddleware;

    @GetMapping("/exchange-rates")
    @RateLimited
    @Operation(summary = "Listar Taxas de Câmbio disponíveis")
    public ResponseEntity<ApiResponse<ExchangeRateResponse>> getExchangeRates() throws SSLException {

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(messageService.getLocalizedMessage("exchange.rates", localeMiddleware.getActualLocale()), exchangeRateService.getExchangeRates()));
    }

    @RateLimited
    @GetMapping("/currencies")
    @Operation(summary = "Listar moedas Disponíveis")
    public ResponseEntity<ApiResponse<List<CurrencyResponse>>> getCurrencys() throws SSLException {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(messageService.getLocalizedMessage("currencys.found", localeMiddleware.getActualLocale()), exchangeRateService.getCurrencies()));
    }

    @GetMapping("/exchange-rates/{currency}")
    @RateLimited
    @Operation(summary = "Buscar Taxa de Câmbio por moeda")
    public ResponseEntity<ApiResponse<RateResponse>> getExchangeRatesByCurrency(@PathVariable(value = "currency") String currency) throws SSLException, NotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(messageService.getLocalizedMessage("exchange.rate.by.currency", localeMiddleware.getActualLocale()), exchangeRateService.getExchangeRatesByCurrency(currency)));
    }


    @GetMapping("/exchange/quote")
    @RateLimited
    @Operation(summary = "Calcula conversão entre moedas usando parâmetros from/to")
    public ResponseEntity<ApiResponse<CurrencyConversionResponse>> getQuote(
            @RequestParam @Valid @NotBlank String from,
            @RequestParam @Valid @NotBlank String to,
            @RequestParam @Valid @DecimalMin(value = "0.01") BigDecimal amount
    ) throws SSLException, NotFoundException, IllegalArgumentException {

        CurrencyConversionResponse response = exchangeRateService.convertCurrency(from, to, amount);
        return ResponseEntity.ok(new ApiResponse<>(
                messageService.getLocalizedMessage("conversion.success", localeMiddleware.getActualLocale()),
                response
        ));
    }

    @GetMapping("/sell-foreign-currency")
    @RateLimited
    @Deprecated
    @Operation(summary = "DEPRECATED: Use /quote com from=CURRENCY&to=MZN", deprecated = true)
    public ResponseEntity<ApiResponse<CurrencyConversionResponse>> sellForeignCurrency(
            @RequestParam @Valid @DecimalMin(value = "0.01") BigDecimal amount,
            @RequestParam @Valid @NotBlank String currency
    ) throws SSLException, NotFoundException, IllegalArgumentException {
        return getQuote(currency, "MZN", amount);
    }


    @GetMapping("/buy-foreign-currency")
    @RateLimited
    @Deprecated
    @Operation(summary = "DEPRECATED: Use /quote com from=MZN&to=CURRENCY", deprecated = true)
    public ResponseEntity<ApiResponse<CurrencyConversionResponse>> buyCurrency(
            @RequestParam @Valid @DecimalMin(value = "0.01") BigDecimal amount,
            @RequestParam @Valid @NotBlank String currency
    ) throws SSLException, NotFoundException, IllegalArgumentException {
        return getQuote("MZN", currency, amount);
    }


    @GetMapping("/sell-metical")
    @RateLimited
    @Deprecated
    @Operation(summary = "DEPRECATED: Use /quote com from=MZN&to=CURRENCY", deprecated = true)
    public ResponseEntity<ApiResponse<CurrencyConversionResponse>> sellCurrency(
            @RequestParam @Valid @DecimalMin(value = "0.01") BigDecimal amount,
            @RequestParam @Valid @NotBlank String currency
    ) throws SSLException, NotFoundException, IllegalArgumentException {
        return getQuote("MZN", currency, amount);
    }

}
