# Script de démarrage rapide pour l'orchestration Docker
# Utilisation: powershell -ExecutionPolicy Bypass -File .\start-docker-services.ps1

param(
    [Parameter(ParameterSetName="Action")]
    [ValidateSet("up", "down", "logs", "ps", "restart", "rebuild", "clean")]
    [string]$Action = "up",

    [Parameter(ParameterSetName="Action")]
    [string]$Service
)

# Couleurs pour les messages
$colors = @{
    Success = "Green"
    Error = "Red"
    Warning = "Yellow"
    Info = "Cyan"
}

function Write-ColorOutput($Message, $Color = "Info") {
    Write-Host $Message -ForegroundColor $colors[$Color]
}

function Check-Prerequisites {
    Write-ColorOutput "`n=== Vérification des prérequis ===" "Info"

    # Vérifier Docker
    try {
        $dockerVersion = docker --version
        Write-ColorOutput "✓ Docker: $dockerVersion" "Success"
    }
    catch {
        Write-ColorOutput "✗ Docker n'est pas installé ou pas dans le PATH" "Error"
        return $false
    }

    # Vérifier Docker Compose
    try {
        $composeVersion = docker-compose --version
        Write-ColorOutput "✓ Docker Compose: $composeVersion" "Success"
    }
    catch {
        Write-ColorOutput "✗ Docker Compose n'est pas installé" "Error"
        return $false
    }

    # Vérifier que Docker est en cours d'exécution
    try {
        docker ps > $null 2>&1
        Write-ColorOutput "✓ Docker is running" "Success"
    }
    catch {
        Write-ColorOutput "✗ Docker n'est pas en cours d'exécution. Veuillez démarrer Docker Desktop." "Error"
        return $false
    }

    return $true
}

function Start-Services {
    Write-ColorOutput "`n=== Démarrage des services ===" "Info"

    if (-not (Test-Path ".env")) {
        Write-ColorOutput "⚠ Fichier .env non trouvé. Création depuis .env.example..." "Warning"
        if (Test-Path ".env.example") {
            Copy-Item ".env.example" ".env"
            Write-ColorOutput "✓ Fichier .env créé. Veuillez éditer les valeurs si nécessaire." "Success"
        }
        else {
            Write-ColorOutput "✗ .env.example non trouvé" "Error"
            return $false
        }
    }

    Write-ColorOutput "Démarrage en mode détaché (sans logs)..." "Info"
    Write-ColorOutput "Utilisez 'powershell -File .\start-docker-services.ps1 -Action logs' pour voir les logs" "Info"

    docker-compose up -d --build

    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "`n✓ Services démarrés avec succès!" "Success"
        Write-ColorOutput "`nAttente du démarrage des services (3-5 minutes)..." "Info"

        # Attendre un peu
        Start-Sleep -Seconds 5

        # Afficher le statut
        Show-Status

        Write-ColorOutput "`nServices disponibles:" "Success"
        Write-ColorOutput "  - Frontend:     http://localhost:4200" "Info"
        Write-ColorOutput "  - Gateway:      http://localhost:8888" "Info"
        Write-ColorOutput "  - Discovery:    http://localhost:8761" "Info"
        Write-ColorOutput "  - Customer:     http://localhost:8056" "Info"
        Write-ColorOutput "  - Ebank:        http://localhost:8057" "Info"
        Write-ColorOutput "  - Chatbot:      http://localhost:8058" "Info"

        return $true
    }
    else {
        Write-ColorOutput "✗ Erreur lors du démarrage des services" "Error"
        return $false
    }
}

function Stop-Services {
    Write-ColorOutput "`n=== Arrêt des services ===" "Info"
    docker-compose down

    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "✓ Services arrêtés avec succès" "Success"
        return $true
    }
    else {
        Write-ColorOutput "✗ Erreur lors de l'arrêt des services" "Error"
        return $false
    }
}

function Show-Logs {
    Write-ColorOutput "`n=== Affichage des logs ===" "Info"

    if ($Service) {
        Write-ColorOutput "Logs du service: $Service" "Info"
        docker-compose logs -f $Service
    }
    else {
        Write-ColorOutput "Logs de tous les services (Ctrl+C pour arrêter):" "Info"
        docker-compose logs -f
    }
}

function Show-Status {
    Write-ColorOutput "`n=== Statut des services ===" "Info"
    docker-compose ps
}

function Restart-ServiceCmd {
    Write-ColorOutput "`n=== Redémarrage des services ===" "Info"

    if ($Service) {
        Write-ColorOutput "Redémarrage du service: $Service" "Info"
        docker-compose restart $Service
    }
    else {
        Write-ColorOutput "Redémarrage de tous les services..." "Info"
        docker-compose restart
    }

    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "✓ Services redémarrés" "Success"
        Start-Sleep -Seconds 3
        Show-Status
        return $true
    }
    else {
        Write-ColorOutput "✗ Erreur lors du redémarrage" "Error"
        return $false
    }
}

function Rebuild-Images {
    Write-ColorOutput "`n=== Reconstruction des images ===" "Info"
    Write-ColorOutput "Ceci peut prendre plusieurs minutes..." "Warning"

    docker-compose build --no-cache

    if ($LASTEXITCODE -eq 0) {
        Write-ColorOutput "✓ Images reconstruites" "Success"
        Write-ColorOutput "`nDémarrage des services..." "Info"
        Start-Services
        return $true
    }
    else {
        Write-ColorOutput "✗ Erreur lors de la reconstruction" "Error"
        return $false
    }
}

function Clean-Environment {
    Write-ColorOutput "`n=== Nettoyage de l'environnement ===" "Warning"
    Write-ColorOutput "Ceci va supprimer tous les containers, volumes et images locales" "Error"
    $response = Read-Host "Êtes-vous sûr? (oui/non)"

    if ($response -eq "oui") {
        Write-ColorOutput "Suppression en cours..." "Warning"
        docker-compose down -v --rmi local

        if ($LASTEXITCODE -eq 0) {
            Write-ColorOutput "✓ Nettoyage terminé" "Success"
            return $true
        }
        else {
            Write-ColorOutput "✗ Erreur lors du nettoyage" "Error"
            return $false
        }
    }
    else {
        Write-ColorOutput "Nettoyage annulé" "Info"
        return $false
    }
}

# Script principal
Write-ColorOutput @"
╔════════════════════════════════════════╗
║   Ebank Microservices - Orchestration  ║
║              Docker                    ║
╚════════════════════════════════════════╝
"@ "Success"

# Vérifier les prérequis
if (-not (Check-Prerequisites)) {
    Write-ColorOutput "`nVeruillez installer Docker Desktop et réessayer." "Error"
    exit 1
}

# Exécuter l'action demandée
switch ($Action) {
    "up" {
        Start-Services | Out-Null
    }
    "down" {
        Stop-Services | Out-Null
    }
    "logs" {
        Show-Logs
    }
    "ps" {
        Show-Status
    }
    "restart" {
        Restart-ServiceCmd | Out-Null
    }
    "rebuild" {
        Rebuild-Images | Out-Null
    }
    "clean" {
        Clean-Environment | Out-Null
    }
    default {
        Start-Services | Out-Null
    }
}

Write-ColorOutput "`n✓ Opération terminée" "Success"

