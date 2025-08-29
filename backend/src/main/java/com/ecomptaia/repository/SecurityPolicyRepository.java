package com.ecomptaia.repository;

import com.ecomptaia.entity.SecurityPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityPolicyRepository extends JpaRepository<SecurityPolicy, Long> {

    // ==================== RECHERCHE PAR TYPE ====================

    List<SecurityPolicy> findByPolicyTypeOrderByPriorityAsc(SecurityPolicy.PolicyType policyType);

    List<SecurityPolicy> findByPolicyTypeAndIsActiveTrueOrderByPriorityAsc(SecurityPolicy.PolicyType policyType);

    // ==================== RECHERCHE PAR STATUT ====================

    List<SecurityPolicy> findByIsActiveTrueOrderByPriorityAsc();

    List<SecurityPolicy> findByIsActiveFalseOrderByPriorityAsc();

    // ==================== RECHERCHE PAR ENTREPRISE ====================

    List<SecurityPolicy> findByEntrepriseIdOrderByPriorityAsc(Long entrepriseId);

    List<SecurityPolicy> findByEntrepriseIdAndIsActiveTrueOrderByPriorityAsc(Long entrepriseId);

    List<SecurityPolicy> findByEntrepriseIdAndPolicyTypeOrderByPriorityAsc(Long entrepriseId, SecurityPolicy.PolicyType policyType);

    List<SecurityPolicy> findByEntrepriseIdAndPolicyTypeAndIsActiveTrueOrderByPriorityAsc(Long entrepriseId, SecurityPolicy.PolicyType policyType);

    // ==================== RECHERCHE PAR CRÉATEUR ====================

    List<SecurityPolicy> findByCreatedByOrderByCreatedAtDesc(Long createdBy);

    List<SecurityPolicy> findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(Long createdBy);

    // ==================== STATISTIQUES ====================

    @Query("SELECT COUNT(sp) FROM SecurityPolicy sp WHERE sp.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(sp) FROM SecurityPolicy sp WHERE sp.entrepriseId = :entrepriseId AND sp.isActive = true")
    Long countActiveByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT COUNT(sp) FROM SecurityPolicy sp WHERE sp.entrepriseId = :entrepriseId AND sp.policyType = :policyType")
    Long countByEntrepriseIdAndPolicyType(@Param("entrepriseId") Long entrepriseId, @Param("policyType") SecurityPolicy.PolicyType policyType);

    @Query("SELECT COUNT(sp) FROM SecurityPolicy sp WHERE sp.entrepriseId = :entrepriseId AND sp.policyType = :policyType AND sp.isActive = true")
    Long countActiveByEntrepriseIdAndPolicyType(@Param("entrepriseId") Long entrepriseId, @Param("policyType") SecurityPolicy.PolicyType policyType);

    // ==================== RECHERCHE AVANCÉE ====================

    @Query("SELECT sp FROM SecurityPolicy sp WHERE sp.entrepriseId = :entrepriseId " +
           "AND (:policyType IS NULL OR sp.policyType = :policyType) " +
           "AND (:isActive IS NULL OR sp.isActive = :isActive) " +
           "AND (:policyName IS NULL OR sp.policyName LIKE %:policyName%) " +
           "ORDER BY sp.priority ASC, sp.createdAt DESC")
    List<SecurityPolicy> findPoliciesWithCriteria(@Param("entrepriseId") Long entrepriseId,
                                                 @Param("policyType") SecurityPolicy.PolicyType policyType,
                                                 @Param("isActive") Boolean isActive,
                                                 @Param("policyName") String policyName);

    // ==================== POLITIQUES PAR PRIORITÉ ====================

    @Query("SELECT sp FROM SecurityPolicy sp WHERE sp.entrepriseId = :entrepriseId " +
           "AND sp.isActive = true " +
           "AND sp.policyType = :policyType " +
           "ORDER BY sp.priority ASC")
    List<SecurityPolicy> findActivePoliciesByTypeOrderedByPriority(@Param("entrepriseId") Long entrepriseId,
                                                                  @Param("policyType") SecurityPolicy.PolicyType policyType);

    // ==================== POLITIQUES RÉCENTES ====================

    @Query("SELECT sp FROM SecurityPolicy sp WHERE sp.entrepriseId = :entrepriseId " +
           "ORDER BY sp.createdAt DESC")
    List<SecurityPolicy> findRecentPolicies(@Param("entrepriseId") Long entrepriseId);

    @Query("SELECT sp FROM SecurityPolicy sp WHERE sp.entrepriseId = :entrepriseId " +
           "AND sp.isActive = true " +
           "ORDER BY sp.updatedAt DESC NULLS LAST, sp.createdAt DESC")
    List<SecurityPolicy> findRecentActivePolicies(@Param("entrepriseId") Long entrepriseId);
}




