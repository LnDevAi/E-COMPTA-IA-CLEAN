package com.ecomptaia.controller;

import com.ecomptaia.entity.Risk;
import com.ecomptaia.entity.RiskAssessment;
import com.ecomptaia.service.RiskManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risk-management")
@CrossOrigin(origins = "*")
public class RiskManagementController {

    @Autowired
    private RiskManagementService riskManagementService;

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Test de base du module
     */
    @GetMapping("/test/base")
    public ResponseEntity<Map<String, Object>> testBase() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Module de Gestion des Risques opérationnel");
        response.put("timestamp", LocalDateTime.now());
        response.put("module", "Risk Management");
        response.put("version", "1.0");
        return ResponseEntity.ok(response);
    }

    /**
     * Test de création de risque
     */
    @PostMapping("/test/create-risk")
    public ResponseEntity<Map<String, Object>> testCreateRisk() {
        try {
            Risk risk = riskManagementService.createRisk(
                "Risque de fraude informatique",
                Risk.RiskCategory.TECHNOLOGICAL,
                Risk.RiskType.CYBER,
                1L,
                "Risque de cyberattaque ou de fraude informatique"
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Risque créé avec succès");
            response.put("risk", risk);
            response.put("riskId", risk.getId());
            response.put("riskName", risk.getRiskName());
            response.put("riskCategory", risk.getRiskCategory());
            response.put("riskType", risk.getRiskType());
            response.put("riskLevel", risk.getRiskLevel());
            response.put("isActive", risk.getIsActive());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la création du risque: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test de création d'évaluation
     */
    @PostMapping("/test/create-assessment")
    public ResponseEntity<Map<String, Object>> testCreateAssessment() {
        try {
            // Créer d'abord un risque
            Risk risk = riskManagementService.createRisk(
                "Risque de change",
                Risk.RiskCategory.FINANCIAL,
                Risk.RiskType.EXCHANGE_RATE,
                1L,
                "Risque lié aux fluctuations des taux de change"
            );

            // Créer une évaluation
            RiskAssessment assessment = riskManagementService.createAssessment(
                risk.getId(),
                1L,
                RiskAssessment.AssessmentType.INITIAL,
                1L
            );

            // Compléter l'évaluation
            assessment = riskManagementService.completeAssessment(
                assessment.getId(),
                4, // Probabilité élevée
                5, // Impact très élevé
                "Évaluation initiale du risque de change",
                "Mettre en place une couverture de change"
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Évaluation créée et complétée avec succès");
            response.put("assessment", assessment);
            response.put("assessmentId", assessment.getId());
            response.put("riskScore", assessment.getRiskScore());
            response.put("riskLevel", assessment.getRiskLevel());
            response.put("assessmentStatus", assessment.getAssessmentStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la création de l'évaluation: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test d'approbation d'évaluation
     */
    @PostMapping("/test/approve-assessment")
    public ResponseEntity<Map<String, Object>> testApproveAssessment() {
        try {
            // Créer un risque et une évaluation
            Risk risk = riskManagementService.createRisk(
                "Risque opérationnel",
                Risk.RiskCategory.OPERATIONAL,
                Risk.RiskType.OPERATIONAL_FAILURE,
                1L,
                "Risque de défaillance des systèmes opérationnels"
            );

            RiskAssessment assessment = riskManagementService.createAssessment(
                risk.getId(),
                1L,
                RiskAssessment.AssessmentType.PERIODIC,
                1L
            );

            assessment = riskManagementService.completeAssessment(
                assessment.getId(),
                3, // Probabilité moyenne
                4, // Impact élevé
                "Évaluation périodique du risque opérationnel",
                "Renforcer les contrôles internes"
            );

            // Approuver l'évaluation
            assessment = riskManagementService.approveAssessment(
                assessment.getId(),
                2L,
                "Évaluation approuvée par le comité de gestion des risques"
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Évaluation approuvée avec succès");
            response.put("assessment", assessment);
            response.put("approvalStatus", assessment.getApprovalStatus());
            response.put("approvedBy", assessment.getApprovedBy());
            response.put("approvalDate", assessment.getApprovalDate());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de l'approbation: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test des statistiques
     */
    @GetMapping("/test/statistics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testStatistics(@PathVariable Long entrepriseId) {
        try {
            // Créer quelques risques pour les tests
            riskManagementService.createRisk("Risque 1", Risk.RiskCategory.FINANCIAL, Risk.RiskType.CREDIT, entrepriseId, "Description 1");
            riskManagementService.createRisk("Risque 2", Risk.RiskCategory.OPERATIONAL, Risk.RiskType.HUMAN_ERROR, entrepriseId, "Description 2");
            riskManagementService.createRisk("Risque 3", Risk.RiskCategory.COMPLIANCE, Risk.RiskType.REGULATORY, entrepriseId, "Description 3");

            Map<String, Object> riskStats = riskManagementService.getRiskStatistics(entrepriseId);
            Map<String, Object> assessmentStats = riskManagementService.getAssessmentStatistics(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Statistiques récupérées avec succès");
            response.put("riskStatistics", riskStats);
            response.put("assessmentStatistics", assessmentStats);
            response.put("entrepriseId", entrepriseId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des statistiques: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test de recherche
     */
    @GetMapping("/test/search/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testSearch(@PathVariable Long entrepriseId) {
        try {
            // Créer des risques avec des mots-clés spécifiques
            riskManagementService.createRisk("Risque de liquidité bancaire", Risk.RiskCategory.FINANCIAL, Risk.RiskType.LIQUIDITY, entrepriseId, "Risque de liquidité");
            riskManagementService.createRisk("Risque de conformité OHADA", Risk.RiskCategory.COMPLIANCE, Risk.RiskType.REGULATORY, entrepriseId, "Conformité OHADA");

            List<Risk> financialRisks = riskManagementService.searchRisks(entrepriseId, "financier");
            List<Risk> complianceRisks = riskManagementService.searchRisks(entrepriseId, "conformité");

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Recherche effectuée avec succès");
            response.put("financialRisks", financialRisks);
            response.put("complianceRisks", complianceRisks);
            response.put("financialRisksCount", financialRisks.size());
            response.put("complianceRisksCount", complianceRisks.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la recherche: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test de surveillance
     */
    @GetMapping("/test/monitoring/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testMonitoring(@PathVariable Long entrepriseId) {
        try {
            List<Risk> criticalRisks = riskManagementService.getCriticalRisks(entrepriseId);
            List<Risk> overdueRisks = riskManagementService.getOverdueRisks(entrepriseId);
            List<Risk> risksNeedingAssessment = riskManagementService.getRisksNeedingAssessment(entrepriseId);
            List<RiskAssessment> pendingApprovals = riskManagementService.getPendingApprovalAssessments(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Surveillance effectuée avec succès");
            response.put("criticalRisks", criticalRisks);
            response.put("overdueRisks", overdueRisks);
            response.put("risksNeedingAssessment", risksNeedingAssessment);
            response.put("pendingApprovals", pendingApprovals);
            response.put("criticalRisksCount", criticalRisks.size());
            response.put("overdueRisksCount", overdueRisks.size());
            response.put("risksNeedingAssessmentCount", risksNeedingAssessment.size());
            response.put("pendingApprovalsCount", pendingApprovals.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la surveillance: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test complet du module
     */
    @PostMapping("/test/complete")
    public ResponseEntity<Map<String, Object>> testComplete() {
        try {
            Long entrepriseId = 1L;

            // 1. Créer des risques de différentes catégories
            Risk financialRisk = riskManagementService.createRisk(
                "Risque de crédit client",
                Risk.RiskCategory.FINANCIAL,
                Risk.RiskType.CREDIT,
                entrepriseId,
                "Risque de non-paiement des clients"
            );

            Risk operationalRisk = riskManagementService.createRisk(
                "Risque de panne système",
                Risk.RiskCategory.OPERATIONAL,
                Risk.RiskType.OPERATIONAL_FAILURE,
                entrepriseId,
                "Risque de défaillance des systèmes informatiques"
            );

            // 2. Créer et compléter des évaluations
            RiskAssessment assessment1 = riskManagementService.createAssessment(
                financialRisk.getId(),
                1L,
                RiskAssessment.AssessmentType.INITIAL,
                entrepriseId
            );

            assessment1 = riskManagementService.completeAssessment(
                assessment1.getId(),
                4, // Probabilité élevée
                5, // Impact très élevé
                "Évaluation initiale du risque de crédit",
                "Mettre en place des garanties bancaires"
            );

            RiskAssessment assessment2 = riskManagementService.createAssessment(
                operationalRisk.getId(),
                1L,
                RiskAssessment.AssessmentType.PERIODIC,
                entrepriseId
            );

            assessment2 = riskManagementService.completeAssessment(
                assessment2.getId(),
                2, // Probabilité faible
                4, // Impact élevé
                "Évaluation périodique du risque opérationnel",
                "Renforcer la maintenance préventive"
            );

            // 3. Approuver une évaluation
            assessment1 = riskManagementService.approveAssessment(
                assessment1.getId(),
                2L,
                "Approuvé par le directeur financier"
            );

            // 4. Récupérer les statistiques
            Map<String, Object> riskStats = riskManagementService.getRiskStatistics(entrepriseId);
            Map<String, Object> assessmentStats = riskManagementService.getAssessmentStatistics(entrepriseId);

            // 5. Récupérer les données de surveillance
            List<Risk> criticalRisks = riskManagementService.getCriticalRisks(entrepriseId);
            List<RiskAssessment> pendingApprovals = riskManagementService.getPendingApprovalAssessments(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Test complet du module de gestion des risques réussi");
            response.put("risksCreated", 2);
            response.put("assessmentsCreated", 2);
            response.put("assessmentsApproved", 1);
            response.put("riskStatistics", riskStats);
            response.put("assessmentStatistics", assessmentStats);
            response.put("criticalRisksCount", criticalRisks.size());
            response.put("pendingApprovalsCount", pendingApprovals.size());
            response.put("module", "Risk Management");
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors du test complet: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS DE GESTION ====================

    /**
     * Créer un nouveau risque
     */
    @PostMapping("/risks")
    public ResponseEntity<Map<String, Object>> createRisk(@RequestBody Map<String, Object> request) {
        try {
            String riskName = (String) request.get("riskName");
            String categoryStr = (String) request.get("riskCategory");
            String typeStr = (String) request.get("riskType");
            Long entrepriseId = Long.valueOf(request.get("entrepriseId").toString());
            String description = (String) request.get("description");

            Risk.RiskCategory category = Risk.RiskCategory.valueOf(categoryStr);
            Risk.RiskType type = Risk.RiskType.valueOf(typeStr);

            Risk risk = riskManagementService.createRisk(riskName, category, type, entrepriseId, description);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Risque créé avec succès");
            response.put("risk", risk);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la création du risque: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Mettre à jour un risque
     */
    @PutMapping("/risks/{riskId}")
    public ResponseEntity<Map<String, Object>> updateRisk(@PathVariable Long riskId, @RequestBody Risk riskDetails) {
        try {
            Risk risk = riskManagementService.updateRisk(riskId, riskDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Risque mis à jour avec succès");
            response.put("risk", risk);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la mise à jour: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir tous les risques d'une entreprise
     */
    @GetMapping("/risks/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> getRisks(@PathVariable Long entrepriseId) {
        try {
            List<Risk> risks = riskManagementService.getRisksByCategory(entrepriseId, null);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("risks", risks);
            response.put("count", risks.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Créer une évaluation
     */
    @PostMapping("/assessments")
    public ResponseEntity<Map<String, Object>> createAssessment(@RequestBody Map<String, Object> request) {
        try {
            Long riskId = Long.valueOf(request.get("riskId").toString());
            Long assessorId = Long.valueOf(request.get("assessorId").toString());
            String typeStr = (String) request.get("assessmentType");
            Long entrepriseId = Long.valueOf(request.get("entrepriseId").toString());

            RiskAssessment.AssessmentType type = RiskAssessment.AssessmentType.valueOf(typeStr);

            RiskAssessment assessment = riskManagementService.createAssessment(riskId, assessorId, type, entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Évaluation créée avec succès");
            response.put("assessment", assessment);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Compléter une évaluation
     */
    @PutMapping("/assessments/{assessmentId}/complete")
    public ResponseEntity<Map<String, Object>> completeAssessment(@PathVariable Long assessmentId, @RequestBody Map<String, Object> request) {
        try {
            Integer probabilityScore = (Integer) request.get("probabilityScore");
            Integer impactScore = (Integer) request.get("impactScore");
            String assessmentNotes = (String) request.get("assessmentNotes");
            String recommendations = (String) request.get("recommendations");

            RiskAssessment assessment = riskManagementService.completeAssessment(assessmentId, probabilityScore, impactScore, assessmentNotes, recommendations);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Évaluation complétée avec succès");
            response.put("assessment", assessment);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la completion: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statistiques
     */
    @GetMapping("/statistics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> getStatistics(@PathVariable Long entrepriseId) {
        try {
            Map<String, Object> riskStats = riskManagementService.getRiskStatistics(entrepriseId);
            Map<String, Object> assessmentStats = riskManagementService.getAssessmentStatistics(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("riskStatistics", riskStats);
            response.put("assessmentStatistics", assessmentStats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des statistiques: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Rechercher des risques
     */
    @GetMapping("/search/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> searchRisks(@PathVariable Long entrepriseId, @RequestParam String keyword) {
        try {
            List<Risk> risks = riskManagementService.searchRisks(entrepriseId, keyword);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("risks", risks);
            response.put("count", risks.size());
            response.put("keyword", keyword);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la recherche: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS DE TEST SUPPLÉMENTAIRES ====================

    /**
     * Test des catégories de risques
     */
    @GetMapping("/test/risk-categories")
    public ResponseEntity<Map<String, Object>> testRiskCategories() {
        try {
            Map<String, Object> categories = new HashMap<>();
            for (Risk.RiskCategory category : Risk.RiskCategory.values()) {
                categories.put(category.name(), category.getDescription());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Catégories de risques récupérées");
            response.put("categories", categories);
            response.put("count", categories.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des catégories: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test des types de risques
     */
    @GetMapping("/test/risk-types")
    public ResponseEntity<Map<String, Object>> testRiskTypes() {
        try {
            Map<String, Object> types = new HashMap<>();
            for (Risk.RiskType type : Risk.RiskType.values()) {
                types.put(type.name(), type.getDescription());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Types de risques récupérés");
            response.put("types", types);
            response.put("count", types.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des types: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test des niveaux de risques
     */
    @GetMapping("/test/risk-levels")
    public ResponseEntity<Map<String, Object>> testRiskLevels() {
        try {
            Map<String, Object> levels = new HashMap<>();
            for (Risk.RiskLevel level : Risk.RiskLevel.values()) {
                levels.put(level.name(), level.getDescription());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Niveaux de risques récupérés");
            response.put("levels", levels);
            response.put("count", levels.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des niveaux: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
