package com.ecomptaia.repository;

import com.ecomptaia.entity.EcritureComptable;
import com.ecomptaia.entity.Company;
import com.ecomptaia.entity.FinancialPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;

@Repository
public interface EcritureComptableRepository extends JpaRepository<EcritureComptable, Long> {
    
    // Recherche par entreprise
    List<EcritureComptable> findByEntrepriseOrderByDateEcritureDesc(Company entreprise);
    
    // Recherche par exercice
    List<EcritureComptable> findByExerciceOrderByDateEcritureDesc(FinancialPeriod exercice);
    
    // Recherche par entreprise et exercice
    List<EcritureComptable> findByEntrepriseAndExerciceOrderByDateEcritureDesc(Company entreprise, FinancialPeriod exercice);
    
    // Recherche par statut
    List<EcritureComptable> findByStatutOrderByDateEcritureDesc(EcritureComptable.StatutEcriture statut);
    
    // Recherche par type d'écriture
    List<EcritureComptable> findByTypeEcritureOrderByDateEcritureDesc(EcritureComptable.TypeEcriture typeEcriture);
    
    // Recherche par source
    List<EcritureComptable> findBySourceOrderByDateEcritureDesc(EcritureComptable.SourceEcriture source);
    
    // Recherche par période
    List<EcritureComptable> findByDateEcritureBetweenOrderByDateEcritureDesc(LocalDate dateDebut, LocalDate dateFin);
    
    // Recherche par numéro de pièce
    Optional<EcritureComptable> findByNumeroPiece(String numeroPiece);
    
    // Recherche par référence
    List<EcritureComptable> findByReferenceContainingIgnoreCaseOrderByDateEcritureDesc(String reference);
    
    // Recherche par libellé
    List<EcritureComptable> findByLibelleContainingIgnoreCaseOrderByDateEcritureDesc(String libelle);
    
    // Recherche par template
    List<EcritureComptable> findByTemplateIdOrderByDateEcritureDesc(String templateId);
    
    // Recherche par devise
    List<EcritureComptable> findByDeviseOrderByDateEcritureDesc(String devise);
    
    // Recherche par utilisateur
    List<EcritureComptable> findByUtilisateurIdOrderByDateEcritureDesc(Long utilisateurId);
    
    // Recherche avancée avec critères multiples
    @Query("SELECT e FROM EcritureComptable e WHERE " +
           "(:entreprise IS NULL OR e.entreprise = :entreprise) AND " +
           "(:exercice IS NULL OR e.exercice = :exercice) AND " +
           "(:statut IS NULL OR e.statut = :statut) AND " +
           "(:typeEcriture IS NULL OR e.typeEcriture = :typeEcriture) AND " +
           "(:source IS NULL OR e.source = :source) AND " +
           "(:dateDebut IS NULL OR e.dateEcriture >= :dateDebut) AND " +
           "(:dateFin IS NULL OR e.dateEcriture <= :dateFin) AND " +
           "(:devise IS NULL OR e.devise = :devise) AND " +
           "(:utilisateurId IS NULL OR e.utilisateur.id = :utilisateurId) " +
           "ORDER BY e.dateEcriture DESC")
    List<EcritureComptable> findEcrituresWithCriteria(
        @Param("entreprise") Company entreprise,
        @Param("exercice") FinancialPeriod exercice,
        @Param("statut") EcritureComptable.StatutEcriture statut,
        @Param("typeEcriture") EcritureComptable.TypeEcriture typeEcriture,
        @Param("source") EcritureComptable.SourceEcriture source,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin,
        @Param("devise") String devise,
        @Param("utilisateurId") Long utilisateurId
    );
    
    // Recherche par texte (libellé, référence, numéro de pièce)
    @Query("SELECT e FROM EcritureComptable e WHERE " +
           "(:entreprise IS NULL OR e.entreprise = :entreprise) AND " +
           "(:texte IS NULL OR " +
           "LOWER(e.libelle) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(e.reference) LIKE LOWER(CONCAT('%', :texte, '%')) OR " +
           "LOWER(e.numeroPiece) LIKE LOWER(CONCAT('%', :texte, '%'))) " +
           "ORDER BY e.dateEcriture DESC")
    List<EcritureComptable> findByTexte(
        @Param("entreprise") Company entreprise,
        @Param("texte") String texte
    );
    
    // Comptage par statut
    @Query("SELECT e.statut, COUNT(e) FROM EcritureComptable e WHERE e.entreprise = :entreprise GROUP BY e.statut")
    List<Object[]> countByStatut(@Param("entreprise") Company entreprise);
    
    // Comptage par type d'écriture
    @Query("SELECT e.typeEcriture, COUNT(e) FROM EcritureComptable e WHERE e.entreprise = :entreprise GROUP BY e.typeEcriture")
    List<Object[]> countByTypeEcriture(@Param("entreprise") Company entreprise);
    
    // Comptage par source
    @Query("SELECT e.source, COUNT(e) FROM EcritureComptable e WHERE e.entreprise = :entreprise GROUP BY e.source")
    List<Object[]> countBySource(@Param("entreprise") Company entreprise);
    
    // Comptage par mois
    @Query("SELECT YEAR(e.dateEcriture), MONTH(e.dateEcriture), COUNT(e) FROM EcritureComptable e " +
           "WHERE e.entreprise = :entreprise AND e.dateEcriture BETWEEN :dateDebut AND :dateFin " +
           "GROUP BY YEAR(e.dateEcriture), MONTH(e.dateEcriture) ORDER BY YEAR(e.dateEcriture), MONTH(e.dateEcriture)")
    List<Object[]> countByMonth(
        @Param("entreprise") Company entreprise,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );
    
    // Recherche des écritures non équilibrées
    @Query("SELECT e FROM EcritureComptable e WHERE e.totalDebit != e.totalCredit ORDER BY e.dateEcriture DESC")
    List<EcritureComptable> findNonEquilibrees();
    
    // Recherche des écritures avec validation IA faible
    @Query("SELECT e FROM EcritureComptable e WHERE e.validationAiConfiance < :seuilConfiance ORDER BY e.dateEcriture DESC")
    List<EcritureComptable> findByConfianceIAFaible(@Param("seuilConfiance") Double seuilConfiance);
    
    // Recherche des dernières écritures
    @Query("SELECT e FROM EcritureComptable e WHERE e.entreprise = :entreprise ORDER BY e.dateEcriture DESC, e.createdAt DESC")
    List<EcritureComptable> findDernieresEcritures(@Param("entreprise") Company entreprise, Pageable pageable);
    
    // Recherche par montant
    @Query("SELECT e FROM EcritureComptable e WHERE e.totalDebit = :montant OR e.totalCredit = :montant ORDER BY e.dateEcriture DESC")
    List<EcritureComptable> findByMontant(@Param("montant") BigDecimal montant);
    
    // Recherche par plage de montants
    @Query("SELECT e FROM EcritureComptable e WHERE " +
           "e.totalDebit BETWEEN :montantMin AND :montantMax OR " +
           "e.totalCredit BETWEEN :montantMin AND :montantMax " +
           "ORDER BY e.dateEcriture DESC")
    List<EcritureComptable> findByPlageMontants(
        @Param("montantMin") BigDecimal montantMin,
        @Param("montantMax") BigDecimal montantMax
    );
}
