package com.ecomptaia.repository;

import com.ecomptaia.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    List<JournalEntry> findByCompanyId(Long companyId);

    List<JournalEntry> findByCompanyIdAndStatus(Long companyId, String status);

    List<JournalEntry> findByCompanyIdAndEntryDateBetween(Long companyId, LocalDate startDate, LocalDate endDate);

    List<JournalEntry> findByCompanyIdAndJournalType(Long companyId, String journalType);

    @Query("SELECT je FROM JournalEntry je WHERE je.companyId = :companyId AND je.status = 'VALIDÉ' AND je.isPosted = true")
    List<JournalEntry> findPostedEntriesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT je FROM JournalEntry je WHERE je.companyId = :companyId AND je.status = 'BROUILLON'")
    List<JournalEntry> findDraftEntriesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT je FROM JournalEntry je WHERE je.companyId = :companyId AND je.isReconciled = false")
    List<JournalEntry> findUnreconciledEntriesByCompany(@Param("companyId") Long companyId);

    Optional<JournalEntry> findByEntryNumberAndCompanyId(String entryNumber, Long companyId);

    @Query("SELECT COUNT(je) FROM JournalEntry je WHERE je.companyId = :companyId AND je.status = 'VALIDÉ'")
    Long countValidatedEntriesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT COUNT(je) FROM JournalEntry je WHERE je.companyId = :companyId AND je.status = 'BROUILLON'")
    Long countDraftEntriesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT je FROM JournalEntry je WHERE je.companyId = :companyId AND je.entryDate >= :startDate AND je.entryDate <= :endDate AND je.status = 'VALIDÉ' ORDER BY je.entryDate DESC")
    List<JournalEntry> findValidatedEntriesByDateRange(@Param("companyId") Long companyId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT DISTINCT je.journalType FROM JournalEntry je WHERE je.companyId = :companyId")
    List<String> findDistinctJournalTypesByCompany(@Param("companyId") Long companyId);

    @Query("SELECT je FROM JournalEntry je WHERE je.companyId = :companyId AND je.currency = :currency")
    List<JournalEntry> findByCompanyAndCurrency(@Param("companyId") Long companyId, @Param("currency") String currency);

    @Query("SELECT je FROM JournalEntry je WHERE je.companyId = :companyId AND je.createdBy = :userId")
    List<JournalEntry> findByCompanyAndCreatedBy(@Param("companyId") Long companyId, @Param("userId") Long userId);

    @Query("SELECT je FROM JournalEntry je WHERE je.companyId = :companyId AND je.validatedBy = :userId")
    List<JournalEntry> findByCompanyAndValidatedBy(@Param("companyId") Long companyId, @Param("userId") Long userId);
}
