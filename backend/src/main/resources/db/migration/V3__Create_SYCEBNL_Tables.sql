-- =====================================================
-- E-COMPTA-IA - SCRIPT DE CRÉATION DES TABLES SYCEBNL
-- Version: 3.0
-- Date: 2024-08-09
-- Description: Création des tables pour le module SYCEBNL-OHADA
-- =====================================================

-- Nécessaire pour les UUID générés par JPA
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm"; -- Pour les recherches textuelles avancées

-- ==================== TABLES SYCEBNL ====================

-- Table des organisations SYCEBNL
CREATE TABLE sycebnl_organizations (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL,
    
    -- Informations générales
    organization_name VARCHAR(255) NOT NULL,
    legal_form VARCHAR(100),
    registration_number VARCHAR(100),
    tax_identification_number VARCHAR(100),
    organization_type VARCHAR(50),
    
    -- Système comptable SYCEBNL
    accounting_system VARCHAR(50) NOT NULL,
    fiscal_year_start VARCHAR(10),
    fiscal_year_end VARCHAR(10),
    base_currency VARCHAR(10) NOT NULL,
    reporting_currency VARCHAR(10),
    
    -- Seuils SYCEBNL
    annual_revenue DECIMAL(15,2),
    employee_count INTEGER,
    total_assets DECIMAL(15,2),
    meets_normal_system_criteria BOOLEAN,
    
    -- Informations légales
    legal_address TEXT,
    headquarters_address TEXT,
    phone_number VARCHAR(50),
    email VARCHAR(255),
    website VARCHAR(255),
    
    -- Gestion des fonds
    fund_restriction_policy TEXT,
    donor_restriction_tracking BOOLEAN DEFAULT TRUE,
    temporarily_restricted_funds DECIMAL(15,2) DEFAULT 0.00,
    permanently_restricted_funds DECIMAL(15,2) DEFAULT 0.00,
    unrestricted_funds DECIMAL(15,2) DEFAULT 0.00,
    
    -- Conformité OHADA
    ohada_compliance_status VARCHAR(50),
    last_compliance_audit DATE,
    next_compliance_audit DATE,
    auditor_name VARCHAR(255),
    auditor_license_number VARCHAR(100),
    
    -- Configuration spécifique ONG
    mission_statement TEXT,
    program_areas JSONB DEFAULT '{}'::jsonb,
    geographic_scope JSONB DEFAULT '{}'::jsonb,
    beneficiary_count INTEGER,
    volunteer_count INTEGER,
    
    -- Métadonnées
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(255),
    approved_by VARCHAR(255),
    approval_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sycebnl_organization_company FOREIGN KEY (company_id) REFERENCES companies(id)
);

-- Index pour la performance
CREATE INDEX idx_sycebnl_organization_company ON sycebnl_organizations(company_id);
CREATE INDEX idx_sycebnl_organization_type ON sycebnl_organizations(organization_type);
CREATE INDEX idx_sycebnl_organization_system ON sycebnl_organizations(accounting_system);
CREATE INDEX idx_sycebnl_organization_compliance ON sycebnl_organizations(ohada_compliance_status);
CREATE INDEX idx_sycebnl_organization_audit ON sycebnl_organizations(next_compliance_audit);

-- Table des membres SYCEBNL
CREATE TABLE sycebnl_members (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    
    -- Informations personnelles
    member_number VARCHAR(100) UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(50),
    date_of_birth DATE,
    gender VARCHAR(20),
    nationality VARCHAR(100),
    id_number VARCHAR(100),
    id_type VARCHAR(50),
    
    -- Adresse
    address TEXT,
    city VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    region VARCHAR(100),
    
    -- Statut membre
    membership_status VARCHAR(50) NOT NULL,
    member_type VARCHAR(50),
    registration_date DATE NOT NULL,
    membership_expiry_date DATE,
    last_renewal_date DATE,
    
    -- Cotisations
    annual_contribution DECIMAL(10,2),
    contribution_frequency VARCHAR(50),
    total_contributions_paid DECIMAL(15,2) DEFAULT 0.00,
    outstanding_contributions DECIMAL(15,2) DEFAULT 0.00,
    last_contribution_date DATE,
    next_contribution_due DATE,
    
    -- Fonctions et responsabilités
    position_title VARCHAR(255),
    department VARCHAR(100),
    is_board_member BOOLEAN DEFAULT FALSE,
    is_volunteer BOOLEAN DEFAULT FALSE,
    volunteer_hours_per_month INTEGER,
    skills JSONB DEFAULT '{}'::jsonb,
    interests JSONB DEFAULT '{}'::jsonb,
    
    -- Analytics et CRM
    engagement_score INTEGER,
    lifetime_value DECIMAL(15,2) DEFAULT 0.00,
    churn_probability DECIMAL(5,4),
    member_segment VARCHAR(50),
    communication_preferences JSONB DEFAULT '{}'::jsonb,
    email_opt_in BOOLEAN DEFAULT TRUE,
    sms_opt_in BOOLEAN DEFAULT TRUE,
    language_preference VARCHAR(10) DEFAULT 'fr',
    
    -- Métadonnées
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sycebnl_member_organization FOREIGN KEY (organization_id) REFERENCES sycebnl_organizations(id)
);

-- Index pour la performance
CREATE INDEX idx_sycebnl_member_organization ON sycebnl_members(organization_id);
CREATE INDEX idx_sycebnl_member_status ON sycebnl_members(membership_status);
CREATE INDEX idx_sycebnl_member_type ON sycebnl_members(member_type);
CREATE INDEX idx_sycebnl_member_segment ON sycebnl_members(member_segment);
CREATE INDEX idx_sycebnl_member_engagement ON sycebnl_members(engagement_score DESC);

-- Table des comptes SYCEBNL
CREATE TABLE sycebnl_accounts (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    
    -- Informations du compte
    account_code VARCHAR(50) NOT NULL,
    account_name VARCHAR(255) NOT NULL,
    account_description TEXT,
    
    -- Classification SYCEBNL
    account_class INTEGER NOT NULL,
    account_category VARCHAR(50),
    account_type VARCHAR(50),
    
    -- Hiérarchie
    parent_account_id BIGINT,
    account_level INTEGER,
    is_group_account BOOLEAN DEFAULT FALSE,
    
    -- Solde et mouvements
    opening_debit_balance DECIMAL(19,2) DEFAULT 0.00,
    opening_credit_balance DECIMAL(19,2) DEFAULT 0.00,
    current_debit_balance DECIMAL(19,2) DEFAULT 0.00,
    current_credit_balance DECIMAL(19,2) DEFAULT 0.00,
    closing_debit_balance DECIMAL(19,2) DEFAULT 0.00,
    closing_credit_balance DECIMAL(19,2) DEFAULT 0.00,
    
    -- Restrictions et affectations
    is_restricted BOOLEAN DEFAULT FALSE,
    restriction_type VARCHAR(50),
    restriction_description TEXT,
    donor_restriction VARCHAR(255),
    time_restriction VARCHAR(255),
    purpose_restriction VARCHAR(255),
    
    -- Amortissements
    is_depreciable BOOLEAN DEFAULT FALSE,
    depreciation_method VARCHAR(50),
    useful_life_years INTEGER,
    depreciation_rate DECIMAL(5,2),
    accumulated_depreciation DECIMAL(19,2) DEFAULT 0.00,
    
    -- Configuration
    is_active BOOLEAN DEFAULT TRUE,
    is_system_account BOOLEAN DEFAULT FALSE,
    requires_approval BOOLEAN DEFAULT FALSE,
    is_cash_account BOOLEAN DEFAULT FALSE,
    is_bank_account BOOLEAN DEFAULT FALSE,
    
    -- Métadonnées
    created_by VARCHAR(255),
    approved_by VARCHAR(255),
    approval_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sycebnl_account_organization FOREIGN KEY (organization_id) REFERENCES sycebnl_organizations(id),
    CONSTRAINT fk_sycebnl_account_parent FOREIGN KEY (parent_account_id) REFERENCES sycebnl_accounts(id)
);

-- Index pour la performance
CREATE INDEX idx_sycebnl_account_organization ON sycebnl_accounts(organization_id);
CREATE INDEX idx_sycebnl_account_code ON sycebnl_accounts(account_code);
CREATE INDEX idx_sycebnl_account_class ON sycebnl_accounts(account_class);
CREATE INDEX idx_sycebnl_account_category ON sycebnl_accounts(account_category);
CREATE INDEX idx_sycebnl_account_parent ON sycebnl_accounts(parent_account_id);

-- Table des états financiers SYCEBNL
CREATE TABLE sycebnl_financial_statements (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    
    -- Période
    fiscal_year INTEGER NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    statement_type VARCHAR(50) NOT NULL, -- SN, SMT, BILAN, COMPTE_RESULTAT
    
    -- Métadonnées du rapport
    statement_date DATE NOT NULL,
    currency VARCHAR(10) NOT NULL,
    exchange_rate DECIMAL(10,4),
    
    -- Contenu du rapport
    statement_data JSONB NOT NULL,
    notes TEXT,
    
    -- Validation et approbation
    is_draft BOOLEAN DEFAULT TRUE,
    is_approved BOOLEAN DEFAULT FALSE,
    approved_by VARCHAR(255),
    approval_date TIMESTAMP,
    
    -- Métadonnées
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sycebnl_statement_organization FOREIGN KEY (organization_id) REFERENCES sycebnl_organizations(id)
);

-- Index pour la performance
CREATE INDEX idx_sycebnl_statement_organization ON sycebnl_financial_statements(organization_id);
CREATE INDEX idx_sycebnl_statement_year ON sycebnl_financial_statements(fiscal_year);
CREATE INDEX idx_sycebnl_statement_type ON sycebnl_financial_statements(statement_type);
CREATE INDEX idx_sycebnl_statement_date ON sycebnl_financial_statements(statement_date);

-- Table des budgets SYCEBNL
CREATE TABLE sycebnl_budgets (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    
    -- Période budgétaire
    budget_year INTEGER NOT NULL,
    budget_period_start DATE NOT NULL,
    budget_period_end DATE NOT NULL,
    budget_type VARCHAR(50) NOT NULL, -- ANNUAL, QUARTERLY, MONTHLY, PROJECT
    
    -- Informations du budget
    budget_name VARCHAR(255) NOT NULL,
    budget_description TEXT,
    total_budget_amount DECIMAL(19,2) NOT NULL,
    approved_budget_amount DECIMAL(19,2),
    
    -- Statut
    budget_status VARCHAR(50) DEFAULT 'DRAFT',
    is_approved BOOLEAN DEFAULT FALSE,
    approved_by VARCHAR(255),
    approval_date TIMESTAMP,
    
    -- Métadonnées
    created_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sycebnl_budget_organization FOREIGN KEY (organization_id) REFERENCES sycebnl_organizations(id)
);

-- Index pour la performance
CREATE INDEX idx_sycebnl_budget_organization ON sycebnl_budgets(organization_id);
CREATE INDEX idx_sycebnl_budget_year ON sycebnl_budgets(budget_year);
CREATE INDEX idx_sycebnl_budget_type ON sycebnl_budgets(budget_type);
CREATE INDEX idx_sycebnl_budget_status ON sycebnl_budgets(budget_status);

-- Table des lignes de budget
CREATE TABLE sycebnl_budget_lines (
    id BIGSERIAL PRIMARY KEY,
    budget_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    
    -- Montants budgétaires
    budgeted_amount DECIMAL(19,2) NOT NULL,
    approved_amount DECIMAL(19,2),
    actual_amount DECIMAL(19,2) DEFAULT 0.00,
    variance_amount DECIMAL(19,2) DEFAULT 0.00,
    variance_percentage DECIMAL(5,2) DEFAULT 0.00,
    
    -- Métadonnées
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sycebnl_budget_line_budget FOREIGN KEY (budget_id) REFERENCES sycebnl_budgets(id),
    CONSTRAINT fk_sycebnl_budget_line_account FOREIGN KEY (account_id) REFERENCES sycebnl_accounts(id)
);

-- Index pour la performance
CREATE INDEX idx_sycebnl_budget_line_budget ON sycebnl_budget_lines(budget_id);
CREATE INDEX idx_sycebnl_budget_line_account ON sycebnl_budget_lines(account_id);

-- Table des projets SYCEBNL
CREATE TABLE sycebnl_projects (
    id BIGSERIAL PRIMARY KEY,
    organization_id BIGINT NOT NULL,
    
    -- Informations du projet
    project_code VARCHAR(50) NOT NULL,
    project_name VARCHAR(255) NOT NULL,
    project_description TEXT,
    project_type VARCHAR(50),
    
    -- Période du projet
    project_start_date DATE NOT NULL,
    project_end_date DATE,
    project_duration_months INTEGER,
    
    -- Budget du projet
    total_budget DECIMAL(19,2) NOT NULL,
    approved_budget DECIMAL(19,2),
    spent_amount DECIMAL(19,2) DEFAULT 0.00,
    remaining_budget DECIMAL(19,2),
    
    -- Statut
    project_status VARCHAR(50) DEFAULT 'PLANNING',
    completion_percentage DECIMAL(5,2) DEFAULT 0.00,
    
    -- Données du projet
    project_data JSONB DEFAULT '{}'::jsonb,
    beneficiary_count INTEGER,
    geographic_scope JSONB DEFAULT '{}'::jsonb,
    
    -- Métadonnées
    created_by VARCHAR(255),
    project_manager VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_sycebnl_project_organization FOREIGN KEY (organization_id) REFERENCES sycebnl_organizations(id)
);

-- Index pour la performance
CREATE INDEX idx_sycebnl_project_organization ON sycebnl_projects(organization_id);
CREATE INDEX idx_sycebnl_project_code ON sycebnl_projects(project_code);
CREATE INDEX idx_sycebnl_project_status ON sycebnl_projects(project_status);
CREATE INDEX idx_sycebnl_project_dates ON sycebnl_projects(project_start_date, project_end_date);

-- ==================== DONNÉES INITIALES ====================

-- Insertion des types d'organisations par défaut
INSERT INTO sycebnl_organizations (
    company_id, organization_name, legal_form, organization_type, 
    accounting_system, base_currency, meets_normal_system_criteria,
    ohada_compliance_status, is_active, created_by
) VALUES 
(1, 'Organisation Exemple', 'Association', 'ASSOCIATION', 'MINIMAL', 'XOF', false, 'UNDER_REVIEW', true, 'system');

-- Insertion des comptes SYCEBNL de base (Système Minimal)
INSERT INTO sycebnl_accounts (
    organization_id, account_code, account_name, account_class, 
    account_category, account_type, account_level, is_group_account,
    is_active, created_by
) VALUES 
(1, '1', 'TRÉSORERIE', 1, 'ASSETS', 'ASSET', 1, true, true, 'system'),
(1, '11', 'Caisse', 1, 'ASSETS', 'ASSET', 2, false, true, 'system'),
(1, '12', 'Banque', 1, 'ASSETS', 'ASSET', 2, false, true, 'system'),
(1, '2', 'DETTES', 2, 'LIABILITIES', 'LIABILITY', 1, true, true, 'system'),
(1, '21', 'Dettes fournisseurs', 2, 'LIABILITIES', 'LIABILITY', 2, false, true, 'system'),
(1, '22', 'Dettes fiscales', 2, 'LIABILITIES', 'LIABILITY', 2, false, true, 'system'),
(1, '3', 'CAPITAUX PROPRES', 3, 'EQUITY', 'EQUITY', 1, true, true, 'system'),
(1, '31', 'Capital', 3, 'EQUITY', 'EQUITY', 2, false, true, 'system'),
(1, '32', 'Réserves', 3, 'EQUITY', 'EQUITY', 2, false, true, 'system'),
(1, '4', 'PRODUITS', 4, 'REVENUE', 'REVENUE', 1, true, true, 'system'),
(1, '41', 'Cotisations', 4, 'REVENUE', 'REVENUE', 2, false, true, 'system'),
(1, '42', 'Subventions', 4, 'REVENUE', 'REVENUE', 2, false, true, 'system'),
(1, '43', 'Dons', 4, 'REVENUE', 'REVENUE', 2, false, true, 'system'),
(1, '5', 'CHARGES', 5, 'EXPENSES', 'EXPENSE', 1, true, true, 'system'),
(1, '51', 'Personnel', 5, 'EXPENSES', 'EXPENSE', 2, false, true, 'system'),
(1, '52', 'Fonctionnement', 5, 'EXPENSES', 'EXPENSE', 2, false, true, 'system'),
(1, '53', 'Investissements', 5, 'EXPENSES', 'EXPENSE', 2, false, true, 'system');

-- Insertion d'un membre exemple
INSERT INTO sycebnl_members (
    organization_id, member_number, first_name, last_name, email,
    membership_status, member_type, registration_date, annual_contribution,
    contribution_frequency, is_active, created_by
) VALUES 
(1, 'MEM001', 'Jean', 'Dupont', 'jean.dupont@example.com',
 'ACTIVE', 'INDIVIDUAL', CURRENT_DATE, 5000.00, 'ANNUAL', true, 'system');

-- Insertion d'un budget exemple
INSERT INTO sycebnl_budgets (
    organization_id, budget_year, budget_period_start, budget_period_end,
    budget_type, budget_name, total_budget_amount, budget_status,
    is_active, created_by
) VALUES 
(1, EXTRACT(YEAR FROM CURRENT_DATE), 
 DATE_TRUNC('year', CURRENT_DATE), 
 DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year' - INTERVAL '1 day',
 'ANNUAL', 'Budget Annuel ' || EXTRACT(YEAR FROM CURRENT_DATE), 1000000.00, 'DRAFT', true, 'system');

-- Insertion d'un projet exemple
INSERT INTO sycebnl_projects (
    organization_id, project_code, project_name, project_description,
    project_type, project_start_date, project_end_date, total_budget,
    project_status, created_by
) VALUES 
(1, 'PROJ001', 'Projet Développement Communautaire', 'Projet de développement communautaire dans la région',
 'DEVELOPMENT', CURRENT_DATE, CURRENT_DATE + INTERVAL '12 months', 500000.00, 'PLANNING', 'system');

-- ==================== COMMENTAIRES ====================

COMMENT ON TABLE sycebnl_organizations IS 'Organisations à but non lucratif conformes SYCEBNL-OHADA';
COMMENT ON TABLE sycebnl_members IS 'Membres des organisations SYCEBNL avec CRM intégré';
COMMENT ON TABLE sycebnl_accounts IS 'Plan comptable SYCEBNL avec gestion des restrictions';
COMMENT ON TABLE sycebnl_financial_statements IS 'États financiers SN/SMT conformes OHADA';
COMMENT ON TABLE sycebnl_budgets IS 'Budgets des organisations avec suivi des variances';
COMMENT ON TABLE sycebnl_budget_lines IS 'Lignes de budget détaillées par compte';
COMMENT ON TABLE sycebnl_projects IS 'Projets des organisations avec suivi budgétaire';

COMMENT ON COLUMN sycebnl_organizations.accounting_system IS 'Système comptable: NORMAL, MINIMAL, TRANSITIONAL';
COMMENT ON COLUMN sycebnl_organizations.ohada_compliance_status IS 'Statut de conformité OHADA';
COMMENT ON COLUMN sycebnl_accounts.account_class IS 'Classe de compte SYCEBNL (1-7)';
COMMENT ON COLUMN sycebnl_accounts.is_restricted IS 'Compte avec restrictions de fonds';
COMMENT ON COLUMN sycebnl_financial_statements.statement_type IS 'Type: SN, SMT, BILAN, COMPTE_RESULTAT';









