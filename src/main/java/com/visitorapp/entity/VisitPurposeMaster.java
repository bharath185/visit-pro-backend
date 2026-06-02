package com.visitorapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "VisitPurposeMaster")
public class VisitPurposeMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PurposeId")
    private Integer purposeId;

    @Column(name = "PurposeName")
    private String purposeName;

    @Column(name = "Description")
    private String description;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    public Integer getPurposeId() { return purposeId; }
    public void setPurposeId(Integer v) { this.purposeId = v; }

    public String getPurposeName() { return purposeName; }
    public void setPurposeName(String v) { this.purposeName = v; }

    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean v) { this.isDeleted = v; }
}
