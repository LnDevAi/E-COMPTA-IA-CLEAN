package com.ecomptaia.repository;

import com.ecomptaia.entity.CountryConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryConfigRepository extends JpaRepository<CountryConfig, Long> {

    // Recherche par code pays
    Optional<CountryConfig> findByCountryCodeAndIsActiveTrue(String countryCode);

    // Recherche par standard comptable
    List<CountryConfig> findByAccountingStandardAndIsActiveTrue(String accountingStandard);

    // Recherche par type de système
    List<CountryConfig> findBySystemTypeAndIsActiveTrue(String systemType);

    // Recherche par devise
    List<CountryConfig> findByCurrencyAndIsActiveTrue(String currency);

    // Recherche par statut
    List<CountryConfig> findByStatusAndIsActiveTrue(String status);

    // Recherche pays avec API de création d'entreprise disponible
    List<CountryConfig> findByBusinessCreationApiAvailableTrueAndIsActiveTrue();

    // Recherche pays avec API fiscale disponible
    List<CountryConfig> findByTaxAdministrationApiUrlIsNotNullAndIsActiveTrue();

    // Recherche pays avec API sécurité sociale disponible
    List<CountryConfig> findBySocialSecurityApiUrlIsNotNullAndIsActiveTrue();

    // Recherche pays prioritaires (OHADA + autres)
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.countryCode IN ('BF', 'CI', 'SN', 'ML', 'NE', 'TD', 'BJ', 'TG', 'CM', 'CF', 'CG', 'CD', 'GA', 'GQ', 'TD') AND cc.isActive = true ORDER BY cc.countryCode")
    List<CountryConfig> findOHADACountries();

    // Recherche pays développés
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.countryCode IN ('FR', 'US', 'GB', 'DE', 'CA', 'AU', 'JP', 'SG') AND cc.isActive = true ORDER BY cc.countryCode")
    List<CountryConfig> findDevelopedCountries();

    // Recherche par score de maturité digitale (via ExpansionAnalysis)
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.countryCode IN (SELECT ea.countryCode FROM ExpansionAnalysis ea WHERE ea.digitalMaturityScore >= :minScore) AND cc.isActive = true")
    List<CountryConfig> findByDigitalMaturityScoreGreaterThan(@Param("minScore") Integer minScore);

    // Recherche pays recommandés pour expansion
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.countryCode IN (SELECT ea.countryCode FROM ExpansionAnalysis ea WHERE ea.recommendation = 'RECOMMENDED') AND cc.isActive = true")
    List<CountryConfig> findRecommendedForExpansion();

    // Statistiques par standard comptable
    @Query("SELECT cc.accountingStandard, COUNT(cc) FROM CountryConfig cc WHERE cc.isActive = true GROUP BY cc.accountingStandard")
    List<Object[]> getStatisticsByAccountingStandard();

    // Statistiques par devise
    @Query("SELECT cc.currency, COUNT(cc) FROM CountryConfig cc WHERE cc.isActive = true GROUP BY cc.currency")
    List<Object[]> getStatisticsByCurrency();

    // Recherche pays avec APIs complètes
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.businessCreationApiAvailable = true AND cc.taxAdministrationApiUrl IS NOT NULL AND cc.socialSecurityApiUrl IS NOT NULL AND cc.isActive = true")
    List<CountryConfig> findCountriesWithCompleteApis();

    // Recherche pays avec système de paiement configuré
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.paymentSystems IS NOT NULL AND cc.paymentSystems != '' AND cc.isActive = true")
    List<CountryConfig> findCountriesWithPaymentSystems();

    // Recherche par continent (approximatif via codes pays)
    @Query("SELECT cc FROM CountryConfig cc WHERE " +
           "(cc.countryCode IN ('BF', 'CI', 'SN', 'ML', 'NE', 'TD', 'BJ', 'TG', 'CM', 'CF', 'CG', 'CD', 'GA', 'GQ', 'TD') AND :continent = 'AFRICA') OR " +
           "(cc.countryCode IN ('FR', 'DE', 'GB', 'IT', 'ES', 'NL', 'BE', 'CH', 'AT', 'SE', 'NO', 'DK', 'FI') AND :continent = 'EUROPE') OR " +
           "(cc.countryCode IN ('US', 'CA', 'MX') AND :continent = 'AMERICA') OR " +
           "(cc.countryCode IN ('CN', 'JP', 'KR', 'IN', 'SG', 'AU', 'NZ') AND :continent = 'ASIA') " +
           "AND cc.isActive = true")
    List<CountryConfig> findByContinent(@Param("continent") String continent);

    // Recherche pays avec réglementations configurées
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.regulations IS NOT NULL AND cc.regulations != '' AND cc.isActive = true")
    List<CountryConfig> findCountriesWithRegulations();

    // Recherche pays avec indicateurs économiques
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.economicIndicators IS NOT NULL AND cc.economicIndicators != '' AND cc.isActive = true")
    List<CountryConfig> findCountriesWithEconomicIndicators();

    // Recherche par version de configuration
    List<CountryConfig> findByVersionAndIsActiveTrue(String version);

    // Recherche pays créés récemment
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.createdAt >= :since AND cc.isActive = true ORDER BY cc.createdAt DESC")
    List<CountryConfig> findRecentlyCreated(@Param("since") java.time.LocalDateTime since);

    // Recherche pays mis à jour récemment
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.updatedAt >= :since AND cc.isActive = true ORDER BY cc.updatedAt DESC")
    List<CountryConfig> findRecentlyUpdated(@Param("since") java.time.LocalDateTime since);

    // Compter les pays par statut
    @Query("SELECT cc.status, COUNT(cc) FROM CountryConfig cc WHERE cc.isActive = true GROUP BY cc.status")
    List<Object[]> getStatisticsByStatus();

    // Compter les pays par type de système
    @Query("SELECT cc.systemType, COUNT(cc) FROM CountryConfig cc WHERE cc.isActive = true GROUP BY cc.systemType")
    List<Object[]> getStatisticsBySystemType();

    // Recherche pays avec configuration complète
    @Query("SELECT cc FROM CountryConfig cc WHERE " +
           "cc.businessCreationPlatform IS NOT NULL AND " +
           "cc.taxAdministrationWebsite IS NOT NULL AND " +
           "cc.socialSecurityWebsite IS NOT NULL AND " +
           "cc.centralBankWebsite IS NOT NULL AND " +
           "cc.paymentSystems IS NOT NULL AND " +
           "cc.officialApis IS NOT NULL AND " +
           "cc.isActive = true")
    List<CountryConfig> findCountriesWithCompleteConfiguration();

    // Recherche pays pour test d'intégration
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.status = 'TESTING' AND cc.isActive = true")
    List<CountryConfig> findCountriesForIntegrationTesting();

    // Recherche pays actifs avec APIs
    @Query("SELECT cc FROM CountryConfig cc WHERE cc.status = 'ACTIVE' AND " +
           "(cc.businessCreationApiAvailable = true OR cc.taxAdministrationApiUrl IS NOT NULL OR cc.socialSecurityApiUrl IS NOT NULL) " +
           "AND cc.isActive = true")
    List<CountryConfig> findActiveCountriesWithApis();
}




