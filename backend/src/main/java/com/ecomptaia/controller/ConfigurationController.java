package com.ecomptaia.controller;

import com.ecomptaia.entity.Configuration;
import com.ecomptaia.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
@CrossOrigin(origins = "*")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    // ==================== GESTION DES CONFIGURATIONS ====================

    /**
     * Créer une nouvelle configuration
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createConfiguration(
            @RequestBody Configuration configuration) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            // Valider la configuration
            if (!configurationService.validateConfiguration(configuration)) {
                response.put("success", false);
                response.put("message", "Configuration invalide");
                return ResponseEntity.badRequest().body(response);
            }
            
            Configuration created = configurationService.createConfiguration(configuration);
            response.put("success", true);
            response.put("data", created);
            response.put("message", "Configuration créée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Mettre à jour une configuration
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateConfiguration(
            @PathVariable Long id,
            @RequestBody Configuration configuration) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Configuration updated = configurationService.updateConfiguration(id, configuration);
            response.put("success", true);
            response.put("data", updated);
            response.put("message", "Configuration mise à jour avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Supprimer une configuration
     */
    @DeleteMapping("/delete/{configKey}")
    public ResponseEntity<Map<String, Object>> deleteConfiguration(
            @PathVariable String configKey) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            configurationService.deleteConfiguration(configKey);
            response.put("success", true);
            response.put("message", "Configuration supprimée avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir une configuration par clé
     */
    @GetMapping("/key/{configKey}")
    public ResponseEntity<Map<String, Object>> getConfigurationByKey(
            @PathVariable String configKey) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Configuration config = configurationService.getConfigurationByKey(configKey)
                    .orElse(null);
            
            if (config != null) {
                response.put("success", true);
                response.put("data", config);
            } else {
                response.put("success", false);
                response.put("message", "Configuration non trouvée");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir une configuration avec contexte
     */
    @GetMapping("/key/{configKey}/context")
    public ResponseEntity<Map<String, Object>> getConfigurationWithContext(
            @PathVariable String configKey,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Configuration config = configurationService.getConfigurationWithPriority(configKey, companyId, userId)
                    .orElse(null);
            
            if (config != null) {
                response.put("success", true);
                response.put("data", config);
            } else {
                response.put("success", false);
                response.put("message", "Configuration non trouvée");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== OBTENTION DES VALEURS TYPÉES ====================

    /**
     * Obtenir une valeur String
     */
    @GetMapping("/value/string/{configKey}")
    public ResponseEntity<Map<String, Object>> getStringValue(
            @PathVariable String configKey,
            @RequestParam(required = false) String defaultValue) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            String value = configurationService.getStringValue(configKey, defaultValue != null ? defaultValue : "");
            response.put("success", true);
            response.put("key", configKey);
            response.put("value", value);
            response.put("type", "STRING");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir une valeur Integer
     */
    @GetMapping("/value/integer/{configKey}")
    public ResponseEntity<Map<String, Object>> getIntegerValue(
            @PathVariable String configKey,
            @RequestParam(required = false, defaultValue = "0") Integer defaultValue) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Integer value = configurationService.getIntegerValue(configKey, defaultValue);
            response.put("success", true);
            response.put("key", configKey);
            response.put("value", value);
            response.put("type", "INTEGER");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir une valeur Boolean
     */
    @GetMapping("/value/boolean/{configKey}")
    public ResponseEntity<Map<String, Object>> getBooleanValue(
            @PathVariable String configKey,
            @RequestParam(required = false, defaultValue = "false") Boolean defaultValue) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Boolean value = configurationService.getBooleanValue(configKey, defaultValue);
            response.put("success", true);
            response.put("key", configKey);
            response.put("value", value);
            response.put("type", "BOOLEAN");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir une valeur Decimal
     */
    @GetMapping("/value/decimal/{configKey}")
    public ResponseEntity<Map<String, Object>> getDecimalValue(
            @PathVariable String configKey,
            @RequestParam(required = false, defaultValue = "0.0") String defaultValue) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            java.math.BigDecimal value = configurationService.getDecimalValue(configKey, new java.math.BigDecimal(defaultValue));
            response.put("success", true);
            response.put("key", configKey);
            response.put("value", value);
            response.put("type", "DECIMAL");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== GESTION PAR CATÉGORIE ====================

    /**
     * Obtenir toutes les configurations d'une catégorie
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getConfigurationsByCategory(
            @PathVariable Configuration.ConfigCategory category) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Configuration> configs = configurationService.getConfigurationsByCategory(category);
            response.put("success", true);
            response.put("data", configs);
            response.put("category", category);
            response.put("count", configs.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir toutes les configurations d'une catégorie pour une entreprise
     */
    @GetMapping("/category/{category}/company/{companyId}")
    public ResponseEntity<Map<String, Object>> getConfigurationsByCategoryAndCompany(
            @PathVariable Configuration.ConfigCategory category,
            @PathVariable Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Configuration> configs = configurationService.getConfigurationsByCategory(category, companyId);
            response.put("success", true);
            response.put("data", configs);
            response.put("category", category);
            response.put("companyId", companyId);
            response.put("count", configs.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== GESTION PAR CONTEXTE ====================

    /**
     * Obtenir toutes les configurations système
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemConfigurations() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Configuration> configs = configurationService.getSystemConfigurations();
            response.put("success", true);
            response.put("data", configs);
            response.put("context", "SYSTEM");
            response.put("count", configs.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir toutes les configurations d'entreprise
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<Map<String, Object>> getCompanyConfigurations(
            @PathVariable Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Configuration> configs = configurationService.getCompanyConfigurations(companyId);
            response.put("success", true);
            response.put("data", configs);
            response.put("companyId", companyId);
            response.put("count", configs.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir toutes les configurations utilisateur
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserConfigurations(
            @PathVariable Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Configuration> configs = configurationService.getUserConfigurations(userId);
            response.put("success", true);
            response.put("data", configs);
            response.put("userId", userId);
            response.put("count", configs.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== RECHERCHE ET FILTRAGE ====================

    /**
     * Rechercher des configurations
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchConfigurations(
            @RequestParam String query) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Configuration> configs = configurationService.searchConfigurations(query);
            response.put("success", true);
            response.put("data", configs);
            response.put("query", query);
            response.put("count", configs.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Filtrer les configurations
     */
    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterConfigurations(
            @RequestParam(required = false) Configuration.ConfigCategory category,
            @RequestParam(required = false) Configuration.ConfigType configType,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Configuration> configs = configurationService.filterConfigurations(category, configType, companyId, userId);
            response.put("success", true);
            response.put("data", configs);
            response.put("filters", Map.of(
                "category", category,
                "configType", configType,
                "companyId", companyId,
                "userId", userId
            ));
            response.put("count", configs.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== GESTION DES VERSIONS ====================

    /**
     * Obtenir l'historique des versions d'une configuration
     */
    @GetMapping("/history/{configKey}")
    public ResponseEntity<Map<String, Object>> getConfigurationHistory(
            @PathVariable String configKey) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            List<Configuration> history = configurationService.getConfigurationHistory(configKey);
            response.put("success", true);
            response.put("data", history);
            response.put("configKey", configKey);
            response.put("count", history.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== VALIDATION ET VÉRIFICATION ====================

    /**
     * Valider une configuration
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateConfiguration(
            @RequestBody Configuration configuration) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isValid = configurationService.validateConfiguration(configuration);
            response.put("success", true);
            response.put("valid", isValid);
            response.put("message", isValid ? "Configuration valide" : "Configuration invalide");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Vérifier si une configuration existe
     */
    @GetMapping("/exists/{configKey}")
    public ResponseEntity<Map<String, Object>> configurationExists(
            @PathVariable String configKey) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exists = configurationService.configurationExists(configKey);
            response.put("success", true);
            response.put("exists", exists);
            response.put("configKey", configKey);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== INITIALISATION ET DONNÉES DE TEST ====================

    /**
     * Initialiser les configurations par défaut
     */
    @PostMapping("/init")
    public ResponseEntity<Map<String, Object>> initializeDefaultConfigurations() {
        Map<String, Object> response = new HashMap<>();
        try {
            configurationService.initializeDefaultConfigurations();
            response.put("success", true);
            response.put("message", "Configurations par défaut initialisées avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test du système de configuration
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testConfiguration() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> testData = configurationService.getTestConfigurationData();
            response.put("success", true);
            response.put("message", "Système de configuration dynamique opérationnel");
            response.put("data", testData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors du test : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}







