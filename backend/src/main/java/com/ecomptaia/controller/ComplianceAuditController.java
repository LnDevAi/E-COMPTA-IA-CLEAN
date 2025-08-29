package com.ecomptaia.controller;

import com.ecomptaia.entity.ComplianceRequirement;
import com.ecomptaia.entity.Audit;
import com.ecomptaia.service.ComplianceAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/compliance-audit")
@CrossOrigin(origins = "*")
public class ComplianceAuditController {

    @Autowired
    private ComplianceAuditService complianceAuditService;

    // ==================== ENDPOINTS DE TEST ====================

    /**
     * Test de base du module
     */
    @GetMapping("/test/base")
    public ResponseEntity<Map<String, Object>> testBase() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Module de Conformité et Audit opérationnel");
        response.put("timestamp", LocalDateTime.now());
        response.put("module", "Compliance & Audit");
        response.put("version", "1.0");
        return ResponseEntity.ok(response);
    }

    /**
     * Test de création d'exigence de conformité
     */
    @PostMapping("/test/create-compliance-requirement")
    public ResponseEntity<Map<String, Object>> testCreateComplianceRequirement() {
        try {
            ComplianceRequirement requirement = complianceAuditService.createComplianceRequirement(
                "OHADA-001",
                "Conformité aux normes comptables OHADA",
                ComplianceRequirement.RequirementCategory.ACCOUNTING_STANDARDS,
                ComplianceRequirement.RequirementType.MANDATORY,
                ComplianceRequirement.PriorityLevel.CRITICAL,
                1L
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Exigence de conformité créée avec succès");
            response.put("requirement", requirement);
            response.put("requirementId", requirement.getId());
            response.put("requirementCode", requirement.getRequirementCode());
            response.put("requirementName", requirement.getRequirementName());
            response.put("requirementCategory", requirement.getRequirementCategory());
            response.put("requirementType", requirement.getRequirementType());
            response.put("priorityLevel", requirement.getPriorityLevel());
            response.put("complianceStatus", requirement.getComplianceStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la création de l'exigence: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test de création d'audit
     */
    @PostMapping("/test/create-audit")
    public ResponseEntity<Map<String, Object>> testCreateAudit() {
        try {
            Audit audit = complianceAuditService.createAudit(
                "AUDIT-2024-001",
                "Audit de conformité OHADA 2024",
                Audit.AuditType.COMPLIANCE,
                Audit.AuditScope.FULL,
                Audit.AuditPriority.HIGH,
                1L,
                1L
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Audit créé avec succès");
            response.put("audit", audit);
            response.put("auditId", audit.getId());
            response.put("auditCode", audit.getAuditCode());
            response.put("auditName", audit.getAuditName());
            response.put("auditType", audit.getAuditType());
            response.put("auditScope", audit.getAuditScope());
            response.put("auditPriority", audit.getAuditPriority());
            response.put("auditStatus", audit.getAuditStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la création de l'audit: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test de mise à jour du statut de conformité
     */
    @PostMapping("/test/update-compliance-status")
    public ResponseEntity<Map<String, Object>> testUpdateComplianceStatus() {
        try {
            // Créer d'abord une exigence
            ComplianceRequirement requirement = complianceAuditService.createComplianceRequirement(
                "OHADA-002",
                "Rapports financiers trimestriels",
                ComplianceRequirement.RequirementCategory.FINANCIAL_REPORTING,
                ComplianceRequirement.RequirementType.MANDATORY,
                ComplianceRequirement.PriorityLevel.HIGH,
                1L
            );

            // Mettre à jour le statut
            requirement = complianceAuditService.updateComplianceStatus(
                requirement.getId(),
                ComplianceRequirement.ComplianceStatus.COMPLIANT,
                "Conformité vérifiée et validée"
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Statut de conformité mis à jour avec succès");
            response.put("requirement", requirement);
            response.put("complianceStatus", requirement.getComplianceStatus());
            response.put("complianceScore", requirement.getComplianceScore());
            response.put("lastComplianceDate", requirement.getLastComplianceDate());
            response.put("nextComplianceDate", requirement.getNextComplianceDate());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la mise à jour du statut: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test de completion d'audit
     */
    @PostMapping("/test/complete-audit")
    public ResponseEntity<Map<String, Object>> testCompleteAudit() {
        try {
            // Créer un audit
            Audit audit = complianceAuditService.createAudit(
                "AUDIT-2024-002",
                "Audit financier interne",
                Audit.AuditType.FINANCIAL,
                Audit.AuditScope.SPECIFIC,
                Audit.AuditPriority.MEDIUM,
                1L,
                1L
            );

            // Démarrer l'audit
            audit = complianceAuditService.startAudit(audit.getId());

            // Terminer l'audit
            audit = complianceAuditService.completeAudit(
                audit.getId(),
                "Aucune anomalie majeure détectée",
                "L'entreprise est conforme aux normes OHADA",
                "Maintenir les procédures actuelles",
                4, // Audit score
                5, // Compliance score
                2  // Risk score
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Audit complété avec succès");
            response.put("audit", audit);
            response.put("auditStatus", audit.getAuditStatus());
            response.put("overallRating", audit.getOverallRating());
            response.put("auditScore", audit.getAuditScore());
            response.put("complianceScore", audit.getComplianceScore());
            response.put("riskScore", audit.getRiskScore());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la completion de l'audit: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test des statistiques de conformité
     */
    @GetMapping("/test/compliance-statistics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testComplianceStatistics(@PathVariable Long entrepriseId) {
        try {
            // Créer quelques exigences pour les tests
            complianceAuditService.createComplianceRequirement("OHADA-003", "Conformité fiscale", 
                ComplianceRequirement.RequirementCategory.TAX_COMPLIANCE, 
                ComplianceRequirement.RequirementType.MANDATORY, 
                ComplianceRequirement.PriorityLevel.HIGH, entrepriseId);

            complianceAuditService.createComplianceRequirement("OHADA-004", "Contrôles internes", 
                ComplianceRequirement.RequirementCategory.INTERNAL_CONTROLS, 
                ComplianceRequirement.RequirementType.RECOMMENDED, 
                ComplianceRequirement.PriorityLevel.MEDIUM, entrepriseId);

            Map<String, Object> statistics = complianceAuditService.getComplianceStatistics(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Statistiques de conformité récupérées avec succès");
            response.put("statistics", statistics);
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
     * Test des statistiques d'audit
     */
    @GetMapping("/test/audit-statistics/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testAuditStatistics(@PathVariable Long entrepriseId) {
        try {
            // Créer quelques audits pour les tests
            complianceAuditService.createAudit("AUDIT-2024-003", "Audit opérationnel", 
                Audit.AuditType.OPERATIONAL, Audit.AuditScope.LIMITED, 
                Audit.AuditPriority.LOW, 1L, entrepriseId);

            complianceAuditService.createAudit("AUDIT-2024-004", "Audit IT", 
                Audit.AuditType.IT_AUDIT, Audit.AuditScope.FOCUSED, 
                Audit.AuditPriority.MEDIUM, 1L, entrepriseId);

            Map<String, Object> statistics = complianceAuditService.getAuditStatistics(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Statistiques d'audit récupérées avec succès");
            response.put("statistics", statistics);
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
     * Test de surveillance
     */
    @GetMapping("/test/monitoring/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> testMonitoring(@PathVariable Long entrepriseId) {
        try {
            List<ComplianceRequirement> overdueRequirements = complianceAuditService.getOverdueRequirements(entrepriseId);
            List<Audit> overdueAudits = complianceAuditService.getOverdueAudits(entrepriseId);
            List<ComplianceRequirement> criticalNonCompliant = complianceAuditService.getCriticalNonCompliantRequirements(entrepriseId);
            List<Audit> auditsRequiringFollowUp = complianceAuditService.getAuditsRequiringFollowUp(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Surveillance effectuée avec succès");
            response.put("overdueRequirements", overdueRequirements);
            response.put("overdueAudits", overdueAudits);
            response.put("criticalNonCompliantRequirements", criticalNonCompliant);
            response.put("auditsRequiringFollowUp", auditsRequiringFollowUp);
            response.put("overdueRequirementsCount", overdueRequirements.size());
            response.put("overdueAuditsCount", overdueAudits.size());
            response.put("criticalNonCompliantCount", criticalNonCompliant.size());
            response.put("auditsRequiringFollowUpCount", auditsRequiringFollowUp.size());

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

            // 1. Créer des exigences de conformité
            ComplianceRequirement requirement1 = complianceAuditService.createComplianceRequirement(
                "OHADA-005", "Gouvernance d'entreprise", 
                ComplianceRequirement.RequirementCategory.CORPORATE_GOVERNANCE, 
                ComplianceRequirement.RequirementType.MANDATORY, 
                ComplianceRequirement.PriorityLevel.CRITICAL, entrepriseId);

            ComplianceRequirement requirement2 = complianceAuditService.createComplianceRequirement(
                "OHADA-006", "Protection des données", 
                ComplianceRequirement.RequirementCategory.DATA_PROTECTION, 
                ComplianceRequirement.RequirementType.RECOMMENDED, 
                ComplianceRequirement.PriorityLevel.HIGH, entrepriseId);

            // 2. Mettre à jour les statuts
            requirement1 = complianceAuditService.updateComplianceStatus(
                requirement1.getId(), ComplianceRequirement.ComplianceStatus.COMPLIANT, "Conforme");

            requirement2 = complianceAuditService.updateComplianceStatus(
                requirement2.getId(), ComplianceRequirement.ComplianceStatus.PARTIALLY_COMPLIANT, "Partiellement conforme");

            // 3. Créer et compléter des audits
            Audit audit1 = complianceAuditService.createAudit(
                "AUDIT-2024-005", "Audit de gouvernance", 
                Audit.AuditType.COMPLIANCE, Audit.AuditScope.FULL, 
                Audit.AuditPriority.HIGH, 1L, entrepriseId);

            audit1 = complianceAuditService.startAudit(audit1.getId());
            audit1 = complianceAuditService.completeAudit(
                audit1.getId(), "Bonne gouvernance", "Conforme", "Continuer", 5, 5, 1);

            // 4. Récupérer les statistiques
            Map<String, Object> complianceStats = complianceAuditService.getComplianceStatistics(entrepriseId);
            Map<String, Object> auditStats = complianceAuditService.getAuditStatistics(entrepriseId);

            // 5. Récupérer les données de surveillance
            List<ComplianceRequirement> overdueRequirements = complianceAuditService.getOverdueRequirements(entrepriseId);
            List<Audit> overdueAudits = complianceAuditService.getOverdueAudits(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Test complet du module de conformité et audit réussi");
            response.put("requirementsCreated", 2);
            response.put("auditsCreated", 1);
            response.put("complianceStatistics", complianceStats);
            response.put("auditStatistics", auditStats);
            response.put("overdueRequirementsCount", overdueRequirements.size());
            response.put("overdueAuditsCount", overdueAudits.size());
            response.put("module", "Compliance & Audit");
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
     * Créer une exigence de conformité
     */
    @PostMapping("/compliance-requirements")
    public ResponseEntity<Map<String, Object>> createComplianceRequirement(@RequestBody Map<String, Object> request) {
        try {
            String requirementCode = (String) request.get("requirementCode");
            String requirementName = (String) request.get("requirementName");
            String categoryStr = (String) request.get("requirementCategory");
            String typeStr = (String) request.get("requirementType");
            String priorityStr = (String) request.get("priorityLevel");
            Long entrepriseId = Long.valueOf(request.get("entrepriseId").toString());

            ComplianceRequirement.RequirementCategory category = ComplianceRequirement.RequirementCategory.valueOf(categoryStr);
            ComplianceRequirement.RequirementType type = ComplianceRequirement.RequirementType.valueOf(typeStr);
            ComplianceRequirement.PriorityLevel priority = ComplianceRequirement.PriorityLevel.valueOf(priorityStr);

            ComplianceRequirement requirement = complianceAuditService.createComplianceRequirement(
                requirementCode, requirementName, category, type, priority, entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Exigence de conformité créée avec succès");
            response.put("requirement", requirement);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Mettre à jour le statut de conformité
     */
    @PutMapping("/compliance-requirements/{requirementId}/status")
    public ResponseEntity<Map<String, Object>> updateComplianceStatus(@PathVariable Long requirementId, 
                                                                     @RequestBody Map<String, Object> request) {
        try {
            String statusStr = (String) request.get("complianceStatus");
            String notes = (String) request.get("notes");

            ComplianceRequirement.ComplianceStatus status = ComplianceRequirement.ComplianceStatus.valueOf(statusStr);

            ComplianceRequirement requirement = complianceAuditService.updateComplianceStatus(requirementId, status, notes);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Statut de conformité mis à jour avec succès");
            response.put("requirement", requirement);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la mise à jour: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Créer un audit
     */
    @PostMapping("/audits")
    public ResponseEntity<Map<String, Object>> createAudit(@RequestBody Map<String, Object> request) {
        try {
            String auditCode = (String) request.get("auditCode");
            String auditName = (String) request.get("auditName");
            String auditTypeStr = (String) request.get("auditType");
            String auditScopeStr = (String) request.get("auditScope");
            String auditPriorityStr = (String) request.get("auditPriority");
            Long auditorId = Long.valueOf(request.get("auditorId").toString());
            Long entrepriseId = Long.valueOf(request.get("entrepriseId").toString());

            Audit.AuditType auditType = Audit.AuditType.valueOf(auditTypeStr);
            Audit.AuditScope auditScope = Audit.AuditScope.valueOf(auditScopeStr);
            Audit.AuditPriority auditPriority = Audit.AuditPriority.valueOf(auditPriorityStr);

            Audit audit = complianceAuditService.createAudit(auditCode, auditName, auditType, auditScope, auditPriority, auditorId, entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Audit créé avec succès");
            response.put("audit", audit);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la création: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Terminer un audit
     */
    @PutMapping("/audits/{auditId}/complete")
    public ResponseEntity<Map<String, Object>> completeAudit(@PathVariable Long auditId, 
                                                           @RequestBody Map<String, Object> request) {
        try {
            String findings = (String) request.get("findings");
            String conclusions = (String) request.get("conclusions");
            String recommendations = (String) request.get("recommendations");
            Integer auditScore = (Integer) request.get("auditScore");
            Integer complianceScore = (Integer) request.get("complianceScore");
            Integer riskScore = (Integer) request.get("riskScore");

            Audit audit = complianceAuditService.completeAudit(auditId, findings, conclusions, recommendations, 
                                                             auditScore, complianceScore, riskScore);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Audit complété avec succès");
            response.put("audit", audit);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la completion: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statistiques de conformité
     */
    @GetMapping("/statistics/compliance/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> getComplianceStatistics(@PathVariable Long entrepriseId) {
        try {
            Map<String, Object> statistics = complianceAuditService.getComplianceStatistics(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("statistics", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Obtenir les statistiques d'audit
     */
    @GetMapping("/statistics/audit/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> getAuditStatistics(@PathVariable Long entrepriseId) {
        try {
            Map<String, Object> statistics = complianceAuditService.getAuditStatistics(entrepriseId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("statistics", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS DE TEST SUPPLÉMENTAIRES ====================

    /**
     * Test des catégories d'exigences
     */
    @GetMapping("/test/requirement-categories")
    public ResponseEntity<Map<String, Object>> testRequirementCategories() {
        try {
            Map<String, Object> categories = new HashMap<>();
            for (ComplianceRequirement.RequirementCategory category : ComplianceRequirement.RequirementCategory.values()) {
                categories.put(category.name(), category.getDescription());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Catégories d'exigences récupérées");
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
     * Test des types d'audit
     */
    @GetMapping("/test/audit-types")
    public ResponseEntity<Map<String, Object>> testAuditTypes() {
        try {
            Map<String, Object> types = new HashMap<>();
            for (Audit.AuditType type : Audit.AuditType.values()) {
                types.put(type.name(), type.getDescription());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Types d'audit récupérés");
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
     * Test des statuts de conformité
     */
    @GetMapping("/test/compliance-statuses")
    public ResponseEntity<Map<String, Object>> testComplianceStatuses() {
        try {
            Map<String, Object> statuses = new HashMap<>();
            for (ComplianceRequirement.ComplianceStatus status : ComplianceRequirement.ComplianceStatus.values()) {
                statuses.put(status.name(), status.getDescription());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Statuts de conformité récupérés");
            response.put("statuses", statuses);
            response.put("count", statuses.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des statuts: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
