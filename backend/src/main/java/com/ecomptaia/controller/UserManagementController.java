package com.ecomptaia.controller;

import com.ecomptaia.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Contrôleur pour la gestion des utilisateurs et permissions
 */
@RestController
@RequestMapping("/api/user-management")
@CrossOrigin(origins = "*")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    /**
     * Création d'un nouvel utilisateur
     */
    @PostMapping("/create-user")
    public ResponseEntity<Map<String, Object>> createUser(
            @RequestParam("companyId") Long companyId,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("role") String role) {
        
        try {
            Map<String, Object> result = userManagementService.createUser(
                companyId, username, email, firstName, lastName, role);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la création: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Test de création d'utilisateur (GET)
     */
    @GetMapping("/test-create-user")
    public ResponseEntity<Map<String, Object>> testCreateUser() {
        try {
            Map<String, Object> result = userManagementService.createUser(
                1L, "testuser", "test@company.com", "Test", "User", "ACCOUNTANT");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors du test: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Test de création d'utilisateur avec paramètres (GET)
     */
    @GetMapping("/test-create-user-with-params")
    public ResponseEntity<Map<String, Object>> testCreateUserWithParams(
            @RequestParam(value = "companyId", defaultValue = "1") Long companyId,
            @RequestParam(value = "username", defaultValue = "john.doe") String username,
            @RequestParam(value = "email", defaultValue = "john@company.com") String email,
            @RequestParam(value = "firstName", defaultValue = "John") String firstName,
            @RequestParam(value = "lastName", defaultValue = "Doe") String lastName,
            @RequestParam(value = "role", defaultValue = "ACCOUNTANT") String role) {
        
        try {
            Map<String, Object> result = userManagementService.createUser(
                companyId, username, email, firstName, lastName, role);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors du test: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Test simple de connexion (GET)
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Connexion réussie au service de gestion des utilisateurs");
        response.put("status", "SUCCESS");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Récupération des utilisateurs d'une entreprise
     */
    @GetMapping("/company-users")
    public ResponseEntity<Map<String, Object>> getCompanyUsers(
            @RequestParam("companyId") Long companyId) {
        
        try {
            List<Map<String, Object>> users = userManagementService.getCompanyUsers(companyId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("companyId", companyId);
            result.put("users", users);
            result.put("totalUsers", users.size());
            result.put("status", "SUCCESS");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Récupération des rôles disponibles
     */
    @GetMapping("/available-roles")
    public ResponseEntity<Map<String, Object>> getAvailableRoles() {
        try {
            List<Map<String, Object>> roles = userManagementService.getAvailableRoles();
            
            Map<String, Object> result = new HashMap<>();
            result.put("roles", roles);
            result.put("totalRoles", roles.size());
            result.put("status", "SUCCESS");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la récupération: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }

    /**
     * Test de la gestion des utilisateurs
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testUserManagement() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Service de gestion des utilisateurs opérationnel");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("create-user", "POST /api/user-management/create-user");
        endpoints.put("test-create-user", "GET /api/user-management/test-create-user");
        endpoints.put("test-create-user-with-params", "GET /api/user-management/test-create-user-with-params?companyId=1&username=john.doe&email=john@company.com&firstName=John&lastName=Doe&role=ACCOUNTANT");
        endpoints.put("company-users", "GET /api/user-management/company-users?companyId=1");
        endpoints.put("available-roles", "GET /api/user-management/available-roles");
        endpoints.put("test", "GET /api/user-management/test");
        endpoints.put("demo", "GET /api/user-management/demo");
        
        Map<String, String> features = new HashMap<>();
        features.put("userCreation", "Création d'utilisateurs avec rôles");
        features.put("userManagement", "Gestion des utilisateurs par entreprise");
        features.put("roleManagement", "Système de rôles et permissions");
        features.put("permissions", "Permissions granulaires par fonctionnalité");
        features.put("auditTrail", "Traçabilité des actions utilisateurs");
        features.put("security", "Sécurité et contrôle d'accès");
        
        response.put("endpoints", endpoints);
        response.put("features", features);
        response.put("supportedRoles", List.of("ADMIN", "MANAGER", "ACCOUNTANT", "USER"));
        response.put("status", "ready");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Démonstration de la gestion des utilisateurs
     */
    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> getDemo() {
        try {
            Map<String, Object> demo = new HashMap<>();
            Map<String, Object> examples = new HashMap<>();
            
            // Exemple d'utilisateur créé
            Map<String, Object> userExample = new HashMap<>();
            userExample.put("userId", 123456789L);
            userExample.put("companyId", 1L);
            userExample.put("username", "john.doe");
            userExample.put("email", "john.doe@company.com");
            userExample.put("firstName", "John");
            userExample.put("lastName", "Doe");
            userExample.put("role", "ACCOUNTANT");
            userExample.put("permissions", List.of("ACCOUNTING_CREATE", "ACCOUNTING_READ", "ACCOUNTING_UPDATE", "REPORTING_READ"));
            userExample.put("isActive", true);
            userExample.put("createdAt", "2024-12-25T10:30:00");
            userExample.put("lastLogin", null);
            
            // Exemple de rôle
            Map<String, Object> roleExample = new HashMap<>();
            roleExample.put("role", "MANAGER");
            roleExample.put("label", "Gestionnaire");
            roleExample.put("description", "Gestion des opérations comptables et validation");
            roleExample.put("permissions", List.of("USER_READ", "ACCOUNTING_CREATE", "ACCOUNTING_READ", "ACCOUNTING_UPDATE", "REPORTING_READ", "REPORTING_EXPORT"));
            
            examples.put("createdUser", userExample);
            examples.put("roleExample", roleExample);
            
            demo.put("examples", examples);
            demo.put("message", "Démonstration de la gestion des utilisateurs");
            demo.put("description", "Création, gestion des rôles et permissions");
            
            return ResponseEntity.ok(demo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of(
                    "error", "Erreur lors de la génération de la démonstration: " + e.getMessage(),
                    "status", "ERROR"
                ));
        }
    }
}
