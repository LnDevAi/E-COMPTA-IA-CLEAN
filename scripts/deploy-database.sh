#!/bin/bash

# =====================================================
# E-COMPTA-IA - SCRIPT DE DÉPLOIEMENT BASE DE DONNÉES
# =====================================================

set -e

# Couleurs pour les messages
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# Fonction pour vérifier si Docker est installé
check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker n'est pas installé. Veuillez installer Docker d'abord."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose n'est pas installé. Veuillez installer Docker Compose d'abord."
        exit 1
    fi
    
    log_success "Docker et Docker Compose sont installés"
}

# Fonction pour vérifier si Podman est installé
check_podman() {
    if command -v podman &> /dev/null; then
        log_info "Podman détecté, utilisation de Podman au lieu de Docker"
        return 0
    fi
    return 1
}

# Fonction pour démarrer les services de base de données
start_database_services() {
    log_info "Démarrage des services de base de données..."
    
    if check_podman; then
        log_info "Utilisation de Podman Compose"
        podman-compose -f docker-compose.database.yml up -d postgres redis
    else
        log_info "Utilisation de Docker Compose"
        docker-compose -f docker-compose.database.yml up -d postgres redis
    fi
    
    log_success "Services de base de données démarrés"
}

# Fonction pour attendre que PostgreSQL soit prêt
wait_for_postgres() {
    log_info "Attente que PostgreSQL soit prêt..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if check_podman; then
            if podman exec ecomptaia-postgres pg_isready -U ecomptaia_user -d ecomptaia_db &> /dev/null; then
                log_success "PostgreSQL est prêt"
                return 0
            fi
        else
            if docker exec ecomptaia-postgres pg_isready -U ecomptaia_user -d ecomptaia_db &> /dev/null; then
                log_success "PostgreSQL est prêt"
                return 0
            fi
        fi
        
        log_info "Tentative $attempt/$max_attempts - Attente de PostgreSQL..."
        sleep 2
        ((attempt++))
    done
    
    log_error "PostgreSQL n'est pas prêt après $max_attempts tentatives"
    exit 1
}

# Fonction pour attendre que Redis soit prêt
wait_for_redis() {
    log_info "Attente que Redis soit prêt..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if check_podman; then
            if podman exec ecomptaia-redis redis-cli ping &> /dev/null; then
                log_success "Redis est prêt"
                return 0
            fi
        else
            if docker exec ecomptaia-redis redis-cli ping &> /dev/null; then
                log_success "Redis est prêt"
                return 0
            fi
        fi
        
        log_info "Tentative $attempt/$max_attempts - Attente de Redis..."
        sleep 2
        ((attempt++))
    done
    
    log_error "Redis n'est pas prêt après $max_attempts tentatives"
    exit 1
}

# Fonction pour exécuter les migrations
run_migrations() {
    log_info "Exécution des migrations de base de données..."
    
    # Vérifier si le backend est compilé
    if [ ! -f "backend/target/ecomptaia-backend.jar" ]; then
        log_warning "Le backend n'est pas compilé. Compilation en cours..."
        cd backend
        ./mvnw clean package -DskipTests
        cd ..
    fi
    
    # Exécuter les migrations
    cd backend
    ./mvnw flyway:migrate
    cd ..
    
    log_success "Migrations exécutées avec succès"
}

# Fonction pour démarrer les services de monitoring
start_monitoring_services() {
    log_info "Démarrage des services de monitoring..."
    
    if check_podman; then
        podman-compose -f docker-compose.database.yml --profile monitoring up -d
    else
        docker-compose -f docker-compose.database.yml --profile monitoring up -d
    fi
    
    log_success "Services de monitoring démarrés"
    log_info "Prometheus: http://localhost:9090"
    log_info "Grafana: http://localhost:3000 (admin/admin123)"
}

# Fonction pour démarrer les services d'administration
start_admin_services() {
    log_info "Démarrage des services d'administration..."
    
    if check_podman; then
        podman-compose -f docker-compose.database.yml --profile admin up -d
    else
        docker-compose -f docker-compose.database.yml --profile admin up -d
    fi
    
    log_success "Services d'administration démarrés"
    log_info "pgAdmin: http://localhost:8082 (admin@ecomptaia.com/admin123)"
}

# Fonction pour afficher l'aide
show_help() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help              Afficher cette aide"
    echo "  -d, --database-only     Démarrer seulement les services de base de données"
    echo "  -m, --with-monitoring   Démarrer avec les services de monitoring"
    echo "  -a, --with-admin        Démarrer avec les services d'administration"
    echo "  -f, --full              Démarrer tous les services (base de données + monitoring + admin)"
    echo "  -s, --stop              Arrêter tous les services"
    echo "  -r, --restart           Redémarrer tous les services"
    echo "  -l, --logs              Afficher les logs des services"
    echo "  -c, --clean             Nettoyer les volumes et redémarrer"
    echo ""
    echo "Exemples:"
    echo "  $0 -d                   # Démarrer seulement PostgreSQL et Redis"
    echo "  $0 -f                   # Démarrer tous les services"
    echo "  $0 -s                   # Arrêter tous les services"
    echo "  $0 -c                   # Nettoyer et redémarrer"
}

# Fonction pour arrêter les services
stop_services() {
    log_info "Arrêt des services..."
    
    if check_podman; then
        podman-compose -f docker-compose.database.yml down
    else
        docker-compose -f docker-compose.database.yml down
    fi
    
    log_success "Services arrêtés"
}

# Fonction pour redémarrer les services
restart_services() {
    log_info "Redémarrage des services..."
    stop_services
    sleep 5
    start_database_services
    wait_for_postgres
    wait_for_redis
    log_success "Services redémarrés"
}

# Fonction pour afficher les logs
show_logs() {
    log_info "Affichage des logs des services..."
    
    if check_podman; then
        podman-compose -f docker-compose.database.yml logs -f
    else
        docker-compose -f docker-compose.database.yml logs -f
    fi
}

# Fonction pour nettoyer les volumes
clean_volumes() {
    log_warning "Nettoyage des volumes (cela supprimera toutes les données)..."
    read -p "Êtes-vous sûr de vouloir continuer? (y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        log_info "Arrêt des services..."
        stop_services
        
        log_info "Suppression des volumes..."
        if check_podman; then
            podman volume prune -f
        else
            docker volume prune -f
        fi
        
        log_info "Redémarrage des services..."
        start_database_services
        wait_for_postgres
        wait_for_redis
        run_migrations
        
        log_success "Nettoyage terminé et services redémarrés"
    else
        log_info "Nettoyage annulé"
    fi
}

# Fonction principale
main() {
    log_info "=== E-COMPTA-IA - Déploiement Base de Données ==="
    
    # Vérifier les prérequis
    check_docker
    
    # Traiter les arguments
    case "${1:-}" in
        -h|--help)
            show_help
            ;;
        -d|--database-only)
            start_database_services
            wait_for_postgres
            wait_for_redis
            run_migrations
            ;;
        -m|--with-monitoring)
            start_database_services
            wait_for_postgres
            wait_for_redis
            run_migrations
            start_monitoring_services
            ;;
        -a|--with-admin)
            start_database_services
            wait_for_postgres
            wait_for_redis
            run_migrations
            start_admin_services
            ;;
        -f|--full)
            start_database_services
            wait_for_postgres
            wait_for_redis
            run_migrations
            start_monitoring_services
            start_admin_services
            ;;
        -s|--stop)
            stop_services
            ;;
        -r|--restart)
            restart_services
            ;;
        -l|--logs)
            show_logs
            ;;
        -c|--clean)
            clean_volumes
            ;;
        *)
            log_error "Option invalide: ${1:-}"
            show_help
            exit 1
            ;;
    esac
}

# Exécuter la fonction principale
main "$@"








