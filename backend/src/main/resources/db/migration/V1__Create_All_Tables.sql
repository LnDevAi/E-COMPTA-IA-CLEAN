-- =====================================================
-- E-COMPTA-IA - SCRIPT DE CRÉATION DE TOUTES LES TABLES
-- Version: 1.0
-- Date: 2024
-- Description: Création complète de toutes les tables pour l'ERP E-COMPTA-IA
-- =====================================================

-- ==================== TABLES PRINCIPALES ====================

-- Table des entreprises
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    siret VARCHAR(14) UNIQUE,
    vat_number VARCHAR(20) UNIQUE,
    country_code VARCHAR(3),
    country_name VARCHAR(100),
    accounting_standard VARCHAR(20),
    ohada_system_type VARCHAR(10),
    currency VARCHAR(3),
    locale VARCHAR(10),
    business_type VARCHAR(50),
    address VARCHAR(100),
    city VARCHAR(50),
    postal_code VARCHAR(20),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(100),
    industry VARCHAR(50),
    annual_revenue DECIMAL(15,2),
    employee_count INTEGER,
    fiscal_year_start VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(20),
    updated_by VARCHAR(20)
);

-- Table des rôles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des utilisateurs
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    address VARCHAR(100),
    city VARCHAR(50),
    postal_code VARCHAR(20),
    country_code VARCHAR(3),
    department VARCHAR(50),
    position VARCHAR(50),
    employee_code VARCHAR(20),
    company_id BIGINT REFERENCES companies(id),
    gender VARCHAR(20),
    base_salary DECIMAL(10,2),
    salary_currency VARCHAR(3),
    contract_type VARCHAR(20),
    hire_date TIMESTAMP,
    termination_date TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    is_admin BOOLEAN DEFAULT false,
    last_login_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(20),
    updated_by VARCHAR(20)
);

-- Table de liaison utilisateurs-rôles
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- ==================== MODULE COMPTABILITÉ ====================

-- Table des comptes comptables
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    account_class VARCHAR(30) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    opening_balance DECIMAL(15,2) DEFAULT 0,
    currency VARCHAR(3) DEFAULT 'XOF',
    company_id BIGINT REFERENCES companies(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des écritures comptables
CREATE TABLE journal_entries (
    id BIGSERIAL PRIMARY KEY,
    entry_number VARCHAR(50) NOT NULL,
    entry_date DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    journal_type VARCHAR(20) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    total_debit DECIMAL(15,2) NOT NULL,
    total_credit DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    country_code VARCHAR(3) NOT NULL,
    accounting_standard VARCHAR(20) NOT NULL,
    reference VARCHAR(100),
    document_number VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    validated_by BIGINT REFERENCES users(id),
    validated_at TIMESTAMP,
    is_reconciled BOOLEAN DEFAULT false,
    is_posted BOOLEAN DEFAULT false
);

-- Table des lignes d'écriture
CREATE TABLE account_entries (
    id BIGSERIAL PRIMARY KEY,
    journal_entry_id BIGINT NOT NULL REFERENCES journal_entries(id) ON DELETE CASCADE,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    company_id BIGINT NOT NULL REFERENCES companies(id),
    description VARCHAR(255),
    debit_amount DECIMAL(15,2) DEFAULT 0,
    credit_amount DECIMAL(15,2) DEFAULT 0,
    currency VARCHAR(3) NOT NULL,
    exchange_rate DECIMAL(10,6) DEFAULT 1,
    reference VARCHAR(100),
    reconciled_by BIGINT REFERENCES users(id),
    reconciled_at TIMESTAMP,
    is_reconciled BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== MODULE RH ====================

-- Table des employés
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    employee_number VARCHAR(20) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone_number VARCHAR(20),
    address VARCHAR(100),
    city VARCHAR(50),
    postal_code VARCHAR(20),
    country_code VARCHAR(3),
    department VARCHAR(50),
    position VARCHAR(50),
    hire_date DATE NOT NULL,
    termination_date DATE,
    contract_type VARCHAR(20),
    base_salary DECIMAL(10,2),
    salary_currency VARCHAR(3),
    is_active BOOLEAN DEFAULT true,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(20),
    updated_by VARCHAR(20)
);

-- Table des paies
CREATE TABLE payrolls (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    pay_period_start DATE NOT NULL,
    pay_period_end DATE NOT NULL,
    gross_salary DECIMAL(10,2) NOT NULL,
    net_salary DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT',
    payment_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(20),
    updated_by VARCHAR(20)
);

-- Table des congés
CREATE TABLE leaves (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    leave_type VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_requested INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    reason TEXT,
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== MODULE GESTION TIERS ====================

-- Table des tiers (clients/fournisseurs)
CREATE TABLE third_parties (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- CUSTOMER, SUPPLIER, BOTH
    email VARCHAR(100),
    phone_number VARCHAR(20),
    address VARCHAR(100),
    city VARCHAR(50),
    postal_code VARCHAR(20),
    country_code VARCHAR(3),
    vat_number VARCHAR(20),
    registration_number VARCHAR(20),
    is_active BOOLEAN DEFAULT true,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(20),
    updated_by VARCHAR(20)
);

-- ==================== MODULE INVENTAIRE ====================

-- Table des articles
CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,
    item_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    unit_of_measure VARCHAR(20),
    unit_cost DECIMAL(10,2),
    selling_price DECIMAL(10,2),
    currency VARCHAR(3),
    min_stock_level INTEGER DEFAULT 0,
    max_stock_level INTEGER,
    current_stock INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(20),
    updated_by VARCHAR(20)
);

-- Table des mouvements d'inventaire
CREATE TABLE inventory_movements (
    id BIGSERIAL PRIMARY KEY,
    inventory_id BIGINT NOT NULL REFERENCES inventory(id),
    movement_type VARCHAR(20) NOT NULL, -- IN, OUT, TRANSFER, ADJUSTMENT
    quantity INTEGER NOT NULL,
    unit_cost DECIMAL(10,2),
    total_cost DECIMAL(15,2),
    reference VARCHAR(100),
    notes TEXT,
    movement_date DATE NOT NULL,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(20)
);

-- ==================== MODULE ABONNEMENTS ====================

-- Table des plans d'abonnement
CREATE TABLE subscription_plans (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    billing_cycle VARCHAR(20) NOT NULL, -- MONTHLY, YEARLY
    max_users INTEGER,
    max_companies INTEGER,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des fonctionnalités des plans
CREATE TABLE plan_features (
    id BIGSERIAL PRIMARY KEY,
    plan_id BIGINT NOT NULL REFERENCES subscription_plans(id) ON DELETE CASCADE,
    feature_name VARCHAR(100) NOT NULL,
    feature_value VARCHAR(255),
    is_enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des abonnements d'entreprise
CREATE TABLE company_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    plan_id BIGINT NOT NULL REFERENCES subscription_plans(id),
    start_date DATE NOT NULL,
    end_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    auto_renew BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des paiements d'abonnement
CREATE TABLE subscription_payments (
    id BIGSERIAL PRIMARY KEY,
    subscription_id BIGINT NOT NULL REFERENCES company_subscriptions(id),
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(20),
    transaction_id VARCHAR(100),
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== MODULE RAPPORTING ====================

-- Table des états financiers
CREATE TABLE financial_reports (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    report_type VARCHAR(50) NOT NULL,
    report_name VARCHAR(100) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT',
    generated_at TIMESTAMP,
    generated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des métriques
CREATE TABLE metrics (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15,2),
    metric_unit VARCHAR(20),
    calculation_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== MODULE SÉCURITÉ ====================

-- Table des audits de sécurité
CREATE TABLE security_audits (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    event_category VARCHAR(50) NOT NULL,
    description TEXT,
    risk_level VARCHAR(20) NOT NULL,
    success BOOLEAN NOT NULL,
    user_id BIGINT REFERENCES users(id),
    ip_address VARCHAR(45),
    user_agent TEXT,
    details JSONB,
    entreprise_id BIGINT REFERENCES companies(id),
    audit_status VARCHAR(20) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des politiques de sécurité
CREATE TABLE security_policies (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    policy_name VARCHAR(100) NOT NULL,
    policy_type VARCHAR(50) NOT NULL,
    policy_content TEXT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(20),
    updated_by VARCHAR(20)
);

-- ==================== MODULE INTERNATIONALISATION ====================

-- Table des paramètres de localisation
CREATE TABLE locale_settings (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    language VARCHAR(10) NOT NULL,
    country VARCHAR(10) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    date_format VARCHAR(20),
    number_format VARCHAR(20),
    timezone VARCHAR(50),
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des configurations par pays
CREATE TABLE country_configs (
    id BIGSERIAL PRIMARY KEY,
    country_code VARCHAR(3) NOT NULL UNIQUE,
    country_name VARCHAR(100) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    accounting_standard VARCHAR(20),
    tax_system VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== MODULE PROTECTION DES DONNÉES ====================

-- Table des configurations de protection des données
CREATE TABLE data_protection (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    regulation_type VARCHAR(20) NOT NULL, -- GDPR, CCPA, etc.
    data_controller_name VARCHAR(100),
    data_controller_email VARCHAR(100),
    privacy_policy_url VARCHAR(255),
    data_retention_days INTEGER,
    consent_required BOOLEAN DEFAULT true,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des enregistrements de consentement
CREATE TABLE consent_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    email VARCHAR(100) NOT NULL,
    consent_type VARCHAR(50) NOT NULL,
    consent_given BOOLEAN NOT NULL,
    consent_date TIMESTAMP NOT NULL,
    consent_method VARCHAR(50),
    withdrawal_date TIMESTAMP,
    withdrawal_method VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des activités de traitement des données
CREATE TABLE data_processing_activities (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    activity_name VARCHAR(100) NOT NULL,
    purpose VARCHAR(255),
    legal_basis VARCHAR(100),
    data_categories TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== MODULE INTÉGRATIONS EXTERNES ====================

-- Table des comptes bancaires
CREATE TABLE bank_accounts (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    account_number VARCHAR(50) NOT NULL,
    bank_code VARCHAR(10) NOT NULL,
    bank_name VARCHAR(100),
    account_type VARCHAR(20),
    currency VARCHAR(3) NOT NULL,
    opening_balance DECIMAL(15,2) DEFAULT 0,
    current_balance DECIMAL(15,2) DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    last_reconciliation_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des réconciliations
CREATE TABLE reconciliations (
    id BIGSERIAL PRIMARY KEY,
    bank_account_id BIGINT NOT NULL REFERENCES bank_accounts(id),
    reconciliation_date DATE NOT NULL,
    opening_balance DECIMAL(15,2) NOT NULL,
    closing_balance DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(20)
);

-- ==================== MODULE WORKFLOW ====================

-- Table des workflows
CREATE TABLE workflows (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    workflow_type VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des instances de workflow
CREATE TABLE workflow_instances (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL REFERENCES workflows(id),
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    current_step VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDING',
    started_by BIGINT REFERENCES users(id),
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

-- Table des approbations de workflow
CREATE TABLE workflow_approvals (
    id BIGSERIAL PRIMARY KEY,
    workflow_instance_id BIGINT NOT NULL REFERENCES workflow_instances(id),
    approver_id BIGINT NOT NULL REFERENCES users(id),
    approval_step VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    comments TEXT,
    approved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== MODULE NOTIFICATIONS ====================

-- Table des notifications
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    is_read BOOLEAN DEFAULT false,
    priority VARCHAR(20) DEFAULT 'NORMAL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des préférences de notification
CREATE TABLE notification_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    notification_type VARCHAR(50) NOT NULL,
    email_enabled BOOLEAN DEFAULT true,
    push_enabled BOOLEAN DEFAULT true,
    sms_enabled BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== MODULE AUDIT ====================

-- Table des logs d'audit
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(20) NOT NULL, -- CREATE, UPDATE, DELETE
    old_values JSONB,
    new_values JSONB,
    user_id BIGINT REFERENCES users(id),
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== MODULE TÂCHES AUTOMATISÉES ====================

-- Table des tâches automatisées
CREATE TABLE automated_tasks (
    id BIGSERIAL PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL,
    task_type VARCHAR(50) NOT NULL,
    cron_expression VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    last_execution TIMESTAMP,
    next_execution TIMESTAMP,
    execution_count INTEGER DEFAULT 0,
    company_id BIGINT REFERENCES companies(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== INDEX POUR PERFORMANCE ====================

-- Index sur les clés étrangères
CREATE INDEX idx_users_company_id ON users(company_id);
CREATE INDEX idx_journal_entries_company_id ON journal_entries(company_id);
CREATE INDEX idx_journal_entries_created_by ON journal_entries(created_by);
CREATE INDEX idx_journal_entries_validated_by ON journal_entries(validated_by);
CREATE INDEX idx_account_entries_journal_entry_id ON account_entries(journal_entry_id);
CREATE INDEX idx_account_entries_account_id ON account_entries(account_id);
CREATE INDEX idx_account_entries_company_id ON account_entries(company_id);
CREATE INDEX idx_employees_company_id ON employees(company_id);
CREATE INDEX idx_payrolls_employee_id ON payrolls(employee_id);
CREATE INDEX idx_leaves_employee_id ON leaves(employee_id);
CREATE INDEX idx_third_parties_company_id ON third_parties(company_id);
CREATE INDEX idx_inventory_company_id ON inventory(company_id);
CREATE INDEX idx_inventory_movements_inventory_id ON inventory_movements(inventory_id);
CREATE INDEX idx_inventory_movements_company_id ON inventory_movements(company_id);
CREATE INDEX idx_company_subscriptions_company_id ON company_subscriptions(company_id);
CREATE INDEX idx_company_subscriptions_plan_id ON company_subscriptions(plan_id);
CREATE INDEX idx_subscription_payments_subscription_id ON subscription_payments(subscription_id);
CREATE INDEX idx_financial_reports_company_id ON financial_reports(company_id);
CREATE INDEX idx_metrics_company_id ON metrics(company_id);
CREATE INDEX idx_security_audits_user_id ON security_audits(user_id);
CREATE INDEX idx_security_audits_entreprise_id ON security_audits(entreprise_id);
CREATE INDEX idx_security_policies_company_id ON security_policies(company_id);
CREATE INDEX idx_locale_settings_company_id ON locale_settings(company_id);
CREATE INDEX idx_data_protection_company_id ON data_protection(company_id);
CREATE INDEX idx_consent_records_user_id ON consent_records(user_id);
CREATE INDEX idx_data_processing_activities_company_id ON data_processing_activities(company_id);
CREATE INDEX idx_bank_accounts_company_id ON bank_accounts(company_id);
CREATE INDEX idx_reconciliations_bank_account_id ON reconciliations(bank_account_id);
CREATE INDEX idx_workflows_company_id ON workflows(company_id);
CREATE INDEX idx_workflow_instances_workflow_id ON workflow_instances(workflow_id);
CREATE INDEX idx_workflow_instances_entity_id ON workflow_instances(entity_id);
CREATE INDEX idx_workflow_approvals_workflow_instance_id ON workflow_approvals(workflow_instance_id);
CREATE INDEX idx_workflow_approvals_approver_id ON workflow_approvals(approver_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notification_preferences_user_id ON notification_preferences(user_id);
CREATE INDEX idx_audit_logs_entity_id ON audit_logs(entity_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_automated_tasks_company_id ON automated_tasks(company_id);

-- Index sur les champs de recherche fréquents
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_companies_name ON companies(name);
CREATE INDEX idx_companies_siret ON companies(siret);
CREATE INDEX idx_accounts_account_number ON accounts(account_number);
CREATE INDEX idx_journal_entries_entry_number ON journal_entries(entry_number);
CREATE INDEX IF NOT EXISTS idx_journal_entries_entry_date ON journal_entries(entry_date);
CREATE INDEX idx_employees_employee_number ON employees(employee_number);
CREATE INDEX idx_third_parties_name ON third_parties(name);
CREATE INDEX idx_inventory_item_code ON inventory(item_code);
CREATE INDEX idx_inventory_name ON inventory(name);

-- Index sur les champs de date pour les requêtes temporelles
CREATE INDEX IF NOT EXISTS idx_journal_entries_entry_date ON journal_entries(entry_date);
CREATE INDEX idx_payrolls_pay_period_start ON payrolls(pay_period_start);
CREATE INDEX idx_leaves_start_date ON leaves(start_date);
CREATE INDEX idx_inventory_movements_movement_date ON inventory_movements(movement_date);
CREATE INDEX idx_financial_reports_period_start ON financial_reports(period_start);
CREATE INDEX idx_metrics_calculation_date ON metrics(calculation_date);
CREATE INDEX idx_security_audits_created_at ON security_audits(created_at);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);

-- ==================== CONTRAINTES DE DONNÉES ====================

-- Contraintes de validation
ALTER TABLE journal_entries ADD CONSTRAINT chk_journal_entries_total_balance 
    CHECK (total_debit = total_credit);

ALTER TABLE account_entries ADD CONSTRAINT chk_account_entries_amount 
    CHECK (debit_amount >= 0 AND credit_amount >= 0);

ALTER TABLE employees ADD CONSTRAINT chk_employees_hire_date 
    CHECK (hire_date <= COALESCE(termination_date, CURRENT_DATE));

ALTER TABLE leaves ADD CONSTRAINT chk_leaves_dates 
    CHECK (start_date <= end_date);

ALTER TABLE inventory_movements ADD CONSTRAINT chk_inventory_movements_quantity 
    CHECK (quantity != 0);

-- ==================== DONNÉES INITIALES ====================

-- Insertion des rôles de base
-- Données minimales pour la cohérence des migrations dépendantes
INSERT INTO companies (id, name, country_code, currency, is_active)
VALUES (1, 'Entreprise Démo', 'FR', 'EUR', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Administrateur système'),
('ACCOUNTANT', 'Comptable'),
('MANAGER', 'Gestionnaire'),
('EMPLOYEE', 'Employé'),
('VIEWER', 'Lecteur seul');

-- Insertion des standards comptables par pays
INSERT INTO country_configs (country_code, country_name, currency, accounting_standard, tax_system) VALUES 
('FR', 'France', 'EUR', 'PCG_FR', 'VAT'),
('US', 'États-Unis', 'USD', 'GAAP', 'SALES_TAX'),
('GB', 'Royaume-Uni', 'GBP', 'IFRS', 'VAT'),
('DE', 'Allemagne', 'EUR', 'IFRS', 'VAT'),
('SN', 'Sénégal', 'XOF', 'SYSCOHADA', 'VAT'),
('CI', 'Côte d''Ivoire', 'XOF', 'SYSCOHADA', 'VAT'),
('CM', 'Cameroun', 'XAF', 'SYSCOHADA', 'VAT'),
('BF', 'Burkina Faso', 'XOF', 'SYSCOHADA', 'VAT'),
('ML', 'Mali', 'XOF', 'SYSCOHADA', 'VAT'),
('NE', 'Niger', 'XOF', 'SYSCOHADA', 'VAT'),
('TD', 'Tchad', 'XAF', 'SYSCOHADA', 'VAT'),
('CF', 'République Centrafricaine', 'XAF', 'SYSCOHADA', 'VAT'),
('CG', 'Congo', 'XAF', 'SYSCOHADA', 'VAT'),
('CD', 'République Démocratique du Congo', 'CDF', 'SYSCOHADA', 'VAT'),
('GA', 'Gabon', 'XAF', 'SYSCOHADA', 'VAT'),
('GQ', 'Guinée Équatoriale', 'XAF', 'SYSCOHADA', 'VAT'),
('GW', 'Guinée-Bissau', 'XOF', 'SYSCOHADA', 'VAT'),
('GN', 'Guinée', 'GNF', 'SYSCOHADA', 'VAT'),
('BJ', 'Bénin', 'XOF', 'SYSCOHADA', 'VAT'),
('TG', 'Togo', 'XOF', 'SYSCOHADA', 'VAT');

-- Insertion des plans d'abonnement de base
INSERT INTO subscription_plans (name, description, price, currency, billing_cycle, max_users, max_companies) VALUES 
('BASIC', 'Plan de base pour petites entreprises', 29.99, 'EUR', 'MONTHLY', 5, 1),
('PROFESSIONAL', 'Plan professionnel pour moyennes entreprises', 79.99, 'EUR', 'MONTHLY', 25, 3),
('ENTERPRISE', 'Plan entreprise pour grandes organisations', 199.99, 'EUR', 'MONTHLY', 100, 10),
('BASIC_YEARLY', 'Plan de base annuel', 299.99, 'EUR', 'YEARLY', 5, 1),
('PROFESSIONAL_YEARLY', 'Plan professionnel annuel', 799.99, 'EUR', 'YEARLY', 25, 3),
('ENTERPRISE_YEARLY', 'Plan entreprise annuel', 1999.99, 'EUR', 'YEARLY', 100, 10);

-- ==================== TRIGGERS POUR MISE À JOUR AUTOMATIQUE ====================

-- Fonction pour mettre à jour updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers pour updated_at
CREATE TRIGGER update_companies_updated_at BEFORE UPDATE ON companies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_roles_updated_at BEFORE UPDATE ON roles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_accounts_updated_at BEFORE UPDATE ON accounts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_journal_entries_updated_at BEFORE UPDATE ON journal_entries FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_account_entries_updated_at BEFORE UPDATE ON account_entries FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_employees_updated_at BEFORE UPDATE ON employees FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_payrolls_updated_at BEFORE UPDATE ON payrolls FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_leaves_updated_at BEFORE UPDATE ON leaves FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_third_parties_updated_at BEFORE UPDATE ON third_parties FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_inventory_updated_at BEFORE UPDATE ON inventory FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_subscription_plans_updated_at BEFORE UPDATE ON subscription_plans FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_company_subscriptions_updated_at BEFORE UPDATE ON company_subscriptions FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_financial_reports_updated_at BEFORE UPDATE ON financial_reports FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_security_policies_updated_at BEFORE UPDATE ON security_policies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_locale_settings_updated_at BEFORE UPDATE ON locale_settings FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_data_protection_updated_at BEFORE UPDATE ON data_protection FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_data_processing_activities_updated_at BEFORE UPDATE ON data_processing_activities FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_bank_accounts_updated_at BEFORE UPDATE ON bank_accounts FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_workflows_updated_at BEFORE UPDATE ON workflows FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_notification_preferences_updated_at BEFORE UPDATE ON notification_preferences FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_automated_tasks_updated_at BEFORE UPDATE ON automated_tasks FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ==================== COMMENTAIRES SUR LES TABLES ====================

COMMENT ON TABLE companies IS 'Table des entreprises utilisant le système';
COMMENT ON TABLE users IS 'Table des utilisateurs du système';
COMMENT ON TABLE roles IS 'Table des rôles utilisateur';
COMMENT ON TABLE user_roles IS 'Table de liaison utilisateurs-rôles';
COMMENT ON TABLE accounts IS 'Table des comptes comptables';
COMMENT ON TABLE journal_entries IS 'Table des écritures comptables';
COMMENT ON TABLE account_entries IS 'Table des lignes d''écriture comptable';
COMMENT ON TABLE employees IS 'Table des employés';
COMMENT ON TABLE payrolls IS 'Table des paies';
COMMENT ON TABLE leaves IS 'Table des congés';
COMMENT ON TABLE third_parties IS 'Table des tiers (clients/fournisseurs)';
COMMENT ON TABLE inventory IS 'Table des articles en stock';
COMMENT ON TABLE inventory_movements IS 'Table des mouvements de stock';
COMMENT ON TABLE subscription_plans IS 'Table des plans d''abonnement';
COMMENT ON TABLE plan_features IS 'Table des fonctionnalités des plans';
COMMENT ON TABLE company_subscriptions IS 'Table des abonnements d''entreprise';
COMMENT ON TABLE subscription_payments IS 'Table des paiements d''abonnement';
COMMENT ON TABLE financial_reports IS 'Table des états financiers';
COMMENT ON TABLE metrics IS 'Table des métriques de performance';
COMMENT ON TABLE security_audits IS 'Table des audits de sécurité';
COMMENT ON TABLE security_policies IS 'Table des politiques de sécurité';
COMMENT ON TABLE locale_settings IS 'Table des paramètres de localisation';
COMMENT ON TABLE country_configs IS 'Table des configurations par pays';
COMMENT ON TABLE data_protection IS 'Table des configurations de protection des données';
COMMENT ON TABLE consent_records IS 'Table des enregistrements de consentement';
COMMENT ON TABLE data_processing_activities IS 'Table des activités de traitement des données';
COMMENT ON TABLE bank_accounts IS 'Table des comptes bancaires';
COMMENT ON TABLE reconciliations IS 'Table des réconciliations bancaires';
COMMENT ON TABLE workflows IS 'Table des workflows';
COMMENT ON TABLE workflow_instances IS 'Table des instances de workflow';
COMMENT ON TABLE workflow_approvals IS 'Table des approbations de workflow';
COMMENT ON TABLE notifications IS 'Table des notifications';
COMMENT ON TABLE notification_preferences IS 'Table des préférences de notification';
COMMENT ON TABLE audit_logs IS 'Table des logs d''audit';
COMMENT ON TABLE automated_tasks IS 'Table des tâches automatisées';

-- ==================== FIN DU SCRIPT ====================








