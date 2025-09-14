# Checklist Sécurité & Déploiement Production E-COMPTA-IA

## 1. Gestion des secrets
- [ ] Tous les mots de passe et clés sensibles sont injectés via Docker secrets ou un gestionnaire externe (Vault, etc.)
- [ ] Aucun secret n'est commité dans le code source
- [ ] Les fichiers d'environnement ne contiennent pas de valeurs sensibles en clair

## 2. Configuration Docker & Réseaux
- [ ] Utilisation d'utilisateurs non-root dans les Dockerfile
- [ ] Réseaux Docker isolés pour chaque service
- [ ] Variables d'environnement sécurisées
- [ ] Volumes persistants pour les données critiques

## 3. Sécurité applicative
- [ ] Headers de sécurité activés dans Nginx (X-Frame-Options, X-Content-Type-Options, X-XSS-Protection)
- [ ] CORS configuré strictement pour les domaines autorisés
- [ ] Rate limiting activé
- [ ] HTTPS activé et certifié

## 4. Monitoring & Alerting
- [ ] Prometheus, Grafana et alerting configurés et actifs
- [ ] Logs centralisés et rotation configurée
- [ ] Alertes email et Slack opérationnelles

## 5. Mise à jour & dépendances
- [ ] Toutes les dépendances Maven et npm sont à jour
- [ ] Vérification régulière des vulnérabilités (npm audit, mvn dependency-check)

## 6. Backup & restauration
- [ ] Backup automatique activé et testé
- [ ] Clés de chiffrement des backups gérées en secret
- [ ] Restauration testée régulièrement

## 7. Tests & validation
- [ ] Tests unitaires et d'intégration automatisés
- [ ] Tests de montée en charge et de sécurité réalisés

## 8. Documentation
- [ ] Documentation technique et procédure de déploiement à jour
- [ ] Guide d'urgence et contacts support disponibles

---
Dernière vérification : 13 septembre 2025
Responsable : [À compléter]
