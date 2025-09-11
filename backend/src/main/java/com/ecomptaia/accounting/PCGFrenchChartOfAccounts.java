ackage com.ecomptaia.accounting;

import com.ecomptaia.accounting.entity.ChartOfAccounts;
import com.ecomptaia.accounting.entity.AccountClass;
import com.ecomptaia.entity.Account;
import com.ecomptaia.accounting.entity.AccountType;
import com.ecomptaia.accounting.entity.AccountingStandard;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Plan Comptable GÃ©nÃ©ral FranÃ§ais (PCG)
 * Conforme au Plan Comptable GÃ©nÃ©ral franÃ§ais
 */
public class PCGFrenchChartOfAccounts implements ChartOfAccounts {
    
    private final List<AccountClass> accountClasses;
    private final List<Account> allAccounts;
    
    public PCGFrenchChartOfAccounts() {
        this.accountClasses = new ArrayList<>();
        this.allAccounts = new ArrayList<>();
        initializeChartOfAccounts();
    }
    
    @Override
    public AccountingStandard getAccountingStandard() {
        return AccountingStandard.PCG_FR;
    }
    
    @Override
    public String getName() {
        return "Plan Comptable GÃ©nÃ©ral FranÃ§ais";
    }
    
    @Override
    public String getDescription() {
        return "Plan Comptable GÃ©nÃ©ral franÃ§ais - SystÃ¨me comptable franÃ§ais conforme aux normes franÃ§aises";
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
        metadata.put("standard", "PCG_FR");
        metadata.put("version", "2024");
        metadata.put("totalAccounts", allAccounts.size());
        metadata.put("totalClasses", accountClasses.size());
        metadata.put("currencies", Arrays.asList("EUR"));
        metadata.put("regions", Arrays.asList("France", "Europe"));
        metadata.put("regulatory", Arrays.asList("AMF", "H3C"));
        return metadata;
    }
    
    private void initializeChartOfAccounts() {
        // Classe 1 - Comptes de capitaux
        AccountClass classe1 = new AccountClass("1", "Comptes de capitaux", AccountType.EQUITY);
        classe1.addAccount(new Account("10", "Capital et rÃ©serves", AccountType.EQUITY));
        classe1.addAccount(new Account("101"));
        classe1.addAccount(new Account("106", "RÃ©serves", AccountType.EQUITY));
        classe1.addAccount(new Account("1061"));
        classe1.addAccount(new Account("1068", "Autres rÃ©serves", AccountType.EQUITY));
        classe1.addAccount(new Account("12"));
        classe1.addAccount(new Account("120", "RÃ©sultat de l'exercice", AccountType.EQUITY));
        classe1.addAccount(new Account("13"));
        classe1.addAccount(new Account("16", "Emprunts et dettes assimilÃ©es", AccountType.LIABILITY));
        classe1.addAccount(new Account("164"));
        classe1.addAccount(new Account("165", "Emprunts obligataires", AccountType.LIABILITY));
        accountClasses.add(classe1);
        
        // Classe 2 - Comptes d'immobilisations
        AccountClass classe2 = new AccountClass("2");
        classe2.addAccount(new Account("20", "Immobilisations incorporelles", AccountType.ASSET));
        classe2.addAccount(new Account("201"));
        classe2.addAccount(new Account("207", "Fonds commercial", AccountType.ASSET));
        classe2.addAccount(new Account("208"));
        classe2.addAccount(new Account("21", "Immobilisations corporelles", AccountType.ASSET));
        classe2.addAccount(new Account("211"));
        classe2.addAccount(new Account("213", "Constructions", AccountType.ASSET));
        classe2.addAccount(new Account("215"));
        classe2.addAccount(new Account("218", "Autres immobilisations corporelles", AccountType.ASSET));
        classe2.addAccount(new Account("28"));
        classe2.addAccount(new Account("281", "Amortissements des immobilisations incorporelles", AccountType.ASSET));
        classe2.addAccount(new Account("283"));
        accountClasses.add(classe2);
        
        // Classe 3 - Comptes de stocks
        AccountClass classe3 = new AccountClass("3", "Comptes de stocks", AccountType.ASSET);
        classe3.addAccount(new Account("31", "MatiÃ¨res premiÃ¨res", AccountType.ASSET));
        classe3.addAccount(new Account("311"));
        classe3.addAccount(new Account("312", "MatiÃ¨res premiÃ¨res B", AccountType.ASSET));
        classe3.addAccount(new Account("35"));
        classe3.addAccount(new Account("351", "Produits en cours", AccountType.ASSET));
        classe3.addAccount(new Account("355"));
        classe3.addAccount(new Account("37", "Stocks de marchandises", AccountType.ASSET));
        classe3.addAccount(new Account("371"));
        accountClasses.add(classe3);
        
        // Classe 4 - Comptes de tiers
        AccountClass classe4 = new AccountClass("4", "Comptes de tiers", AccountType.ASSET);
        classe4.addAccount(new Account("40", "Fournisseurs", AccountType.LIABILITY));
        classe4.addAccount(new Account("401"));
        classe4.addAccount(new Account("41", "Clients", AccountType.ASSET));
        classe4.addAccount(new Account("411"));
        classe4.addAccount(new Account("42", "Personnel", AccountType.LIABILITY));
        classe4.addAccount(new Account("421"));
        classe4.addAccount(new Account("43", "SÃ©curitÃ© sociale", AccountType.LIABILITY));
        classe4.addAccount(new Account("431"));
        classe4.addAccount(new Account("44", "Ã‰tat", AccountType.LIABILITY));
        classe4.addAccount(new Account("445"));
        classe4.addAccount(new Account("4455", "TVA Ã  dÃ©caisser", AccountType.LIABILITY));
        classe4.addAccount(new Account("4456"));
        accountClasses.add(classe4);
        
        // Classe 5 - Comptes financiers
        AccountClass classe5 = new AccountClass("5", "Comptes financiers", AccountType.ASSET);
        classe5.addAccount(new Account("50", "Valeurs mobiliÃ¨res de placement", AccountType.ASSET));
        classe5.addAccount(new Account("51"));
        classe5.addAccount(new Account("512", "Banques", AccountType.ASSET));
        classe5.addAccount(new Account("52"));
        classe5.addAccount(new Account("53", "Caisse", AccountType.ASSET));
        classe5.addAccount(new Account("531"));
        accountClasses.add(classe5);
        
        // Classe 6 - Comptes de charges
        AccountClass classe6 = new AccountClass("6", "Comptes de charges", AccountType.EXPENSE);
        classe6.addAccount(new Account("60", "Achats", AccountType.EXPENSE));
        classe6.addAccount(new Account("601"));
        classe6.addAccount(new Account("602", "Achats d'approvisionnements", AccountType.EXPENSE));
        classe6.addAccount(new Account("603"));
        classe6.addAccount(new Account("604", "Achats d'Ã©tudes et prestations", AccountType.EXPENSE));
        classe6.addAccount(new Account("605"));
        classe6.addAccount(new Account("61", "Services extÃ©rieurs", AccountType.EXPENSE));
        classe6.addAccount(new Account("611"));
        classe6.addAccount(new Account("62", "Autres services extÃ©rieurs", AccountType.EXPENSE));
        classe6.addAccount(new Account("621"));
        classe6.addAccount(new Account("63", "ImpÃ´ts et taxes", AccountType.EXPENSE));
        classe6.addAccount(new Account("631"));
        classe6.addAccount(new Account("64", "Charges de personnel", AccountType.EXPENSE));
        classe6.addAccount(new Account("641"));
        classe6.addAccount(new Account("645", "Charges de sÃ©curitÃ© sociale", AccountType.EXPENSE));
        classe6.addAccount(new Account("65"));
        classe6.addAccount(new Account("66", "Charges financiÃ¨res", AccountType.EXPENSE));
        classe6.addAccount(new Account("661"));
        classe6.addAccount(new Account("67", "Charges exceptionnelles", AccountType.EXPENSE));
        accountClasses.add(classe6);
        
        // Classe 7 - Comptes de produits
        AccountClass classe7 = new AccountClass("7");
        classe7.addAccount(new Account("70", "Ventes", AccountType.REVENUE));
        classe7.addAccount(new Account("701"));
        classe7.addAccount(new Account("702", "Ventes de produits rÃ©siduels", AccountType.REVENUE));
        classe7.addAccount(new Account("707"));
        classe7.addAccount(new Account("71", "Production stockÃ©e", AccountType.REVENUE));
        classe7.addAccount(new Account("72"));
        classe7.addAccount(new Account("73", "Subventions d'exploitation", AccountType.REVENUE));
        classe7.addAccount(new Account("74"));
        classe7.addAccount(new Account("75", "Autres produits de gestion courante", AccountType.REVENUE));
        classe7.addAccount(new Account("76"));
        classe7.addAccount(new Account("761", "Produits de participations", AccountType.REVENUE));
        classe7.addAccount(new Account("77"));
        accountClasses.add(classe7);
        
        // Ajouter tous les comptes Ã  la liste gÃ©nÃ©rale
        accountClasses.forEach(classe -> allAccounts.addAll(classe.getAccounts()));
    }
}




