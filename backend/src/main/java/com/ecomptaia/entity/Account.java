package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "account_class", nullable = false)
    private AccountClass accountClass;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "opening_balance", precision = 15, scale = 2)
    private BigDecimal openingBalance = BigDecimal.ZERO;
    
    @Column(name = "currency", length = 3)
    private String currency = "XOF";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public Account() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.openingBalance = BigDecimal.ZERO;
        this.currency = "XOF";
        this.name = "";
        this.type = AccountType.ASSET;
        this.accountClass = AccountClass.CURRENT_ASSETS;
        this.accountNumber = "";
    }
    
    public Account(String accountNumber) {
        this();
        this.accountNumber = accountNumber;
        this.name = accountNumber;
    }
    
    public Account(String accountNumber, String name, com.ecomptaia.accounting.entity.AccountType accountingType) {
        this();
        this.accountNumber = accountNumber;
        this.name = name;
        // Map accounting entity type to local enum best-effort
        switch (accountingType) {
            case ASSET -> this.type = AccountType.ASSET;
            case LIABILITY -> this.type = AccountType.LIABILITY;
            case EQUITY -> this.type = AccountType.EQUITY;
            case REVENUE -> this.type = AccountType.REVENUE;
            case EXPENSE -> this.type = AccountType.EXPENSE;
            default -> this.type = AccountType.ASSET;
        }
        // Infer class from first digit when possible
        if (accountNumber != null && !accountNumber.isEmpty()) {
            char c = accountNumber.charAt(0);
            this.accountClass = switch (c) {
                case '1' -> AccountClass.FIXED_ASSETS;
                case '2' -> AccountClass.CURRENT_ASSETS;
                case '3' -> AccountClass.CURRENT_ASSETS;
                case '4' -> AccountClass.CURRENT_LIABILITIES;
                case '5' -> AccountClass.LONG_TERM_LIABILITIES;
                case '6' -> AccountClass.EXPENSES;
                case '7' -> AccountClass.REVENUE;
                default -> AccountClass.CURRENT_ASSETS;
            };
        } else {
            this.accountClass = AccountClass.CURRENT_ASSETS;
        }
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Enums
    public enum AccountType { ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE }
    public enum AccountClass {
        CURRENT_ASSETS, FIXED_ASSETS, CURRENT_LIABILITIES, LONG_TERM_LIABILITIES,
        EQUITY, REVENUE, EXPENSES, COST_OF_GOODS_SOLD
    }
    
    @PreUpdate
    public void preUpdate() { this.updatedAt = LocalDateTime.now(); }
    
    @Override
    public String toString() { return accountNumber + " - " + name; }
}




