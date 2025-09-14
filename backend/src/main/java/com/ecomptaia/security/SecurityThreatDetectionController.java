package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur de détection de menaces pour E-COMPTA-IA INTERNATIONAL
 * Expose les menaces de sécurité pour la surveillance
 */
@RestController
@RequestMapping("/api/security/threats")
public class SecurityThreatDetectionController {

    @Autowired
    private SecurityThreatDetectionService securityThreatDetectionService;

    /**
     * Obtient les menaces de sécurité récentes
     */
    @GetMapping
    public List<SecurityThreatDetectionService.SecurityThreat> getSecurityThreats() {
        return securityThreatDetectionService.getRecentThreats()
                .stream()
                .collect(Collectors.toList());
    }
}