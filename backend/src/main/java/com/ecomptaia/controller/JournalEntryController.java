package com.ecomptaia.controller;

import com.ecomptaia.entity.JournalEntry;
import com.ecomptaia.entity.AccountEntry;
import com.ecomptaia.service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/journal-entries")
@CrossOrigin(origins = "*", maxAge = 3600)
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    /**
     * Créer une nouvelle écriture comptable
     */
    @PostMapping
    public ResponseEntity<?> createJournalEntry(@RequestBody Map<String, Object> request) {
        try {
            // Extraction des données avec vérification de type
            Object entryDataObj = request.get("entry");
            if (!(entryDataObj instanceof Map)) {
                throw new IllegalArgumentException("Le champ 'entry' doit être un objet");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> entryData = (Map<String, Object>) entryDataObj;
            
            Object accountEntriesDataObj = request.get("accountEntries");
            if (!(accountEntriesDataObj instanceof List)) {
                throw new IllegalArgumentException("Le champ 'accountEntries' doit être une liste");
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> accountEntriesData = (List<Map<String, Object>>) accountEntriesDataObj;

            // Création de l'écriture
            JournalEntry entry = new JournalEntry();
            entry.setEntryDate(LocalDate.parse((String) entryData.get("entryDate")));
            entry.setDescription((String) entryData.get("description"));
            entry.setJournalType((String) entryData.get("journalType"));
            entry.setCurrency((String) entryData.get("currency"));
            entry.setCompanyId(Long.valueOf(entryData.get("companyId").toString()));
            entry.setCountryCode((String) entryData.get("countryCode"));
            entry.setAccountingStandard((String) entryData.get("accountingStandard"));

            // Création des lignes d'écriture
            List<AccountEntry> accountEntries = accountEntriesData.stream()
                .map(data -> {
                    AccountEntry accountEntry = new AccountEntry();
                    accountEntry.setAccountNumber((String) data.get("accountNumber"));
                    accountEntry.setAccountName((String) data.get("accountName"));
                    accountEntry.setAccountType((String) data.get("accountType"));
                    accountEntry.setAmount(new java.math.BigDecimal(data.get("amount").toString()));
                    accountEntry.setDescription((String) data.get("description"));
                    accountEntry.setCompanyId(Long.valueOf(entryData.get("companyId").toString()));
                    accountEntry.setCountryCode((String) entryData.get("countryCode"));
                    accountEntry.setAccountingStandard((String) entryData.get("accountingStandard"));
                    return accountEntry;
                })
                .toList();

            JournalEntry createdEntry = journalEntryService.createJournalEntry(entry, accountEntries);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Écriture créée avec succès");
            response.put("entry", createdEntry);
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
     * Valider une écriture
     */
    @PutMapping("/{entryId}/validate")
    public ResponseEntity<?> validateEntry(@PathVariable Long entryId, @RequestBody Map<String, Object> request) {
        try {
            Long validatedBy = Long.valueOf(request.get("validatedBy").toString());
            JournalEntry validatedEntry = journalEntryService.validateEntry(entryId, validatedBy);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Écriture validée avec succès");
            response.put("entry", validatedEntry);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de la validation: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Annuler une écriture
     */
    @PutMapping("/{entryId}/cancel")
    public ResponseEntity<?> cancelEntry(@PathVariable Long entryId) {
        try {
            JournalEntry cancelledEntry = journalEntryService.cancelEntry(entryId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Écriture annulée avec succès");
            response.put("entry", cancelledEntry);
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de l'annulation: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Récupérer les écritures d'une entreprise
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getEntriesByCompany(@PathVariable Long companyId) {
        try {
            List<JournalEntry> entries = journalEntryService.getEntriesByCompany(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("entries", entries);
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
     * Récupérer les écritures par statut
     */
    @GetMapping("/company/{companyId}/status/{status}")
    public ResponseEntity<?> getEntriesByStatus(@PathVariable Long companyId, @PathVariable String status) {
        try {
            List<JournalEntry> entries = journalEntryService.getEntriesByStatus(companyId, status);

            Map<String, Object> response = new HashMap<>();
            response.put("entries", entries);
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
     * Récupérer les écritures par période
     */
    @GetMapping("/company/{companyId}/period")
    public ResponseEntity<?> getEntriesByDateRange(
            @PathVariable Long companyId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<JournalEntry> entries = journalEntryService.getEntriesByDateRange(companyId, start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("entries", entries);
            response.put("total", entries.size());
            response.put("startDate", startDate);
            response.put("endDate", endDate);
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
     * Récupérer les écritures comptabilisées
     */
    @GetMapping("/company/{companyId}/posted")
    public ResponseEntity<?> getPostedEntries(@PathVariable Long companyId) {
        try {
            List<JournalEntry> entries = journalEntryService.getPostedEntries(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("entries", entries);
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
     * Récupérer les brouillons
     */
    @GetMapping("/company/{companyId}/drafts")
    public ResponseEntity<?> getDraftEntries(@PathVariable Long companyId) {
        try {
            List<JournalEntry> entries = journalEntryService.getDraftEntries(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("entries", entries);
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
     * Statistiques des écritures
     */
    @GetMapping("/company/{companyId}/statistics")
    public ResponseEntity<?> getEntryStatistics(@PathVariable Long companyId) {
        try {
            Map<String, Object> stats = journalEntryService.getEntryStatistics(companyId);

            Map<String, Object> response = new HashMap<>();
            response.put("statistics", stats);
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
     * Récupérer toutes les écritures (pour les tests)
     */
    @GetMapping
    public ResponseEntity<?> getAllEntries() {
        try {
            List<JournalEntry> entries = journalEntryService.getAllEntries();

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
     * Endpoint spécifique pour /api/accounting/journal-entries
     */
    @GetMapping("/accounting/journal-entries")
    public ResponseEntity<?> getAccountingJournalEntries() {
        try {
            List<JournalEntry> entries = journalEntryService.getAllEntries();

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
}


