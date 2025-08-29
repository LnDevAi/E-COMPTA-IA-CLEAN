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
import java.util.Optional;

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
            
            // Ne pas injecter de données mock; retourner simplement la liste (peut être vide)

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
    private void createSampleEmployees() { }
}
