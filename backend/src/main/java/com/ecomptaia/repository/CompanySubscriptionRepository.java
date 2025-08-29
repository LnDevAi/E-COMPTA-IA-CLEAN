package com.ecomptaia.repository;

import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.CompanySubscription;
import com.ecomptaia.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription, Long> {
    
    // Souscriptions par entreprise
    List<CompanySubscription> findByCompany(Company company);
    
    // Souscriptions actives par entreprise
    List<CompanySubscription> findByCompanyAndStatusIn(Company company, List<CompanySubscription.SubscriptionStatus> statuses);
    
    // Souscription active par entreprise
    Optional<CompanySubscription> findByCompanyAndStatus(Company company, CompanySubscription.SubscriptionStatus status);
    
    // Souscriptions par plan
    List<CompanySubscription> findBySubscriptionPlan(SubscriptionPlan subscriptionPlan);
    
    // Souscriptions par code
    Optional<CompanySubscription> findBySubscriptionCode(String subscriptionCode);
    
    // Souscriptions par statut
    List<CompanySubscription> findByStatus(CompanySubscription.SubscriptionStatus status);
    
    // Souscriptions par cycle de facturation
    List<CompanySubscription> findByBillingCycle(SubscriptionPlan.BillingCycle billingCycle);
    
    // Souscriptions par devise
    List<CompanySubscription> findByLocalCurrency(String currency);
    
    // Souscriptions par date de début
    List<CompanySubscription> findByStartDate(LocalDate startDate);
    
    // Souscriptions par date de fin
    List<CompanySubscription> findByEndDate(LocalDate endDate);
    
    // Souscriptions expirées
    List<CompanySubscription> findByEndDateBefore(LocalDate date);
    
    // Souscriptions expirant bientôt
    List<CompanySubscription> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Souscriptions par prix final
    List<CompanySubscription> findByFinalPriceGreaterThan(BigDecimal price);
    
    // Souscriptions par prix final (inférieur à)
    List<CompanySubscription> findByFinalPriceLessThan(BigDecimal price);
    
    // Souscriptions par prix final (entre deux valeurs)
    List<CompanySubscription> findByFinalPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Souscriptions avec renouvellement automatique
    List<CompanySubscription> findByAutoRenewTrue();
    
    // Souscriptions sans renouvellement automatique
    List<CompanySubscription> findByAutoRenewFalse();
    
    // Souscriptions par méthode de paiement
    List<CompanySubscription> findByPaymentMethod(String paymentMethod);
    
    // Souscriptions par date de dernier paiement
    List<CompanySubscription> findByLastPaymentDate(LocalDate lastPaymentDate);
    
    // Souscriptions par date de prochain paiement
    List<CompanySubscription> findByNextPaymentDate(LocalDate nextPaymentDate);
    
    // Souscriptions avec paiements en retard
    List<CompanySubscription> findByNextPaymentDateBefore(LocalDate date);
    
    // Souscriptions annulées
    List<CompanySubscription> findByCancellationDateIsNotNull();
    
    // Souscriptions annulées par raison
    List<CompanySubscription> findByCancellationReasonContainingIgnoreCase(String reason);
    
    // Souscriptions par entreprise et plan
    List<CompanySubscription> findByCompanyAndSubscriptionPlan(Company company, SubscriptionPlan subscriptionPlan);
    
    // Souscriptions actives par entreprise et plan
    Optional<CompanySubscription> findByCompanyAndSubscriptionPlanAndStatus(Company company, SubscriptionPlan subscriptionPlan, CompanySubscription.SubscriptionStatus status);
    

    
    // Souscriptions par entreprise et cycle de facturation
    List<CompanySubscription> findByCompanyAndBillingCycle(Company company, SubscriptionPlan.BillingCycle billingCycle);
    
    // Souscriptions par entreprise et devise
    List<CompanySubscription> findByCompanyAndLocalCurrency(Company company, String currency);
    
    // Compter les souscriptions par statut
    @Query("SELECT cs.status, COUNT(cs) FROM CompanySubscription cs GROUP BY cs.status")
    List<Object[]> countByStatus();
    
    // Compter les souscriptions par plan
    @Query("SELECT cs.subscriptionPlan.planName, COUNT(cs) FROM CompanySubscription cs GROUP BY cs.subscriptionPlan.planName")
    List<Object[]> countByPlan();
    
    // Compter les souscriptions par cycle de facturation
    @Query("SELECT cs.billingCycle, COUNT(cs) FROM CompanySubscription cs GROUP BY cs.billingCycle")
    List<Object[]> countByBillingCycle();
    
    // Compter les souscriptions par devise
    @Query("SELECT cs.localCurrency, COUNT(cs) FROM CompanySubscription cs GROUP BY cs.localCurrency")
    List<Object[]> countByCurrency();
    
    // Compter les souscriptions par entreprise
    @Query("SELECT cs.company.name, COUNT(cs) FROM CompanySubscription cs GROUP BY cs.company.name")
    List<Object[]> countByCompany();
    
    // Revenus par plan
    @Query("SELECT cs.subscriptionPlan.planName, SUM(cs.finalPrice) FROM CompanySubscription cs WHERE cs.status = 'ACTIVE' GROUP BY cs.subscriptionPlan.planName")
    List<Object[]> revenueByPlan();
    
    // Revenus par devise
    @Query("SELECT cs.localCurrency, SUM(cs.finalPrice) FROM CompanySubscription cs WHERE cs.status = 'ACTIVE' GROUP BY cs.localCurrency")
    List<Object[]> revenueByCurrency();
    
    // Revenus par cycle de facturation
    @Query("SELECT cs.billingCycle, SUM(cs.finalPrice) FROM CompanySubscription cs WHERE cs.status = 'ACTIVE' GROUP BY cs.billingCycle")
    List<Object[]> revenueByBillingCycle();
    
    // Revenus par mois
    @Query("SELECT YEAR(cs.startDate), MONTH(cs.startDate), SUM(cs.finalPrice) FROM CompanySubscription cs WHERE cs.status = 'ACTIVE' GROUP BY YEAR(cs.startDate), MONTH(cs.startDate) ORDER BY YEAR(cs.startDate) DESC, MONTH(cs.startDate) DESC")
    List<Object[]> revenueByMonth();
    
    // Souscriptions expirant ce mois
    @Query("SELECT cs FROM CompanySubscription cs WHERE cs.endDate BETWEEN :startDate AND :endDate AND cs.status = 'ACTIVE'")
    List<CompanySubscription> findExpiringThisMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Souscriptions avec paiements en retard
    @Query("SELECT cs FROM CompanySubscription cs WHERE cs.nextPaymentDate < :today AND cs.status = 'ACTIVE'")
    List<CompanySubscription> findOverduePayments(@Param("today") LocalDate today);
    
    // Souscriptions à renouveler automatiquement
    @Query("SELECT cs FROM CompanySubscription cs WHERE cs.autoRenew = true AND cs.nextPaymentDate BETWEEN :startDate AND :endDate AND cs.status = 'ACTIVE'")
    List<CompanySubscription> findAutoRenewals(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Statistiques des souscriptions
    @Query("SELECT " +
           "COUNT(cs) as totalSubscriptions, " +
           "COUNT(CASE WHEN cs.status = 'ACTIVE' THEN 1 END) as activeSubscriptions, " +
           "COUNT(CASE WHEN cs.status = 'TRIAL' THEN 1 END) as trialSubscriptions, " +
           "COUNT(CASE WHEN cs.status = 'CANCELLED' THEN 1 END) as cancelledSubscriptions, " +
           "SUM(cs.finalPrice) as totalRevenue, " +
           "AVG(cs.finalPrice) as averagePrice, " +
           "COUNT(CASE WHEN cs.autoRenew = true THEN 1 END) as autoRenewSubscriptions " +
           "FROM CompanySubscription cs")
    Object[] getSubscriptionStatistics();
    
    // Statistiques par entreprise
    @Query("SELECT cs.company.name, " +
           "COUNT(cs) as totalSubscriptions, " +
           "COUNT(CASE WHEN cs.status = 'ACTIVE' THEN 1 END) as activeSubscriptions, " +
           "SUM(cs.finalPrice) as totalSpent, " +
           "AVG(cs.finalPrice) as averageSpent " +
           "FROM CompanySubscription cs GROUP BY cs.company.name")
    List<Object[]> getStatisticsByCompany();
    
    // Statistiques par plan
    @Query("SELECT cs.subscriptionPlan.planName, " +
           "COUNT(cs) as totalSubscriptions, " +
           "COUNT(CASE WHEN cs.status = 'ACTIVE' THEN 1 END) as activeSubscriptions, " +
           "SUM(cs.finalPrice) as totalRevenue, " +
           "AVG(cs.finalPrice) as averagePrice " +
           "FROM CompanySubscription cs GROUP BY cs.subscriptionPlan.planName")
    List<Object[]> getStatisticsByPlan();
    
    // Recherche avancée avec critères multiples
    @Query("SELECT cs FROM CompanySubscription cs WHERE " +
           "(:company IS NULL OR cs.company = :company) AND " +
           "(:subscriptionPlan IS NULL OR cs.subscriptionPlan = :subscriptionPlan) AND " +
           "(:status IS NULL OR cs.status = :status) AND " +
           "(:billingCycle IS NULL OR cs.billingCycle = :billingCycle) AND " +
           "(:currency IS NULL OR cs.localCurrency = :currency) AND " +
           "(:autoRenew IS NULL OR cs.autoRenew = :autoRenew) AND " +
           "(:minPrice IS NULL OR cs.finalPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR cs.finalPrice <= :maxPrice) AND " +
           "(:startDate IS NULL OR cs.startDate >= :startDate) AND " +
           "(:endDate IS NULL OR cs.endDate <= :endDate) " +
           "ORDER BY cs.createdAt DESC")
    List<CompanySubscription> findSubscriptionsWithCriteria(
            @Param("company") Company company,
            @Param("subscriptionPlan") SubscriptionPlan subscriptionPlan,
            @Param("status") CompanySubscription.SubscriptionStatus status,
            @Param("billingCycle") SubscriptionPlan.BillingCycle billingCycle,
            @Param("currency") String currency,
            @Param("autoRenew") Boolean autoRenew,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    // Souscriptions par région (devise)
    @Query("SELECT cs.localCurrency, COUNT(cs), SUM(cs.finalPrice), AVG(cs.finalPrice) FROM CompanySubscription cs WHERE cs.status = 'ACTIVE' GROUP BY cs.localCurrency")
    List<Object[]> getSubscriptionsByRegion();
    
    // Taux de conversion des essais
    @Query("SELECT " +
           "COUNT(CASE WHEN cs.status = 'TRIAL' THEN 1 END) as trialCount, " +
           "COUNT(CASE WHEN cs.status = 'ACTIVE' AND cs.startDate > cs.createdAt THEN 1 END) as convertedCount " +
           "FROM CompanySubscription cs")
    Object[] getTrialConversionRate();
    
    // Souscriptions avec le plus de revenus
    @Query("SELECT cs.company.name, cs.subscriptionPlan.planName, SUM(cs.finalPrice) as totalRevenue " +
           "FROM CompanySubscription cs WHERE cs.status = 'ACTIVE' " +
           "GROUP BY cs.company.name, cs.subscriptionPlan.planName " +
           "ORDER BY totalRevenue DESC")
    List<Object[]> findTopRevenueSubscriptions();
}
