@echo off
setlocal enabledelayedexpansion

REM Script de démarrage rapide pour l'orchestration Docker
REM Utilisation: .\start-docker-services.bat up|down|logs|ps|restart|rebuild|clean

color 0B
title Ebank Microservices - Docker Orchestration

set ACTION=%1
if "%ACTION%"=="" set ACTION=up

REM Vérifier les prérequis
echo.
echo ========================================
echo   Ebank Microservices - Docker
echo ========================================
echo.

echo Vérification des prérequis...
docker --version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo [ERREUR] Docker n'est pas installé ou pas dans le PATH
    echo Veuillez installer Docker Desktop: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    color 0C
    echo [ERREUR] Docker Compose n'est pas installé
    pause
    exit /b 1
)

docker ps >nul 2>&1
if errorlevel 1 (
    color 0E
    echo [ATTENTION] Docker n'est pas en cours d'exécution
    echo Veuillez démarrer Docker Desktop
    pause
    exit /b 1
)

color 0A
echo [OK] Docker et Docker Compose sont correctement configurés
echo.

REM Exécuter l'action demandée
if "%ACTION%"=="up" goto START_SERVICES
if "%ACTION%"=="down" goto STOP_SERVICES
if "%ACTION%"=="logs" goto SHOW_LOGS
if "%ACTION%"=="ps" goto SHOW_STATUS
if "%ACTION%"=="restart" goto RESTART_SERVICES
if "%ACTION%"=="rebuild" goto REBUILD_IMAGES
if "%ACTION%"=="clean" goto CLEAN_ENVIRONMENT
goto USAGE

:START_SERVICES
echo Démarrage des services...
if not exist ".env" (
    echo.
    if exist ".env.example" (
        echo Création du fichier .env depuis .env.example...
        copy .env.example .env
        echo [INFO] Fichier .env créé. Veuillez éditer les valeurs si nécessaire.
    )
)
echo.
docker-compose up -d --build
if errorlevel 1 (
    color 0C
    echo [ERREUR] Erreur lors du démarrage
    pause
    exit /b 1
)
color 0A
echo.
echo [OK] Services démarrés avec succès!
echo.
echo Attente du démarrage... (3-5 minutes)
timeout /t 5 /nobreak
echo.
docker-compose ps
echo.
echo Services disponibles:
echo   - Frontend:     http://localhost:4200
echo   - Gateway:      http://localhost:8888
echo   - Discovery:    http://localhost:8761
echo   - Customer:     http://localhost:8056
echo   - Ebank:        http://localhost:8057
echo   - Chatbot:      http://localhost:8058
echo.
pause
exit /b 0

:STOP_SERVICES
echo Arrêt des services...
docker-compose down
if errorlevel 1 (
    color 0C
    echo [ERREUR] Erreur lors de l'arrêt
    pause
    exit /b 1
)
color 0A
echo [OK] Services arrêtés
pause
exit /b 0

:SHOW_LOGS
echo Affichage des logs (Ctrl+C pour arrêter)
docker-compose logs -f
exit /b 0

:SHOW_STATUS
echo Statut des services:
echo.
docker-compose ps
echo.
pause
exit /b 0

:RESTART_SERVICES
echo Redémarrage des services...
docker-compose restart
if errorlevel 1 (
    color 0C
    echo [ERREUR] Erreur lors du redémarrage
    pause
    exit /b 1
)
color 0A
echo [OK] Services redémarrés
timeout /t 3 /nobreak
docker-compose ps
pause
exit /b 0

:REBUILD_IMAGES
echo Reconstruction des images (peut prendre plusieurs minutes)...
docker-compose build --no-cache
if errorlevel 1 (
    color 0C
    echo [ERREUR] Erreur lors de la reconstruction
    pause
    exit /b 1
)
color 0A
echo [OK] Images reconstruites
echo Démarrage des services...
docker-compose up -d
docker-compose ps
pause
exit /b 0

:CLEAN_ENVIRONMENT
color 0E
echo [ATTENTION] Ceci va supprimer tous les containers, volumes et images
set /p CONFIRM="Êtes-vous sûr? (Tapez 'oui' pour confirmer): "
if not "%CONFIRM%"=="oui" (
    echo Nettoyage annulé
    pause
    exit /b 0
)
echo Nettoyage en cours...
docker-compose down -v --rmi local
if errorlevel 1 (
    color 0C
    echo [ERREUR] Erreur lors du nettoyage
    pause
    exit /b 1
)
color 0A
echo [OK] Nettoyage terminé
pause
exit /b 0

:USAGE
color 0E
echo Usage: %0 [action]
echo.
echo Actions disponibles:
echo   up        - Démarrer tous les services (défaut)
echo   down      - Arrêter les services
echo   logs      - Afficher les logs en temps réel
echo   ps        - Afficher le statut des services
echo   restart   - Redémarrer les services
echo   rebuild   - Reconstruire les images
echo   clean     - Nettoyer complètement l'environnement
echo.
pause
exit /b 1

