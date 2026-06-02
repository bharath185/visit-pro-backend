package com.visitorapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VisitorInviteHistory")
public class VisitorInviteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "VisitorId")
    private Integer visitorId;

    @Column(name = "InviteCode")
    private String inviteCode;

    @Column(name = "CheckoutOtpExpiry")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkoutOtpExpiry;

    @Column(name = "CheckInCode")
    private String checkInCode;

    @Column(name = "CheckoutCode")
    private String checkoutCode;

    @Column(name = "Mail")
    private Boolean mail;

    @Column(name = "Mobile")
    private Boolean mobile;

    @Column(name = "CheckIn")
    private Boolean checkIn;

    @Column(name = "CheckOut")
    private Boolean checkOut;

    @Column(name = "CreatedBy")
    private Integer createdBy;

    @Column(name = "CreatedDate")
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Column(name = "LastUpdatedBy")
    private Integer lastUpdatedBy;

    @Column(name = "LastUpdatedDate")
    @Temporal(TemporalType.DATE)
    private Date lastUpdatedDate;

    @Column(name = "IsActive")
    private Boolean isActive;

    @Column(name = "IsUpdated")
    private Boolean isUpdated;

    @Column(name = "IsDeleted")
    private Boolean isDeleted;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getVisitorId() { return visitorId; }
    public void setVisitorId(Integer visitorId) { this.visitorId = visitorId; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }
    public String getCheckInCode() { return checkInCode; }
    public void setCheckInCode(String v) { this.checkInCode = v; }
    public String getCheckoutCode() { return checkoutCode; }
    public void setCheckoutCode(String v) { this.checkoutCode = v; }
    public Date getCheckoutOtpExpiry() { return checkoutOtpExpiry; }
    public void setCheckoutOtpExpiry(Date v) { this.checkoutOtpExpiry = v; }
    public Boolean getMail() { return mail; }
    public void setMail(Boolean mail) { this.mail = mail; }
    public Boolean getMobile() { return mobile; }
    public void setMobile(Boolean mobile) { this.mobile = mobile; }
    public Boolean getCheckIn() { return checkIn; }
    public void setCheckIn(Boolean checkIn) { this.checkIn = checkIn; }
    public Boolean getCheckOut() { return checkOut; }
    public void setCheckOut(Boolean checkOut) { this.checkOut = checkOut; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public Integer getLastUpdatedBy() { return lastUpdatedBy; }
    public void setLastUpdatedBy(Integer lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }
    public Date getLastUpdatedDate() { return lastUpdatedDate; }
    public void setLastUpdatedDate(Date lastUpdatedDate) { this.lastUpdatedDate = lastUpdatedDate; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public Boolean getIsUpdated() { return isUpdated; }
    public void setIsUpdated(Boolean isUpdated) { this.isUpdated = isUpdated; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
