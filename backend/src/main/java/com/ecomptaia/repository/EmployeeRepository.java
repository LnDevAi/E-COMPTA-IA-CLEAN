package com.ecomptaia.repository;

import com.ecomptaia.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion des employés
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    /**
     * Trouver les employés actifs d'une entreprise
     */
    List<Employee> findByCompanyIdAndIsActiveTrue(Long companyId);
    
    /**
     * Trouver les employés par email
     */
    Employee findByEmailAndIsActiveTrue(String email);
    
    /**
     * Trouver les employés par position
     */
    List<Employee> findByPositionAndCompanyIdAndIsActiveTrue(String position, Long companyId);
    
    /**
     * Compter les employés d'une entreprise
     */
    long countByCompanyIdAndIsActiveTrue(Long companyId);
}