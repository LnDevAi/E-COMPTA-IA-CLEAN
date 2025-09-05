package com.ecomptaia.repository;

import com.ecomptaia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Recherche par nom d'utilisateur
    Optional<User> findByUsername(String username);

    // Recherche par email
    Optional<User> findByEmail(String email);

    // Recherche par nom d'utilisateur ou email
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    // Recherche des utilisateurs actifs
    List<User> findByIsActiveTrue();

    // Recherche des utilisateurs par département
    List<User> findByDepartment(String department);

    // Recherche des utilisateurs par pays
    List<User> findByCountryCode(String countryCode);

    // Recherche des administrateurs
    List<User> findByIsAdminTrue();

    // Recherche des utilisateurs par type de contrat
    List<User> findByContractType(String contractType);

    // Recherche des utilisateurs par plage de salaire
    @Query("SELECT u FROM User u WHERE u.baseSalary BETWEEN :minSalary AND :maxSalary")
    List<User> findBySalaryRange(@Param("minSalary") Double minSalary, @Param("maxSalary") Double maxSalary);

    // Recherche des utilisateurs par date d'embauche
    @Query("SELECT u FROM User u WHERE u.hireDate >= :startDate")
    List<User> findByHireDateAfter(@Param("startDate") java.time.LocalDateTime startDate);

    // Recherche des utilisateurs par nom (recherche partielle)
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);

    // Recherche des utilisateurs par entreprise (via l'ID de l'entreprise)
    @Query("SELECT u FROM User u WHERE u.companyId = :companyId")
    List<User> findByCompanyId(@Param("companyId") Long companyId);

    // Recherche des utilisateurs connectés récemment
    @Query("SELECT u FROM User u WHERE u.lastLoginDate >= :since")
    List<User> findRecentlyActiveUsers(@Param("since") java.time.LocalDateTime since);

    // Compter les utilisateurs actifs par département
    @Query("SELECT u.department, COUNT(u) FROM User u WHERE u.isActive = true GROUP BY u.department")
    List<Object[]> countActiveUsersByDepartment();

    // Compter les utilisateurs par pays
    @Query("SELECT u.countryCode, COUNT(u) FROM User u GROUP BY u.countryCode")
    List<Object[]> countUsersByCountry();

    // Vérifier si un nom d'utilisateur existe
    boolean existsByUsername(String username);

    // Vérifier si un email existe
    boolean existsByEmail(String email);

    // Recherche des utilisateurs par rôle (simplifié - à implémenter plus tard)
    // @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    // List<User> findByRoleName(@Param("roleName") String roleName);

    // Recherche des utilisateurs par permissions (simplifié - à implémenter plus tard)
    // @Query("SELECT u FROM User u JOIN u.permissions p WHERE p.name = :permissionName")
    // List<User> findByPermissionName(@Param("permissionName") String permissionName);
}

