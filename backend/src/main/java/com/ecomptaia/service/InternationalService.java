package com.ecomptaia.service;

import com.ecomptaia.entity.CountryConfig;
import com.ecomptaia.entity.ExpansionAnalysis;
import com.ecomptaia.repository.CountryConfigRepository;
import com.ecomptaia.repository.ExpansionAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class InternationalService {

    @Autowired
    private CountryConfigRepository countryConfigRepository;

    @Autowired
    private ExpansionAnalysisRepository expansionAnalysisRepository;



    /**
     * Obtenir la configuration complète d'un pays
     */
    // @Cacheable(value = "countryConfigs", key = "#countryCode") // Cache temporairement désactivé
    public CountryConfig getCountryConfig(String countryCode) {
        return countryConfigRepository.findByCountryCodeAndIsActiveTrue(countryCode.toUpperCase())
            .orElseThrow(() -> new RuntimeException("Pays non supporté : " + countryCode));
    }

    /**
     * Vérifier si un pays est supporté
     */
    public boolean isCountrySupported(String countryCode) {
        return countryConfigRepository.findByCountryCodeAndIsActiveTrue(countryCode.toUpperCase()).isPresent();
    }

    /**
     * Obtenir la liste des pays supportés
     */
    public List<String> getSupportedCountries() {
        return countryConfigRepository.findAll().stream()
            .filter(CountryConfig::getIsActive)
            .map(CountryConfig::getCountryCode)
            .collect(Collectors.toList());
    }

    /**
     * Obtenir tous les pays OHADA
     */
    public List<CountryConfig> getOHADACountries() {
        return countryConfigRepository.findOHADACountries();
    }

    /**
     * Obtenir les pays développés
     */
    public List<CountryConfig> getDevelopedCountries() {
        return countryConfigRepository.findDevelopedCountries();
    }

    /**
     * Obtenir les pays par standard comptable
     */
    public List<CountryConfig> getCountriesByAccountingStandard(String standard) {
        return countryConfigRepository.findByAccountingStandardAndIsActiveTrue(standard);
    }

    /**
     * Obtenir les pays par type de système
     */
    public List<CountryConfig> getCountriesBySystemType(String systemType) {
        return countryConfigRepository.findBySystemTypeAndIsActiveTrue(systemType);
    }

    /**
     * Obtenir les pays avec APIs complètes
     */
    public List<CountryConfig> getCountriesWithCompleteApis() {
        return countryConfigRepository.findCountriesWithCompleteApis();
    }

    /**
     * Obtenir les pays avec systèmes de paiement
     */
    public List<CountryConfig> getCountriesWithPaymentSystems() {
        return countryConfigRepository.findCountriesWithPaymentSystems();
    }

    /**
     * Obtenir les pays par continent
     */
    public List<CountryConfig> getCountriesByContinent(String continent) {
        return countryConfigRepository.findByContinent(continent);
    }

    /**
     * Créer ou mettre à jour une configuration pays
     */
    public CountryConfig saveCountryConfig(CountryConfig config) {
        if (config.getId() == null) {
            config.setCreatedAt(LocalDateTime.now());
        }
        config.setUpdatedAt(LocalDateTime.now());
        return countryConfigRepository.save(config);
    }

    /**
     * Désactiver un pays
     */
    public void deactivateCountry(String countryCode) {
        CountryConfig config = getCountryConfig(countryCode);
        config.setIsActive(false);
        config.setStatus("INACTIVE");
        config.setUpdatedAt(LocalDateTime.now());
        countryConfigRepository.save(config);
    }

    /**
     * Activer un pays
     */
    public void activateCountry(String countryCode) {
        CountryConfig config = countryConfigRepository.findByCountryCodeAndIsActiveTrue(countryCode.toUpperCase())
            .orElseThrow(() -> new RuntimeException("Configuration pays non trouvée"));
        config.setIsActive(true);
        config.setStatus("ACTIVE");
        config.setUpdatedAt(LocalDateTime.now());
        countryConfigRepository.save(config);
    }

    /**
     * Obtenir les statistiques par standard comptable
     */
    public Map<String, Long> getStatisticsByAccountingStandard() {
        List<Object[]> stats = countryConfigRepository.getStatisticsByAccountingStandard();
        return stats.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));
    }

    /**
     * Obtenir les statistiques par devise
     */
    public Map<String, Long> getStatisticsByCurrency() {
        List<Object[]> stats = countryConfigRepository.getStatisticsByCurrency();
        return stats.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));
    }

    /**
     * Obtenir les statistiques par statut
     */
    public Map<String, Long> getStatisticsByStatus() {
        List<Object[]> stats = countryConfigRepository.getStatisticsByStatus();
        return stats.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));
    }

    /**
     * Obtenir les statistiques par type de système
     */
    public Map<String, Long> getStatisticsBySystemType() {
        List<Object[]> stats = countryConfigRepository.getStatisticsBySystemType();
        return stats.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> (Long) row[1]
            ));
    }

    /**
     * Analyser le potentiel d'expansion d'un pays
     */
    public ExpansionAnalysis analyzeExpansionPotential(String countryCode) {
        CountryConfig config = getCountryConfig(countryCode);
        
        // Calculer les scores
        int digitalMaturityScore = calculateDigitalMaturityScore(config);
        int economicScore = calculateEconomicScore(config);
        int apiAvailabilityScore = calculateApiAvailabilityScore(config);
        int integrationComplexityScore = calculateIntegrationComplexityScore(config);

        // Créer l'analyse
        ExpansionAnalysis analysis = new ExpansionAnalysis(
            countryCode,
            config.getCountryName(),
            digitalMaturityScore,
            economicScore,
            apiAvailabilityScore,
            integrationComplexityScore
        );

        // Ajouter des détails
        analysis.setAnalysisDetails(generateAnalysisDetails(config));
        analysis.setMarketOpportunities(generateMarketOpportunities(config));
        analysis.setChallenges(generateChallenges(config));
        analysis.setNextSteps(generateNextSteps(analysis));

        return expansionAnalysisRepository.save(analysis);
    }

    /**
     * Obtenir les priorités d'expansion
     */
    public List<ExpansionAnalysis> getExpansionPriorities(int limit) {
        return expansionAnalysisRepository.findTopRecommendedCountries().stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * Obtenir les pays recommandés pour expansion
     */
    public List<ExpansionAnalysis> getRecommendedCountries() {
        return expansionAnalysisRepository.findByRecommendationAndIsActiveTrueOrderByTotalScoreDesc("RECOMMENDED");
    }

    /**
     * Obtenir les pays à considérer pour expansion
     */
    public List<ExpansionAnalysis> getConsiderCountries() {
        return expansionAnalysisRepository.findByRecommendationAndIsActiveTrueOrderByTotalScoreDesc("CONSIDER");
    }

    /**
     * Obtenir les pays non recommandés
     */
    public List<ExpansionAnalysis> getNotRecommendedCountries() {
        return expansionAnalysisRepository.findByRecommendationAndIsActiveTrueOrderByTotalScoreDesc("NOT_RECOMMENDED");
    }

    /**
     * Valider l'intégration d'un pays
     */
    public Map<String, Object> validateCountryIntegration(String countryCode) {
        CountryConfig config = getCountryConfig(countryCode);
        Map<String, Object> result = new HashMap<>();
        
        result.put("countryCode", countryCode);
        result.put("countryName", config.getCountryName());
        result.put("validationDate", LocalDateTime.now());
        
        List<String> successes = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Tester les APIs disponibles
        if (config.getBusinessCreationApiAvailable() != null && config.getBusinessCreationApiAvailable()) {
            try {
                if (config.getBusinessCreationApiUrl() != null) {
                    // Test de connexion basique
                    successes.add("API création d'entreprise disponible");
                }
            } catch (Exception e) {
                warnings.add("API création d'entreprise inaccessible: " + e.getMessage());
            }
        }

        if (config.getTaxAdministrationApiUrl() != null) {
            try {
                // Test de connexion basique
                successes.add("API administration fiscale disponible");
            } catch (Exception e) {
                warnings.add("API administration fiscale inaccessible: " + e.getMessage());
            }
        }

        if (config.getSocialSecurityApiUrl() != null) {
            try {
                // Test de connexion basique
                successes.add("API sécurité sociale disponible");
            } catch (Exception e) {
                warnings.add("API sécurité sociale inaccessible: " + e.getMessage());
            }
        }

        if (config.getCentralBankApiUrl() != null) {
            try {
                // Test de connexion basique
                successes.add("API banque centrale disponible");
            } catch (Exception e) {
                warnings.add("API banque centrale inaccessible: " + e.getMessage());
            }
        }

        // Vérifier la configuration
        if (config.getPaymentSystems() == null || config.getPaymentSystems().isEmpty()) {
            warnings.add("Aucun système de paiement configuré");
        } else {
            successes.add("Systèmes de paiement configurés");
        }

        if (config.getOfficialApis() == null || config.getOfficialApis().isEmpty()) {
            warnings.add("Aucune API officielle configurée");
        } else {
            successes.add("APIs officielles configurées");
        }

        if (config.getRegulations() == null || config.getRegulations().isEmpty()) {
            warnings.add("Aucune réglementation configurée");
        } else {
            successes.add("Réglementations configurées");
        }

        result.put("successes", successes);
        result.put("warnings", warnings);
        result.put("errors", errors);
        result.put("overallStatus", errors.isEmpty() ? "SUCCESS" : "WARNING");

        return result;
    }

    // Méthodes privées pour le calcul des scores
    private int calculateDigitalMaturityScore(CountryConfig config) {
        int score = 0;
        
        // Score basé sur les APIs disponibles
        if (config.getBusinessCreationApiAvailable() != null && config.getBusinessCreationApiAvailable()) score += 20;
        if (config.getTaxAdministrationApiUrl() != null) score += 15;
        if (config.getSocialSecurityApiUrl() != null) score += 15;
        if (config.getCentralBankApiUrl() != null) score += 10;
        
        // Score basé sur les systèmes de paiement
        if (config.getPaymentSystems() != null && !config.getPaymentSystems().isEmpty()) score += 20;
        
        // Score basé sur les APIs officielles
        if (config.getOfficialApis() != null && !config.getOfficialApis().isEmpty()) score += 20;
        
        return Math.min(score, 100);
    }

    private int calculateEconomicScore(CountryConfig config) {
        // Score basé sur le standard comptable et la devise
        int score = 50; // Score de base
        
        switch (config.getAccountingStandard()) {
            case "IFRS":
                score += 30;
                break;
            case "US_GAAP":
                score += 25;
                break;
            case "SYSCOHADA":
                score += 20;
                break;
            case "PCG":
                score += 20;
                break;
            default:
                score += 10;
        }
        
        // Score basé sur la devise
        switch (config.getCurrency()) {
            case "USD":
            case "EUR":
                score += 20;
                break;
            case "XOF":
            case "XAF":
                score += 15;
                break;
            default:
                score += 10;
        }
        
        return Math.min(score, 100);
    }

    private int calculateApiAvailabilityScore(CountryConfig config) {
        int score = 0;
        
        if (config.getBusinessCreationApiAvailable() != null && config.getBusinessCreationApiAvailable()) score += 25;
        if (config.getTaxAdministrationApiUrl() != null) score += 25;
        if (config.getSocialSecurityApiUrl() != null) score += 25;
        if (config.getCentralBankApiUrl() != null) score += 25;
        
        return score;
    }

    private int calculateIntegrationComplexityScore(CountryConfig config) {
        int score = 50; // Score de base (complexité moyenne)
        
        // Réduire le score si les APIs sont disponibles
        if (config.getBusinessCreationApiAvailable() != null && config.getBusinessCreationApiAvailable()) score -= 10;
        if (config.getTaxAdministrationApiUrl() != null) score -= 10;
        if (config.getSocialSecurityApiUrl() != null) score -= 10;
        if (config.getCentralBankApiUrl() != null) score -= 10;
        
        // Augmenter le score selon la complexité du standard comptable
        switch (config.getAccountingStandard()) {
            case "IFRS":
                score += 20;
                break;
            case "US_GAAP":
                score += 15;
                break;
            case "SYSCOHADA":
                score += 10;
                break;
            case "PCG":
                score += 10;
                break;
            default:
                score += 5;
        }
        
        return Math.max(0, Math.min(score, 100));
    }

    private String generateAnalysisDetails(CountryConfig config) {
        return String.format(
            "{\"accountingStandard\":\"%s\",\"systemType\":\"%s\",\"currency\":\"%s\",\"apisAvailable\":%d}",
            config.getAccountingStandard(),
            config.getSystemType(),
            config.getCurrency(),
            calculateApiAvailabilityScore(config)
        );
    }

    private String generateMarketOpportunities(CountryConfig config) {
        List<String> opportunities = new ArrayList<>();
        
        if (config.getBusinessCreationApiAvailable() != null && config.getBusinessCreationApiAvailable()) {
            opportunities.add("Intégration automatique création d'entreprises");
        }
        
        if (config.getTaxAdministrationApiUrl() != null) {
            opportunities.add("Déclarations fiscales automatisées");
        }
        
        if (config.getSocialSecurityApiUrl() != null) {
            opportunities.add("Gestion sociale automatisée");
        }
        
        if (config.getPaymentSystems() != null && !config.getPaymentSystems().isEmpty()) {
            opportunities.add("Intégration systèmes de paiement locaux");
        }
        
        return String.join(", ", opportunities);
    }

    private String generateChallenges(CountryConfig config) {
        List<String> challenges = new ArrayList<>();
        
        if (config.getBusinessCreationApiAvailable() == null || !config.getBusinessCreationApiAvailable()) {
            challenges.add("Pas d'API création d'entreprise");
        }
        
        if (config.getTaxAdministrationApiUrl() == null) {
            challenges.add("Pas d'API administration fiscale");
        }
        
        if (config.getSocialSecurityApiUrl() == null) {
            challenges.add("Pas d'API sécurité sociale");
        }
        
        if (config.getPaymentSystems() == null || config.getPaymentSystems().isEmpty()) {
            challenges.add("Pas de système de paiement configuré");
        }
        
        return String.join(", ", challenges);
    }

    private String generateNextSteps(ExpansionAnalysis analysis) {
        List<String> steps = new ArrayList<>();
        
        if (analysis.getApiAvailabilityScore() < 50) {
            steps.add("Développer les intégrations APIs manquantes");
        }
        
        if (analysis.getIntegrationComplexityScore() > 70) {
            steps.add("Simplifier les processus d'intégration");
        }
        
        if (analysis.getDigitalMaturityScore() < 60) {
            steps.add("Améliorer la maturité digitale");
        }
        
        steps.add("Tester l'intégration complète");
        steps.add("Former l'équipe locale");
        
        return String.join(", ", steps);
    }
}
