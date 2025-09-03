package com.ecomptaia.accounting;

import java.util.*;
import java.util.stream.Collectors;

public class SyscohadaChartOfAccounts implements ChartOfAccounts {
    
    private final List<AccountClass> accountClasses;
    private final List<Account> allAccounts;
    
    public SyscohadaChartOfAccounts() {
        this.accountClasses = new ArrayList<>();
        this.allAccounts = new ArrayList<>();
        initializeChartOfAccounts();
    }
    
    @Override
    public AccountingStandard getAccountingStandard() {
        return AccountingStandard.SYSCOHADA;
    }
    
    @Override
    public String getName() {
        return "Plan Comptable SYSCOHADA";
    }
    
    @Override
    public String getDescription() {
        return "Système Comptable OHADA - Plan comptable harmonisé pour les pays de l'OHADA";
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
        metadata.put("standard", "SYSCOHADA");
        metadata.put("version", "2024");
        metadata.put("totalAccounts", allAccounts.size());
        metadata.put("totalClasses", accountClasses.size());
        metadata.put("currencies", Arrays.asList("XOF", "XAF", "CDF"));
        return metadata;
    }
    
    private void initializeChartOfAccounts() {
        // Classe 1 - Comptes de capitaux
        AccountClass classe1 = new AccountClass("1", "Comptes de capitaux", AccountType.EQUITY);
        classe1.addAccount(new Account("10", "Capital et réserves", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("101", "Capital social", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("106", "Réserves", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("12", "Résultats", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("120", "Résultat de l'exercice", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("13", "Subventions d'investissement", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("16", "Emprunts et dettes assimilées", AccountType.LIABILITY, classe1));
        classe1.addAccount(new Account("164", "Emprunts bancaires", AccountType.LIABILITY, classe1));
        accountClasses.add(classe1);
        
        // Classe 2 - Comptes d'immobilisations
        AccountClass classe2 = new AccountClass("2", "Comptes d'immobilisations", AccountType.ASSET);
        classe2.addAccount(new Account("20", "Immobilisations incorporelles", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("201", "Frais d'établissement", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("207", "Fonds commercial", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("21", "Immobilisations corporelles", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("211", "Terrains", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("213", "Constructions", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("215", "Installations techniques", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("28", "Amortissements", AccountType.ASSET, classe2));
        accountClasses.add(classe2);
        
        // Classe 3 - Comptes de stocks
        AccountClass classe3 = new AccountClass("3", "Comptes de stocks", AccountType.ASSET);
        classe3.addAccount(new Account("31", "Matières premières", AccountType.ASSET, classe3));
        classe3.addAccount(new Account("35", "Stocks de produits", AccountType.ASSET, classe3));
        classe3.addAccount(new Account("37", "Stocks de marchandises", AccountType.ASSET, classe3));
        accountClasses.add(classe3);
        
        // Classe 4 - Comptes de tiers
        AccountClass classe4 = new AccountClass("4", "Comptes de tiers", AccountType.ASSET);
        classe4.addAccount(new Account("40", "Fournisseurs", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("401", "Fournisseurs", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("41", "Clients", AccountType.ASSET, classe4));
        classe4.addAccount(new Account("411", "Clients", AccountType.ASSET, classe4));
        classe4.addAccount(new Account("42", "Personnel", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("43", "Sécurité sociale", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("44", "État", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("445", "État - TVA", AccountType.LIABILITY, classe4));
        accountClasses.add(classe4);
        
        // Classe 5 - Comptes financiers
        AccountClass classe5 = new AccountClass("5", "Comptes financiers", AccountType.ASSET);
        classe5.addAccount(new Account("50", "Valeurs mobilières de placement", AccountType.ASSET, classe5));
        classe5.addAccount(new Account("51", "Banques", AccountType.ASSET, classe5));
        classe5.addAccount(new Account("52", "Instruments de trésorerie", AccountType.ASSET, classe5));
        classe5.addAccount(new Account("53", "Caisse", AccountType.ASSET, classe5));
        classe5.addAccount(new Account("531", "Caisse", AccountType.ASSET, classe5));
        accountClasses.add(classe5);
        
        // Classe 6 - Comptes de charges
        AccountClass classe6 = new AccountClass("6", "Comptes de charges", AccountType.EXPENSE);
        classe6.addAccount(new Account("60", "Achats", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("601", "Achats de matières premières", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("602", "Achats d'approvisionnements", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("603", "Achats de fournitures", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("604", "Achats d'études et prestations", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("605", "Achats de marchandises", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("61", "Services extérieurs", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("62", "Autres services extérieurs", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("63", "Impôts et taxes", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("64", "Charges de personnel", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("641", "Rémunérations du personnel", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("645", "Charges de sécurité sociale", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("65", "Autres charges de gestion courante", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("66", "Charges financières", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("67", "Charges exceptionnelles", AccountType.EXPENSE, classe6));
        accountClasses.add(classe6);
        
        // Classe 7 - Comptes de produits
        AccountClass classe7 = new AccountClass("7", "Comptes de produits", AccountType.REVENUE);
        classe7.addAccount(new Account("70", "Ventes", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("701", "Ventes de produits finis", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("702", "Ventes de produits résiduels", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("707", "Ventes de marchandises", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("71", "Production stockée", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("72", "Production immobilisée", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("73", "Subventions d'exploitation", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("74", "Subventions d'équilibre", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("75", "Autres produits de gestion courante", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("76", "Produits financiers", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("77", "Produits exceptionnels", AccountType.REVENUE, classe7));
        accountClasses.add(classe7);
        
        // Ajouter tous les comptes à la liste générale
        accountClasses.forEach(classe -> allAccounts.addAll(classe.getAccounts()));
    }
}






