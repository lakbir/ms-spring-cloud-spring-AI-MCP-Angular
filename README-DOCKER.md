# 🐳 Guide Docker - Ebank Microservices

## 📋 Table des matières
- [Vue d'ensemble](#vue-densemble)
- [Prérequis](#prérequis)
- [Architecture et ordre de démarrage](#architecture-et-ordre-de-démarrage)
- [Installation et configuration](#installation-et-configuration)
- [Commandes essentielles](#commandes-essentielles)
- [Dépannage](#dépannage)
- [Monitoring et logs](#monitoring-et-logs)

---

## 🎯 Vue d'ensemble

Cette configuration Docker orchestr le démarrage de 6 services dans un ordre spécifique pour assurer que les dépendances réseau soient correctement établies :

1. **Discovery Service** (Eureka) - Registre de services
2. **Ebank Service** - Service métier
3. **Customer Service** - Service clients
4. **Gateway Service** - Routeur API
5. **Ebank Chatbot Service** - Service IA
6. **Frontend** - Application Angular

### Architecture réseau

```
┌─────────────────────────────────────────────────────────┐
│                    Ebank Network (Bridge)               │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐           │
│  │Discovery │    │  Ebank   │    │ Customer │           │
│  │ Service  │←──→│ Service  │←──→│ Service  │           │
│  │ :8761    │    │ :8057    │    │ :8056    │           │
│  └──────────┘    └──────────┘    └──────────┘           │
│       ▲                                ▲                  │
│       │                                │                  │
│       └────────────────┬───────────────┘                  │
│                        ▼                                  │
│                  ┌──────────┐                             │
│                  │ Gateway  │                             │
│                  │ Service  │                             │
│                  │ :8888    │                             │
│                  └──────────┘                             │
│                        ▲                                  │
│                        │                                  │
│                  ┌─────────────┐                          │
│                  │Ebank Chatbot│                          │
│                  │   :8058     │                          │
│                  └─────────────┘                          │
│                        ▲                                  │
│                        │                                  │
│                  ┌─────────────┐                          │
│                  │  Frontend   │                          │
│                  │   :4200     │                          │
│                  └─────────────┘                          │
│                                                           │
└─────────────────────────────────────────────────────────┘

    ┌─────────────────────────────────┐
    │   Accès depuis l'hôte Windows    │
    ├─────────────────────────────────┤
    │ - Frontend:    http://localhost:4200    │
    │ - Gateway:     http://localhost:8888    │
    │ - Discovery:   http://localhost:8761    │
    │ - Customer:    http://localhost:8056    │
    │ - Chatbot:     http://localhost:8058    │
    │ - Ebank:       http://localhost:8057    │
    └─────────────────────────────────┘
```

---

## 📦 Prérequis

### Système d'exploitation
- **Windows 10/11** avec Docker Desktop installé

### Logiciels requis
1. **Docker Desktop** (v4.0 ou supérieur)
   - [Télécharger Docker Desktop](https://www.docker.com/products/docker-desktop)
   - Installation avec Hyper-V activé

2. **Git Bash** ou **PowerShell** (déjà inclus dans Windows)

3. **.NET Framework** (optionnel, pour les commandes Docker Desktop)

### Configuration système minimale
- **RAM** : 8 GB (recommandé 16 GB)
- **Disque dur** : 20 GB d'espace libre
- **Processeur** : Minimum 4 cœurs

### Vérification des prérequis

```powershell
# Vérifier l'installation de Docker
docker --version

# Vérifier le service Docker
docker ps

# Vérifier la configuration docker-compose
docker-compose --version
```

---

## 🏗️ Architecture et ordre de démarrage

### Ordre de démarrage géré par `depends_on`

Le fichier `docker-compose.yml` utilise `depends_on` avec condition `service_healthy` pour garantir l'ordre :

```
[START] 
   │
   ▼
┌────────────────────────────────────────┐
│ 1. DISCOVERY SERVICE (Eureka)          │
│    - Port: 8761                        │
│    - Healthcheck: /actuator/health     │
│    - Statut: ✓ Healthy                 │
└────────────────────────────────────────┘
   │
   ├─ Attend healthcheck OK... (10-30s)
   │
   ▼
┌────────────────────────────────────────┐
│ 2. EBANK SERVICE                       │
│    - Port: 8057                        │
│    - Dépend de: Discovery (Healthy)    │
│    - Enregistrement Eureka automatique │
│    - Statut: ✓ Healthy                 │
└────────────────────────────────────────┘
   │
   ├─ Attend healthcheck OK...
   │
   ▼
┌────────────────────────────────────────┐
│ 3. CUSTOMER SERVICE                    │
│    - Port: 8056                        │
│    - Dépend de: Ebank Service (Healthy)│
│    - Enregistrement Eureka automatique │
│    - Statut: ✓ Healthy                 │
└────────────────────────────────────────┘
   │
   ├─ Attend healthcheck OK...
   │
   ▼
┌────────────────────────────────────────┐
│ 4. GATEWAY SERVICE                     │
│    - Port: 8888                        │
│    - Dépend de: Customer Service       │
│    - Routes vers tous les services     │
│    - Statut: ✓ Healthy                 │
└────────────────────────────────────────┘
   │
   ├─ Attend healthcheck OK...
   │
   ▼
┌────────────────────────────────────────┐
│ 5. EBANK CHATBOT SERVICE               │
│    - Port: 8058                        │
│    - Dépend de: Gateway (Healthy)     │
│    - Connecté aux autres services MCP  │
│    - Statut: ✓ Healthy                 │
└────────────────────────────────────────┘
   │
   ├─ Attend healthcheck OK...
   │
   ▼
┌────────────────────────────────────────┐
│ 6. FRONTEND (Angular + Nginx)          │
│    - Port: 4200                        │
│    - Dépend de: Chatbot (Healthy)      │
│    - Routes API vers Gateway           │
│    - Statut: ✓ Running                 │
└────────────────────────────────────────┘
   │
   ▼
[READY] Tous les services sont opérationnels
```

### Healthchecks

Chaque service inclut un **healthcheck** qui s'exécute toutes les 10 secondes :

```dockerfile
HEALTHCHECK --interval=10s --timeout=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:PORT/actuator/health || exit 1
```

**Exemple d'attente** :
- Un service met 15-30 secondes pour être prêt
- Les healthchecks échouent jusqu'à ce que le service soit complètement démarré
- Après 3 healthchecks réussis, le service est marqué comme "healthy"
- Le prochain service dans la dépendance peut alors commencer

---

## 💾 Installation et configuration

### Étape 1 : Cloner ou localiser le projet

```powershell
# Naviguez vers le répertoire du projet
cd C:\workspace\ebank-ms-app
```

### Étape 2 : Vérifier la structure du projet

Vérifiez que tous les fichiers Dockerfile ont été créés dans les répertoires respectifs :

```powershell
# Vérifier la présence des Dockerfiles
Get-ChildItem -Recurse -Filter "Dockerfile" | Select-Object FullName

# Output attendu:
# C:\workspace\ebank-ms-app\discovery-service\Dockerfile
# C:\workspace\ebank-ms-app\ebank-service\Dockerfile
# C:\workspace\ebank-ms-app\customer-service\Dockerfile
# C:\workspace\ebank-ms-app\gateway-service\Dockerfile
# C:\workspace\ebank-ms-app\ebank-chatbot\Dockerfile
# C:\workspace\ebank-ms-app\ebank-front\Dockerfile
```

### Étape 3 : Configuration des variables d'environnement

Créez un fichier `.env` à la racine du projet pour les variables sensibles :

```powershell
# Créer le fichier .env
New-Item -Path "C:\workspace\ebank-ms-app\.env" -ItemType File

# Éditer le fichier et ajouter:
cat > C:\workspace\ebank-ms-app\.env << EOF
# Configuration de l'API Groq/OpenAI pour le chatbot
API_KEY=gsk_votre_clé_api_ici

# Configuration de la base de données (si nécessaire)
DB_URL=jdbc:h2:mem:customer-db
DB_DRIVER=org.h2.Driver

# Configuration Spring Boot
SPRING_PROFILES_ACTIVE=docker
EOF
```

### Étape 4 : Vérifier la configuration Angular

Assurez-vous que votre `package.json` a le script de build :

```powershell
# Vérifier le package.json
Get-Content C:\workspace\ebank-ms-app\ebank-front\package.json | Select-String "build"

# Output attendu:
# "build": "ng build"
```

### Étape 5 : Configuration de Docker Desktop

1. Ouvrez **Docker Desktop**
2. Allez dans **Settings** → **Resources**
3. Allez dans **Advanced** et réglez :
   - **CPUs** : Minimum 4 cœurs
   - **Memory** : Minimum 8 GB
4. Cliquez sur **Apply & Restart**

---

## 🚀 Commandes essentielles

### 1. Démarrer tous les services

```powershell
# Depuis C:\workspace\ebank-ms-app
docker-compose up -d

# Avec affichage des logs en temps réel (ne pas fermer la fenêtre)
docker-compose up

# Builder les images et démarrer
docker-compose up -d --build
```

**Temps d'attente estimé** : 3-5 minutes (selon votre configuration)

### 2. Vérifier le statut des services

```powershell
# Voir le statut de tous les containers
docker-compose ps

# Voir le statut détaillé avec docker
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

**Output attendu** :
```
CONTAINER ID   NAMES                          STATUS                   PORTS
abc123...      ebank-discovery-service        Up 2 minutes (healthy)   0.0.0.0:8761->8761/tcp
def456...      ebank-service                  Up 1 minute (healthy)    0.0.0.0:8057->8057/tcp
ghi789...      ebank-customer-service         Up 50 seconds (healthy)  0.0.0.0:8056->8056/tcp
jkl012...      ebank-gateway-service          Up 40 seconds (healthy)  0.0.0.0:8888->8888/tcp
mno345...      ebank-chatbot-service          Up 25 seconds (healthy)  0.0.0.0:8058->8058/tcp
pqr678...      ebank-frontend                 Up 15 seconds (healthy)  0.0.0.0:4200->4200/tcp
```

### 3. Consulter les logs

```powershell
# Logs de tous les services en temps réel
docker-compose logs -f

# Logs d'un service spécifique
docker-compose logs -f discovery
docker-compose logs -f ebank-service
docker-compose logs -f customer-service
docker-compose logs -f gateway
docker-compose logs -f ebank-chatbot
docker-compose logs -f frontend

# Dernières 100 lignes de logs
docker-compose logs --tail=100 discovery

# Logs avec horodatage
docker-compose logs -f --timestamps discovery
```

### 4. Accéder aux services

Une fois tous les services démarrés, accédez à :

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:4200 | Application Angular |
| **Gateway** | http://localhost:8888 | Routeur API |
| **Discovery** | http://localhost:8761 | Eureka Dashboard |
| **Customer Service** | http://localhost:8056/actuator/health | Healthcheck |
| **Ebank Service** | http://localhost:8057/actuator/health | Healthcheck |
| **Chatbot Service** | http://localhost:8058/actuator/health | Healthcheck |

### 5. Arrêter les services

```powershell
# Arrêter (pause) les containers
docker-compose stop

# Arrêter et supprimer les containers
docker-compose down

# Supprimer aussi les volumes
docker-compose down -v

# Supprimer aussi les images natives
docker-compose down --rmi local

# Supprimer tout (images, volumes, networks)
docker-compose down -v --rmi all
```

### 6. Reconstruire les images

```powershell
# Forcer la reconstruction de toutes les images
docker-compose build --no-cache

# Reconstruire un service spécifique
docker-compose build --no-cache discovery

# Nouveau démarrage après rebuild
docker-compose up -d
```

### 7. Exécuter des commandes dans un container

```powershell
# Accéder au shell d'un container
docker-compose exec discovery sh
docker-compose exec customer-service sh
docker-compose exec frontend sh

# Exécuter une commande unique
docker-compose exec discovery ls -la
docker-compose exec customer-service ps aux | grep java
```

### 8. Nettoyer l'environnement Docker

```powershell
# Voir les images locales
docker images

# Supprimer les images non utilisées
docker image prune -a --force

# Supprimer tous les containers arrêtés
docker container prune --force

# Voir les volumes
docker volume ls

# Supprimer les volumes non utilisés
docker volume prune --force

# Voir les réseaux
docker network ls

# Nettoyer complètement (ATTENTION : supprime tout)
docker system prune -a --volumes --force
```

---

## 🐛 Dépannage

### Problème 1 : Les services ne démarrent pas

**Symptômes** :
```
ERROR: for ebank-discovery-service Cannot connect to Docker daemon
```

**Solutions** :
1. Vérifiez que Docker Desktop est en cours d'exécution
   ```powershell
   tasklist | findstr docker
   ```

2. Redémarrez Docker Desktop :
   - Appuyez sur Windows + Pause
   - Redémarrez Docker

3. Vérifiez les ressources :
   ```powershell
   # Afficher la mémoire disponible
   Get-WmiObject -Class Win32_ComputerSystem | Select-Object TotalPhysicalMemory
   ```

### Problème 2 : Erreur "port already in use"

**Symptômes** :
```
ERROR: for ebank-discovery-service Cannot start service: Ports are not available: exposing port 8761
```

**Solutions** :

```powershell
# Trouver le processus utilisant le port
netstat -ano | findstr :8761

# Tuer le processus
taskkill /PID <PID> /F

# Ou vérifier quels containers Docker utilisent ce port
docker ps --format "table {{.Ports}}\t{{.Names}}"
```

### Problème 3 : Un service n'est pas "healthy"

**Symptômes** :
```
ebank-service          Up 30 seconds (unhealthy)
```

**Vérifications** :

```powershell
# Voir les logs du service concerné
docker-compose logs ebank-service

# Vérifier manuellement le healthcheck
docker-compose exec ebank-service wget --quiet --tries=1 --spider http://localhost:8057/actuator/health
```

**Causes courantes** :
- Le service prend trop de temps à démarrer → Augmentez les timeouts dans le Dockerfile
- Erreur dans la configuration → Vérifiez les logs
- Ressources insuffisantes → Augmentez la RAM dans Docker Desktop

### Problème 4 : Le frontend ne se connecte pas au gateway

**Vérifications** :

```powershell
# Tester la connectivité vers le gateway depuis le frontend
docker-compose exec frontend wget --quiet --spider http://gateway:8888

# Vérifier la configuration Nginx
docker-compose exec frontend cat /etc/nginx/nginx.conf | grep gateway
```

**Solution** :
S'assurer que le nom du container est `gateway` dans le `docker-compose.yml`.

### Problème 5 : Erreur lors du build Maven

**Symptômes** :
```
ERROR] 'dependencies.dependency.version' for org.springframework.cloud:spring-cloud-starter-config
```

**Solution** :
Vérifiez que le `pom.xml` parent a la dependencyManagement pour Spring Cloud (voir la section Maven).

### Problème 6 : Images Docker trop volumineuses

**Optimisations** :

```powershell
# Utiliser des images de base plus petites (Alpine)
# Déjà fait dans le Dockerfile avec eclipse-temurin:21-jre-alpine

# Nettoyer les images non utilisées
docker image prune -a --force

# Voir la taille des images
docker images --format "table {{.Repository}}\t{{.Size}}"
```

---

## 📊 Monitoring et logs

### Tableau de bord en temps réel

```powershell
# Afficher les statistiques des containers
docker stats

# Logs avec timestamps
docker-compose logs -f --timestamps
```

### Analyser les performances

```powershell
# Inspecter un container
docker inspect ebank-discovery-service

# Afficher des événements Docker en temps réel
docker events

# Voir les logs depuis le démarrage
docker-compose logs discovery

# Exporter les logs
docker-compose logs discovery > discovery-logs.txt
```

### Vérifier les connexions réseau

```powershell
# Tester la connectivité entre containers
docker-compose exec customer-service ping discovery
docker-compose exec customer-service curl http://discovery:8761/actuator/health

# Afficher la configuration réseau
docker network inspect ebank-ms-app_ebank-network
```

### Exemple complet de monitoring

```powershell
# Terminal 1: Démarrer les services avec logs en temps réel
docker-compose up

# Terminal 2: Surveiller les statsistiques
docker stats

# Terminal 3: Vérifier les logs d'un service spécifique
docker-compose logs -f gateway

# Terminal 4: Tester les endpoints
for ($i = 0; $i -lt 10; $i++) {
    curl http://localhost:8761/actuator/health
    Start-Sleep -Seconds 2
}
```

---

## 📝 Configuration avancée

### Augmenter la mémoire Java

Modifiez les variables d'environnement `JAVA_OPTS` dans le `docker-compose.yml` :

```yaml
environment:
  - JAVA_OPTS=-Xmx1024m -Xms512m  # Was -Xmx512m -Xms256m
```

### Ajouter des volumes pour la persistance

```yaml
services:
  ebank-service:
    volumes:
      - ./data/ebank:/data
      - ./logs/ebank:/logs
```

### Configurer les variables d'environnement par service

```yaml
services:
  ebank-chatbot:
    environment:
      - API_KEY=${API_KEY}
      - LOG_LEVEL=DEBUG
      - SPRING_PROFILES_ACTIVE=docker
```

---

## 🎓 Commandes de référence rapide

```powershell
# Démarrer
docker-compose up -d --build

# Vérifier le statut
docker-compose ps

# Voir les logs
docker-compose logs -f [service-name]

# Arrêter
docker-compose stop

# Nettoyer
docker-compose down -v

# Reconstruire
docker-compose build --no-cache

# Accéder à un container
docker-compose exec [service-name] sh

# Redémarrer un service
docker-compose restart [service-name]
```

---

## ✅ Checklist de démarrage

- [ ] Docker Desktop V4.0+ installé et en cours d'exécution
- [ ] Minimum 8 GB de RAM alloués à Docker
- [ ] Tous les Dockerfiles créés dans les répertoires respectifs
- [ ] Fichier `.env` créé avec la clé API (si nécessaire)
- [ ] Ports 4200, 8056, 8057, 8058, 8761, 8888 disponibles
- [ ] Projet Maven compilé (`mvn clean install`)
- [ ] `docker-compose.yml` à la racine du projet

```powershell
# Commande de vérification finale
docker-compose config
```

---

## 📞 Support et ressources

- **Docker Documentation** : https://docs.docker.com/
- **Docker Compose** : https://docs.docker.com/compose/
- **Spring Boot Docker** : https://spring.io/guides/gs/spring-boot-docker/
- **Eureka Documentation** : https://github.com/Netflix/eureka

---

## 🔄 Workflow typique du développement

```powershell
# 1. Modifier le code
# 2. Arrêter les services (optionnel)
docker-compose stop

# 3. Reconstruire les images avec les nouveaux changements
docker-compose build --no-cache [service-name]

# 4. Redémarrer
docker-compose up -d

# 5. Vérifier les logs
docker-compose logs -f [service-name]

# 6. Tester les endpoints
curl http://localhost:8888/actuator/health
```

---

**Documenté le : 2026-05-14**  
**Version : 1.0**  
**Dernière mise à jour : Mai 2026**


