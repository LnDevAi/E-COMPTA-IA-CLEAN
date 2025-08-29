package com.ecomptaia.repository;

import com.ecomptaia.entity.CompanySubscription;
import com.ecomptaia.entity.SubscriptionPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPaymentRepository extends JpaRepository<SubscriptionPayment, Long> {
    
    // Paiements par souscription
    List<SubscriptionPayment> findByCompanySubscription(CompanySubscription companySubscription);
    
    // Paiements par souscription et statut
    List<SubscriptionPayment> findByCompanySubscriptionAndStatus(CompanySubscription companySubscription, SubscriptionPayment.PaymentStatus status);
    
    // Paiements par code
    Optional<SubscriptionPayment> findByPaymentCode(String paymentCode);
    
    // Paiements par transaction ID
    Optional<SubscriptionPayment> findByTransactionId(String transactionId);
    
    // Paiements par statut
    List<SubscriptionPayment> findByStatus(SubscriptionPayment.PaymentStatus status);
    
    // Paiements par date
    List<SubscriptionPayment> findByPaymentDate(LocalDate paymentDate);
    
    // Paiements par période
    List<SubscriptionPayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Paiements par devise
    List<SubscriptionPayment> findByCurrency(String currency);
    
    // Paiements par montant
    List<SubscriptionPayment> findByAmountGreaterThan(BigDecimal amount);
    
    // Paiements par montant (inférieur à)
    List<SubscriptionPayment> findByAmountLessThan(BigDecimal amount);
    
    // Paiements par montant (entre deux valeurs)
    List<SubscriptionPayment> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    // Paiements par méthode de paiement
    List<SubscriptionPayment> findByPaymentMethod(String paymentMethod);
    
    // Paiements par période de facturation
    List<SubscriptionPayment> findByBillingPeriodStartAndBillingPeriodEnd(LocalDate startDate, LocalDate endDate);
    
    // Paiements par période de facturation (début)
    List<SubscriptionPayment> findByBillingPeriodStart(LocalDate startDate);
    
    // Paiements par période de facturation (fin)
    List<SubscriptionPayment> findByBillingPeriodEnd(LocalDate endDate);
    
    // Paiements complétés
    List<SubscriptionPayment> findByStatusOrderByPaymentDateDesc(SubscriptionPayment.PaymentStatus status);
    
    // Paiements en attente
    List<SubscriptionPayment> findByStatusOrderByPaymentDateAsc(SubscriptionPayment.PaymentStatus status);
    
    // Paiements remboursés
    List<SubscriptionPayment> findByStatusInOrderByPaymentDateDesc(List<SubscriptionPayment.PaymentStatus> statuses);
    
    // Paiements par entreprise
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE sp.companySubscription.company.id = :companyId")
    List<SubscriptionPayment> findByCompanyId(@Param("companyId") Long companyId);
    
    // Paiements par entreprise et statut
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE sp.companySubscription.company.id = :companyId AND sp.status = :status")
    List<SubscriptionPayment> findByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") SubscriptionPayment.PaymentStatus status);
    
    // Paiements par entreprise et période
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE sp.companySubscription.company.id = :companyId AND sp.paymentDate BETWEEN :startDate AND :endDate")
    List<SubscriptionPayment> findByCompanyIdAndPaymentDateBetween(@Param("companyId") Long companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Paiements par plan
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE sp.companySubscription.subscriptionPlan.id = :planId")
    List<SubscriptionPayment> findByPlanId(@Param("planId") Long planId);
    
    // Paiements par plan et statut
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE sp.companySubscription.subscriptionPlan.id = :planId AND sp.status = :status")
    List<SubscriptionPayment> findByPlanIdAndStatus(@Param("planId") Long planId, @Param("status") SubscriptionPayment.PaymentStatus status);
    
    // Compter les paiements par statut
    @Query("SELECT sp.status, COUNT(sp) FROM SubscriptionPayment sp GROUP BY sp.status")
    List<Object[]> countByStatus();
    
    // Compter les paiements par devise
    @Query("SELECT sp.currency, COUNT(sp) FROM SubscriptionPayment sp GROUP BY sp.currency")
    List<Object[]> countByCurrency();
    
    // Compter les paiements par méthode de paiement
    @Query("SELECT sp.paymentMethod, COUNT(sp) FROM SubscriptionPayment sp GROUP BY sp.paymentMethod")
    List<Object[]> countByPaymentMethod();
    
    // Compter les paiements par entreprise
    @Query("SELECT sp.companySubscription.company.name, COUNT(sp) FROM SubscriptionPayment sp GROUP BY sp.companySubscription.company.name")
    List<Object[]> countByCompany();
    
    // Compter les paiements par plan
    @Query("SELECT sp.companySubscription.subscriptionPlan.planName, COUNT(sp) FROM SubscriptionPayment sp GROUP BY sp.companySubscription.subscriptionPlan.planName")
    List<Object[]> countByPlan();
    
    // Revenus par statut
    @Query("SELECT sp.status, SUM(sp.amount) FROM SubscriptionPayment sp GROUP BY sp.status")
    List<Object[]> revenueByStatus();
    
    // Revenus par devise
    @Query("SELECT sp.currency, SUM(sp.amount) FROM SubscriptionPayment sp GROUP BY sp.currency")
    List<Object[]> revenueByCurrency();
    
    // Revenus par méthode de paiement
    @Query("SELECT sp.paymentMethod, SUM(sp.amount) FROM SubscriptionPayment sp GROUP BY sp.paymentMethod")
    List<Object[]> revenueByPaymentMethod();
    
    // Revenus par entreprise
    @Query("SELECT sp.companySubscription.company.name, SUM(sp.amount) FROM SubscriptionPayment sp GROUP BY sp.companySubscription.company.name")
    List<Object[]> revenueByCompany();
    
    // Revenus par plan
    @Query("SELECT sp.companySubscription.subscriptionPlan.planName, SUM(sp.amount) FROM SubscriptionPayment sp GROUP BY sp.companySubscription.subscriptionPlan.planName")
    List<Object[]> revenueByPlan();
    
    // Revenus par mois
    @Query("SELECT YEAR(sp.paymentDate), MONTH(sp.paymentDate), SUM(sp.amount) FROM SubscriptionPayment sp WHERE sp.status = 'COMPLETED' GROUP BY YEAR(sp.paymentDate), MONTH(sp.paymentDate) ORDER BY YEAR(sp.paymentDate) DESC, MONTH(sp.paymentDate) DESC")
    List<Object[]> revenueByMonth();
    
    // Revenus par jour
    @Query("SELECT sp.paymentDate, SUM(sp.amount) FROM SubscriptionPayment sp WHERE sp.status = 'COMPLETED' GROUP BY sp.paymentDate ORDER BY sp.paymentDate DESC")
    List<Object[]> revenueByDay();
    
    // Paiements récents
    @Query("SELECT sp FROM SubscriptionPayment sp ORDER BY sp.paymentDate DESC")
    List<SubscriptionPayment> findRecentPayments();
    
    // Paiements récents par entreprise
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE sp.companySubscription.company.id = :companyId ORDER BY sp.paymentDate DESC")
    List<SubscriptionPayment> findRecentPaymentsByCompany(@Param("companyId") Long companyId);
    
    // Paiements en attente de traitement
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE sp.status = 'PENDING' ORDER BY sp.paymentDate ASC")
    List<SubscriptionPayment> findPendingPayments();
    
    // Paiements échoués récents
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE sp.status = 'FAILED' ORDER BY sp.paymentDate DESC")
    List<SubscriptionPayment> findFailedPayments();
    
    // Paiements remboursés récents
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE sp.status IN ('REFUNDED', 'PARTIALLY_REFUNDED') ORDER BY sp.paymentDate DESC")
    List<SubscriptionPayment> findRefundedPayments();
    
    // Statistiques des paiements
    @Query("SELECT " +
           "COUNT(sp) as totalPayments, " +
           "COUNT(CASE WHEN sp.status = 'COMPLETED' THEN 1 END) as completedPayments, " +
           "COUNT(CASE WHEN sp.status = 'PENDING' THEN 1 END) as pendingPayments, " +
           "COUNT(CASE WHEN sp.status = 'FAILED' THEN 1 END) as failedPayments, " +
           "COUNT(CASE WHEN sp.status IN ('REFUNDED', 'PARTIALLY_REFUNDED') THEN 1 END) as refundedPayments, " +
           "SUM(sp.amount) as totalAmount, " +
           "AVG(sp.amount) as averageAmount, " +
           "SUM(sp.amountUSD) as totalAmountUSD " +
           "FROM SubscriptionPayment sp")
    Object[] getPaymentStatistics();
    
    // Statistiques des paiements par entreprise
    @Query("SELECT sp.companySubscription.company.name, " +
           "COUNT(sp) as totalPayments, " +
           "COUNT(CASE WHEN sp.status = 'COMPLETED' THEN 1 END) as completedPayments, " +
           "SUM(sp.amount) as totalAmount, " +
           "AVG(sp.amount) as averageAmount " +
           "FROM SubscriptionPayment sp GROUP BY sp.companySubscription.company.name")
    List<Object[]> getPaymentStatisticsByCompany();
    
    // Statistiques des paiements par plan
    @Query("SELECT sp.companySubscription.subscriptionPlan.planName, " +
           "COUNT(sp) as totalPayments, " +
           "COUNT(CASE WHEN sp.status = 'COMPLETED' THEN 1 END) as completedPayments, " +
           "SUM(sp.amount) as totalAmount, " +
           "AVG(sp.amount) as averageAmount " +
           "FROM SubscriptionPayment sp GROUP BY sp.companySubscription.subscriptionPlan.planName")
    List<Object[]> getPaymentStatisticsByPlan();
    
    // Recherche avancée avec critères multiples
    @Query("SELECT sp FROM SubscriptionPayment sp WHERE " +
           "(:companySubscription IS NULL OR sp.companySubscription = :companySubscription) AND " +
           "(:status IS NULL OR sp.status = :status) AND " +
           "(:currency IS NULL OR sp.currency = :currency) AND " +
           "(:paymentMethod IS NULL OR sp.paymentMethod = :paymentMethod) AND " +
           "(:minAmount IS NULL OR sp.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR sp.amount <= :maxAmount) AND " +
           "(:startDate IS NULL OR sp.paymentDate >= :startDate) AND " +
           "(:endDate IS NULL OR sp.paymentDate <= :endDate) " +
           "ORDER BY sp.paymentDate DESC")
    List<SubscriptionPayment> findPaymentsWithCriteria(
            @Param("companySubscription") CompanySubscription companySubscription,
            @Param("status") SubscriptionPayment.PaymentStatus status,
            @Param("currency") String currency,
            @Param("paymentMethod") String paymentMethod,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    // Paiements par région (devise)
    @Query("SELECT sp.currency, COUNT(sp), SUM(sp.amount), AVG(sp.amount) FROM SubscriptionPayment sp WHERE sp.status = 'COMPLETED' GROUP BY sp.currency")
    List<Object[]> getPaymentsByRegion();
    
    // Taux de réussite des paiements
    @Query("SELECT " +
           "COUNT(sp) as totalAttempts, " +
           "COUNT(CASE WHEN sp.status = 'COMPLETED' THEN 1 END) as successfulPayments, " +
           "COUNT(CASE WHEN sp.status = 'FAILED' THEN 1 END) as failedPayments " +
           "FROM SubscriptionPayment sp")
    Object[] getPaymentSuccessRate();
    
    // Paiements avec le plus de montant
    @Query("SELECT sp.companySubscription.company.name, sp.paymentMethod, sp.amount, sp.paymentDate " +
           "FROM SubscriptionPayment sp WHERE sp.status = 'COMPLETED' " +
           "ORDER BY sp.amount DESC")
    List<Object[]> findHighestAmountPayments();
    
    // Tendances de paiement (par mois)
    @Query("SELECT YEAR(sp.paymentDate), MONTH(sp.paymentDate), " +
           "COUNT(sp) as paymentCount, " +
           "SUM(sp.amount) as totalAmount, " +
           "COUNT(CASE WHEN sp.status = 'COMPLETED' THEN 1 END) as completedCount " +
           "FROM SubscriptionPayment sp " +
           "GROUP BY YEAR(sp.paymentDate), MONTH(sp.paymentDate) " +
           "ORDER BY YEAR(sp.paymentDate) DESC, MONTH(sp.paymentDate) DESC")
    List<Object[]> getPaymentTrends();
}
