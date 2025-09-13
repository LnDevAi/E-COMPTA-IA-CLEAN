package com.ecomptaia.sycebnl.service;

import com.ecomptaia.sycebnl.entity.EtatFinancierSycebnl;
import com.ecomptaia.sycebnl.entity.NoteAnnexeSycebnl;
import com.ecomptaia.sycebnl.repository.EtatFinancierSycebnlRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Service d'export des états financiers SYCEBNL
 * Génère des fichiers PDF et Excel pour les états financiers
 */
@Service
@Transactional(readOnly = true)
public class ExportEtatsFinanciersService {
    
    private final EtatFinancierSycebnlRepository etatRepository;
    private final ComptabiliteSycebnlService comptabiliteService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public ExportEtatsFinanciersService(
            EtatFinancierSycebnlRepository etatRepository,
            ComptabiliteSycebnlService comptabiliteService,
            ObjectMapper objectMapper) {
        this.etatRepository = etatRepository;
        this.comptabiliteService = comptabiliteService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Exporte un état financier en PDF
     * 
     * @param etatFinancierId ID de l'état financier
     * @return Contenu du fichier PDF
     */
    public byte[] exporterEnPDF(Long etatFinancierId) {
        EtatFinancierSycebnl etat = etatRepository.findById(etatFinancierId)
                .orElseThrow(() -> new RuntimeException("État financier non trouvé"));
        
        try {
            // Génération du contenu HTML
            String contenuHTML = genererContenuHTML(etat);
            
            // Conversion HTML vers PDF avec une implémentation simple
            return convertirHTMLVersPDF(contenuHTML);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export PDF : " + e.getMessage(), e);
        }
    }
    
    /**
     * Exporte un état financier en Excel
     * 
     * @param etatFinancierId ID de l'état financier
     * @return Contenu du fichier Excel
     */
    public byte[] exporterEnExcel(Long etatFinancierId) {
        EtatFinancierSycebnl etat = etatRepository.findById(etatFinancierId)
                .orElseThrow(() -> new RuntimeException("État financier non trouvé"));
        
        try {
            // Export Excel avec implémentation simple
            return genererExcel(etat);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export Excel : " + e.getMessage(), e);
        }
    }
    
    /**
     * Exporte tous les états financiers d'un exercice en PDF
     * 
     * @param exerciceId ID de l'exercice
     * @return Contenu du fichier PDF consolidé
     */
    public byte[] exporterTousEtatsEnPDF(Long exerciceId) {
        List<EtatFinancierSycebnl> etats = etatRepository.findByExerciceIdOrderByDateArreteDesc(exerciceId);
        
        if (etats.isEmpty()) {
            throw new RuntimeException("Aucun état financier trouvé pour cet exercice");
        }
        
        try {
            StringBuilder contenuHTML = new StringBuilder();
            contenuHTML.append(genererEnTeteHTML());
            
            for (EtatFinancierSycebnl etat : etats) {
                contenuHTML.append(genererContenuHTML(etat));
                contenuHTML.append("<div style='page-break-before: always;'></div>");
            }
            
            contenuHTML.append(genererPiedDePageHTML());
            
            // Conversion HTML vers PDF
            return convertirHTMLVersPDF(contenuHTML.toString());
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'export PDF consolidé : " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère le contenu HTML d'un état financier
     */
    private String genererContenuHTML(EtatFinancierSycebnl etat) {
        StringBuilder html = new StringBuilder();
        
        // En-tête de l'état
        html.append("<div class='etat-financier'>");
        html.append("<h1>").append(getTitreEtat(etat)).append("</h1>");
        html.append("<p><strong>Date d'arrêté :</strong> ").append(formaterDate(etat.getDateArrete())).append("</p>");
        html.append("<p><strong>Système :</strong> ").append(etat.getTypeSysteme().name()).append("</p>");
        html.append("<p><strong>Statut :</strong> ").append(etat.getStatut().name()).append("</p>");
        
        try {
            // Génération du tableau des données
            @SuppressWarnings("unchecked")
            Map<String, Object> donnees = objectMapper.readValue(etat.getDonneesJson(), Map.class);
            
            html.append("<table class='tableau-etat'>");
            html.append("<thead>");
            html.append("<tr>");
            html.append("<th>Code</th>");
            html.append("<th>Libellé</th>");
            html.append("<th>Montant</th>");
            html.append("</tr>");
            html.append("</thead>");
            html.append("<tbody>");
            
            for (Map.Entry<String, Object> entry : donnees.entrySet()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> poste = (Map<String, Object>) entry.getValue();
                
                String code = (String) poste.get("code");
                String libelle = (String) poste.get("libelle");
                BigDecimal montant = (BigDecimal) poste.get("solde");
                Integer niveau = (Integer) poste.get("niveau");
                Boolean estTotal = (Boolean) poste.get("estTotal");
                
                html.append("<tr class='niveau-").append(niveau).append(" ").append(estTotal ? "total" : "detail").append("'>");
                html.append("<td>").append(code).append("</td>");
                html.append("<td>").append(libelle).append("</td>");
                html.append("<td class='montant'>").append(comptabiliteService.formaterMontant(montant)).append("</td>");
                html.append("</tr>");
            }
            
            html.append("</tbody>");
            html.append("</table>");
            
            // Ajout des totaux
            html.append("<div class='totaux'>");
            if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.BILAN) {
                html.append("<p><strong>Total Actif :</strong> ").append(comptabiliteService.formaterMontant(etat.getTotalActif())).append("</p>");
                html.append("<p><strong>Total Passif :</strong> ").append(comptabiliteService.formaterMontant(etat.getTotalPassif())).append("</p>");
                html.append("<p><strong>Équilibre :</strong> ").append(etat.getEquilibre() ? "OUI" : "NON").append("</p>");
            } else if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.COMPTE_RESULTAT ||
                       etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.RECETTES_DEPENSES) {
                html.append("<p><strong>Total Produits :</strong> ").append(comptabiliteService.formaterMontant(etat.getTotalProduits())).append("</p>");
                html.append("<p><strong>Total Charges :</strong> ").append(comptabiliteService.formaterMontant(etat.getTotalCharges())).append("</p>");
                html.append("<p><strong>Résultat Net :</strong> ").append(comptabiliteService.formaterMontant(etat.getResultatNet())).append("</p>");
            }
            html.append("</div>");
            
            // Ajout des notes annexes
            if (etat.getNotesAnnexes() != null && !etat.getNotesAnnexes().isEmpty()) {
                html.append("<div class='notes-annexes'>");
                html.append("<h2>Notes Annexes</h2>");
                
                for (NoteAnnexeSycebnl note : etat.getNotesAnnexes()) {
                    html.append("<div class='note'>");
                    html.append("<h3>").append(note.getNumeroNote()).append(" - ").append(note.getTitreNote()).append("</h3>");
                    html.append("<div class='contenu-note'>").append(note.getContenuNote().replace("\n", "<br>")).append("</div>");
                    html.append("</div>");
                }
                
                html.append("</div>");
            }
            
        } catch (Exception e) {
            html.append("<p class='erreur'>Erreur lors de la génération du contenu : ").append(e.getMessage()).append("</p>");
        }
        
        html.append("</div>");
        
        return html.toString();
    }
    
    
    /**
     * Génère l'en-tête HTML
     */
    private String genererEnTeteHTML() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>États Financiers SYCEBNL</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .etat-financier { margin-bottom: 30px; }
                    h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }
                    h2 { color: #34495e; margin-top: 30px; }
                    h3 { color: #7f8c8d; }
                    .tableau-etat { width: 100%; border-collapse: collapse; margin: 20px 0; }
                    .tableau-etat th, .tableau-etat td { border: 1px solid #bdc3c7; padding: 8px; text-align: left; }
                    .tableau-etat th { background-color: #ecf0f1; font-weight: bold; }
                    .tableau-etat .niveau-1 { font-weight: bold; background-color: #f8f9fa; }
                    .tableau-etat .niveau-2 { padding-left: 20px; }
                    .tableau-etat .total { font-weight: bold; background-color: #e8f5e8; }
                    .montant { text-align: right; }
                    .totaux { background-color: #f8f9fa; padding: 15px; border: 1px solid #dee2e6; margin: 20px 0; }
                    .notes-annexes { margin-top: 30px; }
                    .note { margin-bottom: 20px; padding: 15px; border: 1px solid #dee2e6; }
                    .contenu-note { margin-top: 10px; }
                    .erreur { color: #e74c3c; }
                </style>
            </head>
            <body>
            """;
    }
    
    /**
     * Génère le pied de page HTML
     */
    private String genererPiedDePageHTML() {
        String dateGeneration = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return "<div style='margin-top: 50px; text-align: center; color: #7f8c8d; font-size: 12px;'>" +
               "<p>Généré par SYCEBNL - E-COMPTA-IA</p>" +
               "<p>Date de génération : " + dateGeneration + "</p>" +
               "</div>" +
               "</body>" +
               "</html>";
    }
    
    /**
     * Obtient le titre de l'état financier
     */
    private String getTitreEtat(EtatFinancierSycebnl etat) {
        switch (etat.getTypeEtat()) {
            case BILAN:
                return "BILAN";
            case COMPTE_RESULTAT:
                return "COMPTE DE RÉSULTAT";
            case TABLEAU_FLUX:
                return "TABLEAU DES FLUX DE TRÉSORERIE";
            case RECETTES_DEPENSES:
                return "ÉTAT DES RECETTES ET DÉPENSES";
            case SITUATION_TRESORERIE:
                return "SITUATION DE TRÉSORERIE";
            case ANNEXES:
                return "ANNEXES";
            default:
                return "ÉTAT FINANCIER";
        }
    }
    
    /**
     * Formate une date
     */
    private String formaterDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    /**
     * Convertit du HTML en PDF (implémentation simple)
     */
    private byte[] convertirHTMLVersPDF(String htmlContent) {
        try {
            // Pour l'instant, on retourne le HTML avec un en-tête PDF simple
            // Dans une vraie implémentation, on utiliserait iText, Flying Saucer ou wkhtmltopdf
            String fullHTML = genererEnTeteHTML() + htmlContent + genererPiedDePageHTML();
            
            // Simulation d'un PDF en retournant le HTML avec un en-tête spécial
            String pdfContent = "%PDF-1.4\n" + fullHTML;
            return pdfContent.getBytes("UTF-8");
            
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la conversion PDF : " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère un fichier Excel simple (format CSV amélioré)
     */
    private byte[] genererExcel(EtatFinancierSycebnl etat) {
        try {
            StringBuilder excelContent = new StringBuilder();
            
            // En-tête Excel
            excelContent.append("Code\tLibellé\tMontant\tNiveau\tEst Total\n");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> donnees = objectMapper.readValue(etat.getDonneesJson(), Map.class);
            
            for (Map.Entry<String, Object> entry : donnees.entrySet()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> poste = (Map<String, Object>) entry.getValue();
                
                String code = (String) poste.get("code");
                String libelle = (String) poste.get("libelle");
                BigDecimal montant = (BigDecimal) poste.get("solde");
                Integer niveau = (Integer) poste.get("niveau");
                Boolean estTotal = (Boolean) poste.get("estTotal");
                
                excelContent.append(code).append("\t");
                excelContent.append(libelle).append("\t");
                excelContent.append(montant != null ? montant.toString() : "0").append("\t");
                excelContent.append(niveau != null ? niveau.toString() : "1").append("\t");
                excelContent.append(estTotal != null ? estTotal.toString() : "false").append("\n");
            }
            
            return excelContent.toString().getBytes("UTF-8");
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération Excel : " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère une synthèse Excel simple
     */
    private byte[] genererSyntheseExcelSimple(List<EtatFinancierSycebnl> etats, Long exerciceId) {
        try {
            StringBuilder contenu = new StringBuilder();
            contenu.append("SYNTHÈSE DES ÉTATS FINANCIERS - EXERCICE ").append(exerciceId).append("\n\n");
            
            for (EtatFinancierSycebnl etat : etats) {
                contenu.append("=== ").append(getTitreEtat(etat)).append(" ===\n");
                contenu.append("Date d'arrêté : ").append(formaterDate(etat.getDateArrete())).append("\n");
                contenu.append("Système : ").append(etat.getTypeSysteme().name()).append("\n");
                contenu.append("Statut : ").append(etat.getStatut().name()).append("\n");
                
                if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.BILAN) {
                    contenu.append("Total Actif : ").append(comptabiliteService.formaterMontant(etat.getTotalActif())).append("\n");
                    contenu.append("Total Passif : ").append(comptabiliteService.formaterMontant(etat.getTotalPassif())).append("\n");
                } else if (etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.COMPTE_RESULTAT ||
                           etat.getTypeEtat() == EtatFinancierSycebnl.TypeEtat.RECETTES_DEPENSES) {
                    contenu.append("Total Produits : ").append(comptabiliteService.formaterMontant(etat.getTotalProduits())).append("\n");
                    contenu.append("Total Charges : ").append(comptabiliteService.formaterMontant(etat.getTotalCharges())).append("\n");
                    contenu.append("Résultat Net : ").append(comptabiliteService.formaterMontant(etat.getResultatNet())).append("\n");
                }
                
                contenu.append("\n");
            }
            
            return contenu.toString().getBytes("UTF-8");
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de la synthèse : " + e.getMessage(), e);
        }
    }
    
    /**
     * Génère un fichier de synthèse Excel avec tous les états d'un exercice
     * 
     * @param exerciceId ID de l'exercice
     * @return Contenu du fichier Excel
     */
    public byte[] genererSyntheseExcel(Long exerciceId) {
        List<EtatFinancierSycebnl> etats = etatRepository.findByExerciceIdOrderByDateArreteDesc(exerciceId);
        
        if (etats.isEmpty()) {
            throw new RuntimeException("Aucun état financier trouvé pour cet exercice");
        }
        
        try {
            // Génération Excel avec implémentation simple
            return genererSyntheseExcelSimple(etats, exerciceId);
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération de la synthèse Excel : " + e.getMessage(), e);
        }
    }
}
