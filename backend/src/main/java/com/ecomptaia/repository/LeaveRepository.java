package com.ecomptaia.repository;

import com.ecomptaia.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    // Recherche par code de congé
    Optional<Leave> findByLeaveCode(String leaveCode);

    // Recherche par employé
    List<Leave> findByEmployeeId(Long employeeId);

    // Recherche par entreprise
    List<Leave> findByEntrepriseId(Long entrepriseId);

    // Recherche par type de congé
    List<Leave> findByLeaveType(Leave.LeaveType leaveType);

    // Recherche par statut de congé
    List<Leave> findByLeaveStatus(Leave.LeaveStatus leaveStatus);

    // Recherche par date de début
    List<Leave> findByStartDate(LocalDate startDate);

    // Recherche par date de fin
    List<Leave> findByEndDate(LocalDate endDate);

    // Recherche par entreprise et employé
    List<Leave> findByEntrepriseIdAndEmployeeId(Long entrepriseId, Long employeeId);

    // Recherche par entreprise et type de congé
    List<Leave> findByEntrepriseIdAndLeaveType(Long entrepriseId, Leave.LeaveType leaveType);

    // Recherche par entreprise et statut
    List<Leave> findByEntrepriseIdAndLeaveStatus(Long entrepriseId, Leave.LeaveStatus leaveStatus);

    // Recherche par entreprise et date de début
    List<Leave> findByEntrepriseIdAndStartDate(Long entrepriseId, LocalDate startDate);

    // Recherche par entreprise et date de fin
    List<Leave> findByEntrepriseIdAndEndDate(Long entrepriseId, LocalDate endDate);

    // Recherche par entreprise et période (date range)
    List<Leave> findByEntrepriseIdAndStartDateBetween(Long entrepriseId, LocalDate startDate, LocalDate endDate);

    // Recherche par entreprise et période de fin (date range)
    List<Leave> findByEntrepriseIdAndEndDateBetween(Long entrepriseId, LocalDate startDate, LocalDate endDate);

    // Recherche par employé et période (date range)
    List<Leave> findByEmployeeIdAndStartDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    // Recherche par employé et période de fin (date range)
    List<Leave> findByEmployeeIdAndEndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    // Recherche par entreprise et employé et période
    List<Leave> findByEntrepriseIdAndEmployeeIdAndStartDateBetween(Long entrepriseId, Long employeeId, LocalDate startDate, LocalDate endDate);

    // Recherche par entreprise et employé et période de fin
    List<Leave> findByEntrepriseIdAndEmployeeIdAndEndDateBetween(Long entrepriseId, Long employeeId, LocalDate startDate, LocalDate endDate);

    // Recherche des congés approuvés
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'APPROVED'")
    List<Leave> findApprovedLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des congés en attente
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'PENDING'")
    List<Leave> findPendingLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des congés rejetés
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'REJECTED'")
    List<Leave> findRejectedLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des congés annulés
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'CANCELLED'")
    List<Leave> findCancelledLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des congés en cours
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'IN_PROGRESS'")
    List<Leave> findInProgressLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des congés terminés
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'COMPLETED'")
    List<Leave> findCompletedLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des congés actuellement actifs
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.startDate <= :currentDate AND l.endDate >= :currentDate AND (l.leaveStatus = 'APPROVED' OR l.leaveStatus = 'IN_PROGRESS')")
    List<Leave> findCurrentlyActiveLeaves(@Param("entrepriseId") Long entrepriseId, @Param("currentDate") LocalDate currentDate);

    // Recherche des congés futurs
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.startDate > :currentDate")
    List<Leave> findFutureLeaves(@Param("entrepriseId") Long entrepriseId, @Param("currentDate") LocalDate currentDate);

    // Recherche des congés passés
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.endDate < :currentDate")
    List<Leave> findPastLeaves(@Param("entrepriseId") Long entrepriseId, @Param("currentDate") LocalDate currentDate);

    // Recherche par approbateur
    List<Leave> findByEntrepriseIdAndApprovedBy(Long entrepriseId, Long approvedBy);

    // Recherche par rejeteur
    List<Leave> findByEntrepriseIdAndRejectedBy(Long entrepriseId, Long rejectedBy);

    // Recherche par annulateur
    List<Leave> findByEntrepriseIdAndCancelledBy(Long entrepriseId, Long cancelledBy);

    // Recherche par date d'approbation (range)
    List<Leave> findByEntrepriseIdAndApprovedAtBetween(Long entrepriseId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    // Recherche par date de rejet (range)
    List<Leave> findByEntrepriseIdAndRejectedAtBetween(Long entrepriseId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    // Recherche par date d'annulation (range)
    List<Leave> findByEntrepriseIdAndCancelledAtBetween(Long entrepriseId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);

    // Recherche par congés payés
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.paidLeave = true")
    List<Leave> findPaidLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche par congés non payés
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.paidLeave = false")
    List<Leave> findUnpaidLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche par demi-journée
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.halfDay = true")
    List<Leave> findHalfDayLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche par type de demi-journée
    List<Leave> findByEntrepriseIdAndHalfDayType(Long entrepriseId, Leave.HalfDayType halfDayType);

    // Recherche par destination
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND LOWER(l.destination) LIKE LOWER(CONCAT('%', :destination, '%'))")
    List<Leave> findByEntrepriseIdAndDestinationContaining(@Param("entrepriseId") Long entrepriseId, @Param("destination") String destination);

    // Recherche par raison (contient)
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND LOWER(l.reason) LIKE LOWER(CONCAT('%', :reason, '%'))")
    List<Leave> findByEntrepriseIdAndReasonContaining(@Param("entrepriseId") Long entrepriseId, @Param("reason") String reason);

    // Recherche par nombre de jours (range)
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.totalDays BETWEEN :minDays AND :maxDays")
    List<Leave> findByEntrepriseIdAndTotalDaysBetween(@Param("entrepriseId") Long entrepriseId, @Param("minDays") Integer minDays, @Param("maxDays") Integer maxDays);

    // Statistiques par type de congé
    @Query("SELECT l.leaveType, COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY l.leaveType")
    List<Object[]> getLeaveCountByType(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par statut de congé
    @Query("SELECT l.leaveStatus, COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY l.leaveStatus")
    List<Object[]> getLeaveCountByStatus(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par employé
    @Query("SELECT l.employeeId, COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY l.employeeId")
    List<Object[]> getLeaveCountByEmployee(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par mois
    @Query("SELECT YEAR(l.startDate), MONTH(l.startDate), COUNT(l), SUM(l.totalDays) FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY YEAR(l.startDate), MONTH(l.startDate) ORDER BY YEAR(l.startDate) DESC, MONTH(l.startDate) DESC")
    List<Object[]> getLeaveStatisticsByMonth(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par trimestre
    @Query("SELECT YEAR(l.startDate), QUARTER(l.startDate), COUNT(l), SUM(l.totalDays) FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY YEAR(l.startDate), QUARTER(l.startDate) ORDER BY YEAR(l.startDate) DESC, QUARTER(l.startDate) DESC")
    List<Object[]> getLeaveStatisticsByQuarter(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par année
    @Query("SELECT YEAR(l.startDate), COUNT(l), SUM(l.totalDays) FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY YEAR(l.startDate) ORDER BY YEAR(l.startDate) DESC")
    List<Object[]> getLeaveStatisticsByYear(@Param("entrepriseId") Long entrepriseId);

    // Top des employés par nombre de jours de congé
    @Query("SELECT l.employeeId, SUM(l.totalDays) as totalDays FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY l.employeeId ORDER BY totalDays DESC")
    List<Object[]> getTopEmployeesByLeaveDays(@Param("entrepriseId") Long entrepriseId);

    // Top des employés par nombre de congés
    @Query("SELECT l.employeeId, COUNT(l) as leaveCount FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY l.employeeId ORDER BY leaveCount DESC")
    List<Object[]> getTopEmployeesByLeaveCount(@Param("entrepriseId") Long entrepriseId);

    // Statistiques des congés payés vs non payés
    @Query("SELECT l.paidLeave, COUNT(l), SUM(l.totalDays) FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY l.paidLeave")
    List<Object[]> getPaidVsUnpaidLeaveStatistics(@Param("entrepriseId") Long entrepriseId);

    // Statistiques des demi-journées
    @Query("SELECT l.halfDay, COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId GROUP BY l.halfDay")
    List<Object[]> getHalfDayStatistics(@Param("entrepriseId") Long entrepriseId);

    // Recherche avancée combinée
    @Query("SELECT l FROM Leave l WHERE l.entrepriseId = :entrepriseId " +
           "AND (:employeeId IS NULL OR l.employeeId = :employeeId) " +
           "AND (:leaveType IS NULL OR l.leaveType = :leaveType) " +
           "AND (:leaveStatus IS NULL OR l.leaveStatus = :leaveStatus) " +
           "AND (:startDate IS NULL OR l.startDate >= :startDate) " +
           "AND (:endDate IS NULL OR l.endDate <= :endDate) " +
           "AND (:paidLeave IS NULL OR l.paidLeave = :paidLeave) " +
           "AND (:halfDay IS NULL OR l.halfDay = :halfDay) " +
           "AND (:minDays IS NULL OR l.totalDays >= :minDays) " +
           "AND (:maxDays IS NULL OR l.totalDays <= :maxDays)")
    List<Leave> findLeavesByAdvancedCriteria(
        @Param("entrepriseId") Long entrepriseId,
        @Param("employeeId") Long employeeId,
        @Param("leaveType") Leave.LeaveType leaveType,
        @Param("leaveStatus") Leave.LeaveStatus leaveStatus,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("paidLeave") Boolean paidLeave,
        @Param("halfDay") Boolean halfDay,
        @Param("minDays") Integer minDays,
        @Param("maxDays") Integer maxDays
    );

    // Compter le nombre total de congés par entreprise
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les congés approuvés par entreprise
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'APPROVED'")
    Long countApprovedLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les congés en attente par entreprise
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'PENDING'")
    Long countPendingLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les congés rejetés par entreprise
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'REJECTED'")
    Long countRejectedLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les congés annulés par entreprise
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'CANCELLED'")
    Long countCancelledLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les congés en cours par entreprise
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'IN_PROGRESS'")
    Long countInProgressLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les congés terminés par entreprise
    @Query("SELECT COUNT(l) FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'COMPLETED'")
    Long countCompletedLeavesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Nombre total de jours de congé par entreprise
    @Query("SELECT SUM(l.totalDays) FROM Leave l WHERE l.entrepriseId = :entrepriseId")
    Integer getTotalLeaveDaysByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Nombre total de jours de congé approuvés par entreprise
    @Query("SELECT SUM(l.totalDays) FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.leaveStatus = 'APPROVED'")
    Integer getTotalApprovedLeaveDaysByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Nombre total de jours de congé payés par entreprise
    @Query("SELECT SUM(l.totalDays) FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.paidLeave = true")
    Integer getTotalPaidLeaveDaysByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Nombre total de jours de congé non payés par entreprise
    @Query("SELECT SUM(l.totalDays) FROM Leave l WHERE l.entrepriseId = :entrepriseId AND l.paidLeave = false")
    Integer getTotalUnpaidLeaveDaysByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Nombre moyen de jours de congé par employé
    @Query("SELECT AVG(l.totalDays) FROM Leave l WHERE l.entrepriseId = :entrepriseId")
    Double getAverageLeaveDaysByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Nombre moyen de congés par employé
    @Query("SELECT COUNT(l) / COUNT(DISTINCT l.employeeId) FROM Leave l WHERE l.entrepriseId = :entrepriseId")
    Double getAverageLeaveCountByEntrepriseId(@Param("entrepriseId") Long entrepriseId);
}




