package com.ecomptaia.service;

import com.ecomptaia.model.ohada.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Service d'export PDF pour les états financiers OHADA/SYSCOHADA
 */
@Service
public class OHADAPDFExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Générer le PDF du Bilan OHADA
     */
    public byte[] generateBalanceSheetPDF(BilanSYSCOHADA bilan, String companyName, LocalDate asOfDate) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        
        addHeader(document, companyName, "BILAN", asOfDate);
        addBalanceSheetTable(document, bilan);
        addSignature(document);
        
        document.close();
        return baos.toByteArray();
    }
    
    /**
     * Générer le PDF du Compte de Résultat OHADA
     */
    public byte[] generateIncomeStatementPDF(CompteResultatSYSCOHADA compteResultat, String companyName, 
                                           LocalDate startDate, LocalDate endDate) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        
        addHeader(document, companyName, "COMPTE DE RÉSULTAT", startDate, endDate);
        addIncomeStatementTable(document, compteResultat);
        addSIGSection(document, compteResultat);
        addSignature(document);
        
        document.close();
        return baos.toByteArray();
    }
    
    /**
     * Générer le PDF du Tableau des Flux de Trésorerie OHADA
     */
    public byte[] generateCashFlowPDF(TableauFluxSYSCOHADA tableauFlux, String companyName,
                                    LocalDate startDate, LocalDate endDate) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        
        addHeader(document, companyName, "TABLEAU DES FLUX DE TRÉSORERIE", startDate, endDate);
        addCashFlowTable(document, tableauFlux);
        addSignature(document);
        
        document.close();
        return baos.toByteArray();
    }
    
    /**
     * Générer le PDF des Annexes OHADA
     */
    public byte[] generateAnnexesPDF(AnnexesSYSCOHADA annexes, String companyName, LocalDate asOfDate) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        
        addHeader(document, companyName, "ANNEXES AUX ÉTATS FINANCIERS", asOfDate);
        addAnnexesNotes(document, annexes);
        addSignature(document);
        
        document.close();
        return baos.toByteArray();
    }
    
    /**
     * Générer le PDF complet (tous les états financiers)
     */
    public byte[] generateCompleteFinancialStatementsPDF(BilanSYSCOHADA bilan, CompteResultatSYSCOHADA compteResultat,
                                                        TableauFluxSYSCOHADA tableauFlux, AnnexesSYSCOHADA annexes,
                                                        String companyName, LocalDate startDate, LocalDate endDate) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        
        addTitlePage(document, companyName, startDate, endDate);
        addTableOfContents(document);
        
        document.add(new AreaBreak());
        addHeader(document, companyName, "BILAN", endDate);
        addBalanceSheetTable(document, bilan);
        
        document.add(new AreaBreak());
        addHeader(document, companyName, "COMPTE DE RÉSULTAT", startDate, endDate);
        addIncomeStatementTable(document, compteResultat);
        addSIGSection(document, compteResultat);
        
        document.add(new AreaBreak());
        addHeader(document, companyName, "TABLEAU DES FLUX DE TRÉSORERIE", startDate, endDate);
        addCashFlowTable(document, tableauFlux);
        
        document.add(new AreaBreak());
        addHeader(document, companyName, "ANNEXES AUX ÉTATS FINANCIERS", endDate);
        addAnnexesNotes(document, annexes);
        
        document.add(new AreaBreak());
        addSignature(document);
        
        document.close();
        return baos.toByteArray();
    }
    
    // Méthodes privées pour la construction du PDF
    
    private void addHeader(Document document, String companyName, String title, LocalDate date) throws IOException {
        addHeader(document, companyName, title, date, date);
    }
    
    private void addHeader(Document document, String companyName, String title, LocalDate startDate, LocalDate endDate) throws IOException {
        // Titre principal
        Paragraph titleParagraph = new Paragraph(title)
            .setFontSize(18)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(titleParagraph);
        
        // Informations de l'entreprise
        Paragraph companyParagraph = new Paragraph(companyName)
            .setFontSize(14)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10);
        document.add(companyParagraph);
        
        // Période
        String periodText = startDate.equals(endDate) ? 
            "Au " + startDate.format(DATE_FORMATTER) :
            "Du " + startDate.format(DATE_FORMATTER) + " au " + endDate.format(DATE_FORMATTER);
        
        Paragraph periodParagraph = new Paragraph(periodText)
            .setFontSize(12)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(periodParagraph);
        
        // Standard OHADA
        Paragraph standardParagraph = new Paragraph("Conforme aux standards OHADA/SYSCOHADA")
            .setFontSize(10)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(30);
        document.add(standardParagraph);
    }
    
    private void addBalanceSheetTable(Document document, BilanSYSCOHADA bilan) throws IOException {
        // Tableau Actif
        Table actifTable = new Table(UnitValue.createPercentArray(new float[]{20, 50, 30}))
            .useAllAvailableWidth()
            .setMarginBottom(20);
        
        // En-tête Actif
        actifTable.addHeaderCell(createHeaderCell("ACTIF"));
        actifTable.addHeaderCell(createHeaderCell(""));
        actifTable.addHeaderCell(createHeaderCell("Montants"));
        
        // Immobilisations
        addSectionHeader(actifTable, "IMMOBILISATIONS");
        addPostesToTable(actifTable, bilan.getActifImmobilise());
        addTotalRow(actifTable, "Total Immobilisations", bilan.getTotalActifImmobilise());
        
        // Actif Circulant
        addSectionHeader(actifTable, "ACTIF CIRCULANT");
        addPostesToTable(actifTable, bilan.getActifCirculant());
        addTotalRow(actifTable, "Total Actif Circulant", bilan.getTotalActifCirculant());
        
        // Trésorerie Actif
        addSectionHeader(actifTable, "TRÉSORERIE ACTIF");
        addPostesToTable(actifTable, bilan.getTresorerieActif());
        addTotalRow(actifTable, "Total Trésorerie Actif", bilan.getTotalTresorerieActif());
        
        // Total Actif
        addTotalRow(actifTable, "TOTAL ACTIF", bilan.getTotalActif());
        
        document.add(actifTable);
        
        // Tableau Passif
        Table passifTable = new Table(UnitValue.createPercentArray(new float[]{20, 50, 30}))
            .useAllAvailableWidth()
            .setMarginBottom(20);
        
        // En-tête Passif
        passifTable.addHeaderCell(createHeaderCell("PASSIF"));
        passifTable.addHeaderCell(createHeaderCell(""));
        passifTable.addHeaderCell(createHeaderCell("Montants"));
        
        // Capitaux Propres
        addSectionHeader(passifTable, "CAPITAUX PROPRES");
        addPostesToTable(passifTable, bilan.getCapitauxPropres());
        addTotalRow(passifTable, "Total Capitaux Propres", bilan.getTotalCapitauxPropres());
        
        // Dettes Financières
        addSectionHeader(passifTable, "DETTES FINANCIÈRES");
        addPostesToTable(passifTable, bilan.getDettesFinancieres());
        addTotalRow(passifTable, "Total Dettes Financières", bilan.getTotalDettesFinancieres());
        
        // Passif Circulant
        addSectionHeader(passifTable, "PASSIF CIRCULANT");
        addPostesToTable(passifTable, bilan.getPassifCirculant());
        addTotalRow(passifTable, "Total Passif Circulant", bilan.getTotalPassifCirculant());
        
        // Trésorerie Passif
        addSectionHeader(passifTable, "TRÉSORERIE PASSIF");
        addPostesToTable(passifTable, bilan.getTresoreriePassif());
        addTotalRow(passifTable, "Total Trésorerie Passif", bilan.getTotalTresoreriePassif());
        
        // Total Passif
        addTotalRow(passifTable, "TOTAL PASSIF", bilan.getTotalPassif());
        
        document.add(passifTable);
    }
    
    private void addIncomeStatementTable(Document document, CompteResultatSYSCOHADA compteResultat) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{20, 50, 30}))
            .useAllAvailableWidth()
            .setMarginBottom(20);
        
        // En-tête
        table.addHeaderCell(createHeaderCell("PRODUITS"));
        table.addHeaderCell(createHeaderCell(""));
        table.addHeaderCell(createHeaderCell("Montants"));
        
        // Produits
        addPostesToTable(table, compteResultat.getProduits());
        addTotalRow(table, "TOTAL PRODUITS", compteResultat.getTotalProduits());
        
        document.add(table);
        
        // Tableau des charges
        Table chargesTable = new Table(UnitValue.createPercentArray(new float[]{20, 50, 30}))
            .useAllAvailableWidth()
            .setMarginBottom(20);
        
        chargesTable.addHeaderCell(createHeaderCell("CHARGES"));
        chargesTable.addHeaderCell(createHeaderCell(""));
        chargesTable.addHeaderCell(createHeaderCell("Montants"));
        
        addPostesToTable(chargesTable, compteResultat.getCharges());
        addTotalRow(chargesTable, "TOTAL CHARGES", compteResultat.getTotalCharges());
        
        document.add(chargesTable);
    }
    
    private void addSIGSection(Document document, CompteResultatSYSCOHADA compteResultat) throws IOException {
        Paragraph sigTitle = new Paragraph("SOLDES INTERMÉDIAIRES DE GESTION (SIG)")
            .setFontSize(14)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(sigTitle);
        
        Table sigTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
            .useAllAvailableWidth()
            .setMarginBottom(20);
        
        sigTable.addHeaderCell(createHeaderCell("Soldes Intermédiaires"));
        sigTable.addHeaderCell(createHeaderCell("Montants"));
        
        addSIGRow(sigTable, "Marge brute", compteResultat.getMargeBrute());
        addSIGRow(sigTable, "Valeur ajoutée", compteResultat.getValeurAjoutee());
        addSIGRow(sigTable, "Excédent brut d'exploitation", compteResultat.getExcedentBrutExploitation());
        addSIGRow(sigTable, "Résultat d'exploitation", compteResultat.getResultatExploitation());
        addSIGRow(sigTable, "Résultat financier", compteResultat.getResultatFinancier());
        addSIGRow(sigTable, "Résultat exceptionnel", compteResultat.getResultatExceptionnel());
        addSIGRow(sigTable, "Résultat avant impôt", compteResultat.getResultatAvantImpot());
        addSIGRow(sigTable, "Résultat net", compteResultat.getResultatNet());
        
        document.add(sigTable);
    }
    
    private void addCashFlowTable(Document document, TableauFluxSYSCOHADA tableauFlux) throws IOException {
        Table table = new Table(UnitValue.createPercentArray(new float[]{70, 30}))
            .useAllAvailableWidth()
            .setMarginBottom(20);
        
        // En-tête
        table.addHeaderCell(createHeaderCell("Flux de Trésorerie"));
        table.addHeaderCell(createHeaderCell("Montants"));
        
        // Flux d'activité
        addSectionHeader(table, "FLUX DE TRÉSORERIE LIÉS À L'ACTIVITÉ");
        addPostesToTable(table, tableauFlux.getFluxActivite());
        addTotalRow(table, "Total Flux d'Activité", tableauFlux.getTotalFluxActivite());
        
        // Flux d'investissement
        addSectionHeader(table, "FLUX DE TRÉSORERIE LIÉS AUX INVESTISSEMENTS");
        addPostesToTable(table, tableauFlux.getFluxInvestissement());
        addTotalRow(table, "Total Flux d'Investissement", tableauFlux.getTotalFluxInvestissement());
        
        // Flux de financement
        addSectionHeader(table, "FLUX DE TRÉSORERIE LIÉS AU FINANCEMENT");
        addPostesToTable(table, tableauFlux.getFluxFinancement());
        addTotalRow(table, "Total Flux de Financement", tableauFlux.getTotalFluxFinancement());
        
        // Variation de trésorerie
        addTotalRow(table, "Variation de Trésorerie", tableauFlux.getVariationTresorerie());
        addTotalRow(table, "Trésorerie Début", tableauFlux.getTresorerieDebut());
        addTotalRow(table, "Trésorerie Fin", tableauFlux.getTresorerieFin());
        
        document.add(table);
    }
    
    private void addAnnexesNotes(Document document, AnnexesSYSCOHADA annexes) throws IOException {
        for (Map.Entry<String, NoteAnnexe> entry : annexes.getNotes().entrySet()) {
            NoteAnnexe note = entry.getValue();
            
            Paragraph noteTitle = new Paragraph("Note " + note.getNumero() + " : " + note.getTitre())
                .setFontSize(12)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(5);
            document.add(noteTitle);
            
            if (note.getContenu() != null) {
                Paragraph noteContent = new Paragraph(note.getContenu())
                    .setFontSize(10)
                    .setMarginBottom(10);
                document.add(noteContent);
            }
        }
    }
    
    private void addTitlePage(Document document, String companyName, LocalDate startDate, LocalDate endDate) throws IOException {
        Paragraph title = new Paragraph("ÉTATS FINANCIERS")
            .setFontSize(24)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(200)
            .setMarginBottom(50);
        document.add(title);
        
        Paragraph company = new Paragraph(companyName)
            .setFontSize(18)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(30);
        document.add(company);
        
        String period = "Exercice clos le " + endDate.format(DATE_FORMATTER);
        Paragraph periodText = new Paragraph(period)
            .setFontSize(14)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(100);
        document.add(periodText);
        
        Paragraph standard = new Paragraph("Conforme aux standards OHADA/SYSCOHADA")
            .setFontSize(12)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER);
        document.add(standard);
    }
    
    private void addTableOfContents(Document document) throws IOException {
        Paragraph tocTitle = new Paragraph("TABLE DES MATIÈRES")
            .setFontSize(16)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(tocTitle);
        
        String[] sections = {
            "1. Bilan",
            "2. Compte de Résultat",
            "3. Soldes Intermédiaires de Gestion",
            "4. Tableau des Flux de Trésorerie",
            "5. Annexes aux États Financiers"
        };
        
        for (String section : sections) {
            Paragraph sectionPara = new Paragraph(section)
                .setFontSize(12)
                .setMarginBottom(5);
            document.add(sectionPara);
        }
    }
    
    private void addSignature(Document document) throws IOException {
        Paragraph signature = new Paragraph("Signature et cachet")
            .setFontSize(10)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(50);
        document.add(signature);
    }
    
    // Méthodes utilitaires pour la construction des tableaux
    
    private Cell createHeaderCell(String text) throws IOException {
        return new Cell()
            .add(new Paragraph(text).setBold())
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER);
    }
    
    private void addSectionHeader(Table table, String sectionName) throws IOException {
        Cell cell = new Cell(1, 3)
            .add(new Paragraph(sectionName).setBold())
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setMarginTop(5);
        table.addCell(cell);
    }
    
    private void addPostesToTable(Table table, Map<String, ?> postes) throws IOException {
        for (Map.Entry<String, ?> entry : postes.entrySet()) {
            Object poste = entry.getValue();
            String code = "";
            String libelle = "";
            BigDecimal valeur = BigDecimal.ZERO;
            
            if (poste instanceof PosteBilan) {
                PosteBilan pb = (PosteBilan) poste;
                code = pb.getCode();
                libelle = pb.getLibelle();
                valeur = pb.getValeurExerciceCourant();
            } else if (poste instanceof PosteCompteResultat) {
                PosteCompteResultat pcr = (PosteCompteResultat) poste;
                code = pcr.getCode();
                libelle = pcr.getLibelle();
                valeur = pcr.getValeur();
            } else if (poste instanceof PosteFluxTresorerie) {
                PosteFluxTresorerie pft = (PosteFluxTresorerie) poste;
                code = pft.getCode();
                libelle = pft.getLibelle();
                valeur = pft.getValeur();
            }
            
            if (valeur.compareTo(BigDecimal.ZERO) != 0) {
                table.addCell(new Cell().add(new Paragraph(code)));
                table.addCell(new Cell().add(new Paragraph(libelle)));
                table.addCell(new Cell().add(new Paragraph(formatAmount(valeur))).setTextAlignment(TextAlignment.RIGHT));
            }
        }
    }
    
    private void addTotalRow(Table table, String label, BigDecimal amount) throws IOException {
        table.addCell(new Cell().add(new Paragraph(label).setBold()));
        table.addCell(new Cell().add(new Paragraph("")).setBold());
        table.addCell(new Cell().add(new Paragraph(formatAmount(amount)).setBold()).setTextAlignment(TextAlignment.RIGHT));
    }
    
    private void addSIGRow(Table table, String label, BigDecimal amount) throws IOException {
        table.addCell(new Cell().add(new Paragraph(label)));
        table.addCell(new Cell().add(new Paragraph(formatAmount(amount))).setTextAlignment(TextAlignment.RIGHT));
    }
    
    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0,00";
        return String.format("%,.2f", amount.doubleValue()).replace(",", " ").replace(".", ",");
    }
}
