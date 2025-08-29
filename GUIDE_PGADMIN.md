# Guide Configuration PgAdmin - E-COMPTA-IA

## 🔐 Connexion à PgAdmin

1. **URL** : http://localhost:5050
2. **Identifiants** :
   - Email : `admin@ecomptaia.com`
   - Password : `admin`

## 🗄️ Configuration Serveur PostgreSQL

### Étapes détaillées :

1. **Dans PgAdmin** :
   - Clic droit sur "Servers" (panneau gauche)
   - Sélectionner "Register" → "Server..."

2. **Onglet General** :
   - Name : `E-COMPTA-IA Database` (ou nom de votre choix)

3. **Onglet Connection** :
   ```
   Host name/address : postgres
   Port : 5432
   Maintenance database : ecomptaia_db
   Username : ecomptaia_user
   Password : ecomptaia_password
   ```
   - ✅ Cocher "Save password"

4. **Cliquer "Save"**

## ⚠️ Important

- **Host** : Utiliser `postgres` (nom du service Docker)
- **PAS** `localhost` car nous sommes dans un environnement Docker
- Les conteneurs communiquent par leurs noms de service

## ✅ Vérification

Après configuration, vous devriez voir :
- Serveur connecté dans la liste
- Base de données `ecomptaia_db`
- Tables créées par l'application Spring Boot
- Possibilité d'exécuter des requêtes SQL

## 🔧 En cas de problème

Si la connexion échoue :
1. Vérifier que PostgreSQL est démarré : `docker-compose -f docker/docker-compose.yml ps`
2. Vérifier les logs : `docker-compose -f docker/docker-compose.yml logs postgres`
3. Tester la connexion : `docker exec ecomptaia-postgres pg_isready -U ecomptaia_user -d ecomptaia_db`
