-- Migration pour le système de versioning avancé des documents
-- Version: V5
-- Date: 2024-12-19

-- ==============================================
-- TABLE: document_versions
-- ==============================================

CREATE TABLE IF NOT EXISTS document_versions (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    version_number INTEGER NOT NULL,
    version_label VARCHAR(50),
    version_type VARCHAR(20) NOT NULL CHECK (version_type IN ('MAJOR', 'MINOR', 'PATCH', 'DRAFT', 'FINAL', 'ARCHIVED', 'DELETED')),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    file_hash VARCHAR(64) NOT NULL, -- SHA-256
    mime_type VARCHAR(100),
    title VARCHAR(255),
    description TEXT,
    change_summary TEXT,
    change_details TEXT,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_current_version BOOLEAN NOT NULL DEFAULT FALSE,
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    archive_date TIMESTAMP,
    retention_until TIMESTAMP,
    access_count BIGINT NOT NULL DEFAULT 0,
    last_accessed_at TIMESTAMP,
    download_count BIGINT NOT NULL DEFAULT 0,
    last_downloaded_at TIMESTAMP,
    version_metadata TEXT, -- JSON pour métadonnées supplémentaires
    approval_status VARCHAR(20) CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED', 'EXPIRED')),
    approved_by BIGINT,
    approved_at TIMESTAMP,
    approval_notes TEXT,
    is_encrypted BOOLEAN NOT NULL DEFAULT FALSE,
    encryption_key_id VARCHAR(100),
    compression_algorithm VARCHAR(50),
    original_file_size BIGINT,
    compression_ratio DECIMAL(5,4),
    watermark_applied BOOLEAN NOT NULL DEFAULT FALSE,
    watermark_text VARCHAR(255),
    digital_signature TEXT,
    signature_algorithm VARCHAR(50),
    signed_by BIGINT,
    signed_at TIMESTAMP,
    certificate_chain TEXT, -- Chaîne de certificats pour la signature
    version_tags VARCHAR(500), -- Tags spécifiques à cette version
    parent_version_id BIGINT, -- Référence vers la version parente
    branch_name VARCHAR(100), -- Pour le versioning en branches
    merge_source_version_id BIGINT, -- Version source en cas de merge
    is_auto_save BOOLEAN NOT NULL DEFAULT FALSE,
    auto_save_interval_minutes INTEGER,
    next_auto_save_at TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT fk_document_versions_document_id FOREIGN KEY (document_id) REFERENCES ged_documents(id) ON DELETE CASCADE,
    CONSTRAINT fk_document_versions_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT fk_document_versions_approved_by FOREIGN KEY (approved_by) REFERENCES users(id),
    CONSTRAINT fk_document_versions_signed_by FOREIGN KEY (signed_by) REFERENCES users(id),
    CONSTRAINT fk_document_versions_parent_version_id FOREIGN KEY (parent_version_id) REFERENCES document_versions(id),
    CONSTRAINT fk_document_versions_merge_source_version_id FOREIGN KEY (merge_source_version_id) REFERENCES document_versions(id),
    
    -- Contraintes d'unicité
    CONSTRAINT uk_document_versions_document_version UNIQUE (document_id, version_number),
    CONSTRAINT uk_document_versions_document_current UNIQUE (document_id, is_current_version) DEFERRABLE INITIALLY DEFERRED,
    
    -- Contraintes de validation
    CONSTRAINT chk_document_versions_version_number CHECK (version_number > 0),
    CONSTRAINT chk_document_versions_file_size CHECK (file_size >= 0),
    CONSTRAINT chk_document_versions_access_count CHECK (access_count >= 0),
    CONSTRAINT chk_document_versions_download_count CHECK (download_count >= 0),
    CONSTRAINT chk_document_versions_compression_ratio CHECK (compression_ratio >= 0 AND compression_ratio <= 1),
    CONSTRAINT chk_document_versions_auto_save_interval CHECK (auto_save_interval_minutes IS NULL OR auto_save_interval_minutes > 0)
);

-- ==============================================
-- INDEX POUR PERFORMANCE
-- ==============================================

-- Index pour les recherches par document
CREATE INDEX IF NOT EXISTS idx_document_versions_document_id ON document_versions(document_id);
CREATE INDEX IF NOT EXISTS idx_document_versions_document_version ON document_versions(document_id, version_number);
CREATE INDEX IF NOT EXISTS idx_document_versions_current_version ON document_versions(document_id, is_current_version) WHERE is_current_version = TRUE;

-- Index pour les recherches par type de version
CREATE INDEX IF NOT EXISTS idx_document_versions_version_type ON document_versions(version_type);
CREATE INDEX IF NOT EXISTS idx_document_versions_document_type ON document_versions(document_id, version_type);

-- Index pour les recherches par statut d'approbation
CREATE INDEX IF NOT EXISTS idx_document_versions_approval_status ON document_versions(approval_status);
CREATE INDEX IF NOT EXISTS idx_document_versions_pending_approval ON document_versions(approval_status, created_at) WHERE approval_status = 'PENDING';

-- Index pour les recherches par utilisateur
CREATE INDEX IF NOT EXISTS idx_document_versions_created_by ON document_versions(created_by);
CREATE INDEX IF NOT EXISTS idx_document_versions_approved_by ON document_versions(approved_by);
CREATE INDEX IF NOT EXISTS idx_document_versions_signed_by ON document_versions(signed_by);

-- Index pour les recherches par date
CREATE INDEX IF NOT EXISTS idx_document_versions_created_at ON document_versions(created_at);
CREATE INDEX IF NOT EXISTS idx_document_versions_approved_at ON document_versions(approved_at);
CREATE INDEX IF NOT EXISTS idx_document_versions_signed_at ON document_versions(signed_at);

-- Index pour les recherches par hash et intégrité
CREATE INDEX IF NOT EXISTS idx_document_versions_file_hash ON document_versions(file_hash);
CREATE INDEX IF NOT EXISTS idx_document_versions_file_name ON document_versions(file_name);

-- Index pour les recherches par branche
CREATE INDEX IF NOT EXISTS idx_document_versions_branch_name ON document_versions(branch_name);
CREATE INDEX IF NOT EXISTS idx_document_versions_document_branch ON document_versions(document_id, branch_name);

-- Index pour les recherches par hiérarchie
CREATE INDEX IF NOT EXISTS idx_document_versions_parent_version_id ON document_versions(parent_version_id);
CREATE INDEX IF NOT EXISTS idx_document_versions_merge_source_version_id ON document_versions(merge_source_version_id);

-- Index pour les recherches par archivage
CREATE INDEX IF NOT EXISTS idx_document_versions_is_archived ON document_versions(is_archived);
CREATE INDEX IF NOT EXISTS idx_document_versions_archive_date ON document_versions(archive_date);

-- Index pour les recherches par rétention
CREATE INDEX IF NOT EXISTS idx_document_versions_retention_until ON document_versions(retention_until);
CREATE INDEX IF NOT EXISTS idx_document_versions_expired ON document_versions(retention_until) WHERE retention_until < CURRENT_TIMESTAMP;

-- Index pour les recherches par auto-save
CREATE INDEX IF NOT EXISTS idx_document_versions_is_auto_save ON document_versions(is_auto_save);
CREATE INDEX IF NOT EXISTS idx_document_versions_next_auto_save_at ON document_versions(next_auto_save_at);

-- Index pour les recherches par sécurité
CREATE INDEX IF NOT EXISTS idx_document_versions_is_encrypted ON document_versions(is_encrypted);
CREATE INDEX IF NOT EXISTS idx_document_versions_watermark_applied ON document_versions(watermark_applied);
CREATE INDEX IF NOT EXISTS idx_document_versions_digital_signature ON document_versions(digital_signature) WHERE digital_signature IS NOT NULL;

-- Index pour les recherches par compression
CREATE INDEX IF NOT EXISTS idx_document_versions_compression_algorithm ON document_versions(compression_algorithm);
CREATE INDEX IF NOT EXISTS idx_document_versions_compression_ratio ON document_versions(compression_ratio);

-- Index pour les recherches par performance
CREATE INDEX IF NOT EXISTS idx_document_versions_access_count ON document_versions(access_count DESC);
CREATE INDEX IF NOT EXISTS idx_document_versions_download_count ON document_versions(download_count DESC);
CREATE INDEX IF NOT EXISTS idx_document_versions_last_accessed_at ON document_versions(last_accessed_at DESC);
CREATE INDEX IF NOT EXISTS idx_document_versions_last_downloaded_at ON document_versions(last_downloaded_at DESC);

-- Index pour les recherches par métadonnées
CREATE INDEX IF NOT EXISTS idx_document_versions_version_label ON document_versions(version_label);
CREATE INDEX IF NOT EXISTS idx_document_versions_version_tags ON document_versions USING GIN (to_tsvector('french', version_tags));

-- ==============================================
-- TRIGGERS POUR MISE À JOUR AUTOMATIQUE
-- ==============================================

-- Trigger pour mettre à jour updated_at
CREATE OR REPLACE FUNCTION update_document_versions_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger pour la mise à jour automatique
CREATE TRIGGER trigger_update_document_versions_updated_at
    BEFORE UPDATE ON document_versions
    FOR EACH ROW
    EXECUTE FUNCTION update_document_versions_updated_at();

-- ==============================================
-- VUES POUR FACILITER LES REQUÊTES
-- ==============================================

-- Vue pour les versions actuelles
CREATE OR REPLACE VIEW current_document_versions AS
SELECT 
    dv.*,
    gd.title as document_title,
    gd.document_code,
    gd.company_id,
    gd.country_code,
    gd.accounting_standard
FROM document_versions dv
JOIN ged_documents gd ON dv.document_id = gd.id
WHERE dv.is_current_version = TRUE;

-- Vue pour les versions en attente d'approbation
CREATE OR REPLACE VIEW pending_approval_versions AS
SELECT 
    dv.*,
    gd.title as document_title,
    gd.document_code,
    gd.company_id,
    u.first_name || ' ' || u.last_name as created_by_name
FROM document_versions dv
JOIN ged_documents gd ON dv.document_id = gd.id
LEFT JOIN users u ON dv.created_by = u.id
WHERE dv.approval_status = 'PENDING'
ORDER BY dv.created_at ASC;

-- Vue pour les statistiques de versioning
CREATE OR REPLACE VIEW document_versioning_stats AS
SELECT 
    document_id,
    COUNT(*) as total_versions,
    COUNT(CASE WHEN version_type = 'MAJOR' THEN 1 END) as major_versions,
    COUNT(CASE WHEN version_type = 'MINOR' THEN 1 END) as minor_versions,
    COUNT(CASE WHEN version_type = 'PATCH' THEN 1 END) as patch_versions,
    COUNT(CASE WHEN version_type = 'DRAFT' THEN 1 END) as draft_versions,
    COUNT(CASE WHEN is_archived = TRUE THEN 1 END) as archived_versions,
    COUNT(CASE WHEN approval_status = 'APPROVED' THEN 1 END) as approved_versions,
    COUNT(CASE WHEN approval_status = 'PENDING' THEN 1 END) as pending_versions,
    SUM(file_size) as total_file_size,
    AVG(file_size) as average_file_size,
    MAX(version_number) as latest_version_number,
    MIN(created_at) as first_version_date,
    MAX(created_at) as latest_version_date
FROM document_versions
GROUP BY document_id;

-- ==============================================
-- FONCTIONS UTILITAIRES
-- ==============================================

-- Fonction pour obtenir le prochain numéro de version
CREATE OR REPLACE FUNCTION get_next_version_number(p_document_id BIGINT)
RETURNS INTEGER AS $$
DECLARE
    next_version INTEGER;
BEGIN
    SELECT COALESCE(MAX(version_number), 0) + 1
    INTO next_version
    FROM document_versions
    WHERE document_id = p_document_id;
    
    RETURN next_version;
END;
$$ LANGUAGE plpgsql;

-- Fonction pour archiver les versions expirées
CREATE OR REPLACE FUNCTION archive_expired_versions()
RETURNS INTEGER AS $$
DECLARE
    archived_count INTEGER;
BEGIN
    UPDATE document_versions
    SET is_archived = TRUE,
        archive_date = CURRENT_TIMESTAMP,
        version_type = 'ARCHIVED'
    WHERE retention_until IS NOT NULL 
      AND retention_until < CURRENT_TIMESTAMP
      AND is_archived = FALSE;
    
    GET DIAGNOSTICS archived_count = ROW_COUNT;
    RETURN archived_count;
END;
$$ LANGUAGE plpgsql;

-- Fonction pour nettoyer les versions auto-save anciennes
CREATE OR REPLACE FUNCTION cleanup_old_auto_save_versions(p_days_old INTEGER DEFAULT 7)
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM document_versions
    WHERE is_auto_save = TRUE
      AND created_at < CURRENT_TIMESTAMP - INTERVAL '1 day' * p_days_old
      AND is_current_version = FALSE;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- ==============================================
-- DONNÉES INITIALES (OPTIONNEL)
-- ==============================================

-- Insérer des types de version par défaut si nécessaire
-- (Les enums sont gérés au niveau application)

-- ==============================================
-- COMMENTAIRES
-- ==============================================

COMMENT ON TABLE document_versions IS 'Table pour le versioning avancé des documents avec support des branches, signatures numériques, et auto-save';
COMMENT ON COLUMN document_versions.version_type IS 'Type de version: MAJOR, MINOR, PATCH, DRAFT, FINAL, ARCHIVED, DELETED';
COMMENT ON COLUMN document_versions.file_hash IS 'Hash SHA-256 du fichier pour détecter les changements';
COMMENT ON COLUMN document_versions.approval_status IS 'Statut d''approbation de la version';
COMMENT ON COLUMN document_versions.branch_name IS 'Nom de la branche pour le versioning en branches';
COMMENT ON COLUMN document_versions.parent_version_id IS 'Référence vers la version parente pour la hiérarchie';
COMMENT ON COLUMN document_versions.merge_source_version_id IS 'Version source en cas de fusion de branches';
COMMENT ON COLUMN document_versions.is_auto_save IS 'Indique si cette version est une auto-sauvegarde';
COMMENT ON COLUMN document_versions.next_auto_save_at IS 'Prochaine auto-sauvegarde programmée';
COMMENT ON COLUMN document_versions.compression_ratio IS 'Ratio de compression (0.0 à 1.0)';
COMMENT ON COLUMN document_versions.digital_signature IS 'Signature numérique de la version';
COMMENT ON COLUMN document_versions.certificate_chain IS 'Chaîne de certificats pour la vérification de signature';

-- ==============================================
-- FIN DE LA MIGRATION
-- ==============================================






