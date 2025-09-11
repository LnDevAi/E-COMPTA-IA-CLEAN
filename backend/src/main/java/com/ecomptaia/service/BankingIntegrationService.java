ackage com.ecomptaia.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.entity.BankAccount;

/**
 * Service d'intégration bancaire pour E-COMPTA-IA
 * Gère l'import automatique des relevés bancaires
 */
@Service
public class BankingIntegrationService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private JournalEntryService journalEntryService;
    
    
    // ==================== INTÉGRATIONS BANCAIRES ====================
    
    /**
     * Importer les relevés bancaires depuis une API bancaire
     */
    public Map<String, Object> importBankStatements(String bankCode, String accountNumber, 
                                                   LocalDate startDate, LocalDate endDate, Long companyId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Configuration des headers pour l'API bancaire
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + getBankApiToken(bankCode));
            headers.set("Content-Type", "application/json");
            
            // Construction de l'URL de l'API bancaire
            String apiUrl = buildBankApiUrl(bankCode, accountNumber, startDate, endDate);
            
            // Appel à l'API bancaire
            HttpEntity<String> entity = new HttpEntity<>(headers);
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> bankData = (Map<String, Object>) response.getBody();
                if (bankData != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> transactions = (List<Map<String, Object>>) bankData.get("transactions");
                
                    // Traitement des transactions
                    int importedCount = 0;
                    if (transactions != null) {
                        for (Map<String, Object> transaction : transactions) {
                            if (importBankTransaction(transaction, companyId)) {
                                importedCount++;
                            }
                        }
                    }
                    
                    result.put("success", true);
                    result.put("importedTransactions", importedCount);
                    result.put("totalTransactions", transactions != null ? transactions.size() : 0);
                    result.put("message", "Import bancaire réussi");
                } else {
                    result.put("success", false);
                    result.put("error", "Données bancaires vides");
                }
                
            } else {
                result.put("success", false);
                result.put("error", "Erreur API bancaire: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Erreur lors de l'import: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Importer une transaction bancaire individuelle
     */
    private boolean importBankTransaction(Map<String, Object> transaction, Long companyId) {
        try {
            // Créer l'écriture comptable pour la transaction
            JournalEntry entry = new JournalEntry();
            entry.setEntryNumber("BANK_" + System.currentTimeMillis());
            entry.setEntryDate(LocalDate.parse(transaction.get("date").toString()));
            entry.setDescription(transaction.get("description").toString());
            entry.setJournalType("BANQUE");
            entry.setCurrency(transaction.get("currency").toString());
            entry.setTotalDebit(new BigDecimal(transaction.get("amount").toString()));
            entry.setTotalCredit(new BigDecimal("0"));
            entry.setStatus("BROUILLON");
            entry.setReference(transaction.get("reference").toString());
            entry.setCreatedAt(LocalDateTime.now());
            
            // Sauvegarder l'écriture
            journalEntryService.createJournalEntry(entry, new ArrayList<>());
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Obtenir le token d'API pour une banque
     */
    private String getBankApiToken(String bankCode) {
        // En production, récupérer depuis la base de données
        Map<String, String> bankTokens = Map.of(
            "BNP", "bnp_token_123",
            "SG", "sg_token_456",
            "CA", "ca_token_789"
        );
        return bankTokens.getOrDefault(bankCode, "default_token");
    }
    
    /**
     * Construire l'URL de l'API bancaire
     */
    private String buildBankApiUrl(String bankCode, String accountNumber, 
                                 LocalDate startDate, LocalDate endDate) {
        String baseUrl = getBankApiBaseUrl(bankCode);
        return String.format("%s/accounts/%s/transactions?from=%s&to=%s", 
                           baseUrl, accountNumber, startDate, endDate);
    }
    
    /**
     * Obtenir l'URL de base de l'API bancaire
     */
    private String getBankApiBaseUrl(String bankCode) {
        Map<String, String> bankUrls = Map.of(
            "BNP", "https://api.bnpparibas.com/v1",
            "SG", "https://api.societegenerale.com/v1",
            "CA", "https://api.credit-agricole.com/v1"
        );
        return bankUrls.getOrDefault(bankCode, "https://api.bank.com/v1");
    }
    
    /**
     * Synchroniser les comptes bancaires
     */
    public Map<String, Object> syncBankAccounts(Long companyId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Récupérer les comptes bancaires de l'entreprise
            List<BankAccount> bankAccounts = getCompanyBankAccounts(companyId);
            
            int syncedCount = 0;
            for (BankAccount account : bankAccounts) {
                if (syncBankAccount(account)) {
                    syncedCount++;
                }
            }
            
            result.put("success", true);
            result.put("syncedAccounts", syncedCount);
            result.put("totalAccounts", bankAccounts.size());
            result.put("message", "Synchronisation bancaire réussie");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Erreur lors de la synchronisation: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Synchroniser un compte bancaire individuel
     */
    private boolean syncBankAccount(BankAccount account) {
        try {
            // Importer les 30 derniers jours
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(30);
            
            Map<String, Object> importResult = importBankStatements(
                account.getBankCode(), 
                account.getAccountNumber(), 
                startDate, 
                endDate, 
                account.getCompanyId()
            );
            
            return (Boolean) importResult.get("success");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Récupérer les comptes bancaires d'une entreprise
     */
    private List<BankAccount> getCompanyBankAccounts(Long companyId) {
        // En production, récupérer depuis la base de données
        // Pour l'instant, retourner une liste vide
        return new ArrayList<>();
    }
}