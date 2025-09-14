package com.ecomptaia.repository;

import com.ecomptaia.entity.DocumentWorkflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des workflows de documents
 */
@Repository
public interface DocumentWorkflowRepository extends JpaRepository<DocumentWorkflow, Long> {
    
    /**
     * Trouver les workflows actifs d'un document
     */
    List<DocumentWorkflow> findByDocumentIdAndIsActiveTrueOrderByCreatedAtDesc(Long documentId);
    
    /**
     * Trouver les workflows d'une entreprise
     */
    List<DocumentWorkflow> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    /**
     * Trouver les workflows par statut
     */
    List<DocumentWorkflow> findByStatusAndCompanyIdAndIsActiveTrue(String status, Long companyId);
    
    /**
     * Trouver les workflows en attente d'approbation
     */
    List<DocumentWorkflow> findByStatusAndIsActiveTrueOrderByCreatedAtAsc(String status);
}