# Déploiement E-COMPTA-IA INTERNATIONAL

## Prérequis

- **Podman Desktop** installé et démarré
- **Java 17** installé (pour la compilation locale)
- **Maven** installé (pour la compilation locale)
- **Node.js 20** installé (pour la compilation locale du frontend)

## Fichiers de déploiement

### 1. Script PowerShell (Windows)
```powershell
# Exécuter le script PowerShell
.\deploy-podman.ps1
```

### 2. Script Bash (Linux/macOS)
```bash
# Rendre le script exécutable
chmod +x deploy-podman.sh

# Exécuter le script
./deploy-podman.sh
```

### 3. Fichier de composition
- `docker/podman-compose.yml` - Configuration pour Podman
- `docker/docker-compose.yml` - Configuration pour Docker (référence)

## Architecture des services

### Backend (Port 8080)
- **Image :** OpenJDK 17 + Maven
- **Port :** 8080
- **Base de données :** PostgreSQL
- **JWT :** Sécurisé avec clé secrète

### Frontend (Port 4200)
- **Image :** Node.js 20 + Nginx
- **Port :** 4200
- **Framework :** Angular 17
- **Serveur :** Nginx optimisé

### Base de données (Port 5432)
- **Image :** PostgreSQL 15 Alpine
- **Port :** 5432
- **Base :** ecomptaia_db
- **Utilisateur :** ecomptaia_user
- **Mot de passe :** ecomptaia_password

## Variables d'environnement

### Backend
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ecomptaia_db
SPRING_DATASOURCE_USERNAME=ecomptaia_user
SPRING_DATASOURCE_PASSWORD=ecomptaia_password
JWT_SECRET_KEY=ecomptaiaSecretKey2024ForProductionEnvironment
```

## Commandes utiles

### Voir les logs
```bash
# Tous les services
podman-compose -f docker/podman-compose.yml logs -f

# Service spécifique
podman-compose -f docker/podman-compose.yml logs -f backend
podman-compose -f docker/podman-compose.yml logs -f frontend
podman-compose -f docker/podman-compose.yml logs -f postgres
```

### Arrêter les services
```bash
podman-compose -f docker/podman-compose.yml down
```

### Redémarrer un service
```bash
podman-compose -f docker/podman-compose.yml restart backend
```

### Voir le statut
```bash
podman-compose -f docker/podman-compose.yml ps
```

## URLs d'accès

- **Frontend :** http://localhost:4200
- **Backend API :** http://localhost:8080
- **Base de données :** localhost:5432

## Dépannage

### Problème de port déjà utilisé
```bash
# Vérifier les ports utilisés
netstat -tulpn | grep :8080
netstat -tulpn | grep :4200
netstat -tulpn | grep :5432

# Tuer le processus
kill -9 <PID>
```

### Problème de permissions
```bash
# Vérifier les permissions Podman
podman info

# Redémarrer Podman Desktop
```

### Problème de compilation
```bash
# Nettoyer et recompiler
cd backend
mvn clean compile

cd ../frontend
npm install
npm run build
```

## Sécurité

- **JWT :** Clé secrète de production
- **Base de données :** Utilisateur dédié avec permissions limitées
- **Ports :** Seuls les ports nécessaires sont exposés
- **Réseau :** Isolation avec réseau bridge dédié

## Performance

- **Backend :** JVM optimisé pour la production
- **Frontend :** Build de production optimisé
- **Base de données :** Configuration PostgreSQL optimisée
- **Nginx :** Configuration de cache et compression
