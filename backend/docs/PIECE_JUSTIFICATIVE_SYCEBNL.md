# Fonctionnalité Pièce Justificative Comptable SYCEBNL

## Vue d'ensemble

Cette fonctionnalité implémente un workflow complet de gestion des pièces justificatives comptables dans le système SYCEBNL, avec analyse OCR, IA, et génération automatique d'écritures comptables.

## Architecture

### Entités principales

1. **PieceJustificativeSycebnl** - Pièce justificative principale
2. **AnalyseOCRSycebnl** - Résultats de l'analyse OCR
3. **AnalyseIASycebnl** - Résultats de l'analyse IA
4. **PropositionEcritureSycebnl** - Propositions d'écritures
5. **LignePropositionEcritureSycebnl** - Lignes des propositions

### Services

1. **PieceJustificativeSycebnlService** - Service principal orchestrant le workflow
2. **AnalyseOCRService** - Analyse OCR des documents
3. **AnalyseIAService** - Analyse IA et extraction d'informations
4. **PropositionEcritureService** - Génération de propositions comptables
5. **GenerationEcritureService** - Génération d'écritures comptables

## Workflow

### 1. Téléchargement de la pièce justificative

```http
POST /api/sycebnl/pieces-justificatives/upload
Content-Type: multipart/form-data
fichier: [fichier PDF/image]
libellePJ: "Facture fournisseur"
datePiece: "2024-01-15"
typePJ: "FACTURE_FOURNISSEUR"
entrepriseId: 1
utilisateurId: 1
```text`r`n### 2. Analyse OCR automatique
L'analyse OCR est déclenchée automatiquement après le téléchargement :
```http
POST /api/sycebnl/pieces-justificatives/{id}/analyse-ocr
```text`r`n**Résultats :**
- Texte extrait du document
- Niveau de confiance OCR
- Langue détectée
- Métadonnées de traitement
### 3. Analyse IA automatique
L'analyse IA est déclenchée automatiquement après l'OCR :
```http
POST /api/sycebnl/pieces-justificatives/{id}/analyse-ia
```text`r`n**Informations extraites :**
- Type de document détecté
- Montant total et détail (HT, TTC, TVA)
- Fournisseur/client
- Date du document
- Numéro de facture
- Description des prestations
### 4. Génération de propositions
```http
POST /api/sycebnl/pieces-justificatives/{id}/generer-propositions
```text`r`n**Types de propositions générées :**
- **Facture fournisseur** : Charge + TVA déductible → Fournisseur
- **Reçu** : Banque/Caisse → Client
- **Bon de commande** : Engagement → Engagement
- **Générique** : Charge → Tiers
### 5. Validation des propositions
```http
POST /api/sycebnl/pieces-justificatives/propositions/{id}/valider
Content-Type: application/json
{
  "validateurId": 1,
  "commentaires": "Proposition validée"
}
```text`r`n### 6. Génération d'écriture comptable
```http
POST /api/sycebnl/pieces-justificatives/propositions/{id}/generer-ecriture
```text`r`n## API REST complète
### Consultation
```http
# Récupérer une PJ par ID
GET /api/sycebnl/pieces-justificatives/{id}
# Récupérer les PJ d'une entreprise
GET /api/sycebnl/pieces-justificatives/entreprise/{entrepriseId}
# Récupérer les PJ par statut
GET /api/sycebnl/pieces-justificatives/statut/{statut}
# Récupérer les propositions d'une PJ
GET /api/sycebnl/pieces-justificatives/{id}/propositions
```text`r`n### Métadonnées
```http
# Types de PJ disponibles
GET /api/sycebnl/pieces-justificatives/types
# Statuts de traitement disponibles
GET /api/sycebnl/pieces-justificatives/statuts
```text`r`n## Statuts de traitement
1. **TELECHARGEE** - PJ téléchargée
2. **ANALYSE_OCR_EN_COURS** - Analyse OCR en cours
3. **ANALYSE_OCR_TERMINEE** - Analyse OCR terminée
4. **ANALYSE_IA_EN_COURS** - Analyse IA en cours
5. **ANALYSE_IA_TERMINEE** - Analyse IA terminée
6. **PROPOSITIONS_GENEREES** - Propositions générées
7. **EN_ATTENTE_VALIDATION** - En attente de validation
8. **VALIDEE** - Validée
9. **ECRITURE_GENEREES** - Écriture générée
10. **REJETEE** - Rejetée
11. **ARCHIVEE** - Archivée
## Types de pièces justificatives
- **FACTURE_FOURNISSEUR** - Facture fournisseur
- **FACTURE_CLIENT** - Facture client
- **BON_COMMANDE** - Bon de commande
- **BON_LIVRAISON** - Bon de livraison
- **RECU** - Reçu
- **BORDEREAU_BANCAIRE** - Bordereau bancaire
- **RELEVE_BANCAIRE** - Relevé bancaire
- **BULLETIN_PAIE** - Bulletin de paie
- **DECLARATION_FISCALE** - Déclaration fiscale
- **CONTRAT** - Contrat
- **AUTRE** - Autre
## Intégration avec le système existant
### Liaison avec les écritures comptables
Les écritures générées sont intégrées dans le système comptable existant :
- Numéro de pièce unique
- Liaison avec l'exercice comptable
- Métadonnées complètes pour traçabilité
- Source marquée comme "IA"
### Plan comptable SYCEBNL
Les propositions utilisent le plan comptable SYCEBNL :
- Comptes de charges (60%)
- Comptes de fournisseurs (401%)
- Comptes de clients (411%)
- Comptes de TVA (445%)
- Comptes de trésorerie (5%)
## Gestion des erreurs
### Analyse OCR
- Confiance faible → Notification pour vérification manuelle
- Erreur de traitement → Statut "REJETEE"
### Analyse IA
- Informations manquantes → Propositions génériques
- Confiance faible → Validation manuelle requise
### Génération d'écritures
- Écriture non équilibrée → Erreur de validation
- Comptes manquants → Création automatique ou erreur
## Sécurité et traçabilité
- Tous les fichiers sont stockés avec checksum
- Historique complet des analyses
- Logs détaillés des opérations
- Validation des utilisateurs pour chaque étape critique
## Performance
- Traitement asynchrone des analyses
- Cache des résultats d'analyse
- Optimisation des requêtes de base de données
- Gestion des timeouts
## Évolutions futures
1. **Intégration OCR réelle** - Tesseract, Google Vision API
2. **Modèles IA avancés** - GPT-4, Claude pour l'analyse
3. **Apprentissage automatique** - Amélioration continue des propositions
4. **Interface utilisateur** - Frontend dédié pour la gestion des PJ
5. **Notifications** - Alertes pour les validations en attente
