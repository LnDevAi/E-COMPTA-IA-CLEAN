package com.ecomptaia.repository;

import com.ecomptaia.entity.ExpansionAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpansionAnalysisRepository extends JpaRepository<ExpansionAnalysis, Long> {

    // Recherche par code pays
    Optional<ExpansionAnalysis> findByCountryCodeAndIsActiveTrue(String countryCode);

    // Recherche par recommandation
    List<ExpansionAnalysis> findByRecommendationAndIsActiveTrue(String recommendation);

    // Recherche pays recommandés
    List<ExpansionAnalysis> findByRecommendationAndIsActiveTrueOrderByTotalScoreDesc(String recommendation);

    // Recherche par score minimum
    List<ExpansionAnalysis> findByTotalScoreGreaterThanEqualAndIsActiveTrueOrderByTotalScoreDesc(Integer minScore);

    // Recherche par score de maturité digitale
    List<ExpansionAnalysis> findByDigitalMaturityScoreGreaterThanEqualAndIsActiveTrueOrderByDigitalMaturityScoreDesc(Integer minScore);

    // Recherche par score économique
    List<ExpansionAnalysis> findByEconomicScoreGreaterThanEqualAndIsActiveTrueOrderByEconomicScoreDesc(Integer minScore);

    // Recherche par score disponibilité API
    List<ExpansionAnalysis> findByApiAvailabilityScoreGreaterThanEqualAndIsActiveTrueOrderByApiAvailabilityScoreDesc(Integer minScore);

    // Recherche par complexité d'intégration
    List<ExpansionAnalysis> findByIntegrationComplexityScoreLessThanEqualAndIsActiveTrueOrderByIntegrationComplexityScoreAsc(Integer maxScore);

    // Recherche analyses récentes
    List<ExpansionAnalysis> findByAnalysisDateGreaterThanEqualAndIsActiveTrueOrderByAnalysisDateDesc(LocalDateTime since);

    // Recherche analyses par période
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.analysisDate BETWEEN :startDate AND :endDate AND ea.isActive = true ORDER BY ea.analysisDate DESC")
    List<ExpansionAnalysis> findByAnalysisDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Top pays recommandés
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.recommendation = 'RECOMMENDED' AND ea.isActive = true ORDER BY ea.totalScore DESC")
    List<ExpansionAnalysis> findTopRecommendedCountries();

    // Top pays par maturité digitale
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.isActive = true ORDER BY ea.digitalMaturityScore DESC")
    List<ExpansionAnalysis> findTopCountriesByDigitalMaturity();

    // Top pays par score économique
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.isActive = true ORDER BY ea.economicScore DESC")
    List<ExpansionAnalysis> findTopCountriesByEconomicScore();

    // Top pays par disponibilité API
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.isActive = true ORDER BY ea.apiAvailabilityScore DESC")
    List<ExpansionAnalysis> findTopCountriesByApiAvailability();

    // Pays avec intégration facile
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.integrationComplexityScore <= 30 AND ea.isActive = true ORDER BY ea.integrationComplexityScore ASC")
    List<ExpansionAnalysis> findCountriesWithEasyIntegration();

    // Pays avec intégration complexe
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.integrationComplexityScore >= 70 AND ea.isActive = true ORDER BY ea.integrationComplexityScore DESC")
    List<ExpansionAnalysis> findCountriesWithComplexIntegration();

    // Statistiques par recommandation
    @Query("SELECT ea.recommendation, COUNT(ea) FROM ExpansionAnalysis ea WHERE ea.isActive = true GROUP BY ea.recommendation")
    List<Object[]> getStatisticsByRecommendation();

    // Statistiques par score de maturité digitale
    @Query("SELECT " +
           "CASE " +
           "  WHEN ea.digitalMaturityScore >= 80 THEN 'HIGH' " +
           "  WHEN ea.digitalMaturityScore >= 60 THEN 'MEDIUM' " +
           "  ELSE 'LOW' " +
           "END as maturity_level, " +
           "COUNT(ea) " +
           "FROM ExpansionAnalysis ea WHERE ea.isActive = true GROUP BY " +
           "CASE " +
           "  WHEN ea.digitalMaturityScore >= 80 THEN 'HIGH' " +
           "  WHEN ea.digitalMaturityScore >= 60 THEN 'MEDIUM' " +
           "  ELSE 'LOW' " +
           "END")
    List<Object[]> getStatisticsByDigitalMaturityLevel();

    // Statistiques par score économique
    @Query("SELECT " +
           "CASE " +
           "  WHEN ea.economicScore >= 80 THEN 'HIGH' " +
           "  WHEN ea.economicScore >= 60 THEN 'MEDIUM' " +
           "  ELSE 'LOW' " +
           "END as economic_level, " +
           "COUNT(ea) " +
           "FROM ExpansionAnalysis ea WHERE ea.isActive = true GROUP BY " +
           "CASE " +
           "  WHEN ea.economicScore >= 80 THEN 'HIGH' " +
           "  WHEN ea.economicScore >= 60 THEN 'MEDIUM' " +
           "  ELSE 'LOW' " +
           "END")
    List<Object[]> getStatisticsByEconomicLevel();

    // Statistiques par complexité d'intégration
    @Query("SELECT " +
           "CASE " +
           "  WHEN ea.integrationComplexityScore <= 30 THEN 'EASY' " +
           "  WHEN ea.integrationComplexityScore <= 60 THEN 'MEDIUM' " +
           "  ELSE 'COMPLEX' " +
           "END as complexity_level, " +
           "COUNT(ea) " +
           "FROM ExpansionAnalysis ea WHERE ea.isActive = true GROUP BY " +
           "CASE " +
           "  WHEN ea.integrationComplexityScore <= 30 THEN 'EASY' " +
           "  WHEN ea.integrationComplexityScore <= 60 THEN 'MEDIUM' " +
           "  ELSE 'COMPLEX' " +
           "END")
    List<Object[]> getStatisticsByIntegrationComplexity();

    // Recherche pays avec opportunités de marché
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.marketOpportunities IS NOT NULL AND ea.marketOpportunities != '' AND ea.isActive = true")
    List<ExpansionAnalysis> findCountriesWithMarketOpportunities();

    // Recherche pays avec défis identifiés
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.challenges IS NOT NULL AND ea.challenges != '' AND ea.isActive = true")
    List<ExpansionAnalysis> findCountriesWithChallenges();

    // Recherche pays avec prochaines étapes définies
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.nextSteps IS NOT NULL AND ea.nextSteps != '' AND ea.isActive = true")
    List<ExpansionAnalysis> findCountriesWithNextSteps();

    // Recherche analyses mises à jour récemment
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.updatedAt >= :since AND ea.isActive = true ORDER BY ea.updatedAt DESC")
    List<ExpansionAnalysis> findRecentlyUpdated(@Param("since") LocalDateTime since);

    // Recherche pays avec analyse détaillée
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.analysisDetails IS NOT NULL AND ea.analysisDetails != '' AND ea.isActive = true")
    List<ExpansionAnalysis> findCountriesWithDetailedAnalysis();

    // Compter les analyses par période
    @Query("SELECT DATE(ea.analysisDate) as analysis_date, COUNT(ea) FROM ExpansionAnalysis ea " +
           "WHERE ea.analysisDate >= :since AND ea.isActive = true " +
           "GROUP BY DATE(ea.analysisDate) ORDER BY analysis_date DESC")
    List<Object[]> getAnalysisCountByDate(@Param("since") LocalDateTime since);

    // Moyenne des scores par recommandation
    @Query("SELECT ea.recommendation, " +
           "AVG(ea.digitalMaturityScore) as avg_digital, " +
           "AVG(ea.economicScore) as avg_economic, " +
           "AVG(ea.apiAvailabilityScore) as avg_api, " +
           "AVG(ea.integrationComplexityScore) as avg_integration, " +
           "AVG(ea.totalScore) as avg_total " +
           "FROM ExpansionAnalysis ea WHERE ea.isActive = true GROUP BY ea.recommendation")
    List<Object[]> getAverageScoresByRecommendation();

    // Recherche pays avec score total élevé
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.totalScore >= 85 AND ea.isActive = true ORDER BY ea.totalScore DESC")
    List<ExpansionAnalysis> findHighScoringCountries();

    // Recherche pays avec score total faible
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.totalScore <= 40 AND ea.isActive = true ORDER BY ea.totalScore ASC")
    List<ExpansionAnalysis> findLowScoringCountries();

    // Recherche pays avec progression récente
    @Query("SELECT ea FROM ExpansionAnalysis ea WHERE ea.updatedAt >= :since AND ea.totalScore > ea.digitalMaturityScore AND ea.isActive = true ORDER BY ea.updatedAt DESC")
    List<ExpansionAnalysis> findCountriesWithRecentProgress(@Param("since") LocalDateTime since);
}





