# Script PowerShell pour pousser le projet E-COMPTA-IA vers GitHub
# Ce script remplace complètement le contenu du repository distant

# Configuration des variables
$LOCAL_PATH = "C:\Users\HP\Documents\GitHub\E_COMPTA_IA\E_COMPTA_IA_INTERNATIONAL\E-COMPTA-IA-CLEAN"
$REMOTE_REPO = "https://github.com/LnDevAi/E-COMPTA-IA-CLEAN.git"
$BRANCH_NAME = "main"

Write-Host "🚀 Début du processus de push vers GitHub" -ForegroundColor Green
Write-Host "📁 Répertoire local: $LOCAL_PATH" -ForegroundColor Yellow
Write-Host "🌐 Repository distant: $REMOTE_REPO" -ForegroundColor Yellow

# Vérifier si le répertoire local existe
if (-not (Test-Path $LOCAL_PATH)) {
    Write-Host "❌ Erreur: Le répertoire local n'existe pas: $LOCAL_PATH" -ForegroundColor Red
    exit 1
}

# Se déplacer dans le répertoire du projet
Set-Location $LOCAL_PATH
Write-Host "📍 Positionnement dans le répertoire du projet" -ForegroundColor Cyan

# Vérifier si c'est un repository Git
if (-not (Test-Path ".git")) {
    Write-Host "🔧 Initialisation du repository Git local..." -ForegroundColor Yellow
    git init
    Write-Host "✅ Repository Git initialisé" -ForegroundColor Green
}

# Configurer l'utilisateur Git si nécessaire
try {
    $gitUser = git config --global user.name
    $gitEmail = git config --global user.email
    
    if ([string]::IsNullOrEmpty($gitUser) -or [string]::IsNullOrEmpty($gitEmail)) {
        Write-Host "⚙️ Configuration utilisateur Git requise..." -ForegroundColor Yellow
        $userName = Read-Host "Entrez votre nom d'utilisateur Git"
        $userEmail = Read-Host "Entrez votre email Git"
        
        git config --global user.name "$userName"
        git config --global user.email "$userEmail"
        Write-Host "✅ Configuration utilisateur Git mise à jour" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠️ Attention: Impossible de vérifier la configuration Git" -ForegroundColor Yellow
}

# Ajouter le remote origin (ou le mettre à jour)
Write-Host "🔗 Configuration du repository distant..." -ForegroundColor Cyan
try {
    git remote remove origin 2>$null
} catch {
    # Remote n'existe pas, pas de problème
}

git remote add origin $REMOTE_REPO
Write-Host "✅ Remote origin configuré" -ForegroundColor Green

# Créer un fichier .gitignore complet si inexistant
if (-not (Test-Path ".gitignore")) {
    Write-Host "📝 Création du fichier .gitignore..." -ForegroundColor Yellow
    @"
# Dossiers de dépendances
node_modules/
target/
.mvn/
vendor/

# Fichiers de configuration locaux
.env
.env.local
.env.development
.env.production
*.properties.local

# Fichiers de build
dist/
build/
out/
*.jar
*.war

# Fichiers IDE
.vscode/
.idea/
*.swp
*.swo
*~

# OS générés
.DS_Store
Thumbs.db

# Logs
*.log
logs/

# Cache
.cache/
.parcel-cache/

# Fichiers temporaires
*.tmp
*.temp
"@ | Out-File -FilePath ".gitignore" -Encoding UTF8
    Write-Host "✅ Fichier .gitignore créé" -ForegroundColor Green
}

# Ajouter tous les fichiers
Write-Host "📦 Ajout de tous les fichiers..." -ForegroundColor Cyan
git add .

# Vérifier s'il y a des changements à commiter
$status = git status --porcelain
if ([string]::IsNullOrEmpty($status)) {
    Write-Host "ℹ️ Aucun changement détecté" -ForegroundColor Yellow
} else {
    Write-Host "📋 Fichiers détectés pour commit:" -ForegroundColor Yellow
    git status --short
}

# Créer un commit avec timestamp
$commitMessage = "🚀 Déploiement complet E-COMPTA-IA - $(Get-Date -Format 'dd/MM/yyyy HH:mm:ss')"
Write-Host "💾 Création du commit: $commitMessage" -ForegroundColor Cyan
git commit -m "$commitMessage"

# Push avec force pour remplacer complètement le contenu distant
Write-Host "🚀 Push vers GitHub avec remplacement complet..." -ForegroundColor Cyan
Write-Host "⚠️ ATTENTION: Cette opération va remplacer tout le contenu du repository distant!" -ForegroundColor Red

$confirmation = Read-Host "Voulez-vous continuer? (oui/non)"
if ($confirmation -eq "oui" -or $confirmation -eq "o" -or $confirmation -eq "y" -or $confirmation -eq "yes") {
    
    # Tentative de push normal d'abord
    Write-Host "📤 Tentative de push normal..." -ForegroundColor Yellow
    $pushResult = git push -u origin $BRANCH_NAME 2>&1
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "⚠️ Push normal échoué, utilisation du push forcé..." -ForegroundColor Yellow
        Write-Host "🔄 Exécution du push avec --force..." -ForegroundColor Cyan
        
        git push --force -u origin $BRANCH_NAME
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Push forcé réussi!" -ForegroundColor Green
        } else {
            Write-Host "❌ Erreur lors du push forcé" -ForegroundColor Red
            Write-Host "Détails de l'erreur:" -ForegroundColor Yellow
            git push --force -u origin $BRANCH_NAME
            exit 1
        }
    } else {
        Write-Host "✅ Push normal réussi!" -ForegroundColor Green
    }
    
    # Affichage des informations finales
    Write-Host "`n🎉 DÉPLOIEMENT TERMINÉ AVEC SUCCÈS!" -ForegroundColor Green
    Write-Host "🌐 Votre projet est maintenant disponible sur: $REMOTE_REPO" -ForegroundColor Cyan
    Write-Host "📊 Statistiques du repository:" -ForegroundColor Yellow
    
    # Statistiques
    $fileCount = (Get-ChildItem -Recurse -File | Where-Object { $_.FullName -notlike "*\.git\*" }).Count
    $folderCount = (Get-ChildItem -Recurse -Directory | Where-Object { $_.FullName -notlike "*\.git\*" }).Count
    
    Write-Host "   Dossiers: $folderCount" -ForegroundColor White
    Write-Host "   Fichiers: $fileCount" -ForegroundColor White
    Write-Host "   Branche: $BRANCH_NAME" -ForegroundColor White
    
    # Afficher les derniers commits
    Write-Host "`nDerniers commits:" -ForegroundColor Yellow
    git log --oneline -5
    
} else {
    Write-Host "Operation annulee par l'utilisateur" -ForegroundColor Red
    exit 0
}

# Verification finale
Write-Host "`nVerification finale..." -ForegroundColor Cyan
$remoteInfo = git remote -v
Write-Host "Remote configure:" -ForegroundColor Yellow
Write-Host $remoteInfo -ForegroundColor White

Write-Host "`nScript termine avec succes!" -ForegroundColor Green
Write-Host "Conseil: Vous pouvez maintenant cloner votre projet depuis: $REMOTE_REPO" -ForegroundColor Cyan

# Pause pour voir les resultats
Write-Host "`nAppuyez sur une touche pour fermer..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")