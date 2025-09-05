#!/bin/bash

# Script de déploiement automatique du monitoring E-COMPTA-IA
# Compatible Podman Desktop

set -e

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="ecomptaia-monitoring"
NETWORK_NAME="ecomptaia-monitoring"
COMPOSE_FILE="podman-compose.yml"

echo -e "${BLUE}🚀 DÉPLOIEMENT MONITORING E-COMPTA-IA${NC}"
echo "=================================="

# Vérification des prérequis
echo -e "${YELLOW}📋 Vérification des prérequis...${NC}"

if ! command -v podman &> /dev/null; then
    echo -e "${RED}❌ Podman n'est pas installé${NC}"
    exit 1
fi

if ! command -v podman-compose &> /dev/null; then
    echo -e "${RED}❌ podman-compose n'est pas installé${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Podman et podman-compose disponibles${NC}"

# Création des volumes Podman
echo -e "${YELLOW}📦 Création des volumes...${NC}"

podman volume create prometheus_data 2>/dev/null || echo "Volume prometheus_data existe déjà"
podman volume create grafana_data 2>/dev/null || echo "Volume grafana_data existe déjà"
podman volume create alertmanager_data 2>/dev/null || echo "Volume alertmanager_data existe déjà"

echo -e "${GREEN}✅ Volumes créés${NC}"

# Création du réseau
echo -e "${YELLOW}🌐 Création du réseau...${NC}"

if ! podman network exists $NETWORK_NAME; then
    podman network create $NETWORK_NAME
    echo -e "${GREEN}✅ Réseau $NETWORK_NAME créé${NC}"
else
    echo -e "${GREEN}✅ Réseau $NETWORK_NAME existe déjà${NC}"
fi

# Vérification des ports disponibles
echo -e "${YELLOW}🔍 Vérification des ports...${NC}"

PORTS=(9090 3000 9100 9187 9121 9093 8080)
for port in "${PORTS[@]}"; do
    if netstat -tuln | grep ":$port " > /dev/null; then
        echo -e "${YELLOW}⚠️  Port $port est déjà utilisé${NC}"
    else
        echo -e "${GREEN}✅ Port $port disponible${NC}"
    fi
done

# Déploiement des services
echo -e "${YELLOW}🚀 Déploiement des services...${NC}"

podman-compose -f $COMPOSE_FILE down 2>/dev/null || true
podman-compose -f $COMPOSE_FILE up -d

echo -e "${GREEN}✅ Services déployés${NC}"

# Attente du démarrage
echo -e "${YELLOW}⏳ Attente du démarrage des services...${NC}"
sleep 30

# Vérification de la santé des services
echo -e "${YELLOW}🏥 Vérification de la santé des services...${NC}"

SERVICES=(
    "prometheus:9090"
    "grafana:3000"
    "node-exporter:9100"
    "postgres-exporter:9187"
    "redis-exporter:9121"
    "alertmanager:9093"
    "cadvisor:8080"
)

for service in "${SERVICES[@]}"; do
    IFS=':' read -r name port <<< "$service"
    
    if curl -s "http://localhost:$port" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ $name est opérationnel (port $port)${NC}"
    else
        echo -e "${RED}❌ $name n'est pas accessible (port $port)${NC}"
    fi
done

# Configuration initiale Grafana
echo -e "${YELLOW}🎨 Configuration initiale Grafana...${NC}"

# Attendre que Grafana soit prêt
echo "Attente que Grafana soit prêt..."
for i in {1..60}; do
    if curl -s "http://localhost:3000/api/health" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ Grafana est prêt${NC}"
        break
    fi
    echo "Tentative $i/60..."
    sleep 5
done

# Affichage des URLs d'accès
echo -e "${BLUE}🌐 URLs d'accès au monitoring:${NC}"
echo "=================================="
echo -e "${GREEN}📊 Prometheus:${NC} http://localhost:9090"
echo -e "${GREEN}🎨 Grafana:${NC} http://localhost:3000 (admin/admin123)"
echo -e "${GREEN}📈 Node Exporter:${NC} http://localhost:9100"
echo -e "${GREEN}🗄️  Postgres Exporter:${NC} http://localhost:9187"
echo -e "${GREEN}🔴 Redis Exporter:${NC} http://localhost:9121"
echo -e "${GREEN}🚨 Alertmanager:${NC} http://localhost:9093"
echo -e "${GREEN}📊 cAdvisor:${NC} http://localhost:8080"

echo ""
echo -e "${BLUE}📋 Commandes utiles:${NC}"
echo "=================================="
echo -e "${YELLOW}Voir les logs:${NC} podman-compose -f $COMPOSE_FILE logs -f"
echo -e "${YELLOW}Arrêter:${NC} podman-compose -f $COMPOSE_FILE down"
echo -e "${YELLOW}Redémarrer:${NC} podman-compose -f $COMPOSE_FILE restart"
echo -e "${YELLOW}Status:${NC} podman-compose -f $COMPOSE_FILE ps"

echo ""
echo -e "${GREEN}🎉 Déploiement du monitoring terminé avec succès !${NC}"
echo -e "${YELLOW}⚠️  N'oubliez pas de configurer les variables d'environnement dans .env.monitoring${NC}"

