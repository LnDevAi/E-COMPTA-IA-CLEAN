package com.ecomptaia.service;

import com.ecomptaia.entity.Backup;
import com.ecomptaia.repository.BackupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class BackupService {

    @Autowired
    private BackupRepository backupRepository;

    // ==================== CRÉATION DE SAUVEGARDE ====================

    /**
     * Créer une nouvelle sauvegarde
     */
    public Backup createBackup(String backupName, Backup.BackupType backupType, 
                              Backup.StorageType storageType, Long entrepriseId, Long utilisateurId) {
        
        Backup backup = new Backup(backupName, backupType, storageType, entrepriseId, utilisateurId);
        backup.setVersion("1.0");
        backup.setMetadata("{\"createdBy\":\"system\",\"description\":\"Sauvegarde automatique\"}");
        
        return backupRepository.save(backup);
    }

    /**
     * Démarrer une sauvegarde automatique
     */
    @Async
    public CompletableFuture<Backup> startAutomaticBackup(Long entrepriseId, Long utilisateurId) {
        try {
            String backupName = "Sauvegarde_auto_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            
            Backup backup = createBackup(backupName, Backup.BackupType.COMPLETE, 
                                       Backup.StorageType.LOCAL, entrepriseId, utilisateurId);
            
            backup.setStatus(Backup.BackupStatus.IN_PROGRESS);
            backup = backupRepository.save(backup);
            
            // Simuler le processus de sauvegarde
            Thread.sleep(2000); // Simulation du temps de sauvegarde
            
            // Finaliser la sauvegarde
            backup = finalizeBackup(backup.getId());
            
            return CompletableFuture.completedFuture(backup);
            
        } catch (Exception e) {
            Backup failedBackup = new Backup("Sauvegarde_échouée", Backup.BackupType.COMPLETE, 
                                           Backup.StorageType.LOCAL, entrepriseId, utilisateurId);
            failedBackup.setStatus(Backup.BackupStatus.FAILED);
            failedBackup.setErrorMessage(e.getMessage());
            return CompletableFuture.completedFuture(backupRepository.save(failedBackup));
        }
    }

    /**
     * Finaliser une sauvegarde
     */
    public Backup finalizeBackup(Long backupId) {
        Backup backup = backupRepository.findById(backupId)
                .orElseThrow(() -> new RuntimeException("Sauvegarde non trouvée"));
        
        backup.setStatus(Backup.BackupStatus.COMPLETED);
        backup.setCompletionDate(LocalDateTime.now());
        backup.setDurationSeconds(calculateDuration(backup.getBackupDate(), backup.getCompletionDate()));
        backup.setFilePath("/backups/" + backup.getBackupName() + ".zip");
        backup.setFileSize(1024L * 1024L); // 1MB simulé
        backup.setCompressionRatio(0.75);
        backup.setChecksum(generateChecksum(backup.getBackupName()));
        
        return backupRepository.save(backup);
    }

    // ==================== RECHERCHE ET RÉCUPÉRATION ====================

    /**
     * Obtenir toutes les sauvegardes d'une entreprise
     */
    public List<Backup> getBackupsByEntreprise(Long entrepriseId) {
        return backupRepository.findByEntrepriseIdOrderByBackupDateDesc(entrepriseId);
    }

    /**
     * Obtenir les sauvegardes par statut
     */
    public List<Backup> getBackupsByStatus(Backup.BackupStatus status) {
        return backupRepository.findByStatusOrderByBackupDateDesc(status);
    }

    /**
     * Obtenir les sauvegardes par type
     */
    public List<Backup> getBackupsByType(Backup.BackupType backupType) {
        return backupRepository.findByBackupTypeOrderByBackupDateDesc(backupType);
    }

    /**
     * Obtenir la dernière sauvegarde complétée
     */
    public Backup getLastCompletedBackup(Long entrepriseId) {
        return backupRepository.findLastCompletedBackupByEntreprise(entrepriseId);
    }

    /**
     * Recherche avancée de sauvegardes
     */
    public List<Backup> searchBackups(Long entrepriseId, Backup.BackupStatus status, 
                                     Backup.BackupType backupType, Backup.StorageType storageType,
                                     Boolean encryptionEnabled, LocalDateTime startDate, LocalDateTime endDate) {
        return backupRepository.findBackupsWithCriteria(entrepriseId, status, backupType, 
                                                       storageType, encryptionEnabled, startDate, endDate);
    }

    // ==================== RESTAURATION ====================

    /**
     * Démarrer une restauration
     */
    @Async
    public CompletableFuture<Backup> startRestoration(Long backupId) {
        try {
            Backup backup = backupRepository.findById(backupId)
                    .orElseThrow(() -> new RuntimeException("Sauvegarde non trouvée"));
            
            backup.setStatus(Backup.BackupStatus.RESTORING);
            backup = backupRepository.save(backup);
            
            // Simuler le processus de restauration
            Thread.sleep(3000); // Simulation du temps de restauration
            
            // Finaliser la restauration
            backup.setStatus(Backup.BackupStatus.COMPLETED);
            backup.setCompletionDate(LocalDateTime.now());
            backup.setDurationSeconds(calculateDuration(backup.getBackupDate(), backup.getCompletionDate()));
            
            return CompletableFuture.completedFuture(backupRepository.save(backup));
            
        } catch (Exception e) {
            Backup failedBackup = backupRepository.findById(backupId).orElse(new Backup());
            failedBackup.setStatus(Backup.BackupStatus.FAILED);
            failedBackup.setErrorMessage("Erreur de restauration: " + e.getMessage());
            return CompletableFuture.completedFuture(backupRepository.save(failedBackup));
        }
    }

    // ==================== SUPPRESSION ====================

    /**
     * Supprimer une sauvegarde
     */
    public void deleteBackup(Long backupId) {
        Backup backup = backupRepository.findById(backupId)
                .orElseThrow(() -> new RuntimeException("Sauvegarde non trouvée"));
        
        // Supprimer le fichier physique si il existe
        if (backup.getFilePath() != null) {
            try {
                Path filePath = Paths.get(backup.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log l'erreur mais continuer
                System.err.println("Erreur lors de la suppression du fichier: " + e.getMessage());
            }
        }
        
        backupRepository.deleteById(backupId);
    }

    /**
     * Nettoyer les anciennes sauvegardes
     */
    public void cleanupOldBackups(Long entrepriseId, int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<Backup> oldBackups = backupRepository.findByBackupDateBetweenAndEntrepriseIdOrderByBackupDateDesc(
                LocalDateTime.MIN, cutoffDate, entrepriseId);
        
        for (Backup backup : oldBackups) {
            deleteBackup(backup.getId());
        }
    }

    // ==================== STATISTIQUES ====================

    /**
     * Obtenir les statistiques de sauvegarde
     */
    public Map<String, Object> getBackupStatistics(Long entrepriseId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Comptages
        stats.put("totalBackups", backupRepository.countByEntrepriseId(entrepriseId));
        stats.put("completedBackups", backupRepository.countByEntrepriseIdAndStatus(entrepriseId, Backup.BackupStatus.COMPLETED));
        stats.put("failedBackups", backupRepository.countByEntrepriseIdAndStatus(entrepriseId, Backup.BackupStatus.FAILED));
        stats.put("inProgressBackups", backupRepository.countByEntrepriseIdAndStatus(entrepriseId, Backup.BackupStatus.IN_PROGRESS));
        
        // Moyennes
        stats.put("averageFileSize", backupRepository.getAverageFileSizeByEntreprise(entrepriseId));
        stats.put("averageDuration", backupRepository.getAverageDurationByEntreprise(entrepriseId));
        stats.put("averageCompressionRatio", backupRepository.getAverageCompressionRatioByEntreprise(entrepriseId));
        
        // Dernières sauvegardes
        stats.put("lastCompletedBackup", backupRepository.findLastCompletedBackupByEntreprise(entrepriseId));
        stats.put("recentBackups", backupRepository.findLastCompletedBackupsByEntreprise(entrepriseId));
        
        return stats;
    }

    /**
     * Obtenir les métriques de performance
     */
    public Map<String, Object> getPerformanceMetrics(Long entrepriseId) {
        Map<String, Object> metrics = new HashMap<>();
        
        List<Backup> recentBackups = backupRepository.findByEntrepriseIdOrderByBackupDateDesc(entrepriseId);
        
        if (!recentBackups.isEmpty()) {
            // Taux de succès
            long successfulBackups = recentBackups.stream()
                    .filter(b -> b.getStatus() == Backup.BackupStatus.COMPLETED)
                    .count();
            double successRate = (double) successfulBackups / recentBackups.size() * 100;
            metrics.put("successRate", successRate);
            
            // Temps moyen de sauvegarde
            double avgDuration = recentBackups.stream()
                    .filter(b -> b.getDurationSeconds() != null)
                    .mapToLong(Backup::getDurationSeconds)
                    .average()
                    .orElse(0.0);
            metrics.put("averageDuration", avgDuration);
            
            // Taille moyenne des fichiers
            double avgFileSize = recentBackups.stream()
                    .filter(b -> b.getFileSize() != null)
                    .mapToLong(Backup::getFileSize)
                    .average()
                    .orElse(0.0);
            metrics.put("averageFileSize", avgFileSize);
        }
        
        return metrics;
    }

    // ==================== UTILITAIRES ====================

    /**
     * Calculer la durée entre deux dates
     */
    private Long calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0L;
        return java.time.Duration.between(start, end).getSeconds();
    }

    /**
     * Générer un checksum pour la sauvegarde
     */
    private String generateChecksum(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return "checksum_error";
        }
    }

    /**
     * Créer un fichier de sauvegarde simulé
     */
    public void createBackupFile(String backupName, String content) throws IOException {
        String backupDir = "backups";
        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String fileName = backupDir + "/" + backupName + ".zip";
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName))) {
            ZipEntry entry = new ZipEntry("backup_data.txt");
            zos.putNextEntry(entry);
            zos.write(content.getBytes());
            zos.closeEntry();
        }
    }

    /**
     * Vérifier l'intégrité d'une sauvegarde
     */
    public boolean verifyBackupIntegrity(Long backupId) {
        Backup backup = backupRepository.findById(backupId)
                .orElseThrow(() -> new RuntimeException("Sauvegarde non trouvée"));
        
        if (backup.getFilePath() == null) return false;
        
        File backupFile = new File(backup.getFilePath());
        if (!backupFile.exists()) return false;
        
        // Vérifier la taille du fichier
        if (backup.getFileSize() != null && backupFile.length() != backup.getFileSize()) {
            return false;
        }
        
        // Vérifier le checksum si disponible
        if (backup.getChecksum() != null) {
            String calculatedChecksum = generateChecksum(backup.getBackupName());
            return calculatedChecksum.equals(backup.getChecksum());
        }
        
        return true;
    }

    /**
     * Obtenir les données de test
     */
    public Map<String, Object> getTestData() {
        Map<String, Object> testData = new HashMap<>();
        
        // Créer quelques sauvegardes de test
        Backup backup1 = createBackup("Test_Backup_1", Backup.BackupType.COMPLETE, 
                                     Backup.StorageType.LOCAL, 1L, 1L);
        backup1.setStatus(Backup.BackupStatus.COMPLETED);
        backup1.setFileSize(1024L * 1024L * 5); // 5MB
        backup1.setDurationSeconds(120L);
        backup1.setCompressionRatio(0.8);
        backupRepository.save(backup1);
        
        Backup backup2 = createBackup("Test_Backup_2", Backup.BackupType.INCREMENTAL, 
                                     Backup.StorageType.CLOUD, 1L, 1L);
        backup2.setStatus(Backup.BackupStatus.COMPLETED);
        backup2.setFileSize(1024L * 1024L * 2); // 2MB
        backup2.setDurationSeconds(60L);
        backup2.setCompressionRatio(0.7);
        backupRepository.save(backup2);
        
        testData.put("message", "Données de test créées avec succès");
        testData.put("backupsCreated", 2);
        testData.put("backup1", backup1);
        testData.put("backup2", backup2);
        
        return testData;
    }
}







