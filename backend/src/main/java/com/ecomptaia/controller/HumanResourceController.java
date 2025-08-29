package com.ecomptaia.controller;

import com.ecomptaia.entity.Employee;
import com.ecomptaia.entity.Payroll;
import com.ecomptaia.entity.Leave;
import com.ecomptaia.service.HumanResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*")
public class HumanResourceController {

    @Autowired
    private HumanResourceService humanResourceService;

    // ==================== ENDPOINTS DE TEST ====================

    @GetMapping("/test/base")
    public ResponseEntity<Map<String, Object>> testBase() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("status", "success");
            response.put("message", "Module RH et Paie opérationnel");
            response.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors du test de base: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/test/complete")
    public ResponseEntity<Map<String, Object>> testComplete() {
        Map<String, Object> response = new HashMap<>();
        try {
            Long entrepriseId = 1L;
            Long approverId = 101L;

            // 1. Créer des employés
            Employee emp1 = humanResourceService.createEmployee(
                "EMP-2024-001", "Jean", "Dupont", "jean.dupont@entreprise.com",
                Employee.Gender.MALE, Employee.ContractType.PERMANENT, "Informatique",
                "Développeur Senior", BigDecimal.valueOf(450000), "XOF", entrepriseId);

            Employee emp2 = humanResourceService.createEmployee(
                "EMP-2024-002", "Marie", "Martin", "marie.martin@entreprise.com",
                Employee.Gender.FEMALE, Employee.ContractType.PERMANENT, "Ressources Humaines",
                "Responsable RH", BigDecimal.valueOf(380000), "XOF", entrepriseId);

            response.put("createdEmployees", List.of(emp1, emp2));

            // 2. Créer des paies
            Payroll payroll1 = humanResourceService.createPayroll(
                "PAY-2024-001", emp1.getId(), Payroll.PayPeriod.MONTHLY,
                LocalDate.of(2024, 1, 31), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31),
                BigDecimal.valueOf(450000), BigDecimal.valueOf(160), BigDecimal.valueOf(10),
                BigDecimal.valueOf(3000), BigDecimal.valueOf(50000), "Prime de performance",
                BigDecimal.valueOf(25000), "Indemnité de transport", entrepriseId);

            Payroll payroll2 = humanResourceService.createPayroll(
                "PAY-2024-002", emp2.getId(), Payroll.PayPeriod.MONTHLY,
                LocalDate.of(2024, 1, 31), LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31),
                BigDecimal.valueOf(380000), BigDecimal.valueOf(160), BigDecimal.valueOf(5),
                BigDecimal.valueOf(2500), BigDecimal.valueOf(30000), "Prime de fin d'année",
                BigDecimal.valueOf(20000), "Indemnité de repas", entrepriseId);

            response.put("createdPayrolls", List.of(payroll1, payroll2));

            // 3. Calculer et approuver les paies
            payroll1 = humanResourceService.calculatePayroll(payroll1.getId());
            payroll1 = humanResourceService.approvePayroll(payroll1.getId(), approverId);
            payroll1 = humanResourceService.markPayrollAsPaid(payroll1.getId(), Payroll.PaymentMethod.BANK_TRANSFER, "VIR-2024-001");

            payroll2 = humanResourceService.calculatePayroll(payroll2.getId());
            payroll2 = humanResourceService.approvePayroll(payroll2.getId(), approverId);
            payroll2 = humanResourceService.markPayrollAsPaid(payroll2.getId(), Payroll.PaymentMethod.BANK_TRANSFER, "VIR-2024-002");

            response.put("processedPayrolls", List.of(payroll1, payroll2));

            // 4. Créer des demandes de congé
            Leave leave1 = humanResourceService.createLeaveRequest(
                "LEAVE-2024-001", emp1.getId(), Leave.LeaveType.ANNUAL,
                LocalDate.of(2024, 7, 15), LocalDate.of(2024, 7, 30), "Congé d'été", entrepriseId);

            Leave leave2 = humanResourceService.createLeaveRequest(
                "LEAVE-2024-002", emp2.getId(), Leave.LeaveType.SICK,
                LocalDate.of(2024, 2, 10), LocalDate.of(2024, 2, 12), "Congé maladie", entrepriseId);

            response.put("createdLeaves", List.of(leave1, leave2));

            // 5. Approuver/rejeter les congés
            leave1 = humanResourceService.approveLeave(leave1.getId(), approverId, "Congé approuvé");
            leave2 = humanResourceService.rejectLeave(leave2.getId(), approverId, "Certificat médical requis");

            response.put("processedLeaves", List.of(leave1, leave2));

            // 6. Obtenir les statistiques
            Map<String, Object> employeeStats = humanResourceService.getEmployeeStatistics(entrepriseId);
            Map<String, Object> payrollStats = humanResourceService.getPayrollStatistics(entrepriseId);
            Map<String, Object> leaveStats = humanResourceService.getLeaveStatistics(entrepriseId);

            response.put("employeeStatistics", employeeStats);
            response.put("payrollStatistics", payrollStats);
            response.put("leaveStatistics", leaveStats);

            response.put("status", "success");
            response.put("message", "Test complet du module RH et Paie exécuté avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de l'exécution du test complet: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS EMPLOYÉS ====================

    @PostMapping("/employees")
    public ResponseEntity<Map<String, Object>> createEmployee(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Employee employee = humanResourceService.createEmployee(
                (String) request.get("employeeCode"),
                (String) request.get("firstName"),
                (String) request.get("lastName"),
                (String) request.get("email"),
                Employee.Gender.valueOf((String) request.get("gender")),
                Employee.ContractType.valueOf((String) request.get("contractType")),
                (String) request.get("department"),
                (String) request.get("position"),
                new BigDecimal(request.get("baseSalary").toString()),
                (String) request.get("salaryCurrency"),
                Long.valueOf(request.get("entrepriseId").toString())
            );
            
            response.put("status", "success");
            response.put("message", "Employé créé avec succès");
            response.put("employee", employee);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la création de l'employé: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/employees/{entrepriseId}")
    public ResponseEntity<Map<String, Object>> getEmployees(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Employee> employees = humanResourceService.getAllEmployeesByEntreprise(entrepriseId);
            response.put("status", "success");
            response.put("employees", employees);
            response.put("count", employees.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des employés: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/employees/{entrepriseId}/search")
    public ResponseEntity<Map<String, Object>> searchEmployees(@PathVariable Long entrepriseId, @RequestParam String term) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Employee> employees = humanResourceService.searchEmployees(entrepriseId, term);
            response.put("status", "success");
            response.put("employees", employees);
            response.put("count", employees.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la recherche d'employés: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/employees/{entrepriseId}/statistics")
    public ResponseEntity<Map<String, Object>> getEmployeeStatistics(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> statistics = humanResourceService.getEmployeeStatistics(entrepriseId);
            response.put("status", "success");
            response.put("statistics", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des statistiques: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS PAIE ====================

    @PostMapping("/payrolls")
    public ResponseEntity<Map<String, Object>> createPayroll(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Payroll payroll = humanResourceService.createPayroll(
                (String) request.get("payrollCode"),
                Long.valueOf(request.get("employeeId").toString()),
                Payroll.PayPeriod.valueOf((String) request.get("payPeriod")),
                LocalDate.parse((String) request.get("payDate")),
                LocalDate.parse((String) request.get("periodStartDate")),
                LocalDate.parse((String) request.get("periodEndDate")),
                new BigDecimal(request.get("baseSalary").toString()),
                new BigDecimal(request.get("hoursWorked").toString()),
                new BigDecimal(request.get("overtimeHours").toString()),
                new BigDecimal(request.get("overtimeRate").toString()),
                new BigDecimal(request.get("bonusAmount").toString()),
                (String) request.get("bonusDescription"),
                new BigDecimal(request.get("allowanceAmount").toString()),
                (String) request.get("allowanceDescription"),
                Long.valueOf(request.get("entrepriseId").toString())
            );
            
            response.put("status", "success");
            response.put("message", "Paie créée avec succès");
            response.put("payroll", payroll);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la création de la paie: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/payrolls/{payrollId}/calculate")
    public ResponseEntity<Map<String, Object>> calculatePayroll(@PathVariable Long payrollId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Payroll payroll = humanResourceService.calculatePayroll(payrollId);
            response.put("status", "success");
            response.put("message", "Paie calculée avec succès");
            response.put("payroll", payroll);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors du calcul de la paie: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/payrolls/{payrollId}/approve")
    public ResponseEntity<Map<String, Object>> approvePayroll(@PathVariable Long payrollId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Payroll payroll = humanResourceService.approvePayroll(payrollId, Long.valueOf(request.get("approvedBy").toString()));
            response.put("status", "success");
            response.put("message", "Paie approuvée avec succès");
            response.put("payroll", payroll);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de l'approbation de la paie: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/payrolls/{payrollId}/mark-paid")
    public ResponseEntity<Map<String, Object>> markPayrollAsPaid(@PathVariable Long payrollId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Payroll payroll = humanResourceService.markPayrollAsPaid(
                payrollId,
                Payroll.PaymentMethod.valueOf((String) request.get("paymentMethod")),
                (String) request.get("bankTransferReference")
            );
            response.put("status", "success");
            response.put("message", "Paie marquée comme payée avec succès");
            response.put("payroll", payroll);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors du marquage de la paie: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/payrolls/{entrepriseId}/statistics")
    public ResponseEntity<Map<String, Object>> getPayrollStatistics(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> statistics = humanResourceService.getPayrollStatistics(entrepriseId);
            response.put("status", "success");
            response.put("statistics", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des statistiques de paie: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS CONGÉS ====================

    @GetMapping("/leaves/test")
    public ResponseEntity<Map<String, Object>> testLeaves() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("status", "success");
            response.put("message", "Endpoint des congés accessible");
            response.put("timestamp", java.time.LocalDateTime.now());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors du test des congés: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/leaves")
    public ResponseEntity<Map<String, Object>> createLeaveRequest(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Leave leave = humanResourceService.createLeaveRequest(
                (String) request.get("leaveCode"),
                Long.valueOf(request.get("employeeId").toString()),
                Leave.LeaveType.valueOf((String) request.get("leaveType")),
                LocalDate.parse((String) request.get("startDate")),
                LocalDate.parse((String) request.get("endDate")),
                (String) request.get("reason"),
                Long.valueOf(request.get("entrepriseId").toString())
            );
            
            response.put("status", "success");
            response.put("message", "Demande de congé créée avec succès");
            response.put("leave", leave);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la création de la demande de congé: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/leaves/{leaveId}/approve")
    public ResponseEntity<Map<String, Object>> approveLeave(@PathVariable Long leaveId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Leave leave = humanResourceService.approveLeave(
                leaveId,
                Long.valueOf(request.get("approvedBy").toString()),
                (String) request.get("approvalNotes")
            );
            response.put("status", "success");
            response.put("message", "Demande de congé approuvée avec succès");
            response.put("leave", leave);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de l'approbation de la demande de congé: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/leaves/{leaveId}/reject")
    public ResponseEntity<Map<String, Object>> rejectLeave(@PathVariable Long leaveId, @RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Leave leave = humanResourceService.rejectLeave(
                leaveId,
                Long.valueOf(request.get("rejectedBy").toString()),
                (String) request.get("rejectionReason")
            );
            response.put("status", "success");
            response.put("message", "Demande de congé rejetée avec succès");
            response.put("leave", leave);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors du rejet de la demande de congé: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/leaves/{entrepriseId}/statistics")
    public ResponseEntity<Map<String, Object>> getLeaveStatistics(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> statistics = humanResourceService.getLeaveStatistics(entrepriseId);
            response.put("status", "success");
            response.put("statistics", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération des statistiques de congés: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS DE STATISTIQUES GÉNÉRALES ====================

    @GetMapping("/{entrepriseId}/dashboard")
    public ResponseEntity<Map<String, Object>> getHRDashboard(@PathVariable Long entrepriseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("employeeStatistics", humanResourceService.getEmployeeStatistics(entrepriseId));
            dashboard.put("payrollStatistics", humanResourceService.getPayrollStatistics(entrepriseId));
            dashboard.put("leaveStatistics", humanResourceService.getLeaveStatistics(entrepriseId));
            
            response.put("status", "success");
            response.put("dashboard", dashboard);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération du tableau de bord: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ==================== ENDPOINTS SIMPLES POUR LES TESTS ====================

    /**
     * Récupérer tous les employés (pour les tests)
     */
    @GetMapping("/employees")
    public ResponseEntity<Map<String, Object>> getAllEmployees() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Employee> employees = humanResourceService.getAllEmployees();
            response.put("status", "success");
            response.put("employees", employees);
            response.put("total", employees.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Récupérer tous les congés (pour les tests)
     */
    @GetMapping("/leaves")
    public ResponseEntity<Map<String, Object>> getAllLeaves() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Leave> leaves = humanResourceService.getAllLeaves();
            response.put("status", "success");
            response.put("leaves", leaves);
            response.put("total", leaves.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Récupérer toutes les paies (pour les tests)
     */
    @GetMapping("/payrolls")
    public ResponseEntity<Map<String, Object>> getAllPayrolls() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Payroll> payrolls = humanResourceService.getAllPayrolls();
            response.put("status", "success");
            response.put("payrolls", payrolls);
            response.put("total", payrolls.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erreur lors de la récupération: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
