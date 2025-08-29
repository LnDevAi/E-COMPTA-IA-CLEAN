# Modèles d'Écriture Comptable - Module E COMPTA IA International

## 1. Architecture des Modèles d'Écriture

### Modèle de Base d'Écriture Comptable

```json
{
  "id": "UUID",
  "numero_piece": "string",
  "date_ecriture": "date",
  "date_piece": "date",
  "reference": "string",
  "libelle": "string",
  "type_ecriture": "enum[NORMALE, OUVERTURE, CLOTURE, A_NOUVEAU]",
  "statut": "enum[BROUILLON, VALIDEE, CLOTUREE]",
  "entreprise_id": "UUID",
  "exercice_id": "UUID",
  "utilisateur_id": "UUID",
  "devise": "string",
  "taux_change": "decimal",
  "total_debit": "decimal",
  "total_credit": "decimal",
  "lignes": [
    {
      "compte_id": "UUID",
      "compte_numero": "string",
      "compte_libelle": "string",
      "libelle_ligne": "string",
      "debit": "decimal",
      "credit": "decimal",
      "tiers_id": "UUID?",
      "centre_cout_id": "UUID?",
      "projet_id": "UUID?",
      "analytique": {}
    }
  ],
  "pieces_jointes": [],
  "metadata": {
    "source": "enum[MANUELLE, IMPORTEE, AUTOMATIQUE, IA]",
    "template_id": "UUID?",
    "validation_ai": {
      "confiance": "decimal",
      "suggestions": []
    }
  }
}
```

## 2. Templates d'Écritures par Type d'Opération

### 2.1 Écritures de Vente

#### Vente avec TVA (SYSCOHADA/OHADA)
```json
{
  "nom": "Vente de marchandises avec TVA",
  "code": "VENTE_TVA_OHADA",
  "standard": "SYSCOHADA",
  "description": "Écriture de vente avec TVA collectée",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "411%",
      "libelle": "Clients",
      "formule": "montant_ht * (1 + taux_tva)"
    },
    {
      "position": "credit",
      "compte_pattern": "701%",
      "libelle": "Ventes de marchandises",
      "formule": "montant_ht"
    },
    {
      "position": "credit",
      "compte_pattern": "4434%",
      "libelle": "TVA collectée",
      "formule": "montant_ht * taux_tva"
    }
  ],
  "variables": {
    "montant_ht": "decimal",
    "taux_tva": "decimal",
    "client_id": "UUID",
    "facture_numero": "string"
  }
}
```

#### Vente Export (Sans TVA)
```json
{
  "nom": "Vente à l'export",
  "code": "VENTE_EXPORT_OHADA",
  "standard": "SYSCOHADA",
  "description": "Vente à l'exportation sans TVA",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "411%",
      "libelle": "Clients",
      "formule": "montant_ht"
    },
    {
      "position": "credit",
      "compte_pattern": "702%",
      "libelle": "Ventes à l'exportation",
      "formule": "montant_ht"
    }
  ]
}
```

### 2.2 Écritures d'Achat

#### Achat avec TVA Déductible
```json
{
  "nom": "Achat de marchandises avec TVA",
  "code": "ACHAT_TVA_OHADA",
  "standard": "SYSCOHADA",
  "description": "Achat avec TVA déductible",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "601%",
      "libelle": "Achats de marchandises",
      "formule": "montant_ht"
    },
    {
      "position": "debit",
      "compte_pattern": "4431%",
      "libelle": "TVA déductible",
      "formule": "montant_ht * taux_tva"
    },
    {
      "position": "credit",
      "compte_pattern": "401%",
      "libelle": "Fournisseurs",
      "formule": "montant_ht * (1 + taux_tva)"
    }
  ]
}
```

### 2.3 Écritures de Paie

#### Salaire Simple
```json
{
  "nom": "Paiement de salaire",
  "code": "PAIE_SALAIRE_OHADA",
  "standard": "SYSCOHADA",
  "description": "Écriture de paiement de salaire",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "661%",
      "libelle": "Rémunérations du personnel",
      "formule": "salaire_brut"
    },
    {
      "position": "credit",
      "compte_pattern": "421%",
      "libelle": "Personnel - Rémunérations dues",
      "formule": "salaire_net"
    },
    {
      "position": "credit",
      "compte_pattern": "431%",
      "libelle": "Sécurité sociale",
      "formule": "cotisations_sociales"
    },
    {
      "position": "credit",
      "compte_pattern": "441%",
      "libelle": "Impôt sur les salaires",
      "formule": "impot_salaire"
    }
  ]
}
```

### 2.4 Écritures Bancaires

#### Virement Bancaire
```json
{
  "nom": "Virement bancaire",
  "code": "VIREMENT_BANCAIRE",
  "standard": "UNIVERSEL",
  "description": "Virement entre comptes bancaires",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "521%",
      "libelle": "Banque destinataire",
      "formule": "montant"
    },
    {
      "position": "credit",
      "compte_pattern": "521%",
      "libelle": "Banque source",
      "formule": "montant"
    }
  ]
}
```

## 3. Modèles d'Assistant IA pour l'Écriture

### 3.1 Prompts de Génération d'Écriture

```json
{
  "prompt_base": "En tant qu'assistant comptable expert en {standard_comptable}, analysez cette opération et générez l'écriture comptable appropriée :",
  "contexte_variables": {
    "standard_comptable": "SYSCOHADA|IFRS|GAAP",
    "pays": "code_pays",
    "devise": "devise_locale",
    "taux_tva": "taux_applicable"
  },
  "exemples": [
    {
      "input": "Vente de marchandises 1.000.000 FCFA HT, TVA 18%",
      "output": {
        "libelle": "Vente marchandises du {date}",
        "lignes": [
          {"411001": {"debit": 1180000, "libelle": "Clients"}},
          {"701001": {"credit": 1000000, "libelle": "Ventes marchandises"}},
          {"443401": {"credit": 180000, "libelle": "TVA collectée"}}
        ]
      }
    }
  ]
}
```

### 3.2 Validation Automatique par IA

```json
{
  "regles_validation": [
    {
      "nom": "Équilibre débit/crédit",
      "type": "OBLIGATOIRE",
      "condition": "total_debit === total_credit",
      "message_erreur": "L'écriture doit être équilibrée"
    },
    {
      "nom": "Cohérence TVA",
      "type": "SUGGESTION",
      "condition": "taux_tva_calcule === taux_tva_legal",
      "message_suggestion": "Vérifiez le taux de TVA appliqué"
    },
    {
      "nom": "Comptes autorisés",
      "type": "VALIDATION",
      "condition": "compte IN plan_comptable_actif",
      "message_erreur": "Compte non autorisé dans ce plan comptable"
    }
  ]
}
```

## 4. Adaptateurs Multi-Standards

### 4.1 Mapping SYSCOHADA → IFRS
```json
{
  "mappings": {
    "SYSCOHADA_to_IFRS": {
      "411": "Trade receivables",
      "401": "Trade payables",
      "701": "Revenue from contracts with customers",
      "601": "Cost of sales",
      "521": "Cash and cash equivalents"
    }
  }
}
```

### 4.2 Templates Adaptatifs par Pays
```json
{
  "templates_par_pays": {
    "BF": {
      "standard": "SYSCOHADA",
      "taux_tva": 0.18,
      "devise": "XOF",
      "particularites": {
        "tva_export": false,
        "retenue_source": true
      }
    },
    "CI": {
      "standard": "SYSCOHADA",
      "taux_tva": 0.18,
      "devise": "XOF",
      "particularites": {
        "tva_export": false,
        "taxe_unique": true
      }
    }
  }
}
```

## 5. Interface Utilisateur - Composants Angular

### 5.1 Formulaire de Saisie Intelligent
```typescript
interface EcritureFormModel {
  date_piece: Date;
  reference: string;
  libelle: string;
  type_operation: string;
  montant_ht?: number;
  taux_tva?: number;
  tiers?: Tiers;
  lignes: LigneEcriture[];
  suggestions_ai?: SuggestionIA[];
}

interface SuggestionIA {
  confiance: number;
  description: string;
  template_propose: string;
  lignes_proposees: LigneEcriture[];
}
```

### 5.2 Validation Temps Réel
```typescript
class ValidationService {
  validerEcriture(ecriture: EcritureFormModel): ValidationResult {
    const erreurs = [];
    const suggestions = [];
    
    // Validation équilibre
    if (!this.isEquilibree(ecriture)) {
      erreurs.push("Écriture non équilibrée");
    }
    
    // Validation IA
    const validationIA = this.aiService.valider(ecriture);
    suggestions.push(...validationIA.suggestions);
    
    return { erreurs, suggestions };
  }
}
```

## 6. Base de Connaissances IA

### 6.1 Règles Comptables SYSCOHADA
```json
{
  "regles_syscohada": {
    "principe_equilibre": "Toute écriture doit respecter l'équation : Débit = Crédit",
    "principe_partie_double": "Chaque opération affecte au moins deux comptes",
    "nomenclature": {
      "classe_1": "Comptes de ressources durables",
      "classe_2": "Comptes d'actif immobilisé",
      "classe_3": "Comptes de stocks",
      "classe_4": "Comptes de tiers",
      "classe_5": "Comptes de trésorerie",
      "classe_6": "Comptes de charges",
      "classe_7": "Comptes de produits"
    },
    "tva": {
      "taux_normal": 0.18,
      "taux_reduit": 0.09,
      "exonerations": ["exports", "operations_bancaires", "transport_international"]
    }
  }
}
```

### 6.2 Patterns de Reconnaissance IA
```json
{
  "patterns_reconnaissance": {
    "vente": {
      "mots_cles": ["vente", "facturation", "client", "CA"],
      "template_default": "VENTE_TVA_OHADA",
      "comptes_probables": ["411%", "701%", "4434%"]
    },
    "achat": {
      "mots_cles": ["achat", "fournisseur", "approvisionnement"],
      "template_default": "ACHAT_TVA_OHADA",
      "comptes_probables": ["401%", "601%", "4431%"]
    },
    "paie": {
      "mots_cles": ["salaire", "paie", "rémunération", "personnel"],
      "template_default": "PAIE_SALAIRE_OHADA",
      "comptes_probables": ["661%", "421%", "431%", "441%"]
    }
  }
}
```

### 2.5 Écritures de Trésorerie

#### Encaissement Client
```json
{
  "nom": "Encaissement client",
  "code": "ENCAISSEMENT_CLIENT",
  "standard": "SYSCOHADA",
  "description": "Règlement d'une créance client",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "montant"
    },
    {
      "position": "credit",
      "compte_pattern": "411%",
      "libelle": "Clients",
      "formule": "montant"
    }
  ]
}
```

#### Paiement Fournisseur
```json
{
  "nom": "Paiement fournisseur",
  "code": "PAIEMENT_FOURNISSEUR",
  "standard": "SYSCOHADA",
  "description": "Règlement d'une dette fournisseur",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "401%",
      "libelle": "Fournisseurs",
      "formule": "montant"
    },
    {
      "position": "credit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "montant"
    }
  ]
}
```

#### Remise en Banque
```json
{
  "nom": "Remise en banque",
  "code": "REMISE_BANQUE",
  "standard": "SYSCOHADA",
  "description": "Remise de chèques ou espèces",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "montant"
    },
    {
      "position": "credit",
      "compte_pattern": "531%",
      "libelle": "Caisse",
      "formule": "montant"
    }
  ]
}
```

#### Frais Bancaires
```json
{
  "nom": "Frais bancaires",
  "code": "FRAIS_BANCAIRES",
  "standard": "SYSCOHADA",
  "description": "Prélèvement automatique frais bancaires",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "631%",
      "libelle": "Frais bancaires",
      "formule": "montant"
    },
    {
      "position": "credit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "montant"
    }
  ]
}
```

#### Agios et Intérêts Débiteurs
```json
{
  "nom": "Agios",
  "code": "AGIOS_INTERETS",
  "standard": "SYSCOHADA",
  "description": "Intérêts et agios bancaires",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "671%",
      "libelle": "Intérêts des emprunts",
      "formule": "montant"
    },
    {
      "position": "credit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "montant"
    }
  ]
}
```

### 2.6 Écritures d'Immobilisations

#### Acquisition d'Immobilisation
```json
{
  "nom": "Acquisition immobilisation",
  "code": "ACHAT_IMMOBILISATION",
  "standard": "SYSCOHADA",
  "description": "Achat d'une immobilisation corporelle",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "2%",
      "libelle": "Immobilisations corporelles",
      "formule": "montant_ht"
    },
    {
      "position": "debit",
      "compte_pattern": "4431%",
      "libelle": "TVA déductible",
      "formule": "montant_ht * taux_tva"
    },
    {
      "position": "credit",
      "compte_pattern": "404%",
      "libelle": "Fournisseurs d'immobilisations",
      "formule": "montant_ht * (1 + taux_tva)"
    }
  ]
}
```

#### Dotation aux Amortissements
```json
{
  "nom": "Dotation aux amortissements",
  "code": "DOTATION_AMORTISSEMENT",
  "standard": "SYSCOHADA",
  "description": "Constatation de l'amortissement",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "681%",
      "libelle": "Dotations aux amortissements",
      "formule": "montant_amortissement"
    },
    {
      "position": "credit",
      "compte_pattern": "28%",
      "libelle": "Amortissements",
      "formule": "montant_amortissement"
    }
  ]
}
```

#### Cession d'Immobilisation
```json
{
  "nom": "Cession immobilisation",
  "code": "CESSION_IMMOBILISATION",
  "standard": "SYSCOHADA",
  "description": "Vente d'une immobilisation",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "462%",
      "libelle": "Créances sur cessions d'immo",
      "formule": "prix_vente"
    },
    {
      "position": "credit",
      "compte_pattern": "82%",
      "libelle": "Produits de cessions d'immo",
      "formule": "prix_vente"
    },
    {
      "position": "debit",
      "compte_pattern": "81%",
      "libelle": "Valeur comptable cédée",
      "formule": "valeur_nette_comptable"
    },
    {
      "position": "credit",
      "compte_pattern": "2%",
      "libelle": "Immobilisations",
      "formule": "valeur_origine"
    },
    {
      "position": "debit",
      "compte_pattern": "28%",
      "libelle": "Amortissements",
      "formule": "cumul_amortissements"
    }
  ]
}
```

### 2.7 Écritures de Stock

#### Entrée de Stock
```json
{
  "nom": "Entrée de stock",
  "code": "ENTREE_STOCK",
  "standard": "SYSCOHADA",
  "description": "Entrée marchandises en stock",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "31%",
      "libelle": "Stocks de marchandises",
      "formule": "valeur_stock"
    },
    {
      "position": "credit",
      "compte_pattern": "603%",
      "libelle": "Variation des stocks",
      "formule": "valeur_stock"
    }
  ]
}
```

#### Sortie de Stock
```json
{
  "nom": "Sortie de stock",
  "code": "SORTIE_STOCK",
  "standard": "SYSCOHADA",
  "description": "Sortie marchandises du stock",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "603%",
      "libelle": "Variation des stocks",
      "formule": "cout_sortie"
    },
    {
      "position": "credit",
      "compte_pattern": "31%",
      "libelle": "Stocks de marchandises",
      "formule": "cout_sortie"
    }
  ]
}
```

#### Dépréciation de Stock
```json
{
  "nom": "Dépréciation de stock",
  "code": "DEPRECIATION_STOCK",
  "standard": "SYSCOHADA",
  "description": "Constatation de dépréciation",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "659%",
      "libelle": "Charges provisionnées",
      "formule": "montant_depreciation"
    },
    {
      "position": "credit",
      "compte_pattern": "39%",
      "libelle": "Dépréciations des stocks",
      "formule": "montant_depreciation"
    }
  ]
}
```

### 2.8 Écritures Fiscales et Sociales

#### Déclaration TVA
```json
{
  "nom": "Déclaration TVA",
  "code": "DECLARATION_TVA",
  "standard": "SYSCOHADA",
  "description": "Liquidation de la TVA",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "4434%",
      "libelle": "TVA collectée",
      "formule": "tva_collectee"
    },
    {
      "position": "credit",
      "compte_pattern": "4431%",
      "libelle": "TVA déductible",
      "formule": "tva_deductible"
    },
    {
      "position": "credit",
      "compte_pattern": "4441%",
      "libelle": "TVA due",
      "formule": "tva_collectee - tva_deductible"
    }
  ]
}
```

#### Paiement TVA
```json
{
  "nom": "Paiement TVA",
  "code": "PAIEMENT_TVA",
  "standard": "SYSCOHADA",
  "description": "Règlement de la TVA due",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "4441%",
      "libelle": "TVA due",
      "formule": "montant_tva"
    },
    {
      "position": "credit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "montant_tva"
    }
  ]
}
```

#### Charges Sociales
```json
{
  "nom": "Déclaration charges sociales",
  "code": "CHARGES_SOCIALES",
  "standard": "SYSCOHADA",
  "description": "Provisions pour charges sociales",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "663%",
      "libelle": "Charges sociales",
      "formule": "montant_charges"
    },
    {
      "position": "credit",
      "compte_pattern": "431%",
      "libelle": "Sécurité sociale",
      "formule": "montant_charges"
    }
  ]
}
```

#### Impôt sur les Bénéfices
```json
{
  "nom": "Provision impôt bénéfices",
  "code": "PROVISION_IS",
  "standard": "SYSCOHADA",
  "description": "Provision pour impôt sur les bénéfices",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "891%",
      "libelle": "Impôts sur les bénéfices",
      "formule": "montant_is"
    },
    {
      "position": "credit",
      "compte_pattern": "442%",
      "libelle": "État - Impôts sur les bénéfices",
      "formule": "montant_is"
    }
  ]
}
```

### 2.9 Écritures de Financement

#### Emprunt Bancaire
```json
{
  "nom": "Souscription emprunt",
  "code": "EMPRUNT_BANCAIRE",
  "standard": "SYSCOHADA",
  "description": "Mise à disposition d'un emprunt",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "montant_emprunt"
    },
    {
      "position": "credit",
      "compte_pattern": "161%",
      "libelle": "Emprunts et dettes",
      "formule": "montant_emprunt"
    }
  ]
}
```

#### Remboursement Emprunt
```json
{
  "nom": "Remboursement emprunt",
  "code": "REMBOURSEMENT_EMPRUNT",
  "standard": "SYSCOHADA",
  "description": "Remboursement capital + intérêts",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "161%",
      "libelle": "Emprunts et dettes",
      "formule": "capital"
    },
    {
      "position": "debit",
      "compte_pattern": "671%",
      "libelle": "Intérêts des emprunts",
      "formule": "interets"
    },
    {
      "position": "credit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "capital + interets"
    }
  ]
}
```

#### Augmentation de Capital
```json
{
  "nom": "Augmentation de capital",
  "code": "AUGMENTATION_CAPITAL",
  "standard": "SYSCOHADA",
  "description": "Souscription et libération capital",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "montant_libere"
    },
    {
      "position": "credit",
      "compte_pattern": "101%",
      "libelle": "Capital social",
      "formule": "valeur_nominale"
    },
    {
      "position": "credit",
      "compte_pattern": "104%",
      "libelle": "Prime d'émission",
      "formule": "montant_libere - valeur_nominale"
    }
  ]
}
```

#### Distribution Dividendes
```json
{
  "nom": "Distribution dividendes",
  "code": "DIVIDENDES",
  "standard": "SYSCOHADA",
  "description": "Mise en paiement dividendes",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "119%",
      "libelle": "Report à nouveau",
      "formule": "dividendes"
    },
    {
      "position": "credit",
      "compte_pattern": "465%",
      "libelle": "Associés - Dividendes à payer",
      "formule": "dividendes"
    }
  ]
}
```

### 2.10 Écritures de Régularisation

#### Charges Constatées d'Avance
```json
{
  "nom": "Charges constatées d'avance",
  "code": "CCA",
  "standard": "SYSCOHADA",
  "description": "Régularisation charges payées d'avance",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "476%",
      "libelle": "Charges constatées d'avance",
      "formule": "montant_cca"
    },
    {
      "position": "credit",
      "compte_pattern": "6%",
      "libelle": "Charges d'exploitation",
      "formule": "montant_cca"
    }
  ]
}
```

#### Produits Constatés d'Avance
```json
{
  "nom": "Produits constatés d'avance",
  "code": "PCA",
  "standard": "SYSCOHADA",
  "description": "Régularisation produits perçus d'avance",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "7%",
      "libelle": "Produits d'exploitation",
      "formule": "montant_pca"
    },
    {
      "position": "credit",
      "compte_pattern": "477%",
      "libelle": "Produits constatés d'avance",
      "formule": "montant_pca"
    }
  ]
}
```

#### Charges à Payer
```json
{
  "nom": "Charges à payer",
  "code": "CHARGES_A_PAYER",
  "standard": "SYSCOHADA",
  "description": "Régularisation charges non encore payées",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "6%",
      "libelle": "Charges d'exploitation",
      "formule": "montant_charge"
    },
    {
      "position": "credit",
      "compte_pattern": "478%",
      "libelle": "Charges à payer",
      "formule": "montant_charge"
    }
  ]
}
```

#### Produits à Recevoir
```json
{
  "nom": "Produits à recevoir",
  "code": "PRODUITS_A_RECEVOIR",
  "standard": "SYSCOHADA",
  "description": "Régularisation produits non encore perçus",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "487%",
      "libelle": "Produits à recevoir",
      "formule": "montant_produit"
    },
    {
      "position": "credit",
      "compte_pattern": "7%",
      "libelle": "Produits d'exploitation",
      "formule": "montant_produit"
    }
  ]
}
```

### 2.11 Écritures de Change

#### Achat Devise
```json
{
  "nom": "Achat de devise",
  "code": "ACHAT_DEVISE",
  "standard": "UNIVERSEL",
  "description": "Acquisition de devises étrangères",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "522%",
      "libelle": "Banque devise",
      "formule": "montant_devise * taux_achat"
    },
    {
      "position": "debit",
      "compte_pattern": "631%",
      "libelle": "Frais de change",
      "formule": "commission"
    },
    {
      "position": "credit",
      "compte_pattern": "521%",
      "libelle": "Banque locale",
      "formule": "(montant_devise * taux_achat) + commission"
    }
  ]
}
```

#### Écart de Change
```json
{
  "nom": "Écart de change",
  "code": "ECART_CHANGE",
  "standard": "SYSCOHADA",
  "description": "Constatation écart de change",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "766%|666%",
      "libelle": "Gains/Pertes de change",
      "formule": "ecart_change"
    },
    {
      "position": "credit",
      "compte_pattern": "411%|401%",
      "libelle": "Créances/Dettes en devise",
      "formule": "ecart_change"
    }
  ]
}
```

### 2.12 Écritures de Production

#### Coût de Production
```json
{
  "nom": "Coût de production",
  "code": "COUT_PRODUCTION",
  "standard": "SYSCOHADA",
  "description": "Incorporation coûts de production",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "33%",
      "libelle": "Produits en cours",
      "formule": "cout_production"
    },
    {
      "position": "credit",
      "compte_pattern": "72%",
      "libelle": "Production immobilisée",
      "formule": "cout_production"
    }
  ]
}
```

#### Déchets et Rebuts
```json
{
  "nom": "Vente déchets",
  "code": "VENTE_DECHETS",
  "standard": "SYSCOHADA",
  "description": "Valorisation des déchets de production",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "531%",
      "libelle": "Caisse",
      "formule": "prix_vente"
    },
    {
      "position": "credit",
      "compte_pattern": "758%",
      "libelle": "Produits divers",
      "formule": "prix_vente"
    }
  ]
}
```

### 2.13 Écritures Spéciales

#### Écriture d'Ouverture
```json
{
  "nom": "Écriture d'ouverture",
  "code": "OUVERTURE_EXERCICE",
  "standard": "UNIVERSEL",
  "description": "Reprise des soldes de l'exercice précédent",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "ACTIF",
      "libelle": "Comptes d'actif",
      "formule": "solde_precedent"
    },
    {
      "position": "credit",
      "compte_pattern": "PASSIF",
      "libelle": "Comptes de passif",
      "formule": "solde_precedent"
    }
  ]
}
```

#### Écriture de Clôture
```json
{
  "nom": "Écriture de clôture",
  "code": "CLOTURE_EXERCICE",
  "standard": "UNIVERSEL",
  "description": "Soldage des comptes de résultat",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "7%",
      "libelle": "Comptes de produits",
      "formule": "total_produits"
    },
    {
      "position": "credit",
      "compte_pattern": "6%",
      "libelle": "Comptes de charges",
      "formule": "total_charges"
    },
    {
      "position": "credit",
      "compte_pattern": "130%",
      "libelle": "Résultat de l'exercice",
      "formule": "total_produits - total_charges"
    }
  ]
}
```

#### Report à Nouveau
```json
{
  "nom": "Affectation résultat",
  "code": "AFFECTATION_RESULTAT",
  "standard": "SYSCOHADA",
  "description": "Affectation du résultat de l'exercice",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "130%",
      "libelle": "Résultat de l'exercice",
      "formule": "resultat"
    },
    {
      "position": "credit",
      "compte_pattern": "119%",
      "libelle": "Report à nouveau",
      "formule": "part_rn"
    },
    {
      "position": "credit",
      "compte_pattern": "106%",
      "libelle": "Réserves",
      "formule": "part_reserves"
    },
    {
      "position": "credit",
      "compte_pattern": "465%",
      "libelle": "Dividendes à distribuer",
      "formule": "part_dividendes"
    }
  ]
}
```

### 2.14 Écritures de Provisions

#### Provision pour Risques
```json
{
  "nom": "Provision pour risques",
  "code": "PROVISION_RISQUES",
  "standard": "SYSCOHADA",
  "description": "Constitution provision pour risques",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "691%",
      "libelle": "Dotations aux provisions",
      "formule": "montant_provision"
    },
    {
      "position": "credit",
      "compte_pattern": "15%",
      "libelle": "Provisions pour risques",
      "formule": "montant_provision"
    }
  ]
}
```

#### Provision Clients Douteux
```json
{
  "nom": "Provision clients douteux",
  "code": "PROVISION_CLIENTS",
  "standard": "SYSCOHADA",
  "description": "Dépréciation créances douteuses",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "416%",
      "libelle": "Clients douteux",
      "formule": "creance_douteuse"
    },
    {
      "position": "credit",
      "compte_pattern": "411%",
      "libelle": "Clients",
      "formule": "creance_douteuse"
    },
    {
      "position": "debit",
      "compte_pattern": "654%",
      "libelle": "Pertes sur créances",
      "formule": "montant_depreciation"
    },
    {
      "position": "credit",
      "compte_pattern": "491%",
      "libelle": "Dépréciation clients",
      "formule": "montant_depreciation"
    }
  ]
}
```

### 2.15 Écritures de Location

#### Location Financement (Crédit-bail)
```json
{
  "nom": "Crédit-bail",
  "code": "CREDIT_BAIL",
  "standard": "SYSCOHADA",
  "description": "Redevance de crédit-bail",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "612%",
      "libelle": "Redevances de crédit-bail",
      "formule": "redevance"
    },
    {
      "position": "credit",
      "compte_pattern": "401%",
      "libelle": "Fournisseurs",
      "formule": "redevance"
    }
  ]
}
```

#### Location Opérationnelle
```json
{
  "nom": "Loyer",
  "code": "LOYER_OPERATIONNEL",
  "standard": "SYSCOHADA",
  "description": "Charge de loyer",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "613%",
      "libelle": "Locations",
      "formule": "montant_loyer"
    },
    {
      "position": "credit",
      "compte_pattern": "401%|521%",
      "libelle": "Fournisseurs ou Banque",
      "formule": "montant_loyer"
    }
  ]
}
```

### 2.16 Écritures d'Assurance

#### Prime d'Assurance
```json
{
  "nom": "Prime d'assurance",
  "code": "PRIME_ASSURANCE",
  "standard": "SYSCOHADA",
  "description": "Paiement prime d'assurance",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "616%",
      "libelle": "Primes d'assurance",
      "formule": "prime_annuelle"
    },
    {
      "position": "credit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "prime_annuelle"
    }
  ]
}
```

#### Indemnité d'Assurance
```json
{
  "nom": "Indemnité assurance",
  "code": "INDEMNITE_ASSURANCE",
  "standard": "SYSCOHADA",
  "description": "Réception indemnité d'assurance",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "indemnite"
    },
    {
      "position": "credit",
      "compte_pattern": "758%",
      "libelle": "Produits divers",
      "formule": "indemnite"
    }
  ]
}
```

### 2.17 Écritures de Subventions

#### Réception Subvention
```json
{
  "nom": "Subvention reçue",
  "code": "SUBVENTION_RECUE",
  "standard": "SYSCOHADA",
  "description": "Encaissement subvention d'exploitation",
  "comptes_pattern": [
    {
      "position": "debit",
      "compte_pattern": "521%",
      "libelle": "Banque",
      "formule": "montant_subvention"
    },
    {
      "position": "credit",
      "compte_pattern": "74%",