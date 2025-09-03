-- Script d'initialisation de la base de données E-COMPTA-IA INTERNATIONAL
-- Ce script sera exécuté automatiquement au premier démarrage du conteneur PostgreSQL

-- Créer la base de données si elle n'existe pas
-- (PostgreSQL crée automatiquement la base de données via les variables d'environnement)

-- Créer les extensions nécessaires
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Créer la table des utilisateurs
CREATE TABLE IF NOT EXISTS utilisateurs (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telephone VARCHAR(20),
    adresse TEXT,
    ville VARCHAR(100),
    code_postal VARCHAR(10),
    pays VARCHAR(100) DEFAULT 'France',
    langue VARCHAR(10) DEFAULT 'fr',
    devise VARCHAR(3) DEFAULT 'EUR',
    actif BOOLEAN DEFAULT true,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Créer la table des rôles
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    permissions TEXT[],
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Créer la table de liaison utilisateurs-rôles
CREATE TABLE IF NOT EXISTS utilisateurs_roles (
    utilisateur_id BIGINT REFERENCES utilisateurs(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (utilisateur_id, role_id)
);

-- Créer la table des entreprises
CREATE TABLE IF NOT EXISTS entreprises (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    siret VARCHAR(14) UNIQUE,
    tva VARCHAR(20),
    adresse TEXT,
    ville VARCHAR(100),
    code_postal VARCHAR(10),
    pays VARCHAR(100) DEFAULT 'France',
    devise VARCHAR(3) DEFAULT 'EUR',
    langue VARCHAR(10) DEFAULT 'fr',
    actif BOOLEAN DEFAULT true,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Créer la table des métriques
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
    entreprise_id BIGINT REFERENCES entreprises(id),
    utilisateur_id BIGINT REFERENCES utilisateurs(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Créer les index pour améliorer les performances
CREATE INDEX IF NOT EXISTS idx_utilisateurs_email ON utilisateurs(email);
CREATE INDEX IF NOT EXISTS idx_utilisateurs_actif ON utilisateurs(actif);
CREATE INDEX IF NOT EXISTS idx_entreprises_siret ON entreprises(siret);
CREATE INDEX IF NOT EXISTS idx_entreprises_actif ON entreprises(actif);
CREATE INDEX IF NOT EXISTS idx_metrics_entreprise_id ON metrics(entreprise_id);
CREATE INDEX IF NOT EXISTS idx_metrics_category ON metrics(category);
CREATE INDEX IF NOT EXISTS idx_metrics_status ON metrics(status);
CREATE INDEX IF NOT EXISTS idx_metrics_timestamp ON metrics(timestamp);

-- Insérer les rôles par défaut
INSERT INTO roles (nom, description, permissions) VALUES
('ADMIN', 'Administrateur système avec tous les droits', ARRAY['*']),
('COMPTABLE', 'Comptable avec accès aux opérations comptables', ARRAY['COMPTA_READ', 'COMPTA_WRITE', 'RAPPORTS_READ']),
('MANAGER', 'Manager avec accès aux rapports et analyses', ARRAY['COMPTA_READ', 'RAPPORTS_READ', 'ANALYSES_READ']),
('UTILISATEUR', 'Utilisateur standard avec accès limité', ARRAY['COMPTA_READ']);

-- Insérer un utilisateur admin par défaut (mot de passe: admin123)
INSERT INTO utilisateurs (nom, prenom, email, password, pays, devise, langue) VALUES
('Admin', 'System', 'admin@ecomptaia.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'France', 'EUR', 'fr');

-- Assigner le rôle admin à l'utilisateur admin
INSERT INTO utilisateurs_roles (utilisateur_id, role_id) VALUES
(1, 1);

-- Insérer une entreprise par défaut
INSERT INTO entreprises (nom, pays, devise, langue) VALUES
('E-COMPTA-IA Demo', 'France', 'EUR', 'fr');

-- Insérer une métrique de test
INSERT INTO metrics (metric_name, metric_type, metric_value, category, source, timestamp, status, entreprise_id, utilisateur_id, description)
VALUES ('database.initialized', 'GAUGE', 1.0, 'SYSTEM', 'postgres', CURRENT_TIMESTAMP, 'NORMAL', 1, 1, 'Base de données initialisée avec succès');

-- Message de confirmation
DO $$
BEGIN
    RAISE NOTICE 'Base de données E-COMPTA-IA INTERNATIONAL initialisée avec succès !';
    RAISE NOTICE 'Utilisateur admin créé: admin@ecomptaia.com (mot de passe: admin123)';
    RAISE NOTICE 'Entreprise de démonstration créée: E-COMPTA-IA Demo';
END $$;
