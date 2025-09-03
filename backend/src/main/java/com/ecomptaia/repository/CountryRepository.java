package com.ecomptaia.repository;

import com.ecomptaia.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    // Recherche par code pays
    Optional<Country> findByCode(String code);

    // Recherche par nom de pays
    Optional<Country> findByName(String name);

    // Recherche par code devise
    List<Country> findByCurrencyCode(String currencyCode);

    // Recherche par standard comptable
    List<Country> findByAccountingStandard(String accountingStandard);

    // Recherche des pays actifs
    List<Country> findByIsActiveTrue();

    // Recherche par région
    List<Country> findByRegion(String region);

    // Recherche par langue officielle
    List<Country> findByOfficialLanguage(String officialLanguage);

    // Recherche des pays par zone OHADA
    @Query("SELECT c FROM Country c WHERE c.ohadaZone IS NOT NULL")
    List<Country> findOHADACountries();

    // Recherche des pays par zone monétaire
    @Query("SELECT c FROM Country c WHERE c.currencyCode = :currencyCode")
    List<Country> findByCurrency(@Param("currencyCode") String currencyCode);

    // Recherche des pays par fuseau horaire
    List<Country> findByTimezone(String timezone);

    // Recherche des pays par continent
    List<Country> findByContinent(String continent);

    // Recherche des pays par population (plage)
    @Query("SELECT c FROM Country c WHERE c.population BETWEEN :minPopulation AND :maxPopulation")
    List<Country> findByPopulationRange(@Param("minPopulation") Long minPopulation, @Param("maxPopulation") Long maxPopulation);

    // Recherche des pays par PIB (plage)
    @Query("SELECT c FROM Country c WHERE c.gdp BETWEEN :minGdp AND :maxGdp")
    List<Country> findByGdpRange(@Param("minGdp") Double minGdp, @Param("maxGdp") Double maxGdp);

    // Recherche des pays par indicateur de développement
    List<Country> findByDevelopmentIndexGreaterThan(Double developmentIndex);

    // Recherche des pays par type d'économie
    List<Country> findByEconomyType(String economyType);

    // Recherche des pays par système fiscal
    List<Country> findByTaxSystem(String taxSystem);

    // Recherche des pays par système de sécurité sociale
    List<Country> findBySocialSecuritySystem(String socialSecuritySystem);

    // Recherche des pays par réglementation comptable
    @Query("SELECT c FROM Country c WHERE c.accountingRegulations LIKE %:regulation%")
    List<Country> findByAccountingRegulation(@Param("regulation") String regulation);

    // Recherche des pays par normes fiscales
    @Query("SELECT c FROM Country c WHERE c.taxStandards LIKE %:standard%")
    List<Country> findByTaxStandard(@Param("standard") String standard);

    // Compter les pays par devise
    @Query("SELECT c.currencyCode, COUNT(c) FROM Country c GROUP BY c.currencyCode")
    List<Object[]> countCountriesByCurrency();

    // Compter les pays par standard comptable
    @Query("SELECT c.accountingStandard, COUNT(c) FROM Country c GROUP BY c.accountingStandard")
    List<Object[]> countCountriesByAccountingStandard();

    // Compter les pays par région
    @Query("SELECT c.region, COUNT(c) FROM Country c GROUP BY c.region")
    List<Object[]> countCountriesByRegion();

    // Vérifier si un code pays existe
    boolean existsByCode(String code);

    // Vérifier si un nom de pays existe
    boolean existsByName(String name);

    // Recherche des pays par indicateurs économiques
    @Query("SELECT c FROM Country c WHERE c.inflationRate <= :maxInflation AND c.unemploymentRate <= :maxUnemployment")
    List<Country> findStableEconomies(@Param("maxInflation") Double maxInflation, @Param("maxUnemployment") Double maxUnemployment);

    // Recherche des pays par facilité de faire des affaires
    @Query("SELECT c FROM Country c WHERE c.easeOfDoingBusinessIndex >= :minIndex ORDER BY c.easeOfDoingBusinessIndex DESC")
    List<Country> findBusinessFriendlyCountries(@Param("minIndex") Integer minIndex);

    // Recherche des pays par corruption
    @Query("SELECT c FROM Country c WHERE c.corruptionIndex <= :maxCorruption ORDER BY c.corruptionIndex ASC")
    List<Country> findLowCorruptionCountries(@Param("maxCorruption") Integer maxCorruption);
}
