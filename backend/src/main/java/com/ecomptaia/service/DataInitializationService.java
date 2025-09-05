package com.ecomptaia.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Service d'initialisation des données de base
 */
@Service
@Transactional
public class DataInitializationService implements CommandLineRunner {

    // @Autowired
    // private UserManagementService userManagementService; // Sera utilisé plus tard

    @Override
    public void run(String... args) throws Exception {
        initializeDefaultData();
    }

    private void initializeDefaultData() {
        try {
            // Initialisation des utilisateurs par défaut
            initializeDefaultUsers();
            
            // Initialisation des données de base
            initializeBaseData();
            
            System.out.println("✅ Données d'initialisation créées avec succès");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'initialisation des données: " + e.getMessage());
        }
    }

    private void initializeDefaultUsers() {
        try {
            // Création d'un utilisateur administrateur par défaut
            Map<String, Object> adminUser = new HashMap<>();
            adminUser.put("username", "admin");
            adminUser.put("email", "admin@ecomptaia.com");
            adminUser.put("firstName", "Administrateur");
            adminUser.put("lastName", "Système");
            adminUser.put("role", "ROLE_ADMIN");
            adminUser.put("password", "admin123");
            adminUser.put("isActive", true);
            adminUser.put("companyId", 1L);

            // Création d'un utilisateur test
            Map<String, Object> testUser = new HashMap<>();
            testUser.put("username", "test");
            testUser.put("email", "test@ecomptaia.com");
            testUser.put("firstName", "Utilisateur");
            testUser.put("lastName", "Test");
            testUser.put("role", "ROLE_USER");
            testUser.put("password", "test123");
            testUser.put("isActive", true);
            testUser.put("companyId", 1L);

            System.out.println("✅ Utilisateurs par défaut initialisés");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de l'initialisation des utilisateurs: " + e.getMessage());
        }
    }

    private void initializeBaseData() {
        try {
            // Ici on peut ajouter d'autres initialisations
            // - Pays et devises
            // - Plans comptables
            // - Paramètres système
            // etc.
            
            System.out.println("✅ Données de base initialisées");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors de l'initialisation des données de base: " + e.getMessage());
        }
    }
}
