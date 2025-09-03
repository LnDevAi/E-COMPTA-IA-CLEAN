#!/bin/bash

# Script de déploiement E-COMPTA-IA avec Podman Desktop
# Script Bash compatible avec tous les shells

echo "=== Deploiement E-COMPTA-IA INTERNATIONAL avec Podman Desktop ==="
echo "====================================================="

# Vérifier que Podman est installé
if ! command -v podman &> /dev/null; then
    echo "ERREUR Podman n'est pas installe ou pas dans le PATH"
    echo "Veuillez installer Podman Desktop depuis: https://podman-desktop.io/"
    exit 1
fi

# Afficher la version de Podman
PODMAN_VERSION=$(podman --version)
echo "OK Podman detecte: $PODMAN_VERSION"

# Vérifier que Podman Desktop est démarré
if ! podman info &> /dev/null; then
    echo "ERREUR Podman Desktop n'est pas demarre"
    echo "Veuillez demarrer Podman Desktop et reessayer"
    exit 1
fi

echo "OK Podman Desktop est demarre"

# Arrêter et supprimer les conteneurs existants
echo "Nettoyage des conteneurs existants..."
podman-compose -f docker/podman-compose.yml down --remove-orphans

# Supprimer les images existantes (optionnel)
echo "Suppression des images existantes..."
podman rmi $(podman images -q ecomptaia-*) --force 2>/dev/null || true

# Construire et démarrer les services
echo "Construction et demarrage des services..."
podman-compose -f docker/podman-compose.yml up --build -d

# Attendre que les services soient prêts
echo "Attente du demarrage des services..."
sleep 30

# Vérifier le statut des services
echo "Statut des services:"
podman-compose -f docker/podman-compose.yml ps

# Vérifier la santé des services
echo "Verification de la sante des services..."

# Backend
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "OK Backend: Service accessible"
else
    echo "ERREUR Backend: Service non accessible"
fi

# Frontend
if curl -s http://localhost:4200 > /dev/null; then
    echo "OK Frontend: Service accessible"
else
    echo "ERREUR Frontend: Service non accessible"
fi

# Base de données
if podman exec ecomptaia-postgres pg_isready -U ecomptaia_user > /dev/null 2>&1; then
    echo "OK Base de donnees: Service accessible"
else
    echo "ERREUR Base de donnees: Service non accessible"
fi

# Afficher les URLs d'accès
echo "URLs d'acces:"
echo "   Frontend: http://localhost:4200"
echo "   Backend API: http://localhost:8080"
echo "   Base de donnees: localhost:5432"

echo "Deploiement termine!"
echo "Utilisez 'podman-compose -f docker/podman-compose.yml logs -f' pour voir les logs en temps reel"
