package com.ecomptaia.accounting.entity;

public enum AccountClassEnum {
    CURRENT_ASSETS("Actifs circulants"),
    FIXED_ASSETS("Actifs immobilisés"),
    CURRENT_LIABILITIES("Passifs circulants"),
    LONG_TERM_LIABILITIES("Passifs à long terme"),
    EQUITY("Capitaux propres"),
    REVENUE("Produits"),
    EXPENSES("Charges"),
    COST_OF_GOODS_SOLD("Coût des marchandises vendues");
    
    private final String description;
    
    AccountClassEnum(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
}

