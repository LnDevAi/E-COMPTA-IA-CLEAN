package com.ecomptaia.service;

import com.ecomptaia.entity.Employee;
import com.ecomptaia.entity.Payroll;
import com.ecomptaia.repository.EmployeeRepository;
import com.ecomptaia.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion de la paie
 */
@Service
@Transactional
public class PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Calculer la paie d'un employé
     */
    public Payroll calculatePayroll(Long employeeId, Integer month, Integer year) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (!employeeOpt.isPresent()) {
            throw new RuntimeException("Employé non trouvé");
        }

        Employee employee = employeeOpt.get();
        Payroll payroll = new Payroll();
        
        payroll.setEmployeeId(employeeId);
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setBaseSalary(employee.getBaseSalary());
        
        // Calculs de base
        BigDecimal baseSalary = employee.getBaseSalary();
        BigDecimal grossSalary = baseSalary;
        
        // Calcul des charges sociales (exemple: 20% du salaire brut)
        BigDecimal socialCharges = baseSalary.multiply(new BigDecimal("0.20"));
        
        // Calcul de l'impôt sur le revenu (exemple: 10% du salaire brut)
        BigDecimal incomeTax = baseSalary.multiply(new BigDecimal("0.10"));
        
        // Calcul du salaire net
        BigDecimal netSalary = grossSalary.subtract(socialCharges).subtract(incomeTax);
        
        payroll.setGrossSalary(grossSalary);
        payroll.setSocialCharges(socialCharges);
        payroll.setIncomeTax(incomeTax);
        payroll.setNetSalary(netSalary);
        payroll.setCurrency(employee.getSalaryCurrency());
        payroll.setStatus("CALCULATED");
        payroll.setCreatedAt(LocalDateTime.now());
        payroll.setUpdatedAt(LocalDateTime.now());
        
        return payrollRepository.save(payroll);
    }

    /**
     * Valider une paie
     */
    public Payroll validatePayroll(Long payrollId) {
        Optional<Payroll> payrollOpt = payrollRepository.findById(payrollId);
        if (payrollOpt.isPresent()) {
            Payroll payroll = payrollOpt.get();
            payroll.setStatus("VALIDATED");
            payroll.setUpdatedAt(LocalDateTime.now());
            return payrollRepository.save(payroll);
        }
        return null;
    }

    /**
     * Payer une paie
     */
    public Payroll payPayroll(Long payrollId) {
        Optional<Payroll> payrollOpt = payrollRepository.findById(payrollId);
        if (payrollOpt.isPresent()) {
            Payroll payroll = payrollOpt.get();
            payroll.setStatus("PAID");
            payroll.setPaymentDate(LocalDateTime.now());
            payroll.setUpdatedAt(LocalDateTime.now());
            return payrollRepository.save(payroll);
        }
        return null;
    }

    /**
     * Obtenir les paies d'un employé
     */
    public List<Payroll> getEmployeePayrolls(Long employeeId) {
        return payrollRepository.findByEmployeeIdOrderByYearDescMonthDesc(employeeId);
    }

    /**
     * Obtenir les paies d'une période
     */
    public List<Payroll> getPayrollsByPeriod(Integer month, Integer year) {
        return payrollRepository.findByMonthAndYear(month, year);
    }

    /**
     * Obtenir les paies d'une entreprise
     */
    public List<Payroll> getCompanyPayrolls(Long companyId) {
        return payrollRepository.findByCompanyIdOrderByYearDescMonthDesc(companyId);
    }

    /**
     * Créer un employé
     */
    public Employee createEmployee(String firstName, String lastName, String email, 
                                 String position, BigDecimal baseSalary, String currency, Long companyId) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setPosition(position);
        employee.setBaseSalary(baseSalary);
        employee.setSalaryCurrency(currency);
        employee.setCompanyId(companyId);
        employee.setHireDate(LocalDateTime.now());
        employee.setIsActive(true);
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        
        return employeeRepository.save(employee);
    }

    /**
     * Mettre à jour les informations d'un employé
     */
    public Employee updateEmployee(Long employeeId, String firstName, String lastName, 
                                 String email, String position, BigDecimal baseSalary) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setEmail(email);
            employee.setPosition(position);
            employee.setBaseSalary(baseSalary);
            employee.setUpdatedAt(LocalDateTime.now());
            return employeeRepository.save(employee);
        }
        return null;
    }

    /**
     * Désactiver un employé
     */
    public void deactivateEmployee(Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            employee.setIsActive(false);
            employee.setTerminationDate(LocalDateTime.now());
            employee.setUpdatedAt(LocalDateTime.now());
            employeeRepository.save(employee);
        }
    }

    /**
     * Obtenir tous les employés d'une entreprise
     */
    public List<Employee> getCompanyEmployees(Long companyId) {
        return employeeRepository.findByCompanyIdAndIsActiveTrue(companyId);
    }

    /**
     * Obtenir les statistiques de paie
     */
    public Object getPayrollStatistics(Long companyId) {
        List<Payroll> payrolls = getCompanyPayrolls(companyId);
        List<Employee> employees = getCompanyEmployees(companyId);
        
        BigDecimal totalGrossSalary = payrolls.stream()
            .map(Payroll::getGrossSalary)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalNetSalary = payrolls.stream()
            .map(Payroll::getNetSalary)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new Object() {
            public final int totalEmployees = employees.size();
            public final int totalPayrolls = payrolls.size();
            public final BigDecimal grossSalary = totalGrossSalary;
            public final BigDecimal netSalary = totalNetSalary;
            public final LocalDateTime lastUpdate = LocalDateTime.now();
            
            @Override
            public String toString() {
                return String.format("PayrollStats{employees=%d, payrolls=%d, gross=%s, net=%s, updated=%s}", 
                    totalEmployees, totalPayrolls, grossSalary, netSalary, lastUpdate);
            }
        };
    }
}
