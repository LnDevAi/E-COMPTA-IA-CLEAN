-- =====================================================
-- MIGRATION V6 : TABLES ÉTATS FINANCIERS SYCEBNL
-- Mapping comptes vers postes des états financiers
-- =====================================================

-- Table de mapping des comptes vers les postes des états financiers
CREATE TABLE mapping_comptes_postes (
    id BIGSERIAL PRIMARY KEY,
    pays_code VARCHAR(2) NOT NULL,
    standard_comptable VARCHAR(20) NOT NULL,
    type_systeme VARCHAR(10) NOT NULL CHECK (type_systeme IN ('NORMAL', 'MINIMAL')),
    type_etat VARCHAR(50) NOT NULL,
    poste_code VARCHAR(10) NOT NULL,
    poste_libelle VARCHAR(255) NOT NULL,
    comptes_pattern TEXT[] NOT NULL,
    signe_normal VARCHAR(10) NOT NULL CHECK (signe_normal IN ('DEBIT', 'CREDIT')),
    ordre_affichage INTEGER NOT NULL,
    niveau INTEGER NOT NULL DEFAULT 1,
    est_total BOOLEAN DEFAULT FALSE,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE(pays_code, standard_comptable, type_systeme, type_etat, poste_code),
    INDEX idx_mapping_pays_standard (pays_code, standard_comptable),
    INDEX idx_mapping_systeme_etat (type_systeme, type_etat),
    INDEX idx_mapping_ordre (ordre_affichage)
);

-- Table des états financiers générés
CREATE TABLE etats_financiers_sycebnl (
    id BIGSERIAL PRIMARY KEY,
    exercice_id BIGINT NOT NULL,
    entite_id BIGINT NOT NULL,
    type_systeme VARCHAR(10) NOT NULL CHECK (type_systeme IN ('NORMAL', 'MINIMAL')),
    type_etat VARCHAR(50) NOT NULL,
    date_arrete DATE NOT NULL,
    statut VARCHAR(20) DEFAULT 'BROUILLON' CHECK (statut IN ('BROUILLON', 'VALIDE', 'CLOTURE')),
    donnees_json JSONB,
    total_actif DECIMAL(15,2),
    total_passif DECIMAL(15,2),
    total_produits DECIMAL(15,2),
    total_charges DECIMAL(15,2),
    resultat_net DECIMAL(15,2),
    equilibre BOOLEAN DEFAULT FALSE,
    genere_par VARCHAR(255),
    valide_par VARCHAR(255),
    date_validation TIMESTAMP,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (exercice_id) REFERENCES exercices(id),
    FOREIGN KEY (entite_id) REFERENCES entreprises(id),
    INDEX idx_etats_exercice_entite (exercice_id, entite_id),
    INDEX idx_etats_type_systeme (type_systeme, type_etat),
    INDEX idx_etats_date_arrete (date_arrete)
);

-- Table des notes annexes
CREATE TABLE notes_annexes_sycebnl (
    id BIGSERIAL PRIMARY KEY,
    etat_financier_id BIGINT NOT NULL,
    numero_note VARCHAR(10) NOT NULL,
    titre_note VARCHAR(255) NOT NULL,
    type_note VARCHAR(50) NOT NULL,
    contenu_note TEXT NOT NULL,
    ordre_affichage INTEGER NOT NULL,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (etat_financier_id) REFERENCES etats_financiers_sycebnl(id) ON DELETE CASCADE,
    INDEX idx_notes_etat (etat_financier_id),
    INDEX idx_notes_ordre (ordre_affichage)
);

-- Table du plan comptable SYCEBNL
CREATE TABLE plan_comptable_sycebnl (
    id BIGSERIAL PRIMARY KEY,
    numero_compte VARCHAR(10) NOT NULL UNIQUE,
    intitule_compte VARCHAR(255) NOT NULL,
    classe_compte VARCHAR(20) NOT NULL,
    type_compte VARCHAR(20) NOT NULL,
    niveau INTEGER NOT NULL DEFAULT 1,
    compte_parent VARCHAR(10),
    sens_normal VARCHAR(10) NOT NULL CHECK (sens_normal IN ('DEBITEUR', 'CREDITEUR')),
    utilise_systeme_normal BOOLEAN DEFAULT TRUE,
    utilise_smt BOOLEAN DEFAULT TRUE,
    obligatoire_ong BOOLEAN DEFAULT FALSE,
    description_utilisation TEXT,
    actif BOOLEAN DEFAULT TRUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_plan_numero (numero_compte),
    INDEX idx_plan_classe (classe_compte),
    INDEX idx_plan_niveau (niveau)
);

-- =====================================================
-- DONNÉES INITIALES - MAPPING SYSTÈME NORMAL
-- =====================================================

-- BILAN - ACTIF IMMOBILISÉ
INSERT INTO mapping_comptes_postes (pays_code, standard_comptable, type_systeme, type_etat, poste_code, poste_libelle, comptes_pattern, signe_normal, ordre_affichage, niveau, est_total) VALUES
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AD', 'IMMOBILISATIONS INCORPORELLES', ARRAY['20%', '21%'], 'DEBIT', 10, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AF', 'Frais d''établissement', ARRAY['201%'], 'DEBIT', 11, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AG', 'Charges à répartir', ARRAY['202%'], 'DEBIT', 12, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AH', 'Primes de remboursement des obligations', ARRAY['206%'], 'DEBIT', 13, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AI', 'Brevets, licences, logiciels', ARRAY['211%', '212%', '213%'], 'DEBIT', 14, 2, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AJ', 'IMMOBILISATIONS CORPORELLES', ARRAY['22%', '23%', '24%'], 'DEBIT', 20, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AK', 'Terrains', ARRAY['221%', '222%'], 'DEBIT', 21, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AL', 'Bâtiments, installations techniques', ARRAY['231%', '232%'], 'DEBIT', 22, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AM', 'Matériel et outillage', ARRAY['233%', '234%'], 'DEBIT', 23, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AN', 'Matériel de transport', ARRAY['245%'], 'DEBIT', 24, 2, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AP', 'IMMOBILISATIONS FINANCIÈRES', ARRAY['26%', '27%'], 'DEBIT', 30, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AQ', 'Titres de participation', ARRAY['261%'], 'DEBIT', 31, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'AR', 'Prêts et créances immobilisées', ARRAY['274%', '275%'], 'DEBIT', 32, 2, false),

-- BILAN - ACTIF CIRCULANT
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BA', 'Actif circulant HAO', ARRAY['85%'], 'DEBIT', 40, 1, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BB', 'STOCKS ET EN-COURS', ARRAY['31%', '32%', '33%', '34%', '35%', '36%', '37%', '38%'], 'DEBIT', 50, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BC', 'Marchandises', ARRAY['31%'], 'DEBIT', 51, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BD', 'Matières premières', ARRAY['321%'], 'DEBIT', 52, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BE', 'Autres approvisionnements', ARRAY['322%'], 'DEBIT', 53, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BF', 'En-cours', ARRAY['33%'], 'DEBIT', 54, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BG', 'Produits fabriqués', ARRAY['36%'], 'DEBIT', 55, 2, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BH', 'CRÉANCES ET EMPLOIS ASSIMILÉS', ARRAY['40%', '41%', '42%', '43%', '44%', '45%', '46%', '47%', '48%'], 'DEBIT', 60, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BI', 'Fournisseurs, avances versées', ARRAY['409%'], 'DEBIT', 61, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BJ', 'Clients', ARRAY['411%', '413%', '416%', '418%'], 'DEBIT', 62, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BK', 'Autres créances', ARRAY['425%', '427%', '428%', '444%', '445%', '446%', '447%', '448%'], 'DEBIT', 63, 2, false),

-- BILAN - TRÉSORERIE ACTIF
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BQ', 'Titres de placement', ARRAY['50%'], 'DEBIT', 70, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BR', 'Valeurs à encaisser', ARRAY['51%'], 'DEBIT', 71, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BS', 'Banques, chèques postaux, caisse', ARRAY['521%', '531%', '541%', '571%'], 'DEBIT', 72, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'BT', 'TOTAL TRÉSORERIE-ACTIF', ARRAY[], 'DEBIT', 79, 0, true),

-- BILAN - PASSIF CAPITAUX PROPRES
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CA', 'CAPITAL', ARRAY['101%', '104%', '105%'], 'CREDIT', 100, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CB', 'Apporteurs, capital non appelé', ARRAY['109%'], 'CREDIT', 101, 2, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CC', 'PRIMES ET RÉSERVES', ARRAY['11%'], 'CREDIT', 110, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CD', 'Primes d''apport', ARRAY['111%'], 'CREDIT', 111, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CE', 'Écarts de réévaluation', ARRAY['105%'], 'CREDIT', 112, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CF', 'Réserves', ARRAY['118%'], 'CREDIT', 113, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CG', 'Report à nouveau', ARRAY['121%'], 'CREDIT', 114, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CH', 'Résultat net de l''exercice', ARRAY['130%'], 'CREDIT', 115, 2, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CI', 'AUTRES CAPITAUX PROPRES', ARRAY['14%', '15%'], 'CREDIT', 120, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CJ', 'Subventions d''investissement', ARRAY['141%', '142%'], 'CREDIT', 121, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'CK', 'Provisions réglementées', ARRAY['151%', '153%', '155%'], 'CREDIT', 122, 2, false),

-- BILAN - DETTES FINANCIÈRES
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DA', 'EMPRUNTS ET DETTES FINANCIÈRES', ARRAY['16%'], 'CREDIT', 130, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DB', 'Emprunts obligataires', ARRAY['161%'], 'CREDIT', 131, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DC', 'Emprunts et dettes auprès des établissements de crédit', ARRAY['162%', '163%', '164%'], 'CREDIT', 132, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DD', 'Emprunts et dettes financières diverses', ARRAY['166%', '167%', '168%'], 'CREDIT', 133, 2, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DE', 'PROVISIONS POUR RISQUES ET CHARGES', ARRAY['19%'], 'CREDIT', 140, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DF', 'Provisions pour risques', ARRAY['191%'], 'CREDIT', 141, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DG', 'Provisions pour charges', ARRAY['197%', '198%'], 'CREDIT', 142, 2, false),

-- BILAN - PASSIF CIRCULANT
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DH', 'Dettes circulantes HAO', ARRAY['86%'], 'CREDIT', 150, 1, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DI', 'CLIENTS, AVANCES REÇUES', ARRAY['419%'], 'CREDIT', 160, 1, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DJ', 'FOURNISSEURS D''EXPLOITATION', ARRAY['401%', '403%', '408%'], 'CREDIT', 170, 1, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DK', 'DETTES FISCALES ET SOCIALES', ARRAY['42%', '43%'], 'CREDIT', 180, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DL', 'Dettes fiscales', ARRAY['444%', '445%'], 'CREDIT', 181, 2, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DM', 'Dettes sociales', ARRAY['421%', '422%', '423%', '424%', '426%', '427%', '428%'], 'CREDIT', 182, 2, false),

('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DN', 'AUTRES DETTES', ARRAY['47%'], 'CREDIT', 190, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DO', 'PROVISIONS POUR RISQUES À COURT TERME', ARRAY['499%'], 'CREDIT', 195, 1, false),

-- BILAN - TRÉSORERIE PASSIF
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DQ', 'Banques, crédits d''escompte', ARRAY['565%'], 'CREDIT', 200, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DR', 'Banques, établissements financiers', ARRAY['521%', '566%'], 'CREDIT', 201, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DS', 'Banques, découverts', ARRAY['564%'], 'CREDIT', 202, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'BILAN', 'DT', 'TOTAL TRÉSORERIE-PASSIF', ARRAY[], 'CREDIT', 209, 0, true);

-- COMPTE DE RÉSULTAT - SYSTÈME NORMAL
INSERT INTO mapping_comptes_postes (pays_code, standard_comptable, type_systeme, type_etat, poste_code, poste_libelle, comptes_pattern, signe_normal, ordre_affichage, niveau, est_total) VALUES
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'TA', 'Ventes de marchandises', ARRAY['701%'], 'CREDIT', 10, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'TB', 'Ventes de produits fabriqués', ARRAY['702%', '703%'], 'CREDIT', 11, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'TC', 'Prestations de services', ARRAY['706%'], 'CREDIT', 12, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'TD', 'Subventions d''exploitation', ARRAY['74%'], 'CREDIT', 13, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'TE', 'Autres produits d''exploitation', ARRAY['75%'], 'CREDIT', 14, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'TF', 'Produits financiers', ARRAY['76%'], 'CREDIT', 15, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'TG', 'Produits exceptionnels', ARRAY['77%'], 'CREDIT', 16, 1, false),

('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RA', 'Achats de marchandises', ARRAY['601%'], 'DEBIT', 50, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RB', 'Variation stocks marchandises', ARRAY['6031%', '6032%'], 'DEBIT', 51, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RC', 'Achats de matières premières', ARRAY['602%'], 'DEBIT', 52, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RD', 'Autres achats', ARRAY['604%', '605%', '606%', '607%', '608%'], 'DEBIT', 54, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RF', 'Variation autres stocks', ARRAY['6033%', '6034%'], 'DEBIT', 55, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RG', 'Transports', ARRAY['61%'], 'DEBIT', 56, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RH', 'Services extérieurs', ARRAY['62%'], 'DEBIT', 57, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RI', 'Impôts et taxes', ARRAY['63%'], 'DEBIT', 58, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RJ', 'Autres charges', ARRAY['65%'], 'DEBIT', 59, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RK', 'Charges de personnel', ARRAY['66%'], 'DEBIT', 60, 1, false),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'RL', 'Dotations amortissements et provisions', ARRAY['681%', '691%'], 'DEBIT', 61, 1, false),

-- SOLDES INTERMÉDIAIRES DE GESTION
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'XA', 'MARGE COMMERCIALE', ARRAY[], 'CREDIT', 100, 0, true),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'XB', 'CHIFFRE D''AFFAIRES', ARRAY[], 'CREDIT', 101, 0, true),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'XC', 'VALEUR AJOUTÉE', ARRAY[], 'CREDIT', 102, 0, true),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'XD', 'EXCÉDENT BRUT D''EXPLOITATION', ARRAY[], 'CREDIT', 103, 0, true),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'XE', 'RÉSULTAT D''EXPLOITATION', ARRAY[], 'CREDIT', 104, 0, true),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'XF', 'RÉSULTAT FINANCIER', ARRAY[], 'CREDIT', 105, 0, true),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'XG', 'RÉSULTAT ACTIVITÉS ORDINAIRES', ARRAY[], 'CREDIT', 106, 0, true),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'XH', 'RÉSULTAT HORS ACTIVITÉS ORDINAIRES', ARRAY[], 'CREDIT', 107, 0, true),
('BF', 'SYSCOHADA', 'NORMAL', 'COMPTE_RESULTAT', 'XI', 'RÉSULTAT NET DE L''EXERCICE', ARRAY[], 'CREDIT', 108, 0, true);

-- =====================================================
-- DONNÉES INITIALES - MAPPING SYSTÈME MINIMAL (SMT)
-- =====================================================

-- SYSTÈME MINIMAL - ÉTAT RECETTES/DÉPENSES
INSERT INTO mapping_comptes_postes (pays_code, standard_comptable, type_systeme, type_etat, poste_code, poste_libelle, comptes_pattern, signe_normal, ordre_affichage, niveau, est_total) VALUES
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'R01', 'Ventes de marchandises', ARRAY['701%'], 'CREDIT', 10, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'R02', 'Prestations de services', ARRAY['706%'], 'CREDIT', 11, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'R03', 'Subventions d''exploitation', ARRAY['74%'], 'CREDIT', 12, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'R04', 'Autres recettes d''exploitation', ARRAY['75%'], 'CREDIT', 13, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'R05', 'Revenus financiers', ARRAY['76%'], 'CREDIT', 14, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'R99', 'TOTAL RECETTES', ARRAY[], 'CREDIT', 19, 0, true),

('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D01', 'Achats de marchandises', ARRAY['601%'], 'DEBIT', 30, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D02', 'Services extérieurs', ARRAY['62%'], 'DEBIT', 31, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D03', 'Transports', ARRAY['61%'], 'DEBIT', 32, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D04', 'Autres achats', ARRAY['604%', '605%', '606%', '607%', '608%'], 'DEBIT', 33, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D05', 'Impôts et taxes', ARRAY['63%'], 'DEBIT', 34, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D06', 'Charges de personnel', ARRAY['66%'], 'DEBIT', 35, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D07', 'Autres charges d''exploitation', ARRAY['65%'], 'DEBIT', 36, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D08', 'Charges financières', ARRAY['671%', '672%', '673%'], 'DEBIT', 37, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D09', 'Autres dépenses', ARRAY['87%'], 'DEBIT', 38, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'D99', 'TOTAL DÉPENSES', ARRAY[], 'DEBIT', 39, 0, true),

-- SOLDE SMT
('BF', 'SYSCOHADA', 'MINIMAL', 'RECETTES_DEPENSES', 'S01', 'EXCÉDENT OU DÉFICIT DE LA PÉRIODE', ARRAY[], 'CREDIT', 40, 0, true);

-- SYSTÈME MINIMAL - SITUATION TRÉSORERIE
INSERT INTO mapping_comptes_postes (pays_code, standard_comptable, type_systeme, type_etat, poste_code, poste_libelle, comptes_pattern, signe_normal, ordre_affichage, niveau, est_total) VALUES
('BF', 'SYSCOHADA', 'MINIMAL', 'SITUATION_TRESORERIE', 'T01', 'Caisse', ARRAY['571%', '572%'], 'DEBIT', 10, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'SITUATION_TRESORERIE', 'T02', 'Banques', ARRAY['521%'], 'DEBIT', 11, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'SITUATION_TRESORERIE', 'T03', 'Chèques postaux', ARRAY['531%'], 'DEBIT', 12, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'SITUATION_TRESORERIE', 'T04', 'Autres comptes de trésorerie', ARRAY['541%', '58%'], 'DEBIT', 13, 1, false),
('BF', 'SYSCOHADA', 'MINIMAL', 'SITUATION_TRESORERIE', 'T99', 'TOTAL DISPONIBILITÉS', ARRAY[], 'DEBIT', 19, 0, true);

-- =====================================================
-- PLAN COMPTABLE SYCEBNL INITIAL
-- =====================================================

-- Classes principales
INSERT INTO plan_comptable_sycebnl (numero_compte, intitule_compte, classe_compte, type_compte, niveau, sens_normal, utilise_systeme_normal, utilise_smt, obligatoire_ong) VALUES
('1', 'FINANCEMENT PERMANENT', 'CLASSE_1', 'CLASSE', 1, 'CREDITEUR', true, true, false),
('2', 'IMMOBILISATIONS', 'CLASSE_2', 'CLASSE', 1, 'DEBITEUR', true, true, false),
('3', 'STOCKS', 'CLASSE_3', 'CLASSE', 1, 'DEBITEUR', true, true, false),
('4', 'CRÉANCES ET DETTES', 'CLASSE_4', 'CLASSE', 1, 'DEBITEUR', true, true, false),
('5', 'COMPTES FINANCIERS', 'CLASSE_5', 'CLASSE', 1, 'DEBITEUR', true, true, false),
('6', 'CHARGES', 'CLASSE_6', 'CLASSE', 1, 'DEBITEUR', true, true, false),
('7', 'PRODUITS', 'CLASSE_7', 'CLASSE', 1, 'CREDITEUR', true, true, false),
('8', 'ENGAGEMENTS HORS BILAN', 'CLASSE_8', 'CLASSE', 1, 'DEBITEUR', true, false, true);

-- Comptes spécifiques ONG
INSERT INTO plan_comptable_sycebnl (numero_compte, intitule_compte, classe_compte, type_compte, niveau, compte_parent, sens_normal, utilise_systeme_normal, utilise_smt, obligatoire_ong, description_utilisation) VALUES
('101', 'Fonds associatifs', 'CLASSE_1', 'COMPTE', 2, '1', 'CREDITEUR', true, true, true, 'Capital social des associations'),
('104', 'Fonds de dotation', 'CLASSE_1', 'COMPTE', 2, '1', 'CREDITEUR', true, true, true, 'Fonds de dotation des fondations'),
('105', 'Fonds de réserve', 'CLASSE_1', 'COMPTE', 2, '1', 'CREDITEUR', true, true, true, 'Réserves obligatoires'),
('215', 'Matériel de mission', 'CLASSE_2', 'COMPTE', 2, '2', 'DEBITEUR', true, true, true, 'Matériel spécifique aux missions ONG'),
('218', 'Véhicules de mission', 'CLASSE_2', 'COMPTE', 2, '2', 'DEBITEUR', true, true, true, 'Véhicules pour les missions'),
('7401', 'Subventions de l''État', 'CLASSE_7', 'COMPTE', 2, '7', 'CREDITEUR', true, true, true, 'Subventions publiques'),
('7402', 'Subventions des collectivités', 'CLASSE_7', 'COMPTE', 2, '7', 'CREDITEUR', true, true, true, 'Subventions locales'),
('7403', 'Subventions d''organismes internationaux', 'CLASSE_7', 'COMPTE', 2, '7', 'CREDITEUR', true, true, true, 'Subventions internationales'),
('7501', 'Dons manuels', 'CLASSE_7', 'COMPTE', 2, '7', 'CREDITEUR', true, true, true, 'Dons en espèces'),
('7502', 'Dons en nature', 'CLASSE_7', 'COMPTE', 2, '7', 'CREDITEUR', true, true, true, 'Dons de biens et services'),
('7503', 'Legs et testaments', 'CLASSE_7', 'COMPTE', 2, '7', 'CREDITEUR', true, true, true, 'Héritages et legs'),
('755', 'Cotisations', 'CLASSE_7', 'COMPTE', 2, '7', 'CREDITEUR', true, true, true, 'Cotisations des membres');

-- =====================================================
-- TRIGGERS POUR MISE À JOUR AUTOMATIQUE
-- =====================================================

-- Trigger pour mise à jour automatique de date_modification
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.date_modification = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Application des triggers
CREATE TRIGGER update_mapping_comptes_postes_modtime 
    BEFORE UPDATE ON mapping_comptes_postes 
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_etats_financiers_sycebnl_modtime 
    BEFORE UPDATE ON etats_financiers_sycebnl 
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();

CREATE TRIGGER update_notes_annexes_sycebnl_modtime 
    BEFORE UPDATE ON notes_annexes_sycebnl 
    FOR EACH ROW EXECUTE FUNCTION update_modified_column();






