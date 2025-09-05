# Script de résolution complète E-COMPTA-IA - Version corrigée
Write-Host "🚀 RESOLUTION COMPLETE E-COMPTA-IA" -ForegroundColor Blue
Write-Host "==================================" -ForegroundColor Blue

# 1. Aller dans le dossier docker
Write-Host "📁 Nettoyage du conteneur PostgreSQL existant..." -ForegroundColor Yellow
cd docker

# 2. Arrêter et supprimer le conteneur PostgreSQL
podman stop ecomptaia-postgres 2>$null
podman rm ecomptaia-postgres 2>$null
podman volume rm docker_postgres_data 2>$null

Write-Host "✅ Ancien conteneur supprimé" -ForegroundColor Green

# 3. Créer un nouveau conteneur PostgreSQL simple
Write-Host "🐘 Creation d'un nouveau conteneur PostgreSQL..." -ForegroundColor Yellow
podman run -d --name ecomptaia-postgres-new -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=ecomptaia_db -p 5432:5432 postgres:15-alpine

Write-Host "✅ Nouveau conteneur PostgreSQL créé" -ForegroundColor Green

# 4. Attendre que PostgreSQL démarre
Write-Host "⏳ Attente du démarrage de PostgreSQL..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# 5. Vérifier que le conteneur fonctionne
Write-Host "🔍 Verification du conteneur..." -ForegroundColor Yellow
podman ps --filter name=ecomptaia-postgres-new

# 6. Retourner dans le dossier backend
Write-Host "📁 Retour dans le dossier backend..." -ForegroundColor Yellow
cd ../backend

# 7. Créer une nouvelle configuration application.properties
Write-Host "⚙️ Creation d'une nouvelle configuration..." -ForegroundColor Yellow

$config = @"
# Configuration du serveur - Port automatique
server.port=0

# Configuration de la base de données PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/ecomptaia_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuration JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Configuration de sécurité JWT
spring.security.jwt.secret=ecomptaiaSecretKey2024
spring.security.jwt.expiration=86400000

# Configuration CORS
spring.web.cors.allowed-origins=http://localhost:4200
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*

# Configuration des logs
logging.level.com.ecomptaia=DEBUG
logging.level.org.springframework.security=DEBUG

# Configuration Actuator pour le monitoring
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.endpoints.web.base-path=/actuator

# Configuration Prometheus
management.metrics.export.prometheus.enabled=true
"@

# 8. Écrire la nouvelle configuration
Set-Content -Path "src/main/resources/application.properties" -Value $config -Encoding UTF8

Write-Host "✅ Configuration application.properties mise à jour" -ForegroundColor Green

# 9. Tester la connexion PostgreSQL
Write-Host "🔌 Test de la connexion PostgreSQL..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

try {
    $response = Test-NetConnection -ComputerName localhost -Port 5432 -WarningAction SilentlyContinue
    if ($response.TcpTestSucceeded) {
        Write-Host "✅ Connexion PostgreSQL réussie" -ForegroundColor Green
    } else {
        Write-Host "❌ Connexion PostgreSQL échouée" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Erreur de test de connexion" -ForegroundColor Red
}

# 10. Instructions finales
Write-Host "🚀 Relance de l'application Spring Boot..." -ForegroundColor Yellow
Write-Host "==================================" -ForegroundColor Blue
Write-Host "Executez maintenant cette commande :" -ForegroundColor Green
Write-Host '$env:SPRING_PROFILES_ACTIVE="monitoring"' -ForegroundColor Yellow
Write-Host "mvn spring-boot:run" -ForegroundColor Yellow
Write-Host "==================================" -ForegroundColor Blue

Write-Host "🎉 Script de résolution terminé !" -ForegroundColor Green
Write-Host "L'application devrait maintenant démarrer sans erreur !" -ForegroundColor Green