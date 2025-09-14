package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Contrôleur du tableau de bord de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Expose les données du tableau de bord de sécurité
 */
@RestController
@RequestMapping("/api/security/dashboard")
public class SecurityDashboardController {

    @Autowired
    private SecurityDashboardService securityDashboardService;

    /**
     * Obtient les données du tableau de bord de sécurité
     */
    @GetMapping
    public Map<String, Object> getSecurityDashboard() {
        return securityDashboardService.getSecurityDashboardData();
    }
}

