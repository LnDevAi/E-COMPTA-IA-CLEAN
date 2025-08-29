package com.ecomptaia.controller;

import com.ecomptaia.service.LegalInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Map;

/**
 * Contrôleur pour la gestion des informations légales et documents d'entreprise
 */
@RestController
@RequestMapping("/api/legal-information")
@CrossOrigin(origins = "*")
public class LegalInformationController {

    @Autowired
    private LegalInformationService legalInformationService;

    /**
     * Collecte des informations légales d'entreprise
     */
    @PostMapping("/collect-legal-info")
    public ResponseEntity<Map<String, Object>> collectLegalInformation(
            @RequestParam String companyName,
            @RequestParam String legalForm,
            @RequestParam String registrationNumber,
            @RequestParam(required = false) String taxId,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String postalCode,
            @RequestParam String country,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String incorporationDate,
            @RequestParam(required = false) String businessActivity,
            @RequestParam(required = false) String capital,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String directors,
            @RequestParam(required = false) String shareholders,
            @RequestParam(required = false) String bankAccount) {
        
        LocalDate incorporationDateObj = null;
        if (incorporationDate != null && !incorporationDate.trim().isEmpty()) {
            incorporationDateObj = LocalDate.parse(incorporationDate);
        }
        
        Map<String, Object> result = legalInformationService.collectLegalInformation(
                companyName, legalForm, registrationNumber, taxId, address, city, postalCode,
                country, phone, email, website, incorporationDateObj, businessActivity,
                capital, currency, directors, shareholders, bankAccount);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Upload de documents de création
     */
    @PostMapping("/upload-documents")
    public ResponseEntity<Map<String, Object>> uploadCreationDocuments(
            @RequestParam Long legalInfoId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("documentTypes") String[] documentTypes) {
        
        Map<String, Object> result = legalInformationService.uploadCreationDocuments(
                legalInfoId, files, documentTypes);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Récupération des types de documents requis par pays
     */
    @GetMapping("/required-documents/{country}")
    public ResponseEntity<Map<String, Object>> getRequiredDocuments(@PathVariable String country) {
        Map<String, Object> result = Map.of(
                "country", country,
                "documents", legalInformationService.getRequiredDocuments(country),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Récupération des formes juridiques par pays
     */
    @GetMapping("/legal-forms/{country}")
    public ResponseEntity<Map<String, Object>> getLegalForms(@PathVariable String country) {
        Map<String, Object> result = Map.of(
                "country", country,
                "legalForms", legalInformationService.getLegalForms(country),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Validation de conformité par pays
     */
    @PostMapping("/validate-compliance")
    public ResponseEntity<Map<String, Object>> validateCompliance(
            @RequestParam String country,
            @RequestParam(required = false) String legalForm,
            @RequestParam(required = false) String capital) {
        
        Map<String, Object> result = legalInformationService.validateCompliance(country, legalForm, capital);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test pour la collecte d'informations légales
     */
    @GetMapping("/test-collect-legal-info")
    public ResponseEntity<Map<String, Object>> testCollectLegalInformation() {
        Map<String, Object> result = legalInformationService.collectLegalInformation(
                "Test Company SARL",
                "SARL",
                "RC123456789",
                "FR12345678901",
                "123 Rue de la Paix",
                "Paris",
                "75001",
                "FRANCE",
                "+33123456789",
                "contact@testcompany.com",
                "www.testcompany.com",
                LocalDate.of(2020, 1, 1),
                "Services informatiques",
                "10000€",
                "EUR",
                "Jean Dupont",
                "Jean Dupont (100%)",
                "FR7630001007941234567890185"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test pour les documents requis
     */
    @GetMapping("/test-required-documents")
    public ResponseEntity<Map<String, Object>> testRequiredDocuments() {
        Map<String, Object> result = Map.of(
                "france", legalInformationService.getRequiredDocuments("FRANCE"),
                "cameroun", legalInformationService.getRequiredDocuments("CAMEROUN"),
                "senegal", legalInformationService.getRequiredDocuments("SENEGAL"),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test pour les formes juridiques
     */
    @GetMapping("/test-legal-forms")
    public ResponseEntity<Map<String, Object>> testLegalForms() {
        Map<String, Object> result = Map.of(
                "france", legalInformationService.getLegalForms("FRANCE"),
                "cameroun", legalInformationService.getLegalForms("CAMEROUN"),
                "senegal", legalInformationService.getLegalForms("SENEGAL"),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint de test pour la validation de conformité
     */
    @GetMapping("/test-compliance")
    public ResponseEntity<Map<String, Object>> testCompliance() {
        Map<String, Object> result = Map.of(
                "france", legalInformationService.validateCompliance("FRANCE", "SARL", "10000€"),
                "cameroun", legalInformationService.validateCompliance("CAMEROUN", "SARL", "2000000 FCFA"),
                "senegal", legalInformationService.validateCompliance("SENEGAL", "SARL", "500000 FCFA"),
                "status", "SUCCESS"
        );
        
        return ResponseEntity.ok(result);
    }
}




