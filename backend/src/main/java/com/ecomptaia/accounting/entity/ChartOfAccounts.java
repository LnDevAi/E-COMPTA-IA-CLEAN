ackage com.ecomptaia.accounting.entity;

import com.ecomptaia.entity.Account;
import com.ecomptaia.accounting.entity.AccountingStandard;

import java.util.List;
import java.util.Map;

public interface ChartOfAccounts {
    
    /**
     * Retourne le standard comptable associé
     */
    AccountingStandard getAccountingStandard();
    
    /**
     * Retourne le nom du plan comptable
     */
    String getName();
    
    /**
     * Retourne la description du plan comptable
     */
    String getDescription();
    
    /**
     * Retourne la liste des classes comptables
     */
    List<AccountClass> getAccountClasses();
    
    /**
     * Retourne un compte par son numéro
     */
    Account getAccountByNumber(String accountNumber);
    
    /**
     * Retourne tous les comptes
     */
    List<Account> getAllAccounts();
    
    /**
     * Retourne les comptes d'une classe donnée
     */
    List<Account> getAccountsByClass(String className);
    
    /**
     * Retourne les métadonnées du plan comptable
     */
    Map<String, Object> getMetadata();
}





