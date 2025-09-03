# 🚀 GUIDE DE DÉPLOIEMENT DOCKER E-COMPTA-IA

## 📋 PRÉREQUIS

### Système
- Docker Desktop installé et démarré
- Au moins 4GB de RAM disponible
- 10GB d'espace disque libre
- Windows 10/11 ou Linux/MacOS

### Ports requis
- **8080** : Backend Spring Boot
- **4200** : Frontend Angular
- **5432** : PostgreSQL
- **5050** : PgAdmin

## 🔧 INSTALLATION ET DÉPLOIEMENT

### 1. Déploiement automatique (Recommandé)

```bash
# Exécuter le script de déploiement
./deploy-and-test.bat
```

### 2. Déploiement manuel

```bash
# 1. Nettoyage
docker-compose -f docker/docker-compose.yml down -v
docker system prune -f

# 2. Construction
docker-compose -f docker/docker-compose.yml build --no-cache

# 3. Démarrage
docker-compose -f docker/docker-compose.yml up -d

# 4. Vérification
docker-compose -f docker/docker-compose.yml ps
```

## 🌐 ACCÈS À L'APPLICATION

### URLs principales
- **Frontend** : http://localhost:4200
- **Backend API** : http://localhost:8080
- **PgAdmin** : http://localhost:5050

### Identifiants par défaut
- **PgAdmin** : admin@ecomptaia.com / admin
- **Application** : admin@ecomptaia.com / admin123

## 🔍 DIAGNOSTIC ET TROUBLESHOOTING

### Vérification de l'état des services

```bash
# État des conteneurs
docker-compose -f docker/docker-compose.yml ps

# Logs en temps réel
docker-compose -f docker/docker-compose.yml logs -f

# Logs d'un service spécifique
docker-compose -f docker/docker-compose.yml logs backend
docker-compose -f docker/docker-compose.yml logs frontend
docker-compose -f docker/docker-compose.yml logs postgres
```

### Tests de connectivité

```bash
# Test PostgreSQL
docker exec ecomptaia-postgres pg_isready -U ecomptaia_user -d ecomptaia_db

# Test Backend
curl http://localhost:8080/api/health

# Test Frontend
curl http://localhost:4200/health
```

### Problèmes courants

#### 1. Erreur CORS
**Symptôme** : Erreurs CORS dans la console du navigateur
**Solution** : Vérifier que le backend est démarré et accessible

#### 2. Base de données non accessible
**Symptôme** : Erreurs de connexion à PostgreSQL
**Solution** : 
```bash
docker-compose -f docker/docker-compose.yml restart postgres
```

#### 3. Frontend affiche des données mockées
**Symptôme** : Dashboard avec données factices
**Solution** : Vérifier la connexion au backend et les logs

#### 4. Ports déjà utilisés
**Symptôme** : Erreur "port already in use"
**Solution** : 
```bash
# Arrêter les services utilisant les ports
netstat -ano | findstr :8080
netstat -ano | findstr :4200
netstat -ano | findstr :5432
```

## 📊 MONITORING

### Métriques système
```bash
# Utilisation des ressources
docker stats

# Espace disque
docker system df
```

### Logs d'application
```bash
# Logs du backend
docker logs ecomptaia-backend

# Logs du frontend
docker logs ecomptaia-frontend

# Logs de la base de données
docker logs ecomptaia-postgres
```

## 🔄 MAINTENANCE

### Sauvegarde de la base de données
```bash
# Sauvegarde
docker exec ecomptaia-postgres pg_dump -U ecomptaia_user ecomptaia_db > backup.sql

# Restauration
docker exec -i ecomptaia-postgres psql -U ecomptaia_user ecomptaia_db < backup.sql
```

### Mise à jour de l'application
```bash
# Arrêt des services
docker-compose -f docker/docker-compose.yml down

# Pull des dernières images
docker-compose -f docker/docker-compose.yml pull

# Redémarrage
docker-compose -f docker/docker-compose.yml up -d
```

### Nettoyage
```bash
# Arrêt complet
docker-compose -f docker/docker-compose.yml down -v

# Nettoyage des images
docker system prune -a

# Nettoyage des volumes
docker volume prune
```

## 🛡️ SÉCURITÉ

### Variables d'environnement sensibles
- Modifier les mots de passe par défaut
- Utiliser des secrets Docker pour les clés JWT
- Configurer HTTPS en production

### Accès réseau
- Limiter l'accès aux ports sensibles
- Utiliser un reverse proxy (nginx/traefik)
- Configurer un firewall

## 📈 PRODUCTION

### Recommandations pour la production
1. **Base de données** : Utiliser PostgreSQL externe
2. **Sécurité** : HTTPS obligatoire
3. **Monitoring** : Prometheus + Grafana
4. **Logs** : ELK Stack ou équivalent
5. **Backup** : Automatisation des sauvegardes
6. **Scaling** : Kubernetes pour le scaling horizontal

### Configuration de production
```bash
# Variables d'environnement de production
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET_KEY=your-secure-jwt-secret
export SPRING_DATASOURCE_PASSWORD=your-secure-db-password
```

## 📞 SUPPORT

### En cas de problème
1. Vérifier les logs : `docker-compose logs`
2. Tester la connectivité : `curl http://localhost:8080/api/health`
3. Vérifier les ressources : `docker stats`
4. Consulter la documentation technique

### Contacts
- **Support technique** : support@ecomptaia.com
- **Documentation** : Voir les fichiers README.md
- **Issues** : Créer une issue sur le repository

---

**Version** : 1.0.0  
**Dernière mise à jour** : $(date)  
**Auteur** : Équipe E-COMPTA-IA

