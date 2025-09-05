package com.ecomptaia.repository;

import com.ecomptaia.entity.FinancialPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialPeriodRepository extends JpaRepository<FinancialPeriod, Long> {

    List<FinancialPeriod> findByCompanyIdOrderByStartDateDesc(Long companyId);

    Optional<FinancialPeriod> findByCompanyIdAndIsCurrentTrue(Long companyId);

    List<FinancialPeriod> findByCompanyIdAndStatusOrderByStartDateDesc(Long companyId, String status);

    @Query("SELECT fp FROM FinancialPeriod fp WHERE fp.companyId = :companyId AND fp.status = 'OPEN' ORDER BY fp.startDate DESC")
    List<FinancialPeriod> findOpenPeriodsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT fp FROM FinancialPeriod fp WHERE fp.companyId = :companyId AND fp.status = 'CLOSED' ORDER BY fp.startDate DESC")
    List<FinancialPeriod> findClosedPeriodsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT fp FROM FinancialPeriod fp WHERE fp.companyId = :companyId AND fp.isLocked = true ORDER BY fp.startDate DESC")
    List<FinancialPeriod> findLockedPeriodsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT fp FROM FinancialPeriod fp WHERE fp.companyId = :companyId AND :date BETWEEN fp.startDate AND fp.endDate")
    Optional<FinancialPeriod> findPeriodByDate(@Param("companyId") Long companyId, @Param("date") LocalDate date);

    @Query("SELECT fp FROM FinancialPeriod fp WHERE fp.companyId = :companyId AND fp.startDate >= :startDate AND fp.endDate <= :endDate ORDER BY fp.startDate DESC")
    List<FinancialPeriod> findPeriodsByDateRange(@Param("companyId") Long companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(fp) FROM FinancialPeriod fp WHERE fp.companyId = :companyId AND fp.status = 'OPEN'")
    Long countOpenPeriodsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(fp) FROM FinancialPeriod fp WHERE fp.companyId = :companyId AND fp.status = 'CLOSED'")
    Long countClosedPeriodsByCompany(@Param("companyId") Long companyId);

    @Query("SELECT fp FROM FinancialPeriod fp WHERE fp.companyId = :companyId AND fp.periodName LIKE %:searchTerm% ORDER BY fp.startDate DESC")
    List<FinancialPeriod> searchByPeriodName(@Param("companyId") Long companyId, @Param("searchTerm") String searchTerm);
}








