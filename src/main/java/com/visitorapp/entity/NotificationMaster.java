package com.visitorapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "NotificationMaster")
public class NotificationMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotificationId") private Integer notificationId;
    @Column(name = "EmpId") private Integer empId;
    @Column(name = "Title") private String title;
    @Column(name = "Message") private String message;
    @Column(name = "Type") private String type;
    @Column(name = "IsRead") private Boolean isRead;
    @Column(name = "IsStarred") private Boolean isStarred;
    @Column(name = "RelatedId") private Integer relatedId;
    @Column(name = "IsActive") private Boolean isActive;
    @Column(name = "IsDeleted") private Boolean isDeleted;
    @Column(name = "CreatedDate") private Date createdDate;

    public Integer getNotificationId() { return notificationId; }
    public void setNotificationId(Integer v) { this.notificationId = v; }
    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer v) { this.empId = v; }
    public String getTitle() { return title; }
    public void setTitle(String v) { this.title = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }
    public String getType() { return type; }
    public void setType(String v) { this.type = v; }
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean v) { this.isRead = v; }
    public Boolean getIsStarred() { return isStarred; }
    public void setIsStarred(Boolean v) { this.isStarred = v; }
    public Integer getRelatedId() { return relatedId; }
    public void setRelatedId(Integer v) { this.relatedId = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean v) { this.isDeleted = v; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date v) { this.createdDate = v; }
}