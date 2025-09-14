package com.ecomptaia.accounting;

import com.ecomptaia.accounting.entity.ChartOfAccounts;
import com.ecomptaia.accounting.entity.AccountClass;
import com.ecomptaia.accounting.entity.Account;
import com.ecomptaia.accounting.entity.AccountType;
import com.ecomptaia.accounting.entity.AccountingStandard;
import java.util.*;

public class PCGFrenchChartOfAccounts implements ChartOfAccounts {
    private final java.util.List<AccountClass> accountClasses = new java.util.ArrayList<>();
    private final java.util.List<Account> allAccounts = new java.util.ArrayList<>();
    
    @Override
    public AccountingStandard getAccountingStandard() { return AccountingStandard.PCG_FR; }
    @Override
    public String getName() { return "PCG Français"; }
    @Override
    public String getDescription() { return "Plan Comptable Général Français"; }
    @Override
    public java.util.List<AccountClass> getAccountClasses() { return accountClasses; }
    @Override
    public Account getAccountByNumber(String accountNumber) { return null; }
    @Override
    public java.util.List<Account> getAllAccounts() { return allAccounts; }
    @Override
    public java.util.List<Account> getAccountsByClass(String className) { return java.util.Collections.emptyList(); }
    @Override
    public Map<String, Object> getMetadata() { return java.util.Collections.emptyMap(); }
    
    public PCGFrenchChartOfAccounts() {
        AccountClass classe1 = new AccountClass("1", "Capitaux propres", AccountType.EQUITY);
        classe1.addAccount(new Account("1010", "Capital social", AccountType.EQUITY));
        classe1.addAccount(new Account("1020"));
        classe1.addAccount(new Account("1030", "Réserves", AccountType.EQUITY));
        classe1.addAccount(new Account("1040"));
        classe1.addAccount(new Account("1050", "Prime d'émission", AccountType.EQUITY));
        classe1.addAccount(new Account("1060"));
        accountClasses.add(classe1);
        AccountClass classe2 = new AccountClass("2", "Immobilisations", AccountType.ASSET);
        classe2.addAccount(new Account("2010", "Immobilisations incorporelles", AccountType.ASSET));
        classe2.addAccount(new Account("2020"));
        classe2.addAccount(new Account("2030", "Immobilisations corporelles", AccountType.ASSET));
        classe2.addAccount(new Account("2040"));
        classe2.addAccount(new Account("2050", "Immobilisations financières", AccountType.ASSET));
        classe2.addAccount(new Account("2060"));
        accountClasses.add(classe2);
        
        accountClasses.forEach(c -> allAccounts.addAll(c.getAccounts()));
    }
}




