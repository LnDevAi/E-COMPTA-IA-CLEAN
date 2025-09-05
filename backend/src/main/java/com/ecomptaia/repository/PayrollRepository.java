package com.ecomptaia.repository;

import com.ecomptaia.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    // Recherche par code de paie
    Optional<Payroll> findByPayrollCode(String payrollCode);

    // Recherche par employé
    List<Payroll> findByEmployeeId(Long employeeId);

    // Recherche par entreprise
    List<Payroll> findByEntrepriseId(Long entrepriseId);

    // Recherche par statut de paie
    List<Payroll> findByPayrollStatus(Payroll.PayrollStatus payrollStatus);

    // Recherche par période de paie
    List<Payroll> findByPayPeriod(Payroll.PayPeriod payPeriod);

    // Recherche par méthode de paiement
    List<Payroll> findByPaymentMethod(Payroll.PaymentMethod paymentMethod);

    // Recherche par devise
    List<Payroll> findByCurrency(String currency);

    // Recherche par date de paie
    List<Payroll> findByPayDate(LocalDate payDate);

    // Recherche par période de paie (date range)
    List<Payroll> findByPeriodStartDateBetween(LocalDate startDate, LocalDate endDate);

    // Recherche par entreprise et employé
    List<Payroll> findByEntrepriseIdAndEmployeeId(Long entrepriseId, Long employeeId);

    // Recherche par entreprise et statut
    List<Payroll> findByEntrepriseIdAndPayrollStatus(Long entrepriseId, Payroll.PayrollStatus payrollStatus);

    // Recherche par entreprise et période de paie
    List<Payroll> findByEntrepriseIdAndPayPeriod(Long entrepriseId, Payroll.PayPeriod payPeriod);

    // Recherche par entreprise et méthode de paiement
    List<Payroll> findByEntrepriseIdAndPaymentMethod(Long entrepriseId, Payroll.PaymentMethod paymentMethod);

    // Recherche par entreprise et devise
    List<Payroll> findByEntrepriseIdAndCurrency(Long entrepriseId, String currency);

    // Recherche par entreprise et date de paie
    List<Payroll> findByEntrepriseIdAndPayDate(Long entrepriseId, LocalDate payDate);

    // Recherche par entreprise et période de paie (date range)
    List<Payroll> findByEntrepriseIdAndPeriodStartDateBetween(Long entrepriseId, LocalDate startDate, LocalDate endDate);

    // Recherche par entreprise et date de paie (range)
    List<Payroll> findByEntrepriseIdAndPayDateBetween(Long entrepriseId, LocalDate startDate, LocalDate endDate);

    // Recherche par salaire brut (range)
    @Query("SELECT p FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.grossSalary BETWEEN :minSalary AND :maxSalary")
    List<Payroll> findByEntrepriseIdAndGrossSalaryBetween(@Param("entrepriseId") Long entrepriseId, @Param("minSalary") java.math.BigDecimal minSalary, @Param("maxSalary") java.math.BigDecimal maxSalary);

    // Recherche par salaire net (range)
    @Query("SELECT p FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.netSalary BETWEEN :minSalary AND :maxSalary")
    List<Payroll> findByEntrepriseIdAndNetSalaryBetween(@Param("entrepriseId") Long entrepriseId, @Param("minSalary") java.math.BigDecimal minSalary, @Param("maxSalary") java.math.BigDecimal maxSalary);

    // Recherche par employé et période
    List<Payroll> findByEmployeeIdAndPeriodStartDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    // Recherche par employé et date de paie (range)
    List<Payroll> findByEmployeeIdAndPayDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    // Recherche des paies payées
    @Query("SELECT p FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'PAID'")
    List<Payroll> findPaidPayrollsByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des paies approuvées
    @Query("SELECT p FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND (p.payrollStatus = 'APPROVED' OR p.payrollStatus = 'PAID')")
    List<Payroll> findApprovedPayrollsByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des paies en attente
    @Query("SELECT p FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'PENDING_APPROVAL'")
    List<Payroll> findPendingPayrollsByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des paies en brouillon
    @Query("SELECT p FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'DRAFT'")
    List<Payroll> findDraftPayrollsByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des paies annulées
    @Query("SELECT p FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'CANCELLED'")
    List<Payroll> findCancelledPayrollsByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche par approbateur
    List<Payroll> findByEntrepriseIdAndApprovedBy(Long entrepriseId, Long approvedBy);

    // Recherche par date d'approbation (range)
    List<Payroll> findByEntrepriseIdAndApprovedAtBetween(Long entrepriseId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    // Statistiques par statut de paie
    @Query("SELECT p.payrollStatus, COUNT(p) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.payrollStatus")
    List<Object[]> getPayrollCountByStatus(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par période de paie
    @Query("SELECT p.payPeriod, COUNT(p) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.payPeriod")
    List<Object[]> getPayrollCountByPayPeriod(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par méthode de paiement
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.paymentMethod")
    List<Object[]> getPayrollCountByPaymentMethod(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par devise
    @Query("SELECT p.currency, COUNT(p) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.currency")
    List<Object[]> getPayrollCountByCurrency(@Param("entrepriseId") Long entrepriseId);

    // Salaire total par statut
    @Query("SELECT p.payrollStatus, SUM(p.grossSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.payrollStatus")
    List<Object[]> getTotalSalaryByStatus(@Param("entrepriseId") Long entrepriseId);

    // Salaire total par période de paie
    @Query("SELECT p.payPeriod, SUM(p.grossSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.payPeriod")
    List<Object[]> getTotalSalaryByPayPeriod(@Param("entrepriseId") Long entrepriseId);

    // Salaire total par méthode de paiement
    @Query("SELECT p.paymentMethod, SUM(p.grossSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.paymentMethod")
    List<Object[]> getTotalSalaryByPaymentMethod(@Param("entrepriseId") Long entrepriseId);

    // Salaire total par devise
    @Query("SELECT p.currency, SUM(p.grossSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.currency")
    List<Object[]> getTotalSalaryByCurrency(@Param("entrepriseId") Long entrepriseId);

    // Salaire net total par statut
    @Query("SELECT p.payrollStatus, SUM(p.netSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.payrollStatus")
    List<Object[]> getTotalNetSalaryByStatus(@Param("entrepriseId") Long entrepriseId);

    // Salaire net total par période de paie
    @Query("SELECT p.payPeriod, SUM(p.netSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.payPeriod")
    List<Object[]> getTotalNetSalaryByPayPeriod(@Param("entrepriseId") Long entrepriseId);

    // Salaire net total par méthode de paiement
    @Query("SELECT p.paymentMethod, SUM(p.netSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.paymentMethod")
    List<Object[]> getTotalNetSalaryByPaymentMethod(@Param("entrepriseId") Long entrepriseId);

    // Salaire net total par devise
    @Query("SELECT p.currency, SUM(p.netSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.currency")
    List<Object[]> getTotalNetSalaryByCurrency(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par mois
    @Query("SELECT YEAR(p.payDate), MONTH(p.payDate), COUNT(p), SUM(p.grossSalary), SUM(p.netSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY YEAR(p.payDate), MONTH(p.payDate) ORDER BY YEAR(p.payDate) DESC, MONTH(p.payDate) DESC")
    List<Object[]> getPayrollStatisticsByMonth(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par trimestre
    @Query("SELECT YEAR(p.payDate), QUARTER(p.payDate), COUNT(p), SUM(p.grossSalary), SUM(p.netSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY YEAR(p.payDate), QUARTER(p.payDate) ORDER BY YEAR(p.payDate) DESC, QUARTER(p.payDate) DESC")
    List<Object[]> getPayrollStatisticsByQuarter(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par année
    @Query("SELECT YEAR(p.payDate), COUNT(p), SUM(p.grossSalary), SUM(p.netSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY YEAR(p.payDate) ORDER BY YEAR(p.payDate) DESC")
    List<Object[]> getPayrollStatisticsByYear(@Param("entrepriseId") Long entrepriseId);

    // Top des employés par salaire brut
    @Query("SELECT p.employeeId, SUM(p.grossSalary) as totalGross FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.employeeId ORDER BY totalGross DESC")
    List<Object[]> getTopEmployeesByGrossSalary(@Param("entrepriseId") Long entrepriseId);

    // Top des employés par salaire net
    @Query("SELECT p.employeeId, SUM(p.netSalary) as totalNet FROM Payroll p WHERE p.entrepriseId = :entrepriseId GROUP BY p.employeeId ORDER BY totalNet DESC")
    List<Object[]> getTopEmployeesByNetSalary(@Param("entrepriseId") Long entrepriseId);

    // Statistiques des déductions
    @Query("SELECT SUM(p.incomeTax), SUM(p.socialSecurityTax), SUM(p.healthInsurance), SUM(p.pensionContribution), SUM(p.otherDeductions) FROM Payroll p WHERE p.entrepriseId = :entrepriseId")
    List<Object[]> getDeductionStatistics(@Param("entrepriseId") Long entrepriseId);

    // Statistiques des primes et commissions
    @Query("SELECT SUM(p.bonusAmount), SUM(p.allowanceAmount), SUM(p.commissionAmount), SUM(p.overtimePay) FROM Payroll p WHERE p.entrepriseId = :entrepriseId")
    List<Object[]> getBonusAndCommissionStatistics(@Param("entrepriseId") Long entrepriseId);

    // Recherche avancée combinée
    @Query("SELECT p FROM Payroll p WHERE p.entrepriseId = :entrepriseId " +
           "AND (:employeeId IS NULL OR p.employeeId = :employeeId) " +
           "AND (:payrollStatus IS NULL OR p.payrollStatus = :payrollStatus) " +
           "AND (:payPeriod IS NULL OR p.payPeriod = :payPeriod) " +
           "AND (:paymentMethod IS NULL OR p.paymentMethod = :paymentMethod) " +
           "AND (:currency IS NULL OR p.currency = :currency) " +
           "AND (:startDate IS NULL OR p.payDate >= :startDate) " +
           "AND (:endDate IS NULL OR p.payDate <= :endDate) " +
           "AND (:minGrossSalary IS NULL OR p.grossSalary >= :minGrossSalary) " +
           "AND (:maxGrossSalary IS NULL OR p.grossSalary <= :maxGrossSalary)")
    List<Payroll> findPayrollsByAdvancedCriteria(
        @Param("entrepriseId") Long entrepriseId,
        @Param("employeeId") Long employeeId,
        @Param("payrollStatus") Payroll.PayrollStatus payrollStatus,
        @Param("payPeriod") Payroll.PayPeriod payPeriod,
        @Param("paymentMethod") Payroll.PaymentMethod paymentMethod,
        @Param("currency") String currency,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("minGrossSalary") java.math.BigDecimal minGrossSalary,
        @Param("maxGrossSalary") java.math.BigDecimal maxGrossSalary
    );

    // Compter le nombre total de paies par entreprise
    @Query("SELECT COUNT(p) FROM Payroll p WHERE p.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les paies payées par entreprise
    @Query("SELECT COUNT(p) FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'PAID'")
    Long countPaidPayrollsByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les paies approuvées par entreprise
    @Query("SELECT COUNT(p) FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND (p.payrollStatus = 'APPROVED' OR p.payrollStatus = 'PAID')")
    Long countApprovedPayrollsByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les paies en attente par entreprise
    @Query("SELECT COUNT(p) FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'PENDING_APPROVAL'")
    Long countPendingPayrollsByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les paies en brouillon par entreprise
    @Query("SELECT COUNT(p) FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'DRAFT'")
    Long countDraftPayrollsByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Salaire total de l'entreprise
    @Query("SELECT SUM(p.grossSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'PAID'")
    java.math.BigDecimal getTotalPaidSalaryByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Salaire net total de l'entreprise
    @Query("SELECT SUM(p.netSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'PAID'")
    java.math.BigDecimal getTotalPaidNetSalaryByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Salaire moyen par employé
    @Query("SELECT AVG(p.grossSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'PAID'")
    java.math.BigDecimal getAveragePaidSalaryByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Salaire net moyen par employé
    @Query("SELECT AVG(p.netSalary) FROM Payroll p WHERE p.entrepriseId = :entrepriseId AND p.payrollStatus = 'PAID'")
    java.math.BigDecimal getAveragePaidNetSalaryByEntrepriseId(@Param("entrepriseId") Long entrepriseId);
}







