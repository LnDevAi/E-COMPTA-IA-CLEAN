package com.ecomptaia.service;

import com.ecomptaia.entity.ThirdParty;
import com.ecomptaia.repository.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class ThirdPartyService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private ThirdPartyNumberingService numberingService;

    /**
     * Créer un nouveau tiers
     */
    public ThirdParty createThirdParty(ThirdParty thirdParty) {
        // Validation des données obligatoires
        validateThirdParty(thirdParty);
        
        // Validation du code unique
        if (thirdPartyRepository.findByCodeAndCompanyIdAndIsActiveTrue(thirdParty.getCode(), thirdParty.getCompanyId()).isPresent()) {
            throw new RuntimeException("Le code tiers existe déjà pour cette entreprise");
        }
        
        // Génération automatique du numéro de compte si non fourni
        if (thirdParty.getAccountNumber() == null || thirdParty.getAccountNumber().trim().isEmpty()) {
            String accountNumber = numberingService.generateThirdPartyAccountNumber(thirdParty.getCompanyId(), thirdParty.getType());
            thirdParty.setAccountNumber(accountNumber);
        } else {
            // Validation du numéro de compte fourni
            if (!numberingService.validateThirdPartyAccountNumber(thirdParty.getAccountNumber(), thirdParty.getType())) {
                throw new RuntimeException("Numéro de compte tiers invalide pour le type: " + thirdParty.getType());
            }
            
            // Vérification de l'unicité du numéro de compte
            if (!numberingService.isAccountNumberAvailable(thirdParty.getCompanyId(), thirdParty.getAccountNumber())) {
                throw new RuntimeException("Le numéro de compte tiers existe déjà: " + thirdParty.getAccountNumber());
            }
        }
        
        thirdParty.setIsActive(true);
        thirdParty.setCreatedAt(LocalDateTime.now());
        thirdParty.setUpdatedAt(LocalDateTime.now());
        
        return thirdPartyRepository.save(thirdParty);
    }

    /**
     * Mettre à jour un tiers
     */
    public ThirdParty updateThirdParty(Long id, ThirdParty thirdPartyDetails) {
        Optional<ThirdParty> thirdPartyOpt = thirdPartyRepository.findById(id);
        if (thirdPartyOpt.isPresent()) {
            ThirdParty thirdParty = thirdPartyOpt.get();
            
            thirdParty.setName(thirdPartyDetails.getName());
            thirdParty.setType(thirdPartyDetails.getType());
            thirdParty.setCategory(thirdPartyDetails.getCategory());
            thirdParty.setAddress(thirdPartyDetails.getAddress());
            thirdParty.setCity(thirdPartyDetails.getCity());
            thirdParty.setPostalCode(thirdPartyDetails.getPostalCode());
            thirdParty.setPhone(thirdPartyDetails.getPhone());
            thirdParty.setEmail(thirdPartyDetails.getEmail());
            thirdParty.setWebsite(thirdPartyDetails.getWebsite());
            thirdParty.setCurrency(thirdPartyDetails.getCurrency());
            thirdParty.setCreditLimit(thirdPartyDetails.getCreditLimit());
            thirdParty.setPaymentTerms(thirdPartyDetails.getPaymentTerms());
            thirdParty.setPaymentDelay(thirdPartyDetails.getPaymentDelay());
            thirdParty.setNotes(thirdPartyDetails.getNotes());
            thirdParty.setUpdatedAt(LocalDateTime.now());
            
            return thirdPartyRepository.save(thirdParty);
        }
        throw new RuntimeException("Tiers non trouvé");
    }

    /**
     * Désactiver un tiers
     */
    public ThirdParty deactivateThirdParty(Long id) {
        Optional<ThirdParty> thirdPartyOpt = thirdPartyRepository.findById(id);
        if (thirdPartyOpt.isPresent()) {
            ThirdParty thirdParty = thirdPartyOpt.get();
            thirdParty.setIsActive(false);
            thirdParty.setUpdatedAt(LocalDateTime.now());
            return thirdPartyRepository.save(thirdParty);
        }
        throw new RuntimeException("Tiers non trouvé");
    }

    /**
     * Récupérer tous les tiers d'une entreprise
     */
    public List<ThirdParty> getThirdPartiesByCompany(Long companyId) {
        return thirdPartyRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    /**
     * Récupérer les clients d'une entreprise
     */
    public List<ThirdParty> getClientsByCompany(Long companyId) {
        return thirdPartyRepository.findClientsByCompany(companyId);
    }

    /**
     * Récupérer les fournisseurs d'une entreprise
     */
    public List<ThirdParty> getSuppliersByCompany(Long companyId) {
        return thirdPartyRepository.findSuppliersByCompany(companyId);
    }

    /**
     * Récupérer les débiteurs (clients avec solde positif)
     */
    public List<ThirdParty> getDebtorsByCompany(Long companyId) {
        return thirdPartyRepository.findDebtorsByCompany(companyId);
    }

    /**
     * Récupérer les créanciers (fournisseurs avec solde négatif)
     */
    public List<ThirdParty> getCreditorsByCompany(Long companyId) {
        return thirdPartyRepository.findCreditorsByCompany(companyId);
    }

    /**
     * Rechercher par nom
     */
    public List<ThirdParty> searchByName(Long companyId, String searchTerm) {
        return thirdPartyRepository.searchByName(companyId, searchTerm);
    }

    /**
     * Rechercher par code
     */
    public List<ThirdParty> searchByCode(Long companyId, String searchTerm) {
        return thirdPartyRepository.searchByCode(companyId, searchTerm);
    }

    /**
     * Trouver par numéro fiscal
     */
    public Optional<ThirdParty> findByTaxId(Long companyId, String taxId) {
        return thirdPartyRepository.findByTaxId(companyId, taxId);
    }

    /**
     * Trouver par numéro de TVA
     */
    public Optional<ThirdParty> findByVatNumber(Long companyId, String vatNumber) {
        return thirdPartyRepository.findByVatNumber(companyId, vatNumber);
    }

    /**
     * Mettre à jour le solde d'un tiers
     */
    public ThirdParty updateBalance(Long id, BigDecimal newBalance) {
        Optional<ThirdParty> thirdPartyOpt = thirdPartyRepository.findById(id);
        if (thirdPartyOpt.isPresent()) {
            ThirdParty thirdParty = thirdPartyOpt.get();
            thirdParty.setCurrentBalance(newBalance);
            thirdParty.setUpdatedAt(LocalDateTime.now());
            return thirdPartyRepository.save(thirdParty);
        }
        throw new RuntimeException("Tiers non trouvé");
    }

    /**
     * Statistiques des tiers
     */
    public Map<String, Object> getThirdPartyStatistics(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalClients", thirdPartyRepository.countClientsByCompany(companyId));
        stats.put("totalSuppliers", thirdPartyRepository.countSuppliersByCompany(companyId));
        stats.put("totalDebtors", thirdPartyRepository.findDebtorsByCompany(companyId).size());
        stats.put("totalCreditors", thirdPartyRepository.findCreditorsByCompany(companyId).size());
        
        // Calcul du total des créances
        BigDecimal totalReceivables = thirdPartyRepository.findDebtorsByCompany(companyId)
            .stream()
            .map(ThirdParty::getCurrentBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalReceivables", totalReceivables);
        
        // Calcul du total des dettes
        BigDecimal totalPayables = thirdPartyRepository.findCreditorsByCompany(companyId)
            .stream()
            .map(ThirdParty::getCurrentBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalPayables", totalPayables.abs());
        
        return stats;
    }

    /**
     * Validation des données tiers
     */
    private void validateThirdParty(ThirdParty thirdParty) {
        if (thirdParty.getCode() == null || thirdParty.getCode().trim().isEmpty()) {
            throw new RuntimeException("Le code tiers est obligatoire");
        }
        
        if (thirdParty.getName() == null || thirdParty.getName().trim().isEmpty()) {
            throw new RuntimeException("Le nom du tiers est obligatoire");
        }
        
        if (thirdParty.getType() == null || thirdParty.getType().trim().isEmpty()) {
            throw new RuntimeException("Le type de tiers est obligatoire");
        }
        
        if (thirdParty.getCountryCode() == null || thirdParty.getCountryCode().trim().isEmpty()) {
            throw new RuntimeException("Le code pays est obligatoire");
        }
    }

    /**
     * Récupérer tous les tiers (pour les tests)
     */
    public List<ThirdParty> getAllThirdParties() {
        return thirdPartyRepository.findAll();
    }
}

