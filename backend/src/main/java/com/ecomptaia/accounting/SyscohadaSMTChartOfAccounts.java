package com.ecomptaia.accounting;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Plan comptable SYSCOHADA - Système Minimal de Trésorerie (SMT)
 * Version simplifiée pour les petites entreprises et micro-entreprises
 */
public class SyscohadaSMTChartOfAccounts implements ChartOfAccounts {
    
    private final List<AccountClass> accountClasses;
    private final List<Account> allAccounts;
    
    public SyscohadaSMTChartOfAccounts() {
        this.accountClasses = new ArrayList<>();
        this.allAccounts = new ArrayList<>();
        initializeChartOfAccounts();
    }
    
    @Override
    public AccountingStandard getAccountingStandard() {
        return AccountingStandard.SYSCOHADA_SMT;
    }
    
    @Override
    public String getName() {
        return "Plan Comptable SYSCOHADA - Système Minimal de Trésorerie";
    }
    
    @Override
    public String getDescription() {
        return "Système Comptable OHADA - Système Minimal de Trésorerie pour petites entreprises";
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
        metadata.put("standard", "SYSCOHADA_SMT");
        metadata.put("version", "2024");
        metadata.put("totalAccounts", allAccounts.size());
        metadata.put("totalClasses", accountClasses.size());
        metadata.put("currencies", Arrays.asList("XOF", "XAF", "CDF"));
        metadata.put("systemType", "Système Minimal de Trésorerie");
        metadata.put("targetEntities", "Petites entreprises, micro-entreprises");
        return metadata;
    }
    
    private void initializeChartOfAccounts() {
        // Classe 1 - Comptes de capitaux (simplifiée)
        AccountClass classe1 = new AccountClass("1", "Comptes de capitaux", AccountType.EQUITY);
        classe1.addAccount(new Account("10", "Capital et réserves", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("101", "Capital social", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("106", "Réserves", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("12", "Résultats", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("120", "Résultat de l'exercice", AccountType.EQUITY, classe1));
        classe1.addAccount(new Account("16", "Emprunts et dettes assimilées", AccountType.LIABILITY, classe1));
        classe1.addAccount(new Account("164", "Emprunts bancaires", AccountType.LIABILITY, classe1));
        accountClasses.add(classe1);
        
        // Classe 2 - Comptes d'immobilisations (simplifiée)
        AccountClass classe2 = new AccountClass("2", "Comptes d'immobilisations", AccountType.ASSET);
        classe2.addAccount(new Account("20", "Immobilisations incorporelles", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("201", "Frais d'établissement", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("207", "Fonds commercial", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("22", "Terrains", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("23", "Bâtiments", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("24", "Matériel et mobilier", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("241", "Matériel de bureau", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("245", "Matériel de transport", AccountType.ASSET, classe2));
        classe2.addAccount(new Account("28", "Amortissements", AccountType.ASSET, classe2));
        accountClasses.add(classe2);
        
        // Classe 3 - Comptes de stocks (simplifiée)
        AccountClass classe3 = new AccountClass("3", "Comptes de stocks", AccountType.ASSET);
        classe3.addAccount(new Account("31", "Matières premières", AccountType.ASSET, classe3));
        classe3.addAccount(new Account("35", "Produits finis", AccountType.ASSET, classe3));
        classe3.addAccount(new Account("37", "Stocks de marchandises", AccountType.ASSET, classe3));
        accountClasses.add(classe3);
        
        // Classe 4 - Comptes de tiers (simplifiée)
        AccountClass classe4 = new AccountClass("4", "Comptes de tiers", AccountType.ASSET);
        classe4.addAccount(new Account("40", "Fournisseurs et comptes rattachés", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("401", "Fournisseurs", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("41", "Clients et comptes rattachés", AccountType.ASSET, classe4));
        classe4.addAccount(new Account("411", "Clients", AccountType.ASSET, classe4));
        classe4.addAccount(new Account("42", "Personnel et comptes rattachés", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("421", "Personnel - Rémunérations dues", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("43", "Sécurité sociale et autres organismes sociaux", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("44", "État et autres collectivités publiques", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("445", "État - TVA collectée", AccountType.LIABILITY, classe4));
        classe4.addAccount(new Account("447", "Autres impôts et taxes", AccountType.LIABILITY, classe4));
        accountClasses.add(classe4);
        
        // Classe 5 - Comptes de trésorerie (simplifiée)
        AccountClass classe5 = new AccountClass("5", "Comptes de trésorerie", AccountType.ASSET);
        classe5.addAccount(new Account("51", "Banques", AccountType.ASSET, classe5));
        classe5.addAccount(new Account("52", "Caisse", AccountType.ASSET, classe5));
        classe5.addAccount(new Account("53", "Chèques postaux", AccountType.ASSET, classe5));
        accountClasses.add(classe5);
        
        // Classe 6 - Comptes de charges (simplifiée)
        AccountClass classe6 = new AccountClass("6", "Comptes de charges", AccountType.EXPENSE);
        classe6.addAccount(new Account("60", "Achats", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("601", "Achats de matières premières", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("607", "Achats de marchandises", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("61", "Services extérieurs", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("62", "Autres services extérieurs", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("63", "Impôts et taxes", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("64", "Charges de personnel", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("641", "Rémunérations du personnel", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("65", "Autres charges de gestion courante", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("66", "Charges financières", AccountType.EXPENSE, classe6));
        classe6.addAccount(new Account("67", "Charges exceptionnelles", AccountType.EXPENSE, classe6));
        accountClasses.add(classe6);
        
        // Classe 7 - Comptes de produits (simplifiée)
        AccountClass classe7 = new AccountClass("7", "Comptes de produits", AccountType.REVENUE);
        classe7.addAccount(new Account("70", "Ventes", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("701", "Ventes de produits finis", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("707", "Ventes de marchandises", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("71", "Production stockée", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("72", "Production immobilisée", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("75", "Autres produits de gestion courante", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("76", "Produits financiers", AccountType.REVENUE, classe7));
        classe7.addAccount(new Account("77", "Produits exceptionnels", AccountType.REVENUE, classe7));
        accountClasses.add(classe7);
        
        // Classe 8 - Comptes spéciaux (simplifiée)
        AccountClass classe8 = new AccountClass("8", "Comptes spéciaux", AccountType.ASSET);
        classe8.addAccount(new Account("80", "Comptes spéciaux", AccountType.ASSET, classe8));
        accountClasses.add(classe8);
        
        // Ajouter tous les comptes à la liste générale
        accountClasses.forEach(classe -> allAccounts.addAll(classe.getAccounts()));
    }
}
