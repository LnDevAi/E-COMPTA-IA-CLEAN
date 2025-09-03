package com.ecomptaia.service;

import com.ecomptaia.entity.Configuration;
import com.ecomptaia.repository.ConfigurationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // ==================== GESTION DES CONFIGURATIONS ====================

    /**
     * Créer une nouvelle configuration
     */
    public Configuration createConfiguration(Configuration configuration) {
        // Vérifier si la clé existe déjà
        if (configurationRepository.findByConfigKey(configuration.getConfigKey()).isPresent()) {
            throw new RuntimeException("Une configuration avec cette clé existe déjà");
        }
        
        configuration.setCreatedAt(LocalDateTime.now());
        configuration.setVersion(1);
        
        return configurationRepository.save(configuration);
    }

    /**
     * Mettre à jour une configuration
     */
    @CacheEvict(value = "configurations", key = "#configuration.configKey")
    public Configuration updateConfiguration(Long id, Configuration configuration) {
        Configuration existingConfig = configurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuration non trouvée"));
        
        existingConfig.setConfigValue(configuration.getConfigValue());
        existingConfig.setDescription(configuration.getDescription());
        existingConfig.setIsActive(configuration.getIsActive());
        existingConfig.setIsEncrypted(configuration.getIsEncrypted());
        existingConfig.setUpdatedAt(LocalDateTime.now());
        
        return configurationRepository.save(existingConfig);
    }

    /**
     * Supprimer une configuration (désactivation)
     */
    @CacheEvict(value = "configurations", key = "#configKey")
    public void deleteConfiguration(String configKey) {
        Configuration config = configurationRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new RuntimeException("Configuration non trouvée"));
        
        config.setIsActive(false);
        config.setUpdatedAt(LocalDateTime.now());
        configurationRepository.save(config);
    }

    /**
     * Obtenir une configuration par clé
     */
    @Cacheable(value = "configurations", key = "#configKey")
    public Optional<Configuration> getConfigurationByKey(String configKey) {
        return configurationRepository.findByConfigKeyAndIsActiveTrue(configKey);
    }

    /**
     * Obtenir une configuration avec priorité (système > entreprise > utilisateur)
     */
    public Optional<Configuration> getConfigurationWithPriority(String configKey, Long companyId, Long userId) {
        List<Configuration> configs = configurationRepository.findConfigurationsByPriority(configKey);
        
        // Filtrer par contexte
        return configs.stream()
                .filter(config -> {
                    if (companyId != null && config.getCompanyId() != null) {
                        return config.getCompanyId().equals(companyId);
                    }
                    if (userId != null && config.getUserId() != null) {
                        return config.getUserId().equals(userId);
                    }
                    return config.getCompanyId() == null && config.getUserId() == null;
                })
                .findFirst();
    }

    // ==================== OBTENTION DES VALEURS TYPÉES ====================

    /**
     * Obtenir une valeur String
     */
    public String getStringValue(String configKey, String defaultValue) {
        return getConfigurationByKey(configKey)
                .map(Configuration::getConfigValue)
                .orElse(defaultValue);
    }

    /**
     * Obtenir une valeur String avec contexte
     */
    public String getStringValue(String configKey, String defaultValue, Long companyId, Long userId) {
        return getConfigurationWithPriority(configKey, companyId, userId)
                .map(Configuration::getConfigValue)
                .orElse(defaultValue);
    }

    /**
     * Obtenir une valeur Integer
     */
    public Integer getIntegerValue(String configKey, Integer defaultValue) {
        try {
            return getConfigurationByKey(configKey)
                    .map(config -> Integer.parseInt(config.getConfigValue()))
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Obtenir une valeur Integer avec contexte
     */
    public Integer getIntegerValue(String configKey, Integer defaultValue, Long companyId, Long userId) {
        try {
            return getConfigurationWithPriority(configKey, companyId, userId)
                    .map(config -> Integer.parseInt(config.getConfigValue()))
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Obtenir une valeur Boolean
     */
    public Boolean getBooleanValue(String configKey, Boolean defaultValue) {
        return getConfigurationByKey(configKey)
                .map(config -> Boolean.parseBoolean(config.getConfigValue()))
                .orElse(defaultValue);
    }

    /**
     * Obtenir une valeur Boolean avec contexte
     */
    public Boolean getBooleanValue(String configKey, Boolean defaultValue, Long companyId, Long userId) {
        return getConfigurationWithPriority(configKey, companyId, userId)
                .map(config -> Boolean.parseBoolean(config.getConfigValue()))
                .orElse(defaultValue);
    }

    /**
     * Obtenir une valeur Decimal
     */
    public BigDecimal getDecimalValue(String configKey, BigDecimal defaultValue) {
        try {
            return getConfigurationByKey(configKey)
                    .map(config -> new BigDecimal(config.getConfigValue()))
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Obtenir une valeur Decimal avec contexte
     */
    public BigDecimal getDecimalValue(String configKey, BigDecimal defaultValue, Long companyId, Long userId) {
        try {
            return getConfigurationWithPriority(configKey, companyId, userId)
                    .map(config -> new BigDecimal(config.getConfigValue()))
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Obtenir une valeur JSON
     */
    public <T> T getJsonValue(String configKey, Class<T> clazz, T defaultValue) {
        try {
            return getConfigurationByKey(configKey)
                    .map(config -> {
                        try {
                            return objectMapper.readValue(config.getConfigValue(), clazz);
                        } catch (Exception e) {
                            return defaultValue;
                        }
                    })
                    .orElse(defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Obtenir une valeur JSON avec contexte
     */
    public <T> T getJsonValue(String configKey, Class<T> clazz, T defaultValue, Long companyId, Long userId) {
        try {
            return getConfigurationWithPriority(configKey, companyId, userId)
                    .map(config -> {
                        try {
                            return objectMapper.readValue(config.getConfigValue(), clazz);
                        } catch (Exception e) {
                            return defaultValue;
                        }
                    })
                    .orElse(defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // ==================== GESTION PAR CATÉGORIE ====================

    /**
     * Obtenir toutes les configurations d'une catégorie
     */
    public List<Configuration> getConfigurationsByCategory(Configuration.ConfigCategory category) {
        return configurationRepository.findByCategoryAndIsActiveTrue(category);
    }

    /**
     * Obtenir toutes les configurations d'une catégorie pour une entreprise
     */
    public List<Configuration> getConfigurationsByCategory(Configuration.ConfigCategory category, Long companyId) {
        return configurationRepository.findByCompanyIdAndCategoryAndIsActiveTrue(companyId, category);
    }

    /**
     * Obtenir toutes les configurations d'une catégorie pour un utilisateur
     */
    public List<Configuration> getConfigurationsByCategory(Configuration.ConfigCategory category, Long companyId, Long userId) {
        return configurationRepository.findByUserIdAndCategoryAndIsActiveTrue(userId, category);
    }

    // ==================== GESTION PAR CONTEXTE ====================

    /**
     * Obtenir toutes les configurations système
     */
    public List<Configuration> getSystemConfigurations() {
        return configurationRepository.findByCompanyIdIsNullAndUserIdIsNullAndIsActiveTrue();
    }

    /**
     * Obtenir toutes les configurations d'entreprise
     */
    public List<Configuration> getCompanyConfigurations(Long companyId) {
        return configurationRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    /**
     * Obtenir toutes les configurations utilisateur
     */
    public List<Configuration> getUserConfigurations(Long userId) {
        return configurationRepository.findByUserIdAndIsActiveTrue(userId);
    }

    // ==================== RECHERCHE ET FILTRAGE ====================

    /**
     * Rechercher des configurations
     */
    public List<Configuration> searchConfigurations(String query) {
        List<Configuration> byKey = configurationRepository.findByConfigKeyContainingIgnoreCase(query);
        List<Configuration> byDescription = configurationRepository.findByDescriptionContainingIgnoreCase(query);
        
        Set<Configuration> results = new HashSet<>();
        results.addAll(byKey);
        results.addAll(byDescription);
        
        return results.stream()
                .filter(Configuration::getIsActive)
                .collect(Collectors.toList());
    }

    /**
     * Filtrer les configurations
     */
    public List<Configuration> filterConfigurations(Configuration.ConfigCategory category, 
                                                   Configuration.ConfigType configType, 
                                                   Long companyId, 
                                                   Long userId) {
        return configurationRepository.findConfigurationsWithFilters(category, configType, companyId, userId);
    }

    // ==================== GESTION DES VERSIONS ====================

    /**
     * Obtenir l'historique des versions d'une configuration
     */
    public List<Configuration> getConfigurationHistory(String configKey) {
        // Note: Cette implémentation suppose une table d'historique séparée
        // Pour simplifier, on retourne la configuration actuelle
        return configurationRepository.findByConfigKey(configKey)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    /**
     * Restaurer une version précédente
     */
    public Configuration restoreConfigurationVersion(Long configId, Integer version) {
        // Note: Cette implémentation suppose une table d'historique séparée
        // Pour simplifier, on retourne la configuration actuelle
        return configurationRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Configuration non trouvée"));
    }

    // ==================== VALIDATION ET VÉRIFICATION ====================

    /**
     * Valider une configuration
     */
    public boolean validateConfiguration(Configuration configuration) {
        if (configuration.getConfigKey() == null || configuration.getConfigKey().trim().isEmpty()) {
            return false;
        }
        
        if (configuration.getConfigValue() == null) {
            return false;
        }
        
        // Validation selon le type
        try {
            switch (configuration.getConfigType()) {
                case INTEGER:
                    Integer.parseInt(configuration.getConfigValue());
                    break;
                case BOOLEAN:
                    Boolean.parseBoolean(configuration.getConfigValue());
                    break;
                case DECIMAL:
                    new BigDecimal(configuration.getConfigValue());
                    break;
                case JSON:
                    objectMapper.readTree(configuration.getConfigValue());
                    break;
                case STRING:
                    // Les chaînes sont toujours valides
                    break;
                case ENCRYPTED:
                    // Les valeurs chiffrées sont validées par le système de chiffrement
                    break;
                case XML:
                    // Validation XML basique - vérifier que c'est un XML valide
                    if (!configuration.getConfigValue().trim().startsWith("<")) {
                        throw new IllegalArgumentException("Valeur XML invalide");
                    }
                    break;
            }
        } catch (Exception e) {
            return false;
        }
        
        return true;
    }

    /**
     * Vérifier si une configuration existe
     */
    public boolean configurationExists(String configKey) {
        return configurationRepository.findByConfigKeyAndIsActiveTrue(configKey).isPresent();
    }

    // ==================== DONNÉES DE TEST ====================

    /**
     * Initialiser les configurations par défaut
     */
    public void initializeDefaultConfigurations() {
        // Configurations système
        createConfigurationIfNotExists("app.name", "E-COMPTA IA", Configuration.ConfigType.STRING, Configuration.ConfigCategory.SYSTEM, "Nom de l'application");
        createConfigurationIfNotExists("app.version", "1.0.0", Configuration.ConfigType.STRING, Configuration.ConfigCategory.SYSTEM, "Version de l'application");
        createConfigurationIfNotExists("app.environment", "development", Configuration.ConfigType.STRING, Configuration.ConfigCategory.SYSTEM, "Environnement");
        
        // Configurations de sécurité
        createConfigurationIfNotExists("security.jwt.expiration", "3600", Configuration.ConfigType.INTEGER, Configuration.ConfigCategory.SECURITY, "Expiration du token JWT (secondes)");
        createConfigurationIfNotExists("security.password.minLength", "8", Configuration.ConfigType.INTEGER, Configuration.ConfigCategory.SECURITY, "Longueur minimale du mot de passe");
        createConfigurationIfNotExists("security.session.timeout", "1800", Configuration.ConfigType.INTEGER, Configuration.ConfigCategory.SECURITY, "Timeout de session (secondes)");
        
        // Configurations de notifications
        createConfigurationIfNotExists("notification.email.enabled", "true", Configuration.ConfigType.BOOLEAN, Configuration.ConfigCategory.NOTIFICATION, "Activer les notifications email");
        createConfigurationIfNotExists("notification.sms.enabled", "false", Configuration.ConfigType.BOOLEAN, Configuration.ConfigCategory.NOTIFICATION, "Activer les notifications SMS");
        createConfigurationIfNotExists("notification.push.enabled", "true", Configuration.ConfigType.BOOLEAN, Configuration.ConfigCategory.NOTIFICATION, "Activer les notifications push");
        
        // Configurations comptables
        createConfigurationIfNotExists("accounting.default.currency", "XOF", Configuration.ConfigType.STRING, Configuration.ConfigCategory.ACCOUNTING, "Devise par défaut");
        createConfigurationIfNotExists("accounting.decimal.places", "2", Configuration.ConfigType.INTEGER, Configuration.ConfigCategory.ACCOUNTING, "Nombre de décimales");
        createConfigurationIfNotExists("accounting.auto.numbering", "true", Configuration.ConfigType.BOOLEAN, Configuration.ConfigCategory.ACCOUNTING, "Numérotation automatique");
        
        // Configurations AI
        createConfigurationIfNotExists("ai.enabled", "true", Configuration.ConfigType.BOOLEAN, Configuration.ConfigCategory.AI, "Activer l'IA");
        createConfigurationIfNotExists("ai.provider", "openai", Configuration.ConfigType.STRING, Configuration.ConfigCategory.AI, "Fournisseur d'IA");
        createConfigurationIfNotExists("ai.max.tokens", "1000", Configuration.ConfigType.INTEGER, Configuration.ConfigCategory.AI, "Nombre maximum de tokens");
        
        // Configurations mobile
        createConfigurationIfNotExists("mobile.sync.interval", "3600", Configuration.ConfigType.INTEGER, Configuration.ConfigCategory.MOBILE, "Intervalle de synchronisation mobile (secondes)");
        createConfigurationIfNotExists("mobile.offline.enabled", "true", Configuration.ConfigType.BOOLEAN, Configuration.ConfigCategory.MOBILE, "Activer le mode hors ligne");
    }

    /**
     * Créer une configuration si elle n'existe pas
     */
    private void createConfigurationIfNotExists(String configKey, String configValue, 
                                               Configuration.ConfigType configType, 
                                               Configuration.ConfigCategory category, 
                                               String description) {
        if (!configurationExists(configKey)) {
            Configuration config = new Configuration(configKey, configValue, configType, category);
            config.setDescription(description);
            createConfiguration(config);
        }
    }

    /**
     * Données de test pour le service de configuration
     */
    public Map<String, Object> getTestConfigurationData() {
        Map<String, Object> testData = new HashMap<>();
        
        testData.put("message", "Service de configuration dynamique opérationnel");
        testData.put("timestamp", LocalDateTime.now());
        testData.put("version", "1.0.0");
        
        // Statistiques
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConfigurations", configurationRepository.count());
        stats.put("activeConfigurations", configurationRepository.findByIsActiveTrue().size());
        stats.put("systemConfigurations", configurationRepository.findByCompanyIdIsNullAndUserIdIsNullAndIsActiveTrue().size());
        stats.put("categories", Configuration.ConfigCategory.values().length);
        testData.put("stats", stats);
        
        // Exemples de configurations
        List<Map<String, Object>> examples = new ArrayList<>();
        
        Map<String, Object> example1 = new HashMap<>();
        example1.put("key", "app.name");
        example1.put("value", "E-COMPTA IA");
        example1.put("type", "STRING");
        example1.put("category", "SYSTEM");
        examples.add(example1);
        
        Map<String, Object> example2 = new HashMap<>();
        example2.put("key", "security.jwt.expiration");
        example2.put("value", "3600");
        example2.put("type", "INTEGER");
        example2.put("category", "SECURITY");
        examples.add(example2);
        
        Map<String, Object> example3 = new HashMap<>();
        example3.put("key", "ai.enabled");
        example3.put("value", "true");
        example3.put("type", "BOOLEAN");
        example3.put("category", "AI");
        examples.add(example3);
        
        testData.put("examples", examples);
        
        return testData;
    }
}




