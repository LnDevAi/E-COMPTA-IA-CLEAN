package com.ecomptaia.repository;

import com.ecomptaia.entity.SocialDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SocialDeclarationRepository extends JpaRepository<SocialDeclaration, Long> {

    // Trouver par entreprise
    List<SocialDeclaration> findByEntrepriseId(Long entrepriseId);

    // Trouver par type de déclaration
    List<SocialDeclaration> findByDeclarationType(SocialDeclaration.DeclarationType declarationType);

    // Trouver par entreprise et type
    List<SocialDeclaration> findByEntrepriseIdAndDeclarationType(Long entrepriseId, 
                                                               SocialDeclaration.DeclarationType declarationType);

    // Trouver par statut de soumission
    List<SocialDeclaration> findBySubmissionStatus(SocialDeclaration.SubmissionStatus submissionStatus);

    // Trouver par entreprise et statut
    List<SocialDeclaration> findByEntrepriseIdAndSubmissionStatus(Long entrepriseId, 
                                                                 SocialDeclaration.SubmissionStatus submissionStatus);

    // Trouver par statut de paiement
    List<SocialDeclaration> findByPaymentStatus(SocialDeclaration.PaymentStatus paymentStatus);

    // Trouver par période
    List<SocialDeclaration> findByPeriodMonthAndPeriodYear(Integer periodMonth, Integer periodYear);

    // Trouver par entreprise et période
    List<SocialDeclaration> findByEntrepriseIdAndPeriodMonthAndPeriodYear(Long entrepriseId, 
                                                                         Integer periodMonth, 
                                                                         Integer periodYear);

    // Trouver les déclarations en retard
    @Query("SELECT sd FROM SocialDeclaration sd WHERE sd.submissionStatus = 'DRAFT' AND sd.periodMonth < :currentMonth AND sd.periodYear <= :currentYear")
    List<SocialDeclaration> findOverdueDeclarations(@Param("currentMonth") Integer currentMonth, 
                                                   @Param("currentYear") Integer currentYear);

    // Trouver les déclarations à soumettre
    @Query("SELECT sd FROM SocialDeclaration sd WHERE sd.submissionStatus = 'READY' OR sd.submissionStatus = 'DRAFT'")
    List<SocialDeclaration> findDeclarationsToSubmit();

    // Trouver par numéro de référence
    List<SocialDeclaration> findByReferenceNumber(String referenceNumber);

    // Trouver par numéro d'employeur
    List<SocialDeclaration> findByEmployerNumber(String employerNumber);

    // Compter par entreprise
    Long countByEntrepriseId(Long entrepriseId);

    // Compter par type de déclaration
    Long countByDeclarationType(SocialDeclaration.DeclarationType declarationType);

    // Compter par statut de soumission
    Long countBySubmissionStatus(SocialDeclaration.SubmissionStatus submissionStatus);

    // Compter par entreprise et statut
    Long countByEntrepriseIdAndSubmissionStatus(Long entrepriseId, 
                                               SocialDeclaration.SubmissionStatus submissionStatus);

    // Trouver les déclarations récentes
    @Query("SELECT sd FROM SocialDeclaration sd WHERE sd.entrepriseId = :entrepriseId ORDER BY sd.createdAt DESC")
    List<SocialDeclaration> findRecentDeclarations(@Param("entrepriseId") Long entrepriseId);

    // Trouver par période et type
    List<SocialDeclaration> findByPeriodMonthAndPeriodYearAndDeclarationType(Integer periodMonth, 
                                                                            Integer periodYear, 
                                                                            SocialDeclaration.DeclarationType declarationType);

    // Trouver les déclarations validées
    @Query("SELECT sd FROM SocialDeclaration sd WHERE sd.submissionStatus = 'VALIDATED' OR sd.submissionStatus = 'APPROVED'")
    List<SocialDeclaration> findValidatedDeclarations();

    // Trouver les déclarations rejetées
    @Query("SELECT sd FROM SocialDeclaration sd WHERE sd.submissionStatus = 'REJECTED'")
    List<SocialDeclaration> findRejectedDeclarations();

    // Trouver par date de soumission
    @Query("SELECT sd FROM SocialDeclaration sd WHERE sd.submissionDate BETWEEN :startDate AND :endDate")
    List<SocialDeclaration> findDeclarationsBySubmissionDate(@Param("startDate") LocalDateTime startDate, 
                                                            @Param("endDate") LocalDateTime endDate);
}
