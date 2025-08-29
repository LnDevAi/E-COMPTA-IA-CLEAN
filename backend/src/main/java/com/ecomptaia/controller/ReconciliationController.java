package com.ecomptaia.controller;

import com.ecomptaia.entity.Reconciliation;
import com.ecomptaia.service.ReconciliationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/reconciliations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReconciliationController {

    @Autowired
    private ReconciliationService reconciliationService;

    /**
     * Créer un nouveau rapprochement
     */
    @PostMapping
    public ResponseEntity<?> createReconciliation(@RequestBody Reconciliation reconciliation) {
        try {
            Reconciliation createdReconciliation = reconciliationService.createReconciliation(reconciliation);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rapprochement créé avec succès");
            response.put("reconciliation", createdReconciliation);
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
     * Démarrer un rapprochement
     */
    @PutMapping("/{id}/start")
    public ResponseEntity<?> startReconciliation(@PathVariable Long id) {
        try {
            Reconciliation startedReconciliation = reconciliationService.startReconciliation(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rapprochement démarré avec succès");
            response.put("reconciliation", startedReconciliation);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors du démarrage: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Finaliser un rapprochement
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeReconciliation(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long completedBy = Long.valueOf(request.get("completedBy").toString());
            Reconciliation completedReconciliation = reconciliationService.completeReconciliation(id, completedBy);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rapprochement finalisé avec succès");
            response.put("reconciliation", completedReconciliation);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la finalisation: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Annuler un rapprochement
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReconciliation(@PathVariable Long id) {
        try {
            Reconciliation cancelledReconciliation = reconciliationService.cancelReconciliation(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rapprochement annulé avec succès");
            response.put("reconciliation", cancelledReconciliation);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de l'annulation: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Mettre à jour les soldes d'un rapprochement
     */
    @PutMapping("/{id}/balances")
    public ResponseEntity<?> updateBalances(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            BigDecimal bankStatementBalance = new BigDecimal(request.get("bankStatementBalance").toString());
            BigDecimal bookBalance = new BigDecimal(request.get("bookBalance").toString());
            
            Reconciliation updatedReconciliation = reconciliationService.updateBalances(id, bankStatementBalance, bookBalance);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Soldes mis à jour avec succès");
            response.put("reconciliation", updatedReconciliation);
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
     * Rapprochement automatique
     */
    @PostMapping("/{id}/auto-reconcile")
    public ResponseEntity<?> autoReconcile(@PathVariable Long id) {
        try {
            Map<String, Object> result = reconciliationService.autoReconcile(id);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rapprochement automatique effectué");
            response.put("result", result);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors du rapprochement automatique: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Récupérer les rapprochements d'une entreprise
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getReconciliationsByCompany(@PathVariable Long companyId) {
        try {
            List<Reconciliation> reconciliations = reconciliationService.getReconciliationsByCompany(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("reconciliations", reconciliations);
            response.put("total", reconciliations.size());
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
     * Récupérer les rapprochements d'un compte bancaire
     */
    @GetMapping("/bank-account/{bankAccountId}")
    public ResponseEntity<?> getReconciliationsByBankAccount(@PathVariable Long bankAccountId) {
        try {
            List<Reconciliation> reconciliations = reconciliationService.getReconciliationsByBankAccount(bankAccountId);

            Map<String, Object> response = new HashMap<>();
            response.put("reconciliations", reconciliations);
            response.put("total", reconciliations.size());
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
     * Récupérer les rapprochements par statut
     */
    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<?> getReconciliationsByStatus(@PathVariable Long companyId, @PathVariable String status) {
        try {
            List<Reconciliation> reconciliations = reconciliationService.getReconciliationsByStatus(companyId, status);

            Map<String, Object> response = new HashMap<>();
            response.put("reconciliations", reconciliations);
            response.put("total", reconciliations.size());
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
     * Récupérer les rapprochements finalisés
     */
    @GetMapping("/company/{companyId}/completed")
    public ResponseEntity<?> getCompletedReconciliations(@PathVariable Long companyId) {
        try {
            List<Reconciliation> reconciliations = reconciliationService.getCompletedReconciliations(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("reconciliations", reconciliations);
            response.put("total", reconciliations.size());
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
     * Récupérer les rapprochements en cours
     */
    @GetMapping("/company/{companyId}/in-progress")
    public ResponseEntity<?> getInProgressReconciliations(@PathVariable Long companyId) {
        try {
            List<Reconciliation> reconciliations = reconciliationService.getInProgressReconciliations(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("reconciliations", reconciliations);
            response.put("total", reconciliations.size());
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
     * Récupérer les rapprochements avec différences
     */
    @GetMapping("/company/{companyId}/with-differences")
    public ResponseEntity<?> getReconciliationsWithDifferences(@PathVariable Long companyId) {
        try {
            List<Reconciliation> reconciliations = reconciliationService.getReconciliationsWithDifferences(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("reconciliations", reconciliations);
            response.put("total", reconciliations.size());
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
     * Récupérer les rapprochements équilibrés
     */
    @GetMapping("/company/{companyId}/balanced")
    public ResponseEntity<?> getBalancedReconciliations(@PathVariable Long companyId) {
        try {
            List<Reconciliation> reconciliations = reconciliationService.getBalancedReconciliations(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("reconciliations", reconciliations);
            response.put("total", reconciliations.size());
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
     * Statistiques de rapprochement
     */
    @GetMapping("/company/{companyId}/statistics")
    public ResponseEntity<?> getReconciliationStatistics(@PathVariable Long companyId) {
        try {
            Map<String, Object> stats = reconciliationService.getReconciliationStatistics(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("statistics", stats);
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
     * Générer un rapport de rapprochement
     */
    @GetMapping("/{id}/report")
    public ResponseEntity<?> generateReconciliationReport(@PathVariable Long id) {
        try {
            Map<String, Object> report = reconciliationService.generateReconciliationReport(id);

            Map<String, Object> response = new HashMap<>();
            response.put("report", report);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la génération: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }
}
