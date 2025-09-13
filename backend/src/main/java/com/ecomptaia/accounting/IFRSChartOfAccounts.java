package com.ecomptaia.accounting;

import com.ecomptaia.accounting.entity.ChartOfAccounts;
import com.ecomptaia.accounting.entity.AccountClass;
import com.ecomptaia.entity.Account;
import com.ecomptaia.accounting.entity.AccountType;
import com.ecomptaia.accounting.entity.AccountingStandard;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Plan Comptable IFRS (International Financial Reporting Standards)
 * Conforme aux normes internationales IFRS
 */
public class IFRSChartOfAccounts implements ChartOfAccounts {
    
    private final List<AccountClass> accountClasses;
    private final List<Account> allAccounts;
    
    public IFRSChartOfAccounts() {
        this.accountClasses = new ArrayList<>();
        this.allAccounts = new ArrayList<>();
        initializeChartOfAccounts();
    }
    
    @Override
    public AccountingStandard getAccountingStandard() {
        return AccountingStandard.IFRS;
    }
    
    @Override
    public String getName() {
        return "IFRS Chart of Accounts";
    }
    
    @Override
    public String getDescription() {
        return "International Financial Reporting Standards - Harmonized chart of accounts for international companies";
    }
    
    @Override
    public List<AccountClass> getAccountClasses() {
        return accountClasses;
    }
    
    @Override
    public Account getAccountByNumber(String accountNumber) {
        return allAccounts.stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }
    
    @Override
    public List<Account> getAllAccounts() {
        return allAccounts;
    }
    
    @Override
    public List<Account> getAccountsByClass(String className) {
        return accountClasses.stream()
                .filter(ac -> ac.getName().equals(className) || ac.getClassNumber().equals(className))
                .flatMap(ac -> ac.getAccounts().stream())
                .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("standard", "IFRS");
        metadata.put("version", "2024");
        metadata.put("totalAccounts", allAccounts.size());
        metadata.put("totalClasses", accountClasses.size());
        metadata.put("currencies", Arrays.asList("USD", "EUR", "GBP", "JPY", "CAD", "AUD"));
        metadata.put("regions", Arrays.asList("Global", "Europe", "Americas", "Asia-Pacific"));
        return metadata;
    }
    
    private void initializeChartOfAccounts() {
        // Classe 1 - Assets (Actifs)
        AccountClass assets = new AccountClass("1", "Assets", AccountType.ASSET);
        assets.addAccount(new Account("1000", "Current Assets", AccountType.ASSET));
        assets.addAccount(new Account("1100"));
        assets.addAccount(new Account("1110", "Cash on Hand", AccountType.ASSET));
        assets.addAccount(new Account("1120"));
        assets.addAccount(new Account("1200", "Trade Receivables", AccountType.ASSET));
        assets.addAccount(new Account("1210"));
        assets.addAccount(new Account("1300", "Inventory", AccountType.ASSET));
        assets.addAccount(new Account("1310"));
        assets.addAccount(new Account("1320", "Work in Progress", AccountType.ASSET));
        assets.addAccount(new Account("1330"));
        assets.addAccount(new Account("1400", "Non-Current Assets", AccountType.ASSET));
        assets.addAccount(new Account("1500"));
        assets.addAccount(new Account("1510", "Land", AccountType.ASSET));
        assets.addAccount(new Account("1520"));
        assets.addAccount(new Account("1530", "Machinery and Equipment", AccountType.ASSET));
        assets.addAccount(new Account("1600"));
        assets.addAccount(new Account("1610", "Goodwill", AccountType.ASSET));
        assets.addAccount(new Account("1620"));
        accountClasses.add(assets);
        
        // Classe 2 - Liabilities (Passifs)
        AccountClass liabilities = new AccountClass("2", "Liabilities", AccountType.LIABILITY);
        liabilities.addAccount(new Account("2000", "Current Liabilities", AccountType.LIABILITY));
        liabilities.addAccount(new Account("2100"));
        liabilities.addAccount(new Account("2110", "Accounts Payable", AccountType.LIABILITY));
        liabilities.addAccount(new Account("2200"));
        liabilities.addAccount(new Account("2300", "Short-term Debt", AccountType.LIABILITY));
        liabilities.addAccount(new Account("2400"));
        liabilities.addAccount(new Account("2500", "Long-term Debt", AccountType.LIABILITY));
        liabilities.addAccount(new Account("2600"));
        accountClasses.add(liabilities);
        
        // Classe 3 - Equity (Capitaux propres)
        AccountClass equity = new AccountClass("3", "Equity", AccountType.EQUITY);
        equity.addAccount(new Account("3000", "Share Capital", AccountType.EQUITY));
        equity.addAccount(new Account("3100"));
        equity.addAccount(new Account("3200", "Preferred Stock", AccountType.EQUITY));
        equity.addAccount(new Account("3300"));
        equity.addAccount(new Account("3400", "Other Comprehensive Income", AccountType.EQUITY));
        equity.addAccount(new Account("3500"));
        accountClasses.add(equity);
        
        // Classe 4 - Revenue (Produits)
        AccountClass revenue = new AccountClass("4", "Revenue", AccountType.REVENUE);
        revenue.addAccount(new Account("4000", "Sales Revenue", AccountType.REVENUE));
        revenue.addAccount(new Account("4100"));
        revenue.addAccount(new Account("4200", "Service Revenue", AccountType.REVENUE));
        revenue.addAccount(new Account("4300"));
        revenue.addAccount(new Account("4400", "Interest Income", AccountType.REVENUE));
        revenue.addAccount(new Account("4500"));
        accountClasses.add(revenue);
        
        // Classe 5 - Expenses (Charges)
        AccountClass expenses = new AccountClass("5", "Expenses", AccountType.EXPENSE);
        expenses.addAccount(new Account("5000", "Cost of Goods Sold", AccountType.EXPENSE));
        expenses.addAccount(new Account("5100"));
        expenses.addAccount(new Account("5200", "Direct Labor", AccountType.EXPENSE));
        expenses.addAccount(new Account("5300"));
        expenses.addAccount(new Account("5400", "Operating Expenses", AccountType.EXPENSE));
        expenses.addAccount(new Account("5410"));
        expenses.addAccount(new Account("5420", "Administrative Expenses", AccountType.EXPENSE));
        expenses.addAccount(new Account("5500"));
        expenses.addAccount(new Account("5600", "Interest Expense", AccountType.EXPENSE));
        expenses.addAccount(new Account("5700"));
        accountClasses.add(expenses);
        
        // Ajouter tous les comptes à la liste générale
        accountClasses.forEach(classe -> allAccounts.addAll(classe.getAccounts()));
    }
}






