# Stage 1: Build de l'application Angular
FROM node:20-alpine AS build

# Définir le répertoire de travail
WORKDIR /app

# Installer les outils nécessaires
RUN apk add --no-cache git curl

# Copier les fichiers de dépendances
COPY package*.json ./

# Installer les dépendances (inclure devDependencies pour ng)
ENV NG_CLI_ANALYTICS=ci
RUN npm ci --legacy-peer-deps && \
    npm cache clean --force

# Copier le code source
COPY . .

# Construire l'application pour la production
RUN npm run build -- --configuration=production

# Stage 2: Serveur web optimisé
FROM nginx:1.27-alpine

# Installer curl pour le healthcheck
RUN apk add --no-cache curl

# Copier les fichiers construits depuis le stage précédent
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Copier la configuration nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Créer les répertoires nécessaires et définir les permissions
RUN mkdir -p /var/cache/nginx /var/log/nginx /var/run && \
    chown -R nginx:nginx /var/cache/nginx /var/log/nginx /var/run /usr/share/nginx/html

# Laisser l'utilisateur root par défaut pour autoriser l'écoute sur le port 80

# Exposer le port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD curl -f http://localhost/health || exit 1

# Commande pour démarrer nginx
CMD ["nginx", "-g", "daemon off;"]
