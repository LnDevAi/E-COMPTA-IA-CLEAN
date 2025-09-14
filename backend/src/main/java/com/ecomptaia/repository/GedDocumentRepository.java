package com.ecomptaia.repository;

import com.ecomptaia.entity.GedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion électronique des documents
 */
@Repository
public interface GedDocumentRepository extends JpaRepository<GedDocument, Long> {
    
    /**
     * Trouver les documents actifs d'une entreprise
     */
    List<GedDocument> findByCompanyIdAndIsActiveTrueOrderByUploadDateDesc(Long companyId);
    
    /**
     * Rechercher des documents par nom
     */
    List<GedDocument> findByFileNameContainingIgnoreCaseAndCompanyIdAndIsActiveTrue(String fileName, Long companyId);
    
    /**
     * Rechercher des documents par type
     */
    List<GedDocument> findByFileTypeAndCompanyIdAndIsActiveTrue(String fileType, Long companyId);
    
    /**
     * Trouver les documents par statut
     */
    List<GedDocument> findByStatusAndCompanyIdAndIsActiveTrue(String status, Long companyId);
    
    /**
     * Compter les documents d'une entreprise
     */
    long countByCompanyIdAndIsActiveTrue(Long companyId);
}