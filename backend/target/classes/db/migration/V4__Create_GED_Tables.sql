-- =====================================================
-- E-COMPTA-IA - SCRIPT DE CRÉATION DES TABLES GED
-- Version: 1.0
-- Date: 2024
-- Description: Création des tables pour le module GED (Gestion Électronique de Documents)
-- =====================================================

-- ==================== MODULE GED ====================

-- Table des documents GED
CREATE TABLE ged_documents (
    id BIGSERIAL PRIMARY KEY,
    document_code VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(50),
    mime_type VARCHAR(100),
    document_type VARCHAR(50) NOT NULL,
    category VARCHAR(100),
    tags TEXT,
    version INTEGER DEFAULT 1,
    is_current_version BOOLEAN DEFAULT true,
    parent_document_id BIGINT REFERENCES ged_documents(id),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    security_level VARCHAR(50) NOT NULL DEFAULT 'INTERNAL',
    expiry_date DATE,
    is_archived BOOLEAN DEFAULT false,
    archive_date DATE,
    retention_period_years INTEGER DEFAULT 7,
    module_reference VARCHAR(100),
    entity_reference_id BIGINT,
    entity_reference_type VARCHAR(100),
    ecriture_id BIGINT REFERENCES journal_entries(id),
    company_id BIGINT NOT NULL REFERENCES companies(id),
    country_code VARCHAR(3) NOT NULL,
    accounting_standard VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    checksum VARCHAR(255),
    is_encrypted BOOLEAN DEFAULT false,
    
    -- Contraintes
    CONSTRAINT chk_ged_document_type CHECK (document_type IN (
        'INVOICE', 'RECEIPT', 'CONTRACT', 'REPORT', 'POLICY', 'PROCEDURE', 
        'MANUAL', 'CERTIFICATE', 'LICENSE', 'INSURANCE', 'TAX_DOCUMENT', 
        'LEGAL_DOCUMENT', 'HR_DOCUMENT', 'ASSET_DOCUMENT', 'INVENTORY_DOCUMENT', 
        'FINANCIAL_STATEMENT', 'AUDIT_REPORT', 'COMPLIANCE_DOCUMENT', 'OTHER'
    )),
    CONSTRAINT chk_ged_document_status CHECK (status IN (
        'DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED', 'ARCHIVED', 'EXPIRED', 'DELETED'
    )),
    CONSTRAINT chk_ged_security_level CHECK (security_level IN (
        'PUBLIC', 'INTERNAL', 'CONFIDENTIAL', 'RESTRICTED', 'SECRET'
    )),
    CONSTRAINT chk_ged_file_size CHECK (file_size > 0),
    CONSTRAINT chk_ged_version CHECK (version > 0)
);

-- Table des workflows de documents
CREATE TABLE document_workflows (
    id BIGSERIAL PRIMARY KEY,
    workflow_code VARCHAR(50) UNIQUE NOT NULL,
    workflow_name VARCHAR(255) NOT NULL,
    description TEXT,
    document_type VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    requires_approval BOOLEAN DEFAULT true,
    approval_levels INTEGER DEFAULT 1,
    auto_approve BOOLEAN DEFAULT false,
    auto_archive BOOLEAN DEFAULT false,
    archive_days INTEGER DEFAULT 365,
    retention_years INTEGER DEFAULT 7,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    country_code VARCHAR(3) NOT NULL,
    accounting_standard VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    
    -- Contraintes
    CONSTRAINT chk_doc_workflow_type CHECK (document_type IN (
        'INVOICE', 'RECEIPT', 'CONTRACT', 'REPORT', 'POLICY', 'PROCEDURE', 
        'MANUAL', 'CERTIFICATE', 'LICENSE', 'INSURANCE', 'TAX_DOCUMENT', 
        'LEGAL_DOCUMENT', 'HR_DOCUMENT', 'ASSET_DOCUMENT', 'INVENTORY_DOCUMENT', 
        'FINANCIAL_STATEMENT', 'AUDIT_REPORT', 'COMPLIANCE_DOCUMENT', 'OTHER'
    )),
    CONSTRAINT chk_doc_workflow_levels CHECK (approval_levels > 0),
    CONSTRAINT chk_doc_workflow_archive_days CHECK (archive_days > 0),
    CONSTRAINT chk_doc_workflow_retention CHECK (retention_years > 0)
);

-- Table des approbations de documents
CREATE TABLE document_approvals (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES ged_documents(id) ON DELETE CASCADE,
    approval_level INTEGER NOT NULL,
    approver_id BIGINT NOT NULL REFERENCES users(id),
    approver_name VARCHAR(255),
    approval_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    approval_date TIMESTAMP,
    comments TEXT,
    is_required BOOLEAN DEFAULT true,
    due_date TIMESTAMP,
    is_overdue BOOLEAN DEFAULT false,
    reminder_sent BOOLEAN DEFAULT false,
    reminder_count INTEGER DEFAULT 0,
    last_reminder_date TIMESTAMP,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    country_code VARCHAR(3) NOT NULL,
    accounting_standard VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT chk_doc_approval_status CHECK (approval_status IN (
        'PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'DELEGATED', 'EXPIRED'
    )),
    CONSTRAINT chk_doc_approval_level CHECK (approval_level > 0),
    CONSTRAINT chk_doc_approval_reminder_count CHECK (reminder_count >= 0)
);

-- Table des étapes de workflow
CREATE TABLE workflow_steps (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL REFERENCES document_workflows(id) ON DELETE CASCADE,
    step_name VARCHAR(255) NOT NULL,
    step_description TEXT,
    step_order INTEGER NOT NULL,
    approver_role VARCHAR(100) NOT NULL,
    approver_id BIGINT REFERENCES users(id),
    is_required BOOLEAN DEFAULT true,
    estimated_days INTEGER DEFAULT 1,
    auto_approve BOOLEAN DEFAULT false,
    conditions JSONB,
    actions JSONB,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT chk_workflow_step_order CHECK (step_order > 0),
    CONSTRAINT chk_workflow_step_days CHECK (estimated_days > 0)
);

-- Table des instances de workflow de documents
CREATE TABLE document_workflow_instances (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL REFERENCES document_workflows(id),
    document_id BIGINT NOT NULL REFERENCES ged_documents(id) ON DELETE CASCADE,
    current_step INTEGER DEFAULT 1,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_by BIGINT REFERENCES users(id),
    completed_at TIMESTAMP,
    completed_by BIGINT REFERENCES users(id),
    total_steps INTEGER NOT NULL,
    completed_steps INTEGER DEFAULT 0,
    progress DECIMAL(5,2) DEFAULT 0.00,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    country_code VARCHAR(3) NOT NULL,
    accounting_standard VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT chk_doc_workflow_instance_status CHECK (status IN (
        'PENDING', 'IN_PROGRESS', 'COMPLETED', 'REJECTED', 'CANCELLED'
    )),
    CONSTRAINT chk_doc_workflow_instance_progress CHECK (progress >= 0 AND progress <= 100),
    CONSTRAINT chk_doc_workflow_instance_steps CHECK (total_steps > 0 AND completed_steps >= 0)
);

-- Table des versions de documents
CREATE TABLE document_versions (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES ged_documents(id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(50),
    mime_type VARCHAR(100),
    checksum VARCHAR(255),
    change_description TEXT,
    is_current BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    
    -- Contraintes
    CONSTRAINT chk_doc_version_number CHECK (version_number > 0),
    CONSTRAINT chk_doc_version_file_size CHECK (file_size > 0),
    UNIQUE(document_id, version_number)
);

-- Table des métadonnées de documents
CREATE TABLE document_metadata (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES ged_documents(id) ON DELETE CASCADE,
    metadata_key VARCHAR(100) NOT NULL,
    metadata_value TEXT,
    metadata_type VARCHAR(50) DEFAULT 'TEXT',
    is_searchable BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT chk_doc_metadata_type CHECK (metadata_type IN (
        'TEXT', 'NUMBER', 'DATE', 'BOOLEAN', 'JSON', 'XML'
    )),
    UNIQUE(document_id, metadata_key)
);

-- Table des accès aux documents
CREATE TABLE document_access_logs (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES ged_documents(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    access_type VARCHAR(50) NOT NULL,
    access_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    session_id VARCHAR(255),
    company_id BIGINT NOT NULL REFERENCES companies(id),
    
    -- Contraintes
    CONSTRAINT chk_doc_access_type CHECK (access_type IN (
        'VIEW', 'DOWNLOAD', 'EDIT', 'DELETE', 'APPROVE', 'REJECT', 'COMMENT'
    ))
);

-- ==================== INDEX POUR PERFORMANCE ====================

-- Index pour ged_documents
CREATE INDEX idx_ged_documents_company_id ON ged_documents(company_id);
CREATE INDEX idx_ged_documents_document_type ON ged_documents(document_type);
CREATE INDEX idx_ged_documents_status ON ged_documents(status);
CREATE INDEX idx_ged_documents_security_level ON ged_documents(security_level);
CREATE INDEX idx_ged_documents_created_at ON ged_documents(created_at);
CREATE INDEX idx_ged_documents_document_code ON ged_documents(document_code);
CREATE INDEX idx_ged_documents_title ON ged_documents(title);
CREATE INDEX idx_ged_documents_category ON ged_documents(category);
CREATE INDEX idx_ged_documents_created_by ON ged_documents(created_by);
CREATE INDEX idx_ged_documents_entity_reference ON ged_documents(entity_reference_type, entity_reference_id);

-- Index pour document_workflows
CREATE INDEX idx_document_workflows_company_id ON document_workflows(company_id);
CREATE INDEX idx_document_workflows_document_type ON document_workflows(document_type);
CREATE INDEX idx_document_workflows_is_active ON document_workflows(is_active);
CREATE INDEX idx_document_workflows_workflow_code ON document_workflows(workflow_code);

-- Index pour document_approvals
CREATE INDEX idx_document_approvals_document_id ON document_approvals(document_id);
CREATE INDEX idx_document_approvals_approver_id ON document_approvals(approver_id);
CREATE INDEX idx_document_approvals_approval_status ON document_approvals(approval_status);
CREATE INDEX idx_document_approvals_approval_level ON document_approvals(approval_level);
CREATE INDEX idx_document_approvals_company_id ON document_approvals(company_id);
CREATE INDEX idx_document_approvals_due_date ON document_approvals(due_date);
CREATE INDEX idx_document_approvals_is_overdue ON document_approvals(is_overdue);

-- Index pour workflow_steps
CREATE INDEX idx_workflow_steps_workflow_id ON workflow_steps(workflow_id);
CREATE INDEX idx_workflow_steps_step_order ON workflow_steps(step_order);
CREATE INDEX idx_workflow_steps_approver_role ON workflow_steps(approver_role);
CREATE INDEX idx_workflow_steps_approver_id ON workflow_steps(approver_id);

-- Index pour document_workflow_instances
CREATE INDEX idx_doc_workflow_instances_workflow_id ON document_workflow_instances(workflow_id);
CREATE INDEX idx_doc_workflow_instances_document_id ON document_workflow_instances(document_id);
CREATE INDEX idx_doc_workflow_instances_status ON document_workflow_instances(status);
CREATE INDEX idx_doc_workflow_instances_started_by ON document_workflow_instances(started_by);
CREATE INDEX idx_doc_workflow_instances_company_id ON document_workflow_instances(company_id);

-- Index pour document_versions
CREATE INDEX idx_document_versions_document_id ON document_versions(document_id);
CREATE INDEX idx_document_versions_version_number ON document_versions(version_number);
CREATE INDEX idx_document_versions_is_current ON document_versions(is_current);

-- Index pour document_metadata
CREATE INDEX idx_document_metadata_document_id ON document_metadata(document_id);
CREATE INDEX idx_document_metadata_key ON document_metadata(metadata_key);
CREATE INDEX idx_document_metadata_searchable ON document_metadata(is_searchable);

-- Index pour document_access_logs
CREATE INDEX idx_document_access_logs_document_id ON document_access_logs(document_id);
CREATE INDEX idx_document_access_logs_user_id ON document_access_logs(user_id);
CREATE INDEX idx_document_access_logs_access_type ON document_access_logs(access_type);
CREATE INDEX idx_document_access_logs_access_date ON document_access_logs(access_date);
CREATE INDEX idx_document_access_logs_company_id ON document_access_logs(company_id);

-- ==================== TRIGGERS POUR MISE À JOUR AUTOMATIQUE ====================

-- Trigger pour mettre à jour updated_at sur ged_documents
CREATE OR REPLACE FUNCTION update_ged_documents_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_ged_documents_updated_at
    BEFORE UPDATE ON ged_documents
    FOR EACH ROW
    EXECUTE FUNCTION update_ged_documents_updated_at();

-- Trigger pour mettre à jour updated_at sur document_workflows
CREATE OR REPLACE FUNCTION update_document_workflows_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_document_workflows_updated_at
    BEFORE UPDATE ON document_workflows
    FOR EACH ROW
    EXECUTE FUNCTION update_document_workflows_updated_at();

-- Trigger pour mettre à jour updated_at sur document_approvals
CREATE OR REPLACE FUNCTION update_document_approvals_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_document_approvals_updated_at
    BEFORE UPDATE ON document_approvals
    FOR EACH ROW
    EXECUTE FUNCTION update_document_approvals_updated_at();

-- Trigger pour mettre à jour updated_at sur workflow_steps
CREATE OR REPLACE FUNCTION update_workflow_steps_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_workflow_steps_updated_at
    BEFORE UPDATE ON workflow_steps
    FOR EACH ROW
    EXECUTE FUNCTION update_workflow_steps_updated_at();

-- Trigger pour mettre à jour updated_at sur document_workflow_instances
CREATE OR REPLACE FUNCTION update_document_workflow_instances_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_document_workflow_instances_updated_at
    BEFORE UPDATE ON document_workflow_instances
    FOR EACH ROW
    EXECUTE FUNCTION update_document_workflow_instances_updated_at();

-- Trigger pour mettre à jour updated_at sur document_metadata
CREATE OR REPLACE FUNCTION update_document_metadata_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_document_metadata_updated_at
    BEFORE UPDATE ON document_metadata
    FOR EACH ROW
    EXECUTE FUNCTION update_document_metadata_updated_at();

-- ==================== DONNÉES INITIALES ====================

-- Insérer des workflows par défaut
INSERT INTO document_workflows (workflow_code, workflow_name, description, document_type, is_active, requires_approval, approval_levels, company_id, country_code, accounting_standard, created_by) VALUES
('WF-INV-001', 'Workflow Factures', 'Workflow d''approbation pour les factures', 'INVOICE', true, true, 2, 1, 'SN', 'SYCEBNL', 1),
('WF-CON-001', 'Workflow Contrats', 'Workflow d''approbation pour les contrats', 'CONTRACT', true, true, 3, 1, 'SN', 'SYCEBNL', 1),
('WF-REP-001', 'Workflow Rapports', 'Workflow d''approbation pour les rapports', 'REPORT', true, true, 1, 1, 'SN', 'SYCEBNL', 1),
('WF-POL-001', 'Workflow Politiques', 'Workflow d''approbation pour les politiques', 'POLICY', true, true, 2, 1, 'SN', 'SYCEBNL', 1);

-- Insérer des étapes de workflow par défaut pour les factures
INSERT INTO workflow_steps (workflow_id, step_name, step_description, step_order, approver_role, is_required, estimated_days, company_id) VALUES
(1, 'Vérification comptable', 'Vérification des montants et comptes comptables', 1, 'COMPTABLE', true, 1, 1),
(1, 'Approbation manager', 'Approbation par le manager financier', 2, 'MANAGER_FINANCIER', true, 2, 1);

-- Insérer des étapes de workflow par défaut pour les contrats
INSERT INTO workflow_steps (workflow_id, step_name, step_description, step_order, approver_role, is_required, estimated_days, company_id) VALUES
(2, 'Vérification juridique', 'Vérification des clauses juridiques', 1, 'JURISTE', true, 3, 1),
(2, 'Approbation commercial', 'Approbation par le responsable commercial', 2, 'RESPONSABLE_COMMERCIAL', true, 2, 1),
(2, 'Validation direction', 'Validation finale par la direction', 3, 'DIRECTEUR', false, 1, 1);

-- ==================== COMMENTAIRES ====================

COMMENT ON TABLE ged_documents IS 'Table des documents GED (Gestion Électronique de Documents)';
COMMENT ON TABLE document_workflows IS 'Table des workflows de documents';
COMMENT ON TABLE document_approvals IS 'Table des approbations de documents';
COMMENT ON TABLE workflow_steps IS 'Table des étapes de workflow';
COMMENT ON TABLE document_workflow_instances IS 'Table des instances de workflow de documents';
COMMENT ON TABLE document_versions IS 'Table des versions de documents';
COMMENT ON TABLE document_metadata IS 'Table des métadonnées de documents';
COMMENT ON TABLE document_access_logs IS 'Table des logs d''accès aux documents';

-- ==================== FIN DU SCRIPT ====================






