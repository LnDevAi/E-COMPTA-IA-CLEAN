package com.ecomptaia.controller;

import com.ecomptaia.entity.ThirdParty;
import com.ecomptaia.repository.ThirdPartyRepository;
import com.ecomptaia.service.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/third-parties")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SupplierController {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private ThirdPartyService thirdPartyService;

    /**
     * Récupérer tous les fournisseurs
     */
    @GetMapping("/suppliers")
    public ResponseEntity<?> getAllSuppliers() {
        try {
            // Utiliser le repository pour récupérer les vrais fournisseurs
            List<ThirdParty> suppliers = thirdPartyRepository.findSuppliersByCompany(1L);
            
            // Ne pas injecter de données mock; retourner simplement la liste (peut être vide)

            Map<String, Object> response = new HashMap<>();
            response.put("data", suppliers);
            response.put("total", suppliers.size());
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la récupération: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Récupérer un fournisseur par ID
     */
    @GetMapping("/suppliers/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Long id) {
        try {
            Optional<ThirdParty> supplier = thirdPartyRepository.findById(id);
            
            if (supplier.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", supplier.get());
                response.put("status", "SUCCESS");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Fournisseur non trouvé");
                error.put("status", "NOT_FOUND");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la récupération: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer un nouveau fournisseur
     */
    @PostMapping("/suppliers")
    public ResponseEntity<?> createSupplier(@RequestBody Map<String, Object> supplierData) {
        try {
            ThirdParty supplier = new ThirdParty();
            supplier.setCode((String) supplierData.get("code"));
            supplier.setAccountNumber((String) supplierData.get("accountNumber"));
            supplier.setName((String) supplierData.get("name"));
            supplier.setType("FOURNISSEUR");
            supplier.setCategory((String) supplierData.get("category"));
            supplier.setTaxIdentificationNumber((String) supplierData.get("taxIdentificationNumber"));
            supplier.setVatNumber((String) supplierData.get("vatNumber"));
            supplier.setAddress((String) supplierData.get("address"));
            supplier.setCity((String) supplierData.get("city"));
            supplier.setPostalCode((String) supplierData.get("postalCode"));
            supplier.setCountryCode((String) supplierData.get("countryCode"));
            supplier.setPhone((String) supplierData.get("phone"));
            supplier.setEmail((String) supplierData.get("email"));
            supplier.setWebsite((String) supplierData.get("website"));
            supplier.setCurrency((String) supplierData.get("currency"));
            supplier.setCreditLimit(new java.math.BigDecimal(supplierData.get("creditLimit").toString()));
            supplier.setPaymentTerms((String) supplierData.get("paymentTerms"));
            supplier.setPaymentDelay((Integer) supplierData.get("paymentDelay"));
            supplier.setCompanyId(1L);
            supplier.setAccountingStandard("OHADA");
            supplier.setIsActive(true);
            
            ThirdParty savedSupplier = thirdPartyRepository.save(supplier);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Fournisseur créé avec succès");
            response.put("data", savedSupplier);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la création: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Mettre à jour un fournisseur
     */
    @PutMapping("/suppliers/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Optional<ThirdParty> optionalSupplier = thirdPartyRepository.findById(id);
            if (optionalSupplier.isPresent()) {
                ThirdParty supplier = optionalSupplier.get();
                
                if (updates.containsKey("name")) {
                    supplier.setName((String) updates.get("name"));
                }
                if (updates.containsKey("email")) {
                    supplier.setEmail((String) updates.get("email"));
                }
                if (updates.containsKey("phone")) {
                    supplier.setPhone((String) updates.get("phone"));
                }
                if (updates.containsKey("address")) {
                    supplier.setAddress((String) updates.get("address"));
                }
                if (updates.containsKey("creditLimit")) {
                    supplier.setCreditLimit(new java.math.BigDecimal(updates.get("creditLimit").toString()));
                }
                if (updates.containsKey("paymentTerms")) {
                    supplier.setPaymentTerms((String) updates.get("paymentTerms"));
                }
                if (updates.containsKey("isActive")) {
                    supplier.setIsActive((Boolean) updates.get("isActive"));
                }
                
                ThirdParty updatedSupplier = thirdPartyRepository.save(supplier);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Fournisseur mis à jour avec succès");
                response.put("data", updatedSupplier);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Fournisseur non trouvé");
                error.put("status", "NOT_FOUND");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la mise à jour: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Supprimer un fournisseur
     */
    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Long id) {
        try {
            if (thirdPartyRepository.existsById(id)) {
                thirdPartyRepository.deleteById(id);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Fournisseur supprimé avec succès");
                response.put("id", id);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Fournisseur non trouvé");
                error.put("status", "NOT_FOUND");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la suppression: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Rechercher des fournisseurs
     */
    @GetMapping("/suppliers/search")
    public ResponseEntity<?> searchSuppliers(@RequestParam String searchTerm) {
        try {
            List<ThirdParty> suppliers = thirdPartyRepository.searchByName(1L, searchTerm);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", suppliers);
            response.put("total", suppliers.size());
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la recherche: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer des fournisseurs de test
     */
    private void createSampleSuppliers() { }
}
