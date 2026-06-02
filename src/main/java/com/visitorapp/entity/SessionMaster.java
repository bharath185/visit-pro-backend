package com.visitorapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SessionMaster")
public class SessionMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id") private Integer id;
    @Column(name = "Username") private String username;
    @Column(name = "TockenId") private String tockenId;
    @Column(name = "AuthKey") private String authKey;
    @Column(name = "RoleId") private Integer roleId;
    @Column(name = "Status") private Boolean status;
    @Column(name = "Expired") private Boolean expired;
    @Column(name = "CreatedDate") private Date createdDate;
    @Column(name = "LastUpdatedDate") private Date lastUpdatedDate;
    @Column(name = "IsActive") private Boolean isActive;
    @Column(name = "IsDeleted") private Boolean isDeleted;

    public Integer getId() { return id; }
    public void setId(Integer v) { this.id = v; }
    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    public String getTockenId() { return tockenId; }
    public void setTockenId(String v) { this.tockenId = v; }
    public String getAuthKey() { return authKey; }
    public void setAuthKey(String v) { this.authKey = v; }
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer v) { this.roleId = v; }
    public Boolean getStatus() { return status; }
    public void setStatus(Boolean v) { this.status = v; }
    public Boolean getExpired() { return expired; }
    public void setExpired(Boolean v) { this.expired = v; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date v) { this.createdDate = v; }
    public Date getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(Date v) { this.lastUpdatedDate = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean v) { this.isDeleted = v; }
}
