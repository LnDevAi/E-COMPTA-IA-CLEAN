package com.ecomptaia.service;

import com.ecomptaia.entity.DocumentWorkflow;
import com.ecomptaia.entity.DocumentApproval;
import com.ecomptaia.entity.GedDocument;
import com.ecomptaia.repository.DocumentWorkflowRepository;
import com.ecomptaia.repository.DocumentApprovalRepository;
import com.ecomptaia.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkflowManagementService {
    
    private final DocumentWorkflowRepository workflowRepository;
    private final DocumentApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    
    public WorkflowManagementService(DocumentWorkflowRepository workflowRepository,
                                     DocumentApprovalRepository approvalRepository,
                                     UserRepository userRepository) {
        this.workflowRepository = workflowRepository;
        this.approvalRepository = approvalRepository;
        this.userRepository = userRepository;
    }
    
    public List<DocumentWorkflow> getWorkflowsByCompany(Long companyId) {
        return workflowRepository.findByCompanyIdOrderByWorkflowNameAsc(companyId);
    }
    
    public Optional<DocumentWorkflow> getWorkflowById(Long workflowId) {
        return workflowRepository.findById(workflowId);
    }
    
    public List<DocumentWorkflow> getActiveWorkflowsByDocumentType(Long companyId, GedDocument.DocumentType documentType) {
        List<DocumentWorkflow> all = workflowRepository.findByCompanyIdAndDocumentTypeOrderByWorkflowNameAsc(companyId, documentType);
        List<DocumentWorkflow> active = new ArrayList<>();
        for (DocumentWorkflow w : all) {
            if (Boolean.TRUE.equals(w.getIsActive())) {
                active.add(w);
            }
        }
        return active;
    }
    
    public DocumentWorkflow createWorkflow(DocumentWorkflow workflow) {
        workflow.setId(null);
        return workflowRepository.save(workflow);
    }
    
    public DocumentWorkflow updateWorkflow(Long workflowId, DocumentWorkflow workflowData) {
        DocumentWorkflow existing = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow non trouvé"));
        existing.setWorkflowName(workflowData.getWorkflowName());
        existing.setDescription(workflowData.getDescription());
        existing.setDocumentType(workflowData.getDocumentType());
        existing.setIsActive(workflowData.getIsActive());
        existing.setRequiresApproval(workflowData.getRequiresApproval());
        existing.setApprovalLevels(workflowData.getApprovalLevels());
        existing.setAutoApprove(workflowData.getAutoApprove());
        existing.setAutoArchive(workflowData.getAutoArchive());
        existing.setArchiveDays(workflowData.getArchiveDays());
        existing.setRetentionYears(workflowData.getRetentionYears());
        existing.setCountryCode(workflowData.getCountryCode());
        existing.setAccountingStandard(workflowData.getAccountingStandard());
        return workflowRepository.save(existing);
    }
    
    public void deleteWorkflow(Long workflowId) {
        if (!workflowRepository.existsById(workflowId)) {
            throw new RuntimeException("Workflow non trouvé");
        }
        workflowRepository.deleteById(workflowId);
    }
    
    public List<DocumentApproval> getPendingApprovalsForUser(Long userId, Long companyId) {
        return approvalRepository.findPendingApprovalsForApprover(companyId, userId);
    }
    
    public List<DocumentApproval> getDocumentApprovals(Long documentId) {
        return approvalRepository.findByDocumentIdOrderByApprovalLevelAsc(documentId);
    }
    
    public DocumentApproval approveDocument(Long approvalId, String comments, Long approverId) {
        DocumentApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approbation non trouvée"));
        approval.setApprovalStatus(DocumentApproval.ApprovalStatus.APPROVED);
        approval.setComments(comments);
        approval.setApprovalDate(java.time.LocalDateTime.now());
        return approvalRepository.save(approval);
    }
    
    public DocumentApproval rejectDocument(Long approvalId, String comments, Long approverId) {
        DocumentApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approbation non trouvée"));
        approval.setApprovalStatus(DocumentApproval.ApprovalStatus.REJECTED);
        approval.setComments(comments);
        approval.setApprovalDate(java.time.LocalDateTime.now());
        return approvalRepository.save(approval);
    }
    
    public DocumentApproval cancelApproval(Long approvalId, String comments, Long approverId) {
        DocumentApproval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approbation non trouvée"));
        approval.setApprovalStatus(DocumentApproval.ApprovalStatus.CANCELLED);
        approval.setComments(comments);
        approval.setApprovalDate(java.time.LocalDateTime.now());
        return approvalRepository.save(approval);
    }
    
    public void initializeDocumentWorkflow(Long documentId, Long companyId) {
        // No-op minimal initialization for now
    }
    
    public Map<String, Object> getWorkflowStatistics(Long companyId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("workflows", workflowRepository.findByCompanyIdOrderByCreatedAtDesc(companyId).size());
        stats.put("approvals", approvalRepository.findByCompanyIdOrderByCreatedAtDesc(companyId).size());
        return stats;
    }
    
    public List<Map<String, Object>> getDocumentsPendingApproval(Long companyId) {
        List<DocumentApproval> approvals = approvalRepository.findByCompanyIdAndApprovalStatusOrderByDueDateAsc(companyId, DocumentApproval.ApprovalStatus.PENDING);
        List<Map<String, Object>> result = new ArrayList<>();
        for (DocumentApproval da : approvals) {
            Map<String, Object> row = new HashMap<>();
            row.put("documentId", da.getDocumentId());
            row.put("approvalLevel", da.getApprovalLevel());
            row.put("approverId", da.getApproverId());
            row.put("dueDate", da.getDueDate());
            result.add(row);
        }
        return result;
    }
}
