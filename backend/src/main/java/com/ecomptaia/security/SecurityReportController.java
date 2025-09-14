package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Contrôleur des rapports de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Expose les rapports de sécurité pour la surveillance
 */
@RestController
@RequestMapping("/api/security/reports")
public class SecurityReportController {

    @Autowired
    private SecurityReportService securityReportService;

    /**
     * Obtient le rapport de sécurité complet
     */
    @GetMapping
    public Map<String, Object> getSecurityReport() {
        return securityReportService.generateSecurityReport();
    }
}

