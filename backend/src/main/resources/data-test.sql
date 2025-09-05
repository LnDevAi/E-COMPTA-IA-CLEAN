-- =====================================================
-- BASE DE DONNÉES DE TEST E-COMPTA-IA
-- Données réelles pour le développement
-- =====================================================

-- Nettoyer les tables existantes
DELETE FROM ligne_ecriture WHERE id IS NOT NULL;
DELETE FROM ecriture_comptable WHERE id IS NOT NULL;
DELETE FROM template_ecriture WHERE id IS NOT NULL;
DELETE FROM account WHERE id IS NOT NULL;
DELETE FROM cost_center WHERE id IS NOT NULL;
DELETE FROM project WHERE id IS NOT NULL;
DELETE FROM company WHERE id IS NOT NULL;

-- =====================================================
-- 1. ENTREPRISES
-- =====================================================
INSERT INTO company (id, name, code, address, phone, email, currency, created_at, updated_at) VALUES
(1, 'SARL TECHNO-SERVICES', 'TS001', 'Avenue de la République, Dakar', '+221 33 123 45 67', 'contact@techno-services.sn', 'XOF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'ENTREPRISE COMMERCIALE MODERNE', 'ECM002', 'Rue du Commerce, Abidjan', '+225 20 234 56 78', 'info@ecm.ci', 'XOF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'SERVICES FINANCIERS AFRIQUE', 'SFA003', 'Boulevard de la Paix, Ouagadougou', '+226 25 345 67 89', 'admin@sfa.bf', 'XOF', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 2. PROJETS
-- =====================================================
INSERT INTO project (id, name, description, start_date, end_date, budget, status, company_id, created_at, updated_at) VALUES
(1, 'Développement Logiciel Comptable', 'Création d''un système de gestion comptable moderne', '2024-01-15', '2024-12-31', 5000000.00, 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Formation Personnel', 'Formation des employés aux nouvelles technologies', '2024-03-01', '2024-06-30', 1200000.00, 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Expansion Commerciale', 'Ouverture de nouvelles succursales', '2024-02-01', '2024-11-30', 8000000.00, 'PLANNED', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Audit Financier 2024', 'Audit complet des comptes annuels', '2024-01-01', '2024-03-31', 2500000.00, 'COMPLETED', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 3. CENTRES DE COÛT
-- =====================================================
INSERT INTO cost_center (id, name, code, description, company_id, created_at, updated_at) VALUES
(1, 'Direction Générale', 'DG', 'Direction et administration générale', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Développement IT', 'IT', 'Développement et maintenance informatique', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Ressources Humaines', 'RH', 'Gestion du personnel et formation', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Commercial', 'COM', 'Ventes et marketing', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Production', 'PROD', 'Fabrication et production', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Finance', 'FIN', 'Gestion financière et comptable', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 4. COMPTES COMPTABLES
-- =====================================================
INSERT INTO account (id, code, name, type, category, company_id, created_at, updated_at) VALUES
-- Actifs
(1, '101', 'Caisse', 'ASSET', 'CURRENT_ASSET', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, '102', 'Banque BNP Paribas', 'ASSET', 'CURRENT_ASSET', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, '103', 'Banque Société Générale', 'ASSET', 'CURRENT_ASSET', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, '201', 'Clients', 'ASSET', 'CURRENT_ASSET', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, '211', 'Clients douteux', 'ASSET', 'CURRENT_ASSET', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, '301', 'Stocks matières premières', 'ASSET', 'CURRENT_ASSET', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, '302', 'Stocks produits finis', 'ASSET', 'CURRENT_ASSET', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, '401', 'Immobilisations corporelles', 'ASSET', 'FIXED_ASSET', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, '402', 'Immobilisations incorporelles', 'ASSET', 'FIXED_ASSET', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Passifs
(10, '501', 'Fournisseurs', 'LIABILITY', 'CURRENT_LIABILITY', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, '502', 'Dettes fiscales', 'LIABILITY', 'CURRENT_LIABILITY', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, '503', 'Dettes sociales', 'LIABILITY', 'CURRENT_LIABILITY', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, '601', 'Emprunts bancaires', 'LIABILITY', 'LONG_TERM_LIABILITY', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, '701', 'Capital social', 'EQUITY', 'SHAREHOLDERS_EQUITY', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, '702', 'Réserves', 'EQUITY', 'SHAREHOLDERS_EQUITY', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, '703', 'Résultat de l''exercice', 'EQUITY', 'SHAREHOLDERS_EQUITY', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Charges
(17, '801', 'Achats matières premières', 'EXPENSE', 'OPERATING_EXPENSE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, '802', 'Variation stocks', 'EXPENSE', 'OPERATING_EXPENSE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, '803', 'Services extérieurs', 'EXPENSE', 'OPERATING_EXPENSE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, '804', 'Impôts et taxes', 'EXPENSE', 'OPERATING_EXPENSE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, '805', 'Charges de personnel', 'EXPENSE', 'OPERATING_EXPENSE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(22, '806', 'Dotations aux amortissements', 'EXPENSE', 'OPERATING_EXPENSE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(23, '807', 'Charges financières', 'EXPENSE', 'FINANCIAL_EXPENSE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(24, '808', 'Charges exceptionnelles', 'EXPENSE', 'EXCEPTIONAL_EXPENSE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Produits
(25, '901', 'Ventes de produits', 'REVENUE', 'OPERATING_REVENUE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(26, '902', 'Ventes de services', 'REVENUE', 'OPERATING_REVENUE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(27, '903', 'Produits financiers', 'REVENUE', 'FINANCIAL_REVENUE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(28, '904', 'Produits exceptionnels', 'REVENUE', 'EXCEPTIONAL_REVENUE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 5. TEMPLATES D'ÉCRITURES
-- =====================================================
INSERT INTO template_ecriture (id, name, description, company_id, created_at, updated_at) VALUES
(1, 'Vente au comptant', 'Écriture standard pour vente au comptant', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Vente à crédit', 'Écriture standard pour vente à crédit', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Achat fournisseur', 'Écriture standard pour achat fournisseur', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Paiement salaires', 'Écriture standard pour paiement des salaires', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Règlement client', 'Écriture standard pour règlement client', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 6. ÉCRITURES COMPTABLES (JANVIER 2024)
-- =====================================================
INSERT INTO ecriture_comptable (id, reference, description, date_ecriture, amount, type, status, company_id, project_id, cost_center_id, created_at, updated_at) VALUES
-- Ventes
(1, 'VTE-2024-001', 'Vente logiciel comptable - Client ABC', '2024-01-15', 1500000.00, 'SALE', 'VALIDATED', 1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'VTE-2024-002', 'Vente formation - Client XYZ', '2024-01-20', 800000.00, 'SALE', 'VALIDATED', 1, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'VTE-2024-003', 'Vente maintenance - Client DEF', '2024-01-25', 500000.00, 'SALE', 'PENDING', 1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Achats
(4, 'ACH-2024-001', 'Achat matériel informatique', '2024-01-10', 1200000.00, 'PURCHASE', 'VALIDATED', 1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'ACH-2024-002', 'Achat logiciels de développement', '2024-01-18', 600000.00, 'PURCHASE', 'VALIDATED', 1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'ACH-2024-003', 'Achat fournitures de bureau', '2024-01-22', 150000.00, 'PURCHASE', 'PENDING', 1, NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Charges de personnel
(7, 'SAL-2024-001', 'Paiement salaires janvier 2024', '2024-01-31', 2500000.00, 'SALARY', 'VALIDATED', 1, NULL, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'SAL-2024-002', 'Charges sociales janvier 2024', '2024-01-31', 500000.00, 'SOCIAL_CHARGES', 'VALIDATED', 1, NULL, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Autres charges
(9, 'CHG-2024-001', 'Électricité et eau', '2024-01-15', 180000.00, 'UTILITY', 'VALIDATED', 1, NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'CHG-2024-002', 'Assurance véhicules', '2024-01-20', 120000.00, 'INSURANCE', 'VALIDATED', 1, NULL, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- 7. LIGNES D'ÉCRITURES
-- =====================================================
-- Vente 1 (VTE-2024-001)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(1, 2, 1, 0.00, 1500000.00, 'Encaissement vente', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 25, 1, 1500000.00, 0.00, 'Vente de produits', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Vente 2 (VTE-2024-002)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(3, 4, 2, 0.00, 800000.00, 'Créance client', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 26, 2, 800000.00, 0.00, 'Vente de services', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Vente 3 (VTE-2024-003)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(5, 4, 3, 0.00, 500000.00, 'Créance client', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 26, 3, 500000.00, 0.00, 'Vente de services', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Achat 1 (ACH-2024-001)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(7, 8, 4, 1200000.00, 0.00, 'Achat matériel informatique', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 10, 4, 0.00, 1200000.00, 'Dette fournisseur', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Achat 2 (ACH-2024-002)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(9, 9, 5, 600000.00, 0.00, 'Achat logiciels', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 10, 5, 0.00, 600000.00, 'Dette fournisseur', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Achat 3 (ACH-2024-003)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(11, 19, 6, 150000.00, 0.00, 'Fournitures de bureau', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 10, 6, 0.00, 150000.00, 'Dette fournisseur', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Salaires (SAL-2024-001)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(13, 21, 7, 2500000.00, 0.00, 'Charges de personnel', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 2, 7, 0.00, 2500000.00, 'Paiement salaires', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Charges sociales (SAL-2024-002)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(15, 12, 8, 500000.00, 0.00, 'Charges sociales', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 2, 8, 0.00, 500000.00, 'Paiement charges sociales', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Électricité (CHG-2024-001)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(17, 19, 9, 180000.00, 0.00, 'Services extérieurs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 2, 9, 0.00, 180000.00, 'Paiement facture', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assurance (CHG-2024-002)
INSERT INTO ligne_ecriture (id, account_id, ecriture_id, debit, credit, description, created_at, updated_at) VALUES
(19, 19, 10, 120000.00, 0.00, 'Services extérieurs', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 2, 10, 0.00, 120000.00, 'Paiement assurance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- RÉSUMÉ DES DONNÉES CRÉÉES
-- =====================================================
-- 3 Entreprises
-- 4 Projets
-- 6 Centres de coût
-- 28 Comptes comptables
-- 5 Templates d'écritures
-- 10 Écritures comptables
-- 20 Lignes d'écritures
-- =====================================================
