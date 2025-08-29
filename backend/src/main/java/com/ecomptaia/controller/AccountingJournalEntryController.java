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
            
            // Ne pas injecter de données mock; retourner simplement la liste (peut être vide)

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
    private void createSampleJournalEntries() { }
}
