#!/bin/bash

# 🚀 SCRIPT DE DÉPLOIEMENT PRODUCTION - E-COMPTA-IA INTERNATIONAL
# Version: 1.0.0
# Date: $(date)

set -e

echo "🚀 DÉPLOIEMENT PRODUCTION E-COMPTA-IA INTERNATIONAL"
echo "=================================================="

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Variables d'environnement
ENV_FILE=".env.production"
COMPOSE_FILE="docker-compose.prod.yml"

# Fonction pour afficher les messages
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Vérification des prérequis
check_prerequisites() {
    log_info "Vérification des prérequis..."
    
    # Vérifier Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker n'est pas installé"
        exit 1
    fi
    
    # Vérifier Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose n'est pas installé"
        exit 1
    fi
    
    # Vérifier les permissions
    if ! docker info &> /dev/null; then
        log_error "Vous n'avez pas les permissions pour exécuter Docker"
        exit 1
    fi
    
    log_success "Prérequis vérifiés"
}

# Création du fichier d'environnement
create_env_file() {
    log_info "Création du fichier d'environnement..."
    
    if [ ! -f "$ENV_FILE" ]; then
        cat > "$ENV_FILE" << EOF
# Configuration Production E-COMPTA-IA INTERNATIONAL
# ================================================

# Base de données
DB_PASSWORD=Ecomptaia2024!

# JWT
JWT_SECRET=EcomptaiaJWTSecret2024!

# Redis
REDIS_PASSWORD=EcomptaiaRedis2024!

# Grafana
GRAFANA_PASSWORD=EcomptaiaGrafana2024!

# API Keys IA (à configurer)
OPENAI_API_KEY=your_openai_api_key_here
CLAUDE_API_KEY=your_claude_api_key_here

# Configuration SSL (à configurer)
SSL_CERT_PATH=/etc/nginx/ssl/ecomptaia.crt
SSL_KEY_PATH=/etc/nginx/ssl/ecomptaia.key

# Monitoring
PROMETHEUS_RETENTION_TIME=200h
ELASTICSEARCH_HEAP_SIZE=512m

# Backup
BACKUP_RETENTION_DAYS=30
BACKUP_SCHEDULE=0 2 * * *

# Performance
JVM_HEAP_SIZE=2g
NGINX_WORKER_PROCESSES=auto
EOF
        log_success "Fichier d'environnement créé: $ENV_FILE"
        log_warning "Veuillez configurer les API keys IA dans $ENV_FILE"
    else
        log_info "Fichier d'environnement existant: $ENV_FILE"
    fi
}

# Création des certificats SSL auto-signés (pour le développement)
create_ssl_certificates() {
    log_info "Création des certificats SSL..."
    
    mkdir -p nginx/ssl
    
    if [ ! -f "nginx/ssl/ecomptaia.crt" ] || [ ! -f "nginx/ssl/ecomptaia.key" ]; then
        openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
            -keyout nginx/ssl/ecomptaia.key \
            -out nginx/ssl/ecomptaia.crt \
            -subj "/C=SN/ST=Dakar/L=Dakar/O=E-COMPTA-IA/OU=IT/CN=ecomptaia.com"
        
        log_success "Certificats SSL créés"
        log_warning "Certificats auto-signés créés. Pour la production, utilisez des certificats Let's Encrypt"
    else
        log_info "Certificats SSL existants"
    fi
}

# Création des dossiers nécessaires
create_directories() {
    log_info "Création des dossiers nécessaires..."
    
    mkdir -p {backup,logs,monitoring/grafana/{dashboards,datasources},scripts}
    
    log_success "Dossiers créés"
}

# Configuration du monitoring
setup_monitoring() {
    log_info "Configuration du monitoring..."
    
    # Configuration Prometheus
    cat > monitoring/prometheus.yml << EOF
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  # - "first_rules.yml"
  # - "second_rules.yml"

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'ecomptaia-backend'
    static_configs:
      - targets: ['backend:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s

  - job_name: 'nginx'
    static_configs:
      - targets: ['nginx:80']
    metrics_path: '/nginx_status'

  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres:5432']
    scrape_interval: 60s
EOF

    # Configuration Grafana
    cat > monitoring/grafana/datasources/prometheus.yml << EOF
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
EOF

    log_success "Monitoring configuré"
}

# Script de backup
create_backup_script() {
    log_info "Création du script de backup..."
    
    cat > scripts/backup.sh << 'EOF'
#!/bin/bash

# Script de backup automatique
BACKUP_DIR="/backup"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="ecomptaia_prod"
DB_USER="ecomptaia_user"
DB_HOST="postgres"

# Créer le backup
pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME > $BACKUP_DIR/backup_$DATE.sql

# Compresser le backup
gzip $BACKUP_DIR/backup_$DATE.sql

# Supprimer les anciens backups (garder 30 jours)
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +30 -delete

echo "Backup créé: backup_$DATE.sql.gz"
EOF

    chmod +x scripts/backup.sh
    log_success "Script de backup créé"
}

# Build et déploiement
deploy_application() {
    log_info "Déploiement de l'application..."
    
    # Arrêter les conteneurs existants
    log_info "Arrêt des conteneurs existants..."
    docker-compose -f $COMPOSE_FILE down --remove-orphans
    
    # Nettoyer les images non utilisées
    log_info "Nettoyage des images..."
    docker system prune -f
    
    # Build des images
    log_info "Build des images Docker..."
    docker-compose -f $COMPOSE_FILE build --no-cache
    
    # Démarrer les services
    log_info "Démarrage des services..."
    docker-compose -f $COMPOSE_FILE up -d
    
    log_success "Application déployée"
}

# Vérification du déploiement
verify_deployment() {
    log_info "Vérification du déploiement..."
    
    # Attendre que les services démarrent
    sleep 30
    
    # Vérifier les conteneurs
    log_info "Statut des conteneurs:"
    docker-compose -f $COMPOSE_FILE ps
    
    # Vérifier la santé des services
    log_info "Vérification de la santé des services..."
    
    # Backend
    if curl -f http://localhost:8080/api/system/health &> /dev/null; then
        log_success "Backend opérationnel"
    else
        log_error "Backend non accessible"
    fi
    
    # Frontend
    if curl -f http://localhost:80 &> /dev/null; then
        log_success "Frontend opérationnel"
    else
        log_error "Frontend non accessible"
    fi
    
    # Base de données
    if docker-compose -f $COMPOSE_FILE exec -T postgres pg_isready -U ecomptaia_user &> /dev/null; then
        log_success "Base de données opérationnelle"
    else
        log_error "Base de données non accessible"
    fi
}

# Affichage des informations de connexion
show_connection_info() {
    log_success "🎉 DÉPLOIEMENT TERMINÉ AVEC SUCCÈS !"
    echo ""
    echo "📋 INFORMATIONS DE CONNEXION:"
    echo "============================="
    echo "🌐 Application principale: https://ecomptaia.com"
    echo "📊 Dashboard admin: https://admin.ecomptaia.com"
    echo "📈 Monitoring: https://monitoring.ecomptaia.com"
    echo ""
    echo "🔧 PORTS UTILISÉS:"
    echo "=================="
    echo "Frontend: 80 (HTTP) / 443 (HTTPS)"
    echo "Backend API: 8080"
    echo "Grafana: 3000"
    echo "Prometheus: 9090"
    echo "Kibana: 5601"
    echo "Elasticsearch: 9200"
    echo ""
    echo "📝 COMMANDES UTILES:"
    echo "==================="
    echo "Voir les logs: docker-compose -f $COMPOSE_FILE logs -f"
    echo "Redémarrer: docker-compose -f $COMPOSE_FILE restart"
    echo "Arrêter: docker-compose -f $COMPOSE_FILE down"
    echo "Mise à jour: ./deploy-production.sh"
    echo ""
    echo "🔐 CONFIGURATION REQUISE:"
    echo "========================="
    echo "1. Configurez les API keys IA dans $ENV_FILE"
    echo "2. Remplacez les certificats SSL par des certificats valides"
    echo "3. Configurez votre domaine DNS"
    echo ""
}

# Fonction principale
main() {
    echo "🚀 DÉMARRAGE DU DÉPLOIEMENT PRODUCTION"
    echo "======================================"
    
    check_prerequisites
    create_env_file
    create_ssl_certificates
    create_directories
    setup_monitoring
    create_backup_script
    deploy_application
    verify_deployment
    show_connection_info
    
    log_success "🎉 E-COMPTA-IA INTERNATIONAL EST MAINTENANT OPÉRATIONNEL !"
    echo ""
    echo "🌍 RÉVOLUTION COMPTABLE MONDIALE LANCÉE !"
    echo "De l'Afrique au Monde - E COMPTA IA INTERNATIONAL"
}

# Exécution du script
main "$@"







