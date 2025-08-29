package com.ecomptaia.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserManagementService {

    public Map<String, Object> createUser(Long companyId, String username, String email, 
                                        String firstName, String lastName, String role) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (username == null || username.trim().isEmpty()) {
                result.put("error", "Le nom d'utilisateur est obligatoire");
                result.put("status", "ERROR");
                return result;
            }
            
            Long userId = System.currentTimeMillis();
            
            Map<String, Object> user = new HashMap<>();
            user.put("userId", userId);
            user.put("companyId", companyId);
            user.put("username", username);
            user.put("email", email);
            user.put("firstName", firstName);
            user.put("lastName", lastName);
            user.put("role", role);
            user.put("permissions", getDefaultPermissions(role));
            user.put("isActive", true);
            user.put("createdAt", LocalDateTime.now());
            user.put("lastLogin", null);
            
            result.put("user", user);
            result.put("message", "Utilisateur créé avec succès");
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la création: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    public List<Map<String, Object>> getCompanyUsers(Long companyId) {
        List<Map<String, Object>> users = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("userId", (long) i);
            user.put("companyId", companyId);
            user.put("username", "user" + i);
            user.put("email", "user" + i + "@company.com");
            user.put("firstName", "Prénom" + i);
            user.put("lastName", "Nom" + i);
            user.put("role", i == 1 ? "ADMIN" : (i == 2 ? "MANAGER" : "USER"));
            user.put("permissions", getDefaultPermissions(i == 1 ? "ADMIN" : (i == 2 ? "MANAGER" : "USER")));
            user.put("isActive", true);
            user.put("createdAt", LocalDateTime.now().minusDays(i));
            user.put("lastLogin", LocalDateTime.now().minusHours(i));
            
            users.add(user);
        }
        
        return users;
    }

    public List<Map<String, Object>> getAvailableRoles() {
        List<Map<String, Object>> roles = new ArrayList<>();
        
        Map<String, Object> adminRole = new HashMap<>();
        adminRole.put("role", "ADMIN");
        adminRole.put("label", "Administrateur");
        adminRole.put("description", "Accès complet à toutes les fonctionnalités");
        adminRole.put("permissions", getDefaultPermissions("ADMIN"));
        roles.add(adminRole);
        
        Map<String, Object> managerRole = new HashMap<>();
        managerRole.put("role", "MANAGER");
        managerRole.put("label", "Gestionnaire");
        managerRole.put("description", "Gestion des opérations comptables et validation");
        managerRole.put("permissions", getDefaultPermissions("MANAGER"));
        roles.add(managerRole);
        
        Map<String, Object> accountantRole = new HashMap<>();
        accountantRole.put("role", "ACCOUNTANT");
        accountantRole.put("label", "Comptable");
        accountantRole.put("description", "Saisie et gestion des écritures comptables");
        accountantRole.put("permissions", getDefaultPermissions("ACCOUNTANT"));
        roles.add(accountantRole);
        
        Map<String, Object> userRole = new HashMap<>();
        userRole.put("role", "USER");
        userRole.put("label", "Utilisateur");
        userRole.put("description", "Consultation et rapports limités");
        userRole.put("permissions", getDefaultPermissions("USER"));
        roles.add(userRole);
        
        return roles;
    }

    private List<String> getDefaultPermissions(String role) {
        switch (role) {
            case "ADMIN":
                return Arrays.asList("USER_CREATE", "USER_READ", "USER_UPDATE", "USER_DELETE",
                                   "ACCOUNTING_CREATE", "ACCOUNTING_READ", "ACCOUNTING_UPDATE", "ACCOUNTING_DELETE",
                                   "REPORTING_READ", "REPORTING_EXPORT", "SYSTEM_CONFIG");
            case "MANAGER":
                return Arrays.asList("USER_READ", "ACCOUNTING_CREATE", "ACCOUNTING_READ", "ACCOUNTING_UPDATE",
                                   "REPORTING_READ", "REPORTING_EXPORT");
            case "ACCOUNTANT":
                return Arrays.asList("ACCOUNTING_CREATE", "ACCOUNTING_READ", "ACCOUNTING_UPDATE",
                                   "REPORTING_READ");
            case "USER":
                return Arrays.asList("ACCOUNTING_READ", "REPORTING_READ");
            default:
                return new ArrayList<>();
        }
    }
}
