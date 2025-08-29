package com.ecomptaia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Service pour l'envoi d'emails automatiques
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.email.support:support@ecomptaia.com}")
    private String supportEmail;

    @Value("${app.email.noreply:noreply@ecomptaia.com}")
    private String noreplyEmail;

    /**
     * Envoyer un email de bienvenue
     */
    public boolean envoyerEmailBienvenue(String toEmail, String nomUtilisateur, String nomEntreprise) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("dateInscription", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        variables.put("supportEmail", supportEmail);
        
        return envoyerEmailTemplate(toEmail, "Bienvenue sur E-COMPTA-IA", "welcome", variables);
    }

    /**
     * Envoyer un email de confirmation d'inscription
     */
    public boolean envoyerEmailConfirmationInscription(String toEmail, String nomUtilisateur, String tokenConfirmation) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("tokenConfirmation", tokenConfirmation);
        variables.put("lienConfirmation", "https://app.ecomptaia.com/confirmer-email?token=" + tokenConfirmation);
        variables.put("dateExpiration", LocalDateTime.now().plusHours(24).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        return envoyerEmailTemplate(toEmail, "Confirmez votre inscription - E-COMPTA-IA", "email-confirmation", variables);
    }

    /**
     * Envoyer un email de réinitialisation de mot de passe
     */
    public boolean envoyerEmailResetPassword(String toEmail, String nomUtilisateur, String tokenReset) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("tokenReset", tokenReset);
        variables.put("lienReset", "https://app.ecomptaia.com/reset-password?token=" + tokenReset);
        variables.put("dateExpiration", LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        return envoyerEmailTemplate(toEmail, "Réinitialisation de votre mot de passe - E-COMPTA-IA", "reset-password", variables);
    }

    /**
     * Envoyer un email de notification d'abonnement
     */
    public boolean envoyerEmailAbonnement(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                        String planAbonnement, String montant, String devise, String cycleFacturation) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("planAbonnement", planAbonnement);
        variables.put("montant", montant);
        variables.put("devise", devise);
        variables.put("cycleFacturation", cycleFacturation);
        variables.put("dateAbonnement", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        return envoyerEmailTemplate(toEmail, "Confirmation d'abonnement - E-COMPTA-IA", "subscription", variables);
    }

    /**
     * Envoyer un email de rappel d'abonnement
     */
    public boolean envoyerEmailRappelAbonnement(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                              String planAbonnement, String dateExpiration) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("planAbonnement", planAbonnement);
        variables.put("dateExpiration", dateExpiration);
        variables.put("lienRenouvellement", "https://app.ecomptaia.com/renouveler-abonnement");
        
        return envoyerEmailTemplate(toEmail, "Rappel : Votre abonnement expire bientôt - E-COMPTA-IA", "subscription-reminder", variables);
    }

    /**
     * Envoyer un email de notification de facture
     */
    public boolean envoyerEmailFacture(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                     String numeroFacture, String montant, String devise, String dateEcheance) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("numeroFacture", numeroFacture);
        variables.put("montant", montant);
        variables.put("devise", devise);
        variables.put("dateEcheance", dateEcheance);
        variables.put("lienPaiement", "https://app.ecomptaia.com/paiement/" + numeroFacture);
        
        return envoyerEmailTemplate(toEmail, "Nouvelle facture disponible - E-COMPTA-IA", "invoice", variables);
    }

    /**
     * Envoyer un email de notification de validation comptable
     */
    public boolean envoyerEmailValidationComptable(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                                 String typeOperation, String numeroOperation, String montant, String devise) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("typeOperation", typeOperation);
        variables.put("numeroOperation", numeroOperation);
        variables.put("montant", montant);
        variables.put("devise", devise);
        variables.put("dateValidation", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        return envoyerEmailTemplate(toEmail, "Opération comptable validée - E-COMPTA-IA", "accounting-validation", variables);
    }

    /**
     * Envoyer un email de notification d'anomalie détectée
     */
    public boolean envoyerEmailAnomalieDetectee(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                              String typeAnomalie, String description, String niveauUrgence) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("typeAnomalie", typeAnomalie);
        variables.put("description", description);
        variables.put("niveauUrgence", niveauUrgence);
        variables.put("dateDetection", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        variables.put("lienCorrection", "https://app.ecomptaia.com/anomalies");
        
        return envoyerEmailTemplate(toEmail, "Anomalie détectée dans votre comptabilité - E-COMPTA-IA", "anomaly-detected", variables);
    }

    /**
     * Envoyer un email de rapport mensuel
     */
    public boolean envoyerEmailRapportMensuel(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                            String moisAnnee, Map<String, Object> statistiques) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("moisAnnee", moisAnnee);
        variables.put("statistiques", statistiques);
        variables.put("dateGeneration", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        variables.put("lienRapport", "https://app.ecomptaia.com/rapports/" + moisAnnee);
        
        return envoyerEmailTemplate(toEmail, "Rapport mensuel " + moisAnnee + " - E-COMPTA-IA", "monthly-report", variables);
    }

    /**
     * Envoyer un email de notification de sauvegarde
     */
    public boolean envoyerEmailSauvegarde(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                        String typeSauvegarde, String statut, String details) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("typeSauvegarde", typeSauvegarde);
        variables.put("statut", statut);
        variables.put("details", details);
        variables.put("dateSauvegarde", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        return envoyerEmailTemplate(toEmail, "Notification de sauvegarde - E-COMPTA-IA", "backup-notification", variables);
    }

    /**
     * Envoyer un email de support technique
     */
    public boolean envoyerEmailSupport(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                     String sujet, String message, String priorite) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("sujet", sujet);
        variables.put("message", message);
        variables.put("priorite", priorite);
        variables.put("dateDemande", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        variables.put("numeroTicket", "TICKET-" + System.currentTimeMillis());
        
        return envoyerEmailTemplate(toEmail, "Demande de support - " + sujet + " - E-COMPTA-IA", "support-request", variables);
    }

    /**
     * Envoyer un email de notification de maintenance
     */
    public boolean envoyerEmailMaintenance(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                         String dateMaintenance, String duree, String impact) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("dateMaintenance", dateMaintenance);
        variables.put("duree", duree);
        variables.put("impact", impact);
        variables.put("dateNotification", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        return envoyerEmailTemplate(toEmail, "Maintenance programmée - E-COMPTA-IA", "maintenance-notification", variables);
    }

    /**
     * Envoyer un email de notification de nouvelle fonctionnalité
     */
    public boolean envoyerEmailNouvelleFonctionnalite(String toEmail, String nomUtilisateur, String nomEntreprise, 
                                                    String nomFonctionnalite, String description, String benefices) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", nomUtilisateur);
        variables.put("nomEntreprise", nomEntreprise);
        variables.put("nomFonctionnalite", nomFonctionnalite);
        variables.put("description", description);
        variables.put("benefices", benefices);
        variables.put("datePublication", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        variables.put("lienDocumentation", "https://docs.ecomptaia.com/nouvelles-fonctionnalites");
        
        return envoyerEmailTemplate(toEmail, "Nouvelle fonctionnalité disponible - E-COMPTA-IA", "new-feature", variables);
    }

    /**
     * Méthode générique pour envoyer un email avec template
     */
    private boolean envoyerEmailTemplate(String toEmail, String sujet, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(noreplyEmail);
            helper.setTo(toEmail);
            helper.setSubject(sujet);
            
            // Ajouter les variables par défaut
            variables.put("dateEnvoi", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            variables.put("supportEmail", supportEmail);
            variables.put("appName", "E-COMPTA-IA");
            variables.put("appUrl", "https://app.ecomptaia.com");
            
            Context context = new Context();
            context.setVariables(variables);
            
            String htmlContent = templateEngine.process("emails/" + templateName, context);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email à " + toEmail + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Envoyer un email simple sans template
     */
    public boolean envoyerEmailSimple(String toEmail, String sujet, String contenu) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(noreplyEmail);
            helper.setTo(toEmail);
            helper.setSubject(sujet);
            helper.setText(contenu, true);
            
            mailSender.send(message);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email simple à " + toEmail + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Tester la configuration email
     */
    public boolean testerConfigurationEmail(String toEmail) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("nomUtilisateur", "Test");
        variables.put("nomEntreprise", "Entreprise Test");
        variables.put("dateTest", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        return envoyerEmailTemplate(toEmail, "Test de configuration email - E-COMPTA-IA", "test-config", variables);
    }
}




