-- Script SQL pour créer la table metrics dans H2 Console
-- Copier et coller ce script dans l'interface H2 Console

-- Créer la table metrics
CREATE TABLE IF NOT EXISTS metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric_name VARCHAR(255) NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    metric_value DOUBLE NOT NULL,
    unit VARCHAR(50),
    category VARCHAR(50) NOT NULL,
    source VARCHAR(255) NOT NULL,
    timestamp DATETIME NOT NULL,
    description TEXT,
    tags TEXT,
    threshold_warning DOUBLE,
    threshold_critical DOUBLE,
    status VARCHAR(50) NOT NULL,
    entreprise_id BIGINT,
    utilisateur_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Créer les index
CREATE INDEX IF NOT EXISTS idx_metrics_entreprise_id ON metrics(entreprise_id);
CREATE INDEX IF NOT EXISTS idx_metrics_category ON metrics(category);
CREATE INDEX IF NOT EXISTS idx_metrics_status ON metrics(status);
CREATE INDEX IF NOT EXISTS idx_metrics_timestamp ON metrics(timestamp);
CREATE INDEX IF NOT EXISTS idx_metrics_metric_name ON metrics(metric_name);
CREATE INDEX IF NOT EXISTS idx_metrics_source ON metrics(source);

-- Insérer une métrique de test
INSERT INTO metrics (metric_name, metric_type, metric_value, category, source, timestamp, status, entreprise_id, utilisateur_id, description)
VALUES ('test.table.created', 'GAUGE', 1.0, 'SYSTEM', 'database', CURRENT_TIMESTAMP(), 'NORMAL', 1, 1, 'Métrique de test pour vérifier la création de la table');

-- Vérifier que la table a été créée
SELECT COUNT(*) as total_metrics FROM metrics;
