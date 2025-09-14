package com.ecomptaia.service;

import com.ecomptaia.entity.DocumentVersion;
import com.ecomptaia.repository.DocumentVersionRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentVersioningService {

    private final DocumentVersionRepository versionRepository;

    public DocumentVersioningService(DocumentVersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    // Création simple d'une version (MINIMALE, sans Lombok)
    public DocumentVersion createVersion(Long documentId, String versionType, Long createdBy) {
        DocumentVersion v = new DocumentVersion();
        // v.setDocumentId(documentId);
        // v.setVersionType(DocumentVersion.VersionType.valueOf(versionType));
        // v.setCreatedBy(createdBy);
        // v.setCreatedAt(LocalDateTime.now());
        // v.setIsCurrentVersion(Boolean.TRUE);
        return versionRepository.save(v);
    }

    public List<DocumentVersion> getDocumentVersions(Long documentId) {
        try {
            return versionRepository.findAll().stream()
                .filter(v -> documentId.equals(v.getDocumentId()))
                .sorted((v1, v2) -> v2.getVersionNumber().compareTo(v1.getVersionNumber()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public Optional<DocumentVersion> getCurrentVersion(Long documentId) {
        try {
            return versionRepository.findAll().stream()
                .filter(v -> documentId.equals(v.getDocumentId()))
                .sorted((v1, v2) -> v2.getVersionNumber().compareTo(v1.getVersionNumber()))
                .findFirst();
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    public DocumentVersion approveVersion(Long versionId, Long approvedBy, String approvalNotes) {
        DocumentVersion version = versionRepository.findById(versionId)
            .orElseThrow(() -> new RuntimeException("Version not found"));
        return versionRepository.save(version);
    }

    public DocumentVersion rejectVersion(Long versionId, Long rejectedBy, String rejectionReason) {
        DocumentVersion version = versionRepository.findById(versionId)
            .orElseThrow(() -> new RuntimeException("Version not found"));
        return versionRepository.save(version);
    }

    public List<DocumentVersion> getPendingApprovalVersions() {
        try {
            return versionRepository.findAll().stream()
                .filter(v -> DocumentVersion.ApprovalStatus.PENDING.equals(v.getApprovalStatus()))
                .sorted((v1, v2) -> v1.getCreatedAt().compareTo(v2.getCreatedAt()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<DocumentVersion> getApprovedVersions() {
        try {
            return versionRepository.findAll().stream()
                .filter(v -> DocumentVersion.ApprovalStatus.APPROVED.equals(v.getApprovalStatus()))
                .sorted((v1, v2) -> v2.getApprovedAt().compareTo(v1.getApprovedAt()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public List<DocumentVersion> getArchivedVersions() {
        try {
            return versionRepository.findAll().stream()
                .filter(v -> Boolean.TRUE.equals(v.getIsArchived()))
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public VersioningStatistics getVersioningStatistics(Long documentId) {
        return new VersioningStatistics();
    }

    public static class VersioningStatistics {
        private Long totalVersions = 0L;
        private Long majorVersions = 0L;
        private Long minorVersions = 0L;
        private Long patchVersions = 0L;
        private Long draftVersions = 0L;
        private Long archivedVersions = 0L;
        private Long approvedVersions = 0L;
        private Long pendingVersions = 0L;
        private Long totalFileSize = 0L;
        private Double averageFileSize = 0.0;
        
        // Getters et setters
        public Long getTotalVersions() { return totalVersions; }
        public void setTotalVersions(Long totalVersions) { this.totalVersions = totalVersions; }
        
        public Long getMajorVersions() { return majorVersions; }
        public void setMajorVersions(Long majorVersions) { this.majorVersions = majorVersions; }
        
        public Long getMinorVersions() { return minorVersions; }
        public void setMinorVersions(Long minorVersions) { this.minorVersions = minorVersions; }
        
        public Long getPatchVersions() { return patchVersions; }
        public void setPatchVersions(Long patchVersions) { this.patchVersions = patchVersions; }
        
        public Long getDraftVersions() { return draftVersions; }
        public void setDraftVersions(Long draftVersions) { this.draftVersions = draftVersions; }
        
        public Long getArchivedVersions() { return archivedVersions; }
        public void setArchivedVersions(Long archivedVersions) { this.archivedVersions = archivedVersions; }
        
        public Long getApprovedVersions() { return approvedVersions; }
        public void setApprovedVersions(Long approvedVersions) { this.approvedVersions = approvedVersions; }
        
        public Long getPendingVersions() { return pendingVersions; }
        public void setPendingVersions(Long pendingVersions) { this.pendingVersions = pendingVersions; }
        
        public Long getTotalFileSize() { return totalFileSize; }
        public void setTotalFileSize(Long totalFileSize) { this.totalFileSize = totalFileSize; }
        
        public Double getAverageFileSize() { return averageFileSize; }
        public void setAverageFileSize(Double averageFileSize) { this.averageFileSize = averageFileSize; }
    }
}