package com.ecomptaia.repository;

import com.ecomptaia.entity.AutomatedTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AutomatedTaskRepository extends JpaRepository<AutomatedTask, Long> {
    
    List<AutomatedTask> findByStatusAndIsActiveTrue(String status);
    
    List<AutomatedTask> findByTaskTypeAndIsActiveTrue(String taskType);
    
    List<AutomatedTask> findByCountryCodeAndIsActiveTrue(String countryCode);
    
    List<AutomatedTask> findByCountryCodeAndStatusAndIsActiveTrue(String countryCode, String status);
    
    List<AutomatedTask> findByAccountingStandardAndIsActiveTrue(String accountingStandard);
    
    @Query("SELECT at FROM AutomatedTask at WHERE at.status = 'PENDING' AND at.isActive = true ORDER BY at.priority DESC, at.createdAt ASC")
    List<AutomatedTask> findPendingTasksOrderedByPriority();
    
    @Query("SELECT at FROM AutomatedTask at WHERE at.status = 'COMPLETED' AND at.confidence >= :minConfidence AND at.isActive = true")
    List<AutomatedTask> findCompletedTasksWithMinConfidence(@Param("minConfidence") Double minConfidence);
    
    @Query("SELECT at FROM AutomatedTask at WHERE at.createdAt >= :startDate AND at.createdAt <= :endDate AND at.isActive = true")
    List<AutomatedTask> findTasksByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT at FROM AutomatedTask at WHERE at.status = 'FAILED' AND at.isActive = true ORDER BY at.createdAt DESC")
    List<AutomatedTask> findFailedTasks();
    
    @Query("SELECT at FROM AutomatedTask at WHERE at.processingTime > :maxTime AND at.isActive = true")
    List<AutomatedTask> findSlowTasks(@Param("maxTime") Integer maxTime);
    
    @Query("SELECT DISTINCT at.taskType FROM AutomatedTask at WHERE at.isActive = true")
    List<String> findAllDistinctTaskTypes();
    
    @Query("SELECT at FROM AutomatedTask at WHERE at.aiModel = :aiModel AND at.isActive = true")
    List<AutomatedTask> findByAiModel(@Param("aiModel") String aiModel);
    
    @Query("SELECT COUNT(at) FROM AutomatedTask at WHERE at.status = :status AND at.isActive = true")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT AVG(at.processingTime) FROM AutomatedTask at WHERE at.status = 'COMPLETED' AND at.processingTime IS NOT NULL AND at.isActive = true")
    Double getAverageProcessingTime();
    
    @Query("SELECT AVG(at.confidence) FROM AutomatedTask at WHERE at.status = 'COMPLETED' AND at.confidence IS NOT NULL AND at.isActive = true")
    Double getAverageConfidence();
}








