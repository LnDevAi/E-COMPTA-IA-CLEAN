#!/bin/bash

# Script de vérification de santé du monitoring E-COMPTA-IA
# Vérifie le statut de tous les services et métriques

set -e

# Couleurs pour l'affichage
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
COMPOSE_FILE="podman-compose.yml"

echo -e "${BLUE}🏥 VÉRIFICATION DE SANTÉ MONITORING E-COMPTA-IA${NC}"
echo "================================================"

# Vérification du statut des containers
echo -e "${YELLOW}📦 Statut des containers...${NC}"
podman-compose -f $COMPOSE_FILE ps

echo ""

# Vérification des métriques Prometheus
echo -e "${YELLOW}📊 Vérification des métriques Prometheus...${NC}"

if curl -s "http://localhost:9090/api/v1/query?query=up" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Prometheus répond aux requêtes${NC}"
    
    # Vérification des targets
    TARGETS=$(curl -s "http://localhost:9090/api/v1/targets" | jq -r '.data.activeTargets[] | "\(.labels.job): \(.health)"' 2>/dev/null || echo "jq non disponible")
    if [ ! -z "$TARGETS" ]; then
        echo "Targets actifs:"
        echo "$TARGETS"
    fi
else
    echo -e "${RED}❌ Prometheus ne répond pas${NC}"
fi

echo ""

# Vérification de Grafana
echo -e "${YELLOW}🎨 Vérification de Grafana...${NC}"

if curl -s "http://localhost:3000/api/health" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Grafana est opérationnel${NC}"
    
    # Vérification des datasources
    if command -v jq &> /dev/null; then
        DATASOURCES=$(curl -s "http://localhost:3000/api/datasources" 2>/dev/null | jq -r '.[].name' 2>/dev/null || echo "Non accessible")
        echo "Datasources configurés: $DATASOURCES"
    fi
else
    echo -e "${RED}❌ Grafana n'est pas accessible${NC}"
fi

echo ""

# Vérification des exportateurs
echo -e "${YELLOW}📈 Vérification des exportateurs...${NC}"

# Node Exporter
if curl -s "http://localhost:9100/metrics" | grep -q "node_cpu_seconds_total"; then
    echo -e "${GREEN}✅ Node Exporter fonctionne${NC}"
else
    echo -e "${RED}❌ Node Exporter ne fonctionne pas${NC}"
fi

# Postgres Exporter
if curl -s "http://localhost:9187/metrics" | grep -q "pg_up"; then
    echo -e "${GREEN}✅ Postgres Exporter fonctionne${NC}"
else
    echo -e "${RED}❌ Postgres Exporter ne fonctionne pas${NC}"
fi

# Redis Exporter
if curl -s "http://localhost:9121/metrics" | grep -q "redis_up"; then
    echo -e "${GREEN}✅ Redis Exporter fonctionne${NC}"
else
    echo -e "${RED}❌ Redis Exporter ne fonctionne pas${NC}"
fi

echo ""

# Vérification d'Alertmanager
echo -e "${YELLOW}🚨 Vérification d'Alertmanager...${NC}"

if curl -s "http://localhost:9093/api/v1/status" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Alertmanager est opérationnel${NC}"
    
    # Vérification des alertes actives
    ACTIVE_ALERTS=$(curl -s "http://localhost:9093/api/v1/alerts" | jq -r '.data[] | select(.status.state == "active") | .labels.alertname' 2>/dev/null || echo "jq non disponible")
    if [ ! -z "$ACTIVE_ALERTS" ]; then
        echo -e "${YELLOW}⚠️  Alertes actives:${NC}"
        echo "$ACTIVE_ALERTS"
    else
        echo -e "${GREEN}✅ Aucune alerte active${NC}"
    fi
else
    echo -e "${RED}❌ Alertmanager n'est pas accessible${NC}"
fi

echo ""

# Vérification de cAdvisor
echo -e "${YELLOW}📊 Vérification de cAdvisor...${NC}"

if curl -s "http://localhost:8080/metrics" | grep -q "container_cpu_usage_seconds_total"; then
    echo -e "${GREEN}✅ cAdvisor fonctionne${NC}"
else
    echo -e "${RED}❌ cAdvisor ne fonctionne pas${NC}"
fi

echo ""

# Vérification des métriques système
echo -e "${YELLOW}💻 Métriques système...${NC}"

if command -v curl &> /dev/null; then
    # CPU
    CPU_USAGE=$(curl -s "http://localhost:9100/metrics" | grep "node_cpu_seconds_total{mode=\"idle\"}" | head -1 | cut -d' ' -f2 | awk '{print 100-$1*100}' 2>/dev/null || echo "N/A")
    echo "CPU Usage: ${CPU_USAGE}%"
    
    # Mémoire
    MEM_TOTAL=$(curl -s "http://localhost:9100/metrics" | grep "node_memory_MemTotal_bytes" | head -1 | cut -d' ' -f2 2>/dev/null || echo "N/A")
    MEM_AVAILABLE=$(curl -s "http://localhost:9100/metrics" | grep "node_memory_MemAvailable_bytes" | head -1 | cut -d' ' -f2 2>/dev/null || echo "N/A")
    
    if [ "$MEM_TOTAL" != "N/A" ] && [ "$MEM_AVAILABLE" != "N/A" ]; then
        MEM_USAGE=$(echo "scale=2; ($MEM_TOTAL - $MEM_AVAILABLE) * 100 / $MEM_TOTAL" | bc 2>/dev/null || echo "N/A")
        echo "Mémoire Usage: ${MEM_USAGE}%"
    fi
    
    # Disque
    DISK_USAGE=$(curl -s "http://localhost:9100/metrics" | grep "node_filesystem_avail_bytes{mountpoint=\"/\"}" | head -1 | cut -d' ' -f2 2>/dev/null || echo "N/A")
    if [ "$DISK_USAGE" != "N/A" ]; then
        echo "Espace disque disponible: $(echo "scale=2; $DISK_USAGE / 1024 / 1024 / 1024" | bc 2>/dev/null || echo "N/A") GB"
    fi
fi

echo ""

# Vérification des logs
echo -e "${YELLOW}📝 Vérification des logs récents...${NC}"

echo "Logs Prometheus (dernières 5 lignes):"
podman logs ecomptaia-prometheus --tail 5 2>/dev/null || echo "Container non trouvé"

echo ""
echo "Logs Grafana (dernières 5 lignes):"
podman logs ecomptaia-grafana --tail 5 2>/dev/null || echo "Container non trouvé"

echo ""

# Résumé de santé
echo -e "${BLUE}📋 RÉSUMÉ DE SANTÉ${NC}"
echo "=================="

HEALTHY_SERVICES=0
TOTAL_SERVICES=7

# Compter les services en bonne santé
for port in 9090 3000 9100 9187 9121 9093 8080; do
    if curl -s "http://localhost:$port" > /dev/null 2>&1; then
        ((HEALTHY_SERVICES++))
    fi
done

HEALTH_PERCENTAGE=$((HEALTHY_SERVICES * 100 / TOTAL_SERVICES))

if [ $HEALTH_PERCENTAGE -eq 100 ]; then
    echo -e "${GREEN}🎉 Tous les services sont en bonne santé (100%)${NC}"
elif [ $HEALTH_PERCENTAGE -ge 80 ]; then
    echo -e "${GREEN}✅ La plupart des services sont en bonne santé (${HEALTH_PERCENTAGE}%)${NC}"
elif [ $HEALTH_PERCENTAGE -ge 60 ]; then
    echo -e "${YELLOW}⚠️  Certains services ont des problèmes (${HEALTH_PERCENTAGE}%)${NC}"
else
    echo -e "${RED}❌ Beaucoup de services ont des problèmes (${HEALTH_PERCENTAGE}%)${NC}"
fi

echo "Services en bonne santé: $HEALTHY_SERVICES/$TOTAL_SERVICES"

echo ""
echo -e "${BLUE}🔧 Actions recommandées:${NC}"
echo "========================"

if [ $HEALTH_PERCENTAGE -lt 100 ]; then
    echo -e "${YELLOW}• Vérifier les logs des services défaillants${NC}"
    echo -e "${YELLOW}• Redémarrer les services problématiques${NC}"
    echo -e "${YELLOW}• Vérifier la configuration des variables d'environnement${NC}"
    echo -e "${YELLOW}• Contrôler l'espace disque et la mémoire${NC}"
else
    echo -e "${GREEN}• Tous les services fonctionnent correctement${NC}"
    echo -e "${GREEN}• Le monitoring est opérationnel${NC}"
    echo -e "${GREEN}• Aucune action requise${NC}"
fi

echo ""
echo -e "${GREEN}✅ Vérification de santé terminée${NC}"

