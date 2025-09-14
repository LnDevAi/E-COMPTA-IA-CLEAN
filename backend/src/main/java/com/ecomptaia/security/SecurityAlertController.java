package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur des alertes de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Expose les alertes de sécurité pour la surveillance
 */
@RestController
@RequestMapping("/api/security/alerts")
public class SecurityAlertController {

    @Autowired
    private SecurityAlertService securityAlertService;

    /**
     * Obtient les alertes de sécurité récentes
     */
    @GetMapping
    public List<SecurityAlertService.SecurityAlert> getSecurityAlerts() {
        return securityAlertService.getRecentAlerts()
                .stream()
                .collect(Collectors.toList());
    }
}

