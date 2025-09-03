package com.ecomptaia.controller;

import com.ecomptaia.service.ThirdPartyNumberingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Contrôleur pour la gestion de la numérotation des tiers
 */
@RestController
@RequestMapping("/api/third-party-numbering")
@CrossOrigin(origins = "*")
public class ThirdPartyNumberingController {

    @Autowired
    private ThirdPartyNumberingService numberingService;

    /**
     * Générer un numéro de compte tiers automatiquement
     * POST /api/third-party-numbering/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateAccountNumber(@RequestBody Map<String, Object> request) {
        try {
            Long companyId = Long.valueOf(request.get("companyId").toString());
            String type = (String) request.get("type");
            
            String accountNumber = numberingService.generateThirdPartyAccountNumber(companyId, type);
            String accountName = numberingService.getAccountNameByNumber(accountNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accountNumber", accountNumber);
            response.put("accountName", accountName);
            response.put("type", type);
            response.put("companyId", companyId);
            response.put("message", "Numéro de compte généré avec succès");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la génération du numéro de compte");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Valider un numéro de compte tiers
     * POST /api/third-party-numbering/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateAccountNumber(@RequestBody Map<String, Object> request) {
        try {
            String accountNumber = (String) request.get("accountNumber");
            String type = (String) request.get("type");
            Long companyId = Long.valueOf(request.get("companyId").toString());
            
            boolean isValid = numberingService.validateThirdPartyAccountNumber(accountNumber, type);
            boolean isAvailable = numberingService.isAccountNumberAvailable(companyId, accountNumber);
            boolean isConsistent = numberingService.validateTypeAccountNumberConsistency(type, accountNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accountNumber", accountNumber);
            response.put("type", type);
            response.put("isValid", isValid);
            response.put("isAvailable", isAvailable);
            response.put("isConsistent", isConsistent);
            response.put("accountName", numberingService.getAccountNameByNumber(accountNumber));
            response.put("message", isValid ? "Numéro de compte valide" : "Numéro de compte invalide");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la validation");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Vérifier la disponibilité d'un numéro de compte
     * GET /api/third-party-numbering/check-availability?companyId=1&accountNumber=4010001
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @RequestParam Long companyId,
            @RequestParam String accountNumber) {
        try {
            boolean isAvailable = numberingService.isAccountNumberAvailable(companyId, accountNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accountNumber", accountNumber);
            response.put("companyId", companyId);
            response.put("isAvailable", isAvailable);
            response.put("message", isAvailable ? "Numéro disponible" : "Numéro déjà utilisé");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la vérification");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Obtenir les informations sur un numéro de compte
     * GET /api/third-party-numbering/info?accountNumber=4010001
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getAccountInfo(@RequestParam String accountNumber) {
        try {
            String accountName = numberingService.getAccountNameByNumber(accountNumber);
            String type = numberingService.getThirdPartyTypeByAccountNumber(accountNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("accountNumber", accountNumber);
            response.put("accountName", accountName);
            response.put("type", type);
            response.put("baseAccount", accountNumber.substring(0, 3));
            response.put("sequenceNumber", accountNumber.substring(3));
            response.put("message", "Informations récupérées avec succès");
            response.put("status", "SUCCESS");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Erreur lors de la récupération des informations");
            error.put("message", e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Informations sur les types de tiers et leurs numérotations
     * GET /api/third-party-numbering/types
     */
    @GetMapping("/types")
    public ResponseEntity<Map<String, Object>> getThirdPartyTypes() {
        Map<String, Object> types = Map.of(
            "FOURNISSEUR", Map.of(
                "baseAccount", "401",
                "name", "Fournisseurs",
                "description", "Comptes fournisseurs"
            ),
            "CLIENT", Map.of(
                "baseAccount", "411",
                "name", "Clients",
                "description", "Comptes clients"
            ),
            "PERSONNEL", Map.of(
                "baseAccount", "421",
                "name", "Personnel",
                "description", "Comptes personnel"
            ),
            "SECURITE_SOCIALE", Map.of(
                "baseAccount", "431",
                "name", "Sécurité sociale",
                "description", "Comptes sécurité sociale"
            ),
            "ETAT", Map.of(
                "baseAccount", "441",
                "name", "État et autres collectivités",
                "description", "Comptes État"
            ),
            "GROUPE", Map.of(
                "baseAccount", "451",
                "name", "Groupe et associés",
                "description", "Comptes groupe"
            ),
            "DEPOT", Map.of(
                "baseAccount", "461",
                "name", "Dépôts et cautionnements reçus",
                "description", "Comptes dépôts"
            ),
            "AUTRE", Map.of(
                "baseAccount", "471",
                "name", "Autres créanciers",
                "description", "Autres comptes tiers"
            )
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("types", types);
        response.put("message", "Types de tiers récupérés avec succès");
        response.put("status", "SUCCESS");
        
        return ResponseEntity.ok(response);
    }
}





