package com.ecomptaia.repository;

import com.ecomptaia.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    
    // Recherche par code de plan
    Optional<SubscriptionPlan> findByPlanCode(String planCode);
    
    // Plans actifs
    List<SubscriptionPlan> findByIsActiveTrueOrderBySortOrderAsc();
    
    // Plans par statut
    List<SubscriptionPlan> findByStatus(SubscriptionPlan.PlanStatus status);
    
    // Plans actifs et en vedette
    List<SubscriptionPlan> findByIsActiveTrueAndIsFeaturedTrueOrderBySortOrderAsc();
    
    // Plans par cycle de facturation
    List<SubscriptionPlan> findByBillingCycle(SubscriptionPlan.BillingCycle billingCycle);
    
    // Plans par devise
    List<SubscriptionPlan> findByCurrency(String currency);
    
    // Plans par prix de base (supérieur à)
    List<SubscriptionPlan> findByBasePriceUSDGreaterThan(BigDecimal price);
    
    // Plans par prix de base (inférieur à)
    List<SubscriptionPlan> findByBasePriceUSDLessThan(BigDecimal price);
    
    // Plans par prix de base (entre deux valeurs)
    List<SubscriptionPlan> findByBasePriceUSDBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Plans par nombre maximum d'utilisateurs
    List<SubscriptionPlan> findByMaxUsersGreaterThanEqual(Integer maxUsers);
    
    // Plans par nombre maximum d'entreprises
    List<SubscriptionPlan> findByMaxCompaniesGreaterThanEqual(Integer maxCompanies);
    
    // Plans par limite de stockage
    List<SubscriptionPlan> findByStorageLimitGBGreaterThanEqual(Integer storageLimit);
    
    // Recherche par nom de plan (contient)
    List<SubscriptionPlan> findByPlanNameContainingIgnoreCase(String planName);
    
    // Recherche par description (contient)
    List<SubscriptionPlan> findByDescriptionContainingIgnoreCase(String description);
    
    // Plans par ordre de tri
    List<SubscriptionPlan> findAllByOrderBySortOrderAsc();
    
    // Plans actifs par ordre de prix croissant
    List<SubscriptionPlan> findByIsActiveTrueOrderByBasePriceUSDAsc();
    
    // Plans actifs par ordre de prix décroissant
    List<SubscriptionPlan> findByIsActiveTrueOrderByBasePriceUSDDesc();
    
    // Compter les plans par statut
    @Query("SELECT p.status, COUNT(p) FROM SubscriptionPlan p GROUP BY p.status")
    List<Object[]> countByStatus();
    
    // Compter les plans par cycle de facturation
    @Query("SELECT p.billingCycle, COUNT(p) FROM SubscriptionPlan p GROUP BY p.billingCycle")
    List<Object[]> countByBillingCycle();
    
    // Compter les plans par devise
    @Query("SELECT p.currency, COUNT(p) FROM SubscriptionPlan p GROUP BY p.currency")
    List<Object[]> countByCurrency();
    
    // Prix moyen par cycle de facturation
    @Query("SELECT p.billingCycle, AVG(p.basePriceUSD) FROM SubscriptionPlan p WHERE p.isActive = true GROUP BY p.billingCycle")
    List<Object[]> averagePriceByBillingCycle();
    
    // Prix moyen par devise
    @Query("SELECT p.currency, AVG(p.basePriceUSD) FROM SubscriptionPlan p WHERE p.isActive = true GROUP BY p.currency")
    List<Object[]> averagePriceByCurrency();
    
    // Plans les plus populaires (par nombre de souscriptions)
    @Query("SELECT p, COUNT(cs) as subscriptionCount FROM SubscriptionPlan p LEFT JOIN p.companySubscriptions cs GROUP BY p ORDER BY subscriptionCount DESC")
    List<Object[]> findMostPopularPlans();
    
    // Plans avec le plus de fonctionnalités
    @Query("SELECT p, COUNT(pf) as featureCount FROM SubscriptionPlan p LEFT JOIN p.features pf WHERE pf.isIncluded = true GROUP BY p ORDER BY featureCount DESC")
    List<Object[]> findPlansWithMostFeatures();
    
    // Recherche avancée avec critères multiples
    @Query("SELECT p FROM SubscriptionPlan p WHERE " +
           "(:isActive IS NULL OR p.isActive = :isActive) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:billingCycle IS NULL OR p.billingCycle = :billingCycle) AND " +
           "(:currency IS NULL OR p.currency = :currency) AND " +
           "(:minPrice IS NULL OR p.basePriceUSD >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.basePriceUSD <= :maxPrice) AND " +
           "(:isFeatured IS NULL OR p.isFeatured = :isFeatured) " +
           "ORDER BY p.sortOrder ASC")
    List<SubscriptionPlan> findPlansWithCriteria(
            @Param("isActive") Boolean isActive,
            @Param("status") SubscriptionPlan.PlanStatus status,
            @Param("billingCycle") SubscriptionPlan.BillingCycle billingCycle,
            @Param("currency") String currency,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("isFeatured") Boolean isFeatured
    );
    
    // Plans recommandés pour une entreprise (basé sur la taille)
    @Query("SELECT p FROM SubscriptionPlan p WHERE p.isActive = true AND " +
           "(:maxUsers IS NULL OR p.maxUsers >= :maxUsers) AND " +
           "(:maxCompanies IS NULL OR p.maxCompanies >= :maxCompanies) " +
           "ORDER BY p.basePriceUSD ASC")
    List<SubscriptionPlan> findRecommendedPlans(
            @Param("maxUsers") Integer maxUsers,
            @Param("maxCompanies") Integer maxCompanies
    );
    
    // Statistiques des plans
    @Query("SELECT " +
           "COUNT(p) as totalPlans, " +
           "COUNT(CASE WHEN p.isActive = true THEN 1 END) as activePlans, " +
           "COUNT(CASE WHEN p.isFeatured = true THEN 1 END) as featuredPlans, " +
           "AVG(p.basePriceUSD) as averagePrice, " +
           "MIN(p.basePriceUSD) as minPrice, " +
           "MAX(p.basePriceUSD) as maxPrice " +
           "FROM SubscriptionPlan p")
    Object[] getPlanStatistics();
    
    // Plans par région (devise)
    @Query("SELECT p.currency, COUNT(p), AVG(p.basePriceUSD) FROM SubscriptionPlan p WHERE p.isActive = true GROUP BY p.currency")
    List<Object[]> getPlansByRegion();
    
    // Plans expirant bientôt (pour les plans en version bêta ou dépréciés)
    @Query("SELECT p FROM SubscriptionPlan p WHERE p.status IN ('BETA', 'DEPRECATED') AND p.updatedAt < :dateThreshold")
    List<SubscriptionPlan> findPlansExpiringSoon(@Param("dateThreshold") java.time.LocalDateTime dateThreshold);
}







