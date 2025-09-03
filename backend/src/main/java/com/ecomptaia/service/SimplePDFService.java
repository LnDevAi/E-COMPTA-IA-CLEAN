package com.ecomptaia.service;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Service PDF simplifié pour tester la génération de PDF
 */
@Service
public class SimplePDFService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Générer un PDF de test simple
     */
    public byte[] generateSimpleTestPDF(String companyName, LocalDate asOfDate) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        
        // Titre
        Paragraph title = new Paragraph("BILAN OHADA - TEST")
            .setFontSize(18)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(title);
        
        // Informations de l'entreprise
        Paragraph company = new Paragraph("Entreprise: " + companyName)
            .setFontSize(14)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10);
        document.add(company);
        
        // Date
        Paragraph date = new Paragraph("Date: " + asOfDate.format(DATE_FORMATTER))
            .setFontSize(12)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(30);
        document.add(date);
        
        // Contenu de test
        Paragraph content = new Paragraph("Ceci est un test de génération PDF OHADA.\n\n" +
            "Le service PDF fonctionne correctement si vous voyez ce message.\n\n" +
            "Prochaines étapes :\n" +
            "• Intégration des données OHADA\n" +
            "• Formatage des tableaux\n" +
            "• Styles professionnels")
            .setFontSize(12)
            .setMarginBottom(20);
        document.add(content);
        
        // Signature
        Paragraph signature = new Paragraph("Signature et cachet")
            .setFontSize(10)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(50);
        document.add(signature);
        
        document.close();
        return baos.toByteArray();
    }
}






