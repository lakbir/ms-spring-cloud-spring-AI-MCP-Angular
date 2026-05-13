# 📦 Configuration Docker - Synthèse Complète

Bienvenue! Cette configuration Docker permet d'orchestrer facilement tous vos services microservices.

## 📁 Fichiers créés

```
ebank-ms-app/
├── docker-compose.yml              ← Configuration d'orchestration (PRINCIPAL)
├── .dockerignore                   ← Fichiers à ignorer lors du build
├── .env.example                    ← Template des variables d'environnement
├── QUICKSTART-DOCKER.md            ← Guide de démarrage rapide (5 minutes)
├── README-DOCKER.md                ← Documentation complète et détaillée
├── start-docker-services.ps1       ← Script PowerShell (Windows)
├── start-docker-services.bat       ← Script Batch (Windows)
│
├── discovery-service/
│   └── Dockerfile                  ← Builder pour le service Discovery
├── ebank-service/
│   └── Dockerfile                  ← Builder pour le service Ebank
├── customer-service/
│   └── Dockerfile                  ← Builder pour le service Customer
├── gateway-service/
│   └── Dockerfile                  ← Builder pour le service Gateway
├── ebank-chatbot/
│   └── Dockerfile                  ← Builder pour le service Chatbot
└── ebank-front/
    ├── Dockerfile                  ← Builder pour le frontend Angular
    └── nginx.conf                  ← Configuration serveur Nginx
```

## 🎯 Démarrage rapide

### 1️⃣ Via script PowerShell (Recommandé)

```powershell
cd C:\workspace\ebank-ms-app
powershell -ExecutionPolicy Bypass -File .\start-docker-services.ps1 up
```

### 2️⃣ Via script Batch

```cmd
cd C:\workspace\ebank-ms-app
start-docker-services.bat up
```

### 3️⃣ Via Docker Compose directement

```powershell
cd C:\workspace\ebank-ms-app
docker-compose up -d --build
docker-compose ps
docker-compose logs -f
```

## 📖 Documentation

- **[QUICKSTART-DOCKER.md](./QUICKSTART-DOCKER.md)** - Démarrage en 5 minutes ⚡
- **[README-DOCKER.md](./README-DOCKER.md)** - Documentation complète 📚
- **[docker-compose.yml](./docker-compose.yml)** - Configuration d'orchestration 🐳

## 🚀 Services et ports

| # | Service | Port | Statut | URL |
|---|---------|------|--------|-----|
| 1 | Discovery Service (Eureka) | 8761 | ✓ | http://localhost:8761 |
| 2 | Ebank Service | 8057 | ✓ | http://localhost:8057/actuator/health |
| 3 | Customer Service | 8056 | ✓ | http://localhost:8056/actuator/health |
| 4 | Gateway Service | 8888 | ✓ | http://localhost:8888 |
| 5 | Chatbot Service | 8058 | ✓ | http://localhost:8058/actuator/health |
| 6 | Frontend (Angular) | 4200 | ✓ | http://localhost:4200 |

## ⏱️ Ordre de démarrage

```
1. Discovery Service        [Démarrage]
   ↓ (Attend healthcheck)
2. Ebank Service            [Démarrage]
   ↓
3. Customer Service         [Démarrage]
   ↓
4. Gateway Service          [Démarrage activé une fois Customer Service healthy]
   ↓
5. Chatbot Service          [Démarrage activé une fois Gateway Service healthy]
   ↓
6. Frontend                 [Démarrage final]

Temps total: 3-5 minutes
```

## 🎮 Commandes principales

### Démarrage
```powershell
docker-compose up -d --build          # Démarrer en arrière-plan
docker-compose up                     # Démarrer avec logs
```

### Monitoring
```powershell
docker-compose ps                     # Voir le statut
docker-compose logs -f                # Logs temps réel
docker-compose logs -f customer-service  # Logs d'un service
docker-statistics                     # Stats CPU/RAM
```

### Arrêt
```powershell
docker-compose stop                   # Pause
docker-compose down                   # Arrêt complet
docker-compose down -v                # Avec suppression des volumes
```

### Maintenance
```powershell
docker-compose restart                # Redémarrer
docker-compose build --no-cache       # Reconstruire les images
docker-compose exec customer-service sh  # Terminal dans un container
```

## ⚙️ Configuration

### 1. Variables d'environnement

```powershell
# Créer le fichier .env depuis le template
Copy-Item .env.example .env
```

Éditer `.env` pour ajouter:
```env
API_KEY=gsk_votre_clé_api_groq
LOG_LEVEL=INFO
JAVA_OPTS=-Xmx512m -Xms256m
```

### 2. Ressources Docker

Modifier dans Docker Desktop Settings:
- **Memory**: 8-16 GB minimum
- **CPUs**: 4-8 cœurs
- **Disk**: 20 GB minimum

### 3. Configuration par service

Chaque service peut être configuré via variables d'environnement dans `docker-compose.yml`:

```yaml
services:
  customer-service:
    environment:
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://discovery:8761/eureka/
      - JAVA_OPTS=-Xmx512m -Xms256m
```

## 🏥 Diagnostic

### Vérifier l'installation
```powershell
docker --version              # Docker
docker-compose --version      # Docker Compose
docker ps                      # Services actifs
```

### Vérifier la configuration
```powershell
docker-compose config         # Valider docker-compose.yml
docker-compose images         # Voir les images construites
docker network ls             # Voir les réseaux
```

### Logs et résolution de problèmes
```powershell
docker-compose logs discovery          # Logs d'un service
docker-compose logs --tail 100         # Dernières 100 lignes
docker-compose logs --timestamps       # Avec horodatage
docker inspect ebank-discovery-service # Détails d'un container
```

## 🔗 Réseau Docker

Les services communiquent via un réseau Docker bridge `ebank-network`:

```
Frontend (4200)
    ↓ (proxy)
Nginx
    ↓
Gateway (8888)
    ↓
├─ Eureka (8761)
├─ Customer Service (8056)
├─ Ebank Service (8057)
└─ Chatbot (8058)
```

Depuis le host Windows, utilisez `localhost` ou `127.0.0.1`.

## 🐛 Problèmes courants

| Problème | Cause | Solution |
|----------|-------|----------|
| Docker daemon not running | Docker Desktop arrêté | Démarrer Docker Desktop |
| Port already in use | Port occupé | `netstat -ano \| findstr :8888` puis `taskkill /PID <PID> /F` |
| Service unhealthy | Démarrage lent | Attendre 30-60s, vérifier les logs |
| Connexion refusée | Service pas prêt | Vérifier le statut avec `docker-compose ps` |
| Manque de ressources | RAM insuffisante | Augmenter la RAM dans Docker Desktop |

**Voir [README-DOCKER.md](./README-DOCKER.md) pour plus de détails.**

## 🔄 Workflow développement

```powershell
# 1. Modifier le code
# 2. Arrêter les services
docker-compose stop

# 3. Reconstruire
docker-compose build --no-cache [service-name]

# 4. Redémarrer
docker-compose up -d

# 5. Vérifier
docker-compose logs -f [service-name]

# 6. Tester
curl http://localhost:8888/actuator/health
```

## 📊 Architecture réseau

```
┌─────────────────────────────────────────────┐
│        Docker Bridge Network                │
│       (ebank-network)                       │
│                                             │
│  ┌──────────────┐                          │
│  │ Discovery    │                          │
│  │ :8761 (TCP)  │ ← Eureka Service         │
│  └──────────────┘                          │
│         ↑                                   │
│         │ register                         │
│  ┌──────────────┐  ┌──────────────┐       │
│  │ Ebank Srv    │  │ Customer Srv │        │
│  │ :8057 (TCP)  │  │ :8056 (TCP)  │       │
│  └──────────────┘  └──────────────┘       │
│         ↑                 ↑                │
│         │                 │                │
│      ┌──────────────────────────┐          │
│      │    Gateway Service       │          │
│      │    :8888 (TCP)           │          │
│      └──────────────────────────┘          │
│              ↑                              │
│              │                              │
│      ┌──────────────────────────┐          │
│      │   Chatbot Service        │          │
│      │   :8058 (TCP)            │          │
│      └──────────────────────────┘          │
│              ↑                              │
│              │                              │
│      ┌──────────────────────────┐          │
│      │   Frontend (Nginx)       │          │
│      │   :4200 (TCP)            │          │
│      └──────────────────────────┘          │
│                                             │
└─────────────────────────────────────────────┘
                    ↑↓
         ┌──────────────────────┐
         │ Host Windows         │
         │ localhost/127.0.0.1  │
         └──────────────────────┘
```

## 📞 Support

- **Docker**: https://docs.docker.com/
- **Docker Compose**: https://docs.docker.com/compose/
- **Spring Boot**: https://spring.io/
- **Angular**: https://angular.io/
- **Eureka**: https://github.com/Netflix/eureka

## 📋 Checklist startup

- [ ] Docker Desktop v4.0+ installé
- [ ] Minimum 8 GB RAM activée
- [ ] Tous les Dockerfiles présents
- [ ] `docker-compose.yml` présent
- [ ] `.env` créé depuis `.env.example`
- [ ] Port 4200, 8056, 8057, 8058, 8761, 8888 disponibles
- [ ] `docker-compose config` valide


---

**✅ Vous êtes prêt à démarrer !**

Lancez: `powershell -ExecutionPolicy Bypass -File .\start-docker-services.ps1 up`

Puis consultez http://localhost:4200 une fois tous les services démarrés (3-5 minutes).


