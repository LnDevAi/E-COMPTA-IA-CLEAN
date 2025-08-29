package com.ecomptaia.controller;

import com.ecomptaia.entity.Leave;
import com.ecomptaia.repository.LeaveRepository;
import com.ecomptaia.service.HumanResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/hr/leaves")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LeaveController {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private HumanResourceService humanResourceService;

    /**
     * Récupérer toutes les demandes de congés
     */
    @GetMapping
    public ResponseEntity<?> getAllLeaves() {
        try {
            // Utiliser le repository pour récupérer les vraies demandes de congés
            List<Leave> leaves = leaveRepository.findAll();
            
            // Ne pas injecter de données mock; retourner simplement la liste (peut être vide)

            Map<String, Object> response = new HashMap<>();
            response.put("data", leaves);
            response.put("total", leaves.size());
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
     * Récupérer une demande de congé par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeaveById(@PathVariable Long id) {
        try {
            Optional<Leave> leave = leaveRepository.findById(id);
            
            if (leave.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("data", leave.get());
                response.put("status", "SUCCESS");
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Demande de congé non trouvée");
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
     * Créer une nouvelle demande de congé
     */
    @PostMapping
    public ResponseEntity<?> createLeave(@RequestBody Map<String, Object> leaveData) {
        try {
            Leave leave = new Leave();
            leave.setLeaveCode((String) leaveData.get("leaveCode"));
            leave.setEmployeeId(Long.valueOf(leaveData.get("employeeId").toString()));
            leave.setLeaveType(Leave.LeaveType.valueOf((String) leaveData.get("leaveType")));
            leave.setStartDate(java.time.LocalDate.parse((String) leaveData.get("startDate")));
            leave.setEndDate(java.time.LocalDate.parse((String) leaveData.get("endDate")));
            leave.setTotalDays(Integer.valueOf(leaveData.get("totalDays").toString()));
            leave.setReason((String) leaveData.get("reason"));
            leave.setLeaveStatus(Leave.LeaveStatus.PENDING);
            leave.setEntrepriseId(1L);
            
            Leave savedLeave = leaveRepository.save(leave);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Demande de congé créée avec succès");
            response.put("data", savedLeave);
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
     * Mettre à jour une demande de congé
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeave(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Optional<Leave> optionalLeave = leaveRepository.findById(id);
            if (optionalLeave.isPresent()) {
                Leave leave = optionalLeave.get();
                
                if (updates.containsKey("startDate")) {
                    leave.setStartDate(java.time.LocalDate.parse((String) updates.get("startDate")));
                }
                if (updates.containsKey("endDate")) {
                    leave.setEndDate(java.time.LocalDate.parse((String) updates.get("endDate")));
                }
                if (updates.containsKey("totalDays")) {
                    leave.setTotalDays(Integer.valueOf(updates.get("totalDays").toString()));
                }
                if (updates.containsKey("reason")) {
                    leave.setReason((String) updates.get("reason"));
                }
                if (updates.containsKey("leaveStatus")) {
                    leave.setLeaveStatus(Leave.LeaveStatus.valueOf((String) updates.get("leaveStatus")));
                }
                
                Leave updatedLeave = leaveRepository.save(leave);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Demande de congé mise à jour avec succès");
                response.put("data", updatedLeave);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Demande de congé non trouvée");
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
     * Supprimer une demande de congé
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable Long id) {
        try {
            if (leaveRepository.existsById(id)) {
                leaveRepository.deleteById(id);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Demande de congé supprimée avec succès");
                response.put("id", id);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Demande de congé non trouvée");
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
     * Approuver une demande de congé
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id) {
        try {
            Optional<Leave> optionalLeave = leaveRepository.findById(id);
            if (optionalLeave.isPresent()) {
                Leave leave = optionalLeave.get();
                leave.setLeaveStatus(Leave.LeaveStatus.APPROVED);
                leave.setApprovedAt(java.time.LocalDateTime.now());
                leave.setApprovedBy(1L); // userId par défaut
                
                Leave approvedLeave = leaveRepository.save(leave);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Demande de congé approuvée avec succès");
                response.put("data", approvedLeave);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Demande de congé non trouvée");
                error.put("status", "NOT_FOUND");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors de l'approbation: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Rejeter une demande de congé
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id, @RequestBody Map<String, Object> rejectionData) {
        try {
            Optional<Leave> optionalLeave = leaveRepository.findById(id);
            if (optionalLeave.isPresent()) {
                Leave leave = optionalLeave.get();
                leave.setLeaveStatus(Leave.LeaveStatus.REJECTED);
                leave.setRejectedAt(java.time.LocalDateTime.now());
                leave.setRejectedBy(1L); // userId par défaut
                leave.setRejectionReason((String) rejectionData.get("rejectionReason"));
                
                Leave rejectedLeave = leaveRepository.save(leave);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Demande de congé rejetée");
                response.put("data", rejectedLeave);
                response.put("status", "SUCCESS");

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "Demande de congé non trouvée");
                error.put("status", "NOT_FOUND");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Erreur lors du rejet: " + e.getMessage());
            error.put("status", "ERROR");
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Créer des demandes de congé de test
     */
    private void createSampleLeaves() { }
}
