package com.ecomptaia.repository;

import com.ecomptaia.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour la gestion de la paie
 */
@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    
    /**
     * Trouver les paies d'un employé
     */
    List<Payroll> findByEmployeeIdOrderByYearDescMonthDesc(Long employeeId);
    
    /**
     * Trouver les paies d'une période
     */
    List<Payroll> findByMonthAndYear(Integer month, Integer year);
    
    /**
     * Trouver les paies d'une entreprise
     */
    List<Payroll> findByCompanyIdOrderByYearDescMonthDesc(Long companyId);
    
    /**
     * Trouver les paies par statut
     */
    List<Payroll> findByStatusOrderByCreatedAtDesc(String status);
}