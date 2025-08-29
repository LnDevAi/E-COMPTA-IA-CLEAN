package com.ecomptaia.accounting;

public enum AccountType {
    ASSET("ASSET", "Actif"),
    LIABILITY("LIABILITY", "Passif"),
    EQUITY("EQUITY", "Capitaux propres"),
    REVENUE("REVENUE", "Produits"),
    EXPENSE("EXPENSE", "Charges");
    
    private final String code;
    private final String description;
    
    AccountType(String code, String description) {
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
    
    public static AccountType fromString(String text) {
        for (AccountType type : AccountType.values()) {
            if (type.name().equalsIgnoreCase(text) || 
                type.code.equalsIgnoreCase(text) ||
                type.description.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Type de compte non reconnu: " + text);
    }
}

