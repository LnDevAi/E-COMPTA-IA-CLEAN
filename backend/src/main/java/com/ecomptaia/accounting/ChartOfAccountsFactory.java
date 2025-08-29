package com.ecomptaia.accounting;

import org.springframework.stereotype.Component;

@Component
public class ChartOfAccountsFactory {
    
    /**
     * Crée un plan comptable selon le standard spécifié
     */
    public ChartOfAccounts createChartOfAccounts(AccountingStandard standard) {
        switch (standard) {
            case SYSCOHADA:
                return new SyscohadaChartOfAccounts();
            case SYSCOHADA_SMT:
                return new SyscohadaSMTChartOfAccounts();
            case OHADA_SN:
                return new SyscohadaChartOfAccounts();
            case OHADA_SMT:
                return new SyscohadaSMTChartOfAccounts();
            case IFRS:
            case GAAP:
            case PCG_FR:
            case SYSCOA:
                // Pour l'instant, retourner SYSCOHADA par défaut
                // Ces implémentations seront créées plus tard
                return new SyscohadaChartOfAccounts();
            default:
                throw new IllegalArgumentException("Standard comptable non supporté: " + standard);
        }
    }
    
    /**
     * Crée un plan comptable selon le code pays
     */
    public ChartOfAccounts createChartOfAccountsByCountry(String countryCode) {
        switch (countryCode.toUpperCase()) {
            case "SN": // Sénégal
            case "CI": // Côte d'Ivoire
            case "BJ": // Bénin
            case "BF": // Burkina Faso
            case "ML": // Mali
            case "NE": // Niger
            case "TG": // Togo
            case "GW": // Guinée-Bissau
            case "CM": // Cameroun
            case "CF": // République Centrafricaine
            case "CG": // Congo
            case "GA": // Gabon
            case "GQ": // Guinée Équatoriale
            case "TD": // Tchad
            case "CD": // RDC
                return new SyscohadaChartOfAccounts();
            case "FR": // France
            case "DE": // Allemagne
            case "US": // États-Unis
            default:
                // Pour l'instant, tous les autres pays utilisent SYSCOHADA
                // Les implémentations spécifiques seront créées plus tard
                return new SyscohadaChartOfAccounts();
        }
    }
}
