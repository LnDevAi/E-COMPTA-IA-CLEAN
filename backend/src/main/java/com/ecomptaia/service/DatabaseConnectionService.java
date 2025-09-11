ackage com.ecomptaia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service pour la gestion des connexions à la base de données
 */
@Service
@Transactional
public class DatabaseConnectionService {
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * Teste la connexion à la base de données
     */
    public Map<String, Object> testConnection() {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            result.put("success", true);
            result.put("databaseProductName", metaData.getDatabaseProductName());
            result.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            result.put("driverName", metaData.getDriverName());
            result.put("driverVersion", metaData.getDriverVersion());
            result.put("url", metaData.getURL());
            result.put("username", metaData.getUserName());
            result.put("connectionValid", connection.isValid(5));
            
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Obtient la liste de toutes les tables de la base de données
     */
    public List<Map<String, Object>> getAllTables() {
        List<Map<String, Object>> tables = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();
            
            try (ResultSet rs = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    Map<String, Object> table = new HashMap<>();
                    table.put("tableName", rs.getString("TABLE_NAME"));
                    table.put("tableType", rs.getString("TABLE_TYPE"));
                    table.put("remarks", rs.getString("REMARKS"));
                    tables.add(table);
                }
            }
            
        } catch (SQLException e) {
            // Log l'erreur
            System.err.println("Erreur lors de la récupération des tables: " + e.getMessage());
        }
        
        return tables;
    }
    
    /**
     * Obtient les informations sur une table spécifique
     */
    public Map<String, Object> getTableInfo(String tableName) {
        Map<String, Object> tableInfo = new HashMap<>();
        List<Map<String, Object>> columns = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();
            
            // Informations sur la table
            try (ResultSet rs = metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"})) {
                if (rs.next()) {
                    tableInfo.put("tableName", rs.getString("TABLE_NAME"));
                    tableInfo.put("tableType", rs.getString("TABLE_TYPE"));
                    tableInfo.put("remarks", rs.getString("REMARKS"));
                }
            }
            
            // Informations sur les colonnes
            try (ResultSet rs = metaData.getColumns(catalog, schema, tableName, "%")) {
                while (rs.next()) {
                    Map<String, Object> column = new HashMap<>();
                    column.put("columnName", rs.getString("COLUMN_NAME"));
                    column.put("dataType", rs.getInt("DATA_TYPE"));
                    column.put("typeName", rs.getString("TYPE_NAME"));
                    column.put("columnSize", rs.getInt("COLUMN_SIZE"));
                    column.put("nullable", rs.getInt("NULLABLE"));
                    column.put("columnDef", rs.getString("COLUMN_DEF"));
                    column.put("remarks", rs.getString("REMARKS"));
                    columns.add(column);
                }
            }
            
            // Informations sur les clés primaires
            List<String> primaryKeys = new ArrayList<>();
            try (ResultSet rs = metaData.getPrimaryKeys(catalog, schema, tableName)) {
                while (rs.next()) {
                    primaryKeys.add(rs.getString("COLUMN_NAME"));
                }
            }
            
            // Informations sur les clés étrangères
            List<Map<String, Object>> foreignKeys = new ArrayList<>();
            try (ResultSet rs = metaData.getImportedKeys(catalog, schema, tableName)) {
                while (rs.next()) {
                    Map<String, Object> fk = new HashMap<>();
                    fk.put("columnName", rs.getString("FKCOLUMN_NAME"));
                    fk.put("referencedTable", rs.getString("PKTABLE_NAME"));
                    fk.put("referencedColumn", rs.getString("PKCOLUMN_NAME"));
                    foreignKeys.add(fk);
                }
            }
            
            // Informations sur les index
            List<Map<String, Object>> indexes = new ArrayList<>();
            try (ResultSet rs = metaData.getIndexInfo(catalog, schema, tableName, false, false)) {
                while (rs.next()) {
                    Map<String, Object> index = new HashMap<>();
                    index.put("indexName", rs.getString("INDEX_NAME"));
                    index.put("columnName", rs.getString("COLUMN_NAME"));
                    index.put("unique", !rs.getBoolean("NON_UNIQUE"));
                    indexes.add(index);
                }
            }
            
            tableInfo.put("columns", columns);
            tableInfo.put("primaryKeys", primaryKeys);
            tableInfo.put("foreignKeys", foreignKeys);
            tableInfo.put("indexes", indexes);
            
        } catch (SQLException e) {
            tableInfo.put("error", e.getMessage());
        }
        
        return tableInfo;
    }
    
    /**
     * Obtient les statistiques de la base de données
     */
    public Map<String, Object> getDatabaseStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            stats.put("databaseProductName", metaData.getDatabaseProductName());
            stats.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            stats.put("driverName", metaData.getDriverName());
            stats.put("driverVersion", metaData.getDriverVersion());
            stats.put("maxConnections", metaData.getMaxConnections());
            stats.put("maxColumnsInTable", metaData.getMaxColumnsInTable());
            stats.put("maxColumnsInIndex", metaData.getMaxColumnsInIndex());
            stats.put("maxColumnsInSelect", metaData.getMaxColumnsInSelect());
            stats.put("maxTableNameLength", metaData.getMaxTableNameLength());
            stats.put("maxColumnNameLength", metaData.getMaxColumnNameLength());
            stats.put("supportsTransactions", metaData.supportsTransactions());
            stats.put("supportsBatchUpdates", metaData.supportsBatchUpdates());
            stats.put("supportsStoredProcedures", metaData.supportsStoredProcedures());
            stats.put("supportsMultipleTransactions", metaData.supportsMultipleTransactions());
            stats.put("supportsMultipleResultSets", metaData.supportsMultipleResultSets());
            
            // Compter le nombre de tables
            List<Map<String, Object>> tables = getAllTables();
            stats.put("tableCount", tables.size());
            
        } catch (SQLException e) {
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Vérifie l'intégrité de la base de données
     */
    public Map<String, Object> checkDatabaseIntegrity() {
        Map<String, Object> result = new HashMap<>();
        List<String> issues = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();
            
            // Vérifier les tables manquantes
            List<String> expectedTables = List.of(
                "users", "roles", "companies", "accounts", "journal_entries", "account_entries",
                "employees", "payrolls", "leaves", "third_parties", "inventory", "assets",
                "financial_reports", "subscriptions", "notifications", "audit_logs",
                "data_protection", "consent_records", "data_processing_activities",
                "locale_settings", "cost_centers", "projects", "ged_documents",
                "document_workflows", "document_approvals", "bank_accounts", "reconciliations"
            );
            
            List<String> existingTables = new ArrayList<>();
            try (ResultSet rs = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    existingTables.add(rs.getString("TABLE_NAME"));
                }
            }
            
            for (String expectedTable : expectedTables) {
                if (!existingTables.contains(expectedTable)) {
                    issues.add("Table manquante: " + expectedTable);
                }
            }
            
            // Vérifier les tables orphelines
            for (String existingTable : existingTables) {
                if (!expectedTables.contains(existingTable) && !existingTable.startsWith("flyway_")) {
                    issues.add("Table orpheline: " + existingTable);
                }
            }
            
            result.put("success", issues.isEmpty());
            result.put("issues", issues);
            result.put("totalTables", existingTables.size());
            result.put("expectedTables", expectedTables.size());
            
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Obtient les informations sur les contraintes de la base de données
     */
    public Map<String, Object> getConstraintsInfo() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> constraints = new ArrayList<>();
        
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();
            
            // Récupérer toutes les tables
            List<String> tables = new ArrayList<>();
            try (ResultSet rs = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"})) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                }
            }
            
            // Pour chaque table, récupérer les contraintes
            for (String tableName : tables) {
                // Clés primaires
                try (ResultSet rs = metaData.getPrimaryKeys(catalog, schema, tableName)) {
                    while (rs.next()) {
                        Map<String, Object> constraint = new HashMap<>();
                        constraint.put("tableName", tableName);
                        constraint.put("constraintName", rs.getString("PK_NAME"));
                        constraint.put("columnName", rs.getString("COLUMN_NAME"));
                        constraint.put("constraintType", "PRIMARY_KEY");
                        constraints.add(constraint);
                    }
                }
                
                // Clés étrangères
                try (ResultSet rs = metaData.getImportedKeys(catalog, schema, tableName)) {
                    while (rs.next()) {
                        Map<String, Object> constraint = new HashMap<>();
                        constraint.put("tableName", tableName);
                        constraint.put("constraintName", rs.getString("FK_NAME"));
                        constraint.put("columnName", rs.getString("FKCOLUMN_NAME"));
                        constraint.put("referencedTable", rs.getString("PKTABLE_NAME"));
                        constraint.put("referencedColumn", rs.getString("PKCOLUMN_NAME"));
                        constraint.put("constraintType", "FOREIGN_KEY");
                        constraints.add(constraint);
                    }
                }
            }
            
            result.put("constraints", constraints);
            result.put("totalConstraints", constraints.size());
            
        } catch (SQLException e) {
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}



