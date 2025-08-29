package com.ecomptaia.repository;

import com.ecomptaia.entity.CompanySubscription;
import com.ecomptaia.entity.SubscriptionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionUsageRepository extends JpaRepository<SubscriptionUsage, Long> {
    
    // Utilisation par souscription
    List<SubscriptionUsage> findByCompanySubscription(CompanySubscription companySubscription);
    
    // Utilisation par souscription et date
    List<SubscriptionUsage> findByCompanySubscriptionAndUsageDate(CompanySubscription companySubscription, LocalDate usageDate);
    
    // Utilisation par souscription et code de fonctionnalité
    Optional<SubscriptionUsage> findByCompanySubscriptionAndFeatureCode(CompanySubscription companySubscription, String featureCode);
    
    // Utilisation par code de fonctionnalité
    List<SubscriptionUsage> findByFeatureCode(String featureCode);
    
    // Utilisation par nom de fonctionnalité
    List<SubscriptionUsage> findByFeatureNameContainingIgnoreCase(String featureName);
    
    // Utilisation par date
    List<SubscriptionUsage> findByUsageDate(LocalDate usageDate);
    
    // Utilisation par période
    List<SubscriptionUsage> findByUsageDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Utilisation dépassant la limite
    List<SubscriptionUsage> findByIsOverLimitTrue();
    
    // Utilisation proche de la limite (80% ou plus)
    @Query("SELECT su FROM SubscriptionUsage su WHERE su.usagePercentage >= 80.0 AND su.limitValue IS NOT NULL")
    List<SubscriptionUsage> findNearLimitUsage();
    
    // Utilisation avec limite
    List<SubscriptionUsage> findByLimitValueIsNotNull();
    
    // Utilisation sans limite
    List<SubscriptionUsage> findByLimitValueIsNull();
    
    // Utilisation par souscription et dépassement de limite
    List<SubscriptionUsage> findByCompanySubscriptionAndIsOverLimitTrue(CompanySubscription companySubscription);
    
    // Utilisation par souscription et proche de la limite
    @Query("SELECT su FROM SubscriptionUsage su WHERE su.companySubscription = :subscription AND su.usagePercentage >= 80.0 AND su.limitValue IS NOT NULL")
    List<SubscriptionUsage> findNearLimitUsageBySubscription(@Param("subscription") CompanySubscription companySubscription);
    
    // Utilisation par entreprise
    @Query("SELECT su FROM SubscriptionUsage su WHERE su.companySubscription.company.id = :companyId")
    List<SubscriptionUsage> findByCompanyId(@Param("companyId") Long companyId);
    
    // Utilisation par entreprise et date
    @Query("SELECT su FROM SubscriptionUsage su WHERE su.companySubscription.company.id = :companyId AND su.usageDate = :usageDate")
    List<SubscriptionUsage> findByCompanyIdAndUsageDate(@Param("companyId") Long companyId, @Param("usageDate") LocalDate usageDate);
    
    // Utilisation par entreprise et période
    @Query("SELECT su FROM SubscriptionUsage su WHERE su.companySubscription.company.id = :companyId AND su.usageDate BETWEEN :startDate AND :endDate")
    List<SubscriptionUsage> findByCompanyIdAndUsageDateBetween(@Param("companyId") Long companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Utilisation par entreprise et fonctionnalité
    @Query("SELECT su FROM SubscriptionUsage su WHERE su.companySubscription.company.id = :companyId AND su.featureCode = :featureCode")
    List<SubscriptionUsage> findByCompanyIdAndFeatureCode(@Param("companyId") Long companyId, @Param("featureCode") String featureCode);
    
    // Compter l'utilisation par fonctionnalité
    @Query("SELECT su.featureCode, su.featureName, COUNT(su), SUM(su.usageCount) FROM SubscriptionUsage su GROUP BY su.featureCode, su.featureName")
    List<Object[]> countUsageByFeature();
    
    // Compter l'utilisation par entreprise
    @Query("SELECT su.companySubscription.company.name, COUNT(su), SUM(su.usageCount) FROM SubscriptionUsage su GROUP BY su.companySubscription.company.name")
    List<Object[]> countUsageByCompany();
    
    // Compter l'utilisation par plan
    @Query("SELECT su.companySubscription.subscriptionPlan.planName, COUNT(su), SUM(su.usageCount) FROM SubscriptionUsage su GROUP BY su.companySubscription.subscriptionPlan.planName")
    List<Object[]> countUsageByPlan();
    
    // Compter l'utilisation par date
    @Query("SELECT su.usageDate, COUNT(su), SUM(su.usageCount) FROM SubscriptionUsage su GROUP BY su.usageDate ORDER BY su.usageDate DESC")
    List<Object[]> countUsageByDate();
    
    // Compter l'utilisation par mois
    @Query("SELECT YEAR(su.usageDate), MONTH(su.usageDate), COUNT(su), SUM(su.usageCount) FROM SubscriptionUsage su GROUP BY YEAR(su.usageDate), MONTH(su.usageDate) ORDER BY YEAR(su.usageDate) DESC, MONTH(su.usageDate) DESC")
    List<Object[]> countUsageByMonth();
    
    // Fonctionnalités les plus utilisées
    @Query("SELECT su.featureCode, su.featureName, SUM(su.usageCount) as totalUsage FROM SubscriptionUsage su GROUP BY su.featureCode, su.featureName ORDER BY totalUsage DESC")
    List<Object[]> findMostUsedFeatures();
    
    // Entreprises les plus actives
    @Query("SELECT su.companySubscription.company.name, SUM(su.usageCount) as totalUsage FROM SubscriptionUsage su GROUP BY su.companySubscription.company.name ORDER BY totalUsage DESC")
    List<Object[]> findMostActiveCompanies();
    
    // Utilisation dépassant la limite par fonctionnalité
    @Query("SELECT su.featureCode, su.featureName, COUNT(su) as overLimitCount FROM SubscriptionUsage su WHERE su.isOverLimit = true GROUP BY su.featureCode, su.featureName ORDER BY overLimitCount DESC")
    List<Object[]> findOverLimitUsageByFeature();
    
    // Utilisation dépassant la limite par entreprise
    @Query("SELECT su.companySubscription.company.name, COUNT(su) as overLimitCount FROM SubscriptionUsage su WHERE su.isOverLimit = true GROUP BY su.companySubscription.company.name ORDER BY overLimitCount DESC")
    List<Object[]> findOverLimitUsageByCompany();
    
    // Statistiques d'utilisation
    @Query("SELECT " +
           "COUNT(su) as totalUsageRecords, " +
           "SUM(su.usageCount) as totalUsage, " +
           "COUNT(CASE WHEN su.isOverLimit = true THEN 1 END) as overLimitRecords, " +
           "COUNT(CASE WHEN su.limitValue IS NOT NULL THEN 1 END) as limitedFeatures, " +
           "COUNT(DISTINCT su.featureCode) as uniqueFeatures, " +
           "COUNT(DISTINCT su.companySubscription.company) as activeCompanies " +
           "FROM SubscriptionUsage su")
    Object[] getUsageStatistics();
    
    // Statistiques d'utilisation par entreprise
    @Query("SELECT su.companySubscription.company.name, " +
           "COUNT(su) as usageRecords, " +
           "SUM(su.usageCount) as totalUsage, " +
           "COUNT(CASE WHEN su.isOverLimit = true THEN 1 END) as overLimitRecords, " +
           "COUNT(DISTINCT su.featureCode) as uniqueFeatures " +
           "FROM SubscriptionUsage su GROUP BY su.companySubscription.company.name")
    List<Object[]> getUsageStatisticsByCompany();
    
    // Statistiques d'utilisation par fonctionnalité
    @Query("SELECT su.featureCode, su.featureName, " +
           "COUNT(su) as usageRecords, " +
           "SUM(su.usageCount) as totalUsage, " +
           "COUNT(CASE WHEN su.isOverLimit = true THEN 1 END) as overLimitRecords, " +
           "COUNT(DISTINCT su.companySubscription.company) as activeCompanies " +
           "FROM SubscriptionUsage su GROUP BY su.featureCode, su.featureName")
    List<Object[]> getUsageStatisticsByFeature();
    
    // Recherche avancée avec critères multiples
    @Query("SELECT su FROM SubscriptionUsage su WHERE " +
           "(:companySubscription IS NULL OR su.companySubscription = :companySubscription) AND " +
           "(:featureCode IS NULL OR su.featureCode = :featureCode) AND " +
           "(:isOverLimit IS NULL OR su.isOverLimit = :isOverLimit) AND " +
           "(:hasLimit IS NULL OR (:hasLimit = true AND su.limitValue IS NOT NULL) OR (:hasLimit = false AND su.limitValue IS NULL)) AND " +
           "(:startDate IS NULL OR su.usageDate >= :startDate) AND " +
           "(:endDate IS NULL OR su.usageDate <= :endDate) AND " +
           "(:minUsage IS NULL OR su.usageCount >= :minUsage) AND " +
           "(:maxUsage IS NULL OR su.usageCount <= :maxUsage) " +
           "ORDER BY su.usageDate DESC, su.usageCount DESC")
    List<SubscriptionUsage> findUsageWithCriteria(
            @Param("companySubscription") CompanySubscription companySubscription,
            @Param("featureCode") String featureCode,
            @Param("isOverLimit") Boolean isOverLimit,
            @Param("hasLimit") Boolean hasLimit,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minUsage") Integer minUsage,
            @Param("maxUsage") Integer maxUsage
    );
    
    // Utilisation par région (devise de la souscription)
    @Query("SELECT su.companySubscription.localCurrency, COUNT(su), SUM(su.usageCount) FROM SubscriptionUsage su GROUP BY su.companySubscription.localCurrency")
    List<Object[]> getUsageByRegion();
    
    // Tendances d'utilisation (par mois)
    @Query("SELECT YEAR(su.usageDate), MONTH(su.usageDate), " +
           "COUNT(su) as usageRecords, " +
           "SUM(su.usageCount) as totalUsage, " +
           "COUNT(CASE WHEN su.isOverLimit = true THEN 1 END) as overLimitRecords " +
           "FROM SubscriptionUsage su " +
           "GROUP BY YEAR(su.usageDate), MONTH(su.usageDate) " +
           "ORDER BY YEAR(su.usageDate) DESC, MONTH(su.usageDate) DESC")
    List<Object[]> getUsageTrends();
    
    // Alertes d'utilisation (proche de la limite ou dépassement)
    @Query("SELECT su FROM SubscriptionUsage su WHERE " +
           "(su.usagePercentage >= 80.0 AND su.limitValue IS NOT NULL) OR " +
           "su.isOverLimit = true " +
           "ORDER BY su.usagePercentage DESC")
    List<SubscriptionUsage> findUsageAlerts();
}
