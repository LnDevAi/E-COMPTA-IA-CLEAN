ackage com.ecomptaia.security.entity;

import com.ecomptaia.accounting.entity.AccountingStandard;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 14, unique = true)
    private String siret;

    @Column(length = 20, unique = true)
    private String vatNumber;

    @Column(length = 3)
    private String countryCode;

    @Column(length = 100)
    private String countryName;

    @Column(length = 20)
    private String accountingStandard;

    @Column(length = 10)
    private String ohadaSystemType;

    @Column(length = 3)
    private String currency;

    @Column(length = 10)
    private String locale;

    @Column(length = 50)
    private String businessType;

    @Column(length = 100)
    private String address;

    @Column(length = 50)
    private String city;

    @Column(length = 20)
    private String postalCode;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String website;

    @Column(length = 50)
    private String industry;

    @Column(precision = 15, scale = 2)
    private BigDecimal annualRevenue;

    @Column
    private Integer employeeCount;

    @Column(length = 20)
    private String fiscalYearStart;

    @Column
    private boolean isActive = true;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(length = 20)
    private String createdBy;

    @Column(length = 20)
    private String updatedBy;

    // Constructeurs
    public Company() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Company(String name, String countryCode, String currency) {
        this();
        this.name = name;
        this.countryCode = countryCode;
        this.currency = currency;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSiret() { return siret; }
    public void setSiret(String siret) { this.siret = siret; }

    public String getVatNumber() { return vatNumber; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public String getAccountingStandard() { return accountingStandard; }
    public void setAccountingStandard(String accountingStandard) { this.accountingStandard = accountingStandard; }

    public String getOhadaSystemType() { return ohadaSystemType; }
    public void setOhadaSystemType(String ohadaSystemType) { this.ohadaSystemType = ohadaSystemType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public BigDecimal getAnnualRevenue() { return annualRevenue; }
    public void setAnnualRevenue(BigDecimal annualRevenue) { this.annualRevenue = annualRevenue; }

    public Integer getEmployeeCount() { return employeeCount; }
    public void setEmployeeCount(Integer employeeCount) { this.employeeCount = employeeCount; }

    public String getFiscalYearStart() { return fiscalYearStart; }
    public void setFiscalYearStart(String fiscalYearStart) { this.fiscalYearStart = fiscalYearStart; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public void setIsActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", currency='" + currency + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}



