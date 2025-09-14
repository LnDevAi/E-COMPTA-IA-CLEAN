package com.ecomptaia.controller;

import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.service.DocumentManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/document-management")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentManagementController {

    @Autowired
    private DocumentManagementService documentManagementService;

    // === GESTION DES DOCUMENTS ===

    /**
     * Récupérer tous les documents (pour les tests)
     */
    @GetMapping("/documents")
    public ResponseEntity<?> getAllDocuments() {
        try {
            List<GedDocument> documents = documentManagementService.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("documents", documents);
            response.put("total", documents.size());
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la récupération: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer un nouveau document
     */
    @PostMapping("/documents")
    public ResponseEntity<?> createDocument(@RequestBody GedDocument document) {
        try {
            GedDocument createdDocument = documentManagementService.save(document);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document créé avec succès");
            response.put("document", createdDocument);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la création: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Mettre à jour un document
     */
    @PutMapping("/documents/{id}")
    public ResponseEntity<?> updateDocument(@PathVariable Long id, @RequestBody GedDocument document) {
        try {
            GedDocument updatedDocument = documentManagementService.save(document);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document mis à jour avec succès");
            response.put("document", updatedDocument);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la mise à jour: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Supprimer un document
     */
    @DeleteMapping("/documents/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            documentManagementService.deleteDocument(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document supprimé avec succès");
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la suppression: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
