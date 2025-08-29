package com.ecomptaia.repository;

import com.ecomptaia.entity.DocumentWorkflow;
import com.ecomptaia.entity.GedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentWorkflowRepository extends JpaRepository<DocumentWorkflow, Long> {
    
    // Recherche par entreprise
    List<DocumentWorkflow> findByCompanyIdOrderByWorkflowNameAsc(Long companyId);
    
    // Recherche par type de document
    List<DocumentWorkflow> findByCompanyIdAndDocumentTypeOrderByWorkflowNameAsc(Long companyId, GedDocument.DocumentType documentType);
    
    // Recherche par statut actif
    List<DocumentWorkflow> findByCompanyIdAndIsActiveOrderByWorkflowNameAsc(Long companyId, Boolean isActive);
    
    // Recherche par code de workflow
    Optional<DocumentWorkflow> findByCompanyIdAndWorkflowCode(Long companyId, String workflowCode);
    
    // Recherche par nom de workflow
    List<DocumentWorkflow> findByCompanyIdAndWorkflowNameContainingIgnoreCaseOrderByWorkflowNameAsc(Long companyId, String workflowName);
    
    // Recherche par workflows nécessitant approbation
    List<DocumentWorkflow> findByCompanyIdAndRequiresApprovalOrderByWorkflowNameAsc(Long companyId, Boolean requiresApproval);
    
    // Recherche par workflows avec auto-approbation
    List<DocumentWorkflow> findByCompanyIdAndAutoApproveOrderByWorkflowNameAsc(Long companyId, Boolean autoApprove);
    
    // Recherche par workflows avec auto-archivage
    List<DocumentWorkflow> findByCompanyIdAndAutoArchiveOrderByWorkflowNameAsc(Long companyId, Boolean autoArchive);
    
    // Recherche par nombre de niveaux d'approbation
    List<DocumentWorkflow> findByCompanyIdAndApprovalLevelsOrderByWorkflowNameAsc(Long companyId, Integer approvalLevels);
    
    // Recherche par workflows avec plus d'un niveau d'approbation
    List<DocumentWorkflow> findByCompanyIdAndApprovalLevelsGreaterThanOrderByApprovalLevelsDesc(Long companyId, Integer approvalLevels);
    
    // Recherche par période de rétention
    List<DocumentWorkflow> findByCompanyIdAndRetentionYearsOrderByWorkflowNameAsc(Long companyId, Integer retentionYears);
    
    // Recherche par workflows avec rétention longue
    List<DocumentWorkflow> findByCompanyIdAndRetentionYearsGreaterThanOrderByRetentionYearsDesc(Long companyId, Integer retentionYears);
    
    // Recherche par créateur
    List<DocumentWorkflow> findByCompanyIdAndCreatedByOrderByCreatedAtDesc(Long companyId, Long createdBy);
    
    // Recherche par workflows récemment créés
    List<DocumentWorkflow> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
    
    // Recherche par workflows récemment modifiés
    List<DocumentWorkflow> findByCompanyIdOrderByUpdatedAtDesc(Long companyId);
}
