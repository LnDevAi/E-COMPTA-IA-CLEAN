-- =====================================================
-- E-COMPTA-IA - TABLES CRM ET MARKETING
-- Version: 2.0
-- Date: 2024
-- Description: Création des tables pour le module CRM-Digital Marketing
-- =====================================================

-- ==================== TABLES CRM ====================

-- Table des profils clients CRM
CREATE TABLE crm_customers (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    third_party_id BIGINT REFERENCES third_parties(id),
    
    -- Scoring IA
    ai_score INTEGER,
    churn_probability DECIMAL(5,4),
    lifetime_value_predicted DECIMAL(15,2),
    customer_segment VARCHAR(50),
    payment_behavior VARCHAR(50),
    satisfaction_score INTEGER,
    
    -- Données comportementales
    avg_payment_delay INTEGER,
    total_revenue_generated DECIMAL(15,2) DEFAULT 0,
    last_purchase_date TIMESTAMP,
    purchase_frequency DECIMAL(8,2),
    
    -- Préférences communication
    preferred_channels JSONB,
    email_opt_in BOOLEAN DEFAULT true,
    sms_opt_in BOOLEAN DEFAULT true,
    social_media_handles JSONB,
    best_contact_time VARCHAR(20),
    language_preference VARCHAR(10) DEFAULT 'fr',
    timezone VARCHAR(50),
    
    -- Intégrations externes
    external_ids JSONB,
    
    -- Métadonnées
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT fk_crm_customer_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_crm_customer_third_party FOREIGN KEY (third_party_id) REFERENCES third_parties(id),
    CONSTRAINT chk_ai_score CHECK (ai_score >= 0 AND ai_score <= 100),
    CONSTRAINT chk_churn_probability CHECK (churn_probability >= 0 AND churn_probability <= 1),
    CONSTRAINT chk_satisfaction_score CHECK (satisfaction_score >= 0 AND satisfaction_score <= 100)
);

-- Table des campagnes marketing
CREATE TABLE marketing_campaigns (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    
    -- Informations de base
    campaign_name VARCHAR(200) NOT NULL,
    campaign_code VARCHAR(50) UNIQUE,
    campaign_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    description TEXT,
    
    -- Ciblage
    target_segments JSONB,
    target_criteria JSONB,
    estimated_reach INTEGER,
    
    -- Contenu
    subject_line VARCHAR(500),
    content TEXT,
    content_variants JSONB,
    attachments JSONB,
    
    -- Planning
    scheduled_start TIMESTAMP,
    scheduled_end TIMESTAMP,
    actual_start TIMESTAMP,
    actual_end TIMESTAMP,
    
    -- Budget
    budget_allocated DECIMAL(15,2),
    cost_per_message DECIMAL(10,4),
    total_cost DECIMAL(15,2) DEFAULT 0,
    
    -- Métriques
    messages_sent INTEGER DEFAULT 0,
    messages_delivered INTEGER DEFAULT 0,
    messages_opened INTEGER DEFAULT 0,
    clicks_count INTEGER DEFAULT 0,
    conversions_count INTEGER DEFAULT 0,
    revenue_generated DECIMAL(15,2) DEFAULT 0,
    roi_percentage DECIMAL(5,2) DEFAULT 0,
    
    -- Configuration
    channel_config JSONB,
    automation_rules JSONB,
    
    -- Métadonnées
    created_by VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT fk_marketing_campaign_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

-- Table des messages marketing
CREATE TABLE marketing_messages (
    id BIGSERIAL PRIMARY KEY,
    campaign_id BIGINT NOT NULL REFERENCES marketing_campaigns(id),
    customer_id BIGINT REFERENCES crm_customers(id),
    
    -- Informations du message
    channel VARCHAR(50) NOT NULL,
    recipient_email VARCHAR(255),
    recipient_phone VARCHAR(20),
    subject VARCHAR(500),
    content TEXT,
    
    -- Statut et timing
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    opened_at TIMESTAMP,
    clicked_at TIMESTAMP,
    
    -- Métriques
    delivery_status VARCHAR(50),
    open_count INTEGER DEFAULT 0,
    click_count INTEGER DEFAULT 0,
    conversion_value DECIMAL(15,2),
    
    -- Métadonnées
    external_message_id VARCHAR(255),
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT fk_marketing_message_campaign FOREIGN KEY (campaign_id) REFERENCES marketing_campaigns(id),
    CONSTRAINT fk_marketing_message_customer FOREIGN KEY (customer_id) REFERENCES crm_customers(id)
);

-- Table des fournisseurs marketing
CREATE TABLE marketing_providers (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    
    -- Informations du fournisseur
    provider_type VARCHAR(50) NOT NULL, -- EMAIL, SMS, SOCIAL
    platform_name VARCHAR(100) NOT NULL,
    api_credentials JSONB NOT NULL,
    
    -- Configuration
    is_active BOOLEAN DEFAULT true,
    rate_limit_per_minute INTEGER,
    rate_limit_per_day INTEGER,
    
    -- Métadonnées
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT fk_marketing_provider_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT chk_provider_type CHECK (provider_type IN ('EMAIL', 'SMS', 'SOCIAL', 'CRM'))
);

-- Table des interactions clients
CREATE TABLE customer_interactions (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES crm_customers(id),
    campaign_id BIGINT REFERENCES marketing_campaigns(id),
    
    -- Type d'interaction
    interaction_type VARCHAR(50) NOT NULL, -- EMAIL_OPEN, EMAIL_CLICK, SMS_RECEIVED, SOCIAL_ENGAGEMENT
    channel VARCHAR(50) NOT NULL,
    
    -- Détails
    interaction_data JSONB,
    sentiment_score DECIMAL(3,2), -- -1.0 à 1.0
    engagement_score INTEGER, -- 0 à 100
    
    -- Métadonnées
    ip_address INET,
    user_agent TEXT,
    location_data JSONB,
    
    -- Timing
    interaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT fk_customer_interaction_customer FOREIGN KEY (customer_id) REFERENCES crm_customers(id),
    CONSTRAINT fk_customer_interaction_campaign FOREIGN KEY (campaign_id) REFERENCES marketing_campaigns(id),
    CONSTRAINT chk_sentiment_score CHECK (sentiment_score >= -1.0 AND sentiment_score <= 1.0),
    CONSTRAINT chk_engagement_score CHECK (engagement_score >= 0 AND engagement_score <= 100)
);

-- Table des workflows marketing
CREATE TABLE marketing_workflows (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    
    -- Informations du workflow
    workflow_name VARCHAR(200) NOT NULL,
    workflow_code VARCHAR(50) UNIQUE,
    description TEXT,
    
    -- Configuration
    trigger_conditions JSONB NOT NULL,
    workflow_steps JSONB NOT NULL,
    is_active BOOLEAN DEFAULT true,
    
    -- Métriques
    total_executions INTEGER DEFAULT 0,
    successful_executions INTEGER DEFAULT 0,
    failed_executions INTEGER DEFAULT 0,
    
    -- Métadonnées
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT fk_marketing_workflow_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

-- Table des templates de contenu
CREATE TABLE content_templates (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    
    -- Informations du template
    template_name VARCHAR(200) NOT NULL,
    template_type VARCHAR(50) NOT NULL, -- EMAIL, SMS, SOCIAL
    category VARCHAR(100),
    
    -- Contenu
    subject_template VARCHAR(500),
    content_template TEXT NOT NULL,
    variables JSONB, -- Variables disponibles dans le template
    
    -- Métadonnées
    language VARCHAR(10) DEFAULT 'fr',
    is_active BOOLEAN DEFAULT true,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contraintes
    CONSTRAINT fk_content_template_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT chk_template_type CHECK (template_type IN ('EMAIL', 'SMS', 'SOCIAL'))
);

-- ==================== INDEX DE PERFORMANCE ====================

-- Index pour crm_customers
CREATE INDEX idx_crm_customers_company_id ON crm_customers(company_id);
CREATE INDEX idx_crm_customers_third_party_id ON crm_customers(third_party_id);
CREATE INDEX idx_crm_customers_ai_score ON crm_customers(ai_score DESC);
CREATE INDEX idx_crm_customers_customer_segment ON crm_customers(customer_segment);
CREATE INDEX idx_crm_customers_churn_probability ON crm_customers(churn_probability DESC);
CREATE INDEX idx_crm_customers_last_purchase_date ON crm_customers(last_purchase_date);
CREATE INDEX idx_crm_customers_is_active ON crm_customers(is_active);

-- Index pour marketing_campaigns
CREATE INDEX idx_marketing_campaigns_company_id ON marketing_campaigns(company_id);
CREATE INDEX idx_marketing_campaigns_campaign_type ON marketing_campaigns(campaign_type);
CREATE INDEX idx_marketing_campaigns_status ON marketing_campaigns(status);
CREATE INDEX idx_marketing_campaigns_scheduled_start ON marketing_campaigns(scheduled_start);
CREATE INDEX idx_marketing_campaigns_is_active ON marketing_campaigns(is_active);

-- Index pour marketing_messages
CREATE INDEX idx_marketing_messages_campaign_id ON marketing_messages(campaign_id);
CREATE INDEX idx_marketing_messages_customer_id ON marketing_messages(customer_id);
CREATE INDEX idx_marketing_messages_channel ON marketing_messages(channel);
CREATE INDEX idx_marketing_messages_status ON marketing_messages(status);
CREATE INDEX idx_marketing_messages_sent_at ON marketing_messages(sent_at);

-- Index pour customer_interactions
CREATE INDEX idx_customer_interactions_customer_id ON customer_interactions(customer_id);
CREATE INDEX idx_customer_interactions_campaign_id ON customer_interactions(campaign_id);
CREATE INDEX idx_customer_interactions_interaction_type ON customer_interactions(interaction_type);
CREATE INDEX idx_customer_interactions_interaction_date ON customer_interactions(interaction_date);

-- Index pour marketing_providers
CREATE INDEX idx_marketing_providers_company_id ON marketing_providers(company_id);
CREATE INDEX idx_marketing_providers_provider_type ON marketing_providers(provider_type);
CREATE INDEX idx_marketing_providers_is_active ON marketing_providers(is_active);

-- Index pour marketing_workflows
CREATE INDEX idx_marketing_workflows_company_id ON marketing_workflows(company_id);
CREATE INDEX idx_marketing_workflows_is_active ON marketing_workflows(is_active);

-- Index pour content_templates
CREATE INDEX idx_content_templates_company_id ON content_templates(company_id);
CREATE INDEX idx_content_templates_template_type ON content_templates(template_type);
CREATE INDEX idx_content_templates_is_active ON content_templates(is_active);

-- ==================== DONNÉES INITIALES ====================

-- Insertion des fournisseurs marketing par défaut
INSERT INTO marketing_providers (company_id, provider_type, platform_name, api_credentials, is_active) VALUES
(1, 'EMAIL', 'SENDGRID', '{"api_key": "", "from_email": ""}', true),
(1, 'EMAIL', 'MAILCHIMP', '{"api_key": "", "server": "us1"}', true),
(1, 'SMS', 'TWILIO', '{"account_sid": "", "auth_token": "", "phone_number": ""}', true),
(1, 'SMS', 'ORANGE', '{"client_id": "", "client_secret": ""}', true),
(1, 'SOCIAL', 'FACEBOOK', '{"app_id": "", "app_secret": ""}', true),
(1, 'SOCIAL', 'LINKEDIN', '{"client_id": "", "client_secret": ""}', true),
(1, 'CRM', 'HUBSPOT', '{"api_key": ""}', true),
(1, 'CRM', 'SALESFORCE', '{"client_id": "", "client_secret": ""}', true);

-- Insertion des templates de contenu par défaut
INSERT INTO content_templates (company_id, template_name, template_type, category, subject_template, content_template, variables) VALUES
(1, 'Email de bienvenue', 'EMAIL', 'ONBOARDING', 'Bienvenue chez {{company_name}}', 
 'Bonjour {{customer_name}},\n\nBienvenue chez {{company_name}} ! Nous sommes ravis de vous compter parmi nos clients.\n\nCordialement,\nL''équipe {{company_name}}', 
 '["company_name", "customer_name"]'),
(1, 'SMS de rappel', 'SMS', 'REMINDER', NULL, 
 'Bonjour {{customer_name}}, n''oubliez pas votre rendez-vous du {{appointment_date}} à {{appointment_time}}.', 
 '["customer_name", "appointment_date", "appointment_time"]'),
(1, 'Post promotionnel', 'SOCIAL', 'PROMOTION', NULL, 
 '🎉 Nouvelle offre exceptionnelle ! {{promotion_description}} Découvrez nos produits dès maintenant ! #{{company_name}} #promotion', 
 '["promotion_description", "company_name"]');

-- ==================== COMMENTAIRES ====================

COMMENT ON TABLE crm_customers IS 'Profils clients CRM avec intelligence artificielle';
COMMENT ON TABLE marketing_campaigns IS 'Campagnes marketing multi-canaux';
COMMENT ON TABLE marketing_messages IS 'Messages individuels des campagnes';
COMMENT ON TABLE marketing_providers IS 'Fournisseurs de services marketing (SendGrid, Twilio, etc.)';
COMMENT ON TABLE customer_interactions IS 'Interactions et engagements des clients';
COMMENT ON TABLE marketing_workflows IS 'Workflows d''automatisation marketing';
COMMENT ON TABLE content_templates IS 'Templates de contenu pour les campagnes';

COMMENT ON COLUMN crm_customers.ai_score IS 'Score d''intelligence artificielle (0-100)';
COMMENT ON COLUMN crm_customers.churn_probability IS 'Probabilité de désabonnement (0-1)';
COMMENT ON COLUMN crm_customers.lifetime_value_predicted IS 'Valeur vie client prédite';
COMMENT ON COLUMN crm_customers.customer_segment IS 'Segment client (VIP_HIGH_VALUE, STRATEGIC_ACCOUNT, etc.)';
COMMENT ON COLUMN crm_customers.payment_behavior IS 'Comportement de paiement (EARLY_PAYER, PROMPT_PAYER, etc.)';

COMMENT ON COLUMN marketing_campaigns.campaign_type IS 'Type de campagne (EMAIL, SMS, SOCIAL, MULTI_CHANNEL)';
COMMENT ON COLUMN marketing_campaigns.status IS 'Statut de la campagne (DRAFT, SCHEDULED, RUNNING, COMPLETED)';
COMMENT ON COLUMN marketing_campaigns.target_segments IS 'Segments ciblés (JSON array)';
COMMENT ON COLUMN marketing_campaigns.target_criteria IS 'Critères de ciblage avancés (JSON object)';

COMMENT ON COLUMN marketing_messages.channel IS 'Canal de communication (EMAIL, SMS, FACEBOOK, LINKEDIN)';
COMMENT ON COLUMN marketing_messages.status IS 'Statut du message (PENDING, SENT, DELIVERED, OPENED, CLICKED)';

COMMENT ON COLUMN customer_interactions.interaction_type IS 'Type d''interaction (EMAIL_OPEN, EMAIL_CLICK, SMS_RECEIVED, SOCIAL_ENGAGEMENT)';
COMMENT ON COLUMN customer_interactions.sentiment_score IS 'Score de sentiment (-1.0 à 1.0)';
COMMENT ON COLUMN customer_interactions.engagement_score IS 'Score d''engagement (0 à 100)';









