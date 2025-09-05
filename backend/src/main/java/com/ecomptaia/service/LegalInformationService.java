package com.ecomptaia.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service de gestion des informations légales et documents d'entreprise
 */
@Service
public class LegalInformationService {

    /**
     * Collecte des informations légales d'entreprise
     */
    public Map<String, Object> collectLegalInformation(
            String companyName, String legalForm, String registrationNumber,
            String taxId, String address, String city, String postalCode,
            String country, String phone, String email, String website,
            LocalDate incorporationDate, String businessActivity,
            String capital, String currency, String directors,
            String shareholders, String bankAccount) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Validation des données obligatoires
            List<String> errors = new ArrayList<>();
            if (companyName == null || companyName.trim().isEmpty()) {
                errors.add("Le nom de l'entreprise est obligatoire");
            }
            if (legalForm == null || legalForm.trim().isEmpty()) {
                errors.add("La forme juridique est obligatoire");
            }
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                errors.add("Le numéro d'immatriculation est obligatoire");
            }
            if (country == null || country.trim().isEmpty()) {
                errors.add("Le pays est obligatoire");
            }
            
            if (!errors.isEmpty()) {
                result.put("errors", errors);
                result.put("status", "ERROR");
                return result;
            }
            
            // Génération d'un ID unique
            Long legalInfoId = System.currentTimeMillis();
            
            // Création des informations légales
            Map<String, Object> legalInfo = new HashMap<>();
            legalInfo.put("legalInfoId", legalInfoId);
            legalInfo.put("companyName", companyName);
            legalInfo.put("legalForm", legalForm);
            legalInfo.put("registrationNumber", registrationNumber);
            legalInfo.put("taxId", taxId);
            legalInfo.put("address", address);
            legalInfo.put("city", city);
            legalInfo.put("postalCode", postalCode);
            legalInfo.put("country", country);
            legalInfo.put("phone", phone);
            legalInfo.put("email", email);
            legalInfo.put("website", website);
            legalInfo.put("incorporationDate", incorporationDate);
            legalInfo.put("businessActivity", businessActivity);
            legalInfo.put("capital", capital);
            legalInfo.put("currency", currency);
            legalInfo.put("directors", directors);
            legalInfo.put("shareholders", shareholders);
            legalInfo.put("bankAccount", bankAccount);
            legalInfo.put("createdAt", LocalDateTime.now());
            legalInfo.put("status", "PENDING_VALIDATION");
            
            // Validation de conformité par pays
            Map<String, Object> complianceCheck = validateCompliance(country, legalForm, capital);
            legalInfo.put("complianceCheck", complianceCheck);
            
            result.put("legalInfo", legalInfo);
            result.put("message", "Informations légales collectées avec succès");
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de la collecte: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Upload de documents de création
     */
    public Map<String, Object> uploadCreationDocuments(
            Long legalInfoId, MultipartFile[] files, String[] documentTypes) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (files == null || files.length == 0) {
                result.put("error", "Aucun fichier fourni");
                result.put("status", "ERROR");
                return result;
            }
            
            List<Map<String, Object>> uploadedDocuments = new ArrayList<>();
            
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                String documentType = i < documentTypes.length ? documentTypes[i] : "AUTRE";
                
                if (file.isEmpty()) {
                    continue;
                }
                
                // Validation du type de fichier
                if (!isValidFileType(file.getOriginalFilename())) {
                    result.put("error", "Type de fichier non supporté: " + file.getOriginalFilename());
                    result.put("status", "ERROR");
                    return result;
                }
                
                // Génération d'un ID unique pour le document
                String documentId = generateDocumentId();
                
                // Informations du document
                Map<String, Object> document = new HashMap<>();
                document.put("documentId", documentId);
                document.put("legalInfoId", legalInfoId);
                document.put("documentType", documentType);
                document.put("originalName", file.getOriginalFilename());
                document.put("fileSize", file.getSize());
                document.put("contentType", file.getContentType());
                document.put("uploadDate", LocalDateTime.now());
                document.put("status", "UPLOADED");
                
                // Simulation du stockage
                String storagePath = "/legal-documents/" + legalInfoId + "/" + documentId + "_" + file.getOriginalFilename();
                document.put("storagePath", storagePath);
                
                uploadedDocuments.add(document);
            }
            
            result.put("uploadedDocuments", uploadedDocuments);
            result.put("totalDocuments", uploadedDocuments.size());
            result.put("message", "Documents uploadés avec succès");
            result.put("status", "SUCCESS");
            
        } catch (Exception e) {
            result.put("error", "Erreur lors de l'upload: " + e.getMessage());
            result.put("status", "ERROR");
        }
        
        return result;
    }

    /**
     * Validation de conformité par pays
     */
    public Map<String, Object> validateCompliance(String country, String legalForm, String capital) {
        Map<String, Object> compliance = new HashMap<>();
        
        try {
            List<String> warnings = new ArrayList<>();
            List<String> requirements = new ArrayList<>();
            
            // Règles spécifiques par pays
            switch (country.toUpperCase()) {
                case "FRANCE":
                    requirements.add("Extrait Kbis obligatoire");
                    requirements.add("Statuts de l'entreprise");
                    requirements.add("Justificatif d'identité des dirigeants");
                    if (capital != null && capital.contains("€")) {
                        requirements.add("Capital minimum: 1€ pour SARL, 37.000€ pour SA");
                    }
                    break;
                    
                case "CAMEROUN":
                    requirements.add("Extrait du registre du commerce");
                    requirements.add("Statuts de l'entreprise");
                    requirements.add("Certificat de conformité fiscale");
                    requirements.add("Capital minimum: 1.000.000 FCFA");
                    break;
                    
                case "SENEGAL":
                    requirements.add("Extrait du registre du commerce");
                    requirements.add("Statuts de l'entreprise");
                    requirements.add("Certificat de conformité fiscale");
                    requirements.add("Capital minimum: 100.000 FCFA");
                    break;
                    
                case "COTE D'IVOIRE":
                    requirements.add("Extrait du registre du commerce");
                    requirements.add("Statuts de l'entreprise");
                    requirements.add("Certificat de conformité fiscale");
                    requirements.add("Capital minimum: 1.000.000 FCFA");
                    break;
                    
                default:
                    requirements.add("Documents d'identité des dirigeants");
                    requirements.add("Statuts de l'entreprise");
                    requirements.add("Justificatif d'adresse");
            }
            
            // Vérifications générales
            if (legalForm == null || legalForm.trim().isEmpty()) {
                warnings.add("Forme juridique non spécifiée");
            }
            
            if (capital == null || capital.trim().isEmpty()) {
                warnings.add("Capital social non spécifié");
            }
            
            compliance.put("country", country);
            compliance.put("requirements", requirements);
            compliance.put("warnings", warnings);
            compliance.put("isCompliant", warnings.isEmpty());
            compliance.put("validationDate", LocalDateTime.now());
            
        } catch (Exception e) {
            compliance.put("error", "Erreur lors de la validation: " + e.getMessage());
        }
        
        return compliance;
    }

    /**
     * Récupération des types de documents requis par pays
     */
    public List<Map<String, Object>> getRequiredDocuments(String country) {
        List<Map<String, Object>> documents = new ArrayList<>();
        
        try {
            switch (country.toUpperCase()) {
                case "FRANCE":
                    addDocument(documents, "EXTRACT_KBIS", "Extrait Kbis", "Obligatoire", "Document officiel d'immatriculation");
                    addDocument(documents, "STATUTES", "Statuts de l'entreprise", "Obligatoire", "Statuts constitutifs");
                    addDocument(documents, "DIRECTOR_ID", "Justificatif d'identité des dirigeants", "Obligatoire", "Pièce d'identité");
                    addDocument(documents, "ADDRESS_PROOF", "Justificatif d'adresse", "Obligatoire", "Facture ou quittance");
                    addDocument(documents, "BANK_CERTIFICATE", "Attestation bancaire", "Recommandé", "Certificat de dépôt de capital");
                    break;
                    
                case "CAMEROUN":
                    addDocument(documents, "COMMERCE_REGISTER", "Extrait du registre du commerce", "Obligatoire", "Extrait officiel");
                    addDocument(documents, "STATUTES", "Statuts de l'entreprise", "Obligatoire", "Statuts constitutifs");
                    addDocument(documents, "TAX_CERTIFICATE", "Certificat de conformité fiscale", "Obligatoire", "Certificat fiscal");
                    addDocument(documents, "DIRECTOR_ID", "Justificatif d'identité des dirigeants", "Obligatoire", "Pièce d'identité");
                    addDocument(documents, "ADDRESS_PROOF", "Justificatif d'adresse", "Obligatoire", "Facture ou quittance");
                    break;
                    
                case "SENEGAL":
                    addDocument(documents, "COMMERCE_REGISTER", "Extrait du registre du commerce", "Obligatoire", "Extrait officiel");
                    addDocument(documents, "STATUTES", "Statuts de l'entreprise", "Obligatoire", "Statuts constitutifs");
                    addDocument(documents, "TAX_CERTIFICATE", "Certificat de conformité fiscale", "Obligatoire", "Certificat fiscal");
                    addDocument(documents, "DIRECTOR_ID", "Justificatif d'identité des dirigeants", "Obligatoire", "Pièce d'identité");
                    break;
                    
                default:
                    addDocument(documents, "BUSINESS_LICENSE", "Licence commerciale", "Obligatoire", "Licence d'exploitation");
                    addDocument(documents, "STATUTES", "Statuts de l'entreprise", "Obligatoire", "Statuts constitutifs");
                    addDocument(documents, "DIRECTOR_ID", "Justificatif d'identité des dirigeants", "Obligatoire", "Pièce d'identité");
                    addDocument(documents, "ADDRESS_PROOF", "Justificatif d'adresse", "Obligatoire", "Facture ou quittance");
            }
            
        } catch (Exception e) {
            // Gestion d'erreur
        }
        
        return documents;
    }

    /**
     * Récupération des formes juridiques par pays
     */
    public List<Map<String, Object>> getLegalForms(String country) {
        List<Map<String, Object>> legalForms = new ArrayList<>();
        
        try {
            switch (country.toUpperCase()) {
                case "FRANCE":
                    addLegalForm(legalForms, "SARL", "Société à responsabilité limitée", "Capital minimum: 1€");
                    addLegalForm(legalForms, "SA", "Société anonyme", "Capital minimum: 37.000€");
                    addLegalForm(legalForms, "SAS", "Société par actions simplifiée", "Capital minimum: 1€");
                    addLegalForm(legalForms, "EURL", "Entreprise unipersonnelle à responsabilité limitée", "Capital minimum: 1€");
                    addLegalForm(legalForms, "EI", "Entreprise individuelle", "Pas de capital minimum");
                    break;
                    
                case "CAMEROUN":
                    addLegalForm(legalForms, "SARL", "Société à responsabilité limitée", "Capital minimum: 1.000.000 FCFA");
                    addLegalForm(legalForms, "SA", "Société anonyme", "Capital minimum: 10.000.000 FCFA");
                    addLegalForm(legalForms, "SNC", "Société en nom collectif", "Pas de capital minimum");
                    addLegalForm(legalForms, "EI", "Entreprise individuelle", "Pas de capital minimum");
                    break;
                    
                case "SENEGAL":
                    addLegalForm(legalForms, "SARL", "Société à responsabilité limitée", "Capital minimum: 100.000 FCFA");
                    addLegalForm(legalForms, "SA", "Société anonyme", "Capital minimum: 10.000.000 FCFA");
                    addLegalForm(legalForms, "SNC", "Société en nom collectif", "Pas de capital minimum");
                    addLegalForm(legalForms, "EI", "Entreprise individuelle", "Pas de capital minimum");
                    break;
                    
                default:
                    addLegalForm(legalForms, "LLC", "Limited Liability Company", "Capital variable");
                    addLegalForm(legalForms, "CORP", "Corporation", "Capital variable");
                    addLegalForm(legalForms, "PARTNERSHIP", "Partnership", "Pas de capital minimum");
                    addLegalForm(legalForms, "SOLE_PROPRIETORSHIP", "Sole Proprietorship", "Pas de capital minimum");
            }
            
        } catch (Exception e) {
            // Gestion d'erreur
        }
        
        return legalForms;
    }

    // Méthodes utilitaires privées
    private boolean isValidFileType(String fileName) {
        if (fileName == null) return false;
        
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".pdf") || 
               lowerFileName.endsWith(".jpg") || 
               lowerFileName.endsWith(".jpeg") || 
               lowerFileName.endsWith(".png") || 
               lowerFileName.endsWith(".doc") || 
               lowerFileName.endsWith(".docx");
    }

    private String generateDocumentId() {
        return "DOC-" + System.currentTimeMillis();
    }

    private void addDocument(List<Map<String, Object>> documents, String code, String name, String requirement, String description) {
        Map<String, Object> document = new HashMap<>();
        document.put("code", code);
        document.put("name", name);
        document.put("requirement", requirement);
        document.put("description", description);
        documents.add(document);
    }

    private void addLegalForm(List<Map<String, Object>> legalForms, String code, String name, String requirements) {
        Map<String, Object> legalForm = new HashMap<>();
        legalForm.put("code", code);
        legalForm.put("name", name);
        legalForm.put("requirements", requirements);
        legalForms.add(legalForm);
    }
}








