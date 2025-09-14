package com.ecomptaia.repository;

import com.ecomptaia.entity.PieceJustificativeComptable;
import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.FinancialPeriod;
import com.ecomptaia.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des PiÃ¨ces Justificatives Comptables
 */
@Repository
public interface PieceJustificativeComptableRepository extends JpaRepository<PieceJustificativeComptable, Long> {
    
    // ==================== RECHERCHE PAR ENTREPRISE ====================
    
    /**
     * Trouve toutes les PJ d'une entreprise
     */
    List<PieceJustificativeComptable> findByCompanyOrderByDateUploadDesc(Company company);
    
    /**
     * Trouve toutes les PJ d'une entreprise par ID
     */
    List<PieceJustificativeComptable> findByCompanyIdOrderByDateUploadDesc(Long companyId);
    
    /**
     * Trouve toutes les PJ d'un exercice
     */
    List<PieceJustificativeComptable> findByExerciceOrderByDateUploadDesc(FinancialPeriod exercice);
    
    /**
     * Trouve toutes les PJ d'un exercice par ID
     */
    List<PieceJustificativeComptable> findByExerciceIdOrderByDateUploadDesc(Long exerciceId);
    
    // ==================== RECHERCHE PAR STATUT ====================
    
    /**
     * Trouve les PJ par statut de traitement
     */
    List<PieceJustificativeComptable> findByStatutTraitementOrderByDateUploadDesc(
        PieceJustificativeComptable.StatutTraitement statut);
    
    /**
     * Trouve les PJ par statut de traitement et entreprise
     */
    List<PieceJustificativeComptable> findByStatutTraitementAndCompanyOrderByDateUploadDesc(
        PieceJustificativeComptable.StatutTraitement statut, Company company);
    
    /**
     * Trouve les PJ par statut OCR
     */
    List<PieceJustificativeComptable> findByOcrStatutOrderByDateUploadDesc(
        PieceJustificativeComptable.StatutOCR ocrStatut);
    
    /**
     * Trouve les PJ par statut IA
     */
    List<PieceJustificativeComptable> findByIaStatutOrderByDateUploadDesc(
        PieceJustificativeComptable.StatutIA iaStatut);
    
    // ==================== RECHERCHE PAR TYPE ====================
    
    /**
     * Trouve les PJ par type de document
     */
    List<PieceJustificativeComptable> findByTypeDocumentOrderByDateUploadDesc(
        PieceJustificativeComptable.TypeDocument typeDocument);
    
    /**
     * Trouve les PJ par type de document et entreprise
     */
    List<PieceJustificativeComptable> findByTypeDocumentAndCompanyOrderByDateUploadDesc(
        PieceJustificativeComptable.TypeDocument typeDocument, Company company);
    
    // ==================== RECHERCHE PAR UTILISATEUR ====================
    
    /**
     * Trouve les PJ uploadÃ©es par un utilisateur
     */
    List<PieceJustificativeComptable> findByUploadedByOrderByDateUploadDesc(User uploadedBy);
    
    /**
     * Trouve les PJ validÃ©es par un utilisateur
     */
    List<PieceJustificativeComptable> findByValidatedByOrderByDateValidationDesc(User validatedBy);
    
    // ==================== RECHERCHE PAR DATE ====================
    
    /**
     * Trouve les PJ uploadÃ©es entre deux dates
     */
    List<PieceJustificativeComptable> findByDateUploadBetweenOrderByDateUploadDesc(
        LocalDateTime dateDebut, LocalDateTime dateFin);
    
    /**
     * Trouve les PJ uploadÃ©es aprÃ¨s une date
     */
    List<PieceJustificativeComptable> findByDateUploadAfterOrderByDateUploadDesc(LocalDateTime date);
    
    /**
     * Trouve les PJ uploadÃ©es avant une date
     */
    List<PieceJustificativeComptable> findByDateUploadBeforeOrderByDateUploadDesc(LocalDateTime date);
    
    /**
     * Trouve les PJ par date de document
     */
    List<PieceJustificativeComptable> findByDateDocumentOrderByDateUploadDesc(LocalDate dateDocument);
    
    /**
     * Trouve les PJ par plage de dates de document
     */
    List<PieceJustificativeComptable> findByDateDocumentBetweenOrderByDateUploadDesc(
        LocalDate dateDebut, LocalDate dateFin);
    
    // ==================== RECHERCHE PAR MONTANT ====================
    
    /**
     * Trouve les PJ par montant dÃ©tectÃ©
     */
    List<PieceJustificativeComptable> findByMontantDetecteOrderByDateUploadDesc(java.math.BigDecimal montant);
    
    /**
     * Trouve les PJ par plage de montants
     */
    List<PieceJustificativeComptable> findByMontantDetecteBetweenOrderByDateUploadDesc(
        java.math.BigDecimal montantMin, java.math.BigDecimal montantMax);
    
    /**
     * Trouve les PJ avec montant supÃ©rieur Ã 
     */
    List<PieceJustificativeComptable> findByMontantDetecteGreaterThanOrderByDateUploadDesc(
        java.math.BigDecimal montant);
    
    // ==================== RECHERCHE PAR Ã‰CRITURE GÃ‰NÃ‰RÃ‰E ====================
    
    /**
     * Trouve les PJ qui ont gÃ©nÃ©rÃ© une Ã©criture
     */
    List<PieceJustificativeComptable> findByEcritureGenereeIdIsNotNullOrderByDateGenerationEcritureDesc();
    
    /**
     * Trouve une PJ par ID d'Ã©criture gÃ©nÃ©rÃ©e
     */
    Optional<PieceJustificativeComptable> findByEcritureGenereeId(Long ecritureGenereeId);
    
    // ==================== RECHERCHE PAR JOURNAL ====================
    
    /**
     * Trouve les PJ par journal comptable
     */
    List<PieceJustificativeComptable> findByJournalComptableOrderByDateUploadDesc(String journalComptable);
    
    /**
     * Trouve les PJ par journal et entreprise
     */
    List<PieceJustificativeComptable> findByJournalComptableAndCompanyOrderByDateUploadDesc(
        String journalComptable, Company company);
    
    // ==================== RECHERCHE PAR NOM DE FICHIER ====================
    
    /**
     * Trouve une PJ par nom de fichier
     */
    Optional<PieceJustificativeComptable> findByNomFichier(String nomFichier);
    
    /**
     * Trouve une PJ par nom de fichier et entreprise
     */
    Optional<PieceJustificativeComptable> findByNomFichierAndCompany(String nomFichier, Company company);
    
    /**
     * Trouve les PJ par nom de fichier contenant
     */
    List<PieceJustificativeComptable> findByNomFichierContainingIgnoreCaseOrderByDateUploadDesc(String nomFichier);
    
    // ==================== RECHERCHE PAR LIBELLÃ‰ ====================
    
    /**
     * Trouve les PJ par libellÃ© contenant
     */
    List<PieceJustificativeComptable> findByLibelleContainingIgnoreCaseOrderByDateUploadDesc(String libelle);
    
    /**
     * Trouve les PJ par libellÃ© contenant et entreprise
     */
    List<PieceJustificativeComptable> findByLibelleContainingIgnoreCaseAndCompanyOrderByDateUploadDesc(
        String libelle, Company company);
    
    // ==================== RECHERCHE PAR TAGS ====================
    
    /**
     * Trouve les PJ par tag
     */
    @Query("SELECT pj FROM PieceJustificativeComptable pj WHERE pj.tags LIKE %:tag% ORDER BY pj.dateUpload DESC")
    List<PieceJustificativeComptable> findByTag(@Param("tag") String tag);
    
    /**
     * Trouve les PJ par tag et entreprise
     */
    @Query("SELECT pj FROM PieceJustificativeComptable pj WHERE pj.tags LIKE %:tag% AND pj.company = :company ORDER BY pj.dateUpload DESC")
    List<PieceJustificativeComptable> findByTagAndCompany(@Param("tag") String tag, @Param("company") Company company);
    
    // ==================== RECHERCHE AVANCÃ‰E ====================
    
    /**
     * Recherche multicritÃ¨res
     */
    @Query("SELECT pj FROM PieceJustificativeComptable pj WHERE " +
           "(:companyId IS NULL OR pj.company.id = :companyId) AND " +
           "(:exerciceId IS NULL OR pj.exercice.id = :exerciceId) AND " +
           "(:statut IS NULL OR pj.statutTraitement = :statut) AND " +
           "(:typeDocument IS NULL OR pj.typeDocument = :typeDocument) AND " +
           "(:dateDebut IS NULL OR pj.dateUpload >= :dateDebut) AND " +
           "(:dateFin IS NULL OR pj.dateUpload <= :dateFin) AND " +
           "(:montantMin IS NULL OR pj.montantDetecte >= :montantMin) AND " +
           "(:montantMax IS NULL OR pj.montantDetecte <= :montantMax) " +
           "ORDER BY pj.dateUpload DESC")
    List<PieceJustificativeComptable> rechercheAvancee(
        @Param("companyId") Long companyId,
        @Param("exerciceId") Long exerciceId,
        @Param("statut") PieceJustificativeComptable.StatutTraitement statut,
        @Param("typeDocument") PieceJustificativeComptable.TypeDocument typeDocument,
        @Param("dateDebut") LocalDateTime dateDebut,
        @Param("dateFin") LocalDateTime dateFin,
        @Param("montantMin") java.math.BigDecimal montantMin,
        @Param("montantMax") java.math.BigDecimal montantMax
    );
    
    // ==================== STATISTIQUES ====================
    
    /**
     * Compte les PJ par statut
     */
    @Query("SELECT pj.statutTraitement, COUNT(pj) FROM PieceJustificativeComptable pj WHERE pj.company.id = :companyId GROUP BY pj.statutTraitement")
    List<Object[]> getStatistiquesParStatut(@Param("companyId") Long companyId);
    
    /**
     * Compte les PJ par type de document
     */
    @Query("SELECT pj.typeDocument, COUNT(pj) FROM PieceJustificativeComptable pj WHERE pj.company.id = :companyId GROUP BY pj.typeDocument")
    List<Object[]> getStatistiquesParTypeDocument(@Param("companyId") Long companyId);
    
    /**
     * Compte les PJ par mois
     */
    @Query("SELECT YEAR(pj.dateUpload), MONTH(pj.dateUpload), COUNT(pj) FROM PieceJustificativeComptable pj WHERE pj.company.id = :companyId GROUP BY YEAR(pj.dateUpload), MONTH(pj.dateUpload) ORDER BY YEAR(pj.dateUpload) DESC, MONTH(pj.dateUpload) DESC")
    List<Object[]> getStatistiquesParMois(@Param("companyId") Long companyId);
    
    /**
     * Montant total des PJ par entreprise
     */
    @Query("SELECT SUM(pj.montantDetecte) FROM PieceJustificativeComptable pj WHERE pj.company.id = :companyId AND pj.montantDetecte IS NOT NULL")
    java.math.BigDecimal getMontantTotalParEntreprise(@Param("companyId") Long companyId);
    
    /**
     * Montant total des PJ par exercice
     */
    @Query("SELECT SUM(pj.montantDetecte) FROM PieceJustificativeComptable pj WHERE pj.exercice.id = :exerciceId AND pj.montantDetecte IS NOT NULL")
    java.math.BigDecimal getMontantTotalParExercice(@Param("exerciceId") Long exerciceId);
    
    // ==================== VÃ‰RIFICATIONS ====================
    
    /**
     * VÃ©rifie si une PJ existe par nom de fichier et entreprise
     */
    boolean existsByNomFichierAndCompany(String nomFichier, Company company);
    
    /**
     * VÃ©rifie si une PJ existe par numÃ©ro PJ
     */
    boolean existsByNumeroPJ(String numeroPJ);
    
    /**
     * VÃ©rifie si une PJ existe par numÃ©ro PJ et entreprise
     */
    boolean existsByNumeroPJAndCompany(String numeroPJ, Company company);
    
    // ==================== RECHERCHE PAR CONFIANCE IA ====================
    
    /**
     * Trouve les PJ avec confiance IA supÃ©rieure Ã 
     */
    List<PieceJustificativeComptable> findByIaConfidenceGreaterThanOrderByIaConfidenceDesc(
        java.math.BigDecimal confidence);
    
    /**
     * Trouve les PJ avec confiance OCR supÃ©rieure Ã 
     */
    List<PieceJustificativeComptable> findByOcrConfidenceGreaterThanOrderByOcrConfidenceDesc(
        java.math.BigDecimal confidence);
    
    // ==================== RECHERCHE PAR Ã‰TAT DE TRAITEMENT ====================
    
    /**
     * Trouve les PJ en attente de traitement OCR
     */
    @Query("SELECT pj FROM PieceJustificativeComptable pj WHERE pj.ocrStatut = 'PENDING' ORDER BY pj.dateUpload ASC")
    List<PieceJustificativeComptable> findPJEnAttenteOCR();
    
    /**
     * Trouve les PJ en attente de traitement IA
     */
    @Query("SELECT pj FROM PieceJustificativeComptable pj WHERE pj.ocrStatut = 'COMPLETED' AND pj.iaStatut = 'PENDING' ORDER BY pj.ocrDateTraitement ASC")
    List<PieceJustificativeComptable> findPJEnAttenteIA();
    
    /**
     * Trouve les PJ prÃªtes pour validation
     */
    @Query("SELECT pj FROM PieceJustificativeComptable pj WHERE pj.statutTraitement = 'PROPOSITIONS_READY' ORDER BY pj.iaDateTraitement ASC")
    List<PieceJustificativeComptable> findPJPretesPourValidation();
}

