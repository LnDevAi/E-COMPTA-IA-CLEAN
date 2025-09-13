## E-COMPTA-IA - Journal de progression

Dernière mise à jour: auto

Fait récemment:
- Basculé les tests vers PostgreSQL (profil `test`), provisioning local PostgreSQL.
- Ajouté Flyway et exécution des migrations réelles.
- Corrigé index dupliqué dans `V1__Create_All_Tables.sql` (IF NOT EXISTS).
- Ajouté une entreprise par défaut `companies.id=1` pour les seeds dépendants.
- Corrigé schéma SYCEBNL: ajouté `is_active` à `sycebnl_budgets`.

En cours:
- Corriger `V4__Create_GED_Tables.sql`: seed référence `users.id=1` inexistant ⇒ ajout d’un utilisateur admin par défaut dans `V1`.

À venir (prochaines étapes):
- Stabiliser toutes les migrations (CRM, GED, SYCEBNL) sur PostgreSQL.
- Corriger persistance `BalanceComptable`/`SoldeCompte` (cascade ou ordre de persistance).
- Finitions backend tests, puis build frontend Angular et corrections.

\n- Final sync: prêt pour clonage local (PostgreSQL tests, Flyway configuré).\n
