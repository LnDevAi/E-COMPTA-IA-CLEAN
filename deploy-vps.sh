#!/usr/bin/env bash
set -euo pipefail

# ==========================
# E-COMPTA-IA - DEPLOY VPS
# ==========================
# Usage (par défaut):
#   sudo ./deploy-vps.sh
# Variables optionnelles:
#   APP_DIR=/opt/ecomptaia BRANCH=main REPO_URL=https://github.com/LnDevAi/E-COMPTA-IA-CLEAN.git ./deploy-vps.sh

export DEBIAN_FRONTEND=${DEBIAN_FRONTEND:-noninteractive}

APP_DIR=${APP_DIR:-/opt/ecomptaia}
REPO_URL=${REPO_URL:-https://github.com/LnDevAi/E-COMPTA-IA-CLEAN.git}
BRANCH=${BRANCH:-main}
COMPOSE="docker compose"

log() { echo -e "[$(date +'%F %T')] $*"; }

ensure_root() {
  if [ "$(id -u)" -ne 0 ]; then
    log "Veuillez exécuter ce script avec sudo ou en root."; exit 1;
  fi
}

ensure_docker() {
  if ! command -v docker >/dev/null 2>&1; then
    log "Docker introuvable. Installation..."
    apt-get update -y
    apt-get install -y docker.io docker-compose-plugin
    systemctl enable --now docker
  fi
  if ! docker info >/dev/null 2>&1; then
    log "Docker service non disponible. Démarrage..."
    systemctl start docker
  fi
}

stop_old_stack() {
  if [ -d "$APP_DIR" ]; then
    log "Arrêt de l'ancienne stack et suppression de: $APP_DIR"
    if [ -f "$APP_DIR/docker-compose.yml" ]; then
      (cd "$APP_DIR" && $COMPOSE down || true)
    fi
    rm -rf "$APP_DIR"
  fi
}

clone_repo() {
  log "Clonage du dépôt ($BRANCH) vers $APP_DIR"
  git clone --depth=1 -b "$BRANCH" "$REPO_URL" "$APP_DIR"
}

bring_up() {
  log "Construction et démarrage des services Docker"
  cd "$APP_DIR"
  $COMPOSE up -d --build
}

health_checks() {
  log "Vérification des conteneurs"
  $COMPOSE ps
  # Backend exposé par défaut sur 8082, frontend sur 4200 (docker-compose.yml)
  sleep 5
  if command -v curl >/dev/null 2>&1; then
    log "Health check frontend (port 4200)"
    curl -fsS http://localhost:4200 >/dev/null || log "Avertissement: Frontend non accessible sur 4200"
    log "Health check backend (port 8082)"
    curl -fsS http://localhost:8082/api/reporting/test >/dev/null || log "Avertissement: Backend test endpoint non accessible sur 8082"
  fi
}

post_info() {
  cat <<EOF

Déploiement terminé.

Accès:
  - Frontend: http://<IP_VPS>:4200/#/accounting/journal-entries
  - Reporting: http://<IP_VPS>:4200/#/accounting/reporting
  - Backend (si nécessaire): http://<IP_VPS>:8082

Commandes utiles:
  cd $APP_DIR
  $COMPOSE logs -f
  $COMPOSE ps
  $COMPOSE down && $COMPOSE up -d --build

Conseil production:
  - Pour exposer le front sur le port 80: changez le mapping du service frontend dans docker-compose.yml ("80:80") puis relancez.

EOF
}

main() {
  ensure_root
  ensure_docker
  stop_old_stack
  clone_repo
  bring_up
  health_checks
  post_info
}

main "$@"

