# Stage 1: Build Angular
FROM node:20-alpine AS builder

WORKDIR /build

# Copier les fichiers de configuration
COPY ebank-front/package*.json ./

# Installer les dépendances
RUN npm ci

# Copier le code source
COPY ebank-front/ ./

# Builder la version de production
RUN npm run build

# Stage 2: Serveur Nginx
FROM nginx:alpine

# Copier les fichiers construits vers Nginx
COPY --from=builder /build/dist/ebank-front /usr/share/nginx/html

# Copier la configuration Nginx personnalisée
COPY ebank-front/nginx.conf /etc/nginx/nginx.conf

# Exposer le port
EXPOSE 4200

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:4200 || exit 1

# Lancer Nginx
CMD ["nginx", "-g", "daemon off;"]

