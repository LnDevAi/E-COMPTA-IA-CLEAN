ackage com.ecomptaia.controller;

import com.ecomptaia.entity.DocumentVersion;
import com.ecomptaia.service.DocumentVersioningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/document-versioning")
@CrossOrigin(origins = "*")
public class DocumentVersioningController {

    private final DocumentVersioningService versioningService;

    public DocumentVersioningController(DocumentVersioningService versioningService) {
        this.versioningService = versioningService;
    }

    @PostMapping("/documents/{documentId}/versions")
    public ResponseEntity<DocumentVersion> createVersion(@PathVariable Long documentId,
                                                         @RequestParam String versionType,
                                                         @RequestParam Long createdBy) {
        DocumentVersion v = versioningService.createVersion(documentId, versionType, createdBy);
        return ResponseEntity.ok(v);
    }

    @GetMapping("/documents/{documentId}/versions")
    public ResponseEntity<List<DocumentVersion>> getVersions(@PathVariable Long documentId) {
        List<DocumentVersion> versions = versioningService.getDocumentVersions(documentId);
        return ResponseEntity.ok(versions);
    }

    @PutMapping("/versions/{versionId}/approve")
    public ResponseEntity<Map<String, Object>> approveVersion(
            @PathVariable Long versionId,
            @RequestParam Long approvedBy,
            @RequestParam String approvalNotes) {
        
        try {
            DocumentVersion version = versioningService.approveVersion(versionId, approvedBy, approvalNotes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Version approuvée avec succès");
            response.put("version", version);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur lors de l'approbation: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/versions/{versionId}/reject")
    public ResponseEntity<Map<String, Object>> rejectVersion(
            @PathVariable Long versionId,
            @RequestParam Long rejectedBy,
            @RequestParam String rejectionReason) {
        
        try {
            DocumentVersion version = versioningService.rejectVersion(versionId, rejectedBy, rejectionReason);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Version rejetée avec succès");
            response.put("version", version);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", "Erreur lors du rejet: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/versions/pending-approval")
    public ResponseEntity<List<DocumentVersion>> getPendingApprovalVersions() {
        List<DocumentVersion> versions = versioningService.getPendingApprovalVersions();
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/versions/approved")
    public ResponseEntity<List<DocumentVersion>> getApprovedVersions() {
        List<DocumentVersion> versions = versioningService.getApprovedVersions();
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/versions/archived")
    public ResponseEntity<List<DocumentVersion>> getArchivedVersions() {
        List<DocumentVersion> versions = versioningService.getArchivedVersions();
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/documents/{documentId}/statistics")
    public ResponseEntity<DocumentVersioningService.VersioningStatistics> getVersioningStatistics(@PathVariable Long documentId) {
        DocumentVersioningService.VersioningStatistics statistics = versioningService.getVersioningStatistics(documentId);
        return ResponseEntity.ok(statistics);
    }
}