package com.ecomptaia.sycebnl.controller;

import com.ecomptaia.sycebnl.entity.SycebnlOrganization;
import com.ecomptaia.sycebnl.service.SycebnlOrganizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur REST pour le module SYCEBNL-OHADA
 * Gère les organisations à but non lucratif et la conformité OHADA
 */
@RestController
@RequestMapping("/api/sycebnl")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('USER')")
public class SycebnlController {
    
    private final SycebnlOrganizationService organizationService;
    
    public SycebnlController(SycebnlOrganizationService organizationService) {
        this.organizationService = organizationService;
    }
    
    // === GESTION DES ORGANISATIONS ===
    
    /**
     * Créer une nouvelle organisation SYCEBNL
     */
    @PostMapping("/organizations")
    public ResponseEntity<SycebnlOrganization> createOrganization(
            @RequestParam Long companyId,
            @RequestBody CreateOrganizationRequest request) {
        SycebnlOrganization organization = new SycebnlOrganization();
        SycebnlOrganization createdOrganization = organizationService.createOrganization(companyId, organization);
        return ResponseEntity.ok(createdOrganization);
    }
    
    /**
     * Récupérer une organisation par ID
     */
    @GetMapping("/organizations/{organizationId}")
    public ResponseEntity<SycebnlOrganization> getOrganization(@PathVariable Long organizationId) {
        SycebnlOrganization organization = organizationService.getOrganization(organizationId);
        if (organization != null) {
            return ResponseEntity.ok(organization);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Récupérer les organisations avec pagination
     */
    @GetMapping("/organizations")
    public ResponseEntity<Page<SycebnlOrganization>> getOrganizations(
            @RequestParam Long companyId,
            Pageable pageable) {
        Page<SycebnlOrganization> organizations = organizationService.getOrganizations(companyId, pageable);
        return ResponseEntity.ok(organizations);
    }
    
    /**
     * Mettre à jour une organisation
     */
    @PutMapping("/organizations/{organizationId}")
    public ResponseEntity<SycebnlOrganization> updateOrganization(
            @PathVariable Long organizationId,
            @RequestBody SycebnlOrganization updatedOrganization) {
        SycebnlOrganization organization = organizationService.updateOrganization(organizationId, updatedOrganization);
        return ResponseEntity.ok(organization);
    }
    
    /**
     * Vérifier la conformité OHADA
     */
    @GetMapping("/organizations/{organizationId}/compliance")
    public ResponseEntity<SycebnlOrganizationService.ComplianceStatus> checkCompliance(@PathVariable Long organizationId) {
        SycebnlOrganizationService.ComplianceStatus status = organizationService.checkOhadaCompliance(organizationId);
        return ResponseEntity.ok(status);
    }
    
    /**
     * Programmer un audit de conformité
     */
    @PostMapping("/organizations/{organizationId}/audit")
    public ResponseEntity<String> scheduleAudit(
            @PathVariable Long organizationId,
            @RequestParam LocalDate auditDate) {
        organizationService.scheduleNextComplianceAudit(organizationId, auditDate);
        return ResponseEntity.ok("Audit programmé avec succès");
    }
    
    /**
     * Obtenir les organisations nécessitant un audit
     */
    @GetMapping("/organizations/audit-due")
    public ResponseEntity<List<SycebnlOrganization>> getOrganizationsDueForAudit(@RequestParam Long companyId) {
        List<SycebnlOrganization> organizations = organizationService.getOrganizationsDueForAudit();
        return ResponseEntity.ok(organizations);
    }
    
    /**
     * Obtenir les organisations non conformes
     */
    @GetMapping("/organizations/non-compliant")
    public ResponseEntity<List<SycebnlOrganization>> getNonCompliantOrganizations(@RequestParam Long companyId) {
        List<SycebnlOrganization> organizations = organizationService.getNonCompliantOrganizations();
        return ResponseEntity.ok(organizations);
    }
    
    /**
     * Rechercher des organisations
     */
    @GetMapping("/organizations/search")
    public ResponseEntity<List<SycebnlOrganization>> searchOrganizations(
            @RequestParam Long companyId,
            @RequestParam String searchTerm) {
        List<SycebnlOrganization> organizations = organizationService.searchOrganizations(searchTerm);
        return ResponseEntity.ok(organizations);
    }
    
    /**
     * Obtenir les statistiques des organisations
     */
    @GetMapping("/organizations/{organizationId}/statistics")
    public ResponseEntity<SycebnlOrganizationService.OrganizationStatistics> getOrganizationStatistics(@PathVariable Long organizationId) {
        SycebnlOrganizationService.OrganizationStatistics statistics = organizationService.getOrganizationStatistics(organizationId);
        return ResponseEntity.ok(statistics);
    }
    
    // === DTOs ===
    
    public static class CreateOrganizationRequest {
        private String organizationName;
        private String legalForm;
        private String registrationNumber;
        private String taxIdentificationNumber;
        private String organizationType;
        private String fiscalYearStart;
        private String fiscalYearEnd;
        private String baseCurrency;
        private String reportingCurrency;
        private String annualRevenue;
        private String employeeCount;
        private String totalAssets;
        private String legalAddress;
        private String headquartersAddress;
        private String phoneNumber;
        private String email;
        private String website;
        private String missionStatement;
        private String programAreas;
        private String geographicScope;
        private String beneficiaryCount;
        private String volunteerCount;
        
        public String getOrganizationName() { return organizationName; }
        public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
        public String getLegalForm() { return legalForm; }
        public void setLegalForm(String legalForm) { this.legalForm = legalForm; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public String getTaxIdentificationNumber() { return taxIdentificationNumber; }
        public void setTaxIdentificationNumber(String taxIdentificationNumber) { this.taxIdentificationNumber = taxIdentificationNumber; }
        public String getOrganizationType() { return organizationType; }
        public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
        public String getFiscalYearStart() { return fiscalYearStart; }
        public void setFiscalYearStart(String fiscalYearStart) { this.fiscalYearStart = fiscalYearStart; }
        public String getFiscalYearEnd() { return fiscalYearEnd; }
        public void setFiscalYearEnd(String fiscalYearEnd) { this.fiscalYearEnd = fiscalYearEnd; }
        public String getBaseCurrency() { return baseCurrency; }
        public void setBaseCurrency(String baseCurrency) { this.baseCurrency = baseCurrency; }
        public String getReportingCurrency() { return reportingCurrency; }
        public void setReportingCurrency(String reportingCurrency) { this.reportingCurrency = reportingCurrency; }
        public String getAnnualRevenue() { return annualRevenue; }
        public void setAnnualRevenue(String annualRevenue) { this.annualRevenue = annualRevenue; }
        public String getEmployeeCount() { return employeeCount; }
        public void setEmployeeCount(String employeeCount) { this.employeeCount = employeeCount; }
        public String getTotalAssets() { return totalAssets; }
        public void setTotalAssets(String totalAssets) { this.totalAssets = totalAssets; }
        public String getLegalAddress() { return legalAddress; }
        public void setLegalAddress(String legalAddress) { this.legalAddress = legalAddress; }
        public String getHeadquartersAddress() { return headquartersAddress; }
        public void setHeadquartersAddress(String headquartersAddress) { this.headquartersAddress = headquartersAddress; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getWebsite() { return website; }
        public void setWebsite(String website) { this.website = website; }
        public String getMissionStatement() { return missionStatement; }
        public void setMissionStatement(String missionStatement) { this.missionStatement = missionStatement; }
        public String getProgramAreas() { return programAreas; }
        public void setProgramAreas(String programAreas) { this.programAreas = programAreas; }
        public String getGeographicScope() { return geographicScope; }
        public void setGeographicScope(String geographicScope) { this.geographicScope = geographicScope; }
        public String getBeneficiaryCount() { return beneficiaryCount; }
        public void setBeneficiaryCount(String beneficiaryCount) { this.beneficiaryCount = beneficiaryCount; }
        public String getVolunteerCount() { return volunteerCount; }
        public void setVolunteerCount(String volunteerCount) { this.volunteerCount = volunteerCount; }
    }
}

