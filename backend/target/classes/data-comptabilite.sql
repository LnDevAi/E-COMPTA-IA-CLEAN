-- ========================================
-- DONNÉES DE TEST POUR LE MODULE COMPTABILITÉ
-- Balance comptable et États financiers
-- ========================================

-- Supprimer les données existantes (dans l'ordre inverse des dépendances)
DELETE FROM solde_compte;
DELETE FROM balance_comptable;
DELETE FROM etats_financiers;

-- ========================================
-- BALANCES COMPTABLES DE TEST
-- ========================================

-- Balance comptable pour l'exercice 2024
INSERT INTO balance_comptable (
    id, company_id, exercice_id, date_balance, date_debut, date_fin,
    standard_comptable, devise, statut, total_debit, total_credit,
    solde_debit, solde_credit, nombre_comptes, nombre_mouvements,
    equilibre, date_creation, date_modification, created_by, observations, version
) VALUES (
    1, 1, 1, '2024-12-31', '2024-01-01', '2024-12-31',
    'SYSCOHADA', 'XOF', 'GENERATED', 15000000.00, 15000000.00,
    7500000.00, 7500000.00, 25, 150, true,
    '2024-12-31 10:00:00', '2024-12-31 10:00:00', 1,
    'Balance générée automatiquement pour l\'exercice 2024', '1.0'
);

-- Balance comptable pour l'exercice 2023
INSERT INTO balance_comptable (
    id, company_id, exercice_id, date_balance, date_debut, date_fin,
    standard_comptable, devise, statut, total_debit, total_credit,
    solde_debit, solde_credit, nombre_comptes, nombre_mouvements,
    equilibre, date_creation, date_modification, created_by, observations, version
) VALUES (
    2, 1, 2, '2023-12-31', '2023-01-01', '2023-12-31',
    'SYSCOHADA', 'XOF', 'VALIDATED', 12000000.00, 12000000.00,
    6000000.00, 6000000.00, 20, 120, true,
    '2023-12-31 10:00:00', '2023-12-31 15:00:00', 1,
    'Balance validée pour l\'exercice 2023', '1.0'
);

-- ========================================
-- SOLDES DE COMPTES DE TEST
-- ========================================

-- Soldes pour la balance 2024 (Balance ID = 1)
-- ACTIF
INSERT INTO solde_compte (
    id, balance_id, account_id, numero_compte, libelle_compte,
    solde_debut_debit, solde_debut_credit, mouvement_debit, mouvement_credit,
    solde_fin_debit, solde_fin_credit, solde_final, sens_solde,
    nombre_mouvements, date_dernier_mouvement, classe_compte, type_compte,
    nature_compte, ordre_affichage
) VALUES 
-- Classe 2 - ACTIF IMMOBILISÉ
(1, 1, 1, '201', 'Immobilisations incorporelles', 0.00, 0.00, 500000.00, 0.00, 500000.00, 0.00, 500000.00, 'DEBIT', 5, '2024-12-15', 2, 'ASSET', 'ACTIF', 1),
(2, 1, 2, '211', 'Terrains', 0.00, 0.00, 2000000.00, 0.00, 2000000.00, 0.00, 2000000.00, 'DEBIT', 3, '2024-11-20', 2, 'ASSET', 'ACTIF', 2),
(3, 1, 3, '213', 'Constructions', 0.00, 0.00, 1500000.00, 0.00, 1500000.00, 0.00, 1500000.00, 'DEBIT', 4, '2024-10-10', 2, 'ASSET', 'ACTIF', 3),
(4, 1, 4, '218', 'Autres immobilisations corporelles', 0.00, 0.00, 800000.00, 0.00, 800000.00, 0.00, 800000.00, 'DEBIT', 6, '2024-09-05', 2, 'ASSET', 'ACTIF', 4),

-- Classe 3 - ACTIF CIRCULANT
(5, 1, 5, '311', 'Stocks de matières premières', 0.00, 0.00, 300000.00, 0.00, 300000.00, 0.00, 300000.00, 'DEBIT', 8, '2024-12-20', 3, 'ASSET', 'ACTIF', 5),
(6, 1, 6, '312', 'Stocks de produits finis', 0.00, 0.00, 400000.00, 0.00, 400000.00, 0.00, 400000.00, 'DEBIT', 10, '2024-12-18', 3, 'ASSET', 'ACTIF', 6),
(7, 1, 7, '411', 'Clients', 0.00, 0.00, 1200000.00, 0.00, 1200000.00, 0.00, 1200000.00, 'DEBIT', 15, '2024-12-30', 4, 'ASSET', 'ACTIF', 7),
(8, 1, 8, '512', 'Banque', 0.00, 0.00, 800000.00, 0.00, 800000.00, 0.00, 800000.00, 'DEBIT', 20, '2024-12-31', 5, 'ASSET', 'ACTIF', 8),
(9, 1, 9, '531', 'Caisse', 0.00, 0.00, 100000.00, 0.00, 100000.00, 0.00, 100000.00, 'DEBIT', 25, '2024-12-31', 5, 'ASSET', 'ACTIF', 9),

-- PASSIF
-- Classe 1 - CAPITAUX PROPRES
(10, 1, 10, '101', 'Capital social', 0.00, 0.00, 0.00, 3000000.00, 0.00, 3000000.00, -3000000.00, 'CREDIT', 1, '2024-01-01', 1, 'EQUITY', 'PASSIF', 10),
(11, 1, 11, '111', 'Réserves', 0.00, 0.00, 0.00, 500000.00, 0.00, 500000.00, -500000.00, 'CREDIT', 2, '2024-06-30', 1, 'EQUITY', 'PASSIF', 11),
(12, 1, 12, '120', 'Résultat de l\'exercice', 0.00, 0.00, 0.00, 800000.00, 0.00, 800000.00, -800000.00, 'CREDIT', 1, '2024-12-31', 1, 'EQUITY', 'PASSIF', 12),

-- Classe 4 - DETTES
(13, 1, 13, '401', 'Fournisseurs', 0.00, 0.00, 0.00, 600000.00, 0.00, 600000.00, -600000.00, 'CREDIT', 12, '2024-12-25', 4, 'LIABILITY', 'PASSIF', 13),
(14, 1, 14, '421', 'Personnel', 0.00, 0.00, 0.00, 400000.00, 0.00, 400000.00, -400000.00, 'CREDIT', 8, '2024-12-31', 4, 'LIABILITY', 'PASSIF', 14),
(15, 1, 15, '444', 'État - TVA à payer', 0.00, 0.00, 0.00, 200000.00, 0.00, 200000.00, -200000.00, 'CREDIT', 6, '2024-12-31', 4, 'LIABILITY', 'PASSIF', 15),
(16, 1, 16, '161', 'Emprunts', 0.00, 0.00, 0.00, 1000000.00, 0.00, 1000000.00, -1000000.00, 'CREDIT', 4, '2024-03-15', 1, 'LIABILITY', 'PASSIF', 16),

-- CHARGES (Classe 6)
(17, 1, 17, '601', 'Achats de matières premières', 0.00, 0.00, 2000000.00, 0.00, 2000000.00, 0.00, 2000000.00, 'DEBIT', 30, '2024-12-28', 6, 'EXPENSE', 'CHARGES', 17),
(18, 1, 18, '603', 'Variations de stocks', 0.00, 0.00, 100000.00, 0.00, 100000.00, 0.00, 100000.00, 'DEBIT', 5, '2024-12-31', 6, 'EXPENSE', 'CHARGES', 18),
(19, 1, 19, '605', 'Autres charges externes', 0.00, 0.00, 500000.00, 0.00, 500000.00, 0.00, 500000.00, 'DEBIT', 15, '2024-12-20', 6, 'EXPENSE', 'CHARGES', 19),
(20, 1, 20, '621', 'Personnel', 0.00, 0.00, 3000000.00, 0.00, 3000000.00, 0.00, 3000000.00, 'DEBIT', 12, '2024-12-31', 6, 'EXPENSE', 'CHARGES', 20),
(21, 1, 21, '681', 'Dotations aux amortissements', 0.00, 0.00, 200000.00, 0.00, 200000.00, 0.00, 200000.00, 'DEBIT', 4, '2024-12-31', 6, 'EXPENSE', 'CHARGES', 21),

-- PRODUITS (Classe 7)
(22, 1, 22, '701', 'Ventes de produits finis', 0.00, 0.00, 0.00, 8000000.00, 0.00, 8000000.00, -8000000.00, 'CREDIT', 40, '2024-12-30', 7, 'REVENUE', 'PRODUITS', 22),
(23, 1, 23, '706', 'Prestations de services', 0.00, 0.00, 0.00, 1000000.00, 0.00, 1000000.00, -1000000.00, 'CREDIT', 8, '2024-12-15', 7, 'REVENUE', 'PRODUITS', 23),
(24, 1, 24, '771', 'Produits financiers', 0.00, 0.00, 0.00, 50000.00, 0.00, 50000.00, -50000.00, 'CREDIT', 3, '2024-12-31', 7, 'REVENUE', 'PRODUITS', 24),
(25, 1, 25, '781', 'Reprises sur provisions', 0.00, 0.00, 0.00, 30000.00, 0.00, 30000.00, -30000.00, 'CREDIT', 2, '2024-12-31', 7, 'REVENUE', 'PRODUITS', 25);

-- ========================================
-- ÉTATS FINANCIERS DE TEST
-- ========================================

-- Bilan SYSCOHADA pour l'exercice 2024
INSERT INTO etats_financiers (
    id, exercice_smt_id, type_etat, standard_comptable, date_generation,
    date_creation, date_modification, statut, devise, total_actifs, total_passifs,
    total_produits, total_charges, resultat_net, observations, donnees_json, version
) VALUES (
    1, 1, 'BILAN', 'SYSCOHADA', '2024-12-31',
    '2024-12-31 11:00:00', '2024-12-31 11:00:00', 'GENERATED', 'XOF',
    7500000.00, 7500000.00, 0.00, 0.00, 0.00,
    'Bilan SYSCOHADA généré automatiquement',
    '{"actifs":{"actifImmobilise":{"total":4800000.00,"details":{"AD":{"montant":500000.00},"AF":{"montant":3500000.00},"AG":{"montant":800000.00}}},"actifCirculant":{"total":2700000.00,"details":{"BA":{"montant":700000.00},"BB":{"montant":1200000.00},"BG":{"montant":800000.00}}},"totalActifs":7500000.00},"passifs":{"capitauxPropres":{"total":4300000.00,"details":{"CA":{"montant":3000000.00},"CE":{"montant":500000.00},"CF":{"montant":800000.00}}},"dettesFinancieres":{"total":1000000.00,"details":{"DA":{"montant":1000000.00}}},"passifCirculant":{"total":2200000.00,"details":{"DJ":{"montant":600000.00},"DK":{"montant":400000.00},"DL":{"montant":200000.00},"DM":{"montant":1000000.00}}},"totalPassifs":7500000.00},"equilibre":true,"ecart":0.00}',
    '1.0'
);

-- Compte de Résultat SYSCOHADA pour l'exercice 2024
INSERT INTO etats_financiers (
    id, exercice_smt_id, type_etat, standard_comptable, date_generation,
    date_creation, date_modification, statut, devise, total_actifs, total_passifs,
    total_produits, total_charges, resultat_net, observations, donnees_json, version
) VALUES (
    2, 1, 'COMPTE_RESULTAT', 'SYSCOHADA', '2024-12-31',
    '2024-12-31 11:30:00', '2024-12-31 11:30:00', 'GENERATED', 'XOF',
    0.00, 0.00, 9080000.00, 5800000.00, 3280000.00,
    'Compte de résultat SYSCOHADA généré automatiquement',
    '{"produits":{"totalProduitsExploitation":9080000.00,"details":{"TA":{"montant":8000000.00},"TC":{"montant":1000000.00},"TK":{"montant":50000.00},"TL":{"montant":30000.00}},"totalProduits":9080000.00},"charges":{"totalChargesExploitation":5800000.00,"details":{"RA":{"montant":2000000.00},"RC":{"montant":100000.00},"RE":{"montant":500000.00},"RH":{"montant":3000000.00},"RM":{"montant":200000.00}},"totalCharges":5800000.00},"resultatNet":3280000.00,"resultatAvantImpot":3280000.00}',
    '1.0'
);

-- Tableau de Flux de Trésorerie pour l'exercice 2024
INSERT INTO etats_financiers (
    id, exercice_smt_id, type_etat, standard_comptable, date_generation,
    date_creation, date_modification, statut, devise, total_actifs, total_passifs,
    total_produits, total_charges, resultat_net, observations, donnees_json, version
) VALUES (
    3, 1, 'FLUX_TRESORERIE', 'SYSCOHADA', '2024-12-31',
    '2024-12-31 12:00:00', '2024-12-31 12:00:00', 'GENERATED', 'XOF',
    0.00, 0.00, 0.00, 0.00, 0.00,
    'Tableau de flux de trésorerie généré automatiquement',
    '{"fluxExploitation":2500000.00,"fluxInvestissement":-4800000.00,"fluxFinancement":2300000.00,"variationTresorerie":0.00}',
    '1.0'
);

-- Annexes pour l'exercice 2024
INSERT INTO etats_financiers (
    id, exercice_smt_id, type_etat, standard_comptable, date_generation,
    date_creation, date_modification, statut, devise, total_actifs, total_passifs,
    total_produits, total_charges, resultat_net, observations, donnees_json, version
) VALUES (
    4, 1, 'ANNEXES', 'SYSCOHADA', '2024-12-31',
    '2024-12-31 12:30:00', '2024-12-31 12:30:00', 'GENERATED', 'XOF',
    0.00, 0.00, 0.00, 0.00, 0.00,
    'Annexes générées automatiquement',
    '{"methodesComptables":"Méthodes comptables appliquées selon les normes SYSCOHADA","engagements":"Engagements hors bilan : cautionnements 500000 XOF","evenementsPosterieurs":"Aucun événement postérieur significatif"}',
    '1.0'
);

-- ========================================
-- DONNÉES COMPARATIVES (Exercice 2023)
-- ========================================

-- Bilan SYSCOHADA pour l'exercice 2023 (comparatif)
INSERT INTO etats_financiers (
    id, exercice_smt_id, type_etat, standard_comptable, date_generation,
    date_creation, date_modification, statut, devise, total_actifs, total_passifs,
    total_produits, total_charges, resultat_net, observations, donnees_json, version
) VALUES (
    5, 2, 'BILAN', 'SYSCOHADA', '2023-12-31',
    '2023-12-31 11:00:00', '2023-12-31 11:00:00', 'VALIDATED', 'XOF',
    6000000.00, 6000000.00, 0.00, 0.00, 0.00,
    'Bilan SYSCOHADA exercice 2023 (comparatif)',
    '{"actifs":{"totalActifs":6000000.00},"passifs":{"totalPassifs":6000000.00},"equilibre":true}',
    '1.0'
);

-- Compte de Résultat SYSCOHADA pour l'exercice 2023 (comparatif)
INSERT INTO etats_financiers (
    id, exercice_smt_id, type_etat, standard_comptable, date_generation,
    date_creation, date_modification, statut, devise, total_actifs, total_passifs,
    total_produits, total_charges, resultat_net, observations, donnees_json, version
) VALUES (
    6, 2, 'COMPTE_RESULTAT', 'SYSCOHADA', '2023-12-31',
    '2023-12-31 11:30:00', '2023-12-31 11:30:00', 'VALIDATED', 'XOF',
    0.00, 0.00, 7000000.00, 4500000.00, 2500000.00,
    'Compte de résultat SYSCOHADA exercice 2023 (comparatif)',
    '{"produits":{"totalProduits":7000000.00},"charges":{"totalCharges":4500000.00},"resultatNet":2500000.00}',
    '1.0'
);

-- ========================================
-- RÉINITIALISATION DES SÉQUENCES
-- ========================================

-- Réinitialiser les séquences pour les nouveaux enregistrements
-- (Ajustez selon votre SGBD - PostgreSQL, MySQL, etc.)
-- ALTER SEQUENCE balance_comptable_id_seq RESTART WITH 3;
-- ALTER SEQUENCE solde_compte_id_seq RESTART WITH 26;
-- ALTER SEQUENCE etats_financiers_id_seq RESTART WITH 7;






