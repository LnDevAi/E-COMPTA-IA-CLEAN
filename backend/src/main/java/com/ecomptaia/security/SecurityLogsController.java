package com.ecomptaia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur des logs de sécurité pour E-COMPTA-IA INTERNATIONAL
 * Expose les logs de sécurité pour la surveillance
 */
@RestController
@RequestMapping("/api/security/logs")
public class SecurityLogsController {

    @Autowired
    private SecurityLoggingService securityLoggingService;

    /**
     * Obtient les logs de sécurité récents
     */
    @GetMapping
    public List<SecurityLoggingService.SecurityLogEntry> getSecurityLogs() {
        return securityLoggingService.getRecentSecurityLogs()
                .stream()
                .collect(Collectors.toList());
    }
}

