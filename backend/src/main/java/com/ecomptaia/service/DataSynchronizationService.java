ackage com.ecomptaia.service;

import com.ecomptaia.security.entity.Company;
import com.ecomptaia.security.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service pour la synchronisation des données entre les tables
 */
@Service
@Transactional
public class DataSynchronizationService {
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * Synchronise les données de base entre les tables principales
     */
    public Map<String, Object> synchronizeBaseData() {
        Map<String, Object> result = new HashMap<>();
        List<String> operations = new ArrayList<>();
        int totalOperations = 0;
        
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            
            try {
                // Synchroniser les rôles par défaut
                int rolesSynced = synchronizeDefaultRoles(connection);
                if (rolesSynced > 0) {
                    operations.add("Synchronisé " + rolesSynced + " rôles par défaut");
                    totalOperations += rolesSynced;
                }
                
                // Synchroniser les pays par défaut
                int countriesSynced = synchronizeDefaultCountries(connection);
                if (countriesSynced > 0) {
                    operations.add("Synchronisé " + countriesSynced + " pays par défaut");
                    totalOperations += countriesSynced;
                }
                
                // Synchroniser les devises par défaut
                int currenciesSynced = synchronizeDefaultCurrencies(connection);
                if (currenciesSynced > 0) {
                    operations.add("Synchronisé " + currenciesSynced + " devises par défaut");
                    totalOperations += currenciesSynced;
                }
                
                // Synchroniser les plans d'abonnement par défaut
                int plansSynced = synchronizeDefaultSubscriptionPlans(connection);
                if (plansSynced > 0) {
                    operations.add("Synchronisé " + plansSynced + " plans d'abonnement par défaut");
                    totalOperations += plansSynced;
                }
                
                connection.commit();
                result.put("success", true);
                result.put("operations", operations);
                result.put("totalOperations", totalOperations);
                
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
    
    /**
     * Synchronise les rôles par défaut
     */
    private int synchronizeDefaultRoles(Connection connection) throws SQLException {
        int synced = 0;
        
        List<Map<String, Object>> defaultRoles = Arrays.asList(
            Map.of("name", "ADMIN", "description", "Administrateur système", "is_system_role", true),
            Map.of("name", "ACCOUNTANT", "description", "Comptable", "is_system_role", true),
            Map.of("name", "MANAGER", "description", "Gestionnaire", "is_system_role", true),
            Map.of("name", "USER", "description", "Utilisateur standard", "is_system_role", true),
            Map.of("name", "VIEWER", "description", "Lecteur seul", "is_system_role", true)
        );
        
        String sql = "INSERT INTO roles (name, description, is_system_role, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, true, ?, ?) " +
                    "ON CONFLICT (name) DO NOTHING";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();
            
            for (Map<String, Object> role : defaultRoles) {
                stmt.setString(1, (String) role.get("name"));
                stmt.setString(2, (String) role.get("description"));
                stmt.setBoolean(3, (Boolean) role.get("is_system_role"));
                stmt.setObject(4, now);
                stmt.setObject(5, now);
                
                if (stmt.executeUpdate() > 0) {
                    synced++;
                }
            }
        }
        
        return synced;
    }
    
    /**
     * Synchronise les pays par défaut
     */
    private int synchronizeDefaultCountries(Connection connection) throws SQLException {
        int synced = 0;
        
        List<Map<String, Object>> defaultCountries = Arrays.asList(
            Map.of("code", "FR", "name", "France", "currency", "EUR"),
            Map.of("code", "US", "name", "États-Unis", "currency", "USD"),
            Map.of("code", "GB", "name", "Royaume-Uni", "currency", "GBP"),
            Map.of("code", "DE", "name", "Allemagne", "currency", "EUR"),
            Map.of("code", "IT", "name", "Italie", "currency", "EUR"),
            Map.of("code", "ES", "name", "Espagne", "currency", "EUR"),
            Map.of("code", "CA", "name", "Canada", "currency", "CAD"),
            Map.of("code", "AU", "name", "Australie", "currency", "AUD"),
            Map.of("code", "JP", "name", "Japon", "currency", "JPY"),
            Map.of("code", "CN", "name", "Chine", "currency", "CNY")
        );
        
        String sql = "INSERT INTO countries (code, name, currency, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, true, ?, ?) " +
                    "ON CONFLICT (code) DO NOTHING";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();
            
            for (Map<String, Object> country : defaultCountries) {
                stmt.setString(1, (String) country.get("code"));
                stmt.setString(2, (String) country.get("name"));
                stmt.setString(3, (String) country.get("currency"));
                stmt.setObject(4, now);
                stmt.setObject(5, now);
                
                if (stmt.executeUpdate() > 0) {
                    synced++;
                }
            }
        }
        
        return synced;
    }
    
    /**
     * Synchronise les devises par défaut
     */
    private int synchronizeDefaultCurrencies(Connection connection) throws SQLException {
        int synced = 0;
        
        List<Map<String, Object>> defaultCurrencies = Arrays.asList(
            Map.of("code", "EUR", "name", "Euro", "symbol", "€", "decimal_places", 2),
            Map.of("code", "USD", "name", "Dollar américain", "symbol", "$", "decimal_places", 2),
            Map.of("code", "GBP", "name", "Livre sterling", "symbol", "£", "decimal_places", 2),
            Map.of("code", "CAD", "name", "Dollar canadien", "symbol", "C$", "decimal_places", 2),
            Map.of("code", "AUD", "name", "Dollar australien", "symbol", "A$", "decimal_places", 2),
            Map.of("code", "JPY", "name", "Yen japonais", "symbol", "¥", "decimal_places", 0),
            Map.of("code", "CNY", "name", "Yuan chinois", "symbol", "¥", "decimal_places", 2),
            Map.of("code", "CHF", "name", "Franc suisse", "symbol", "CHF", "decimal_places", 2),
            Map.of("code", "SEK", "name", "Couronne suédoise", "symbol", "kr", "decimal_places", 2),
            Map.of("code", "NOK", "name", "Couronne norvégienne", "symbol", "kr", "decimal_places", 2)
        );
        
        String sql = "INSERT INTO currencies (code, name, symbol, decimal_places, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, true, ?, ?) " +
                    "ON CONFLICT (code) DO NOTHING";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();
            
            for (Map<String, Object> currency : defaultCurrencies) {
                stmt.setString(1, (String) currency.get("code"));
                stmt.setString(2, (String) currency.get("name"));
                stmt.setString(3, (String) currency.get("symbol"));
                stmt.setInt(4, (Integer) currency.get("decimal_places"));
                stmt.setObject(5, now);
                stmt.setObject(6, now);
                
                if (stmt.executeUpdate() > 0) {
                    synced++;
                }
            }
        }
        
        return synced;
    }
    
    /**
     * Synchronise les plans d'abonnement par défaut
     */
    private int synchronizeDefaultSubscriptionPlans(Connection connection) throws SQLException {
        int synced = 0;
        
        List<Map<String, Object>> defaultPlans = Arrays.asList(
            Map.of("name", "BASIC", "description", "Plan de base", "price", 29.99, "max_users", 5, "max_companies", 1),
            Map.of("name", "PROFESSIONAL", "description", "Plan professionnel", "price", 79.99, "max_users", 25, "max_companies", 3),
            Map.of("name", "ENTERPRISE", "description", "Plan entreprise", "price", 199.99, "max_users", 100, "max_companies", 10),
            Map.of("name", "UNLIMITED", "description", "Plan illimité", "price", 499.99, "max_users", -1, "max_companies", -1)
        );
        
        String sql = "INSERT INTO subscription_plans (name, description, price, max_users, max_companies, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, true, ?, ?) " +
                    "ON CONFLICT (name) DO NOTHING";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();
            
            for (Map<String, Object> plan : defaultPlans) {
                stmt.setString(1, (String) plan.get("name"));
                stmt.setString(2, (String) plan.get("description"));
                stmt.setDouble(3, (Double) plan.get("price"));
                stmt.setInt(4, (Integer) plan.get("max_users"));
                stmt.setInt(5, (Integer) plan.get("max_companies"));
                stmt.setObject(6, now);
                stmt.setObject(7, now);
                
                if (stmt.executeUpdate() > 0) {
                    synced++;
                }
            }
        }
        
        return synced;
    }
    
    /**
     * Synchronise les données de configuration par entreprise
     */
    public Map<String, Object> synchronizeCompanyConfigurations() {
        Map<String, Object> result = new HashMap<>();
        List<String> operations = new ArrayList<>();
        int totalOperations = 0;
        
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            
            try {
                // Synchroniser les paramètres de localisation par défaut
                int localeSettingsSynced = synchronizeDefaultLocaleSettings(connection);
                if (localeSettingsSynced > 0) {
                    operations.add("Synchronisé " + localeSettingsSynced + " paramètres de localisation par défaut");
                    totalOperations += localeSettingsSynced;
                }
                
                // Synchroniser les configurations de protection des données
                int dataProtectionSynced = synchronizeDefaultDataProtection(connection);
                if (dataProtectionSynced > 0) {
                    operations.add("Synchronisé " + dataProtectionSynced + " configurations de protection des données");
                    totalOperations += dataProtectionSynced;
                }
                
                connection.commit();
                result.put("success", true);
                result.put("operations", operations);
                result.put("totalOperations", totalOperations);
                
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
    
    /**
     * Synchronise les paramètres de localisation par défaut
     */
    private int synchronizeDefaultLocaleSettings(Connection connection) throws SQLException {
        int synced = 0;
        
        // Récupérer toutes les entreprises sans paramètres de localisation
        String sql = "SELECT c.id, c.country_code FROM companies c " +
                    "LEFT JOIN locale_settings ls ON c.id = ls.company_id " +
                    "WHERE ls.id IS NULL";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            String insertSql = "INSERT INTO locale_settings (company_id, language, country, currency, timezone, date_format, number_format, is_default, created_at, updated_at) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, true, ?, ?)";
            
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                LocalDateTime now = LocalDateTime.now();
                
                while (rs.next()) {
                    Long companyId = rs.getLong("id");
                    String countryCode = rs.getString("country_code");
                    
                    // Définir les paramètres par défaut selon le pays
                    String language = getDefaultLanguage(countryCode);
                    String currency = getDefaultCurrency(countryCode);
                    String timezone = getDefaultTimezone(countryCode);
                    String dateFormat = "dd/MM/yyyy";
                    String numberFormat = "#,##0.00";
                    
                    insertStmt.setLong(1, companyId);
                    insertStmt.setString(2, language);
                    insertStmt.setString(3, countryCode);
                    insertStmt.setString(4, currency);
                    insertStmt.setString(5, timezone);
                    insertStmt.setString(6, dateFormat);
                    insertStmt.setString(7, numberFormat);
                    insertStmt.setObject(8, now);
                    insertStmt.setObject(9, now);
                    
                    if (insertStmt.executeUpdate() > 0) {
                        synced++;
                    }
                }
            }
        }
        
        return synced;
    }
    
    /**
     * Synchronise les configurations de protection des données par défaut
     */
    private int synchronizeDefaultDataProtection(Connection connection) throws SQLException {
        int synced = 0;
        
        // Récupérer toutes les entreprises sans configuration de protection des données
        String sql = "SELECT c.id, c.country_code FROM companies c " +
                    "LEFT JOIN data_protection dp ON c.id = dp.company_id " +
                    "WHERE dp.id IS NULL";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            String insertSql = "INSERT INTO data_protection (company_id, regulation_type, data_controller_name, data_controller_email, " +
                              "consent_required, explicit_consent_required, data_portability_enabled, right_to_erasure_enabled, " +
                              "data_breach_notification_enabled, audit_trail_enabled, encryption_enabled, anonymization_enabled, " +
                              "is_active, created_at, updated_at) " +
                              "VALUES (?, ?, ?, ?, true, true, true, true, true, true, true, true, true, ?, ?)";
            
            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                LocalDateTime now = LocalDateTime.now();
                
                while (rs.next()) {
                    Long companyId = rs.getLong("id");
                    String countryCode = rs.getString("country_code");
                    
                    // Définir le type de réglementation selon le pays
                    String regulationType = getDefaultRegulationType(countryCode);
                    String controllerName = "Contrôleur de données par défaut";
                    String controllerEmail = "dpo@company.com";
                    
                    insertStmt.setLong(1, companyId);
                    insertStmt.setString(2, regulationType);
                    insertStmt.setString(3, controllerName);
                    insertStmt.setString(4, controllerEmail);
                    insertStmt.setObject(5, now);
                    insertStmt.setObject(6, now);
                    
                    if (insertStmt.executeUpdate() > 0) {
                        synced++;
                    }
                }
            }
        }
        
        return synced;
    }
    
    /**
     * Obtient la langue par défaut selon le code pays
     */
    private String getDefaultLanguage(String countryCode) {
        return switch (countryCode) {
            case "FR" -> "fr";
            case "US", "CA" -> "en";
            case "GB" -> "en";
            case "DE" -> "de";
            case "IT" -> "it";
            case "ES" -> "es";
            default -> "en";
        };
    }
    
    /**
     * Obtient la devise par défaut selon le code pays
     */
    private String getDefaultCurrency(String countryCode) {
        return switch (countryCode) {
            case "FR", "DE", "IT", "ES" -> "EUR";
            case "US" -> "USD";
            case "GB" -> "GBP";
            case "CA" -> "CAD";
            case "AU" -> "AUD";
            case "JP" -> "JPY";
            case "CN" -> "CNY";
            default -> "EUR";
        };
    }
    
    /**
     * Obtient le fuseau horaire par défaut selon le code pays
     */
    private String getDefaultTimezone(String countryCode) {
        return switch (countryCode) {
            case "FR" -> "Europe/Paris";
            case "US" -> "America/New_York";
            case "GB" -> "Europe/London";
            case "DE" -> "Europe/Berlin";
            case "IT" -> "Europe/Rome";
            case "ES" -> "Europe/Madrid";
            case "CA" -> "America/Toronto";
            case "AU" -> "Australia/Sydney";
            case "JP" -> "Asia/Tokyo";
            case "CN" -> "Asia/Shanghai";
            default -> "UTC";
        };
    }
    
    /**
     * Obtient le type de réglementation par défaut selon le code pays
     */
    private String getDefaultRegulationType(String countryCode) {
        return switch (countryCode) {
            case "FR", "DE", "IT", "ES" -> "GDPR";
            case "US" -> "CCPA";
            case "CA" -> "PIPEDA";
            case "BR" -> "LGPD";
            case "SG" -> "PDPA";
            default -> "GDPR";
        };
    }
    
    /**
     * Génère un rapport de synchronisation complet
     */
    public Map<String, Object> generateSynchronizationReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Synchronisation des données de base
        Map<String, Object> baseDataSync = synchronizeBaseData();
        report.put("baseDataSync", baseDataSync);
        
        // Synchronisation des configurations par entreprise
        Map<String, Object> companyConfigSync = synchronizeCompanyConfigurations();
        report.put("companyConfigSync", companyConfigSync);
        
        // Résumé global
        boolean overallSuccess = (Boolean) baseDataSync.get("success") &&
                               (Boolean) companyConfigSync.get("success");
        
        int totalOperations = (Integer) baseDataSync.get("totalOperations") +
                             (Integer) companyConfigSync.get("totalOperations");
        
        report.put("overallSuccess", overallSuccess);
        report.put("totalOperations", totalOperations);
        report.put("generatedAt", new Date());
        
        return report;
    }
}





