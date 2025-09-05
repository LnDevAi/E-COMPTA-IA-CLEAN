# 🚀 Guide de Déploiement E-COMPTA-IA avec Podman Desktop

## 📋 Table des Matières

1. [Vue d'ensemble](#vue-densemble)
2. [Prérequis](#prérequis)
3. [Installation de Podman Desktop](#installation-de-podman-desktop)
4. [Configuration du Projet](#configuration-du-projet)
5. [Déploiement](#déploiement)
6. [Vérification](#vérification)
7. [Gestion des Services](#gestion-des-services)
8. [Monitoring](#monitoring)
9. [Dépannage](#dépannage)
10. [Sécurité](#sécurité)

## 🎯 Vue d'ensemble

E-COMPTA-IA INTERNATIONAL est une application de comptabilité complète composée de :

- **Backend** : Spring Boot 3.2.5 avec Java 17
- **Frontend** : Angular 20.2.0 avec Angular Material
- **Base de données** : PostgreSQL 15
- **Cache** : Redis 7
- **Reverse Proxy** : Nginx 1.25
- **Monitoring** : Prometheus + Grafana

### Architecture des Services

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   PostgreSQL    │
│   (Angular)     │◄──►│   (Spring Boot) │◄──►│   (Database)    │
│   Port: 4200    │    │   Port: 8080    │    │   Port: 5432    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Nginx         │    │   Redis         │    │   Monitoring    │
│   (Reverse      │    │   (Cache)       │    │   (Prometheus   │
│   Proxy)        │    │   Port: 6379    │    │   + Grafana)    │
│   Port: 80      │    └─────────────────┘    └─────────────────┘
└─────────────────┘
```

## 🔧 Prérequis

### Système d'exploitation
- **Windows 10/11** (recommandé)
- **macOS 10.15+**
- **Linux** (Ubuntu 20.04+, CentOS 8+, Fedora 35+)

### Ressources système
- **RAM** : Minimum 4GB, Recommandé 8GB+
- **CPU** : Minimum 2 cœurs, Recommandé 4 cœurs+
- **Stockage** : Minimum 10GB d'espace libre
- **Réseau** : Connexion Internet pour télécharger les images

### Logiciels requis
- **Podman Desktop** (voir section installation)
- **Git** (pour cloner le projet)
- **PowerShell** (Windows) ou **Bash** (Linux/macOS)

## 📥 Installation de Podman Desktop

### Windows

1. **Télécharger Podman Desktop**
   ```powershell
   # Via winget (recommandé)
   winget install RedHat.Podman-Desktop
   
   # Ou télécharger depuis le site officiel
   # https://podman-desktop.io/downloads/windows
   ```

2. **Installer Podman Desktop**
   - Exécuter l'installateur téléchargé
   - Suivre les instructions d'installation
   - Redémarrer l'ordinateur si demandé

3. **Vérifier l'installation**
   ```powershell
   podman --version
   podman info
   ```

### macOS

1. **Via Homebrew (recommandé)**
   ```bash
   brew install podman
   brew install --cask podman-desktop
   ```

2. **Initialiser Podman**
   ```bash
   podman machine init
   podman machine start
   ```

### Linux (Ubuntu/Debian)

1. **Installer Podman**
   ```bash
   sudo apt update
   sudo apt install -y podman podman-compose
   ```

2. **Configurer Podman**
   ```bash
   # Activer le service
   sudo systemctl enable --now podman.socket
   
   # Configurer les permissions
   sudo usermod --add-subuids 100000-165535 --add-subgids 100000-165535 $USER
   ```

## ⚙️ Configuration du Projet

### 1. Cloner le Projet

```bash
git clone <repository-url>
cd E-COMPTA-IA-CLEAN
```

### 2. Configurer les Variables d'Environnement

Éditer le fichier `env.podman` :

```bash
# Configuration de base
VERSION=1.0.0
ENVIRONMENT=production

# Ports (gestion automatique recommandée)
BACKEND_PORT=8080
FRONTEND_PORT=4200
NGINX_PORT=80
POSTGRES_PORT=5432
REDIS_PORT=6379

# Base de données
POSTGRES_USER=ecomptaia_user
POSTGRES_PASSWORD=CHANGE_ME_SECURE_PASSWORD_2024
POSTGRES_DB=ecomptaia_db

# Redis
REDIS_PASSWORD=CHANGE_ME_REDIS_PASSWORD_2024

# JWT (GÉNÉRER DES CLÉS SÉCURISÉES)
JWT_SECRET=CHANGE_ME_GENERATE_256_BIT_SECRET_KEY_2024
```

### 3. Créer les Répertoires de Données

```bash
mkdir -p data/postgres data/redis data/logs backup
```

## 🚀 Déploiement

### Méthode 1 : Script Automatique (Recommandé)

#### Windows (PowerShell)
```powershell
# Déploiement standard
.\deploy-podman-complete.ps1

# Déploiement avec options
.\deploy-podman-complete.ps1 -Environment production -Force

# Aide
.\deploy-podman-complete.ps1 -Help
```

#### Linux/macOS (Bash)
```bash
# Rendre le script exécutable
chmod +x deploy-podman-complete.sh

# Déploiement standard
./deploy-podman-complete.sh

# Déploiement avec options
SKIP_BUILD=true ./deploy-podman-complete.sh
FORCE=true ./deploy-podman-complete.sh production
```

### Méthode 2 : Commandes Manuelles

```bash
# 1. Construire les images
podman-compose -f docker-compose-podman.yml build

# 2. Démarrer les services
podman-compose -f docker-compose-podman.yml up -d

# 3. Vérifier le statut
podman-compose -f docker-compose-podman.yml ps
```

## ✅ Vérification

### 1. Vérifier les Services

```bash
# Statut des conteneurs
podman-compose -f docker-compose-podman.yml ps

# Logs en temps réel
podman-compose -f docker-compose-podman.yml logs -f

# Logs d'un service spécifique
podman-compose -f docker-compose-podman.yml logs -f backend
```

### 2. Tests de Santé

```bash
# Backend
curl http://localhost:8080/api/actuator/health

# Frontend
curl http://localhost:4200

# Base de données
podman exec ecomptaia-postgres pg_isready -U ecomptaia_user

# Redis
podman exec ecomptaia-redis redis-cli ping
```

### 3. URLs d'Accès

- **Frontend** : http://localhost:4200
- **Backend API** : http://localhost:8080/api
- **Health Check** : http://localhost:8080/api/actuator/health
- **Metrics** : http://localhost:8080/api/actuator/prometheus
- **PostgreSQL** : localhost:5432
- **Redis** : localhost:6379

## 🔧 Gestion des Services

### Commandes Utiles

```bash
# Redémarrer un service
podman-compose -f docker-compose-podman.yml restart backend

# Redémarrer tous les services
podman-compose -f docker-compose-podman.yml restart

# Arrêter les services
podman-compose -f docker-compose-podman.yml down

# Arrêter et supprimer les volumes
podman-compose -f docker-compose-podman.yml down --volumes

# Nettoyer complètement
podman-compose -f docker-compose-podman.yml down --volumes --rmi all
```

### Mise à Jour

```bash
# 1. Arrêter les services
podman-compose -f docker-compose-podman.yml down

# 2. Construire les nouvelles images
podman-compose -f docker-compose-podman.yml build --no-cache

# 3. Redémarrer les services
podman-compose -f docker-compose-podman.yml up -d
```

## 📊 Monitoring

### Prometheus

- **URL** : http://localhost:9090
- **Métriques** : Collecte automatique des métriques système et applicatives

### Grafana

- **URL** : http://localhost:3000
- **Login** : admin / admin123 (changer en production)
- **Dashboards** : Pré-configurés pour E-COMPTA-IA

### Logs

```bash
# Logs de tous les services
podman-compose -f docker-compose-podman.yml logs -f

# Logs d'un service spécifique
podman-compose -f docker-compose-podman.yml logs -f backend
podman-compose -f docker-compose-podman.yml logs -f frontend
podman-compose -f docker-compose-podman.yml logs -f postgres
```

## 🔍 Dépannage

### Problèmes Courants

#### 1. Podman Desktop ne démarre pas

**Symptômes** : Erreur "Podman Desktop n'est pas démarré"

**Solutions** :
```bash
# Windows
# Redémarrer Podman Desktop depuis le menu Démarrer

# Linux
sudo systemctl start podman.socket
sudo systemctl enable podman.socket

# macOS
podman machine start
```

#### 2. Ports déjà utilisés

**Symptômes** : Erreur "Port already in use"

**Solutions** :
```bash
# Vérifier les ports utilisés
netstat -tulpn | grep :8080
netstat -tulpn | grep :4200

# Arrêter les services qui utilisent les ports
# Ou modifier les ports dans env.podman
```

#### 3. Problèmes de permissions

**Symptômes** : Erreurs de permissions sur les volumes

**Solutions** :
```bash
# Linux
sudo chown -R $USER:$USER data/
sudo chmod -R 755 data/

# Windows
# Exécuter PowerShell en tant qu'administrateur
```

#### 4. Services ne démarrent pas

**Symptômes** : Conteneurs en état "Exited"

**Solutions** :
```bash
# Vérifier les logs
podman-compose -f docker-compose-podman.yml logs backend

# Vérifier les ressources système
podman system df
podman system prune

# Redémarrer avec plus de ressources
```

### Commandes de Diagnostic

```bash
# Informations système Podman
podman info

# Utilisation des ressources
podman system df
podman stats

# Images et conteneurs
podman images
podman ps -a

# Réseaux
podman network ls
podman network inspect ecomptaia-network
```

## 🔒 Sécurité

### Recommandations de Production

1. **Changer tous les mots de passe par défaut**
   ```bash
   # Éditer env.podman
   POSTGRES_PASSWORD=your_secure_password_here
   REDIS_PASSWORD=your_redis_password_here
   JWT_SECRET=your_256_bit_secret_key_here
   ```

2. **Activer HTTPS**
   ```bash
   # Générer les certificats SSL
   openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
     -keyout nginx/ssl/key.pem \
     -out nginx/ssl/cert.pem
   
   # Activer HTTPS dans env.podman
   ENABLE_HTTPS=true
   ```

3. **Configurer le pare-feu**
   ```bash
   # Linux (UFW)
   sudo ufw allow 80/tcp
   sudo ufw allow 443/tcp
   sudo ufw deny 5432/tcp  # PostgreSQL
   sudo ufw deny 6379/tcp  # Redis
   ```

4. **Sauvegardes régulières**
   ```bash
   # Script de sauvegarde
   podman exec ecomptaia-postgres pg_dump -U ecomptaia_user ecomptaia_db > backup/db_$(date +%Y%m%d_%H%M%S).sql
   ```

### Variables d'Environnement Sensibles

Ne jamais commiter les fichiers contenant :
- Mots de passe de base de données
- Clés JWT
- Certificats SSL
- Clés API

## 📞 Support

### Ressources Utiles

- **Documentation Podman** : https://docs.podman.io/
- **Podman Desktop** : https://podman-desktop.io/
- **Docker Compose** : https://docs.docker.com/compose/

### Logs et Debugging

```bash
# Logs détaillés
podman-compose -f docker-compose-podman.yml logs --tail=100 -f

# Debug d'un conteneur
podman exec -it ecomptaia-backend /bin/bash
podman exec -it ecomptaia-postgres psql -U ecomptaia_user -d ecomptaia_db
```

---

## 🎉 Félicitations !

Votre application E-COMPTA-IA INTERNATIONAL est maintenant déployée avec Podman Desktop !

Pour toute question ou problème, consultez les logs et la section de dépannage ci-dessus.

**Bonne utilisation !** 🚀
