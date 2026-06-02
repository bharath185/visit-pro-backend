package com.visitorapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "DesignationMaster")
public class DesignationMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DesignationId") private Integer designationId;
    @Column(name = "DeptId") private Integer deptId;
    @Column(name = "PlantId") private Integer plantId;
    @Column(name = "DesignationShortName") private String designationShortName;
    @Column(name = "Designation") private String designation;
    @Column(name = "HierarchyLevel") private Integer hierarchyLevel;
    @Column(name = "Description") private String description;
    @Column(name = "GradeId") private Integer gradeId;
    @Column(name = "Grade") private String grade;
    @Column(name = "CreatedBy") private Integer createdBy;
    @Column(name = "CreatedDate") private Date createdDate;
    @Column(name = "LastUpdatedBy") private Integer lastUpdatedBy;
    @Column(name = "LastUpdatedDate") private Date lastUpdatedDate;
    @Column(name = "IsActive") private Boolean isActive;
    @Column(name = "IsUpdated") private Boolean isUpdated;
    @Column(name = "IsDeleted") private Boolean isDeleted;

    public Integer getDesignationId() { return designationId; }
    public void setDesignationId(Integer v) { this.designationId = v; }
    public Integer getDeptId() { return deptId; }
    public void setDeptId(Integer v) { this.deptId = v; }
    public Integer getPlantId() { return plantId; }
    public void setPlantId(Integer v) { this.plantId = v; }
    public String getDesignationShortName() { return designationShortName; }
    public void setDesignationShortName(String v) { this.designationShortName = v; }
    public String getDesignation() { return designation; }
    public void setDesignation(String v) { this.designation = v; }
    public Integer getHierarchyLevel() { return hierarchyLevel; }
    public void setHierarchyLevel(Integer v) { this.hierarchyLevel = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public Integer getGradeId() { return gradeId; }
    public void setGradeId(Integer v) { this.gradeId = v; }
    public String getGrade() { return grade; }
    public void setGrade(String v) { this.grade = v; }
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
