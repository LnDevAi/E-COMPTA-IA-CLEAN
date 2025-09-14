package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur des incidents de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Expose les incidents de sécurité pour la surveillance
 */
@RestController
@RequestMapping("/api/security/incidents")
public class SecurityIncidentController {

    @Autowired
    private SecurityIncidentService securityIncidentService;

    /**
     * Obtient les incidents de sécurité récents
     */
    @GetMapping
    public List<SecurityIncidentService.SecurityIncident> getSecurityIncidents() {
        return securityIncidentService.getRecentIncidents()
                .stream()
                .collect(Collectors.toList());
    }
}

