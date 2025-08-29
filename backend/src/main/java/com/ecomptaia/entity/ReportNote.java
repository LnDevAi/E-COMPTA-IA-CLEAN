package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_notes")
public class ReportNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note_number", nullable = false)
    private Integer noteNumber;

    @Column(name = "note_title", nullable = false)
    private String noteTitle;

    @Column(name = "note_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private NoteCategory noteCategory;

    @Column(name = "note_content", columnDefinition = "TEXT", nullable = false)
    private String noteContent;

    @Column(name = "financial_report_id", nullable = false)
    private Long financialReportId;

    @Column(name = "parent_note_id")
    private Long parentNoteId;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "is_required", nullable = false)
    private Boolean isRequired;

    @Column(name = "is_disclosure", nullable = false)
    private Boolean isDisclosure;

    @Column(name = "amount_value")
    private BigDecimal amountValue;

    @Column(name = "amount_currency")
    private String amountCurrency;

    @Column(name = "percentage_value")
    private BigDecimal percentageValue;

    @Column(name = "ai_generated", nullable = false)
    private Boolean aiGenerated;

    @Column(name = "ai_confidence_score")
    private BigDecimal aiConfidenceScore;

    @Column(name = "ai_suggestions", columnDefinition = "TEXT")
    private String aiSuggestions;

    @Column(name = "manual_override")
    private Boolean manualOverride;

    @Column(name = "override_reason")
    private String overrideReason;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUMS ====================

    public enum NoteCategory {
        // ==================== INFORMATIONS GÉNÉRALES ====================
        GENERAL_INFORMATION("Informations générales"),
        ACCOUNTING_POLICIES("Politiques comptables"),
        BASIS_OF_PREPARATION("Base de préparation"),
        PRESENTATION_CURRENCY("Devise de présentation"),
        FUNCTIONAL_CURRENCY("Devise fonctionnelle"),
        CONSOLIDATION_PRINCIPLES("Principes de consolidation"),
        
        // ==================== BILAN - ACTIFS ====================
        FIXED_ASSETS("Immobilisations"),
        INTANGIBLE_ASSETS("Immobilisations incorporelles"),
        TANGIBLE_ASSETS("Immobilisations corporelles"),
        FINANCIAL_ASSETS("Immobilisations financières"),
        INVESTMENT_PROPERTIES("Immeubles de placement"),
        BIOLOGICAL_ASSETS("Actifs biologiques"),
        INVENTORIES("Stocks"),
        RECEIVABLES("Créances"),
        CASH_AND_EQUIVALENTS("Trésorerie"),
        PREPAYMENTS("Charges constatées d'avance"),
        DEFERRED_TAX_ASSETS("Actifs d'impôt différé"),
        OTHER_ASSETS("Autres actifs"),
        
        // ==================== BILAN - PASSIFS ====================
        EQUITY("Capitaux propres"),
        SHARE_CAPITAL("Capital social"),
        SHARE_PREMIUM("Prime d'émission"),
        REVALUATION_RESERVE("Réserve de réévaluation"),
        LEGAL_RESERVE("Réserve légale"),
        STATUTORY_RESERVE("Réserve statutaire"),
        OTHER_RESERVES("Autres réserves"),
        RETAINED_EARNINGS("Réserves libres"),
        CURRENT_YEAR_RESULT("Résultat de l'exercice"),
        NON_CONTROLLING_INTEREST("Intérêts minoritaires"),
        
        LIABILITIES("Dettes"),
        TRADE_PAYABLES("Dettes fournisseurs"),
        TAX_PAYABLES("Dettes fiscales"),
        SOCIAL_PAYABLES("Dettes sociales"),
        BANK_LOANS("Emprunts bancaires"),
        FINANCIAL_DEBT("Dettes financières"),
        LEASE_LIABILITIES("Dettes de location"),
        PROVISIONS("Provisions"),
        DEFERRED_TAX_LIABILITIES("Passifs d'impôt différé"),
        OTHER_LIABILITIES("Autres dettes"),
        
        // ==================== COMPTE DE RÉSULTAT ====================
        REVENUE("Chiffre d'affaires"),
        COST_OF_SALES("Coût des ventes"),
        GROSS_PROFIT("Marge brute"),
        OPERATING_EXPENSES("Charges d'exploitation"),
        PERSONNEL_EXPENSES("Frais de personnel"),
        DEPRECIATION_EXPENSES("Dotations aux amortissements"),
        PROVISION_EXPENSES("Dotations aux provisions"),
        OTHER_OPERATING_EXPENSES("Autres charges d'exploitation"),
        OPERATING_INCOME("Résultat d'exploitation"),
        
        FINANCIAL_INCOME("Produits financiers"),
        FINANCIAL_EXPENSES("Charges financières"),
        FINANCIAL_RESULT("Résultat financier"),
        EXTRAORDINARY_INCOME("Produits exceptionnels"),
        EXTRAORDINARY_EXPENSES("Charges exceptionnelles"),
        EXTRAORDINARY_RESULT("Résultat exceptionnel"),
        INCOME_TAX_EXPENSE("Impôts sur les bénéfices"),
        NET_INCOME("Résultat net"),
        
        // ==================== TABLEAU DE FLUX DE TRÉSORERIE ====================
        OPERATING_CASH_FLOW("Flux de trésorerie d'exploitation"),
        INVESTING_CASH_FLOW("Flux de trésorerie d'investissement"),
        FINANCING_CASH_FLOW("Flux de trésorerie de financement"),
        CASH_AND_CASH_EQUIVALENTS("Trésorerie et équivalents"),
        
        // ==================== INFORMATIONS COMPLÉMENTAIRES ====================
        COMMITMENTS("Engagements"),
        CONTINGENCIES("Éventualités"),
        RELATED_PARTY_TRANSACTIONS("Opérations avec des parties liées"),
        SEGMENT_INFORMATION("Informations sectorielles"),
        GEOGRAPHICAL_INFORMATION("Informations géographiques"),
        RISK_MANAGEMENT("Gestion des risques"),
        FINANCIAL_INSTRUMENTS("Instruments financiers"),
        FAIR_VALUE_MEASUREMENTS("Mesures de juste valeur"),
        CAPITAL_MANAGEMENT("Gestion du capital"),
        EVENTS_AFTER_REPORTING_DATE("Événements postérieurs à la date de clôture"),
        
        // ==================== SPÉCIFIQUE OHADA ====================
        OHADA_COMPLIANCE("Conformité OHADA"),
        OHADA_SPECIFIC_POLICIES("Politiques spécifiques OHADA"),
        OHADA_DISCLOSURES("Divulgations OHADA"),
        OHADA_ACCOUNTING_CHOICES("Choix comptables OHADA"),
        OHADA_MEASUREMENT_BASES("Bases de mesure OHADA"),
        OHADA_PRESENTATION("Présentation OHADA");

        private final String description;

        NoteCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public ReportNote() {
        this.createdAt = LocalDateTime.now();
        this.isRequired = false;
        this.isDisclosure = true;
        this.aiGenerated = false;
        this.manualOverride = false;
        this.orderIndex = 0;
    }

    public ReportNote(Integer noteNumber, String noteTitle, NoteCategory noteCategory, String noteContent, Long financialReportId) {
        this();
        this.noteNumber = noteNumber;
        this.noteTitle = noteTitle;
        this.noteCategory = noteCategory;
        this.noteContent = noteContent;
        this.financialReportId = financialReportId;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNoteNumber() {
        return noteNumber;
    }

    public void setNoteNumber(Integer noteNumber) {
        this.noteNumber = noteNumber;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public NoteCategory getNoteCategory() {
        return noteCategory;
    }

    public void setNoteCategory(NoteCategory noteCategory) {
        this.noteCategory = noteCategory;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public Long getFinancialReportId() {
        return financialReportId;
    }

    public void setFinancialReportId(Long financialReportId) {
        this.financialReportId = financialReportId;
    }

    public Long getParentNoteId() {
        return parentNoteId;
    }

    public void setParentNoteId(Long parentNoteId) {
        this.parentNoteId = parentNoteId;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Boolean getIsDisclosure() {
        return isDisclosure;
    }

    public void setIsDisclosure(Boolean isDisclosure) {
        this.isDisclosure = isDisclosure;
    }

    public BigDecimal getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(BigDecimal amountValue) {
        this.amountValue = amountValue;
    }

    public String getAmountCurrency() {
        return amountCurrency;
    }

    public void setAmountCurrency(String amountCurrency) {
        this.amountCurrency = amountCurrency;
    }

    public BigDecimal getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(BigDecimal percentageValue) {
        this.percentageValue = percentageValue;
    }

    public Boolean getAiGenerated() {
        return aiGenerated;
    }

    public void setAiGenerated(Boolean aiGenerated) {
        this.aiGenerated = aiGenerated;
    }

    public BigDecimal getAiConfidenceScore() {
        return aiConfidenceScore;
    }

    public void setAiConfidenceScore(BigDecimal aiConfidenceScore) {
        this.aiConfidenceScore = aiConfidenceScore;
    }

    public String getAiSuggestions() {
        return aiSuggestions;
    }

    public void setAiSuggestions(String aiSuggestions) {
        this.aiSuggestions = aiSuggestions;
    }

    public Boolean getManualOverride() {
        return manualOverride;
    }

    public void setManualOverride(Boolean manualOverride) {
        this.manualOverride = manualOverride;
    }

    public String getOverrideReason() {
        return overrideReason;
    }

    public void setOverrideReason(String overrideReason) {
        this.overrideReason = overrideReason;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
