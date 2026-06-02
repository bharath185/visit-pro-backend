package com.visitorapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "LocationMaster")
public class LocationMaster {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LocationId") private Integer locationId;
    @Column(name = "LocationName") private String locationName;
    @Column(name = "LocationCode") private String locationCode;
    @Column(name = "CompId") private Integer compId;
    @Column(name = "IsActive") private Boolean isActive;
    @Column(name = "IsDeleted") private Boolean isDeleted;
    @Column(name = "CreatedDate") private Date createdDate;
    @Column(name = "LastUpdatedDate") private Date lastUpdatedDate;

    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer v) { this.locationId = v; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String v) { this.locationName = v; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String v) { this.locationCode = v; }
    public Integer getCompId() { return compId; }
    public void setCompId(Integer v) { this.compId = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean v) { this.isDeleted = v; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date v) { this.createdDate = v; }
    public Date getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(Date v) { this.lastUpdatedDate = v; }
}
