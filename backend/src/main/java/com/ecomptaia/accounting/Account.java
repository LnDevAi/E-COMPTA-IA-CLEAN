package com.ecomptaia.accounting;

import java.math.BigDecimal;

public class Account {
    
    private String accountNumber;
    private String name;
    private String description;
    private AccountType type;
    private AccountClass accountClass;
    private boolean active;
    private BigDecimal openingBalance;
    private String currency;
    
    public Account(String accountNumber, String name, AccountType type, AccountClass accountClass) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.type = type;
        this.accountClass = accountClass;
        this.active = true;
        this.openingBalance = BigDecimal.ZERO;
        this.currency = "XOF";
    }
    
    public Account(String accountNumber, String name, String description, 
                  AccountType type, AccountClass accountClass, boolean active,
                  BigDecimal openingBalance, String currency) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.description = description;
        this.type = type;
        this.accountClass = accountClass;
        this.active = active;
        this.openingBalance = openingBalance;
        this.currency = currency;
    }
    
    // Getters et Setters
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }
    
    public AccountClass getAccountClass() { return accountClass; }
    public void setAccountClass(AccountClass accountClass) { this.accountClass = accountClass; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    @Override
    public String toString() {
        return accountNumber + " - " + name;
    }
}

