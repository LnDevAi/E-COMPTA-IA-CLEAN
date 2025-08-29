package com.ecomptaia.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class MobileService {

    // ==================== AUTHENTIFICATION MOBILE ====================

    /**
     * Connexion mobile optimisée
     */
    public Map<String, Object> mobileLogin(Map<String, Object> credentials) {
        Map<String, Object> result = new HashMap<>();
        
        // Simulation d'authentification mobile
        String email = (String) credentials.get("email");
        String password = (String) credentials.get("password");
        
        if ("mobile@test.com".equals(email) && "password123".equals(password)) {
            result.put("userId", 1L);
            result.put("companyId", 1L);
            result.put("accessToken", "mobile_access_token_" + System.currentTimeMillis());
            result.put("refreshToken", "mobile_refresh_token_" + System.currentTimeMillis());
            result.put("expiresIn", 3600);
            result.put("userInfo", Map.of(
                "firstName", "Mobile",
                "lastName", "User",
                "email", email,
                "role", "MOBILE_USER"
            ));
            result.put("permissions", Arrays.asList("READ", "WRITE", "UPLOAD"));
        } else {
            throw new RuntimeException("Identifiants invalides");
        }
        
        return result;
    }

    /**
     * Vérification du token mobile
     */
    public Map<String, Object> verifyMobileToken(String token) {
        Map<String, Object> result = new HashMap<>();
        
        if (token != null && token.startsWith("mobile_access_token_")) {
            result.put("valid", true);
            result.put("userId", 1L);
            result.put("companyId", 1L);
            result.put("expiresAt", LocalDateTime.now().plusHours(1));
        } else {
            result.put("valid", false);
            result.put("message", "Token invalide");
        }
        
        return result;
    }

    /**
     * Rafraîchissement du token
     */
    public Map<String, Object> refreshMobileToken(String refreshToken) {
        Map<String, Object> result = new HashMap<>();
        
        if (refreshToken != null && refreshToken.startsWith("mobile_refresh_token_")) {
            result.put("accessToken", "mobile_access_token_" + System.currentTimeMillis());
            result.put("refreshToken", "mobile_refresh_token_" + System.currentTimeMillis());
            result.put("expiresIn", 3600);
            result.put("userId", 1L);
        } else {
            throw new RuntimeException("Refresh token invalide");
        }
        
        return result;
    }

    // ==================== DASHBOARD MOBILE ====================

    /**
     * Dashboard mobile optimisé
     */
    public Map<String, Object> getMobileDashboard(Long companyId, String deviceType) {
        Map<String, Object> dashboard = new HashMap<>();
        
        // KPIs mobiles
        Map<String, Object> kpis = getMobileKPIs(companyId);
        dashboard.put("kpis", kpis);
        
        // Activité récente
        List<Map<String, Object>> recentActivity = new ArrayList<>();
        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("type", "ECRITURE_CREATED");
        activity1.put("title", "Nouvelle écriture");
        activity1.put("description", "Achat fournitures de bureau");
        activity1.put("timestamp", LocalDateTime.now().minusHours(2));
        activity1.put("amount", 75000.00);
        recentActivity.add(activity1);
        
        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("type", "DOCUMENT_UPLOADED");
        activity2.put("title", "Document uploadé");
        activity2.put("description", "Facture_Fournitures_001.pdf");
        activity2.put("timestamp", LocalDateTime.now().minusHours(4));
        recentActivity.add(activity2);
        
        dashboard.put("recentActivity", recentActivity);
        
        // Notifications non lues
        dashboard.put("unreadNotifications", 3);
        
        // Configuration mobile
        Map<String, Object> config = new HashMap<>();
        config.put("syncEnabled", true);
        config.put("offlineMode", false);
        config.put("lastSync", LocalDateTime.now().minusMinutes(30));
        dashboard.put("config", config);
        
        return dashboard;
    }

    /**
     * KPIs mobiles
     */
    public Map<String, Object> getMobileKPIs(Long companyId) {
        Map<String, Object> kpis = new HashMap<>();
        
        kpis.put("totalEcritures", 1250);
        kpis.put("pendingEcritures", 15);
        kpis.put("totalComptes", 150);
        kpis.put("totalTiers", 85);
        kpis.put("totalDocuments", 890);
        kpis.put("monthlyRevenue", 1250000.00);
        kpis.put("monthlyExpenses", 850000.00);
        kpis.put("netResult", 400000.00);
        kpis.put("currency", "XOF");
        kpis.put("lastUpdate", LocalDateTime.now());
        
        return kpis;
    }

    // ==================== ÉCRITURES MOBILES ====================

    /**
     * Liste des écritures pour mobile
     */
    public Map<String, Object> getMobileEcritures(Long companyId, int page, int size, String status) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> ecritures = new ArrayList<>();
        
        // Écriture 1
        Map<String, Object> ecriture1 = new HashMap<>();
        ecriture1.put("id", 1L);
        ecriture1.put("numeroPiece", "ECR-2024-001");
        ecriture1.put("libelle", "Achat fournitures de bureau");
        ecriture1.put("montant", 75000.00);
        ecriture1.put("dateEcriture", LocalDate.now().minusDays(5));
        ecriture1.put("statut", "VALIDEE");
        ecriture1.put("type", "ACHAT");
        ecriture1.put("devise", "XOF");
        ecriture1.put("synced", true);
        ecritures.add(ecriture1);
        
        // Écriture 2
        Map<String, Object> ecriture2 = new HashMap<>();
        ecriture2.put("id", 2L);
        ecriture2.put("numeroPiece", "ECR-2024-002");
        ecriture2.put("libelle", "Vente marchandises");
        ecriture2.put("montant", 150000.00);
        ecriture2.put("dateEcriture", LocalDate.now().minusDays(3));
        ecriture2.put("statut", "VALIDEE");
        ecriture2.put("type", "VENTE");
        ecriture2.put("devise", "XOF");
        ecriture2.put("synced", true);
        ecritures.add(ecriture2);
        
        result.put("ecritures", ecritures);
        result.put("total", ecritures.size());
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", 1);
        result.put("hasMore", false);
        
        return result;
    }

    /**
     * Détail d'une écriture pour mobile
     */
    public Map<String, Object> getMobileEcritureDetail(Long id, Long companyId) {
        Map<String, Object> ecriture = new HashMap<>();
        
        ecriture.put("id", id);
        ecriture.put("numeroPiece", "ECR-2024-001");
        ecriture.put("libelle", "Achat fournitures de bureau");
        ecriture.put("montant", 75000.00);
        ecriture.put("dateEcriture", LocalDate.now().minusDays(5));
        ecriture.put("statut", "VALIDEE");
        ecriture.put("type", "ACHAT");
        ecriture.put("devise", "XOF");
        ecriture.put("reference", "REF-001");
        ecriture.put("commentaire", "Achat de fournitures pour le bureau");
        
        // Lignes d'écriture
        List<Map<String, Object>> lignes = new ArrayList<>();
        Map<String, Object> ligne1 = new HashMap<>();
        ligne1.put("id", 1L);
        ligne1.put("compte", "6061");
        ligne1.put("libelle", "Fournitures de bureau");
        ligne1.put("debit", 75000.00);
        ligne1.put("credit", 0.00);
        lignes.add(ligne1);
        
        Map<String, Object> ligne2 = new HashMap<>();
        ligne2.put("id", 2L);
        ligne2.put("compte", "5121");
        ligne2.put("libelle", "Banque");
        ligne2.put("debit", 0.00);
        ligne2.put("credit", 75000.00);
        lignes.add(ligne2);
        
        ecriture.put("lignes", lignes);
        ecriture.put("documents", Arrays.asList("Facture_Fournitures_001.pdf"));
        ecriture.put("synced", true);
        
        return ecriture;
    }

    /**
     * Création d'écriture mobile
     */
    public Map<String, Object> createMobileEcriture(Long companyId, Map<String, Object> ecritureData) {
        Map<String, Object> result = new HashMap<>();
        
        // Simulation de création
        result.put("id", System.currentTimeMillis());
        result.put("numeroPiece", "ECR-2024-" + String.format("%03d", (int)(Math.random() * 1000)));
        result.put("status", "CREATED");
        result.put("synced", false);
        result.put("createdAt", LocalDateTime.now());
        result.put("message", "Écriture créée avec succès");
        
        return result;
    }

    // ==================== COMPTES MOBILES ====================

    /**
     * Liste des comptes pour mobile
     */
    public Map<String, Object> getMobileComptes(Long companyId, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> comptes = new ArrayList<>();
        
        // Compte 1
        Map<String, Object> compte1 = new HashMap<>();
        compte1.put("id", 1L);
        compte1.put("numero", "6061");
        compte1.put("nom", "Fournitures de bureau");
        compte1.put("type", "CHARGE");
        compte1.put("classe", "Charges d'exploitation");
        compte1.put("solde", 125000.00);
        compte1.put("devise", "XOF");
        compte1.put("synced", true);
        comptes.add(compte1);
        
        // Compte 2
        Map<String, Object> compte2 = new HashMap<>();
        compte2.put("id", 2L);
        compte2.put("numero", "7071");
        compte2.put("nom", "Ventes de marchandises");
        compte2.put("type", "PRODUIT");
        compte2.put("classe", "Produits d'exploitation");
        compte2.put("solde", 450000.00);
        compte2.put("devise", "XOF");
        compte2.put("synced", true);
        comptes.add(compte2);
        
        result.put("comptes", comptes);
        result.put("total", comptes.size());
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", 1);
        result.put("hasMore", false);
        
        return result;
    }

    /**
     * Recherche de comptes mobile
     */
    public Map<String, Object> searchMobileComptes(Long companyId, String query, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> comptes = new ArrayList<>();
        
        if (query.toLowerCase().contains("fournitures")) {
            Map<String, Object> compte = new HashMap<>();
            compte.put("id", 1L);
            compte.put("numero", "6061");
            compte.put("nom", "Fournitures de bureau");
            compte.put("type", "CHARGE");
            compte.put("solde", 125000.00);
            comptes.add(compte);
        }
        
        result.put("comptes", comptes);
        result.put("query", query);
        result.put("total", comptes.size());
        
        return result;
    }

    // ==================== TIERS MOBILES ====================

    /**
     * Liste des tiers pour mobile
     */
    public Map<String, Object> getMobileTiers(Long companyId, int page, int size, String type) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> tiers = new ArrayList<>();
        
        // Tiers 1
        Map<String, Object> tiers1 = new HashMap<>();
        tiers1.put("id", 1L);
        tiers1.put("nom", "Fournitures Pro SARL");
        tiers1.put("email", "contact@fournitures-pro.bf");
        tiers1.put("telephone", "+226 25 30 12 34");
        tiers1.put("type", "FOURNISSEUR");
        tiers1.put("adresse", "Ouagadougou, Burkina Faso");
        tiers1.put("solde", 75000.00);
        tiers1.put("synced", true);
        tiers.add(tiers1);
        
        // Tiers 2
        Map<String, Object> tiers2 = new HashMap<>();
        tiers2.put("id", 2L);
        tiers2.put("nom", "Client Premium SA");
        tiers2.put("email", "contact@client-premium.bf");
        tiers2.put("telephone", "+226 25 40 56 78");
        tiers2.put("type", "CLIENT");
        tiers2.put("adresse", "Bobo-Dioulasso, Burkina Faso");
        tiers2.put("solde", -150000.00);
        tiers2.put("synced", true);
        tiers.add(tiers2);
        
        result.put("tiers", tiers);
        result.put("total", tiers.size());
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", 1);
        result.put("hasMore", false);
        
        return result;
    }

    /**
     * Détail d'un tiers pour mobile
     */
    public Map<String, Object> getMobileTierDetail(Long id, Long companyId) {
        Map<String, Object> tier = new HashMap<>();
        
        tier.put("id", id);
        tier.put("nom", "Fournitures Pro SARL");
        tier.put("email", "contact@fournitures-pro.bf");
        tier.put("telephone", "+226 25 30 12 34");
        tier.put("type", "FOURNISSEUR");
        tier.put("adresse", "Ouagadougou, Burkina Faso");
        tier.put("solde", 75000.00);
        tier.put("devise", "XOF");
        tier.put("dateCreation", LocalDate.now().minusYears(2));
        tier.put("derniereActivite", LocalDateTime.now().minusDays(5));
        tier.put("documents", Arrays.asList("Facture_001.pdf", "Bon_Livraison_001.pdf"));
        tier.put("synced", true);
        
        return tier;
    }

    // ==================== DOCUMENTS MOBILES ====================

    /**
     * Liste des documents pour mobile
     */
    public Map<String, Object> getMobileDocuments(Long companyId, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> documents = new ArrayList<>();
        
        // Document 1
        Map<String, Object> doc1 = new HashMap<>();
        doc1.put("id", 1L);
        doc1.put("nomFichier", "Facture_Fournitures_001.pdf");
        doc1.put("type", "FACTURE");
        doc1.put("taille", "2.5 MB");
        doc1.put("dateUpload", LocalDateTime.now().minusDays(3));
        doc1.put("dateModification", LocalDateTime.now().minusDays(1));
        doc1.put("utilisateur", "John Doe");
        doc1.put("synced", true);
        documents.add(doc1);
        
        // Document 2
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("id", 2L);
        doc2.put("nomFichier", "Bon_Livraison_002.pdf");
        doc2.put("type", "BON_LIVRAISON");
        doc2.put("taille", "1.8 MB");
        doc2.put("dateUpload", LocalDateTime.now().minusDays(2));
        doc2.put("dateModification", LocalDateTime.now().minusDays(2));
        doc2.put("utilisateur", "Jane Smith");
        doc2.put("synced", true);
        documents.add(doc2);
        
        result.put("documents", documents);
        result.put("total", documents.size());
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", 1);
        result.put("hasMore", false);
        
        return result;
    }

    /**
     * Upload de document mobile
     */
    public Map<String, Object> uploadMobileDocument(Long companyId, Long userId, Map<String, Object> documentData) {
        Map<String, Object> result = new HashMap<>();
        
        // Simulation d'upload
        result.put("id", System.currentTimeMillis());
        result.put("nomFichier", (String) documentData.get("nomFichier"));
        result.put("type", documentData.get("type"));
        result.put("taille", documentData.get("taille"));
        result.put("status", "UPLOADED");
        result.put("synced", false);
        result.put("uploadedAt", LocalDateTime.now());
        result.put("message", "Document uploadé avec succès");
        
        return result;
    }

    // ==================== NOTIFICATIONS MOBILES ====================

    /**
     * Notifications mobiles
     */
    public Map<String, Object> getMobileNotifications(Long userId, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        List<Map<String, Object>> notifications = new ArrayList<>();
        
        // Notification 1
        Map<String, Object> notif1 = new HashMap<>();
        notif1.put("id", 1L);
        notif1.put("type", "INFO");
        notif1.put("title", "Nouvelle écriture");
        notif1.put("message", "Une nouvelle écriture a été créée");
        notif1.put("timestamp", LocalDateTime.now().minusHours(2));
        notif1.put("read", false);
        notif1.put("priority", "MEDIUM");
        notifications.add(notif1);
        
        // Notification 2
        Map<String, Object> notif2 = new HashMap<>();
        notif2.put("id", 2L);
        notif2.put("type", "WARNING");
        notif2.put("title", "Synchronisation");
        notif2.put("message", "Synchronisation terminée");
        notif2.put("timestamp", LocalDateTime.now().minusHours(4));
        notif2.put("read", true);
        notif2.put("priority", "LOW");
        notifications.add(notif2);
        
        result.put("notifications", notifications);
        result.put("total", notifications.size());
        result.put("unreadCount", 1);
        result.put("page", page);
        result.put("size", size);
        result.put("totalPages", 1);
        result.put("hasMore", false);
        
        return result;
    }

    /**
     * Marquer notification comme lue
     */
    public Map<String, Object> markNotificationAsRead(Long id, Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("id", id);
        result.put("status", "READ");
        result.put("updatedAt", LocalDateTime.now());
        result.put("message", "Notification marquée comme lue");
        
        return result;
    }

    // ==================== SYNCHRONISATION MOBILE ====================

    /**
     * Synchronisation des données
     */
    public Map<String, Object> syncMobileData(Long companyId, Long userId, Map<String, Object> syncData) {
        Map<String, Object> result = new HashMap<>();
        
        // Simulation de synchronisation
        result.put("syncId", UUID.randomUUID().toString());
        result.put("status", "COMPLETED");
        result.put("startedAt", LocalDateTime.now().minusMinutes(5));
        result.put("completedAt", LocalDateTime.now());
        result.put("duration", 300); // 5 minutes
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("ecrituresSynced", 15);
        stats.put("comptesSynced", 5);
        stats.put("tiersSynced", 3);
        stats.put("documentsSynced", 8);
        stats.put("conflicts", 0);
        result.put("stats", stats);
        
        result.put("message", "Synchronisation terminée avec succès");
        
        return result;
    }

    /**
     * Statut de synchronisation
     */
    public Map<String, Object> getSyncStatus(Long companyId, Long userId) {
        Map<String, Object> status = new HashMap<>();
        
        status.put("lastSync", LocalDateTime.now().minusMinutes(30));
        status.put("nextSync", LocalDateTime.now().plusMinutes(30));
        status.put("syncEnabled", true);
        status.put("offlineMode", false);
        status.put("pendingChanges", 0);
        status.put("connectionStatus", "ONLINE");
        status.put("syncInterval", 3600); // 1 heure
        
        return status;
    }

    // ==================== CONFIGURATION MOBILE ====================

    /**
     * Configuration mobile
     */
    public Map<String, Object> getMobileConfig(Long companyId, String deviceType) {
        Map<String, Object> config = new HashMap<>();
        
        config.put("syncEnabled", true);
        config.put("syncInterval", 3600);
        config.put("offlineMode", false);
        config.put("maxOfflineDays", 7);
        config.put("autoUpload", true);
        config.put("notificationsEnabled", true);
        config.put("biometricAuth", true);
        config.put("darkMode", false);
        config.put("language", "fr");
        config.put("currency", "XOF");
        config.put("timezone", "Africa/Ouagadougou");
        
        // Configuration spécifique au device
        if ("android".equals(deviceType)) {
            config.put("platform", "ANDROID");
            config.put("version", "1.0.0");
        } else if ("ios".equals(deviceType)) {
            config.put("platform", "IOS");
            config.put("version", "1.0.0");
        }
        
        return config;
    }

    /**
     * Mise à jour de la configuration
     */
    public Map<String, Object> updateMobileConfig(Long userId, Map<String, Object> configData) {
        Map<String, Object> result = new HashMap<>();
        
        result.put("userId", userId);
        result.put("config", configData);
        result.put("updatedAt", LocalDateTime.now());
        result.put("status", "UPDATED");
        result.put("message", "Configuration mise à jour avec succès");
        
        return result;
    }

    // ==================== DONNÉES DE TEST ====================

    /**
     * Données de test pour l'API mobile
     */
    public Map<String, Object> getTestMobileData() {
        Map<String, Object> testData = new HashMap<>();
        
        testData.put("message", "API mobile opérationnelle");
        testData.put("timestamp", LocalDateTime.now());
        testData.put("version", "1.0.0");
        
        // Endpoints disponibles
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("auth", "POST /api/mobile/auth/login");
        endpoints.put("dashboard", "GET /api/mobile/dashboard?companyId=1");
        endpoints.put("ecritures", "GET /api/mobile/ecritures?companyId=1");
        endpoints.put("comptes", "GET /api/mobile/comptes?companyId=1");
        endpoints.put("tiers", "GET /api/mobile/tiers?companyId=1");
        endpoints.put("documents", "GET /api/mobile/documents?companyId=1");
        endpoints.put("notifications", "GET /api/mobile/notifications?userId=1");
        endpoints.put("sync", "POST /api/mobile/sync");
        endpoints.put("config", "GET /api/mobile/config?companyId=1");
        testData.put("endpoints", endpoints);
        
        // Fonctionnalités
        List<String> features = Arrays.asList(
            "Authentification mobile optimisée",
            "Dashboard mobile responsive",
            "Gestion des écritures hors ligne",
            "Synchronisation automatique",
            "Notifications push",
            "Upload de documents",
            "Configuration personnalisable",
            "Support multi-plateformes"
        );
        testData.put("features", features);
        
        return testData;
    }
}




