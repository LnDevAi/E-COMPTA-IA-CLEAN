package com.ecomptaia.repository;

import com.ecomptaia.entity.DocumentApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentApprovalRepository extends JpaRepository<DocumentApproval, Long> {
    
    // Recherche par document
    List<DocumentApproval> findByDocumentIdOrderByApprovalLevelAsc(Long documentId);
    
    // Recherche par approbateur
    List<DocumentApproval> findByCompanyIdAndApproverIdOrderByCreatedAtDesc(Long companyId, Long approverId);
    
    // Recherche par statut d'approbation
    List<DocumentApproval> findByCompanyIdAndApprovalStatusOrderByCreatedAtDesc(Long companyId, DocumentApproval.ApprovalStatus approvalStatus);
    
    // Recherche par niveau d'approbation
    List<DocumentApproval> findByDocumentIdAndApprovalLevelOrderByCreatedAtDesc(Long documentId, Integer approvalLevel);
    
    // Recherche par approbations en attente
    List<DocumentApproval> findByCompanyIdAndApprovalStatusOrderByDueDateAsc(Long companyId, DocumentApproval.ApprovalStatus approvalStatus);
    
    // Recherche par approbations en retard
    List<DocumentApproval> findByCompanyIdAndIsOverdueOrderByDueDateAsc(Long companyId, Boolean isOverdue);
    
    // Recherche par approbations requises
    List<DocumentApproval> findByCompanyIdAndIsRequiredOrderByCreatedAtDesc(Long companyId, Boolean isRequired);
    
    // Recherche par date d'échéance
    List<DocumentApproval> findByCompanyIdAndDueDateBeforeOrderByDueDateAsc(Long companyId, LocalDateTime dueDate);
    
    // Recherche par rappels envoyés
    List<DocumentApproval> findByCompanyIdAndReminderSentOrderByLastReminderDateDesc(Long companyId, Boolean reminderSent);
    
    // Recherche par nombre de rappels
    List<DocumentApproval> findByCompanyIdAndReminderCountGreaterThanOrderByReminderCountDesc(Long companyId, Integer reminderCount);
    
    // Recherche par document et approbateur
    Optional<DocumentApproval> findByDocumentIdAndApproverId(Long documentId, Long approverId);
    
    // Recherche par document et niveau d'approbation
    Optional<DocumentApproval> findByDocumentIdAndApprovalLevel(Long documentId, Integer approvalLevel);
    
    // Recherche par approbations récentes
    List<DocumentApproval> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
    
    // Recherche par approbations récemment modifiées
    List<DocumentApproval> findByCompanyIdOrderByUpdatedAtDesc(Long companyId);
    
    // Recherche par approbations avec commentaires
    List<DocumentApproval> findByCompanyIdAndCommentsIsNotNullOrderByCreatedAtDesc(Long companyId);
    

    
    // Statistiques par statut d'approbation
    @Query("SELECT da.approvalStatus, COUNT(da) FROM DocumentApproval da WHERE da.companyId = :companyId GROUP BY da.approvalStatus")
    List<Object[]> getApprovalStatusStats(@Param("companyId") Long companyId);
    
    // Statistiques par niveau d'approbation
    @Query("SELECT da.approvalLevel, COUNT(da) FROM DocumentApproval da WHERE da.companyId = :companyId GROUP BY da.approvalLevel")
    List<Object[]> getApprovalLevelStats(@Param("companyId") Long companyId);
    
    // Recherche des approbations en retard pour un approbateur
    @Query("SELECT da FROM DocumentApproval da WHERE da.companyId = :companyId AND da.approverId = :approverId AND da.isOverdue = true ORDER BY da.dueDate ASC")
    List<DocumentApproval> findOverdueApprovalsForApprover(@Param("companyId") Long companyId, @Param("approverId") Long approverId);
    
    // Recherche des approbations en attente pour un approbateur
    @Query("SELECT da FROM DocumentApproval da WHERE da.companyId = :companyId AND da.approverId = :approverId AND da.approvalStatus = 'PENDING' ORDER BY da.dueDate ASC")
    List<DocumentApproval> findPendingApprovalsForApprover(@Param("companyId") Long companyId, @Param("approverId") Long approverId);
    
    // Recherche des approbations nécessitant des rappels
    @Query("SELECT da FROM DocumentApproval da WHERE da.companyId = :companyId AND da.approvalStatus = 'PENDING' AND da.dueDate < :currentDate AND da.reminderSent = false ORDER BY da.dueDate ASC")
    List<DocumentApproval> findApprovalsNeedingReminders(@Param("companyId") Long companyId, @Param("currentDate") LocalDateTime currentDate);
}
