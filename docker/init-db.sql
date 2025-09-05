-- Script d'initialisation de la base de données E-COMPTA-IA
-- =========================================================

-- Créer la base de données si elle n'existe pas
-- (PostgreSQL crée automatiquement la base spécifiée dans POSTGRES_DB)

-- Créer l'utilisateur ecomptaia_user s'il n'existe pas
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'ecomptaia_user') THEN
        CREATE ROLE ecomptaia_user WITH LOGIN PASSWORD 'ecomptaia_secure_password_2024_change_me';
    END IF;
END
$$;

-- Accorder les privilèges sur la base de données
GRANT ALL PRIVILEGES ON DATABASE ecomptaia_db TO ecomptaia_user;

-- Accorder les privilèges sur le schéma public
GRANT ALL PRIVILEGES ON SCHEMA public TO ecomptaia_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ecomptaia_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO ecomptaia_user;

-- Configuration des extensions PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
CREATE EXTENSION IF NOT EXISTS "btree_gin";

-- Configuration des paramètres de performance
ALTER SYSTEM SET shared_preload_libraries = 'pg_stat_statements';
ALTER SYSTEM SET track_activity_query_size = 2048;
ALTER SYSTEM SET pg_stat_statements.track = 'all';
ALTER SYSTEM SET log_min_duration_statement = 1000;
ALTER SYSTEM SET log_line_prefix = '%t [%p]: [%l-1] user=%u,db=%d,app=%a,client=%h ';

-- Créer les tables de base pour le monitoring
CREATE TABLE IF NOT EXISTS system_metrics (
    id SERIAL PRIMARY KEY,
    metric_name VARCHAR(255) NOT NULL,
    metric_value DECIMAL(15,4) NOT NULL,
    metric_unit VARCHAR(50),
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tags JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index pour les performances
CREATE INDEX IF NOT EXISTS idx_system_metrics_name_time ON system_metrics(metric_name, timestamp);
CREATE INDEX IF NOT EXISTS idx_system_metrics_timestamp ON system_metrics(timestamp);

-- Table pour les logs d'audit
CREATE TABLE IF NOT EXISTS audit_logs (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    action VARCHAR(255) NOT NULL,
    resource_type VARCHAR(255),
    resource_id VARCHAR(255),
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index pour les performances
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_audit_logs_resource ON audit_logs(resource_type, resource_id);

-- Table pour les sessions utilisateur
CREATE TABLE IF NOT EXISTS user_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255) NOT NULL,
    session_token VARCHAR(500) NOT NULL UNIQUE,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    last_activity TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index pour les performances
CREATE INDEX IF NOT EXISTS idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_user_sessions_token ON user_sessions(session_token);
CREATE INDEX IF NOT EXISTS idx_user_sessions_expires ON user_sessions(expires_at);
CREATE INDEX IF NOT EXISTS idx_user_sessions_active ON user_sessions(is_active);

-- Table pour les configurations système
CREATE TABLE IF NOT EXISTS system_config (
    id SERIAL PRIMARY KEY,
    config_key VARCHAR(255) NOT NULL UNIQUE,
    config_value TEXT,
    config_type VARCHAR(50) DEFAULT 'string',
    description TEXT,
    is_encrypted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index pour les performances
CREATE INDEX IF NOT EXISTS idx_system_config_key ON system_config(config_key);

-- Insérer les configurations par défaut
INSERT INTO system_config (config_key, config_value, config_type, description) VALUES
('app.name', 'E-COMPTA-IA INTERNATIONAL', 'string', 'Nom de l''application'),
('app.version', '1.0.0', 'string', 'Version de l''application'),
('app.environment', 'production', 'string', 'Environnement d''exécution'),
('security.jwt.secret', 'ecomptaia_jwt_secret_key_2024_change_in_production', 'string', 'Clé secrète JWT'),
('security.jwt.expiration', '86400000', 'number', 'Durée d''expiration JWT en millisecondes'),
('database.connection.pool.size', '20', 'number', 'Taille du pool de connexions'),
('cache.redis.ttl', '3600000', 'number', 'TTL du cache Redis en millisecondes'),
('monitoring.prometheus.enabled', 'true', 'boolean', 'Activation de Prometheus'),
('monitoring.grafana.enabled', 'true', 'boolean', 'Activation de Grafana'),
('backup.enabled', 'true', 'boolean', 'Activation des sauvegardes'),
('backup.schedule', '0 2 * * *', 'string', 'Planification des sauvegardes (cron)'),
('backup.retention.days', '30', 'number', 'Rétention des sauvegardes en jours')
ON CONFLICT (config_key) DO NOTHING;

-- Fonction pour mettre à jour le timestamp updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger pour system_config
CREATE TRIGGER update_system_config_updated_at 
    BEFORE UPDATE ON system_config 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Fonction pour nettoyer les sessions expirées
CREATE OR REPLACE FUNCTION cleanup_expired_sessions()
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM user_sessions 
    WHERE expires_at < CURRENT_TIMESTAMP OR is_active = FALSE;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Fonction pour nettoyer les logs d'audit anciens
CREATE OR REPLACE FUNCTION cleanup_old_audit_logs(days_to_keep INTEGER DEFAULT 90)
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM audit_logs 
    WHERE timestamp < CURRENT_TIMESTAMP - INTERVAL '1 day' * days_to_keep;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Fonction pour nettoyer les métriques anciennes
CREATE OR REPLACE FUNCTION cleanup_old_metrics(days_to_keep INTEGER DEFAULT 30)
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM system_metrics 
    WHERE timestamp < CURRENT_TIMESTAMP - INTERVAL '1 day' * days_to_keep;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Créer les vues utiles pour le monitoring
CREATE OR REPLACE VIEW v_active_sessions AS
SELECT 
    us.id,
    us.user_id,
    us.ip_address,
    us.user_agent,
    us.created_at,
    us.expires_at,
    us.last_activity,
    EXTRACT(EPOCH FROM (us.expires_at - CURRENT_TIMESTAMP)) as seconds_until_expiry
FROM user_sessions us
WHERE us.is_active = TRUE 
    AND us.expires_at > CURRENT_TIMESTAMP;

CREATE OR REPLACE VIEW v_system_health AS
SELECT 
    'database' as component,
    'postgresql' as type,
    CASE 
        WHEN pg_is_in_recovery() THEN 'standby'
        ELSE 'primary'
    END as status,
    pg_database_size(current_database()) as size_bytes,
    (SELECT count(*) FROM pg_stat_activity WHERE state = 'active') as active_connections,
    CURRENT_TIMESTAMP as checked_at
UNION ALL
SELECT 
    'cache' as component,
    'redis' as type,
    'unknown' as status,
    0 as size_bytes,
    0 as active_connections,
    CURRENT_TIMESTAMP as checked_at;

-- Accorder les privilèges sur les nouvelles tables et vues
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO ecomptaia_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO ecomptaia_user;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO ecomptaia_user;

-- Message de confirmation
DO $$
BEGIN
    RAISE NOTICE 'Base de données E-COMPTA-IA initialisée avec succès!';
    RAISE NOTICE 'Utilisateur: ecomptaia_user';
    RAISE NOTICE 'Base de données: ecomptaia_db';
    RAISE NOTICE 'Extensions installées: uuid-ossp, pg_trgm, btree_gin';
    RAISE NOTICE 'Tables créées: system_metrics, audit_logs, user_sessions, system_config';
    RAISE NOTICE 'Fonctions créées: update_updated_at_column, cleanup_expired_sessions, cleanup_old_audit_logs, cleanup_old_metrics';
    RAISE NOTICE 'Vues créées: v_active_sessions, v_system_health';
END
$$;