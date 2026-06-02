package com.visitorapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "VisitorManagement")
public class VisitorManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VisitId")
    private Integer visitId;

    @Column(name = "RegNo")
    private String regNo;

    @Column(name = "QR")
    private String qr;

    @Column(name = "Name")
    private String name;

    @Column(name = "Designation")
    private String designation;

    @Column(name = "Company")
    private String company;

    @Column(name = "Purpose")
    private String purpose;

    @Column(name = "PMail")
    private String pMail;

    @Column(name = "OMail")
    private String oMail;

    @Column(name = "Mobile")
    private String mobile;

    @Column(name = "AMobile")
    private String aMobile;

    @Column(name = "Photo")
    private String photo;

    @Column(name = "Category")
    private String category;

    @Column(name = "CompId")
    private String compId;

    @Column(name = "PlantId")
    private Integer plantId;

    @Column(name = "WhomtoMeet")
    private Integer whomToMeet;

    @Column(name = "Date")
    @Temporal(TemporalType.DATE)
    private Date visitDate;

    @Column(name = "Time")
    private String time;

    @Column(name = "Invited")
    private Boolean invited;

    @Column(name = "Accept")
    private Boolean accept;

    @Column(name = "Approved")
    private Boolean approved;

    @Column(name = "Expired")
    private Boolean expired;

    @Column(name = "Accessories")
    private String accessories;

    @Column(name = "DirectCheckIn")
    private Boolean directCheckIn;

    @Column(name = "CheckIn")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkIn;

    @Column(name = "CheckOut")
    @Temporal(TemporalType.TIMESTAMP)
    private Date checkOut;

    @Column(name = "IdCard")
    private String idCard;

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

    @Column(name = "RejectRemark")
    private String rejectRemark;

    public String getRejectRemark() { return rejectRemark; }
    public void setRejectRemark(String v) { this.rejectRemark = v; }
    public Integer getVisitId() { return visitId; }
    public void setVisitId(Integer v) { this.visitId = v; }
    public String getRegNo() { return regNo; }
    public void setRegNo(String v) { this.regNo = v; }
    public String getQr() { return qr; }
    public void setQr(String v) { this.qr = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getDesignation() { return designation; }
    public void setDesignation(String v) { this.designation = v; }
    public String getCompany() { return company; }
    public void setCompany(String v) { this.company = v; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String v) { this.purpose = v; }
    public String getpMail() { return pMail; }
    public void setpMail(String v) { this.pMail = v; }
    public String getoMail() { return oMail; }
    public void setoMail(String v) { this.oMail = v; }
    public String getMobile() { return mobile; }
    public void setMobile(String v) { this.mobile = v; }
    public String getaMobile() { return aMobile; }
    public void setaMobile(String v) { this.aMobile = v; }
    public String getPhoto() { return photo; }
    public void setPhoto(String v) { this.photo = v; }
    public String getCategory() { return category; }
    public void setCategory(String v) { this.category = v; }
    public String getCompId() { return compId; }
    public void setCompId(String v) { this.compId = v; }
    public Integer getPlantId() { return plantId; }
    public void setPlantId(Integer v) { this.plantId = v; }
    public Integer getWhomToMeet() { return whomToMeet; }
    public void setWhomToMeet(Integer v) { this.whomToMeet = v; }
    public Date getVisitDate() { return visitDate; }
    public void setVisitDate(Date v) { this.visitDate = v; }
    public String getTime() { return time; }
    public void setTime(String v) { this.time = v; }
    public Boolean getInvited() { return invited; }
    public void setInvited(Boolean v) { this.invited = v; }
    public Boolean getAccept() { return accept; }
    public void setAccept(Boolean v) { this.accept = v; }
    public Boolean getApproved() { return approved; }
    public void setApproved(Boolean v) { this.approved = v; }
    public Boolean getExpired() { return expired; }
    public void setExpired(Boolean v) { this.expired = v; }
    public String getAccessories() { return accessories; }
    public void setAccessories(String v) { this.accessories = v; }
    public Boolean getDirectCheckIn() { return directCheckIn; }
    public void setDirectCheckIn(Boolean v) { this.directCheckIn = v; }
    public Date getCheckIn() { return checkIn; }
    public void setCheckIn(Date v) { this.checkIn = v; }
    public Date getCheckOut() { return checkOut; }
    public void setCheckOut(Date v) { this.checkOut = v; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String v) { this.idCard = v; }
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
