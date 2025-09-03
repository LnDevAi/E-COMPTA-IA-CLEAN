package com.ecomptaia.controller;

import com.ecomptaia.entity.ThirdParty;
import com.ecomptaia.repository.ThirdPartyRepository;
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

    /**
     * Récupérer tous les clients
     */
    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers() {
        try {
            // Utiliser le repository pour récupérer les vrais clients
            List<ThirdParty> customers = thirdPartyRepository.findClientsByCompany(1L);
            
            // Si aucun client en base, créer des données de test
            if (customers.isEmpty()) {
                createSampleCustomers();
                customers = thirdPartyRepository.findClientsByCompany(1L);
            }

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
    private void createSampleCustomers() {
        try {
            // Client 1
            ThirdParty customer1 = new ThirdParty();
            customer1.setCode("CLI001");
            customer1.setAccountNumber("4110001");
            customer1.setName("ABC Corporation");
            customer1.setType("CLIENT");
            customer1.setCategory("Entreprise");
            customer1.setEmail("contact@abccorp.com");
            customer1.setPhone("+225 27 22 49 50 00");
            customer1.setAddress("123 Avenue des Banques, Abidjan");
            customer1.setCity("Abidjan");
            customer1.setCountryCode("CI");
            customer1.setCurrency("XOF");
            customer1.setCreditLimit(new java.math.BigDecimal("5000000"));
            customer1.setPaymentTerms("30 jours");
            customer1.setPaymentDelay(30);
            customer1.setCompanyId(1L);
            customer1.setAccountingStandard("OHADA");
            customer1.setIsActive(true);
            thirdPartyRepository.save(customer1);

            // Client 2
            ThirdParty customer2 = new ThirdParty();
            customer2.setCode("CLI002");
            customer2.setAccountNumber("4110002");
            customer2.setName("XYZ Industries");
            customer2.setType("CLIENT");
            customer2.setCategory("Entreprise");
            customer2.setEmail("info@xyzindustries.com");
            customer2.setPhone("+225 27 22 49 50 01");
            customer2.setAddress("456 Boulevard du Commerce, Abidjan");
            customer2.setCity("Abidjan");
            customer2.setCountryCode("CI");
            customer2.setCurrency("XOF");
            customer2.setCreditLimit(new java.math.BigDecimal("3000000"));
            customer2.setPaymentTerms("45 jours");
            customer2.setPaymentDelay(45);
            customer2.setCompanyId(1L);
            customer2.setAccountingStandard("OHADA");
            customer2.setIsActive(true);
            thirdPartyRepository.save(customer2);

            // Client 3
            ThirdParty customer3 = new ThirdParty();
            customer3.setCode("CLI003");
            customer3.setAccountNumber("4110003");
            customer3.setName("Tech Solutions SARL");
            customer3.setType("CLIENT");
            customer3.setCategory("Entreprise");
            customer3.setEmail("contact@techsolutions.ci");
            customer3.setPhone("+225 27 22 49 50 02");
            customer3.setAddress("789 Rue des Entrepreneurs, Abidjan");
            customer3.setCity("Abidjan");
            customer3.setCountryCode("CI");
            customer3.setCurrency("XOF");
            customer3.setCreditLimit(new java.math.BigDecimal("8000000"));
            customer3.setPaymentTerms("30 jours");
            customer3.setPaymentDelay(30);
            customer3.setCompanyId(1L);
            customer3.setAccountingStandard("OHADA");
            customer3.setIsActive(true);
            thirdPartyRepository.save(customer3);
        } catch (Exception e) {
            // Ignorer les erreurs lors de la création des données de test
        }
    }
}
