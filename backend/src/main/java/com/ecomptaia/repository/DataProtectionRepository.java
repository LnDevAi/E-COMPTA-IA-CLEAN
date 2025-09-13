package com.ecomptaia.repository;

import com.ecomptaia.entity.DataProtection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion de la protection des données
 */
@Repository
public interface DataProtectionRepository extends JpaRepository<DataProtection, Long> {
    
    /**
     * Trouve la configuration de protection des données par entreprise
     */
    Optional<DataProtection> findByCompanyId(Long companyId);
    
    /**
     * Trouve les configurations par type de réglementation
     */
    List<DataProtection> findByRegulationType(DataProtection.RegulationType regulationType);
    
    /**
     * Trouve les configurations actives
     */
    List<DataProtection> findByIsActiveTrue();
    
    /**
     * Trouve les configurations par entreprise et type de réglementation
     */
    Optional<DataProtection> findByCompanyIdAndRegulationType(Long companyId, DataProtection.RegulationType regulationType);
    
    /**
     * Vérifie si une entreprise a une configuration de protection des données
     */
    boolean existsByCompanyId(Long companyId);
    
    /**
     * Trouve les configurations conformes au RGPD
     */
    @Query("SELECT dp FROM DataProtection dp WHERE dp.regulationType = 'GDPR' AND dp.isActive = true " +
           "AND dp.dataControllerName IS NOT NULL AND dp.dataControllerEmail IS NOT NULL " +
           "AND dp.privacyPolicyUrl IS NOT NULL AND dp.consentRequired = true " +
           "AND dp.dataPortabilityEnabled = true AND dp.rightToErasureEnabled = true " +
           "AND dp.dataBreachNotificationEnabled = true AND dp.auditTrailEnabled = true " +
           "AND dp.encryptionEnabled = true")
    List<DataProtection> findGDPRCompliantConfigurations();
    
    /**
     * Trouve les configurations conformes au CCPA
     */
    @Query("SELECT dp FROM DataProtection dp WHERE dp.regulationType = 'CCPA' AND dp.isActive = true " +
           "AND dp.dataControllerName IS NOT NULL AND dp.dataControllerEmail IS NOT NULL " +
           "AND dp.privacyPolicyUrl IS NOT NULL AND dp.dataPortabilityEnabled = true " +
           "AND dp.rightToErasureEnabled = true AND dp.auditTrailEnabled = true")
    List<DataProtection> findCCPACompliantConfigurations();
    
    /**
     * Trouve les configurations nécessitant une révision
     */
    @Query("SELECT dp FROM DataProtection dp WHERE dp.nextReviewDate <= :currentDate AND dp.isActive = true")
    List<DataProtection> findConfigurationsNeedingReview(@Param("currentDate") java.time.LocalDateTime currentDate);
    
    /**
     * Trouve les configurations par période de rétention
     */
    List<DataProtection> findByDataRetentionPeriodDays(Integer retentionDays);
    
    /**
     * Trouve les configurations créées après une date
     */
    List<DataProtection> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Trouve les configurations mises à jour après une date
     */
    List<DataProtection> findByUpdatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Compte les configurations par type de réglementation
     */
    @Query("SELECT dp.regulationType, COUNT(dp) FROM DataProtection dp WHERE dp.isActive = true GROUP BY dp.regulationType")
    List<Object[]> countByRegulationType();
    
    /**
     * Trouve les configurations avec consentement requis
     */
    List<DataProtection> findByConsentRequiredTrue();
    
    /**
     * Trouve les configurations avec chiffrement activé
     */
    List<DataProtection> findByEncryptionEnabledTrue();
}



