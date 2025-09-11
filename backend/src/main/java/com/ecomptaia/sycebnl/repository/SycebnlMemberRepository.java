ackage com.ecomptaia.sycebnl.repository;

import com.ecomptaia.sycebnl.entity.SycebnlMember;
import com.ecomptaia.sycebnl.entity.SycebnlMember.ContributionFrequency;
import com.ecomptaia.sycebnl.entity.SycebnlMember.MemberSegment;
import com.ecomptaia.sycebnl.entity.SycebnlMember.MemberType;
import com.ecomptaia.sycebnl.entity.SycebnlMember.MembershipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour la gestion des membres SYCEBNL
 * Fournit des méthodes de recherche spécialisées pour la gestion des membres d'organisations
 */
@Repository
public interface SycebnlMemberRepository extends JpaRepository<SycebnlMember, Long> {
    
    // === RECHERCHES PAR ORGANISATION ===
    Page<SycebnlMember> findByOrganizationId(Long organizationId, Pageable pageable);
    
    List<SycebnlMember> findByOrganizationIdAndIsActiveTrue(Long organizationId);
    
    Optional<SycebnlMember> findByOrganizationIdAndMemberNumber(Long organizationId, String memberNumber);
    
    Optional<SycebnlMember> findByOrganizationIdAndEmail(Long organizationId, String email);
    
    // === RECHERCHES PAR STATUT ===
    List<SycebnlMember> findByMembershipStatus(MembershipStatus status);
    
    List<SycebnlMember> findByOrganizationIdAndMembershipStatus(Long organizationId, MembershipStatus status);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.membershipStatus = 'ACTIVE'")
    List<SycebnlMember> findActiveMembers(@Param("organizationId") Long organizationId);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.membershipStatus = 'INACTIVE'")
    List<SycebnlMember> findInactiveMembers(@Param("organizationId") Long organizationId);
    
    // === RECHERCHES PAR TYPE DE MEMBRE ===
    List<SycebnlMember> findByMemberType(MemberType memberType);
    
    List<SycebnlMember> findByOrganizationIdAndMemberType(Long organizationId, MemberType memberType);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.isBoardMember = true")
    List<SycebnlMember> findBoardMembers(@Param("organizationId") Long organizationId);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.isVolunteer = true")
    List<SycebnlMember> findVolunteers(@Param("organizationId") Long organizationId);
    
    // === RECHERCHES PAR COTISATIONS ===
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.outstandingContributions > 0")
    List<SycebnlMember> findMembersWithOutstandingContributions(@Param("organizationId") Long organizationId);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.nextContributionDue <= :dueDate")
    List<SycebnlMember> findMembersWithDueContributions(@Param("organizationId") Long organizationId, @Param("dueDate") LocalDate dueDate);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.lastContributionDate < :cutoffDate")
    List<SycebnlMember> findMembersWithOverdueContributions(@Param("organizationId") Long organizationId, @Param("cutoffDate") LocalDate cutoffDate);
    
    List<SycebnlMember> findByContributionFrequency(ContributionFrequency frequency);
    
    List<SycebnlMember> findByOrganizationIdAndContributionFrequency(Long organizationId, ContributionFrequency frequency);
    
    // === RECHERCHES PAR SEGMENT CRM ===
    List<SycebnlMember> findByMemberSegment(MemberSegment segment);
    
    List<SycebnlMember> findByOrganizationIdAndMemberSegment(Long organizationId, MemberSegment segment);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.engagementScore >= :minScore ORDER BY m.engagementScore DESC")
    List<SycebnlMember> findHighEngagementMembers(@Param("organizationId") Long organizationId, @Param("minScore") Integer minScore);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.churnProbability > :threshold ORDER BY m.churnProbability DESC")
    List<SycebnlMember> findHighChurnRiskMembers(@Param("organizationId") Long organizationId, @Param("threshold") BigDecimal threshold);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.lifetimeValue >= :minValue ORDER BY m.lifetimeValue DESC")
    List<SycebnlMember> findHighValueMembers(@Param("organizationId") Long organizationId, @Param("minValue") BigDecimal minValue);
    
    // === RECHERCHES PAR GÉOGRAPHIE ===
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.country = :country")
    List<SycebnlMember> findByCountry(@Param("organizationId") Long organizationId, @Param("country") String country);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.region = :region")
    List<SycebnlMember> findByRegion(@Param("organizationId") Long organizationId, @Param("region") String region);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.city = :city")
    List<SycebnlMember> findByCity(@Param("organizationId") Long organizationId, @Param("city") String city);
    
    // === RECHERCHES PAR DÉMOGGRAPHIE ===
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.gender = :gender")
    List<SycebnlMember> findByGender(@Param("organizationId") Long organizationId, @Param("gender") String gender);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.nationality = :nationality")
    List<SycebnlMember> findByNationality(@Param("organizationId") Long organizationId, @Param("nationality") String nationality);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.dateOfBirth BETWEEN :startDate AND :endDate")
    List<SycebnlMember> findByAgeRange(@Param("organizationId") Long organizationId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // === RECHERCHES PAR COMMUNICATION ===
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.emailOptIn = true")
    List<SycebnlMember> findMembersOptedInForEmail(@Param("organizationId") Long organizationId);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.smsOptIn = true")
    List<SycebnlMember> findMembersOptedInForSms(@Param("organizationId") Long organizationId);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.languagePreference = :language")
    List<SycebnlMember> findByLanguagePreference(@Param("organizationId") Long organizationId, @Param("language") String language);
    
    // === STATISTIQUES ===
    @Query("SELECT COUNT(m) FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.membershipStatus = :status")
    Long countByMembershipStatus(@Param("organizationId") Long organizationId, @Param("status") MembershipStatus status);
    
    @Query("SELECT COUNT(m) FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.memberType = :type")
    Long countByMemberType(@Param("organizationId") Long organizationId, @Param("type") MemberType type);
    
    @Query("SELECT COUNT(m) FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.memberSegment = :segment")
    Long countByMemberSegment(@Param("organizationId") Long organizationId, @Param("segment") MemberSegment segment);
    
    @Query("SELECT COUNT(m) FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.isVolunteer = true")
    Long countVolunteers(@Param("organizationId") Long organizationId);
    
    @Query("SELECT COUNT(m) FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.isBoardMember = true")
    Long countBoardMembers(@Param("organizationId") Long organizationId);
    
    @Query("SELECT SUM(m.totalContributionsPaid) FROM SycebnlMember m WHERE m.organization.id = :organizationId")
    BigDecimal getTotalContributionsPaid(@Param("organizationId") Long organizationId);
    
    @Query("SELECT SUM(m.outstandingContributions) FROM SycebnlMember m WHERE m.organization.id = :organizationId")
    BigDecimal getTotalOutstandingContributions(@Param("organizationId") Long organizationId);
    
    @Query("SELECT AVG(m.engagementScore) FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.engagementScore IS NOT NULL")
    Double getAverageEngagementScore(@Param("organizationId") Long organizationId);
    
    @Query("SELECT AVG(m.lifetimeValue) FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.lifetimeValue IS NOT NULL")
    BigDecimal getAverageLifetimeValue(@Param("organizationId") Long organizationId);
    
    // === ANALYTICS AVANCÉES ===
    @Query("SELECT m.memberType, COUNT(m), AVG(m.engagementScore), SUM(m.totalContributionsPaid) " +
           "FROM SycebnlMember m WHERE m.organization.id = :organizationId GROUP BY m.memberType")
    List<Object[]> getMemberTypeAnalytics(@Param("organizationId") Long organizationId);
    
    @Query("SELECT m.memberSegment, COUNT(m), AVG(m.engagementScore), AVG(m.lifetimeValue) " +
           "FROM SycebnlMember m WHERE m.organization.id = :organizationId GROUP BY m.memberSegment")
    List<Object[]> getMemberSegmentAnalytics(@Param("organizationId") Long organizationId);
    
    @Query("SELECT m.contributionFrequency, COUNT(m), SUM(m.totalContributionsPaid), AVG(m.annualContribution) " +
           "FROM SycebnlMember m WHERE m.organization.id = :organizationId GROUP BY m.contributionFrequency")
    List<Object[]> getContributionFrequencyAnalytics(@Param("organizationId") Long organizationId);
    
    @Query("SELECT m.country, COUNT(m), SUM(m.totalContributionsPaid) " +
           "FROM SycebnlMember m WHERE m.organization.id = :organizationId GROUP BY m.country")
    List<Object[]> getGeographicAnalytics(@Param("organizationId") Long organizationId);
    
    @Query("SELECT m.gender, COUNT(m), AVG(m.engagementScore) " +
           "FROM SycebnlMember m WHERE m.organization.id = :organizationId GROUP BY m.gender")
    List<Object[]> getGenderAnalytics(@Param("organizationId") Long organizationId);
    
    // === RECHERCHE TEXTUELLE ===
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND " +
           "(LOWER(m.firstName) LIKE %:searchTerm% OR " +
           "LOWER(m.lastName) LIKE %:searchTerm% OR " +
           "LOWER(m.email) LIKE %:searchTerm% OR " +
           "LOWER(m.memberNumber) LIKE %:searchTerm%)")
    List<SycebnlMember> searchMembers(@Param("organizationId") Long organizationId, @Param("searchTerm") String searchTerm);
    
    // === RECHERCHES PAR PÉRIODE ===
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.registrationDate BETWEEN :startDate AND :endDate")
    List<SycebnlMember> findMembersRegisteredBetween(@Param("organizationId") Long organizationId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.lastContributionDate BETWEEN :startDate AND :endDate")
    List<SycebnlMember> findMembersWithContributionsBetween(@Param("organizationId") Long organizationId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.membershipExpiryDate BETWEEN :startDate AND :endDate")
    List<SycebnlMember> findMembersExpiringBetween(@Param("organizationId") Long organizationId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // === RECHERCHES PAR COMPORTEMENT ===
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.lastContributionDate < :cutoffDate AND m.membershipStatus = 'ACTIVE'")
    List<SycebnlMember> findInactiveActiveMembers(@Param("organizationId") Long organizationId, @Param("cutoffDate") LocalDate cutoffDate);
    
    @Query("SELECT m FROM SycebnlMember m WHERE m.organization.id = :organizationId AND m.volunteerHoursPerMonth > :minHours")
    List<SycebnlMember> findHighActivityVolunteers(@Param("organizationId") Long organizationId, @Param("minHours") Integer minHours);
}



