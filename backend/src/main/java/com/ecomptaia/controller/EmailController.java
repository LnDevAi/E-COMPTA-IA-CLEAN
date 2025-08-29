package com.ecomptaia.controller;

import com.ecomptaia.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour la gestion des emails
 */
@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * Tester la configuration email
     */
    @PostMapping("/test-config")
    public ResponseEntity<Map<String, Object>> testerConfigurationEmail(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.testerConfigurationEmail(email);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de test envoyé avec succès à " + email);
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de test");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors du test de configuration: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de bienvenue
     */
    @PostMapping("/bienvenue")
    public ResponseEntity<Map<String, Object>> envoyerEmailBienvenue(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailBienvenue(email, nomUtilisateur, nomEntreprise);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de bienvenue envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de bienvenue");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de confirmation d'inscription
     */
    @PostMapping("/confirmation-inscription")
    public ResponseEntity<Map<String, Object>> envoyerEmailConfirmationInscription(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String tokenConfirmation) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailConfirmationInscription(email, nomUtilisateur, tokenConfirmation);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de confirmation envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de confirmation");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de réinitialisation de mot de passe
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> envoyerEmailResetPassword(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String tokenReset) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailResetPassword(email, nomUtilisateur, tokenReset);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de réinitialisation envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de réinitialisation");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de notification d'abonnement
     */
    @PostMapping("/abonnement")
    public ResponseEntity<Map<String, Object>> envoyerEmailAbonnement(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String planAbonnement,
            @RequestParam String montant,
            @RequestParam String devise,
            @RequestParam String cycleFacturation) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailAbonnement(email, nomUtilisateur, nomEntreprise, 
                                                               planAbonnement, montant, devise, cycleFacturation);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email d'abonnement envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email d'abonnement");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de rappel d'abonnement
     */
    @PostMapping("/rappel-abonnement")
    public ResponseEntity<Map<String, Object>> envoyerEmailRappelAbonnement(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String planAbonnement,
            @RequestParam String dateExpiration) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailRappelAbonnement(email, nomUtilisateur, nomEntreprise, 
                                                                     planAbonnement, dateExpiration);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de rappel d'abonnement envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de rappel");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de notification de facture
     */
    @PostMapping("/facture")
    public ResponseEntity<Map<String, Object>> envoyerEmailFacture(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String numeroFacture,
            @RequestParam String montant,
            @RequestParam String devise,
            @RequestParam String dateEcheance) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailFacture(email, nomUtilisateur, nomEntreprise, 
                                                            numeroFacture, montant, devise, dateEcheance);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de facture envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de facture");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de notification de validation comptable
     */
    @PostMapping("/validation-comptable")
    public ResponseEntity<Map<String, Object>> envoyerEmailValidationComptable(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String typeOperation,
            @RequestParam String numeroOperation,
            @RequestParam String montant,
            @RequestParam String devise) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailValidationComptable(email, nomUtilisateur, nomEntreprise, 
                                                                        typeOperation, numeroOperation, montant, devise);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de validation comptable envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de validation");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de notification d'anomalie détectée
     */
    @PostMapping("/anomalie-detected")
    public ResponseEntity<Map<String, Object>> envoyerEmailAnomalieDetectee(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String typeAnomalie,
            @RequestParam String description,
            @RequestParam String niveauUrgence) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailAnomalieDetectee(email, nomUtilisateur, nomEntreprise, 
                                                                     typeAnomalie, description, niveauUrgence);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email d'anomalie envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email d'anomalie");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de rapport mensuel
     */
    @PostMapping("/rapport-mensuel")
    public ResponseEntity<Map<String, Object>> envoyerEmailRapportMensuel(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String moisAnnee,
            @RequestBody Map<String, Object> statistiques) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailRapportMensuel(email, nomUtilisateur, nomEntreprise, 
                                                                   moisAnnee, statistiques);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de rapport mensuel envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de rapport");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de notification de sauvegarde
     */
    @PostMapping("/sauvegarde")
    public ResponseEntity<Map<String, Object>> envoyerEmailSauvegarde(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String typeSauvegarde,
            @RequestParam String statut,
            @RequestParam String details) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailSauvegarde(email, nomUtilisateur, nomEntreprise, 
                                                               typeSauvegarde, statut, details);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de sauvegarde envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de sauvegarde");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de support technique
     */
    @PostMapping("/support")
    public ResponseEntity<Map<String, Object>> envoyerEmailSupport(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String sujet,
            @RequestParam String message,
            @RequestParam String priorite) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailSupport(email, nomUtilisateur, nomEntreprise, 
                                                            sujet, message, priorite);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de support envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de support");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de notification de maintenance
     */
    @PostMapping("/maintenance")
    public ResponseEntity<Map<String, Object>> envoyerEmailMaintenance(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String dateMaintenance,
            @RequestParam String duree,
            @RequestParam String impact) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailMaintenance(email, nomUtilisateur, nomEntreprise, 
                                                                dateMaintenance, duree, impact);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de maintenance envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de maintenance");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email de notification de nouvelle fonctionnalité
     */
    @PostMapping("/nouvelle-fonctionnalite")
    public ResponseEntity<Map<String, Object>> envoyerEmailNouvelleFonctionnalite(
            @RequestParam String email,
            @RequestParam String nomUtilisateur,
            @RequestParam String nomEntreprise,
            @RequestParam String nomFonctionnalite,
            @RequestParam String description,
            @RequestParam String benefices) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailNouvelleFonctionnalite(email, nomUtilisateur, nomEntreprise, 
                                                                           nomFonctionnalite, description, benefices);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de nouvelle fonctionnalité envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de nouvelle fonctionnalité");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Envoyer un email simple
     */
    @PostMapping("/simple")
    public ResponseEntity<Map<String, Object>> envoyerEmailSimple(
            @RequestParam String email,
            @RequestParam String sujet,
            @RequestParam String contenu) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailSimple(email, sujet, contenu);
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email simple envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email simple");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors de l'envoi: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    // === ENDPOINTS DE TEST ===

    /**
     * Test de configuration email
     */
    @GetMapping("/test-config")
    public ResponseEntity<Map<String, Object>> testConfigEmail(@RequestParam String email) {
        return testerConfigurationEmail(email);
    }

    /**
     * Test d'email de bienvenue
     */
    @GetMapping("/test-bienvenue")
    public ResponseEntity<Map<String, Object>> testEmailBienvenue(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailBienvenue(email, "Utilisateur Test", "Entreprise Test SARL");
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de bienvenue de test envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de bienvenue de test");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors du test: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Test d'email d'abonnement
     */
    @GetMapping("/test-abonnement")
    public ResponseEntity<Map<String, Object>> testEmailAbonnement(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailAbonnement(email, "Utilisateur Test", "Entreprise Test SARL", 
                                                               "PROFESSIONAL", "50000", "XOF", "MENSUEL");
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email d'abonnement de test envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email d'abonnement de test");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors du test: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Test d'email de facture
     */
    @GetMapping("/test-facture")
    public ResponseEntity<Map<String, Object>> testEmailFacture(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = emailService.envoyerEmailFacture(email, "Utilisateur Test", "Entreprise Test SARL", 
                                                            "FACT-2024-001", "50000", "XOF", "31/12/2024");
            
            if (success) {
                result.put("status", "SUCCESS");
                result.put("message", "Email de facture de test envoyé avec succès");
            } else {
                result.put("status", "ERROR");
                result.put("message", "Échec de l'envoi de l'email de facture de test");
            }
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Erreur lors du test: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * Informations sur le système d'emails
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getEmailInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("systeme", "Système d'emails automatiques E-COMPTA-IA");
        info.put("version", "1.0");
        info.put("description", "Service d'envoi d'emails automatiques pour notifications");
        
        Map<String, String> typesEmails = new HashMap<>();
        typesEmails.put("bienvenue", "Email de bienvenue pour nouveaux utilisateurs");
        typesEmails.put("confirmation", "Email de confirmation d'inscription");
        typesEmails.put("reset-password", "Email de réinitialisation de mot de passe");
        typesEmails.put("abonnement", "Email de confirmation d'abonnement");
        typesEmails.put("rappel-abonnement", "Email de rappel d'abonnement");
        typesEmails.put("facture", "Email de notification de facture");
        typesEmails.put("validation-comptable", "Email de validation comptable");
        typesEmails.put("anomalie", "Email de notification d'anomalie");
        typesEmails.put("rapport-mensuel", "Email de rapport mensuel");
        typesEmails.put("sauvegarde", "Email de notification de sauvegarde");
        typesEmails.put("support", "Email de support technique");
        typesEmails.put("maintenance", "Email de notification de maintenance");
        typesEmails.put("nouvelle-fonctionnalite", "Email de nouvelle fonctionnalité");
        info.put("typesEmails", typesEmails);
        
        Map<String, String> configuration = new HashMap<>();
        configuration.put("fromEmail", "noreply@ecomptaia.com");
        configuration.put("supportEmail", "support@ecomptaia.com");
        configuration.put("templates", "Thymeleaf");
        configuration.put("format", "HTML");
        info.put("configuration", configuration);
        
        return ResponseEntity.ok(info);
    }
}




