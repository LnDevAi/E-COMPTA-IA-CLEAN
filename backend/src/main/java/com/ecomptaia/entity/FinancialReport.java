package com.ecomptaia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "financial_reports")
public class FinancialReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Column(name = "report_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(name = "period_start", nullable = false)
    private LocalDateTime periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDateTime periodEnd;

    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "total_assets")
    private BigDecimal totalAssets;

    @Column(name = "total_liabilities")
    private BigDecimal totalLiabilities;

    @Column(name = "total_equity")
    private BigDecimal totalEquity;

    @Column(name = "revenue")
    private BigDecimal revenue;

    @Column(name = "expenses")
    private BigDecimal expenses;

    @Column(name = "net_income")
    private BigDecimal netIncome;

    @Column(name = "operating_cash_flow")
    private BigDecimal operatingCashFlow;

    @Column(name = "investing_cash_flow")
    private BigDecimal investingCashFlow;

    @Column(name = "financing_cash_flow")
    private BigDecimal financingCashFlow;

    @Column(name = "net_cash_flow")
    private BigDecimal netCashFlow;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "accounting_standard", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountingStandard accountingStandard;

    @Column(name = "generated_by")
    private Long generatedBy;

    @Column(name = "entreprise_id", nullable = false)
    private Long entrepriseId;

    @Column(name = "notes_count")
    private Integer notesCount;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ==================== ENUMS ====================

    public enum ReportType {
        BALANCE_SHEET("Bilan comptable"),
        INCOME_STATEMENT("Compte de résultat"),
        CASH_FLOW_STATEMENT("Tableau de flux de trésorerie"),
        NOTES_TO_ACCOUNTS("Notes annexes"),
        COMPLETE_FINANCIAL_REPORT("Rapport financier complet"),
        MANAGEMENT_REPORT("Rapport de gestion"),
        AUDIT_REPORT("Rapport d'audit"),
        TAX_REPORT("Rapport fiscal"),
        REGULATORY_REPORT("Rapport réglementaire");

        private final String description;

        ReportType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ReportStatus {
        DRAFT("Brouillon"),
        IN_PROGRESS("En cours"),
        COMPLETED("Terminé"),
        APPROVED("Approuvé"),
        PUBLISHED("Publié"),
        ARCHIVED("Archivé");

        private final String description;

        ReportStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum AccountingStandard {
        OHADA_SN("OHADA - Système Normal"),
        OHADA_SMT("OHADA - Système Minimal de Trésorerie"),
        IFRS("IFRS"),
        GAAP("GAAP"),
        LOCAL_STANDARDS("Standards locaux");

        private final String description;

        AccountingStandard(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // ==================== CONSTRUCTEURS ====================

    public FinancialReport() {
        this.reportDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.status = ReportStatus.DRAFT;
        this.currency = "XOF";
        this.accountingStandard = AccountingStandard.OHADA_SN;
        this.notesCount = 0;
    }

    public FinancialReport(String reportName, ReportType reportType, LocalDateTime periodStart, LocalDateTime periodEnd) {
        this();
        this.reportName = reportName;
        this.reportType = reportType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    // ==================== GETTERS ET SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public BigDecimal getTotalLiabilities() {
        return totalLiabilities;
    }

    public void setTotalLiabilities(BigDecimal totalLiabilities) {
        this.totalLiabilities = totalLiabilities;
    }

    public BigDecimal getTotalEquity() {
        return totalEquity;
    }

    public void setTotalEquity(BigDecimal totalEquity) {
        this.totalEquity = totalEquity;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getExpenses() {
        return expenses;
    }

    public void setExpenses(BigDecimal expenses) {
        this.expenses = expenses;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(BigDecimal netIncome) {
        this.netIncome = netIncome;
    }

    public BigDecimal getOperatingCashFlow() {
        return operatingCashFlow;
    }

    public void setOperatingCashFlow(BigDecimal operatingCashFlow) {
        this.operatingCashFlow = operatingCashFlow;
    }

    public BigDecimal getInvestingCashFlow() {
        return investingCashFlow;
    }

    public void setInvestingCashFlow(BigDecimal investingCashFlow) {
        this.investingCashFlow = investingCashFlow;
    }

    public BigDecimal getFinancingCashFlow() {
        return financingCashFlow;
    }

    public void setFinancingCashFlow(BigDecimal financingCashFlow) {
        this.financingCashFlow = financingCashFlow;
    }

    public BigDecimal getNetCashFlow() {
        return netCashFlow;
    }

    public void setNetCashFlow(BigDecimal netCashFlow) {
        this.netCashFlow = netCashFlow;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public AccountingStandard getAccountingStandard() {
        return accountingStandard;
    }

    public void setAccountingStandard(AccountingStandard accountingStandard) {
        this.accountingStandard = accountingStandard;
    }

    public Long getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(Long generatedBy) {
        this.generatedBy = generatedBy;
    }

    public Long getEntrepriseId() {
        return entrepriseId;
    }

    public void setEntrepriseId(Long entrepriseId) {
        this.entrepriseId = entrepriseId;
    }

    public Integer getNotesCount() {
        return notesCount;
    }

    public void setNotesCount(Integer notesCount) {
        this.notesCount = notesCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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




