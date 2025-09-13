package com.ecomptaia.service;

import com.ecomptaia.accounting.AccountingStandard;
import com.ecomptaia.accounting.ChartOfAccounts;
import com.ecomptaia.accounting.ChartOfAccountsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service pour la gestion des standards comptables
 * Gère la création et la récupération des plans comptables selon les standards
 */
@Service
public class AccountingStandardService {
    
    @Autowired
    private ChartOfAccountsFactory chartOfAccountsFactory;
    
    /**
     * Récupère un plan comptable selon le standard
     */
    public ChartOfAccounts getChartOfAccounts(AccountingStandard standard) {
        return chartOfAccountsFactory.createChartOfAccounts(standard);
    }
    
    /**
     * Récupère un plan comptable selon le code pays
     */
    public ChartOfAccounts getChartOfAccountsByCountry(String countryCode) {
        return chartOfAccountsFactory.createChartOfAccountsByCountry(countryCode);
    }
    
    /**
     * Récupère tous les standards comptables disponibles
     */
    public List<AccountingStandard> getAllStandards() {
        return Arrays.asList(AccountingStandard.values());
    }
    
    /**
     * Récupère les standards comptables par région
     */
    public Map<String, List<AccountingStandard>> getStandardsByRegion() {
        Map<String, List<AccountingStandard>> standardsByRegion = new HashMap<>();
        
        // Afrique OHADA
        standardsByRegion.put("AFRIQUE_OHADA", Arrays.asList(
            AccountingStandard.SYSCOHADA,
            AccountingStandard.SYSCOHADA_SMT,
            AccountingStandard.OHADA_SN,
            AccountingStandard.OHADA_SMT
        ));
        
        // Europe
        standardsByRegion.put("EUROPE", Arrays.asList(
            AccountingStandard.IFRS,
            AccountingStandard.PCG_FR
        ));
        
        // Amérique du Nord
        standardsByRegion.put("AMERIQUE_NORD", Arrays.asList(
            AccountingStandard.GAAP,
            AccountingStandard.IFRS
        ));
        
        // International
        standardsByRegion.put("INTERNATIONAL", Arrays.asList(
            AccountingStandard.IFRS,
            AccountingStandard.GAAP
        ));
        
        return standardsByRegion;
    }
    
    /**
     * Récupère les métadonnées d'un standard
     */
    public Map<String, Object> getStandardMetadata(AccountingStandard standard) {
        ChartOfAccounts chart = getChartOfAccounts(standard);
        return chart.getMetadata();
    }
    
    /**
     * Vérifie si un standard est supporté
     */
    public boolean isStandardSupported(AccountingStandard standard) {
        try {
            getChartOfAccounts(standard);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Récupère le standard recommandé pour un pays
     */
    public AccountingStandard getRecommendedStandardForCountry(String countryCode) {
        switch (countryCode.toUpperCase()) {
            case "SN": case "CI": case "BJ": case "BF": case "ML": case "NE": case "TG":
            case "GW": case "CM": case "CF": case "CG": case "GA": case "GQ": case "TD": case "CD":
                return AccountingStandard.SYSCOHADA;
            case "FR":
                return AccountingStandard.PCG_FR;
            case "US":
                return AccountingStandard.GAAP;
            case "GB": case "DE": case "CA": case "AU": case "JP": case "SG":
                return AccountingStandard.IFRS;
            default:
                return AccountingStandard.IFRS; // Standard international par défaut
        }
    }
    
    /**
     * Récupère les devises supportées par un standard
     */
    public List<String> getSupportedCurrencies(AccountingStandard standard) {
        Map<String, Object> metadata = getStandardMetadata(standard);
        @SuppressWarnings("unchecked")
        List<String> currencies = (List<String>) metadata.get("currencies");
        return currencies != null ? currencies : Arrays.asList("USD", "EUR");
    }
    
    /**
     * Récupère les régions supportées par un standard
     */
    public List<String> getSupportedRegions(AccountingStandard standard) {
        Map<String, Object> metadata = getStandardMetadata(standard);
        @SuppressWarnings("unchecked")
        List<String> regions = (List<String>) metadata.get("regions");
        return regions != null ? regions : Arrays.asList("Global");
    }
}


