package com.ecomptaia.repository;

import com.ecomptaia.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Recherche par code employé
    Optional<Employee> findByEmployeeCode(String employeeCode);

    // Recherche par email
    Optional<Employee> findByEmail(String email);

    // Recherche par numéro de sécurité sociale
    Optional<Employee> findBySocialSecurityNumber(String socialSecurityNumber);

    // Recherche par entreprise
    List<Employee> findByEntrepriseId(Long entrepriseId);

    // Recherche par statut d'emploi
    List<Employee> findByEmploymentStatus(Employee.EmploymentStatus employmentStatus);

    // Recherche par type de contrat
    List<Employee> findByContractType(Employee.ContractType contractType);

    // Recherche par département
    List<Employee> findByDepartment(String department);

    // Recherche par poste
    List<Employee> findByPosition(String position);

    // Recherche par manager
    List<Employee> findByManagerId(Long managerId);

    // Recherche par genre
    List<Employee> findByGender(Employee.Gender gender);

    // Recherche par nationalité
    List<Employee> findByNationality(String nationality);

    // Recherche par entreprise et statut
    List<Employee> findByEntrepriseIdAndEmploymentStatus(Long entrepriseId, Employee.EmploymentStatus employmentStatus);

    // Recherche par entreprise et département
    List<Employee> findByEntrepriseIdAndDepartment(Long entrepriseId, String department);

    // Recherche par entreprise et type de contrat
    List<Employee> findByEntrepriseIdAndContractType(Long entrepriseId, Employee.ContractType contractType);

    // Recherche par nom ou prénom (contient)
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND (LOWER(e.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Employee> findByEntrepriseIdAndNameContaining(@Param("entrepriseId") Long entrepriseId, @Param("searchTerm") String searchTerm);

    // Recherche par email (contient)
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND LOWER(e.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Employee> findByEntrepriseIdAndEmailContaining(@Param("entrepriseId") Long entrepriseId, @Param("searchTerm") String searchTerm);

    // Recherche par code employé (contient)
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Employee> findByEntrepriseIdAndEmployeeCodeContaining(@Param("entrepriseId") Long entrepriseId, @Param("searchTerm") String searchTerm);

    // Recherche par date d'embauche
    List<Employee> findByEntrepriseIdAndHireDateBetween(Long entrepriseId, LocalDate startDate, LocalDate endDate);

    // Recherche par salaire de base
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.baseSalary BETWEEN :minSalary AND :maxSalary")
    List<Employee> findByEntrepriseIdAndBaseSalaryBetween(@Param("entrepriseId") Long entrepriseId, @Param("minSalary") java.math.BigDecimal minSalary, @Param("maxSalary") java.math.BigDecimal maxSalary);

    // Recherche par évaluation de performance
    List<Employee> findByEntrepriseIdAndPerformanceRating(Long entrepriseId, Integer performanceRating);

    // Recherche par évaluation de performance (range)
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.performanceRating BETWEEN :minRating AND :maxRating")
    List<Employee> findByEntrepriseIdAndPerformanceRatingBetween(@Param("entrepriseId") Long entrepriseId, @Param("minRating") Integer minRating, @Param("maxRating") Integer maxRating);

    // Recherche des employés actifs
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'ACTIVE'")
    List<Employee> findActiveEmployeesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des employés en congé
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'ON_LEAVE'")
    List<Employee> findEmployeesOnLeaveByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des employés terminés
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'TERMINATED'")
    List<Employee> findTerminatedEmployeesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Recherche des employés nécessitant une évaluation
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.nextEvaluationDate <= :currentDate")
    List<Employee> findEmployeesNeedingEvaluation(@Param("entrepriseId") Long entrepriseId, @Param("currentDate") LocalDate currentDate);

    // Recherche par date de naissance
    List<Employee> findByEntrepriseIdAndBirthDateBetween(Long entrepriseId, LocalDate startDate, LocalDate endDate);

    // Recherche par devise de salaire
    List<Employee> findByEntrepriseIdAndSalaryCurrency(Long entrepriseId, String salaryCurrency);

    // Statistiques par département
    @Query("SELECT e.department, COUNT(e) FROM Employee e WHERE e.entrepriseId = :entrepriseId GROUP BY e.department")
    List<Object[]> getEmployeeCountByDepartment(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par statut d'emploi
    @Query("SELECT e.employmentStatus, COUNT(e) FROM Employee e WHERE e.entrepriseId = :entrepriseId GROUP BY e.employmentStatus")
    List<Object[]> getEmployeeCountByEmploymentStatus(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par type de contrat
    @Query("SELECT e.contractType, COUNT(e) FROM Employee e WHERE e.entrepriseId = :entrepriseId GROUP BY e.contractType")
    List<Object[]> getEmployeeCountByContractType(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par genre
    @Query("SELECT e.gender, COUNT(e) FROM Employee e WHERE e.entrepriseId = :entrepriseId GROUP BY e.gender")
    List<Object[]> getEmployeeCountByGender(@Param("entrepriseId") Long entrepriseId);

    // Statistiques par évaluation de performance
    @Query("SELECT e.performanceRating, COUNT(e) FROM Employee e WHERE e.entrepriseId = :entrepriseId GROUP BY e.performanceRating")
    List<Object[]> getEmployeeCountByPerformanceRating(@Param("entrepriseId") Long entrepriseId);

    // Salaire total par département
    @Query("SELECT e.department, SUM(e.baseSalary) FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'ACTIVE' GROUP BY e.department")
    List<Object[]> getTotalSalaryByDepartment(@Param("entrepriseId") Long entrepriseId);

    // Salaire moyen par département
    @Query("SELECT e.department, AVG(e.baseSalary) FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'ACTIVE' GROUP BY e.department")
    List<Object[]> getAverageSalaryByDepartment(@Param("entrepriseId") Long entrepriseId);



    // Employés avec le plus d'années de service
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId ORDER BY e.hireDate ASC")
    List<Employee> findEmployeesBySeniority(@Param("entrepriseId") Long entrepriseId);

    // Employés récemment embauchés
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.hireDate >= :startDate ORDER BY e.hireDate DESC")
    List<Employee> findRecentlyHiredEmployees(@Param("entrepriseId") Long entrepriseId, @Param("startDate") LocalDate startDate);

    // Employés avec les meilleures performances
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId ORDER BY e.performanceRating DESC")
    List<Employee> findTopPerformers(@Param("entrepriseId") Long entrepriseId);

    // Employés avec les performances les plus faibles
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId ORDER BY e.performanceRating ASC")
    List<Employee> findLowPerformers(@Param("entrepriseId") Long entrepriseId);

    // Recherche avancée combinée
    @Query("SELECT e FROM Employee e WHERE e.entrepriseId = :entrepriseId " +
           "AND (:department IS NULL OR e.department = :department) " +
           "AND (:employmentStatus IS NULL OR e.employmentStatus = :employmentStatus) " +
           "AND (:contractType IS NULL OR e.contractType = :contractType) " +
           "AND (:gender IS NULL OR e.gender = :gender) " +
           "AND (:minSalary IS NULL OR e.baseSalary >= :minSalary) " +
           "AND (:maxSalary IS NULL OR e.baseSalary <= :maxSalary)")
    List<Employee> findEmployeesByAdvancedCriteria(
        @Param("entrepriseId") Long entrepriseId,
        @Param("department") String department,
        @Param("employmentStatus") Employee.EmploymentStatus employmentStatus,
        @Param("contractType") Employee.ContractType contractType,
        @Param("gender") Employee.Gender gender,
        @Param("minSalary") java.math.BigDecimal minSalary,
        @Param("maxSalary") java.math.BigDecimal maxSalary
    );

    // Compter le nombre total d'employés par entreprise
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.entrepriseId = :entrepriseId")
    Long countByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les employés actifs par entreprise
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'ACTIVE'")
    Long countActiveEmployeesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les employés en congé par entreprise
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'ON_LEAVE'")
    Long countEmployeesOnLeaveByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Compter les employés terminés par entreprise
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'TERMINATED'")
    Long countTerminatedEmployeesByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Salaire total de l'entreprise
    @Query("SELECT SUM(e.baseSalary) FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'ACTIVE'")
    java.math.BigDecimal getTotalSalaryByEntrepriseId(@Param("entrepriseId") Long entrepriseId);

    // Salaire moyen de l'entreprise
    @Query("SELECT AVG(e.baseSalary) FROM Employee e WHERE e.entrepriseId = :entrepriseId AND e.employmentStatus = 'ACTIVE'")
    java.math.BigDecimal getAverageSalaryByEntrepriseId(@Param("entrepriseId") Long entrepriseId);
}
