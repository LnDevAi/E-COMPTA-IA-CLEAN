package com.ecomptaia.sycebnl.repository;

import com.ecomptaia.security.entity.Company;

import com.ecomptaia.sycebnl.entity.SycebnlOrganization;
import com.ecomptaia.sycebnl.entity.SycebnlOrganization.AccountingSystem;
import com.ecomptaia.sycebnl.entity.SycebnlOrganization.OrganizationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des organisations SYCEBNL
 */
@Repository
public interface SycebnlOrganizationRepository extends JpaRepository<SycebnlOrganization, Long> {
    
    /**
     * Trouver toutes les organisations d'une entreprise
     */
    Page<SycebnlOrganization> findByCompanyId(Long companyId, Pageable pageable);
    
    /**
     * Trouver toutes les organisations actives d'une entreprise
     */
    List<SycebnlOrganization> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    /**
     * Trouver les organisations par type
     */
    List<SycebnlOrganization> findByCompanyIdAndOrganizationType(Long companyId, OrganizationType type);
    
    /**
     * Trouver les organisations par système comptable
     */
    List<SycebnlOrganization> findByCompanyIdAndAccountingSystem(Long companyId, AccountingSystem system);
    
    /**
     * Trouver les organisations par numéro d'enregistrement
     */
    Optional<SycebnlOrganization> findByRegistrationNumber(String registrationNumber);
    
    /**
     * Trouver les organisations par numéro d'identification fiscale
     */
    Optional<SycebnlOrganization> findByTaxIdentificationNumber(String taxIdentificationNumber);
    
    /**
     * Compter les organisations par type
     */
    @Query("SELECT COUNT(o) FROM SycebnlOrganization o WHERE o.company.id = :companyId AND o.organizationType = :type")
    Long countByCompanyIdAndOrganizationType(@Param("companyId") Long companyId, @Param("type") OrganizationType type);
    
    /**
     * Compter les organisations par système comptable
     */
    @Query("SELECT COUNT(o) FROM SycebnlOrganization o WHERE o.company.id = :companyId AND o.accountingSystem = :system")
    Long countByCompanyIdAndAccountingSystem(@Param("companyId") Long companyId, @Param("system") AccountingSystem system);
    
    /**
     * Trouver les organisations nécessitant un audit de conformité
     */
    @Query("SELECT o FROM SycebnlOrganization o WHERE o.company.id = :companyId AND o.nextComplianceAudit <= CURRENT_DATE")
    List<SycebnlOrganization> findOrganizationsNeedingComplianceAudit(@Param("companyId") Long companyId);
}

