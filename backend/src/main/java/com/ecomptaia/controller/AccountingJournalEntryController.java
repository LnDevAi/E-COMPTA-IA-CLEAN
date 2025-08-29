package com.ecomptaia.controller;

import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.repository.JournalEntryRepository;
import com.ecomptaia.service.EcritureComptableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounting")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountingJournalEntryController {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private EcritureComptableService ecritureComptableService;

    @GetMapping("/journal-entries")
    public ResponseEntity<?> getAllJournalEntries() {
        try {
            // Utiliser le repository pour récupérer les vraies écritures
            List<JournalEntry> entries = journalEntryRepository.findAll();
            
            // Si aucune écriture en base, créer des données de test
            if (entries.isEmpty()) {
                createSampleJournalEntries();
                entries = journalEntryRepository.findAll();
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", entries);
            response.put("total", entries.size());
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
     * Récupérer une écriture par ID
     */
    @GetMapping("/journal-entries/{id}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable Long id) {
        try {
            Optional<JournalEntry> entry = journalEntryRepository.findById(id);
            
            if (entry.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", entry.get());
                response.put("status", "SUCCESS");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Écriture non trouvée");
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
     * Créer une nouvelle écriture comptable
     */
    @PostMapping("/journal-entries")
    public ResponseEntity<?> createJournalEntry(@RequestBody Map<String, Object> entryData) {
        try {
            // Créer une nouvelle écriture directement avec le repository
            JournalEntry entry = new JournalEntry();
            entry.setEntryNumber((String) entryData.get("entryNumber"));
            entry.setEntryDate(java.time.LocalDate.parse((String) entryData.get("entryDate")));
            entry.setDescription((String) entryData.get("description"));
            entry.setJournalType((String) entryData.get("journalType"));
            entry.setCurrency((String) entryData.get("currency"));
            entry.setTotalDebit(new java.math.BigDecimal(entryData.get("totalDebit").toString()));
            entry.setTotalCredit(new java.math.BigDecimal(entryData.get("totalCredit").toString()));
            entry.setCompanyId(1L);
            entry.setCountryCode((String) entryData.get("countryCode"));
            entry.setAccountingStandard((String) entryData.get("accountingStandard"));
            entry.setStatus("BROUILLON");
            
            JournalEntry savedEntry = journalEntryRepository.save(entry);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Écriture comptable créée avec succès");
            response.put("data", savedEntry);
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
     * Mettre à jour une écriture comptable
     */
    @PutMapping("/journal-entries/{id}")
    public ResponseEntity<?> updateJournalEntry(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Optional<JournalEntry> optionalEntry = journalEntryRepository.findById(id);
            if (optionalEntry.isPresent()) {
                JournalEntry entry = optionalEntry.get();
                
                if (updates.containsKey("description")) {
                    entry.setDescription((String) updates.get("description"));
                }
                if (updates.containsKey("status")) {
                    entry.setStatus((String) updates.get("status"));
                }
                if (updates.containsKey("totalDebit")) {
                    entry.setTotalDebit(new java.math.BigDecimal(updates.get("totalDebit").toString()));
                }
                if (updates.containsKey("totalCredit")) {
                    entry.setTotalCredit(new java.math.BigDecimal(updates.get("totalCredit").toString()));
                }
                
                JournalEntry updatedEntry = journalEntryRepository.save(entry);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Écriture comptable mise à jour avec succès");
                response.put("data", updatedEntry);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Écriture non trouvée");
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
     * Supprimer une écriture comptable
     */
    @DeleteMapping("/journal-entries/{id}")
    public ResponseEntity<?> deleteJournalEntry(@PathVariable Long id) {
        try {
            if (journalEntryRepository.existsById(id)) {
                journalEntryRepository.deleteById(id);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Écriture comptable supprimée avec succès");
                response.put("id", id);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Écriture non trouvée");
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
     * Valider une écriture comptable
     */
    @PostMapping("/journal-entries/{id}/validate")
    public ResponseEntity<?> validateJournalEntry(@PathVariable Long id) {
        try {
            Optional<JournalEntry> optionalEntry = journalEntryRepository.findById(id);
            if (optionalEntry.isPresent()) {
                JournalEntry entry = optionalEntry.get();
                entry.setStatus("VALIDÉ");
                entry.setValidatedAt(java.time.LocalDateTime.now());
                entry.setValidatedBy(1L); // userId par défaut
                
                JournalEntry validatedEntry = journalEntryRepository.save(entry);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Écriture comptable validée avec succès");
                response.put("data", validatedEntry);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Écriture non trouvée");
                error.put("status", "NOT_FOUND");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la validation: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer des écritures de test
     */
    private void createSampleJournalEntries() {
        try {
            // Écriture 1
            JournalEntry entry1 = new JournalEntry();
            entry1.setEntryNumber("EC-2025-001");
            entry1.setEntryDate(java.time.LocalDate.of(2025, 1, 15));
            entry1.setDescription("Vente de marchandises");
            entry1.setJournalType("VENTES");
            entry1.setCurrency("XOF");
            entry1.setTotalDebit(new java.math.BigDecimal("0"));
            entry1.setTotalCredit(new java.math.BigDecimal("150000"));
            entry1.setCompanyId(1L);
            entry1.setCountryCode("CI");
            entry1.setAccountingStandard("OHADA");
            entry1.setStatus("VALIDÉ");
            journalEntryRepository.save(entry1);

            // Écriture 2
            JournalEntry entry2 = new JournalEntry();
            entry2.setEntryNumber("EC-2025-002");
            entry2.setEntryDate(java.time.LocalDate.of(2025, 1, 15));
            entry2.setDescription("Achat de fournitures");
            entry2.setJournalType("ACHATS");
            entry2.setCurrency("XOF");
            entry2.setTotalDebit(new java.math.BigDecimal("25000"));
            entry2.setTotalCredit(new java.math.BigDecimal("0"));
            entry2.setCompanyId(1L);
            entry2.setCountryCode("CI");
            entry2.setAccountingStandard("OHADA");
            entry2.setStatus("BROUILLON");
            journalEntryRepository.save(entry2);

            // Écriture 3
            JournalEntry entry3 = new JournalEntry();
            entry3.setEntryNumber("EC-2025-003");
            entry3.setEntryDate(java.time.LocalDate.of(2025, 1, 14));
            entry3.setDescription("Paiement salaires");
            entry3.setJournalType("BANQUE");
            entry3.setCurrency("XOF");
            entry3.setTotalDebit(new java.math.BigDecimal("80000"));
            entry3.setTotalCredit(new java.math.BigDecimal("0"));
            entry3.setCompanyId(1L);
            entry3.setCountryCode("CI");
            entry3.setAccountingStandard("OHADA");
            entry3.setStatus("VALIDÉ");
            journalEntryRepository.save(entry3);
        } catch (Exception e) {
            // Ignorer les erreurs lors de la création des données de test
        }
    }
}
