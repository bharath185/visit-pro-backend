package com.visitorapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LoginViewModel {
    @JsonProperty("Username") private String username;
    @JsonProperty("Password")
    private String password;
    @JsonProperty("EmpId") private Integer empId;
    @JsonProperty("EmpCode") private String empCode;
    @JsonProperty("CompId") private Integer compId;
    @JsonProperty("FirstName") private String firstName;
    @JsonProperty("MiddleName") private String middleName;
    @JsonProperty("LastName") private String lastName;
    @JsonProperty("FullName") private String fullName;
    @JsonProperty("EmailId") private String emailId;
    @JsonProperty("MobileNo") private String mobileNo;
    @JsonProperty("DesignationId") private Integer designationId;
    @JsonProperty("DesignationName") private String designationName;
    @JsonProperty("DeptName") private String deptName;
    @JsonProperty("PlantId") private Integer plantId;
    @JsonProperty("PlantName") private String plantName;
    @JsonProperty("IsPlantAdmin") private Boolean isPlantAdmin;
    @JsonProperty("IsSecurity") private Boolean isSecurity;
    @JsonProperty("Photo") private String photo;
    @JsonProperty("Token") private String token;
    @JsonProperty("Msg") private String msg;
    @JsonProperty("Success") private Boolean success;
    @JsonProperty("IsAdmin") private Boolean isAdmin;
    @JsonProperty("ReportId") private Integer reportId;
    @JsonProperty("MustChangePassword") private Boolean mustChangePassword;

    public String getUsername() { return username; }
    public void setUsername(String v) { this.username = v; }
    @JsonIgnore
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer v) { this.empId = v; }
    public String getEmpCode() { return empCode; }
    public void setEmpCode(String v) { this.empCode = v; }
    public Integer getCompId() { return compId; }
    public void setCompId(Integer v) { this.compId = v; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String v) { this.firstName = v; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String v) { this.middleName = v; }
    public String getLastName() { return lastName; }
    public void setLastName(String v) { this.lastName = v; }
    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getEmailId() { return emailId; }
    public void setEmailId(String v) { this.emailId = v; }
    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String v) { this.mobileNo = v; }
    public Integer getDesignationId() { return designationId; }
    public void setDesignationId(Integer v) { this.designationId = v; }
    public String getDesignationName() { return designationName; }
    public void setDesignationName(String v) { this.designationName = v; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String v) { this.deptName = v; }
    public Integer getPlantId() { return plantId; }
    public void setPlantId(Integer v) { this.plantId = v; }
    public String getPlantName() { return plantName; }
    public void setPlantName(String v) { this.plantName = v; }
    public Boolean getIsPlantAdmin() { return isPlantAdmin; }
    public void setIsPlantAdmin(Boolean v) { this.isPlantAdmin = v; }
    public Boolean getIsSecurity() { return isSecurity; }
    public void setIsSecurity(Boolean v) { this.isSecurity = v; }
    public String getPhoto() { return photo; }
    public void setPhoto(String v) { this.photo = v; }
    public String getToken() { return token; }
    public void setToken(String v) { this.token = v; }
    public String getMsg() { return msg; }
    public void setMsg(String v) { this.msg = v; }
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean v) { this.success = v; }
    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean v) { this.isAdmin = v; }
    public Integer getReportId() { return reportId; }
    public void setReportId(Integer v) { this.reportId = v; }
    public Boolean getMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(Boolean v) { this.mustChangePassword = v; }
}
