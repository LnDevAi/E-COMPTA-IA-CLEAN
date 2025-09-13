package com.ecomptaia.accounting.entity;

public enum AccountingStandard {
    SYSCOHADA("SYSCOHADA", "Système Comptable OHADA - Système Normal"),
    SYSCOHADA_SMT("SYSCOHADA_SMT", "Système Comptable OHADA - Système Minimal de Trésorerie"),
    OHADA_SN("OHADA_SN", "Système Normal OHADA"),
    OHADA_SMT("OHADA_SMT", "Système Minimal de Trésorerie OHADA"),
    IFRS("IFRS", "International Financial Reporting Standards"),
    GAAP("GAAP", "Generally Accepted Accounting Principles"),
    PCG_FR("PCG_FR", "Plan Comptable Général Français"),
    SYSCOA("SYSCOA", "Système Comptable Ouest Africain");
    
    private final String code;
    private final String description;
    
    AccountingStandard(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return code + " - " + description;
    }
    
    public static AccountingStandard fromString(String text) {
        for (AccountingStandard standard : AccountingStandard.values()) {
            if (standard.name().equalsIgnoreCase(text) || 
                standard.code.equalsIgnoreCase(text) ||
                standard.description.equalsIgnoreCase(text)) {
                return standard;
            }
        }
        throw new IllegalArgumentException("Standard comptable non reconnu: " + text);
    }
}




