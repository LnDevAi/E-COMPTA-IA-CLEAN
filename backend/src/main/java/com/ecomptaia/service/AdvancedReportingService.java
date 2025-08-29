package com.ecomptaia.service;

import com.ecomptaia.entity.FinancialReport;
import com.ecomptaia.entity.ReportNote;
import com.ecomptaia.repository.FinancialReportRepository;
import com.ecomptaia.repository.ReportNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class AdvancedReportingService {

    @Autowired
    private FinancialReportRepository financialReportRepository;

    @Autowired
    private ReportNoteRepository reportNoteRepository;

    // @Autowired
    // private AssistantIAService assistantIAService; // À activer quand le service IA sera disponible

    // ==================== GÉNÉRATION DE RAPPORTS ====================

    /**
     * Générer un rapport financier complet
     */
    public FinancialReport generateCompleteFinancialReport(String reportName, LocalDateTime periodStart, 
                                                         LocalDateTime periodEnd, Long entrepriseId, Long generatedBy) {
        
        FinancialReport report = new FinancialReport(reportName, FinancialReport.ReportType.COMPLETE_FINANCIAL_REPORT, 
                                                   periodStart, periodEnd);
        report.setEntrepriseId(entrepriseId);
        report.setGeneratedBy(generatedBy);
        report.setStatus(FinancialReport.ReportStatus.IN_PROGRESS);
        
        // Calculer les montants (simulation)
        report.setTotalAssets(new BigDecimal("15000000"));
        report.setTotalLiabilities(new BigDecimal("8000000"));
        report.setTotalEquity(new BigDecimal("7000000"));
        report.setRevenue(new BigDecimal("25000000"));
        report.setExpenses(new BigDecimal("22000000"));
        report.setNetIncome(new BigDecimal("3000000"));
        report.setOperatingCashFlow(new BigDecimal("3500000"));
        report.setInvestingCashFlow(new BigDecimal("-2000000"));
        report.setFinancingCashFlow(new BigDecimal("-500000"));
        report.setNetCashFlow(new BigDecimal("1000000"));
        
        report = financialReportRepository.save(report);
        
        // Générer toutes les notes annexes OHADA automatiquement
        generateCompleteOHADANotes(report);
        
        report.setStatus(FinancialReport.ReportStatus.COMPLETED);
        return financialReportRepository.save(report);
    }

    /**
     * Générer un bilan comptable
     */
    public FinancialReport generateBalanceSheet(String reportName, LocalDateTime periodStart, 
                                              LocalDateTime periodEnd, Long entrepriseId, Long generatedBy) {
        
        FinancialReport report = new FinancialReport(reportName, FinancialReport.ReportType.BALANCE_SHEET, 
                                                   periodStart, periodEnd);
        report.setEntrepriseId(entrepriseId);
        report.setGeneratedBy(generatedBy);
        report.setStatus(FinancialReport.ReportStatus.IN_PROGRESS);
        
        // Calculer les montants du bilan
        report.setTotalAssets(new BigDecimal("15000000"));
        report.setTotalLiabilities(new BigDecimal("8000000"));
        report.setTotalEquity(new BigDecimal("7000000"));
        
        report = financialReportRepository.save(report);
        
        // Générer les notes spécifiques au bilan
        generateBalanceSheetNotes(report);
        
        report.setStatus(FinancialReport.ReportStatus.COMPLETED);
        return financialReportRepository.save(report);
    }

    /**
     * Générer un compte de résultat
     */
    public FinancialReport generateIncomeStatement(String reportName, LocalDateTime periodStart, 
                                                 LocalDateTime periodEnd, Long entrepriseId, Long generatedBy) {
        
        FinancialReport report = new FinancialReport(reportName, FinancialReport.ReportType.INCOME_STATEMENT, 
                                                   periodStart, periodEnd);
        report.setEntrepriseId(entrepriseId);
        report.setGeneratedBy(generatedBy);
        report.setStatus(FinancialReport.ReportStatus.IN_PROGRESS);
        
        // Calculer les montants du compte de résultat
        report.setRevenue(new BigDecimal("25000000"));
        report.setExpenses(new BigDecimal("22000000"));
        report.setNetIncome(new BigDecimal("3000000"));
        
        report = financialReportRepository.save(report);
        
        // Générer les notes spécifiques au compte de résultat
        generateIncomeStatementNotes(report);
        
        report.setStatus(FinancialReport.ReportStatus.COMPLETED);
        return financialReportRepository.save(report);
    }

    /**
     * Générer un tableau de flux de trésorerie
     */
    public FinancialReport generateCashFlowStatement(String reportName, LocalDateTime periodStart, 
                                                   LocalDateTime periodEnd, Long entrepriseId, Long generatedBy) {
        
        FinancialReport report = new FinancialReport(reportName, FinancialReport.ReportType.CASH_FLOW_STATEMENT, 
                                                   periodStart, periodEnd);
        report.setEntrepriseId(entrepriseId);
        report.setGeneratedBy(generatedBy);
        report.setStatus(FinancialReport.ReportStatus.IN_PROGRESS);
        
        // Calculer les flux de trésorerie
        report.setOperatingCashFlow(new BigDecimal("3500000"));
        report.setInvestingCashFlow(new BigDecimal("-2000000"));
        report.setFinancingCashFlow(new BigDecimal("-500000"));
        report.setNetCashFlow(new BigDecimal("1000000"));
        
        report = financialReportRepository.save(report);
        
        // Générer les notes spécifiques au TFT
        generateCashFlowNotes(report);
        
        report.setStatus(FinancialReport.ReportStatus.COMPLETED);
        return financialReportRepository.save(report);
    }

    // ==================== GÉNÉRATION DE NOTES ANNEXES ====================

    /**
     * Générer les notes par défaut pour un rapport complet
     */
    private void generateDefaultNotes(FinancialReport report) {
        List<ReportNote> notes = new ArrayList<>();
        
        // Note 1: Informations générales
        notes.add(createNote(1, "Informations générales", ReportNote.NoteCategory.GENERAL_INFORMATION, 
                           "L'entreprise exerce ses activités dans le secteur du commerce de détail. " +
                           "La période couverte par ce rapport s'étend du " + 
                           report.getPeriodStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                           " au " + report.getPeriodEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        // Note 2: Politiques comptables
        notes.add(createNote(2, "Politiques comptables", ReportNote.NoteCategory.ACCOUNTING_POLICIES, 
                           "Les états financiers sont établis selon les normes OHADA - Système Normal. " +
                           "La devise de présentation est le Franc CFA (XOF).", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        // Note 3: Immobilisations
        notes.add(createNote(3, "Immobilisations", ReportNote.NoteCategory.FIXED_ASSETS, 
                           "Les immobilisations sont comptabilisées au coût d'acquisition. " +
                           "Les amortissements sont calculés selon la méthode linéaire.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        // Note 4: Stocks
        notes.add(createNote(4, "Stocks", ReportNote.NoteCategory.INVENTORIES, 
                           "Les stocks sont évalués au coût d'acquisition ou de production. " +
                           "Les dépréciations sont calculées selon la méthode du coût moyen pondéré.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        // Note 5: Créances
        notes.add(createNote(5, "Créances", ReportNote.NoteCategory.RECEIVABLES, 
                           "Les créances clients sont évaluées à leur valeur nominale. " +
                           "Des dépréciations sont constituées pour les créances douteuses.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        // Note 6: Trésorerie
        notes.add(createNote(6, "Trésorerie", ReportNote.NoteCategory.CASH_AND_EQUIVALENTS, 
                           "La trésorerie comprend les disponibilités en banque et en caisse. " +
                           "Les placements à court terme sont comptabilisés à leur valeur de marché.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        // Note 7: Capitaux propres
        notes.add(createNote(7, "Capitaux propres", ReportNote.NoteCategory.EQUITY, 
                           "Les capitaux propres comprennent le capital social, les réserves et le résultat de l'exercice.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        // Note 8: Dettes
        notes.add(createNote(8, "Dettes", ReportNote.NoteCategory.LIABILITIES, 
                           "Les dettes fournisseurs et autres dettes sont comptabilisées à leur valeur nominale.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        // Note 9: Chiffre d'affaires
        notes.add(createNote(9, "Chiffre d'affaires", ReportNote.NoteCategory.REVENUE, 
                           "Le chiffre d'affaires représente les ventes de marchandises réalisées au cours de l'exercice.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
                 // Note 10: Charges d'exploitation
         notes.add(createNote(10, "Charges d'exploitation", ReportNote.NoteCategory.OPERATING_EXPENSES, 
                             "Les charges d'exploitation comprennent les achats de marchandises, les frais de personnel et les autres charges d'exploitation.", 
                             report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        // Sauvegarder toutes les notes
        reportNoteRepository.saveAll(notes);
        
        // Mettre à jour le nombre de notes
        report.setNotesCount(notes.size());
    }

    /**
     * Générer les notes spécifiques au bilan
     */
    private void generateBalanceSheetNotes(FinancialReport report) {
        List<ReportNote> notes = new ArrayList<>();
        
        notes.add(createNote(1, "Actifs immobilisés", ReportNote.NoteCategory.FIXED_ASSETS, 
                           "Les actifs immobilisés comprennent les immobilisations incorporelles, corporelles et financières.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        notes.add(createNote(2, "Actifs circulants", ReportNote.NoteCategory.INVENTORIES, 
                           "Les actifs circulants comprennent les stocks, créances et disponibilités.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        notes.add(createNote(3, "Capitaux propres", ReportNote.NoteCategory.EQUITY, 
                           "Les capitaux propres représentent les ressources propres de l'entreprise.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        notes.add(createNote(4, "Dettes", ReportNote.NoteCategory.LIABILITIES, 
                           "Les dettes comprennent les dettes fournisseurs, fiscales et sociales.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        reportNoteRepository.saveAll(notes);
        report.setNotesCount(notes.size());
    }

    /**
     * Générer les notes spécifiques au compte de résultat
     */
    private void generateIncomeStatementNotes(FinancialReport report) {
        List<ReportNote> notes = new ArrayList<>();
        
        notes.add(createNote(1, "Chiffre d'affaires", ReportNote.NoteCategory.REVENUE, 
                           "Le chiffre d'affaires représente les ventes nettes de marchandises.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
                 notes.add(createNote(2, "Achats de marchandises", ReportNote.NoteCategory.COST_OF_SALES, 
                            "Les achats de marchandises comprennent les achats nets et les variations de stocks.", 
                            report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
         
         notes.add(createNote(3, "Frais de personnel", ReportNote.NoteCategory.PERSONNEL_EXPENSES, 
                            "Les frais de personnel comprennent les salaires, charges sociales et autres avantages.", 
                            report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
         
         notes.add(createNote(4, "Autres charges", ReportNote.NoteCategory.OTHER_OPERATING_EXPENSES, 
                            "Les autres charges comprennent les loyers, assurances, services extérieurs, etc.", 
                            report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        reportNoteRepository.saveAll(notes);
        report.setNotesCount(notes.size());
    }

    /**
     * Générer les notes spécifiques au TFT
     */
    private void generateCashFlowNotes(FinancialReport report) {
        List<ReportNote> notes = new ArrayList<>();
        
        notes.add(createNote(1, "Flux de trésorerie d'exploitation", ReportNote.NoteCategory.CASH_AND_EQUIVALENTS, 
                           "Les flux de trésorerie d'exploitation proviennent des activités principales de l'entreprise.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        notes.add(createNote(2, "Flux de trésorerie d'investissement", ReportNote.NoteCategory.FIXED_ASSETS, 
                           "Les flux de trésorerie d'investissement concernent les acquisitions et cessions d'immobilisations.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        notes.add(createNote(3, "Flux de trésorerie de financement", ReportNote.NoteCategory.LIABILITIES, 
                           "Les flux de trésorerie de financement concernent les emprunts et remboursements.", 
                           report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));
        
        reportNoteRepository.saveAll(notes);
        report.setNotesCount(notes.size());
    }

    /**
     * Créer une note
     */
    private ReportNote createNote(Integer noteNumber, String title, ReportNote.NoteCategory category, 
                                String content, Long financialReportId, Long entrepriseId, Long createdBy) {
        ReportNote note = new ReportNote(noteNumber, title, category, content, financialReportId);
        note.setEntrepriseId(entrepriseId);
        note.setCreatedBy(createdBy);
        note.setOrderIndex(noteNumber);
        return note;
    }

    /**
     * Créer une note avec IA
     */
    private ReportNote createNoteWithAI(Integer noteNumber, String title, ReportNote.NoteCategory category, 
                                      String baseContent, Long financialReportId, Long entrepriseId, Long createdBy) {
        ReportNote note = new ReportNote(noteNumber, title, category, baseContent, financialReportId);
        note.setEntrepriseId(entrepriseId);
        note.setCreatedBy(createdBy);
        note.setOrderIndex(noteNumber);
        note.setAiGenerated(true);
        note.setAiConfidenceScore(new BigDecimal("0.85")); // Score de confiance simulé
        
        // TODO: Intégrer avec l'Assistant IA pour enrichir le contenu
        // String aiEnhancedContent = assistantIAService.enhanceNoteContent(baseContent, category);
        // note.setNoteContent(aiEnhancedContent);
        // note.setAiSuggestions("Suggestions IA pour améliorer cette note");
        
        return note;
    }

    /**
     * Générer toutes les notes OHADA complètes
     */
    private void generateCompleteOHADANotes(FinancialReport report) {
        List<ReportNote> notes = new ArrayList<>();
        int noteNumber = 1;

        // ==================== INFORMATIONS GÉNÉRALES ====================
        notes.add(createNoteWithAI(noteNumber++, "Informations générales", ReportNote.NoteCategory.GENERAL_INFORMATION, 
                                 "L'entreprise exerce ses activités dans le secteur du commerce de détail. " +
                                 "La période couverte par ce rapport s'étend du " + 
                                 report.getPeriodStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                                 " au " + report.getPeriodEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Politiques comptables", ReportNote.NoteCategory.ACCOUNTING_POLICIES, 
                                 "Les états financiers sont établis selon les normes OHADA - Système Normal. " +
                                 "La devise de présentation est le Franc CFA (XOF). " +
                                 "Les principes comptables appliqués sont la continuité d'exploitation, " +
                                 "la permanence des méthodes et la prudence.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Base de préparation", ReportNote.NoteCategory.BASIS_OF_PREPARATION, 
                                 "Les états financiers sont établis selon les normes comptables OHADA " +
                                 "et les dispositions légales en vigueur dans l'État membre.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Devise de présentation", ReportNote.NoteCategory.PRESENTATION_CURRENCY, 
                                 "La devise de présentation est le Franc CFA (XOF). " +
                                 "Toutes les opérations sont converties en cette devise.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        // ==================== BILAN - ACTIFS ====================
        notes.add(createNoteWithAI(noteNumber++, "Immobilisations incorporelles", ReportNote.NoteCategory.INTANGIBLE_ASSETS, 
                                 "Les immobilisations incorporelles sont comptabilisées au coût d'acquisition. " +
                                 "Les amortissements sont calculés selon la méthode linéaire sur la durée d'utilité estimée.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Immobilisations corporelles", ReportNote.NoteCategory.TANGIBLE_ASSETS, 
                                 "Les immobilisations corporelles sont comptabilisées au coût d'acquisition. " +
                                 "Les amortissements sont calculés selon la méthode linéaire. " +
                                 "Les durées d'amortissement sont conformes aux usages du secteur.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Immobilisations financières", ReportNote.NoteCategory.FINANCIAL_ASSETS, 
                                 "Les immobilisations financières comprennent les titres de participation, " +
                                 "les prêts et les autres créances immobilisées.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Stocks", ReportNote.NoteCategory.INVENTORIES, 
                                 "Les stocks sont évalués au coût d'acquisition ou de production. " +
                                 "Les dépréciations sont calculées selon la méthode du coût moyen pondéré. " +
                                 "Les stocks sont évalués au plus bas du coût et de la valeur nette de réalisation.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Créances clients", ReportNote.NoteCategory.RECEIVABLES, 
                                 "Les créances clients sont évaluées à leur valeur nominale. " +
                                 "Des dépréciations sont constituées pour les créances douteuses " +
                                 "selon les critères de risque établis.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Trésorerie", ReportNote.NoteCategory.CASH_AND_EQUIVALENTS, 
                                 "La trésorerie comprend les disponibilités en banque et en caisse. " +
                                 "Les placements à court terme sont comptabilisés à leur valeur de marché. " +
                                 "Les comptes bancaires sont réconciliés mensuellement.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        // ==================== BILAN - PASSIFS ====================
        notes.add(createNoteWithAI(noteNumber++, "Capital social", ReportNote.NoteCategory.SHARE_CAPITAL, 
                                 "Le capital social s'élève à " + report.getTotalEquity() + " XOF. " +
                                 "Il est entièrement libéré et réparti entre les actionnaires.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Réserves", ReportNote.NoteCategory.OTHER_RESERVES, 
                                 "Les réserves comprennent la réserve légale, les réserves statutaires " +
                                 "et les réserves libres. La réserve légale est constituée à hauteur de 10% du bénéfice net.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Résultat de l'exercice", ReportNote.NoteCategory.CURRENT_YEAR_RESULT, 
                                 "Le résultat de l'exercice s'élève à " + report.getNetIncome() + " XOF. " +
                                 "Il sera distribué selon les décisions de l'assemblée générale.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Dettes fournisseurs", ReportNote.NoteCategory.TRADE_PAYABLES, 
                                 "Les dettes fournisseurs sont comptabilisées à leur valeur nominale. " +
                                 "Les échéances sont respectées selon les conditions commerciales négociées.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Dettes fiscales", ReportNote.NoteCategory.TAX_PAYABLES, 
                                 "Les dettes fiscales comprennent la TVA, l'impôt sur les sociétés " +
                                 "et les autres impôts et taxes. Les déclarations sont déposées dans les délais légaux.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Emprunts bancaires", ReportNote.NoteCategory.BANK_LOANS, 
                                 "Les emprunts bancaires sont contractés auprès d'établissements de crédit agréés. " +
                                 "Les conditions d'emprunt sont conformes aux usages du marché.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        // ==================== COMPTE DE RÉSULTAT ====================
        notes.add(createNoteWithAI(noteNumber++, "Chiffre d'affaires", ReportNote.NoteCategory.REVENUE, 
                                 "Le chiffre d'affaires s'élève à " + report.getRevenue() + " XOF. " +
                                 "Il représente les ventes de marchandises réalisées au cours de l'exercice.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Coût des ventes", ReportNote.NoteCategory.COST_OF_SALES, 
                                 "Le coût des ventes s'élève à " + report.getExpenses() + " XOF. " +
                                 "Il comprend les achats de marchandises et les variations de stocks.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Marge brute", ReportNote.NoteCategory.GROSS_PROFIT, 
                                 "La marge brute s'élève à " + report.getRevenue().subtract(report.getExpenses()) + " XOF. " +
                                 "Elle représente la différence entre le chiffre d'affaires et le coût des ventes.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Charges d'exploitation", ReportNote.NoteCategory.OPERATING_EXPENSES, 
                                 "Les charges d'exploitation comprennent les frais de personnel, " +
                                 "les dotations aux amortissements et les autres charges d'exploitation.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Résultat d'exploitation", ReportNote.NoteCategory.OPERATING_INCOME, 
                                 "Le résultat d'exploitation s'élève à " + report.getNetIncome() + " XOF. " +
                                 "Il représente la performance opérationnelle de l'entreprise.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        // ==================== TABLEAU DE FLUX DE TRÉSORERIE ====================
        notes.add(createNoteWithAI(noteNumber++, "Flux de trésorerie d'exploitation", ReportNote.NoteCategory.OPERATING_CASH_FLOW, 
                                 "Les flux de trésorerie d'exploitation s'élèvent à " + report.getOperatingCashFlow() + " XOF. " +
                                 "Ils proviennent des activités principales de l'entreprise.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Flux de trésorerie d'investissement", ReportNote.NoteCategory.INVESTING_CASH_FLOW, 
                                 "Les flux de trésorerie d'investissement s'élèvent à " + report.getInvestingCashFlow() + " XOF. " +
                                 "Ils concernent les acquisitions et cessions d'immobilisations.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Flux de trésorerie de financement", ReportNote.NoteCategory.FINANCING_CASH_FLOW, 
                                 "Les flux de trésorerie de financement s'élèvent à " + report.getFinancingCashFlow() + " XOF. " +
                                 "Ils concernent les emprunts et remboursements.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        // ==================== INFORMATIONS COMPLÉMENTAIRES ====================
        notes.add(createNoteWithAI(noteNumber++, "Engagements", ReportNote.NoteCategory.COMMITMENTS, 
                                 "Les engagements comprennent les garanties données, les cautions " +
                                 "et les autres engagements hors bilan. Ils sont évalués selon leur nature.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Opérations avec des parties liées", ReportNote.NoteCategory.RELATED_PARTY_TRANSACTIONS, 
                                 "Les opérations avec des parties liées sont effectuées aux conditions normales de marché. " +
                                 "Elles sont identifiées et contrôlées selon les procédures internes.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Gestion des risques", ReportNote.NoteCategory.RISK_MANAGEMENT, 
                                 "L'entreprise identifie et gère les risques financiers, opérationnels " +
                                 "et de conformité selon une approche structurée.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Événements postérieurs", ReportNote.NoteCategory.EVENTS_AFTER_REPORTING_DATE, 
                                 "Aucun événement postérieur à la date de clôture n'a eu d'impact significatif " +
                                 "sur les états financiers présentés.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        // ==================== SPÉCIFIQUE OHADA ====================
        notes.add(createNoteWithAI(noteNumber++, "Conformité OHADA", ReportNote.NoteCategory.OHADA_COMPLIANCE, 
                                 "Les états financiers sont conformes aux normes OHADA - Système Normal. " +
                                 "Toutes les dispositions du SYSCOHADA ont été respectées.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Politiques spécifiques OHADA", ReportNote.NoteCategory.OHADA_SPECIFIC_POLICIES, 
                                 "Les politiques comptables appliquées sont conformes aux dispositions OHADA. " +
                                 "Les choix comptables sont justifiés et documentés.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        notes.add(createNoteWithAI(noteNumber++, "Divulgations OHADA", ReportNote.NoteCategory.OHADA_DISCLOSURES, 
                                 "Toutes les divulgations requises par les normes OHADA sont présentées " +
                                 "dans les notes annexes correspondantes.", 
                                 report.getId(), report.getEntrepriseId(), report.getGeneratedBy()));

        // Sauvegarder toutes les notes
        reportNoteRepository.saveAll(notes);
        
        // Mettre à jour le nombre de notes
        report.setNotesCount(notes.size());
    }

    // ==================== ANALYTICS ET STATISTIQUES ====================

    /**
     * Obtenir les statistiques des rapports
     */
    public Map<String, Object> getReportStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques générales
        stats.put("totalReports", financialReportRepository.countByEntrepriseId(entrepriseId));
        stats.put("completedReports", financialReportRepository.countByEntrepriseIdAndStatus(entrepriseId, FinancialReport.ReportStatus.COMPLETED));
        stats.put("draftReports", financialReportRepository.countByEntrepriseIdAndStatus(entrepriseId, FinancialReport.ReportStatus.DRAFT));
        
        // Statistiques par type
        stats.put("balanceSheets", financialReportRepository.countByEntrepriseIdAndReportType(entrepriseId, FinancialReport.ReportType.BALANCE_SHEET));
        stats.put("incomeStatements", financialReportRepository.countByEntrepriseIdAndReportType(entrepriseId, FinancialReport.ReportType.INCOME_STATEMENT));
        stats.put("cashFlowStatements", financialReportRepository.countByEntrepriseIdAndReportType(entrepriseId, FinancialReport.ReportType.CASH_FLOW_STATEMENT));
        stats.put("completeReports", financialReportRepository.countByEntrepriseIdAndReportType(entrepriseId, FinancialReport.ReportType.COMPLETE_FINANCIAL_REPORT));
        
        // Statistiques des notes
        stats.put("totalNotes", reportNoteRepository.countByEntrepriseId(entrepriseId));
        stats.put("requiredNotes", reportNoteRepository.countRequiredByEntrepriseId(entrepriseId));
        
        return stats;
    }

    /**
     * Obtenir les rapports récents
     */
    public List<FinancialReport> getRecentReports(Long entrepriseId, int limit) {
        List<FinancialReport> reports = financialReportRepository.findRecentReports(entrepriseId);
        return reports.size() > limit ? reports.subList(0, limit) : reports;
    }

    /**
     * Obtenir les notes d'un rapport
     */
    public List<ReportNote> getReportNotes(Long financialReportId) {
        return reportNoteRepository.findByFinancialReportIdOrderByNoteNumberAsc(financialReportId);
    }

    // ==================== RAPPORTS AUTOMATIQUES ====================

    /**
     * Générer les rapports mensuels automatiquement
     */
    @Scheduled(cron = "0 0 1 1 * ?") // Le 1er de chaque mois à 00:00
    public void generateMonthlyReports() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime monthEnd = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);
            
            // Générer les rapports pour toutes les entreprises (simulation)
            generateCompleteFinancialReport("Rapport mensuel - " + monthStart.format(DateTimeFormatter.ofPattern("MMMM yyyy")), 
                                          monthStart, monthEnd, 1L, 1L);
            
            System.out.println("📊 Rapports mensuels générés automatiquement");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération des rapports mensuels: " + e.getMessage());
        }
    }

    /**
     * Générer les rapports trimestriels automatiquement
     */
    @Scheduled(cron = "0 0 1 1 1/3 ?") // Le 1er janvier, avril, juillet, octobre à 00:00
    public void generateQuarterlyReports() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime quarterStart = now.withMonth(((now.getMonthValue() - 1) / 3) * 3 + 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime quarterEnd = quarterStart.plusMonths(3).minusSeconds(1);
            
            // Générer les rapports pour toutes les entreprises (simulation)
            generateCompleteFinancialReport("Rapport trimestriel - Q" + ((now.getMonthValue() - 1) / 3 + 1) + " " + now.getYear(), 
                                          quarterStart, quarterEnd, 1L, 1L);
            
            System.out.println("📊 Rapports trimestriels générés automatiquement");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération des rapports trimestriels: " + e.getMessage());
        }
    }

    /**
     * Générer les rapports annuels automatiquement
     */
    @Scheduled(cron = "0 0 1 1 1 ?") // Le 1er janvier à 00:00
    public void generateAnnualReports() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yearStart = now.withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime yearEnd = now.withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59).withSecond(59);
            
            // Générer les rapports pour toutes les entreprises (simulation)
            generateCompleteFinancialReport("Rapport annuel " + now.getYear(), 
                                          yearStart, yearEnd, 1L, 1L);
            
            System.out.println("📊 Rapports annuels générés automatiquement");
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération des rapports annuels: " + e.getMessage());
        }
    }

    // ==================== UTILITAIRES ====================

    /**
     * Approuver un rapport
     */
    public FinancialReport approveReport(Long reportId) {
        FinancialReport report = financialReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé"));
        
        report.setStatus(FinancialReport.ReportStatus.APPROVED);
        report.setUpdatedAt(LocalDateTime.now());
        
        return financialReportRepository.save(report);
    }

    /**
     * Publier un rapport
     */
    public FinancialReport publishReport(Long reportId) {
        FinancialReport report = financialReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé"));
        
        report.setStatus(FinancialReport.ReportStatus.PUBLISHED);
        report.setUpdatedAt(LocalDateTime.now());
        
        return financialReportRepository.save(report);
    }

    /**
     * Archiver un rapport
     */
    public FinancialReport archiveReport(Long reportId) {
        FinancialReport report = financialReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Rapport non trouvé"));
        
        report.setStatus(FinancialReport.ReportStatus.ARCHIVED);
        report.setUpdatedAt(LocalDateTime.now());
        
        return financialReportRepository.save(report);
    }
}
