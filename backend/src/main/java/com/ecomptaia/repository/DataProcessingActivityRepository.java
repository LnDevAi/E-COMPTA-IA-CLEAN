package com.ecomptaia.repository;

import com.ecomptaia.entity.DataProcessingActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des activités de traitement des données
 */
@Repository
public interface DataProcessingActivityRepository extends JpaRepository<DataProcessingActivity, Long> {
    
    /**
     * Trouve les activités par entreprise
     */
    List<DataProcessingActivity> findByCompanyId(Long companyId);
    
    /**
     * Trouve les activités actives
     */
    List<DataProcessingActivity> findByIsActiveTrue();
    
    /**
     * Trouve les activités par entreprise et statut actif
     */
    List<DataProcessingActivity> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    /**
     * Trouve une activité par nom et entreprise
     */
    Optional<DataProcessingActivity> findByActivityNameAndCompanyId(String activityName, Long companyId);
    
    /**
     * Trouve les activités par base légale
     */
    List<DataProcessingActivity> findByLegalBasis(DataProcessingActivity.LegalBasis legalBasis);
    
    /**
     * Trouve les activités par entreprise et base légale
     */
    List<DataProcessingActivity> findByCompanyIdAndLegalBasis(Long companyId, DataProcessingActivity.LegalBasis legalBasis);
    
    /**
     * Vérifie si une activité existe par nom et entreprise
     */
    boolean existsByActivityNameAndCompanyId(String activityName, Long companyId);
    
    /**
     * Trouve les activités par période de rétention
     */
    List<DataProcessingActivity> findByRetentionPeriod(String retentionPeriod);
    
    /**
     * Trouve les activités créées après une date
     */
    List<DataProcessingActivity> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Trouve les activités mises à jour après une date
     */
    List<DataProcessingActivity> findByUpdatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Trouve les activités par configuration de protection des données
     */
    List<DataProcessingActivity> findByDataProtectionId(Long dataProtectionId);
    
    /**
     * Compte les activités par base légale
     */
    @Query("SELECT dpa.legalBasis, COUNT(dpa) FROM DataProcessingActivity dpa WHERE dpa.isActive = true GROUP BY dpa.legalBasis")
    List<Object[]> countByLegalBasis();
    
    /**
     * Compte les activités par entreprise
     */
    @Query("SELECT dpa.companyId, COUNT(dpa) FROM DataProcessingActivity dpa WHERE dpa.isActive = true GROUP BY dpa.companyId")
    List<Object[]> countByCompany();
    
    /**
     * Trouve les activités avec consentement comme base légale
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.legalBasis = 'CONSENT' AND dpa.isActive = true")
    List<DataProcessingActivity> findActivitiesBasedOnConsent();
    
    /**
     * Trouve les activités avec intérêts légitimes comme base légale
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.legalBasis = 'LEGITIMATE_INTERESTS' AND dpa.isActive = true")
    List<DataProcessingActivity> findActivitiesBasedOnLegitimateInterests();
    
    /**
     * Trouve les activités par mot-clé dans le nom
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.activityName LIKE %:keyword% AND dpa.isActive = true")
    List<DataProcessingActivity> findByActivityNameContaining(@Param("keyword") String keyword);
    
    /**
     * Trouve les activités par mot-clé dans la description
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.activityDescription LIKE %:keyword% AND dpa.isActive = true")
    List<DataProcessingActivity> findByActivityDescriptionContaining(@Param("keyword") String keyword);
    
    /**
     * Trouve les activités par mot-clé dans le but
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.purpose LIKE %:keyword% AND dpa.isActive = true")
    List<DataProcessingActivity> findByPurposeContaining(@Param("keyword") String keyword);
    
    /**
     * Trouve les activités avec transferts vers des pays tiers
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.thirdCountryTransfers IS NOT NULL AND dpa.thirdCountryTransfers != '' AND dpa.isActive = true")
    List<DataProcessingActivity> findActivitiesWithThirdCountryTransfers();
    
    /**
     * Trouve les activités avec mesures de sécurité spécifiées
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.securityMeasures IS NOT NULL AND dpa.securityMeasures != '' AND dpa.isActive = true")
    List<DataProcessingActivity> findActivitiesWithSecurityMeasures();
    
    /**
     * Trouve les activités par catégorie de données
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.dataCategories LIKE %:category% AND dpa.isActive = true")
    List<DataProcessingActivity> findByDataCategory(@Param("category") String category);
    
    /**
     * Trouve les activités par catégorie de personnes concernées
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.dataSubjects LIKE %:subject% AND dpa.isActive = true")
    List<DataProcessingActivity> findByDataSubject(@Param("subject") String subject);
    
    /**
     * Trouve les activités par destinataire
     */
    @Query("SELECT dpa FROM DataProcessingActivity dpa WHERE dpa.recipients LIKE %:recipient% AND dpa.isActive = true")
    List<DataProcessingActivity> findByRecipient(@Param("recipient") String recipient);
}



