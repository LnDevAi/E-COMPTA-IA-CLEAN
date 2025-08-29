package com.ecomptaia.service;

import com.ecomptaia.entity.Currency;
import com.ecomptaia.entity.ExchangeRate;
import com.ecomptaia.repository.CurrencyRepository;
import com.ecomptaia.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    
    /**
     * Convertit un montant d'une devise à une autre
     */
    public BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        // Récupérer le taux de change le plus récent
        Optional<ExchangeRate> rateOpt = exchangeRateRepository.findLatestByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
        
        if (rateOpt.isPresent()) {
            ExchangeRate rate = rateOpt.get();
            return amount.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_UP);
        }
        
        // Si pas de taux direct, essayer la conversion inverse
        Optional<ExchangeRate> inverseRateOpt = exchangeRateRepository.findLatestByFromCurrencyAndToCurrency(toCurrency, fromCurrency);
        
        if (inverseRateOpt.isPresent()) {
            ExchangeRate inverseRate = inverseRateOpt.get();
            BigDecimal inverseRateValue = BigDecimal.ONE.divide(inverseRate.getRate(), 6, RoundingMode.HALF_UP);
            return amount.multiply(inverseRateValue).setScale(2, RoundingMode.HALF_UP);
        }
        
        throw new RuntimeException("Aucun taux de change trouvé pour " + fromCurrency + " vers " + toCurrency);
    }
    
    /**
     * Convertit un montant avec un taux spécifique
     */
    public BigDecimal convertAmountWithRate(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Récupère le taux de change le plus récent
     */
    public Optional<ExchangeRate> getLatestExchangeRate(String fromCurrency, String toCurrency) {
        return exchangeRateRepository.findLatestByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
    }
    
    /**
     * Récupère le taux de change pour une date spécifique
     */
    public Optional<ExchangeRate> getExchangeRateForDate(String fromCurrency, String toCurrency, LocalDate date) {
        return exchangeRateRepository.findByFromCurrencyAndToCurrencyAndDate(fromCurrency, toCurrency, date);
    }
    
    /**
     * Récupère l'historique des taux de change
     */
    public List<ExchangeRate> getExchangeRateHistory(String fromCurrency, String toCurrency) {
        return exchangeRateRepository.findByFromCurrencyAndToCurrencyOrderByDateDesc(fromCurrency, toCurrency);
    }
    
    /**
     * Ajoute un nouveau taux de change
     */
    public ExchangeRate addExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate, LocalDate date, String source) {
        ExchangeRate exchangeRate = new ExchangeRate(fromCurrency, toCurrency, rate, date, source);
        return exchangeRateRepository.save(exchangeRate);
    }
    
    /**
     * Met à jour un taux de change existant
     */
    public ExchangeRate updateExchangeRate(Long id, BigDecimal newRate, String source) {
        Optional<ExchangeRate> rateOpt = exchangeRateRepository.findById(id);
        if (rateOpt.isPresent()) {
            ExchangeRate rate = rateOpt.get();
            rate.setRate(newRate);
            rate.setSource(source);
            return exchangeRateRepository.save(rate);
        }
        throw new RuntimeException("Taux de change non trouvé avec l'ID: " + id);
    }
    
    /**
     * Récupère toutes les devises actives
     */
    public List<Currency> getAllActiveCurrencies() {
        return currencyRepository.findByIsActiveTrue();
    }
    
    /**
     * Récupère une devise par son code
     */
    public Optional<Currency> getCurrencyByCode(String code) {
        return currencyRepository.findByCodeAndIsActiveTrue(code);
    }
    
    /**
     * Ajoute une nouvelle devise
     */
    public Currency addCurrency(String code, String name, String symbol, BigDecimal exchangeRate, String country) {
        Currency currency = new Currency(code, name, symbol, exchangeRate, country);
        return currencyRepository.save(currency);
    }
    
    /**
     * Met à jour le taux de change d'une devise
     */
    public Currency updateCurrencyExchangeRate(String code, BigDecimal newRate) {
        Optional<Currency> currencyOpt = currencyRepository.findByCode(code);
        if (currencyOpt.isPresent()) {
            Currency currency = currencyOpt.get();
            currency.setExchangeRate(newRate);
            return currencyRepository.save(currency);
        }
        throw new RuntimeException("Devise non trouvée avec le code: " + code);
    }
    
    /**
     * Récupère les taux de change pour une date donnée
     */
    public List<ExchangeRate> getExchangeRatesForDate(LocalDate date) {
        return exchangeRateRepository.findByDateAndIsActiveTrue(date);
    }
    
    /**
     * Récupère toutes les devises sources disponibles
     */
    public List<String> getAllFromCurrencies() {
        return exchangeRateRepository.findAllDistinctFromCurrencies();
    }
    
    /**
     * Récupère toutes les devises cibles disponibles
     */
    public List<String> getAllToCurrencies() {
        return exchangeRateRepository.findAllDistinctToCurrencies();
    }
}





