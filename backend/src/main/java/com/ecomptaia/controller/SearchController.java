package com.ecomptaia.controller;

import com.ecomptaia.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    // ==================== RECHERCHE GLOBALE ====================

    /**
     * Recherche globale dans tous les modules
     */
    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> globalSearch(
            @RequestParam String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> results = searchService.globalSearch(query, type, companyId, limit);
            response.put("success", true);
            response.put("data", results);
            response.put("query", query);
            response.put("totalResults", results.get("total"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Recherche intelligente avec suggestions
     */
    @GetMapping("/smart")
    public ResponseEntity<Map<String, Object>> smartSearch(
            @RequestParam String query,
            @RequestParam(required = false) Long companyId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> results = searchService.smartSearch(query, companyId);
            response.put("success", true);
            response.put("data", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== RECHERCHE PAR MODULE ====================

    /**
     * Recherche dans les écritures comptables
     */
    @GetMapping("/ecritures")
    public ResponseEntity<Map<String, Object>> searchEcritures(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String numeroPiece,
            @RequestParam(required = false) String libelle,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false, defaultValue = "20") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> results = searchService.searchEcritures(
                query, numeroPiece, libelle, dateDebut, dateFin, statut, type, companyId, page, size);
            response.put("success", true);
            response.put("data", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Recherche dans les comptes
     */
    @GetMapping("/comptes")
    public ResponseEntity<Map<String, Object>> searchComptes(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String numeroCompte,
            @RequestParam(required = false) String nomCompte,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String classe,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false, defaultValue = "20") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> results = searchService.searchComptes(
                query, numeroCompte, nomCompte, type, classe, companyId, page, size);
            response.put("success", true);
            response.put("data", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Recherche dans les tiers
     */
    @GetMapping("/tiers")
    public ResponseEntity<Map<String, Object>> searchTiers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telephone,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false, defaultValue = "20") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> results = searchService.searchTiers(
                query, nom, email, telephone, type, companyId, page, size);
            response.put("success", true);
            response.put("data", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Recherche dans les documents
     */
    @GetMapping("/documents")
    public ResponseEntity<Map<String, Object>> searchDocuments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String nomFichier,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false, defaultValue = "20") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> results = searchService.searchDocuments(
                query, nomFichier, type, dateDebut, dateFin, companyId, page, size);
            response.put("success", true);
            response.put("data", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== FILTRES AVANCÉS ====================

    /**
     * Filtres avancés pour écritures
     */
    @PostMapping("/ecritures/filters")
    public ResponseEntity<Map<String, Object>> advancedEcrituresFilter(
            @RequestParam Long companyId,
            @RequestBody Map<String, Object> filters) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> results = searchService.advancedEcrituresFilter(companyId, filters);
            response.put("success", true);
            response.put("data", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Filtres avancés pour comptes
     */
    @PostMapping("/comptes/filters")
    public ResponseEntity<Map<String, Object>> advancedComptesFilter(
            @RequestParam Long companyId,
            @RequestBody Map<String, Object> filters) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> results = searchService.advancedComptesFilter(companyId, filters);
            response.put("success", true);
            response.put("data", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== SUGGESTIONS ET AUTOCOMPLÉTION ====================

    /**
     * Suggestions de recherche
     */
    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getSearchSuggestions(
            @RequestParam String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false, defaultValue = "5") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> suggestions = searchService.getSearchSuggestions(query, type, companyId, limit);
            response.put("success", true);
            response.put("data", suggestions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Autocomplétion pour les champs
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<Map<String, Object>> getAutocomplete(
            @RequestParam String field,
            @RequestParam String query,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> autocomplete = searchService.getAutocomplete(field, query, companyId, limit);
            response.put("success", true);
            response.put("data", autocomplete);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== RECHERCHE SÉMANTIQUE ====================

    /**
     * Recherche sémantique
     */
    @GetMapping("/semantic")
    public ResponseEntity<Map<String, Object>> semanticSearch(
            @RequestParam String query,
            @RequestParam(required = false) String context,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> results = searchService.semanticSearch(query, context, companyId, limit);
            response.put("success", true);
            response.put("data", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== HISTORIQUE ET FAVORIS ====================

    /**
     * Historique des recherches
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getSearchHistory(
            @RequestParam Long userId,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> history = searchService.getSearchHistory(userId, limit);
            response.put("success", true);
            response.put("data", history);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Recherches favorites
     */
    @GetMapping("/favorites")
    public ResponseEntity<Map<String, Object>> getSearchFavorites(
            @RequestParam Long userId) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> favorites = searchService.getSearchFavorites(userId);
            response.put("success", true);
            response.put("data", favorites);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Sauvegarder une recherche favorite
     */
    @PostMapping("/favorites/save")
    public ResponseEntity<Map<String, Object>> saveSearchFavorite(
            @RequestParam Long userId,
            @RequestParam String name,
            @RequestBody Map<String, Object> searchCriteria) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> saved = searchService.saveSearchFavorite(userId, name, searchCriteria);
            response.put("success", true);
            response.put("data", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Test du système de recherche
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testSearch() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> testData = searchService.getTestSearchData();
            response.put("success", true);
            response.put("message", "Système de recherche opérationnel");
            response.put("data", testData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur lors du test : " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}





