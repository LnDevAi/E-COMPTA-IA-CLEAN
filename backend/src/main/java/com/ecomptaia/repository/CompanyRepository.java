package com.ecomptaia.repository;

import com.ecomptaia.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    // Recherche par nom d'entreprise
    Optional<Company> findByName(String name);

    // Recherche par numéro SIRET
    Optional<Company> findBySiret(String siret);

    // Recherche par numéro de TVA
    Optional<Company> findByVatNumber(String vatNumber);

    // Recherche par code pays
    List<Company> findByCountryCode(String countryCode);

    // Recherche par devise
    List<Company> findByCurrency(String currency);

    // Recherche par standard comptable
    List<Company> findByAccountingStandard(String accountingStandard);

    // Recherche par type de système OHADA
    List<Company> findByOhadaSystemType(String ohadaSystemType);

    // Recherche des entreprises actives
    List<Company> findByIsActiveTrue();

    // Recherche par secteur d'activité
    List<Company> findByIndustry(String industry);

    // Recherche par type d'entreprise
    List<Company> findByBusinessType(String businessType);

    // Recherche par ville
    List<Company> findByCity(String city);

    // Recherche par code postal
    List<Company> findByPostalCode(String postalCode);

    // Recherche des entreprises par plage de revenus
    @Query("SELECT c FROM Company c WHERE c.annualRevenue BETWEEN :minRevenue AND :maxRevenue")
    List<Company> findByRevenueRange(@Param("minRevenue") Double minRevenue, @Param("maxRevenue") Double maxRevenue);

    // Recherche des entreprises par nombre d'employés
    @Query("SELECT c FROM Company c WHERE c.employeeCount BETWEEN :minEmployees AND :maxEmployees")
    List<Company> findByEmployeeCountRange(@Param("minEmployees") Integer minEmployees, @Param("maxEmployees") Integer maxEmployees);

    // Recherche des entreprises par date de création
    @Query("SELECT c FROM Company c WHERE c.createdAt >= :since")
    List<Company> findByCreatedAfter(@Param("since") java.time.LocalDateTime since);

    // Recherche des entreprises par localisation
    @Query("SELECT c FROM Company c WHERE c.countryCode = :countryCode AND c.city = :city")
    List<Company> findByCountryAndCity(@Param("countryCode") String countryCode, @Param("city") String city);

    // Recherche des entreprises par critères multiples
    @Query("SELECT c FROM Company c WHERE c.countryCode = :countryCode AND c.accountingStandard = :accountingStandard AND c.isActive = true")
    List<Company> findByCountryAndAccountingStandard(@Param("countryCode") String countryCode, @Param("accountingStandard") String accountingStandard);

    // Recherche des entreprises par système OHADA
    @Query("SELECT c FROM Company c WHERE c.ohadaSystemType IS NOT NULL")
    List<Company> findOHADACompanies();

    // Recherche des entreprises par standard comptable spécifique
    @Query("SELECT c FROM Company c WHERE c.accountingStandard IN :standards")
    List<Company> findByAccountingStandards(@Param("standards") List<String> standards);

    // Recherche des entreprises par devises
    @Query("SELECT c FROM Company c WHERE c.currency IN :currencies")
    List<Company> findByCurrencies(@Param("currencies") List<String> currencies);

    // Recherche des entreprises par secteurs
    @Query("SELECT c FROM Company c WHERE c.industry IN :industries")
    List<Company> findByIndustries(@Param("industries") List<String> industries);

    // Recherche des entreprises par taille (PME, Grande entreprise, etc.)
    @Query("SELECT c FROM Company c WHERE c.businessType IN :businessTypes")
    List<Company> findByBusinessTypes(@Param("businessTypes") List<String> businessTypes);

    // Compter les entreprises par pays
    @Query("SELECT c.countryCode, COUNT(c) FROM Company c GROUP BY c.countryCode")
    List<Object[]> countCompaniesByCountry();

    // Compter les entreprises par devise
    @Query("SELECT c.currency, COUNT(c) FROM Company c GROUP BY c.currency")
    List<Object[]> countCompaniesByCurrency();

    // Compter les entreprises par standard comptable
    @Query("SELECT c.accountingStandard, COUNT(c) FROM Company c GROUP BY c.accountingStandard")
    List<Object[]> countCompaniesByAccountingStandard();

    // Compter les entreprises par secteur
    @Query("SELECT c.industry, COUNT(c) FROM Company c GROUP BY c.industry")
    List<Object[]> countCompaniesByIndustry();

    // Compter les entreprises par type
    @Query("SELECT c.businessType, COUNT(c) FROM Company c GROUP BY c.businessType")
    List<Object[]> countCompaniesByBusinessType();

    // Vérifier si un SIRET existe
    boolean existsBySiret(String siret);

    // Vérifier si un numéro de TVA existe
    boolean existsByVatNumber(String vatNumber);

    // Vérifier si un nom d'entreprise existe
    boolean existsByName(String name);

    // Recherche des entreprises par indicateurs de performance
    @Query("SELECT c FROM Company c WHERE c.annualRevenue >= :minRevenue ORDER BY c.annualRevenue DESC")
    List<Company> findTopPerformers(@Param("minRevenue") Double minRevenue);

    // Recherche des entreprises par croissance
    @Query("SELECT c FROM Company c WHERE c.employeeCount >= :minEmployees ORDER BY c.employeeCount DESC")
    List<Company> findGrowingCompanies(@Param("minEmployees") Integer minEmployees);

    // Recherche des entreprises par conformité OHADA
    @Query("SELECT c FROM Company c WHERE c.ohadaSystemType IS NOT NULL AND c.accountingStandard = 'SYSCOHADA'")
    List<Company> findOHADACompliantCompanies();

    // Recherche des entreprises par internationalisation
    @Query("SELECT c FROM Company c WHERE c.countryCode != 'FR' AND c.currency != 'EUR'")
    List<Company> findInternationalCompanies();
}
