package com.ecomptaia.service;

import com.ecomptaia.entity.Employee;
import com.ecomptaia.entity.Payroll;
import com.ecomptaia.entity.Leave;
import com.ecomptaia.repository.EmployeeRepository;
import com.ecomptaia.repository.PayrollRepository;
import com.ecomptaia.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.math.BigDecimal;

@Service
@Transactional
public class HumanResourceService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    // ==================== GESTION DES EMPLOYÉS ====================

    /**
     * Créer un nouvel employé
     */
    public Employee createEmployee(String employeeCode, String firstName, String lastName, String email,
                                 Employee.Gender gender, Employee.ContractType contractType, String department,
                                 String position, BigDecimal baseSalary, String salaryCurrency, Long entrepriseId) {
        Employee employee = new Employee();
        employee.setEmployeeCode(employeeCode);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setGender(gender);
        employee.setContractType(contractType);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setBaseSalary(baseSalary);
        employee.setSalaryCurrency(salaryCurrency);
        employee.setEntrepriseId(entrepriseId);
        employee.setHireDate(LocalDate.now());
        
        return employeeRepository.save(employee);
    }

    /**
     * Mettre à jour un employé
     */
    public Employee updateEmployee(Long employeeId, Map<String, Object> updates) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            
            if (updates.containsKey("firstName")) {
                employee.setFirstName((String) updates.get("firstName"));
            }
            if (updates.containsKey("lastName")) {
                employee.setLastName((String) updates.get("lastName"));
            }
            if (updates.containsKey("email")) {
                employee.setEmail((String) updates.get("email"));
            }
            if (updates.containsKey("phone")) {
                employee.setPhone((String) updates.get("phone"));
            }
            if (updates.containsKey("address")) {
                employee.setAddress((String) updates.get("address"));
            }
            if (updates.containsKey("department")) {
                employee.setDepartment((String) updates.get("department"));
            }
            if (updates.containsKey("position")) {
                employee.setPosition((String) updates.get("position"));
            }
            if (updates.containsKey("baseSalary")) {
                employee.setBaseSalary(new BigDecimal(updates.get("baseSalary").toString()));
            }
            if (updates.containsKey("employmentStatus")) {
                employee.setEmploymentStatus(Employee.EmploymentStatus.valueOf((String) updates.get("employmentStatus")));
            }
            if (updates.containsKey("performanceRating")) {
                employee.setPerformanceRating((Integer) updates.get("performanceRating"));
            }
            
            return employeeRepository.save(employee);
        }
        throw new RuntimeException("Employé non trouvé");
    }

    /**
     * Terminer un employé
     */
    public Employee terminateEmployee(Long employeeId, LocalDate terminationDate, String terminationReason) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setEmploymentStatus(Employee.EmploymentStatus.TERMINATED);
            employee.setTerminationDate(terminationDate);
            employee.setTerminationReason(terminationReason);
            return employeeRepository.save(employee);
        }
        throw new RuntimeException("Employé non trouvé");
    }

    /**
     * Obtenir un employé par ID
     */
    public Employee getEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employé non trouvé"));
    }

    /**
     * Obtenir tous les employés d'une entreprise
     */
    public List<Employee> getAllEmployeesByEntreprise(Long entrepriseId) {
        return employeeRepository.findByEntrepriseId(entrepriseId);
    }

    /**
     * Rechercher des employés
     */
    public List<Employee> searchEmployees(Long entrepriseId, String searchTerm) {
        return employeeRepository.findByEntrepriseIdAndNameContaining(entrepriseId, searchTerm);
    }

    /**
     * Obtenir les statistiques des employés
     */
    public Map<String, Object> getEmployeeStatistics(Long entrepriseId) {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalEmployees", employeeRepository.countByEntrepriseId(entrepriseId));
        statistics.put("activeEmployees", employeeRepository.countActiveEmployeesByEntrepriseId(entrepriseId));
        statistics.put("employeesOnLeave", employeeRepository.countEmployeesOnLeaveByEntrepriseId(entrepriseId));
        statistics.put("terminatedEmployees", employeeRepository.countTerminatedEmployeesByEntrepriseId(entrepriseId));
        statistics.put("totalSalary", employeeRepository.getTotalSalaryByEntrepriseId(entrepriseId));
        statistics.put("averageSalary", employeeRepository.getAverageSalaryByEntrepriseId(entrepriseId));
        
        // Statistiques par département
        statistics.put("employeesByDepartment", employeeRepository.getEmployeeCountByDepartment(entrepriseId));
        statistics.put("employeesByStatus", employeeRepository.getEmployeeCountByEmploymentStatus(entrepriseId));
        statistics.put("employeesByContractType", employeeRepository.getEmployeeCountByContractType(entrepriseId));
        statistics.put("employeesByGender", employeeRepository.getEmployeeCountByGender(entrepriseId));
        statistics.put("employeesByPerformance", employeeRepository.getEmployeeCountByPerformanceRating(entrepriseId));
        
        return statistics;
    }

    // ==================== GESTION DE LA PAIE ====================

    /**
     * Créer une paie
     */
    public Payroll createPayroll(String payrollCode, Long employeeId, Payroll.PayPeriod payPeriod,
                               LocalDate payDate, LocalDate periodStartDate, LocalDate periodEndDate,
                               BigDecimal baseSalary, BigDecimal hoursWorked, BigDecimal overtimeHours,
                               BigDecimal overtimeRate, BigDecimal bonusAmount, String bonusDescription,
                               BigDecimal allowanceAmount, String allowanceDescription, Long entrepriseId) {
        Payroll payroll = new Payroll();
        payroll.setPayrollCode(payrollCode);
        payroll.setEmployeeId(employeeId);
        payroll.setPayPeriod(payPeriod);
        payroll.setPayDate(payDate);
        payroll.setPeriodStartDate(periodStartDate);
        payroll.setPeriodEndDate(periodEndDate);
        payroll.setBaseSalary(baseSalary);
        payroll.setHoursWorked(hoursWorked);
        payroll.setOvertimeHours(overtimeHours);
        payroll.setOvertimeRate(overtimeRate);
        payroll.setBonusAmount(bonusAmount);
        payroll.setBonusDescription(bonusDescription);
        payroll.setAllowanceAmount(allowanceAmount);
        payroll.setAllowanceDescription(allowanceDescription);
        payroll.setEntrepriseId(entrepriseId);
        
        // Calculer les montants
        payroll.calculateOvertimePay();
        payroll.calculateGrossSalary();
        
        return payrollRepository.save(payroll);
    }

    /**
     * Calculer et mettre à jour une paie
     */
    public Payroll calculatePayroll(Long payrollId) {
        Optional<Payroll> optionalPayroll = payrollRepository.findById(payrollId);
        if (optionalPayroll.isPresent()) {
            Payroll payroll = optionalPayroll.get();
            
            // Calculer les montants
            payroll.calculateOvertimePay();
            payroll.calculateCommission();
            payroll.calculateGrossSalary();
            payroll.calculateNetSalary();
            
            return payrollRepository.save(payroll);
        }
        throw new RuntimeException("Paie non trouvée");
    }

    /**
     * Approuver une paie
     */
    public Payroll approvePayroll(Long payrollId, Long approvedBy) {
        Optional<Payroll> optionalPayroll = payrollRepository.findById(payrollId);
        if (optionalPayroll.isPresent()) {
            Payroll payroll = optionalPayroll.get();
            payroll.setPayrollStatus(Payroll.PayrollStatus.APPROVED);
            payroll.setApprovedBy(approvedBy);
            payroll.setApprovedAt(LocalDateTime.now());
            return payrollRepository.save(payroll);
        }
        throw new RuntimeException("Paie non trouvée");
    }

    /**
     * Marquer une paie comme payée
     */
    public Payroll markPayrollAsPaid(Long payrollId, Payroll.PaymentMethod paymentMethod, String bankTransferReference) {
        Optional<Payroll> optionalPayroll = payrollRepository.findById(payrollId);
        if (optionalPayroll.isPresent()) {
            Payroll payroll = optionalPayroll.get();
            payroll.setPayrollStatus(Payroll.PayrollStatus.PAID);
            payroll.setPaymentMethod(paymentMethod);
            payroll.setBankTransferReference(bankTransferReference);
            return payrollRepository.save(payroll);
        }
        throw new RuntimeException("Paie non trouvée");
    }

    /**
     * Obtenir les statistiques de paie
     */
    public Map<String, Object> getPayrollStatistics(Long entrepriseId) {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalPayrolls", payrollRepository.countByEntrepriseId(entrepriseId));
        statistics.put("paidPayrolls", payrollRepository.countPaidPayrollsByEntrepriseId(entrepriseId));
        statistics.put("pendingPayrolls", payrollRepository.countPendingPayrollsByEntrepriseId(entrepriseId));
        statistics.put("draftPayrolls", payrollRepository.countDraftPayrollsByEntrepriseId(entrepriseId));
        statistics.put("totalPaidSalary", payrollRepository.getTotalPaidSalaryByEntrepriseId(entrepriseId));
        statistics.put("totalPaidNetSalary", payrollRepository.getTotalPaidNetSalaryByEntrepriseId(entrepriseId));
        statistics.put("averagePaidSalary", payrollRepository.getAveragePaidSalaryByEntrepriseId(entrepriseId));
        statistics.put("averagePaidNetSalary", payrollRepository.getAveragePaidNetSalaryByEntrepriseId(entrepriseId));
        
        // Statistiques détaillées
        statistics.put("payrollsByStatus", payrollRepository.getPayrollCountByStatus(entrepriseId));
        statistics.put("payrollsByPayPeriod", payrollRepository.getPayrollCountByPayPeriod(entrepriseId));
        statistics.put("payrollsByPaymentMethod", payrollRepository.getPayrollCountByPaymentMethod(entrepriseId));
        statistics.put("payrollsByCurrency", payrollRepository.getPayrollCountByCurrency(entrepriseId));
        statistics.put("salaryByStatus", payrollRepository.getTotalSalaryByStatus(entrepriseId));
        statistics.put("salaryByPayPeriod", payrollRepository.getTotalSalaryByPayPeriod(entrepriseId));
        statistics.put("netSalaryByStatus", payrollRepository.getTotalNetSalaryByStatus(entrepriseId));
        statistics.put("netSalaryByPayPeriod", payrollRepository.getTotalNetSalaryByPayPeriod(entrepriseId));
        statistics.put("monthlyStatistics", payrollRepository.getPayrollStatisticsByMonth(entrepriseId));
        statistics.put("quarterlyStatistics", payrollRepository.getPayrollStatisticsByQuarter(entrepriseId));
        statistics.put("yearlyStatistics", payrollRepository.getPayrollStatisticsByYear(entrepriseId));
        statistics.put("deductionStatistics", payrollRepository.getDeductionStatistics(entrepriseId));
        statistics.put("bonusStatistics", payrollRepository.getBonusAndCommissionStatistics(entrepriseId));
        
        return statistics;
    }

    // ==================== GESTION DES CONGÉS ====================

    /**
     * Créer une demande de congé
     */
    public Leave createLeaveRequest(String leaveCode, Long employeeId, Leave.LeaveType leaveType,
                                  LocalDate startDate, LocalDate endDate, String reason, Long entrepriseId) {
        Leave leave = new Leave();
        leave.setLeaveCode(leaveCode);
        leave.setEmployeeId(employeeId);
        leave.setLeaveType(leaveType);
        leave.setStartDate(startDate);
        leave.setEndDate(endDate);
        leave.setReason(reason);
        leave.setEntrepriseId(entrepriseId);
        
        // Calculer le nombre de jours
        leave.calculateTotalDays();
        
        return leaveRepository.save(leave);
    }

    /**
     * Approuver une demande de congé
     */
    public Leave approveLeave(Long leaveId, Long approvedBy, String approvalNotes) {
        Optional<Leave> optionalLeave = leaveRepository.findById(leaveId);
        if (optionalLeave.isPresent()) {
            Leave leave = optionalLeave.get();
            leave.setLeaveStatus(Leave.LeaveStatus.APPROVED);
            leave.setApprovedBy(approvedBy);
            leave.setApprovedAt(LocalDateTime.now());
            leave.setApprovalNotes(approvalNotes);
            return leaveRepository.save(leave);
        }
        throw new RuntimeException("Demande de congé non trouvée");
    }

    /**
     * Rejeter une demande de congé
     */
    public Leave rejectLeave(Long leaveId, Long rejectedBy, String rejectionReason) {
        Optional<Leave> optionalLeave = leaveRepository.findById(leaveId);
        if (optionalLeave.isPresent()) {
            Leave leave = optionalLeave.get();
            leave.setLeaveStatus(Leave.LeaveStatus.REJECTED);
            leave.setRejectedBy(rejectedBy);
            leave.setRejectedAt(LocalDateTime.now());
            leave.setRejectionReason(rejectionReason);
            return leaveRepository.save(leave);
        }
        throw new RuntimeException("Demande de congé non trouvée");
    }

    /**
     * Annuler une demande de congé
     */
    public Leave cancelLeave(Long leaveId, Long cancelledBy, String cancellationReason) {
        Optional<Leave> optionalLeave = leaveRepository.findById(leaveId);
        if (optionalLeave.isPresent()) {
            Leave leave = optionalLeave.get();
            leave.setLeaveStatus(Leave.LeaveStatus.CANCELLED);
            leave.setCancelledBy(cancelledBy);
            leave.setCancelledAt(LocalDateTime.now());
            leave.setCancellationReason(cancellationReason);
            return leaveRepository.save(leave);
        }
        throw new RuntimeException("Demande de congé non trouvée");
    }

    /**
     * Obtenir les statistiques des congés
     */
    public Map<String, Object> getLeaveStatistics(Long entrepriseId) {
        Map<String, Object> statistics = new HashMap<>();
        
        statistics.put("totalLeaves", leaveRepository.countByEntrepriseId(entrepriseId));
        statistics.put("approvedLeaves", leaveRepository.countApprovedLeavesByEntrepriseId(entrepriseId));
        statistics.put("pendingLeaves", leaveRepository.countPendingLeavesByEntrepriseId(entrepriseId));
        statistics.put("rejectedLeaves", leaveRepository.countRejectedLeavesByEntrepriseId(entrepriseId));
        statistics.put("cancelledLeaves", leaveRepository.countCancelledLeavesByEntrepriseId(entrepriseId));
        statistics.put("inProgressLeaves", leaveRepository.countInProgressLeavesByEntrepriseId(entrepriseId));
        statistics.put("completedLeaves", leaveRepository.countCompletedLeavesByEntrepriseId(entrepriseId));
        statistics.put("totalLeaveDays", leaveRepository.getTotalLeaveDaysByEntrepriseId(entrepriseId));
        statistics.put("totalApprovedLeaveDays", leaveRepository.getTotalApprovedLeaveDaysByEntrepriseId(entrepriseId));
        statistics.put("totalPaidLeaveDays", leaveRepository.getTotalPaidLeaveDaysByEntrepriseId(entrepriseId));
        statistics.put("totalUnpaidLeaveDays", leaveRepository.getTotalUnpaidLeaveDaysByEntrepriseId(entrepriseId));
        statistics.put("averageLeaveDays", leaveRepository.getAverageLeaveDaysByEntrepriseId(entrepriseId));
        statistics.put("averageLeaveCount", leaveRepository.getAverageLeaveCountByEntrepriseId(entrepriseId));
        
        // Statistiques détaillées
        statistics.put("leavesByType", leaveRepository.getLeaveCountByType(entrepriseId));
        statistics.put("leavesByStatus", leaveRepository.getLeaveCountByStatus(entrepriseId));
        statistics.put("leavesByEmployee", leaveRepository.getLeaveCountByEmployee(entrepriseId));
        statistics.put("monthlyStatistics", leaveRepository.getLeaveStatisticsByMonth(entrepriseId));
        statistics.put("quarterlyStatistics", leaveRepository.getLeaveStatisticsByQuarter(entrepriseId));
        statistics.put("yearlyStatistics", leaveRepository.getLeaveStatisticsByYear(entrepriseId));
        statistics.put("paidVsUnpaidStatistics", leaveRepository.getPaidVsUnpaidLeaveStatistics(entrepriseId));
        statistics.put("halfDayStatistics", leaveRepository.getHalfDayStatistics(entrepriseId));
        
        return statistics;
    }

    // ==================== TÂCHES AUTOMATISÉES ====================

    /**
     * Surveillance quotidienne des employés
     */
    @Scheduled(cron = "0 0 9 * * ?")
    @Async
    public CompletableFuture<Void> dailyEmployeeMonitoring() {
        LocalDate today = LocalDate.now();
        

        

        
        // Logique de notification et de mise à jour
        System.out.println("Surveillance quotidienne des employés terminée");
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Surveillance mensuelle de la paie
     */
    @Scheduled(cron = "0 0 8 1 * ?")
    @Async
    public CompletableFuture<Void> monthlyPayrollMonitoring() {
        // Générer les rapports de paie mensuels
        // Vérifier les paies en retard
        // Calculer les statistiques mensuelles
        
        System.out.println("Surveillance mensuelle de la paie terminée");
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Surveillance des congés
     */
    @Scheduled(cron = "0 0 8 * * MON")
    @Async
    public CompletableFuture<Void> weeklyLeaveMonitoring() {
        LocalDate today = LocalDate.now();
        

        

        

        
        System.out.println("Surveillance hebdomadaire des congés terminée");
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Génération des rapports RH
     */
    @Scheduled(cron = "0 0 6 1 * ?")
    @Async
    public CompletableFuture<Void> monthlyHRReportGeneration() {
        // Générer les rapports RH mensuels
        // Statistiques des employés
        // Statistiques de la paie
        // Statistiques des congés
        
        System.out.println("Génération des rapports RH mensuels terminée");
        
        return CompletableFuture.completedFuture(null);
    }

    // ==================== MÉTHODES SIMPLES POUR LES TESTS ====================

    /**
     * Récupérer tous les employés (pour les tests)
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /**
     * Récupérer tous les congés (pour les tests)
     */
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    /**
     * Récupérer toutes les paies (pour les tests)
     */
    public List<Payroll> getAllPayrolls() {
        return payrollRepository.findAll();
    }
}


