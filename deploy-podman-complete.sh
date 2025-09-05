#!/bin/bash

# Script de déploiement complet E-COMPTA-IA avec Podman Desktop
# Script Bash pour Linux/macOS - Version Production
# =====================================================

set -euo pipefail

# Configuration
ENVIRONMENT="${1:-production}"
SKIP_BUILD="${SKIP_BUILD:-false}"
SKIP_TESTS="${SKIP_TESTS:-false}"
FORCE="${FORCE:-false}"

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m' # No Color

# Fonctions d'affichage
print_header() {
    echo -e "\n${CYAN}===============================================${NC}"
    echo -e "  ${WHITE}$1${NC}"
    echo -e "${CYAN}===============================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

# Fonction d'aide
show_help() {
    echo -e "${CYAN}Usage: $0 [ENVIRONMENT] [OPTIONS]${NC}"
    echo ""
    echo -e "${YELLOW}Arguments:${NC}"
    echo "  ENVIRONMENT    Environnement (production|staging|development) [default: production]"
    echo ""
    echo -e "${YELLOW}Variables d'environnement:${NC}"
    echo "  SKIP_BUILD=true    Ignorer la construction des images"
    echo "  SKIP_TESTS=true    Ignorer les tests de santé"
    echo "  FORCE=true         Forcer le redémarrage même si les services sont en cours"
    echo ""
    echo -e "${GREEN}Exemples:${NC}"
    echo "  $0"
    echo "  $0 staging"
    echo "  SKIP_BUILD=true $0"
    echo "  FORCE=true $0 production"
    exit 0
}

# Vérifier les arguments
if [[ "${1:-}" == "--help" ]] || [[ "${1:-}" == "-h" ]]; then
    show_help
fi

# Début du script
print_header "DÉPLOIEMENT E-COMPTA-IA INTERNATIONAL"
print_info "Environnement: $ENVIRONMENT"
print_info "Date: $(date '+%Y-%m-%d %H:%M:%S')"

# 1. Vérifications préliminaires
print_header "VÉRIFICATIONS PRÉLIMINAIRES"

# Vérifier Podman
if command -v podman &> /dev/null; then
    PODMAN_VERSION=$(podman --version)
    print_success "Podman détecté: $PODMAN_VERSION"
else
    print_error "Podman n'est pas installé ou pas dans le PATH"
    print_info "Veuillez installer Podman depuis: https://podman.io/getting-started/installation"
    exit 1
fi

# Vérifier Podman Desktop (optionnel)
if podman info &> /dev/null; then
    print_success "Podman est opérationnel"
else
    print_error "Podman n'est pas opérationnel"
    print_info "Veuillez démarrer le service Podman et réessayer"
    exit 1
fi

# Vérifier les fichiers de configuration
REQUIRED_FILES=(
    "docker-compose-podman.yml"
    "env.podman"
    "backend/Dockerfile"
    "frontend/Dockerfile"
)

for file in "${REQUIRED_FILES[@]}"; do
    if [[ -f "$file" ]]; then
        print_success "Fichier trouvé: $file"
    else
        print_error "Fichier manquant: $file"
        exit 1
    fi
done

# 2. Préparation de l'environnement
print_header "PRÉPARATION DE L'ENVIRONNEMENT"

# Charger les variables d'environnement
if [[ -f "env.podman" ]]; then
    set -a
    source env.podman
    set +a
    print_success "Variables d'environnement chargées"
fi

# Créer les répertoires de données
DATA_DIRS=("data/postgres" "data/redis" "data/logs" "backup")
for dir in "${DATA_DIRS[@]}"; do
    if [[ ! -d "$dir" ]]; then
        mkdir -p "$dir"
        print_success "Répertoire créé: $dir"
    fi
done

# 3. Nettoyage des ressources existantes
print_header "NETTOYAGE DES RESSOURCES EXISTANTES"

if [[ "$FORCE" == "true" ]]; then
    print_info "Arrêt forcé des services existants..."
    podman-compose -f docker-compose-podman.yml down --remove-orphans --volumes 2>/dev/null || true
    print_success "Services arrêtés"
    
    print_info "Suppression des images existantes..."
    IMAGES=$(podman images -q ecomptaia-* 2>/dev/null || true)
    if [[ -n "$IMAGES" ]]; then
        echo "$IMAGES" | xargs podman rmi --force 2>/dev/null || true
        print_success "Images supprimées"
    fi
else
    print_info "Arrêt des services existants..."
    podman-compose -f docker-compose-podman.yml down --remove-orphans 2>/dev/null || true
    print_success "Services arrêtés"
fi

# 4. Construction des images
if [[ "$SKIP_BUILD" != "true" ]]; then
    print_header "CONSTRUCTION DES IMAGES"
    
    print_info "Construction des images Docker..."
    if podman-compose -f docker-compose-podman.yml build --no-cache; then
        print_success "Images construites avec succès"
    else
        print_error "Échec de la construction des images"
        exit 1
    fi
fi

# 5. Démarrage des services
print_header "DÉMARRAGE DES SERVICES"

print_info "Démarrage des services..."
if podman-compose -f docker-compose-podman.yml up -d; then
    print_success "Services démarrés"
else
    print_error "Échec du démarrage des services"
    exit 1
fi

# 6. Attente du démarrage
print_header "ATTENTE DU DÉMARRAGE"

print_info "Attente du démarrage des services (60 secondes)..."
sleep 60

# 7. Vérification de la santé des services
if [[ "$SKIP_TESTS" != "true" ]]; then
    print_header "VÉRIFICATION DE LA SANTÉ DES SERVICES"
    
    # Vérifier PostgreSQL
    if podman exec ecomptaia-postgres pg_isready -U ecomptaia_user -d ecomptaia_db &>/dev/null; then
        print_success "PostgreSQL: Service accessible"
    else
        print_error "PostgreSQL: Service non accessible"
    fi
    
    # Vérifier Redis
    if podman exec ecomptaia-redis redis-cli ping &>/dev/null; then
        print_success "Redis: Service accessible"
    else
        print_error "Redis: Service non accessible"
    fi
    
    # Vérifier Backend
    if curl -f -s "http://localhost:8080/api/actuator/health" &>/dev/null; then
        BACKEND_STATUS=$(curl -s "http://localhost:8080/api/actuator/health" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
        print_success "Backend: Service accessible (Status: $BACKEND_STATUS)"
    else
        print_error "Backend: Service non accessible"
    fi
    
    # Vérifier Frontend
    if curl -f -s "http://localhost:4200" &>/dev/null; then
        FRONTEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:4200")
        print_success "Frontend: Service accessible (HTTP $FRONTEND_STATUS)"
    else
        print_error "Frontend: Service non accessible"
    fi
fi

# 8. Affichage du statut final
print_header "STATUT FINAL DES SERVICES"

print_info "Statut des conteneurs:"
podman-compose -f docker-compose-podman.yml ps

print_header "URLS D'ACCÈS"
echo -e "${WHITE}🌐 Frontend:     http://localhost:4200${NC}"
echo -e "${WHITE}🔧 Backend API:  http://localhost:8080/api${NC}"
echo -e "${WHITE}📊 Health Check: http://localhost:8080/api/actuator/health${NC}"
echo -e "${WHITE}📈 Metrics:      http://localhost:8080/api/actuator/prometheus${NC}"
echo -e "${WHITE}🗄️  PostgreSQL:   localhost:5432${NC}"
echo -e "${WHITE}⚡ Redis:        localhost:6379${NC}"

print_header "COMMANDES UTILES"
echo -e "${YELLOW}📋 Voir les logs:     podman-compose -f docker-compose-podman.yml logs -f${NC}"
echo -e "${YELLOW}🔄 Redémarrer:        podman-compose -f docker-compose-podman.yml restart${NC}"
echo -e "${YELLOW}⏹️  Arrêter:           podman-compose -f docker-compose-podman.yml down${NC}"
echo -e "${YELLOW}🧹 Nettoyer:          podman-compose -f docker-compose-podman.yml down --volumes --rmi all${NC}"

print_header "DÉPLOIEMENT TERMINÉ"
print_success "E-COMPTA-IA INTERNATIONAL est déployé avec succès!"
print_info "Consultez les logs pour plus d'informations: podman-compose -f docker-compose-podman.yml logs -f"
