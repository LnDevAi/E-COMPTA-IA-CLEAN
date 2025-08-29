package com.ecomptaia.service;

import com.ecomptaia.entity.Tax;
import com.ecomptaia.repository.TaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaxService {
    
    @Autowired
    private TaxRepository taxRepository;
    
    @Autowired
    private CurrencyService currencyService;
    
    /**
     * Calcule le montant de taxe pour un montant donné
     */
    public BigDecimal calculateTaxAmount(BigDecimal amount, String countryCode, String taxType) {
        List<Tax> taxes = taxRepository.findByCountryCodeAndTaxTypeAndIsActiveTrue(countryCode, taxType);
        
        if (taxes.isEmpty()) {
            throw new RuntimeException("Aucune taxe trouvée pour " + countryCode + " et type " + taxType);
        }
        
        // Prendre la première taxe (normalement il n'y en a qu'une par type)
        Tax tax = taxes.get(0);
        return calculateTaxAmountWithTax(amount, tax);
    }
    
    /**
     * Calcule le montant de taxe avec une taxe spécifique
     */
    public BigDecimal calculateTaxAmountWithTax(BigDecimal amount, Tax tax) {
        BigDecimal rate = tax.getRate().divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcule toutes les taxes pour un montant donné dans un pays
     */
    public Map<String, Object> calculateAllTaxes(BigDecimal amount, String countryCode, String targetCurrency) {
        List<Tax> taxes = taxRepository.findByCountryCodeAndIsActiveTrue(countryCode);
        
        Map<String, Object> result = new HashMap<>();
        result.put("countryCode", countryCode);
        result.put("originalAmount", amount);
        result.put("targetCurrency", targetCurrency);
        
        Map<String, Object> taxCalculations = new HashMap<>();
        BigDecimal totalTaxAmount = BigDecimal.ZERO;
        
        for (Tax tax : taxes) {
            BigDecimal taxAmount = calculateTaxAmountWithTax(amount, tax);
            
            // Convertir en devise cible si nécessaire
            BigDecimal convertedTaxAmount = taxAmount;
            if (!tax.getCurrency().equals(targetCurrency)) {
                try {
                    convertedTaxAmount = currencyService.convertAmount(taxAmount, tax.getCurrency(), targetCurrency);
                } catch (Exception e) {
                    // Si la conversion échoue, garder le montant original
                    convertedTaxAmount = taxAmount;
                }
            }
            
            Map<String, Object> taxInfo = new HashMap<>();
            taxInfo.put("taxName", tax.getTaxName());
            taxInfo.put("taxType", tax.getTaxType());
            taxInfo.put("rate", tax.getRate());
            taxInfo.put("originalCurrency", tax.getCurrency());
            taxInfo.put("originalAmount", taxAmount);
            taxInfo.put("convertedAmount", convertedTaxAmount);
            taxInfo.put("isMandatory", tax.getIsMandatory());
            taxInfo.put("frequency", tax.getFrequency());
            
            taxCalculations.put(tax.getTaxType(), taxInfo);
            totalTaxAmount = totalTaxAmount.add(convertedTaxAmount);
        }
        
        result.put("taxes", taxCalculations);
        result.put("totalTaxAmount", totalTaxAmount);
        result.put("totalTaxRate", calculateTotalTaxRate(taxes));
        
        return result;
    }
    
    /**
     * Calcule le taux de taxe total
     */
    public BigDecimal calculateTotalTaxRate(List<Tax> taxes) {
        return taxes.stream()
                .map(Tax::getRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Récupère toutes les taxes d'un pays
     */
    public List<Tax> getTaxesByCountry(String countryCode) {
        return taxRepository.findByCountryCodeAndIsActiveTrue(countryCode);
    }
    
    /**
     * Récupère les taxes par type pour un pays
     */
    public List<Tax> getTaxesByCountryAndType(String countryCode, String taxType) {
        return taxRepository.findByCountryCodeAndTaxTypeAndIsActiveTrue(countryCode, taxType);
    }
    
    /**
     * Récupère toutes les taxes d'un type donné
     */
    public List<Tax> getTaxesByType(String taxType) {
        return taxRepository.findByTaxTypeAndIsActiveTrue(taxType);
    }
    
    /**
     * Récupère les types de taxes disponibles pour un pays
     */
    public List<String> getTaxTypesByCountry(String countryCode) {
        return taxRepository.findDistinctTaxTypesByCountry(countryCode);
    }
    
    /**
     * Récupère tous les types de taxes disponibles
     */
    public List<String> getAllTaxTypes() {
        return taxRepository.findAllDistinctTaxTypes();
    }
    
    /**
     * Récupère tous les pays avec des taxes
     */
    public List<String> getAllCountriesWithTaxes() {
        return taxRepository.findAllDistinctCountryCodes();
    }
    
    /**
     * Récupère les taxes obligatoires d'un pays
     */
    public List<Tax> getMandatoryTaxesByCountry(String countryCode) {
        return taxRepository.findByCountryCodeAndIsMandatoryTrueAndIsActiveTrue(countryCode);
    }
    
    /**
     * Compare les taux de taxes entre pays
     */
    public Map<String, Object> compareTaxRates(String taxType, List<String> countryCodes) {
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("taxType", taxType);
        comparison.put("countries", countryCodes);
        
        Map<String, Object> countryRates = new HashMap<>();
        
        for (String countryCode : countryCodes) {
            List<Tax> taxes = taxRepository.findByCountryCodeAndTaxTypeAndIsActiveTrue(countryCode, taxType);
            if (!taxes.isEmpty()) {
                Tax tax = taxes.get(0);
                Map<String, Object> taxInfo = new HashMap<>();
                taxInfo.put("taxName", tax.getTaxName());
                taxInfo.put("rate", tax.getRate());
                taxInfo.put("currency", tax.getCurrency());
                taxInfo.put("description", tax.getDescription());
                countryRates.put(countryCode, taxInfo);
            }
        }
        
        comparison.put("rates", countryRates);
        return comparison;
    }
    
    /**
     * Trouve les pays avec les taux de taxes les plus élevés
     */
    public List<Tax> getTopTaxRates(String taxType, int limit) {
        return taxRepository.findTopTaxesByTypeAndMinRate(taxType, 0.0).stream()
                .limit(limit)
                .toList();
    }
    
    /**
     * Ajoute une nouvelle taxe
     */
    public Tax addTax(String countryCode, String countryName, String taxType, String taxName, 
                     BigDecimal rate, String currency, String description, String frequency) {
        Tax tax = new Tax(countryCode, countryName, taxType, taxName, rate, currency, 
                         null, null, description, frequency);
        return taxRepository.save(tax);
    }
    
    /**
     * Met à jour une taxe existante
     */
    public Tax updateTax(Long id, BigDecimal newRate, String description) {
        Optional<Tax> taxOpt = taxRepository.findById(id);
        if (taxOpt.isPresent()) {
            Tax tax = taxOpt.get();
            tax.setRate(newRate);
            if (description != null) {
                tax.setDescription(description);
            }
            return taxRepository.save(tax);
        }
        throw new RuntimeException("Taxe non trouvée avec l'ID: " + id);
    }
}





