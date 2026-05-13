# 🚀 Démarrage rapide - Docker Ebank Microservices

## ⚡ Démarrage en 5 minutes

### Prérequis
- **Docker Desktop** v4.0+ ([Télécharger](https://www.docker.com/products/docker-desktop))
- Minimum **8 GB de RAM** disponible
- Windows 10/11

### Étape 1: Démarrer les services

**Option A - PowerShell (Recommandé)**
```powershell
# Naviguez vers le répertoire du projet
cd C:\workspace\ebank-ms-app

# Démarrer tous les services
powershell -ExecutionPolicy Bypass -File .\start-docker-services.ps1 up

# Voir les logs
powershell -ExecutionPolicy Bypass -File .\start-docker-services.ps1 logs

# Arrêter
powershell -ExecutionPolicy Bypass -File .\start-docker-services.ps1 down
```

**Option B - Command Prompt**
```cmd
cd C:\workspace\ebank-ms-app
start-docker-services.bat up
```

**Option C - Docker Compose directement**
```powershell
cd C:\workspace\ebank-ms-app
docker-compose up -d --build
docker-compose ps
```

### Étape 2: Attendre le démarrage complet

Les services démarrent dans cet ordre:
1. ✅ **Discovery Service** (10-20s)
2. ✅ **Ebank Service** (15-25s)
3. ✅ **Customer Service** (15-25s)
4. ✅ **Gateway Service** (15-25s)
5. ✅ **Chatbot Service** (15-25s)
6. ✅ **Frontend** (10-15s)

**Temps total estimé: 3-5 minutes**

### Étape 3: Accéder aux services

Une fois tous les services marqués comme "healthy" :

| Service | URL | Description |
|---------|-----|-------------|
| **Frontend** | http://localhost:4200 | 🎨 Application web |
| **Gateway** | http://localhost:8888 | 🚪 Routeur API |
| **Eureka** | http://localhost:8761 | 📊 Dashboard services |

### Vérifier le statut

```powershell
# Voir tous les containers
docker-compose ps

# Voir un service spécifique
docker-compose ps | findstr customer-service

# Voir les logs d'un service
docker-compose logs -f customer-service

# Tester la connectivité
curl http://localhost:8761/actuator/health
```

## 🛑 Arrêter les services

```powershell
# Arrêter (pause)
docker-compose stop

# Arrêter et supprimer
docker-compose down

# Supprimer aussi les volumes
docker-compose down -v
```

## 🔧 Configuration

### Variables d'environnement

Créez un fichier `.env` à la racine:

```powershell
# Copier depuis le template
Copy-Item .env.example .env

# Éditer avec vos valeurs
# API_KEY=votre_clé_api
```

### Augmenter la mémoire Java

Si les services font des timeouts, modifiez `docker-compose.yml`:

```yaml
environment:
  # De ceci:
  - JAVA_OPTS=-Xmx512m -Xms256m
  # À ceci:
  - JAVA_OPTS=-Xmx1024m -Xms512m
```

## 🐛 Problèmes courants

### Docker n'est pas en cours d'exécution
```powershell
# Démarrer Docker Desktop manuellement
# Ou vérifier son statut
docker ps
```

### Port déjà utilisé
```powershell
# Trouver le processus utilisant le port (ex: 8888)
netstat -ano | findstr :8888

# Tuer le processus
taskkill /PID <PID> /F
```

### Un service n'est pas "healthy"
```powershell
# Voir les logs du service
docker-compose logs customer-service

# Attendre un peu plus (peut prendre 30-60s)
Start-Sleep -Seconds 30
docker-compose ps
```

### Manque de ressources
```powershell
# Augmentez la RAM dans Docker Desktop:
# Settings → Resources → Advanced → Memory: 12-16 GB
```

## 📊 Monitoring en temps réel

```powershell
# Terminal 1: Démarrer les services
docker-compose up

# Terminal 2: Voir les logs
docker-compose logs -f

# Terminal 3: Voir les stats
docker stats
```

## 🧹 Nettoyer complètement

```powershell
# Arrêter et supprimer tout
docker-compose down -v --rmi local

# Vérifier qu'il ne reste rien
docker images
docker ps -a
```

## 📚 Documentation complète

Voir [README-DOCKER.md](./README-DOCKER.md) pour la documentation détaillée incluant:
- Architecture réseau complète
- Tous les ports et services
- Commandes avancées
- Troubleshooting détaillé
- Configuration avancée

## 🎓 Commandes essentielles

```powershell
# Démarrer
docker-compose up -d --build

# Voir le statut
docker-compose ps

# Logs temps réel
docker-compose logs -f

# Arrêter
docker-compose down

# Redémarrer un service
docker-compose restart customer-service

# Exécuter une commande
docker-compose exec customer-service sh

# Reconstruire les images
docker-compose build --no-cache
```

## ✅ Checklist rapide

- [ ] Docker Desktop 4.0+ installé et lancé
- [ ] Minimum 8 GB de RAM disponible
- [ ] Tous les Dockerfiles présents (voir ci-dessous)
- [ ] Fichier `docker-compose.yml` à la racine
- [ ] Fichier `.env` créé (copier `.env.example`)

```powershell
# Vérifier la présence des Dockerfiles
Get-ChildItem -Recurse -Filter "Dockerfile" -Path C:\workspace\ebank-ms-app
```

---

**💡 Conseil**: Gardez un terminal ouvert avec `docker-compose logs -f` pour monitorer tous les services!

**📞 Besoin d'aide?** Voir [README-DOCKER.md](./README-DOCKER.md) ou consulter la documentation Docker officielle.


