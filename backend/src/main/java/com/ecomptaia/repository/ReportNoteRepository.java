package com.ecomptaia.repository;

import com.ecomptaia.entity.ReportNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportNoteRepository extends JpaRepository<ReportNote, Long> {

    // ==================== RECHERCHE PAR RAPPORT ====================

    List<ReportNote> findByFinancialReportIdOrderByNoteNumberAsc(Long financialReportId);

    List<ReportNote> findByFinancialReportIdAndEntrepriseIdOrderByNoteNumberAsc(Long financialReportId, Long entrepriseId);

    // ==================== RECHERCHE PAR CATÉGORIE ====================

    List<ReportNote> findByNoteCategoryOrderByNoteNumberAsc(ReportNote.NoteCategory noteCategory);

    List<ReportNote> findByNoteCategoryAndEntrepriseIdOrderByNoteNumberAsc(ReportNote.NoteCategory noteCategory, Long entrepriseId);

    List<ReportNote> findByFinancialReportIdAndNoteCategoryOrderByNoteNumberAsc(Long financialReportId, ReportNote.NoteCategory noteCategory);

    // ==================== RECHERCHE PAR ENTREPRISE ====================

    List<ReportNote> findByEntrepriseIdOrderByCreatedAtDesc(Long entrepriseId);

    // ==================== RECHERCHE PAR CRÉATEUR ====================

    List<ReportNote> findByCreatedByOrderByCreatedAtDesc(Long createdBy);

    List<ReportNote> findByCreatedByAndEntrepriseIdOrderByCreatedAtDesc(Long createdBy, Long entrepriseId);

    // ==================== RECHERCHE PAR PARENT ====================

    List<ReportNote> findByParentNoteIdOrderByOrderIndexAsc(Long parentNoteId);

    List<ReportNote> findByParentNoteIdIsNullOrderByOrderIndexAsc();

    List<ReportNote> findByFinancialReportIdAndParentNoteIdIsNullOrderByOrderIndexAsc(Long financialReportId);

    // ==================== RECHERCHE PAR REQUIS ====================

    List<ReportNote> findByIsRequiredTrueOrderByNoteNumberAsc();

    List<ReportNote> findByFinancialReportIdAndIsRequiredTrueOrderByNoteNumberAsc(Long financialReportId);

    List<ReportNote> findByEntrepriseIdAndIsRequiredTrueOrderByNoteNumberAsc(Long entrepriseId);

    // ==================== RECHERCHE PAR DIVULGATION ====================

    List<ReportNote> findByIsDisclosureTrueOrderByNoteNumberAsc();

    List<ReportNote> findByFinancialReportIdAndIsDisclosureTrueOrderByNoteNumberAsc(Long financialReportId);

    // ==================== STATISTIQUES ====================

    @Query("SELECT COUNT(rn) FROM ReportNote rn WHERE rn.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(rn) FROM ReportNote rn WHERE rn.financialReportId = :financialReportId")
    Long countByFinancialReportId(@Param("financialReportId") Long financialReportId);

    @Query("SELECT COUNT(rn) FROM ReportNote rn WHERE rn.entrepriseId = :entrepriseId AND rn.noteCategory = :noteCategory")
    Long countByEntrepriseIdAndCategory(@Param("entrepriseId") Long entrepriseId, @Param("noteCategory") ReportNote.NoteCategory noteCategory);

    @Query("SELECT COUNT(rn) FROM ReportNote rn WHERE rn.entrepriseId = :entrepriseId AND rn.isRequired = true")
    Long countRequiredByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // ==================== RECHERCHE AVANCÉE ====================

    @Query("SELECT rn FROM ReportNote rn WHERE rn.entrepriseId = :entrepriseId " +
           "AND (:financialReportId IS NULL OR rn.financialReportId = :financialReportId) " +
           "AND (:noteCategory IS NULL OR rn.noteCategory = :noteCategory) " +
           "AND (:isRequired IS NULL OR rn.isRequired = :isRequired) " +
           "AND (:noteTitle IS NULL OR rn.noteTitle LIKE %:noteTitle%) " +
           "ORDER BY rn.noteNumber ASC")
    List<ReportNote> findNotesWithCriteria(@Param("entrepriseId") Long entrepriseId,
                                         @Param("financialReportId") Long financialReportId,
                                         @Param("noteCategory") ReportNote.NoteCategory noteCategory,
                                         @Param("isRequired") Boolean isRequired,
                                         @Param("noteTitle") String noteTitle);

    // ==================== NOTES PAR ORDRE ====================

    @Query("SELECT rn FROM ReportNote rn WHERE rn.financialReportId = :financialReportId ORDER BY rn.orderIndex ASC, rn.noteNumber ASC")
    List<ReportNote> findNotesByOrder(@Param("financialReportId") Long financialReportId);

    // ==================== NOTES AVEC MONTANTS ====================

    @Query("SELECT rn FROM ReportNote rn WHERE rn.entrepriseId = :entrepriseId AND rn.amountValue IS NOT NULL ORDER BY rn.amountValue DESC")
    List<ReportNote> findNotesWithAmounts(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT rn FROM ReportNote rn WHERE rn.financialReportId = :financialReportId AND rn.amountValue IS NOT NULL ORDER BY rn.amountValue DESC")
    List<ReportNote> findNotesWithAmountsByReport(@Param("financialReportId") Long financialReportId);

    // ==================== NOTES AVEC POURCENTAGES ====================

    @Query("SELECT rn FROM ReportNote rn WHERE rn.entrepriseId = :entrepriseId AND rn.percentageValue IS NOT NULL ORDER BY rn.percentageValue DESC")
    List<ReportNote> findNotesWithPercentages(@Param("entrepriseId") Long entrepriseId);

    // ==================== NOTES RÉCENTES ====================

    @Query("SELECT rn FROM ReportNote rn WHERE rn.entrepriseId = :entrepriseId ORDER BY rn.createdAt DESC")
    List<ReportNote> findRecentNotes(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT rn FROM ReportNote rn WHERE rn.financialReportId = :financialReportId ORDER BY rn.createdAt DESC")
    List<ReportNote> findRecentNotesByReport(@Param("financialReportId") Long financialReportId);

    // ==================== NOTES PAR DEVISE ====================

    List<ReportNote> findByAmountCurrencyOrderByAmountValueDesc(String amountCurrency);

    List<ReportNote> findByEntrepriseIdAndAmountCurrencyOrderByAmountValueDesc(Long entrepriseId, String amountCurrency);

    // ==================== NOTES HIÉRARCHIQUES ====================

    @Query("SELECT rn FROM ReportNote rn WHERE rn.financialReportId = :financialReportId AND rn.parentNoteId IS NULL ORDER BY rn.orderIndex ASC")
    List<ReportNote> findParentNotes(@Param("financialReportId") Long financialReportId);

    @Query("SELECT rn FROM ReportNote rn WHERE rn.parentNoteId = :parentNoteId ORDER BY rn.orderIndex ASC")
    List<ReportNote> findChildNotes(@Param("parentNoteId") Long parentNoteId);
}





