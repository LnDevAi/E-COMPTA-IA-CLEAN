# Stage 1: Build de l'application Angular
FROM node:20-alpine AS build

# Définir le répertoire de travail
WORKDIR /app

# Installer les outils nécessaires
RUN apk add --no-cache git

# Copier les fichiers de dépendances
COPY package*.json ./

# Installer les dépendances avec nettoyage du cache
RUN npm cache clean --force
RUN npm install --legacy-peer-deps

# Copier le code source
COPY . .

# Construire l'application pour la production
RUN npm run build

# Stage 2: Serveur web pour servir l'application
FROM nginx:alpine

# Copier les fichiers construits depuis le stage précédent
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Copier la configuration nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exposer le port 80
EXPOSE 80

# Commande pour démarrer nginx
CMD ["nginx", "-g", "daemon off;"]
