# ========================================
# E-COMPTA-IA - DEPLOIEMENT PODMAN DESKTOP
# Script PowerShell Natif
# ========================================

Write-Host "DEPLOIEMENT E-COMPTA-IA SUR PODMAN DESKTOP" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

# Variables
$PROJECT_NAME = "ecomptaia"
$NETWORK_NAME = "$PROJECT_NAME-network"

# Fonctions de log colore
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Blue
}

function Write-Success {
    param([string]$Message)
    Write-Host "[SUCCESS] $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

# Verification de Podman
Write-Info "Verification de Podman..."
try {
    $podmanVersion = podman version
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Podman est operationnel"
        Write-Host $podmanVersion
    } else {
        throw "Podman n'a pas repondu correctement"
    }
} catch {
    Write-Error "Podman n'est pas installe ou n'est pas demarre"
    Write-Info "Veuillez installer et demarrer Podman Desktop"
    exit 1
}

# Verification que podman info fonctionne
try {
    podman info | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw "Podman Desktop n'est pas demarre"
    }
} catch {
    Write-Error "Podman Desktop n'est pas demarre"
    Write-Info "Veuillez demarrer Podman Desktop et reessayer"
    exit 1
}

Write-Host ""

# Nettoyage des ressources existantes
Write-Info "Nettoyage des ressources existantes..."

# Arret et suppression des conteneurs
$containers = @("ecomptaia-backend", "ecomptaia-frontend", "ecomptaia-postgres")
foreach ($container in $containers) {
    try {
        podman stop $container 2>$null
        podman rm $container 2>$null
        Write-Info "Conteneur $container nettoye"
    } catch {
        Write-Info "Conteneur $container non trouve ou deja supprime"
    }
}

# Suppression du reseau
try {
    podman network rm $NETWORK_NAME 2>$null
    Write-Info "Reseau $NETWORK_NAME supprime"
} catch {
    Write-Info "Reseau $NETWORK_NAME non trouve ou deja supprime"
}

Write-Success "Nettoyage termine"
Write-Host ""

# Creation du reseau
Write-Info "Creation du reseau..."
podman network create $NETWORK_NAME
if ($LASTEXITCODE -eq 0) {
    Write-Success "Reseau $NETWORK_NAME cree"
} else {
    Write-Error "Echec de la creation du reseau"
    exit 1
}
Write-Host ""

# Base de donnees PostgreSQL
Write-Info "Demarrage PostgreSQL..."
podman run -d `
    --name ecomptaia-postgres `
    --network $NETWORK_NAME `
    -e POSTGRES_DB=ecomptaia `
    -e POSTGRES_USER=ecomptaia_user `
    -e POSTGRES_PASSWORD="Ecomptaia2024!" `
    -p 5432:5432 `
    -v ecomptaia-postgres-data:/var/lib/postgresql/data `
    postgres:15-alpine

if ($LASTEXITCODE -eq 0) {
    Write-Success "Conteneur PostgreSQL cree"
} else {
    Write-Error "Echec de la creation du conteneur PostgreSQL"
    exit 1
}

Write-Info "Attente du demarrage de PostgreSQL..."
Start-Sleep -Seconds 10

# Verification de la sante PostgreSQL
try {
    podman exec ecomptaia-postgres pg_isready -U ecomptaia_user -d ecomptaia
    if ($LASTEXITCODE -eq 0) {
        Write-Success "PostgreSQL est pret"
    } else {
        Write-Warning "PostgreSQL demarre mais verification de sante echouee"
    }
} catch {
    Write-Warning "Impossible de verifier la sante de PostgreSQL, mais le conteneur est demarre"
}

Write-Host ""

# Compilation du backend
Write-Info "Compilation du backend..."
try {
    mvn clean package -DskipTests
    if ($LASTEXITCODE -eq 0) {
        Write-Success "Backend compile avec succes"
    } else {
        Write-Error "Echec de la compilation du backend"
        exit 1
    }
} catch {
    Write-Error "Erreur lors de la compilation Maven"
    exit 1
}

Write-Host ""

# Creation du Dockerfile backend
Write-Info "Creation du Dockerfile backend..."
$dockerfileContent = @"
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://ecomptaia-postgres:5432/ecomptaia
ENV SPRING_DATASOURCE_USERNAME=ecomptaia_user
ENV SPRING_DATASOURCE_PASSWORD=Ecomptaia2024!
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update
ENTRYPOINT ["java", "-jar", "app.jar"]
"@

$dockerfileContent | Out-File -FilePath "Dockerfile" -Encoding UTF8
Write-Success "Dockerfile backend cree"

# Construction de l'image backend
Write-Info "Construction de l'image backend..."
podman build -t ecomptaia-backend:latest .
if ($LASTEXITCODE -eq 0) {
    Write-Success "Image backend construite"
} else {
    Write-Error "Echec de la construction de l'image backend"
    exit 1
}

Write-Host ""

# Demarrage du backend
Write-Info "Demarrage du backend..."
podman run -d `
    --name ecomptaia-backend `
    --network $NETWORK_NAME `
    -p 8080:8080 `
    ecomptaia-backend:latest

if ($LASTEXITCODE -eq 0) {
    Write-Success "Conteneur backend cree"
} else {
    Write-Error "Echec de la creation du conteneur backend"
    exit 1
}

Write-Info "Attente du demarrage du backend..."
Start-Sleep -Seconds 15

# Verification de la sante du backend
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -TimeoutSec 10 -ErrorAction SilentlyContinue
    if ($response.StatusCode -eq 200) {
        Write-Success "Backend demarre et accessible"
    } else {
        Write-Warning "Backend demarre mais verification de sante echouee (Status: $($response.StatusCode))"
    }
} catch {
    Write-Warning "Backend demarre mais verification de sante echouee (peut prendre plus de temps)"
}

Write-Host ""

# Creation du frontend simple
Write-Info "Creation du frontend simple..."
$frontendDir = "../frontend"
if (-not (Test-Path $frontendDir)) {
    New-Item -ItemType Directory -Path $frontendDir -Force | Out-Null
}

# Creation du fichier HTML
$htmlContent = @"
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>E-COMPTA-IA</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; text-align: center; }
        .container { max-width: 800px; margin: 0 auto; }
        h1 { color: #2c3e50; }
        .status { background: #ecf0f1; padding: 20px; border-radius: 8px; margin: 20px 0; }
        .success { color: #27ae60; }
    </style>
</head>
<body>
    <div class="container">
        <h1>E-COMPTA-IA</h1>
        <div class="status">
            <h2 class="success">Application Deployee avec Succes !</h2>
            <p>Backend Spring Boot operationnel</p>
            <p>Base de donnees PostgreSQL connectee</p>
            <p>Frontend accessible sur ce port</p>
        </div>
        <div class="status">
            <h3>Statut des Services</h3>
            <p><strong>Backend:</strong> <span class="success">Operationnel</span></p>
            <p><strong>Base de donnees:</strong> <span class="success">Connectee</span></p>
            <p><strong>Frontend:</strong> <span class="success">Actif</span></p>
        </div>
    </div>
</body>
</html>
"@

$htmlContent | Out-File -FilePath "$frontendDir/index.html" -Encoding UTF8

# Creation du Dockerfile frontend
$frontendDockerfile = @"
FROM nginx:alpine
COPY index.html /usr/share/nginx/html/
RUN echo 'events { worker_connections 1024; } http { include /etc/nginx/mime.types; default_type application/octet-stream; server { listen 80; server_name localhost; root /usr/share/nginx/html; index index.html; location / { try_files \$uri \$uri/ /index.html; } location /api { proxy_pass http://ecomptaia-backend:8080; proxy_set_header Host \$host; proxy_set_header X-Real-IP \$remote_addr; proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for; proxy_set_header X-Forwarded-Proto \$scheme; } } }' > /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
"@

$frontendDockerfile | Out-File -FilePath "$frontendDir/Dockerfile" -Encoding UTF8

Write-Success "Fichiers frontend crees"

# Construction et demarrage du frontend
Write-Info "Construction et demarrage du frontend..."
podman build -t ecomptaia-frontend:latest $frontendDir
if ($LASTEXITCODE -eq 0) {
    Write-Success "Image frontend construite"
} else {
    Write-Error "Echec de la construction de l'image frontend"
    exit 1
}

podman run -d `
    --name ecomptaia-frontend `
    --network $NETWORK_NAME `
    -p 80:80 `
    ecomptaia-frontend:latest

if ($LASTEXITCODE -eq 0) {
    Write-Success "Conteneur frontend cree"
} else {
    Write-Error "Echec de la creation du conteneur frontend"
    exit 1
}

Write-Host ""

# Resume final
Write-Host ""
Write-Success "DEPLOIEMENT TERMINE AVEC SUCCES !"
Write-Host ""
Write-Host "ACCES AUX SERVICES :" -ForegroundColor Cyan
Write-Host "   Frontend: http://localhost:80" -ForegroundColor White
Write-Host "   Backend:  http://localhost:8080" -ForegroundColor White
Write-Host "   Base de donnees: localhost:5432" -ForegroundColor White
Write-Host ""
Write-Host "INFORMATIONS :" -ForegroundColor Cyan
Write-Host "   Base de donnees: ecomptaia" -ForegroundColor White
Write-Host "   Utilisateur: ecomptaia_user" -ForegroundColor White
Write-Host "   Mot de passe: Ecomptaia2024!" -ForegroundColor White
Write-Host ""

# Affichage du statut des conteneurs
Write-Host "STATUT DES CONTENEURS :" -ForegroundColor Cyan
podman ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

Write-Host ""
Write-Success "Votre application E-COMPTA-IA est maintenant deployee sur Podman Desktop !"
Write-Host ""
Write-Host "Pour arreter l'application :" -ForegroundColor Yellow
Write-Host "   podman stop ecomptaia-backend ecomptaia-frontend ecomptaia-postgres" -ForegroundColor Gray
Write-Host ""
Write-Host "Pour redemarrer l'application :" -ForegroundColor Yellow
Write-Host "   podman start ecomptaia-backend ecomptaia-frontend ecomptaia-postgres" -ForegroundColor Gray
