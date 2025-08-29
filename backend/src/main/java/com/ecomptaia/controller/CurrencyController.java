package com.ecomptaia.controller;

import com.ecomptaia.entity.Currency;
import com.ecomptaia.entity.ExchangeRate;
import com.ecomptaia.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/currency")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    // === GESTION DES DEVISES ===
    
    @GetMapping("/currencies")
    public ResponseEntity<?> getAllCurrencies() {
        List<Currency> currencies = currencyService.getAllActiveCurrencies();
        Map<String, Object> response = new HashMap<>();
        response.put("currencies", currencies);
        response.put("total", currencies.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/currencies/{code}")
    public ResponseEntity<?> getCurrencyByCode(@PathVariable String code) {
        Optional<Currency> currency = currencyService.getCurrencyByCode(code);
        if (currency.isPresent()) {
            return ResponseEntity.ok(currency.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/currencies")
    public ResponseEntity<?> addCurrency(@RequestBody Map<String, Object> request) {
        try {
            String code = (String) request.get("code");
            String name = (String) request.get("name");
            String symbol = (String) request.get("symbol");
            BigDecimal exchangeRate = new BigDecimal(request.get("exchangeRate").toString());
            String country = (String) request.get("country");

            Currency currency = currencyService.addCurrency(code, name, symbol, exchangeRate, country);
            return ResponseEntity.ok(currency);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === CONVERSION DE DEVISES ===
    
    @GetMapping("/convert")
    public ResponseEntity<?> convertAmount(
            @RequestParam BigDecimal amount,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        try {
            BigDecimal convertedAmount = currencyService.convertAmount(amount, fromCurrency, toCurrency);
            
            Map<String, Object> response = new HashMap<>();
            response.put("originalAmount", amount);
            response.put("fromCurrency", fromCurrency);
            response.put("toCurrency", toCurrency);
            response.put("convertedAmount", convertedAmount);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // === GESTION DES TAUX DE CHANGE ===
    
    @GetMapping("/exchange-rates")
    public ResponseEntity<?> getAllExchangeRates() {
        List<String> fromCurrencies = currencyService.getAllFromCurrencies();
        List<String> toCurrencies = currencyService.getAllToCurrencies();
        
        Map<String, Object> response = new HashMap<>();
        response.put("fromCurrencies", fromCurrencies);
        response.put("toCurrencies", toCurrencies);
        response.put("totalFromCurrencies", fromCurrencies.size());
        response.put("totalToCurrencies", toCurrencies.size());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exchange-rates/{fromCurrency}/{toCurrency}")
    public ResponseEntity<?> getLatestExchangeRate(
            @PathVariable String fromCurrency,
            @PathVariable String toCurrency) {
        Optional<ExchangeRate> rate = currencyService.getLatestExchangeRate(fromCurrency, toCurrency);
        if (rate.isPresent()) {
            return ResponseEntity.ok(rate.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exchange-rates/{fromCurrency}/{toCurrency}/history")
    public ResponseEntity<?> getExchangeRateHistory(
            @PathVariable String fromCurrency,
            @PathVariable String toCurrency) {
        List<ExchangeRate> history = currencyService.getExchangeRateHistory(fromCurrency, toCurrency);
        Map<String, Object> response = new HashMap<>();
        response.put("fromCurrency", fromCurrency);
        response.put("toCurrency", toCurrency);
        response.put("history", history);
        response.put("total", history.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exchange-rates/{fromCurrency}/{toCurrency}/date/{date}")
    public ResponseEntity<?> getExchangeRateForDate(
            @PathVariable String fromCurrency,
            @PathVariable String toCurrency,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<ExchangeRate> rate = currencyService.getExchangeRateForDate(fromCurrency, toCurrency, date);
        if (rate.isPresent()) {
            return ResponseEntity.ok(rate.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/exchange-rates")
    public ResponseEntity<?> addExchangeRate(@RequestBody Map<String, Object> request) {
        try {
            String fromCurrency = (String) request.get("fromCurrency");
            String toCurrency = (String) request.get("toCurrency");
            BigDecimal rate = new BigDecimal(request.get("rate").toString());
            LocalDate date = LocalDate.parse((String) request.get("date"));
            String source = (String) request.get("source");

            ExchangeRate exchangeRate = currencyService.addExchangeRate(fromCurrency, toCurrency, rate, date, source);
            return ResponseEntity.ok(exchangeRate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/exchange-rates/{id}")
    public ResponseEntity<?> updateExchangeRate(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal newRate = new BigDecimal(request.get("rate").toString());
            String source = (String) request.get("source");

            ExchangeRate exchangeRate = currencyService.updateExchangeRate(id, newRate, source);
            return ResponseEntity.ok(exchangeRate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/exchange-rates/date/{date}")
    public ResponseEntity<?> getExchangeRatesForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ExchangeRate> rates = currencyService.getExchangeRatesForDate(date);
        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("rates", rates);
        response.put("total", rates.size());
        return ResponseEntity.ok(response);
    }

    // === ENDPOINTS DE TEST ===
    
    @GetMapping("/test")
    public ResponseEntity<?> testCurrencySystem() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Système de devises opérationnel");
        response.put("availableEndpoints", List.of(
            "GET /api/currency/currencies",
            "GET /api/currency/convert",
            "GET /api/currency/exchange-rates",
            "POST /api/currency/exchange-rates"
        ));
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}





