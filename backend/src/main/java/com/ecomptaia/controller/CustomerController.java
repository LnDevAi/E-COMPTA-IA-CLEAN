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
public class CustomerController {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private ThirdPartyService thirdPartyService;

    /**
     * Récupérer tous les clients
     */
    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers() {
        try {
            // Utiliser le repository pour récupérer les vrais clients
            List<ThirdParty> customers = thirdPartyRepository.findClientsByCompany(1L);
            
            // Ne pas injecter de données mock; retourner simplement la liste (peut être vide)

            Map<String, Object> response = new HashMap<>();
            response.put("data", customers);
            response.put("total", customers.size());
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
     * Récupérer un client par ID
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        try {
            Optional<ThirdParty> customer = thirdPartyRepository.findById(id);
            
            if (customer.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", customer.get());
                response.put("status", "SUCCESS");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Client non trouvé");
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
     * Créer un nouveau client
     */
    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(@RequestBody Map<String, Object> customerData) {
        try {
            ThirdParty customer = new ThirdParty();
            customer.setCode((String) customerData.get("code"));
            customer.setAccountNumber((String) customerData.get("accountNumber"));
            customer.setName((String) customerData.get("name"));
            customer.setType("CLIENT");
            customer.setCategory((String) customerData.get("category"));
            customer.setTaxIdentificationNumber((String) customerData.get("taxIdentificationNumber"));
            customer.setVatNumber((String) customerData.get("vatNumber"));
            customer.setAddress((String) customerData.get("address"));
            customer.setCity((String) customerData.get("city"));
            customer.setPostalCode((String) customerData.get("postalCode"));
            customer.setCountryCode((String) customerData.get("countryCode"));
            customer.setPhone((String) customerData.get("phone"));
            customer.setEmail((String) customerData.get("email"));
            customer.setWebsite((String) customerData.get("website"));
            customer.setCurrency((String) customerData.get("currency"));
            customer.setCreditLimit(new java.math.BigDecimal(customerData.get("creditLimit").toString()));
            customer.setPaymentTerms((String) customerData.get("paymentTerms"));
            customer.setPaymentDelay((Integer) customerData.get("paymentDelay"));
            customer.setCompanyId(1L);
            customer.setAccountingStandard("OHADA");
            customer.setIsActive(true);
            
            ThirdParty savedCustomer = thirdPartyRepository.save(customer);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Client créé avec succès");
            response.put("data", savedCustomer);
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
     * Mettre à jour un client
     */
    @PutMapping("/customers/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Optional<ThirdParty> optionalCustomer = thirdPartyRepository.findById(id);
            if (optionalCustomer.isPresent()) {
                ThirdParty customer = optionalCustomer.get();
                
                if (updates.containsKey("name")) {
                    customer.setName((String) updates.get("name"));
                }
                if (updates.containsKey("email")) {
                    customer.setEmail((String) updates.get("email"));
                }
                if (updates.containsKey("phone")) {
                    customer.setPhone((String) updates.get("phone"));
                }
                if (updates.containsKey("address")) {
                    customer.setAddress((String) updates.get("address"));
                }
                if (updates.containsKey("creditLimit")) {
                    customer.setCreditLimit(new java.math.BigDecimal(updates.get("creditLimit").toString()));
                }
                if (updates.containsKey("paymentTerms")) {
                    customer.setPaymentTerms((String) updates.get("paymentTerms"));
                }
                if (updates.containsKey("isActive")) {
                    customer.setIsActive((Boolean) updates.get("isActive"));
                }
                
                ThirdParty updatedCustomer = thirdPartyRepository.save(customer);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Client mis à jour avec succès");
                response.put("data", updatedCustomer);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Client non trouvé");
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
     * Supprimer un client
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            if (thirdPartyRepository.existsById(id)) {
                thirdPartyRepository.deleteById(id);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Client supprimé avec succès");
                response.put("id", id);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Client non trouvé");
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
     * Rechercher des clients
     */
    @GetMapping("/customers/search")
    public ResponseEntity<?> searchCustomers(@RequestParam String searchTerm) {
        try {
            List<ThirdParty> customers = thirdPartyRepository.searchByName(1L, searchTerm);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", customers);
            response.put("total", customers.size());
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
     * Créer des clients de test
     */
    private void createSampleCustomers() { }
}
