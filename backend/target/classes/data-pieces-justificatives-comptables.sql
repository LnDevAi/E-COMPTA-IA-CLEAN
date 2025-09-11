-- ========================================
-- DONNÉES DE TEST POUR LES PIÈCES JUSTIFICATIVES COMPTABLES
-- Module de comptabilité générale avec OCR et IA
-- ========================================

-- Supprimer les données existantes (dans l'ordre inverse des dépendances)
DELETE FROM pieces_justificatives_comptables;

-- ========================================
-- PIÈCES JUSTIFICATIVES COMPTABLES DE TEST
-- ========================================

-- PJ 1: Facture fournisseur ACME SARL
INSERT INTO pieces_justificatives_comptables (
    id, numero_pj, nom_fichier, chemin_fichier, type_fichier, taille_fichier,
    date_document, date_upload, libelle, description, type_document,
    statut_traitement, company_id, exercice_id, uploaded_by, validated_by,
    date_validation, ocr_text, ocr_confidence, ocr_date_traitement, ocr_statut,
    ia_analyse, ia_confidence, ia_date_traitement, ia_statut, ia_suggestions,
    propositions_ecritures, montant_detecte, devise_detectee, comptes_suggerees,
    ecriture_generee_id, fiche_imputation, journal_comptable, numero_ecriture,
    date_generation_ecriture, metadata, tags, created_at, updated_at
) VALUES (
    1, 'PJ12024001', 'facture_acme_2024_001.pdf', 'uploads/pj-comptables/1/1/PJ12024001_facture_acme_2024_001.pdf',
    'application/pdf', 245760, '2024-12-15', '2024-12-15 10:30:00',
    'PJ - Facture ACME SARL', 'Facture fournisseur pour achat de matériel informatique',
    'FACTURE_FOURNISSEUR', 'ECRITURE_GENERE', 1, 1, 1, 1, '2024-12-15 14:45:00',
    'FACTURE N° F2024-001\nDate: 15/12/2024\nFournisseur: ACME SARL\nMontant HT: 100,000.00 XOF\nTVA 18%: 18,000.00 XOF\nMontant TTC: 118,000.00 XOF\nCompte: 401 - Fournisseurs\nLibellé: Achat de matériel informatique',
    0.92, '2024-12-15 10:35:00', 'COMPLETED',
    'Facture fournisseur détectée avec achat de matériel informatique. Montant TTC: 118,000.00 XOF. TVA déductible: 18,000.00 XOF.',
    0.95, '2024-12-15 10:40:00', 'COMPLETED',
    'Comptes suggérés: 401 (Fournisseurs), 601 (Achats), 44566 (TVA déductible)',
    '{"type":"ACHAT","journal":"ACH","lignes":[{"compte":"601","libelle":"Achat de matériel","debit":100000.00,"credit":0.00},{"compte":"44566","libelle":"TVA déductible","debit":18000.00,"credit":0.00},{"compte":"401","libelle":"Fournisseur ACME SARL","debit":0.00,"credit":118000.00}],"totalDebit":118000.00,"totalCredit":118000.00}',
    118000.00, 'XOF',
    '[{"numero":"401","libelle":"Fournisseurs","type":"PASSIF","confidence":0.95},{"numero":"601","libelle":"Achats de matières premières","type":"CHARGES","confidence":0.90},{"numero":"44566","libelle":"TVA déductible","type":"ACTIF","confidence":0.85}]',
    1, '{"numeroPJ":"PJ12024001","numeroEcriture":"EC12024001","dateDocument":"2024-12-15","dateEcriture":"2024-12-15","libelle":"Écriture générée depuis PJ: PJ12024001","montant":118000.00,"devise":"XOF","journal":"ACH","typeDocument":"FACTURE_FOURNISSEUR","lignes":[{"compte":"601","libelle":"Achat de matériel","debit":100000.00,"credit":0.00},{"compte":"44566","libelle":"TVA déductible","debit":18000.00,"credit":0.00},{"compte":"401","libelle":"Fournisseur ACME SARL","debit":0.00,"credit":118000.00}]}',
    'ACH', 'EC12024001', '2024-12-15 14:45:00',
    '{"source":"upload","originalName":"facture_acme_2024_001.pdf","processingTime":"5 minutes"}',
    'facture,fournisseur,achat,materiel,informatique',
    '2024-12-15 10:30:00', '2024-12-15 14:45:00'
);

-- PJ 2: Facture client BETA SARL
INSERT INTO pieces_justificatives_comptables (
    id, numero_pj, nom_fichier, chemin_fichier, type_fichier, taille_fichier,
    date_document, date_upload, libelle, description, type_document,
    statut_traitement, company_id, exercice_id, uploaded_by, validated_by,
    date_validation, ocr_text, ocr_confidence, ocr_date_traitement, ocr_statut,
    ia_analyse, ia_confidence, ia_date_traitement, ia_statut, ia_suggestions,
    propositions_ecritures, montant_detecte, devise_detectee, comptes_suggerees,
    ecriture_generee_id, fiche_imputation, journal_comptable, numero_ecriture,
    date_generation_ecriture, metadata, tags, created_at, updated_at
) VALUES (
    2, 'PJ12024002', 'facture_beta_2024_002.pdf', 'uploads/pj-comptables/1/1/PJ12024002_facture_beta_2024_002.pdf',
    'application/pdf', 198432, '2024-12-16', '2024-12-16 09:15:00',
    'PJ - Facture BETA SARL', 'Facture client pour vente de services de conseil',
    'FACTURE_CLIENT', 'ECRITURE_GENERE', 1, 1, 1, 1, '2024-12-16 11:30:00',
    'FACTURE N° F2024-002\nDate: 16/12/2024\nClient: BETA SARL\nMontant HT: 150,000.00 XOF\nTVA 18%: 27,000.00 XOF\nMontant TTC: 177,000.00 XOF\nCompte: 411 - Clients\nLibellé: Vente de services de conseil',
    0.89, '2024-12-16 09:20:00', 'COMPLETED',
    'Facture client détectée avec vente de services. Montant TTC: 177,000.00 XOF. TVA collectée: 27,000.00 XOF.',
    0.88, '2024-12-16 09:25:00', 'COMPLETED',
    'Comptes suggérés: 411 (Clients), 701 (Ventes), 44571 (TVA collectée)',
    '{"type":"VENTE","journal":"VEN","lignes":[{"compte":"411","libelle":"Client BETA SARL","debit":177000.00,"credit":0.00},{"compte":"701","libelle":"Vente de services","debit":0.00,"credit":150000.00},{"compte":"44571","libelle":"TVA collectée","debit":0.00,"credit":27000.00}],"totalDebit":177000.00,"totalCredit":177000.00}',
    177000.00, 'XOF',
    '[{"numero":"411","libelle":"Clients","type":"ACTIF","confidence":0.95},{"numero":"701","libelle":"Ventes de produits finis","type":"PRODUITS","confidence":0.90},{"numero":"44571","libelle":"TVA collectée","type":"PASSIF","confidence":0.85}]',
    2, '{"numeroPJ":"PJ12024002","numeroEcriture":"EC12024002","dateDocument":"2024-12-16","dateEcriture":"2024-12-16","libelle":"Écriture générée depuis PJ: PJ12024002","montant":177000.00,"devise":"XOF","journal":"VEN","typeDocument":"FACTURE_CLIENT","lignes":[{"compte":"411","libelle":"Client BETA SARL","debit":177000.00,"credit":0.00},{"compte":"701","libelle":"Vente de services","debit":0.00,"credit":150000.00},{"compte":"44571","libelle":"TVA collectée","debit":0.00,"credit":27000.00}]}',
    'VEN', 'EC12024002', '2024-12-16 11:30:00',
    '{"source":"upload","originalName":"facture_beta_2024_002.pdf","processingTime":"4 minutes"}',
    'facture,client,vente,services,conseil',
    '2024-12-16 09:15:00', '2024-12-16 11:30:00'
);

-- PJ 3: Bon de commande GAMMA SARL
INSERT INTO pieces_justificatives_comptables (
    id, numero_pj, nom_fichier, chemin_fichier, type_fichier, taille_fichier,
    date_document, date_upload, libelle, description, type_document,
    statut_traitement, company_id, exercice_id, uploaded_by,
    ocr_text, ocr_confidence, ocr_date_traitement, ocr_statut,
    ia_analyse, ia_confidence, ia_date_traitement, ia_statut, ia_suggestions,
    propositions_ecritures, montant_detecte, devise_detectee, comptes_suggerees,
    metadata, tags, created_at, updated_at
) VALUES (
    3, 'PJ12024003', 'bon_commande_gamma_2024_003.pdf', 'uploads/pj-comptables/1/1/PJ12024003_bon_commande_gamma_2024_003.pdf',
    'application/pdf', 156789, '2024-12-17', '2024-12-17 14:20:00',
    'PJ - Bon de commande GAMMA SARL', 'Bon de commande pour achat de fournitures de bureau',
    'BON_COMMANDE', 'PROPOSITIONS_READY', 1, 1, 1,
    'BON DE COMMANDE N° BC2024-003\nDate: 17/12/2024\nFournisseur: GAMMA SARL\nMontant HT: 25,000.00 XOF\nTVA 18%: 4,500.00 XOF\nMontant TTC: 29,500.00 XOF\nLibellé: Achat de fournitures de bureau',
    0.87, '2024-12-17 14:25:00', 'COMPLETED',
    'Bon de commande détecté pour achat de fournitures. Montant TTC: 29,500.00 XOF. En attente de validation.',
    0.82, '2024-12-17 14:30:00', 'COMPLETED',
    'Comptes suggérés: 401 (Fournisseurs), 602 (Achats de fournitures), 44566 (TVA déductible)',
    '{"type":"ACHAT","journal":"ACH","lignes":[{"compte":"602","libelle":"Achat de fournitures","debit":25000.00,"credit":0.00},{"compte":"44566","libelle":"TVA déductible","debit":4500.00,"credit":0.00},{"compte":"401","libelle":"Fournisseur GAMMA SARL","debit":0.00,"credit":29500.00}],"totalDebit":29500.00,"totalCredit":29500.00}',
    29500.00, 'XOF',
    '[{"numero":"401","libelle":"Fournisseurs","type":"PASSIF","confidence":0.90},{"numero":"602","libelle":"Achats de fournitures","type":"CHARGES","confidence":0.85},{"numero":"44566","libelle":"TVA déductible","type":"ACTIF","confidence":0.80}]',
    '{"source":"upload","originalName":"bon_commande_gamma_2024_003.pdf","processingTime":"3 minutes"}',
    'bon,commande,fournisseur,achat,fournitures,bureau',
    '2024-12-17 14:20:00', '2024-12-17 14:30:00'
);

-- PJ 4: Relevé bancaire BICIS
INSERT INTO pieces_justificatives_comptables (
    id, numero_pj, nom_fichier, chemin_fichier, type_fichier, taille_fichier,
    date_document, date_upload, libelle, description, type_document,
    statut_traitement, company_id, exercice_id, uploaded_by,
    ocr_text, ocr_confidence, ocr_date_traitement, ocr_statut,
    ia_analyse, ia_confidence, ia_date_traitement, ia_statut, ia_suggestions,
    propositions_ecritures, montant_detecte, devise_detectee, comptes_suggerees,
    metadata, tags, created_at, updated_at
) VALUES (
    4, 'PJ12024004', 'releve_bicis_decembre_2024.pdf', 'uploads/pj-comptables/1/1/PJ12024004_releve_bicis_decembre_2024.pdf',
    'application/pdf', 312456, '2024-12-18', '2024-12-18 08:45:00',
    'PJ - Relevé bancaire BICIS', 'Relevé bancaire du mois de décembre 2024',
    'RELEVE_BANCAIRE', 'IA_TERMINE', 1, 1, 1,
    'RELEVE BANCAIRE BICIS\nCompte: 1234567890\nPériode: 01/12/2024 - 31/12/2024\nSolde début: 500,000.00 XOF\nSolde fin: 750,000.00 XOF\nMouvements: Virements entrants, Chèques émis, Frais bancaires',
    0.94, '2024-12-18 08:50:00', 'COMPLETED',
    'Relevé bancaire détecté avec plusieurs mouvements. Solde final: 750,000.00 XOF. Traitement en cours.',
    0.91, '2024-12-18 08:55:00', 'COMPLETED',
    'Comptes suggérés: 512 (Banque), 627 (Frais bancaires), 411 (Clients), 401 (Fournisseurs)',
    '{"type":"BANCAIRE","journal":"BAN","lignes":[{"compte":"512","libelle":"Banque BICIS","debit":250000.00,"credit":0.00},{"compte":"627","libelle":"Frais bancaires","debit":0.00,"credit":5000.00}],"totalDebit":250000.00,"totalCredit":5000.00}',
    250000.00, 'XOF',
    '[{"numero":"512","libelle":"Banque","type":"ACTIF","confidence":0.95},{"numero":"627","libelle":"Frais bancaires","type":"CHARGES","confidence":0.90},{"numero":"411","libelle":"Clients","type":"ACTIF","confidence":0.85}]',
    '{"source":"upload","originalName":"releve_bicis_decembre_2024.pdf","processingTime":"6 minutes"}',
    'releve,bancaire,bicis,decembre,2024',
    '2024-12-18 08:45:00', '2024-12-18 08:55:00'
);

-- PJ 5: Bulletin de paie décembre 2024
INSERT INTO pieces_justificatives_comptables (
    id, numero_pj, nom_fichier, chemin_fichier, type_fichier, taille_fichier,
    date_document, date_upload, libelle, description, type_document,
    statut_traitement, company_id, exercice_id, uploaded_by,
    ocr_text, ocr_confidence, ocr_date_traitement, ocr_statut,
    ia_analyse, ia_confidence, ia_date_traitement, ia_statut, ia_suggestions,
    propositions_ecritures, montant_detecte, devise_detectee, comptes_suggerees,
    metadata, tags, created_at, updated_at
) VALUES (
    5, 'PJ12024005', 'bulletin_paie_decembre_2024.pdf', 'uploads/pj-comptables/1/1/PJ12024005_bulletin_paie_decembre_2024.pdf',
    'application/pdf', 189234, '2024-12-31', '2024-12-31 16:30:00',
    'PJ - Bulletin de paie décembre 2024', 'Bulletin de paie du personnel pour le mois de décembre 2024',
    'BULLETIN_PAIE', 'OCR_TERMINE', 1, 1, 1,
    'BULLETIN DE PAIE\nPériode: 01/12/2024 - 31/12/2024\nEmployé: Jean DUPONT\nSalaire brut: 150,000.00 XOF\nCharges salariales: 22,500.00 XOF\nSalaire net: 127,500.00 XOF\nCharges patronales: 30,000.00 XOF',
    0.96, '2024-12-31 16:35:00', 'COMPLETED',
    'Bulletin de paie détecté. Traitement IA en cours...',
    0.00, '2024-12-31 16:35:00', 'PENDING',
    'Traitement IA en cours...',
    '{}',
    150000.00, 'XOF',
    '[]',
    '{"source":"upload","originalName":"bulletin_paie_decembre_2024.pdf","processingTime":"2 minutes"}',
    'bulletin,paie,decembre,2024,personnel,salaire',
    '2024-12-31 16:30:00', '2024-12-31 16:35:00'
);

-- PJ 6: Avoir fournisseur DELTA SARL
INSERT INTO pieces_justificatives_comptables (
    id, numero_pj, nom_fichier, chemin_fichier, type_fichier, taille_fichier,
    date_document, date_upload, libelle, description, type_document,
    statut_traitement, company_id, exercice_id, uploaded_by,
    ocr_text, ocr_confidence, ocr_date_traitement, ocr_statut,
    ia_analyse, ia_confidence, ia_date_traitement, ia_statut, ia_suggestions,
    propositions_ecritures, montant_detecte, devise_detectee, comptes_suggerees,
    metadata, tags, created_at, updated_at
) VALUES (
    6, 'PJ12024006', 'avoir_delta_2024_006.pdf', 'uploads/pj-comptables/1/1/PJ12024006_avoir_delta_2024_006.pdf',
    'application/pdf', 167890, '2024-12-20', '2024-12-20 11:15:00',
    'PJ - Avoir fournisseur DELTA SARL', 'Avoir fournisseur pour retour de marchandises',
    'AVOIR', 'PROPOSITIONS_READY', 1, 1, 1,
    'AVOIR N° AV2024-006\nDate: 20/12/2024\nFournisseur: DELTA SARL\nMontant HT: 15,000.00 XOF\nTVA 18%: 2,700.00 XOF\nMontant TTC: 17,700.00 XOF\nLibellé: Retour de marchandises défectueuses',
    0.90, '2024-12-20 11:20:00', 'COMPLETED',
    'Avoir fournisseur détecté pour retour de marchandises. Montant TTC: 17,700.00 XOF. Prêt pour validation.',
    0.86, '2024-12-20 11:25:00', 'COMPLETED',
    'Comptes suggérés: 401 (Fournisseurs), 601 (Achats), 44566 (TVA déductible)',
    '{"type":"AVOIR","journal":"ACH","lignes":[{"compte":"401","libelle":"Fournisseur DELTA SARL","debit":17700.00,"credit":0.00},{"compte":"601","libelle":"Achat de marchandises","debit":0.00,"credit":15000.00},{"compte":"44566","libelle":"TVA déductible","debit":0.00,"credit":2700.00}],"totalDebit":17700.00,"totalCredit":17700.00}',
    17700.00, 'XOF',
    '[{"numero":"401","libelle":"Fournisseurs","type":"PASSIF","confidence":0.90},{"numero":"601","libelle":"Achats de matières premières","type":"CHARGES","confidence":0.85},{"numero":"44566","libelle":"TVA déductible","type":"ACTIF","confidence":0.80}]',
    '{"source":"upload","originalName":"avoir_delta_2024_006.pdf","processingTime":"4 minutes"}',
    'avoir,fournisseur,retour,marchandises,defectueuses',
    '2024-12-20 11:15:00', '2024-12-20 11:25:00'
);

-- PJ 7: Chèque émis EPSILON SARL
INSERT INTO pieces_justificatives_comptables (
    id, numero_pj, nom_fichier, chemin_fichier, type_fichier, taille_fichier,
    date_document, date_upload, libelle, description, type_document,
    statut_traitement, company_id, exercice_id, uploaded_by,
    ocr_text, ocr_confidence, ocr_date_traitement, ocr_statut,
    ia_analyse, ia_confidence, ia_date_traitement, ia_statut, ia_suggestions,
    propositions_ecritures, montant_detecte, devise_detectee, comptes_suggerees,
    metadata, tags, created_at, updated_at
) VALUES (
    7, 'PJ12024007', 'cheque_epsilon_2024_007.jpg', 'uploads/pj-comptables/1/1/PJ12024007_cheque_epsilon_2024_007.jpg',
    'image/jpeg', 234567, '2024-12-21', '2024-12-21 13:45:00',
    'PJ - Chèque émis EPSILON SARL', 'Chèque émis pour règlement de facture fournisseur',
    'CHEQUE', 'UPLOADED', 1, 1, 1,
    NULL, NULL, NULL, 'PENDING',
    NULL, NULL, NULL, 'PENDING',
    NULL,
    '{}',
    NULL, NULL,
    '[]',
    '{"source":"upload","originalName":"cheque_epsilon_2024_007.jpg","processingTime":"0 minutes"}',
    'cheque,emis,reglement,facture,fournisseur',
    '2024-12-21 13:45:00', '2024-12-21 13:45:00'
);

-- PJ 8: Virement reçu ZETA SARL
INSERT INTO pieces_justificatives_comptables (
    id, numero_pj, nom_fichier, chemin_fichier, type_fichier, taille_fichier,
    date_document, date_upload, libelle, description, type_document,
    statut_traitement, company_id, exercice_id, uploaded_by,
    ocr_text, ocr_confidence, ocr_date_traitement, ocr_statut,
    ia_analyse, ia_confidence, ia_date_traitement, ia_statut, ia_suggestions,
    propositions_ecritures, montant_detecte, devise_detectee, comptes_suggerees,
    metadata, tags, created_at, updated_at
) VALUES (
    8, 'PJ12024008', 'virement_zeta_2024_008.pdf', 'uploads/pj-comptables/1/1/PJ12024008_virement_zeta_2024_008.pdf',
    'application/pdf', 145678, '2024-12-22', '2024-12-22 10:20:00',
    'PJ - Virement reçu ZETA SARL', 'Virement bancaire reçu de client ZETA SARL',
    'VIREMENT', 'ERROR', 1, 1, 1,
    'VIREMENT BANCAIRE\nDate: 22/12/2024\nÉmetteur: ZETA SARL\nBénéficiaire: Notre entreprise\nMontant: 85,000.00 XOF\nRéférence: VIR2024008\nLibellé: Règlement facture F2024-008',
    0.88, '2024-12-22 10:25:00', 'COMPLETED',
    'Virement bancaire détecté. Erreur lors du traitement IA.',
    0.00, '2024-12-22 10:25:00', 'FAILED',
    'Erreur lors du traitement IA: Service temporairement indisponible',
    '{}',
    85000.00, 'XOF',
    '[]',
    '{"source":"upload","originalName":"virement_zeta_2024_008.pdf","processingTime":"3 minutes","error":"IA service unavailable"}',
    'virement,recu,client,reglement,facture',
    '2024-12-22 10:20:00', '2024-12-22 10:25:00'
);

-- ========================================
-- STATISTIQUES DES DONNÉES DE TEST
-- ========================================

-- Résumé des PJ créées:
-- 1. PJ12024001: Facture fournisseur ACME SARL - 118,000.00 XOF - ÉCRITURE_GENERE
-- 2. PJ12024002: Facture client BETA SARL - 177,000.00 XOF - ÉCRITURE_GENERE  
-- 3. PJ12024003: Bon de commande GAMMA SARL - 29,500.00 XOF - PROPOSITIONS_READY
-- 4. PJ12024004: Relevé bancaire BICIS - 250,000.00 XOF - IA_TERMINE
-- 5. PJ12024005: Bulletin de paie décembre 2024 - 150,000.00 XOF - OCR_TERMINE
-- 6. PJ12024006: Avoir fournisseur DELTA SARL - 17,700.00 XOF - PROPOSITIONS_READY
-- 7. PJ12024007: Chèque émis EPSILON SARL - NULL - UPLOADED
-- 8. PJ12024008: Virement reçu ZETA SARL - 85,000.00 XOF - ERROR

-- Types de documents couverts:
-- - FACTURE_FOURNISSEUR (2)
-- - FACTURE_CLIENT (1)
-- - BON_COMMANDE (1)
-- - RELEVE_BANCAIRE (1)
-- - BULLETIN_PAIE (1)
-- - AVOIR (1)
-- - CHEQUE (1)
-- - VIREMENT (1)

-- Statuts de traitement:
-- - ECRITURE_GENERE (2)
-- - PROPOSITIONS_READY (2)
-- - IA_TERMINE (1)
-- - OCR_TERMINE (1)
-- - UPLOADED (1)
-- - ERROR (1)

-- Montant total des PJ: 827,200.00 XOF
-- Moyenne de confiance OCR: 0.90
-- Moyenne de confiance IA: 0.70

-- ========================================
-- RÉINITIALISATION DES SÉQUENCES
-- ========================================

-- Réinitialiser les séquences pour les nouveaux enregistrements
-- (Ajustez selon votre SGBD - PostgreSQL, MySQL, etc.)
-- ALTER SEQUENCE pieces_justificatives_comptables_id_seq RESTART WITH 9;






