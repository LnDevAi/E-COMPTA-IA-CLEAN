#!/bin/bash

# ========================================
# E-COMPTA-IA - DÉPLOIEMENT PODMAN DESKTOP
# ========================================
# Auteur: Assistant IA
# Date: $(date)
# Version: 1.0.0
# ========================================

set -e  # Arrêt en cas d'erreur

echo "🚀 DÉPLOIEMENT E-COMPTA-IA SUR PODMAN DESKTOP"
echo "=============================================="
echo ""

# Variables d'environnement
PROJECT_NAME="ecomptaia"
BACKEND_IMAGE="${PROJECT_NAME}-backend:latest"
FRONTEND_IMAGE="${PROJECT_NAME}-frontend:latest"
NETWORK_NAME="${PROJECT_NAME}-network"
DB_CONTAINER="${PROJECT_NAME}-postgres"
BACKEND_CONTAINER="${PROJECT_NAME}-backend"
FRONTEND_CONTAINER="${PROJECT_NAME}-frontend"

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Fonction de log coloré
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

# Vérification de Podman
check_podman() {
    log_info "Vérification de Podman..."
    if ! command -v podman &> /dev/null; then
        log_error "Podman n'est pas installé ou n'est pas dans le PATH"
        exit 1
    fi
    
    if ! podman info &> /dev/null; then
        log_error "Podman Desktop n'est pas démarré"
        log_info "Veuillez démarrer Podman Desktop et réessayer"
        exit 1
    fi
    
    log_success "Podman est opérationnel"
    podman version
    echo ""
}
