-- Script d'initialisation pour la table metrics
-- Ce script sera exécuté automatiquement au démarrage de l'application

-- Créer la table metrics si elle n'existe pas
CREATE TABLE IF NOT EXISTS metrics (
    id BIGSERIAL PRIMARY KEY,
    metric_name VARCHAR(255) NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    metric_value DOUBLE PRECISION NOT NULL,
    unit VARCHAR(50),
    category VARCHAR(50) NOT NULL,
    source VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    description TEXT,
    tags TEXT,
    threshold_warning DOUBLE PRECISION,
    threshold_critical DOUBLE PRECISION,
    status VARCHAR(50) NOT NULL,
    entreprise_id BIGINT,
    utilisateur_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Créer les index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_metrics_entreprise_id ON metrics(entreprise_id);
CREATE INDEX IF NOT EXISTS idx_metrics_category ON metrics(category);
CREATE INDEX IF NOT EXISTS idx_metrics_status ON metrics(status);
CREATE INDEX IF NOT EXISTS idx_metrics_timestamp ON metrics(timestamp);
CREATE INDEX IF NOT EXISTS idx_metrics_metric_name ON metrics(metric_name);
CREATE INDEX IF NOT EXISTS idx_metrics_source ON metrics(source);

-- Insérer une métrique de test pour vérifier que la table fonctionne
INSERT INTO metrics (metric_name, metric_type, metric_value, category, source, timestamp, status, entreprise_id, utilisateur_id, description)
VALUES ('test.table.created', 'GAUGE', 1.0, 'SYSTEM', 'database', CURRENT_TIMESTAMP, 'NORMAL', 1, 1, 'Métrique de test pour vérifier la création de la table');

-- Insérer une métrique pour l'authentification
INSERT INTO metrics (metric_name, metric_type, metric_value, category, source, timestamp, status, entreprise_id, utilisateur_id, description)
VALUES ('auth.system.initialized', 'GAUGE', 1.0, 'AUTHENTICATION', 'spring-security', CURRENT_TIMESTAMP, 'NORMAL', 1, 1, 'Système d''authentification initialisé avec succès');

-- Insérer une métrique pour les utilisateurs
INSERT INTO metrics (metric_name, metric_type, metric_value, category, source, timestamp, status, entreprise_id, utilisateur_id, description)
VALUES ('users.default.created', 'GAUGE', 5.0, 'USERS', 'user-management-service', CURRENT_TIMESTAMP, 'NORMAL', 1, 1, 'Utilisateurs par défaut créés par le service de gestion');
