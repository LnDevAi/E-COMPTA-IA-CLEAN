package com.ecomptaia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Service pour la validation des données et la cohérence de la base de données
 */
@Service
@Transactional
public class DataValidationService {
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * Valide l'intégrité référentielle de la base de données
     */
    public Map<String, Object> validateReferentialIntegrity() {
        Map<String, Object> result = new HashMap<>();
        List<String> violations = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // Vérifier les clés étrangères orphelines
            violations.addAll(checkOrphanedForeignKeys(connection));
            
            // Vérifier les contraintes de données
            violations.addAll(checkDataConstraints(connection));
            
            // Vérifier les données manquantes
            violations.addAll(checkMissingData(connection));
            
            result.put("success", violations.isEmpty());
            result.put("violations", violations);
            result.put("totalViolations", violations.size());
            
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Vérifie les clés étrangères orphelines
     */
    private List<String> checkOrphanedForeignKeys(Connection connection) throws SQLException {
        List<String> violations = new ArrayList<>();
        
        // Vérifier les utilisateurs orphelins (company_id)
        String sql = "SELECT u.id, u.company_id FROM users u LEFT JOIN companies c ON u.company_id = c.id WHERE c.id IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Utilisateur orphelin: ID " + rs.getLong("id") + " référence company_id " + rs.getLong("company_id") + " inexistant");
            }
        }
        
        // Vérifier les écritures comptables orphelines
        sql = "SELECT je.id, je.company_id FROM journal_entries je LEFT JOIN companies c ON je.company_id = c.id WHERE c.id IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Écriture comptable orpheline: ID " + rs.getLong("id") + " référence company_id " + rs.getLong("company_id") + " inexistant");
            }
        }
        
        // Vérifier les lignes d'écriture orphelines
        sql = "SELECT ae.id, ae.journal_entry_id FROM account_entries ae LEFT JOIN journal_entries je ON ae.journal_entry_id = je.id WHERE je.id IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Ligne d'écriture orpheline: ID " + rs.getLong("id") + " référence journal_entry_id " + rs.getLong("journal_entry_id") + " inexistant");
            }
        }
        
        // Vérifier les employés orphelins
        sql = "SELECT e.id, e.company_id FROM employees e LEFT JOIN companies c ON e.company_id = c.id WHERE c.id IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Employé orphelin: ID " + rs.getLong("id") + " référence company_id " + rs.getLong("company_id") + " inexistant");
            }
        }
        
        return violations;
    }
    
    /**
     * Vérifie les contraintes de données
     */
    private List<String> checkDataConstraints(Connection connection) throws SQLException {
        List<String> violations = new ArrayList<>();
        
        // Vérifier les montants négatifs dans les écritures comptables
        String sql = "SELECT id, debit_amount, credit_amount FROM account_entries WHERE debit_amount < 0 OR credit_amount < 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Montant négatif dans account_entries ID " + rs.getLong("id") + 
                             " (débit: " + rs.getBigDecimal("debit_amount") + 
                             ", crédit: " + rs.getBigDecimal("credit_amount") + ")");
            }
        }
        
        // Vérifier les dates incohérentes
        sql = "SELECT id, created_at, updated_at FROM journal_entries WHERE updated_at < created_at";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Date incohérente dans journal_entries ID " + rs.getLong("id") + 
                             " (updated_at < created_at)");
            }
        }
        
        // Vérifier les emails invalides
        sql = "SELECT id, email FROM users WHERE email NOT LIKE '%@%' OR email = ''";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Email invalide dans users ID " + rs.getLong("id") + 
                             " (email: " + rs.getString("email") + ")");
            }
        }
        
        return violations;
    }
    
    /**
     * Vérifie les données manquantes
     */
    private List<String> checkMissingData(Connection connection) throws SQLException {
        List<String> violations = new ArrayList<>();
        
        // Vérifier les utilisateurs sans nom
        String sql = "SELECT id FROM users WHERE first_name IS NULL OR first_name = '' OR last_name IS NULL OR last_name = ''";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Utilisateur sans nom complet: ID " + rs.getLong("id"));
            }
        }
        
        // Vérifier les entreprises sans nom
        sql = "SELECT id FROM companies WHERE name IS NULL OR name = ''";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Entreprise sans nom: ID " + rs.getLong("id"));
            }
        }
        
        // Vérifier les comptes sans numéro
        sql = "SELECT id FROM accounts WHERE account_number IS NULL OR account_number = ''";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                violations.add("Compte sans numéro: ID " + rs.getLong("id"));
            }
        }
        
        return violations;
    }
    
    /**
     * Valide la cohérence des écritures comptables
     */
    public Map<String, Object> validateAccountingEntries() {
        Map<String, Object> result = new HashMap<>();
        List<String> issues = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // Vérifier l'équilibre des écritures (débit = crédit)
            String sql = "SELECT je.id, je.reference, " +
                        "SUM(ae.debit_amount) as total_debit, " +
                        "SUM(ae.credit_amount) as total_credit " +
                        "FROM journal_entries je " +
                        "LEFT JOIN account_entries ae ON je.id = ae.journal_entry_id " +
                        "GROUP BY je.id, je.reference " +
                        "HAVING SUM(ae.debit_amount) != SUM(ae.credit_amount)";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    issues.add("Écriture déséquilibrée ID " + rs.getLong("id") + 
                             " (référence: " + rs.getString("reference") + 
                             ", débit: " + rs.getBigDecimal("total_debit") + 
                             ", crédit: " + rs.getBigDecimal("total_credit") + ")");
                }
            }
            
            // Vérifier les écritures sans lignes
            sql = "SELECT je.id, je.reference FROM journal_entries je " +
                  "LEFT JOIN account_entries ae ON je.id = ae.journal_entry_id " +
                  "WHERE ae.id IS NULL";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    issues.add("Écriture sans lignes ID " + rs.getLong("id") + 
                             " (référence: " + rs.getString("reference") + ")");
                }
            }
            
            result.put("success", issues.isEmpty());
            result.put("issues", issues);
            result.put("totalIssues", issues.size());
            
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Valide la cohérence des données multi-tenant
     */
    public Map<String, Object> validateMultiTenantData() {
        Map<String, Object> result = new HashMap<>();
        List<String> issues = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // Vérifier que chaque utilisateur appartient à une entreprise valide
            String sql = "SELECT u.id, u.email, u.company_id FROM users u " +
                        "LEFT JOIN companies c ON u.company_id = c.id " +
                        "WHERE c.id IS NULL OR c.is_active = false";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    issues.add("Utilisateur sans entreprise active: ID " + rs.getLong("id") + 
                             " (email: " + rs.getString("email") + 
                             ", company_id: " + rs.getLong("company_id") + ")");
                }
            }
            
            // Vérifier que chaque écriture appartient à une entreprise valide
            sql = "SELECT je.id, je.reference, je.company_id FROM journal_entries je " +
                  "LEFT JOIN companies c ON je.company_id = c.id " +
                  "WHERE c.id IS NULL OR c.is_active = false";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    issues.add("Écriture sans entreprise active: ID " + rs.getLong("id") + 
                             " (référence: " + rs.getString("reference") + 
                             ", company_id: " + rs.getLong("company_id") + ")");
                }
            }
            
            result.put("success", issues.isEmpty());
            result.put("issues", issues);
            result.put("totalIssues", issues.size());
            
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Génère un rapport de validation complet
     */
    public Map<String, Object> generateValidationReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Validation de l'intégrité référentielle
        Map<String, Object> referentialIntegrity = validateReferentialIntegrity();
        report.put("referentialIntegrity", referentialIntegrity);
        
        // Validation des écritures comptables
        Map<String, Object> accountingEntries = validateAccountingEntries();
        report.put("accountingEntries", accountingEntries);
        
        // Validation des données multi-tenant
        Map<String, Object> multiTenantData = validateMultiTenantData();
        report.put("multiTenantData", multiTenantData);
        
        // Résumé global
        boolean overallSuccess = (Boolean) referentialIntegrity.get("success") &&
                               (Boolean) accountingEntries.get("success") &&
                               (Boolean) multiTenantData.get("success");
        
        int totalIssues = (Integer) referentialIntegrity.get("totalViolations") +
                         (Integer) accountingEntries.get("totalIssues") +
                         (Integer) multiTenantData.get("totalIssues");
        
        report.put("overallSuccess", overallSuccess);
        report.put("totalIssues", totalIssues);
        report.put("generatedAt", new Date());
        
        return report;
    }
    
    /**
     * Nettoie les données orphelines
     */
    public Map<String, Object> cleanupOrphanedData() {
        Map<String, Object> result = new HashMap<>();
        List<String> cleaned = new ArrayList<>();
        int totalCleaned = 0;
        
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            
            try {
                // Nettoyer les lignes d'écriture orphelines
                String sql = "DELETE FROM account_entries WHERE journal_entry_id NOT IN (SELECT id FROM journal_entries)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    int count = stmt.executeUpdate();
                    if (count > 0) {
                        cleaned.add("Supprimé " + count + " lignes d'écriture orphelines");
                        totalCleaned += count;
                    }
                }
                
                // Nettoyer les approbations de documents orphelines
                sql = "DELETE FROM document_approvals WHERE document_id NOT IN (SELECT id FROM ged_documents)";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    int count = stmt.executeUpdate();
                    if (count > 0) {
                        cleaned.add("Supprimé " + count + " approbations de documents orphelines");
                        totalCleaned += count;
                    }
                }
                
                connection.commit();
                result.put("success", true);
                result.put("cleaned", cleaned);
                result.put("totalCleaned", totalCleaned);
                
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
            
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}



