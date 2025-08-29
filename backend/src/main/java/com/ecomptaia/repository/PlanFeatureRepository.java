package com.ecomptaia.repository;

import com.ecomptaia.entity.PlanFeature;
import com.ecomptaia.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanFeatureRepository extends JpaRepository<PlanFeature, Long> {
    
    // Fonctionnalités par plan
    List<PlanFeature> findBySubscriptionPlan(SubscriptionPlan subscriptionPlan);
    
    // Fonctionnalités par plan et statut
    List<PlanFeature> findBySubscriptionPlanAndStatus(SubscriptionPlan subscriptionPlan, PlanFeature.FeatureStatus status);
    
    // Fonctionnalités actives par plan
    List<PlanFeature> findBySubscriptionPlanAndIsIncludedTrueAndStatus(SubscriptionPlan subscriptionPlan, PlanFeature.FeatureStatus status);
    
    // Fonctionnalités par code
    Optional<PlanFeature> findByFeatureCode(String featureCode);
    
    // Fonctionnalités par nom
    List<PlanFeature> findByFeatureNameContainingIgnoreCase(String featureName);
    
    // Fonctionnalités par type
    List<PlanFeature> findByFeatureType(PlanFeature.FeatureType featureType);
    
    // Fonctionnalités incluses
    List<PlanFeature> findByIsIncludedTrue();
    
    // Fonctionnalités non incluses
    List<PlanFeature> findByIsIncludedFalse();
    
    // Fonctionnalités par statut
    List<PlanFeature> findByStatus(PlanFeature.FeatureStatus status);
    
    // Fonctionnalités avec limite
    List<PlanFeature> findByLimitValueIsNotNull();
    
    // Fonctionnalités sans limite
    List<PlanFeature> findByLimitValueIsNull();
    
    // Fonctionnalités par ordre de tri
    List<PlanFeature> findBySubscriptionPlanOrderBySortOrderAsc(SubscriptionPlan subscriptionPlan);
    
    // Fonctionnalités par plan et type
    List<PlanFeature> findBySubscriptionPlanAndFeatureType(SubscriptionPlan subscriptionPlan, PlanFeature.FeatureType featureType);
    
    // Fonctionnalités par plan et incluses
    List<PlanFeature> findBySubscriptionPlanAndIsIncludedTrue(SubscriptionPlan subscriptionPlan);
    
    // Fonctionnalités par plan et non incluses
    List<PlanFeature> findBySubscriptionPlanAndIsIncludedFalse(SubscriptionPlan subscriptionPlan);
    
    // Recherche par description
    List<PlanFeature> findByDescriptionContainingIgnoreCase(String description);
    
    // Fonctionnalités avec limite supérieure à une valeur
    List<PlanFeature> findByLimitValueGreaterThan(Integer limitValue);
    
    // Fonctionnalités avec limite inférieure à une valeur
    List<PlanFeature> findByLimitValueLessThan(Integer limitValue);
    
    // Fonctionnalités avec limite entre deux valeurs
    List<PlanFeature> findByLimitValueBetween(Integer minLimit, Integer maxLimit);
    
    // Compter les fonctionnalités par plan
    @Query("SELECT pf.subscriptionPlan.id, COUNT(pf) FROM PlanFeature pf GROUP BY pf.subscriptionPlan.id")
    List<Object[]> countFeaturesByPlan();
    
    // Compter les fonctionnalités par type
    @Query("SELECT pf.featureType, COUNT(pf) FROM PlanFeature pf GROUP BY pf.featureType")
    List<Object[]> countFeaturesByType();
    
    // Compter les fonctionnalités par statut
    @Query("SELECT pf.status, COUNT(pf) FROM PlanFeature pf GROUP BY pf.status")
    List<Object[]> countFeaturesByStatus();
    
    // Compter les fonctionnalités incluses vs non incluses
    @Query("SELECT pf.isIncluded, COUNT(pf) FROM PlanFeature pf GROUP BY pf.isIncluded")
    List<Object[]> countFeaturesByInclusion();
    
    // Fonctionnalités les plus communes (par code)
    @Query("SELECT pf.featureCode, COUNT(pf) as usageCount FROM PlanFeature pf GROUP BY pf.featureCode ORDER BY usageCount DESC")
    List<Object[]> findMostCommonFeatures();
    
    // Fonctionnalités par plan avec statistiques
    @Query("SELECT pf.subscriptionPlan.planName, " +
           "COUNT(pf) as totalFeatures, " +
           "COUNT(CASE WHEN pf.isIncluded = true THEN 1 END) as includedFeatures, " +
           "COUNT(CASE WHEN pf.limitValue IS NOT NULL THEN 1 END) as limitedFeatures " +
           "FROM PlanFeature pf GROUP BY pf.subscriptionPlan.planName")
    List<Object[]> getFeatureStatisticsByPlan();
    
    // Recherche avancée avec critères multiples
    @Query("SELECT pf FROM PlanFeature pf WHERE " +
           "(:subscriptionPlan IS NULL OR pf.subscriptionPlan = :subscriptionPlan) AND " +
           "(:featureType IS NULL OR pf.featureType = :featureType) AND " +
           "(:isIncluded IS NULL OR pf.isIncluded = :isIncluded) AND " +
           "(:status IS NULL OR pf.status = :status) AND " +
           "(:hasLimit IS NULL OR (:hasLimit = true AND pf.limitValue IS NOT NULL) OR (:hasLimit = false AND pf.limitValue IS NULL)) AND " +
           "(:featureCode IS NULL OR pf.featureCode LIKE %:featureCode%) AND " +
           "(:featureName IS NULL OR pf.featureName LIKE %:featureName%) " +
           "ORDER BY pf.subscriptionPlan.sortOrder ASC, pf.sortOrder ASC")
    List<PlanFeature> findFeaturesWithCriteria(
            @Param("subscriptionPlan") SubscriptionPlan subscriptionPlan,
            @Param("featureType") PlanFeature.FeatureType featureType,
            @Param("isIncluded") Boolean isIncluded,
            @Param("status") PlanFeature.FeatureStatus status,
            @Param("hasLimit") Boolean hasLimit,
            @Param("featureCode") String featureCode,
            @Param("featureName") String featureName
    );
    
    // Fonctionnalités communes entre plans
    @Query("SELECT pf1.featureCode, pf1.featureName, COUNT(DISTINCT pf1.subscriptionPlan) as planCount " +
           "FROM PlanFeature pf1 " +
           "WHERE pf1.isIncluded = true " +
           "GROUP BY pf1.featureCode, pf1.featureName " +
           "HAVING COUNT(DISTINCT pf1.subscriptionPlan) > 1 " +
           "ORDER BY planCount DESC")
    List<Object[]> findCommonFeaturesAcrossPlans();
    
    // Fonctionnalités uniques par plan
    @Query("SELECT pf.subscriptionPlan.planName, pf.featureCode, pf.featureName " +
           "FROM PlanFeature pf " +
           "WHERE pf.isIncluded = true AND " +
           "pf.featureCode NOT IN (" +
           "  SELECT pf2.featureCode FROM PlanFeature pf2 " +
           "  WHERE pf2.subscriptionPlan != pf.subscriptionPlan AND pf2.isIncluded = true" +
           ")")
    List<Object[]> findUniqueFeaturesByPlan();
    
    // Fonctionnalités avec les plus hautes limites
    @Query("SELECT pf.featureCode, pf.featureName, pf.limitValue, pf.subscriptionPlan.planName " +
           "FROM PlanFeature pf " +
           "WHERE pf.limitValue IS NOT NULL " +
           "ORDER BY pf.limitValue DESC")
    List<Object[]> findFeaturesWithHighestLimits();
    
    // Statistiques des fonctionnalités
    @Query("SELECT " +
           "COUNT(pf) as totalFeatures, " +
           "COUNT(CASE WHEN pf.isIncluded = true THEN 1 END) as includedFeatures, " +
           "COUNT(CASE WHEN pf.limitValue IS NOT NULL THEN 1 END) as limitedFeatures, " +
           "COUNT(DISTINCT pf.featureCode) as uniqueFeatureCodes, " +
           "COUNT(DISTINCT pf.subscriptionPlan) as plansWithFeatures " +
           "FROM PlanFeature pf")
    Object[] getFeatureStatistics();
    
    // Fonctionnalités par région (devise du plan)
    @Query("SELECT pf.subscriptionPlan.currency, COUNT(pf), COUNT(CASE WHEN pf.isIncluded = true THEN 1 END) " +
           "FROM PlanFeature pf GROUP BY pf.subscriptionPlan.currency")
    List<Object[]> getFeaturesByRegion();
    
    // Fonctionnalités expirant bientôt (pour les fonctionnalités en version bêta ou dépréciées)
    @Query("SELECT pf FROM PlanFeature pf WHERE pf.status IN ('BETA', 'DEPRECATED') AND pf.updatedAt < :dateThreshold")
    List<PlanFeature> findFeaturesExpiringSoon(@Param("dateThreshold") java.time.LocalDateTime dateThreshold);
}
