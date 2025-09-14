package com.ecomptaia.repository;

import com.ecomptaia.entity.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des versions de documents
 */
@Repository
public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
    
    /**
     * Trouver les versions actives d'un document
     */
    List<DocumentVersion> findByDocumentIdAndIsActiveTrueOrderByCreatedAtDesc(Long documentId);
    
    /**
     * Trouver la dernière version d'un document
     */
    DocumentVersion findFirstByDocumentIdAndIsActiveTrueOrderByCreatedAtDesc(Long documentId);
    
    /**
     * Compter les versions d'un document
     */
    long countByDocumentIdAndIsActiveTrue(Long documentId);
}