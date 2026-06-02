package com.visitorapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DeptMaster")
public class DeptMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DeptId") private Integer deptId;
    @Column(name = "CompId") private Integer compId;
    @Column(name = "DeptShortName") private String deptShortName;
    @Column(name = "DeptName") private String deptName;
    @Column(name = "CreatedBy") private Integer createdBy;
    @Column(name = "CreatedDate") private Date createdDate;
    @Column(name = "LastUpdatedBy") private Integer lastUpdatedBy;
    @Column(name = "LastUpdatedDate") private Date lastUpdatedDate;
    @Column(name = "IsActive") private Boolean isActive;
    @Column(name = "IsUpdated") private Boolean isUpdated;
    @Column(name = "IsDeleted") private Boolean isDeleted;

    public Integer getDeptId() { return deptId; }
    public void setDeptId(Integer v) { this.deptId = v; }
    public Integer getCompId() { return compId; }
    public void setCompId(Integer v) { this.compId = v; }
    public String getDeptShortName() { return deptShortName; }
    public void setDeptShortName(String v) { this.deptShortName = v; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String v) { this.deptName = v; }
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
