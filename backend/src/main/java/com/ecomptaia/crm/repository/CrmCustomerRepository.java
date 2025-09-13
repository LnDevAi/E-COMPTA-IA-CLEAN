package com.ecomptaia.crm.repository;

import com.ecomptaia.crm.entity.CrmCustomer;
import com.ecomptaia.crm.entity.CrmCustomer.CustomerSegment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des clients CRM
 */
@Repository
public interface CrmCustomerRepository extends JpaRepository<CrmCustomer, Long> {
    
    /**
     * Trouver tous les clients d'une entreprise
     */
    Page<CrmCustomer> findByCompanyId(Long companyId, Pageable pageable);
    
    /**
     * Trouver tous les clients actifs d'une entreprise
     */
    List<CrmCustomer> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    /**
     * Trouver les clients par segment
     */
    List<CrmCustomer> findByCompanyIdAndCustomerSegment(Long companyId, CustomerSegment segment);
    
    /**
     * Trouver les clients par ThirdParty
     */
    Optional<CrmCustomer> findByThirdPartyId(Long thirdPartyId);
    
    /**
     * Trouver les clients par score IA minimum
     */
    List<CrmCustomer> findByCompanyIdAndAiScoreGreaterThanEqual(Long companyId, Integer minScore);
    
    /**
     * Trouver les clients à risque de churn
     */
    @Query("SELECT c FROM CrmCustomer c WHERE c.company.id = :companyId AND c.churnProbability >= :minChurnProbability")
    List<CrmCustomer> findHighChurnRiskCustomers(@Param("companyId") Long companyId, @Param("minChurnProbability") Double minChurnProbability);
    
    /**
     * Trouver les clients VIP
     */
    @Query("SELECT c FROM CrmCustomer c WHERE c.company.id = :companyId AND c.customerSegment = 'VIP_HIGH_VALUE'")
    List<CrmCustomer> findVipCustomers(@Param("companyId") Long companyId);
    
    /**
     * Compter les clients par segment
     */
    @Query("SELECT COUNT(c) FROM CrmCustomer c WHERE c.company.id = :companyId AND c.customerSegment = :segment")
    Long countByCompanyIdAndCustomerSegment(@Param("companyId") Long companyId, @Param("segment") CustomerSegment segment);
    
    /**
     * Calculer le revenu total généré par les clients
     */
    @Query("SELECT SUM(c.totalRevenueGenerated) FROM CrmCustomer c WHERE c.company.id = :companyId")
    Double calculateTotalRevenueByCompany(@Param("companyId") Long companyId);
    
    /**
     * Trouver les clients avec le plus haut revenu généré
     */
    @Query("SELECT c FROM CrmCustomer c WHERE c.company.id = :companyId ORDER BY c.totalRevenueGenerated DESC")
    List<CrmCustomer> findTopRevenueCustomers(@Param("companyId") Long companyId, Pageable pageable);
}

