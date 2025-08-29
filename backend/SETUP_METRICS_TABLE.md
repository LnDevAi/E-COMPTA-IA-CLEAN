# Guide d'installation de la table METRICS

## Problème
La table `metrics` n'est pas créée automatiquement par Hibernate. Ce guide explique comment la créer manuellement.

## Solution 1 : Exécution du script SQL (RECOMMANDÉ)

### Étape 1 : Accéder à votre base de données
```bash
# Pour MySQL/MariaDB
mysql -u username -p database_name

# Pour H2 (si utilisé en développement)
# Ouvrir l'interface web H2 sur http://localhost:8080/h2-console
```

### Étape 2 : Exécuter le script SQL
```sql
-- Copier et coller le contenu du fichier sql/create_metrics_table.sql
-- Ou exécuter directement :

CREATE TABLE IF NOT EXISTS metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metric_name VARCHAR(255) NOT NULL,
    metric_type VARCHAR(50) NOT NULL,
    value DOUBLE NOT NULL,
    unit VARCHAR(50),
    category VARCHAR(50) NOT NULL,
    source VARCHAR(255) NOT NULL,
    timestamp DATETIME NOT NULL,
    description TEXT,
    tags TEXT,
    threshold_warning DOUBLE,
    threshold_critical DOUBLE,
    status VARCHAR(50) NOT NULL,
    entreprise_id BIGINT,
    utilisateur_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Étape 3 : Vérifier la création
```sql
-- Vérifier que la table existe
SHOW TABLES LIKE 'metrics';

-- Vérifier la structure
DESCRIBE metrics;
```

## Solution 2 : Configuration Hibernate (ALTERNATIVE)

### Modifier application.properties
```properties
# Forcer la création des tables
spring.jpa.hibernate.ddl-auto=create-drop

# OU pour créer seulement les tables manquantes
spring.jpa.hibernate.ddl-auto=update

# Activer les logs SQL pour debug
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## Solution 3 : Vérification via API

### Étape 1 : Redémarrer l'application
```bash
# Arrêter l'application (CTRL+C)
# Redémarrer
mvn spring-boot:run
```

### Étape 2 : Tester l'endpoint de vérification
```bash
# Vérifier si la table existe
curl -X GET "http://localhost:8080/api/monitoring/check-table"

# Ou utiliser PowerShell
Invoke-WebRequest -Uri "http://localhost:8080/api/monitoring/check-table" -Method GET
```

### Étape 3 : Tester la création de métriques
```bash
# Créer une métrique de test
curl -X POST "http://localhost:8080/api/monitoring/metrics/create" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "metricName=test.metric&metricType=GAUGE&value=100.0&category=SYSTEM&source=test&entrepriseId=1&utilisateurId=1"
```

## Vérification finale

Une fois la table créée, tous ces endpoints devraient fonctionner :

✅ `GET /api/monitoring/check-table` - Vérification de la table  
✅ `GET /api/monitoring/diagnostic` - Test de base de données  
✅ `POST /api/monitoring/metrics/create` - Création de métriques  
✅ `GET /api/monitoring/metrics/by-entreprise?entrepriseId=1` - Récupération de métriques  

## Dépannage

### Erreur : "Table metrics not found"
- Vérifiez que le script SQL a été exécuté correctement
- Vérifiez les permissions de l'utilisateur de base de données
- Vérifiez que vous êtes connecté à la bonne base de données

### Erreur : "Access denied"
- Vérifiez les permissions de l'utilisateur MySQL
- Vérifiez que l'utilisateur a les droits CREATE, INSERT, SELECT

### Erreur : "Connection refused"
- Vérifiez que la base de données est démarrée
- Vérifiez les paramètres de connexion dans application.properties




