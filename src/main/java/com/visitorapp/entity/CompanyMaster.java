package com.visitorapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CompanyMaster")
public class CompanyMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompId") private Integer compId;
    @Column(name = "Company") private String company;
    @Column(name = "CompanyCode") private String companyCode;
    @Column(name = "LocationMap") private String locationMap;
    @Column(name = "Address") private String address;
    @Column(name = "CreatedBy") private Integer createdBy;
    @Column(name = "CreatedDate") private Date createdDate;
    @Column(name = "LastUpdatedBy") private Integer lastUpdatedBy;
    @Column(name = "LastUpdatedDate") private Date lastUpdatedDate;
    @Column(name = "IsActive") private Boolean isActive;
    @Column(name = "IsUpdated") private Boolean isUpdated;
    @Column(name = "IsDeleted") private Boolean isDeleted;

    public Integer getCompId() { return compId; }
    public void setCompId(Integer v) { this.compId = v; }
    public String getCompany() { return company; }
    public void setCompany(String v) { this.company = v; }
    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String v) { this.companyCode = v; }
    public String getLocationMap() { return locationMap; }
    public void setLocationMap(String v) { this.locationMap = v; }
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer v) { this.createdBy = v; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date v) { this.createdDate = v; }
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(Integer v) { this.lastUpdatedBy = v; }
    public Date getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(Date v) { this.lastUpdatedDate = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }
    public Boolean getIsUpdated() { return isUpdated; }
    public void setIsUpdated(Boolean v) { this.isUpdated = v; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean v) { this.isDeleted = v; }
}
