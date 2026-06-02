package com.visitorapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class VisitorManagementViewModel {
    @JsonProperty("VisitId") private Integer visitId;
    @JsonProperty("RegNo") private String regNo;
    @JsonProperty("QR") private String qr;
    @JsonProperty("Name") private String name;
    @JsonProperty("Designation") private String designation;
    @JsonProperty("Company") private String company;
    @JsonProperty("Purpose") private String purpose;
    @JsonProperty("PMail") private String pMail;
    @JsonProperty("OMail") private String oMail;
    @JsonProperty("Mobile") private String mobile;
    @JsonProperty("AMobile") private String aMobile;
    @JsonProperty("Photo") private String photo;
    @JsonProperty("Category") private String category;
    @JsonProperty("CompId") private String compId;
    @JsonProperty("PlantId") private Integer plantId;
    @JsonProperty("CompName") private String compName;
    @JsonProperty("WhomtoMeet") private Integer whomToMeet;
    @JsonProperty("WName") private String wName;
    @JsonProperty("WEmpCode") private String wEmpCode;
    @JsonProperty("Date") private Date visitDate;
    @JsonProperty("Time") private String time;
    @JsonProperty("Invited") private Boolean invited;
    @JsonProperty("Accept") private Boolean accept;
    @JsonProperty("Approved") private Boolean approved;
    @JsonProperty("Expired") private Boolean expired;
    @JsonProperty("Accessories") private String accessories;
    @JsonProperty("DirectCheckIn") private Boolean directCheckIn;
    @JsonProperty("CheckIn") private Date checkIn;
    @JsonProperty("CheckOut") private Date checkOut;
    @JsonProperty("IdCard") private String idCard;
    @JsonProperty("VisitorCheckIn") private Boolean visitorCheckIn;
    @JsonProperty("VisitorCheckOut") private Boolean visitorCheckOut;
    @JsonProperty("CreatedBy") private Integer createdBy;
    @JsonProperty("CreatedDate") private Date createdDate;
    @JsonProperty("LastUpdatedBy") private Integer lastUpdatedBy;
    @JsonProperty("LastUpdatedDate") private Date lastUpdatedDate;
    @JsonProperty("IsActive") private Boolean isActive;
    @JsonProperty("IsUpdated") private Boolean isUpdated;
    @JsonProperty("IsDeleted") private Boolean isDeleted;
    @JsonProperty("RejectRemark") private String rejectRemark;
    @JsonProperty("EmpId") private Integer empId;
    private java.util.List<VisitorManagementViewModel> visitorList;

    public java.util.List<VisitorManagementViewModel> getVisitorList() { return visitorList; }
    public void setVisitorList(java.util.List<VisitorManagementViewModel> v) { this.visitorList = v; }
    @JsonProperty("EmpCode") private String empCode;
    @JsonProperty("InviteCode") private String inviteCode;
    @JsonProperty("CheckInCode") private String checkInCode;
    @JsonProperty("CheckoutCode") private String checkoutCode;
    @JsonProperty("Status") private String status;
    @JsonProperty("Msg") private String msg;
    @JsonProperty("OTP") private String otp;

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
    public String getCompName() { return compName; }
    public void setCompName(String v) { this.compName = v; }
    public Integer getWhomToMeet() { return whomToMeet; }
    public void setWhomToMeet(Integer v) { this.whomToMeet = v; }
    public String getwName() { return wName; }
    public void setwName(String v) { this.wName = v; }
    public String getwEmpCode() { return wEmpCode; }
    public void setwEmpCode(String v) { this.wEmpCode = v; }
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
    public Boolean getVisitorCheckIn() { return visitorCheckIn; }
    public void setVisitorCheckIn(Boolean v) { this.visitorCheckIn = v; }
    public Boolean getVisitorCheckOut() { return visitorCheckOut; }
    public void setVisitorCheckOut(Boolean v) { this.visitorCheckOut = v; }
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
    public String getRejectRemark() { return rejectRemark; }
    public void setRejectRemark(String v) { this.rejectRemark = v; }
    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer v) { this.empId = v; }
    public String getMsg() { return msg; }
    public void setMsg(String v) { this.msg = v; }
    public String getEmpCode() { return empCode; }
    public void setEmpCode(String v) { this.empCode = v; }
    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String v) { this.inviteCode = v; }
    public String getCheckInCode() { return checkInCode; }
    public void setCheckInCode(String v) { this.checkInCode = v; }
    public String getCheckoutCode() { return checkoutCode; }
    public void setCheckoutCode(String v) { this.checkoutCode = v; }
    public String getStatus() { return status; }
    public void setStatus(String v) { this.status = v; }
    public String getOtp() { return otp; }
    public void setOtp(String v) { this.otp = v; }
}
