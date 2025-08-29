package com.ecomptaia.repository;

import com.ecomptaia.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    /**
     * Trouver tous les abonnements actifs
     */
    List<Subscription> findByIsActiveTrue();
    
    /**
     * Trouver un abonnement par nom
     */
    Optional<Subscription> findByName(String name);
    
    /**
     * Trouver les abonnements par cycle de facturation
     */
    List<Subscription> findByBillingCycle(Subscription.BillingCycle billingCycle);
    
    /**
     * Trouver les abonnements par plage de prix
     */
    @Query("SELECT s FROM Subscription s WHERE s.price BETWEEN :minPrice AND :maxPrice AND s.isActive = true")
    List<Subscription> findByPriceRange(@Param("minPrice") java.math.BigDecimal minPrice, 
                                       @Param("maxPrice") java.math.BigDecimal maxPrice);
    
    /**
     * Trouver les abonnements qui incluent un module spécifique
     */
    @Query("SELECT s FROM Subscription s JOIN s.includedModules m WHERE m = :moduleType AND s.isActive = true")
    List<Subscription> findByIncludedModule(@Param("moduleType") Subscription.ModuleType moduleType);
    
    /**
     * Trouver les abonnements qui incluent une fonctionnalité spécifique
     */
    @Query("SELECT s FROM Subscription s JOIN s.includedFeatures f WHERE f = :featureType AND s.isActive = true")
    List<Subscription> findByIncludedFeature(@Param("featureType") Subscription.FeatureType featureType);
    
    /**
     * Trouver les abonnements par nombre maximum d'utilisateurs
     */
    List<Subscription> findByMaxUsersGreaterThanEqualAndIsActiveTrue(Integer maxUsers);
    
    /**
     * Trouver les abonnements par nombre maximum d'entreprises
     */
    List<Subscription> findByMaxCompaniesGreaterThanEqualAndIsActiveTrue(Integer maxCompanies);
    
    /**
     * Trouver les abonnements par devise
     */
    List<Subscription> findByCurrencyAndIsActiveTrue(String currency);
    
    /**
     * Compter les abonnements actifs
     */
    long countByIsActiveTrue();
    
    /**
     * Trouver les abonnements créés après une date
     */
    List<Subscription> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Trouver les abonnements mis à jour après une date
     */
    List<Subscription> findByUpdatedAtAfter(java.time.LocalDateTime date);
}




