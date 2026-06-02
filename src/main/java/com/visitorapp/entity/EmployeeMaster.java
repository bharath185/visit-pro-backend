package com.visitorapp.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "EmployeeMaster")
public class EmployeeMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmpId") private Integer empId;
    @Column(name = "CompId") private Integer compId;
    @Column(name = "LEId") private Integer leId;
    @Column(name = "BUId") private Integer buId;
    @Column(name = "LocationId") private Integer locationId;
    @Column(name = "PlantId") private Integer plantId;
    @Column(name = "CategoryId") private Integer categoryId;
    @Column(name = "DeptName") private String deptName;
    @Column(name = "DesignationId") private Integer designationId;
    @Column(name = "DesignationName") private String designationName;
    @Column(name = "ReportId") private Integer reportId;
    @Column(name = "ReportName") private String reportName;
    @Column(name = "EmpCode") private String empCode;
    @Column(name = "UserName") private String userName;
    @Column(name = "Password") private String password;
    @Column(name = "Photo") private String photo;
    @Column(name = "Salutation") private Integer salutation;
    @Column(name = "FirstName") private String firstName;
    @Column(name = "MiddleName") private String middleName;
    @Column(name = "LastName") private String lastName;
    @Column(name = "DOB") private Date dob;
    @Column(name = "MobileNo") private String mobileNo;
    @Column(name = "EmailId") private String emailId;
    @Column(name = "BloodGroup") private String bloodGroup;
    @Column(name = "MaritalStatus") private String maritalStatus;
    @Column(name = "Gender") private String gender;
    @Column(name = "JoiningDate") private Date joiningDate;
    @Column(name = "InterviewDate") private Date interviewDate;
    @Column(name = "EndDate") private Date endDate;
    @Column(name = "EmpStatus") private String empStatus;
    @Column(name = "Reason") private String reason;
    @Column(name = "EmpType") private Integer empType;
    @Column(name = "IsRelieved") private Boolean isRelieved;
    @Column(name = "IsActive") private Boolean isActive;
    @Column(name = "IsDeleted") private Boolean isDeleted;
    @Column(name = "IsAdminUser") private Boolean isAdminUser;
    @Column(name = "IsSecurity") private Boolean isSecurity;
    @Column(name = "IsTempPassword") private Boolean isTempPassword;

    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer v) { this.empId = v; }
    public Integer getCompId() { return compId; }
    public void setCompId(Integer v) { this.compId = v; }
    public Integer getLeId() { return leId; }
    public void setLeId(Integer v) { this.leId = v; }
    public Integer getBuId() { return buId; }
    public void setBuId(Integer v) { this.buId = v; }
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer v) { this.locationId = v; }
    public Integer getPlantId() { return plantId; }
    public void setPlantId(Integer v) { this.plantId = v; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer v) { this.categoryId = v; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String v) { this.deptName = v; }
    public Integer getDesignationId() { return designationId; }
    public void setDesignationId(Integer v) { this.designationId = v; }
    public String getDesignationName() { return designationName; }
    public void setDesignationName(String v) { this.designationName = v; }
    public Integer getReportId() { return reportId; }
    public void setReportId(Integer v) { this.reportId = v; }
    public String getReportName() { return reportName; }
    public void setReportName(String v) { this.reportName = v; }
    public String getEmpCode() { return empCode; }
    public void setEmpCode(String v) { this.empCode = v; }
    public String getUserName() { return userName; }
    public void setUserName(String v) { this.userName = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public String getPhoto() { return photo; }
    public void setPhoto(String v) { this.photo = v; }
    public Integer getSalutation() { return salutation; }
    public void setSalutation(Integer v) { this.salutation = v; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String v) { this.middleName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public Date getDob() { return dob; }
    public void setDob(Date v) { this.dob = v; }
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String v) { this.mobileNo = v; }
    public String getEmailId() { return emailId; }
    public void setEmailId(String v) { this.emailId = v; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String v) { this.bloodGroup = v; }
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String v) { this.maritalStatus = v; }
    public String getGender() { return gender; }
    public void setGender(String v) { this.gender = v; }
    public Date getJoiningDate() { return joiningDate; }
    public void setJoiningDate(Date v) { this.joiningDate = v; }
    public Date getInterviewDate() { return interviewDate; }
    public void setInterviewDate(Date v) { this.interviewDate = v; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date v) { this.endDate = v; }
    public String getEmpStatus() { return empStatus; }
    public void setEmpStatus(String v) { this.empStatus = v; }
    public String getReason() { return reason; }
    public void setReason(String v) { this.reason = v; }
    public Integer getEmpType() { return empType; }
    public void setEmpType(Integer v) { this.empType = v; }
    public Boolean getIsRelieved() { return isRelieved; }
    public void setIsRelieved(Boolean v) { this.isRelieved = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean v) { this.isDeleted = v; }
    public Boolean getIsAdminUser() { return isAdminUser; }
    public void setIsAdminUser(Boolean v) { this.isAdminUser = v; }
    public Boolean getIsSecurity() { return isSecurity; }
    public void setIsSecurity(Boolean v) { this.isSecurity = v; }
    public Boolean getIsTempPassword() { return isTempPassword; }
    public void setIsTempPassword(Boolean v) { this.isTempPassword = v; }
}
