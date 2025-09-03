package com.ecomptaia.controller;

import com.ecomptaia.entity.Employee;
import com.ecomptaia.repository.EmployeeRepository;
import com.ecomptaia.service.HumanResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/hr/employees")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private HumanResourceService humanResourceService;

    /**
     * Récupérer tous les employés
     */
    @GetMapping
    public ResponseEntity<?> getAllEmployees() {
        try {
            // Utiliser le repository pour récupérer les vrais employés
            List<Employee> employees = employeeRepository.findAll();
            
            // Si aucun employé en base, créer des données de test
            if (employees.isEmpty()) {
                // Créer des employés de test
                createSampleEmployees();
                employees = employeeRepository.findAll();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", employees);
            response.put("total", employees.size());
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
     * Récupérer un employé par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        try {
            Employee employee = humanResourceService.getEmployeeById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", employee);
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
     * Créer un nouvel employé
     */
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Map<String, Object> employeeData) {
        try {
            Employee employee = humanResourceService.createEmployee(
                (String) employeeData.get("employeeCode"),
                (String) employeeData.get("firstName"),
                (String) employeeData.get("lastName"),
                (String) employeeData.get("email"),
                Employee.Gender.valueOf((String) employeeData.get("gender")),
                Employee.ContractType.valueOf((String) employeeData.get("contractType")),
                (String) employeeData.get("department"),
                (String) employeeData.get("position"),
                new java.math.BigDecimal(employeeData.get("baseSalary").toString()),
                (String) employeeData.get("salaryCurrency"),
                1L // entrepriseId par défaut
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Employé créé avec succès");
            response.put("data", employee);
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
     * Mettre à jour un employé
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Employee updatedEmployee = humanResourceService.updateEmployee(id, updates);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Employé mis à jour avec succès");
            response.put("data", updatedEmployee);
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
     * Supprimer un employé
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            // Terminer l'employé au lieu de le supprimer
            Employee terminatedEmployee = humanResourceService.terminateEmployee(id, java.time.LocalDate.now(), "Suppression demandée");
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Employé terminé avec succès");
            response.put("data", terminatedEmployee);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la suppression: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Rechercher des employés
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchEmployees(@RequestParam String searchTerm) {
        try {
            List<Employee> employees = humanResourceService.searchEmployees(1L, searchTerm); // entrepriseId par défaut
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", employees);
            response.put("total", employees.size());
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la recherche: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer des employés de test
     */
    private void createSampleEmployees() {
        try {
            // Employé 1
            humanResourceService.createEmployee(
                "EMP001", "Jean", "Dupont", "jean.dupont@entreprise.com",
                Employee.Gender.MALE, Employee.ContractType.CDI, "Finance", "Comptable",
                new java.math.BigDecimal("450000"), "XOF", 1L
            );

            // Employé 2
            humanResourceService.createEmployee(
                "EMP002", "Marie", "Martin", "marie.martin@entreprise.com",
                Employee.Gender.FEMALE, Employee.ContractType.CDI, "Ressources Humaines", "Directeur RH",
                new java.math.BigDecimal("650000"), "XOF", 1L
            );

            // Employé 3
            humanResourceService.createEmployee(
                "EMP003", "Pierre", "Durand", "pierre.durand@entreprise.com",
                Employee.Gender.MALE, Employee.ContractType.CDI, "IT", "Développeur",
                new java.math.BigDecimal("550000"), "XOF", 1L
            );

            // Employé 4
            humanResourceService.createEmployee(
                "EMP004", "Sophie", "Bernard", "sophie.bernard@entreprise.com",
                Employee.Gender.FEMALE, Employee.ContractType.CDI, "Administration", "Assistante",
                new java.math.BigDecimal("350000"), "XOF", 1L
            );
        } catch (Exception e) {
            // Ignorer les erreurs lors de la création des données de test
        }
    }
}
