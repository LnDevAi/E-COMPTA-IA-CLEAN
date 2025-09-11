-- Données de test pour la fonctionnalité Pièce Justificative SYCEBNL
-- Insertion des données de base nécessaires

-- 1. Entreprises de test
INSERT INTO companies (id, name, country_code, accounting_standard, is_active, created_at, updated_at) VALUES
(1, 'ENTREPRISE TEST SYCEBNL', 'SN', 'SYCEBNL', true, NOW(), NOW()),
(2, 'ONG EXEMPLE', 'SN', 'SYCEBNL', true, NOW(), NOW());

-- 2. Utilisateurs de test
INSERT INTO users (id, username, email, first_name, last_name, password, is_active, is_admin, created_at, updated_at) VALUES
(1, 'admin_sycebnl', 'admin@sycebnl.sn', 'Admin', 'SYCEBNL', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', true, true, NOW(), NOW()),
(2, 'comptable1', 'comptable1@sycebnl.sn', 'Comptable', 'Un', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', true, false, NOW(), NOW()),
(3, 'validateur1', 'validateur1@sycebnl.sn', 'Validateur', 'Un', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', true, false, NOW(), NOW());

-- 3. Exercices comptables
INSERT INTO financial_periods (id, company_id, period_name, start_date, end_date, status, is_current, is_locked, created_at, updated_at) VALUES
(1, 1, 'Exercice 2024', '2024-01-01', '2024-12-31', 'OPEN', true, false, NOW(), NOW()),
(2, 2, 'Exercice 2024', '2024-01-01', '2024-12-31', 'OPEN', true, false, NOW(), NOW());

-- 4. Plan comptable SYCEBNL de base
INSERT INTO accounts (id, company_id, account_number, name, description, type, account_class, currency, is_active, created_at, updated_at) VALUES
-- Comptes de charges (6)
(1, 1, '601000', 'Achats de matières premières', 'Achats de matières premières', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),
(2, 1, '602000', 'Achats de fournitures', 'Achats de fournitures', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),
(3, 1, '603000', 'Achats de marchandises', 'Achats de marchandises', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),
(4, 1, '604000', 'Variations de stocks', 'Variations de stocks', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),
(5, 1, '605000', 'Autres achats', 'Autres achats', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),
(6, 1, '606000', 'Services extérieurs', 'Services extérieurs', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),
(7, 1, '607000', 'Sous-traitance', 'Sous-traitance', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),
(8, 1, '608000', 'Frais de transport', 'Frais de transport', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),
(9, 1, '609000', 'Frais de déplacement', 'Frais de déplacement', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),
(10, 1, '610000', 'Frais de communication', 'Frais de communication', 'EXPENSE', 'CHARGE', 'XOF', true, NOW(), NOW()),

-- Comptes de fournisseurs (401)
(11, 1, '401000', 'Fournisseurs', 'Fournisseurs', 'LIABILITY', 'TIERS', 'XOF', true, NOW(), NOW()),
(12, 1, '401001', 'Fournisseur SARL EXEMPLE', 'Fournisseur SARL EXEMPLE', 'LIABILITY', 'TIERS', 'XOF', true, NOW(), NOW()),
(13, 1, '401002', 'Fournisseur SERVICES', 'Fournisseur SERVICES', 'LIABILITY', 'TIERS', 'XOF', true, NOW(), NOW()),

-- Comptes de clients (411)
(14, 1, '411000', 'Clients', 'Clients', 'ASSET', 'TIERS', 'XOF', true, NOW(), NOW()),
(15, 1, '411001', 'Client ENTREPRISE A', 'Client ENTREPRISE A', 'ASSET', 'TIERS', 'XOF', true, NOW(), NOW()),
(16, 1, '411002', 'Client ENTREPRISE B', 'Client ENTREPRISE B', 'ASSET', 'TIERS', 'XOF', true, NOW(), NOW()),

-- Comptes de TVA (445)
(17, 1, '445000', 'TVA à décaisser', 'TVA à décaisser', 'LIABILITY', 'TAX', 'XOF', true, NOW(), NOW()),
(18, 1, '445001', 'TVA déductible', 'TVA déductible', 'ASSET', 'TAX', 'XOF', true, NOW(), NOW()),
(19, 1, '445002', 'TVA collectée', 'TVA collectée', 'LIABILITY', 'TAX', 'XOF', true, NOW(), NOW()),

-- Comptes de trésorerie (5)
(20, 1, '512000', 'Banque', 'Banque', 'ASSET', 'TRESORERIE', 'XOF', true, NOW(), NOW()),
(21, 1, '512001', 'Banque BICIS', 'Banque BICIS', 'ASSET', 'TRESORERIE', 'XOF', true, NOW(), NOW()),
(22, 1, '530000', 'Caisse', 'Caisse', 'ASSET', 'TRESORERIE', 'XOF', true, NOW(), NOW()),

-- Comptes d'engagements (408)
(23, 1, '408000', 'Engagements', 'Engagements', 'LIABILITY', 'ENGAGEMENT', 'XOF', true, NOW(), NOW()),
(24, 1, '408001', 'Engagements fournisseurs', 'Engagements fournisseurs', 'LIABILITY', 'ENGAGEMENT', 'XOF', true, NOW(), NOW());

-- 5. Documents GED de test
INSERT INTO ged_documents (id, document_code, title, description, file_name, file_path, file_size, file_type, mime_type, document_type, category, tags, version, is_current_version, status, security_level, company_id, country_code, accounting_standard, created_at, updated_at, created_by) VALUES
(1, 'PJ-1-20240115120000-ABC12345', 'Facture fournisseur SARL EXEMPLE', 'Facture fournisseur pour prestations de service', 'facture_sarl_exemple_001.pdf', '/uploads/pj/PJ-1-20240115120000-ABC12345/facture_sarl_exemple_001.pdf', 245760, 'PDF', 'application/pdf', 'INVOICE', 'FACTURE_FOURNISSEUR', 'facture,fournisseur,prestation', 1, true, 'APPROVED', 'INTERNAL', 1, 'SN', 'SYCEBNL', NOW(), NOW(), 1),
(2, 'PJ-2-20240115130000-DEF67890', 'Reçu client ENTREPRISE A', 'Reçu de règlement client', 'recu_client_entreprise_a_001.pdf', '/uploads/pj/PJ-2-20240115130000-DEF67890/recu_client_entreprise_a_001.pdf', 189440, 'PDF', 'application/pdf', 'RECEIPT', 'RECU_CLIENT', 'recu,client,reglement', 1, true, 'APPROVED', 'INTERNAL', 1, 'SN', 'SYCEBNL', NOW(), NOW(), 1),
(3, 'PJ-3-20240115140000-GHI11111', 'Bon de commande fournisseur', 'Bon de commande pour matériel', 'bon_commande_materiel_001.pdf', '/uploads/pj/PJ-3-20240115140000-GHI11111/bon_commande_materiel_001.pdf', 156320, 'PDF', 'application/pdf', 'CONTRACT', 'BON_COMMANDE', 'commande,fournisseur,materiel', 1, true, 'APPROVED', 'INTERNAL', 1, 'SN', 'SYCEBNL', NOW(), NOW(), 1);

-- 6. Pièces justificatives SYCEBNL de test
INSERT INTO pieces_justificatives_sycebnl (id, numero_pj, libelle_pj, date_piece, montant_total, devise, type_pj, statut_traitement, document_id, entreprise_id, utilisateur_id, texte_ocr, confiance_ocr, date_analyse_ocr, analyse_ia, confiance_ia, date_analyse_ia, date_creation, date_modification) VALUES
(1, 'PJ-1-20240115120000-ABC12345', 'Facture fournisseur SARL EXEMPLE', '2024-01-15', 236000.00, 'XOF', 'FACTURE_FOURNISSEUR', 'ANALYSE_IA_TERMINEE', 1, 1, 1, 
'FACTURE N° FACT-2024-001
Date : 15/01/2024
Fournisseur : SARL EXEMPLE
Adresse : 123 Avenue de la République, Dakar
Tél : +221 33 123 45 67

Désignation                    Quantité    Prix HT    Total HT
------------------------------------------------------------
Prestation de service              1      200,000    200,000
Consultation                       1       36,000     36,000

Sous-total HT :                   200,000
TVA 18% :                          36,000
Total TTC :                       236,000

Mode de paiement : Virement bancaire
Échéance : 30 jours', 
0.95, NOW(), 'FACTURE', 0.90, NOW(), NOW(), NOW()),

(2, 'PJ-2-20240115130000-DEF67890', 'Reçu client ENTREPRISE A', '2024-01-15', 150000.00, 'XOF', 'RECU', 'PROPOSITIONS_GENEREES', 2, 1, 1,
'REÇU N° REC-2024-001
Date : 15/01/2024
Client : ENTREPRISE A
Adresse : 456 Rue de la Paix, Dakar

Désignation                    Montant
---------------------------------------
Prestation de service          150,000

Total TTC :                   150,000

Mode de paiement : Virement bancaire
Date de règlement : 15/01/2024',
0.92, NOW(), 'RECU', 0.88, NOW(), NOW(), NOW()),

(3, 'PJ-3-20240115140000-GHI11111', 'Bon de commande fournisseur', '2024-01-15', 50000.00, 'XOF', 'BON_COMMANDE', 'TELECHARGEE', 3, 1, 1,
'BON DE COMMANDE N° BC-2024-001
Date : 15/01/2024
Fournisseur : FOURNISSEUR MATERIEL
Adresse : 789 Boulevard du Centenaire, Dakar

Désignation                    Quantité    Prix HT    Total HT
------------------------------------------------------------
Matériel informatique              1       50,000     50,000

Total HT :                        50,000
TVA 18% :                          9,000
Total TTC :                       59,000

Date de livraison prévue : 20/01/2024',
0.88, NOW(), NULL, NULL, NULL, NOW(), NOW());

-- 7. Analyses OCR de test
INSERT INTO analyses_ocr_sycebnl (id, piece_justificative_id, texte_extrait, confiance_globale, langue_detectee, nombre_mots, nombre_lignes, moteur_ocr_utilise, version_moteur, temps_traitement_ms, date_analyse, statut_analyse) VALUES
(1, 1, 'FACTURE N° FACT-2024-001
Date : 15/01/2024
Fournisseur : SARL EXEMPLE
Adresse : 123 Avenue de la République, Dakar
Tél : +221 33 123 45 67

Désignation                    Quantité    Prix HT    Total HT
------------------------------------------------------------
Prestation de service              1      200,000    200,000
Consultation                       1       36,000     36,000

Sous-total HT :                   200,000
TVA 18% :                          36,000
Total TTC :                       236,000

Mode de paiement : Virement bancaire
Échéance : 30 jours', 0.95, 'fr', 45, 15, 'TESSERACT', '5.0.0', 2500, NOW(), 'TERMINEE'),

(2, 2, 'REÇU N° REC-2024-001
Date : 15/01/2024
Client : ENTREPRISE A
Adresse : 456 Rue de la Paix, Dakar

Désignation                    Montant
---------------------------------------
Prestation de service          150,000

Total TTC :                   150,000

Mode de paiement : Virement bancaire
Date de règlement : 15/01/2024', 0.92, 'fr', 25, 10, 'TESSERACT', '5.0.0', 1800, NOW(), 'TERMINEE'),

(3, 3, 'BON DE COMMANDE N° BC-2024-001
Date : 15/01/2024
Fournisseur : FOURNISSEUR MATERIEL
Adresse : 789 Boulevard du Centenaire, Dakar

Désignation                    Quantité    Prix HT    Total HT
------------------------------------------------------------
Matériel informatique              1       50,000     50,000

Total HT :                        50,000
TVA 18% :                          9,000
Total TTC :                       59,000

Date de livraison prévue : 20/01/2024', 0.88, 'fr', 35, 12, 'TESSERACT', '5.0.0', 2200, NOW(), 'TERMINEE');

-- 8. Analyses IA de test
INSERT INTO analyses_ia_sycebnl (id, piece_justificative_id, type_document_detecte, confiance_type_document, montant_detecte, confiance_montant, devise_detectee, date_detectee, confiance_date, fournisseur_detecte, confiance_fournisseur, client_detecte, confiance_client, numero_facture, confiance_numero_facture, description_detectee, confiance_description, tva_detectee, confiance_tva, montant_ht, montant_ttc, confiance_globale, modele_ia_utilise, version_modele, temps_traitement_ms, date_analyse, statut_analyse) VALUES
(1, 1, 'FACTURE', 0.98, 236000.00, 0.95, 'XOF', '2024-01-15', 0.92, 'SARL EXEMPLE', 0.90, NULL, NULL, 'FACT-2024-001', 0.88, 'Prestation de service et consultation', 0.85, 18.00, 0.90, 200000.00, 236000.00, 0.90, 'GPT-4', '4.0', 3500, NOW(), 'TERMINEE'),

(2, 2, 'RECU', 0.95, 150000.00, 0.92, 'XOF', '2024-01-15', 0.90, NULL, NULL, 'ENTREPRISE A', 0.88, 'REC-2024-001', 0.85, 'Prestation de service', 0.80, NULL, NULL, 150000.00, 150000.00, 0.88, 'GPT-4', '4.0', 2800, NOW(), 'TERMINEE'),

(3, 3, 'BON_COMMANDE', 0.90, 59000.00, 0.88, 'XOF', '2024-01-15', 0.85, 'FOURNISSEUR MATERIEL', 0.82, NULL, NULL, 'BC-2024-001', 0.80, 'Matériel informatique', 0.75, 18.00, 0.85, 50000.00, 59000.00, 0.85, 'GPT-4', '4.0', 3200, NOW(), 'TERMINEE');

-- 9. Propositions d'écritures de test
INSERT INTO propositions_ecritures_sycebnl (id, piece_justificative_id, numero_proposition, libelle_proposition, date_proposition, montant_total, devise, type_ecriture, statut_proposition, confiance_proposition, date_creation, date_modification, cree_par) VALUES
(1, 1, 'PROP-1-1705320000000', 'Facture fournisseur SARL EXEMPLE', '2024-01-15', 236000.00, 'XOF', 'FACTURE_FOURNISSEUR', 'PROPOSEE', 0.90, NOW(), NOW(), 1),
(2, 2, 'PROP-2-1705323600000', 'Encaissement client ENTREPRISE A', '2024-01-15', 150000.00, 'XOF', 'ENCAISSEMENT_CLIENT', 'VALIDEE', 0.88, NOW(), NOW(), 1),
(3, 3, 'PROP-3-1705327200000', 'Engagement commande fournisseur', '2024-01-15', 59000.00, 'XOF', 'AUTRE', 'PROPOSEE', 0.85, NOW(), NOW(), 1);

-- 10. Lignes de propositions de test
INSERT INTO lignes_propositions_ecritures_sycebnl (id, proposition_id, compte_id, numero_compte, libelle_compte, libelle_ligne, debit, credit, ordre, sens_normal, confiance_ligne, justification_ligne, regle_appliquee) VALUES
-- Proposition 1 : Facture fournisseur
(1, 1, 6, '606000', 'Services extérieurs', 'Prestation de service SARL EXEMPLE', 200000.00, NULL, 1, 'DEBITEUR', 0.90, 'Compte de charge standard pour facture fournisseur', 'REGLE_FACTURE_FOURNISSEUR'),
(2, 1, 18, '445001', 'TVA déductible', 'TVA déductible sur facture fournisseur', 36000.00, NULL, 2, 'DEBITEUR', 0.85, 'TVA déductible sur facture fournisseur', 'REGLE_TVA_DEDUCTIBLE'),
(3, 1, 12, '401001', 'Fournisseur SARL EXEMPLE', 'Dette fournisseur SARL EXEMPLE', NULL, 236000.00, 3, 'CREDITEUR', 0.95, 'Dette envers le fournisseur', 'REGLE_FOURNISSEUR'),

-- Proposition 2 : Encaissement client
(4, 2, 21, '512001', 'Banque BICIS', 'Encaissement client ENTREPRISE A', 150000.00, NULL, 1, 'DEBITEUR', 0.90, 'Encaissement en banque', 'REGLE_ENCAISSEMENT'),
(5, 2, 15, '411001', 'Client ENTREPRISE A', 'Règlement client ENTREPRISE A', NULL, 150000.00, 2, 'CREDITEUR', 0.95, 'Règlement de la créance client', 'REGLE_CLIENT'),

-- Proposition 3 : Engagement commande
(6, 3, 24, '408001', 'Engagements fournisseurs', 'Engagement commande fournisseur', 59000.00, NULL, 1, 'DEBITEUR', 0.80, 'Engagement de commande', 'REGLE_ENGAGEMENT'),
(7, 3, 24, '408001', 'Engagements fournisseurs', 'Engagement fournisseur', NULL, 59000.00, 2, 'CREDITEUR', 0.80, 'Engagement envers le fournisseur', 'REGLE_ENGAGEMENT');

-- 11. Écritures comptables générées de test
INSERT INTO ecritures_comptables (id, numero_piece, date_ecriture, date_piece, reference, libelle, type_ecriture, statut, entreprise_id, exercice_id, utilisateur_id, devise, total_debit, total_credit, source, template_id, validation_ai_confiance, validation_ai_suggestions, metadata, created_at, updated_at) VALUES
(1, 'ECR-20240115-ABC123', '2024-01-15', '2024-01-15', 'PROP-2-1705323600000', 'Encaissement client ENTREPRISE A', 'NORMALE', 'VALIDEE', 1, 1, 1, 'XOF', 150000.00, 150000.00, 'IA', '2', 0.88, 'Écriture générée automatiquement à partir de la proposition PROP-2-1705323600000', '{"proposition_id":2,"piece_justificative_id":2,"type_proposition":"ENCAISSEMENT_CLIENT","confiance_proposition":0.88,"date_generation":"2024-01-15T12:00:00","source":"SYCEBNL_IA"}', NOW(), NOW());

-- 12. Lignes d'écritures de test
INSERT INTO lignes_ecritures (id, ecriture_id, compte_id, compte_numero, compte_libelle, libelle_ligne, debit, credit, ordre, analytique, created_at, updated_at) VALUES
(1, 1, 21, '512001', 'Banque BICIS', 'Encaissement client ENTREPRISE A', 150000.00, NULL, 1, 'Justification: Encaissement en banque; Règle: REGLE_ENCAISSEMENT; Confiance: 0.90', NOW(), NOW()),
(2, 1, 15, '411001', 'Client ENTREPRISE A', 'Règlement client ENTREPRISE A', NULL, 150000.00, 2, 'Justification: Règlement de la créance client; Règle: REGLE_CLIENT; Confiance: 0.95', NOW(), NOW());






