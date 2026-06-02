package com.visitorapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MasterViewModel {
    @JsonProperty("Id") private Integer id;
    @JsonProperty("Name") private String name;
    @JsonProperty("Code") private String code;
    @JsonProperty("ShortName") private String shortName;
    @JsonProperty("Description") private String description;
    @JsonProperty("ParentId") private Integer parentId;
    @JsonProperty("ParentName") private String parentName;
    @JsonProperty("IsActive") private Boolean isActive;
    @JsonProperty("Msg") private String msg;
    @JsonProperty("Field1") private String field1;
    @JsonProperty("Field2") private String field2;
    @JsonProperty("Field3") private String field3;
    @JsonProperty("Password") private String password;
    @JsonProperty("IsAdminUser") private Boolean isAdminUser;
    @JsonProperty("IsSecurity") private Boolean isSecurity;
    @JsonProperty("IsPlantAdmin") private Boolean isPlantAdmin;
    @JsonProperty("PlantAdminId") private Integer plantAdminId;
    @JsonProperty("PlantId") private Integer plantId;
    @JsonProperty("LocationId") private Integer locationId;
    @JsonProperty("ReportId") private Integer reportId;
    @JsonProperty("ReportName") private String reportName;

    public Integer getId() { return id; }
    public void setId(Integer v) { this.id = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getCode() { return code; }
    public void setCode(String v) { this.code = v; }
    public String getShortName() { return shortName; }
    public void setShortName(String v) { this.shortName = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { this.description = v; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer v) { this.parentId = v; }
    public String getParentName() { return parentName; }
    public void setParentName(String v) { this.parentName = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }
    public String getMsg() { return msg; }
    public void setMsg(String v) { this.msg = v; }
    public String getField1() { return field1; }
    public void setField1(String v) { this.field1 = v; }
    public String getField2() { return field2; }
    public void setField2(String v) { this.field2 = v; }
    public String getField3() { return field3; }
    public void setField3(String v) { this.field3 = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { this.password = v; }
    public Boolean getIsAdminUser() { return isAdminUser; }
    public void setIsAdminUser(Boolean v) { this.isAdminUser = v; }
    public Boolean getIsSecurity() { return isSecurity; }
    public void setIsSecurity(Boolean v) { this.isSecurity = v; }
    public Boolean getIsPlantAdmin() { return isPlantAdmin; }
    public void setIsPlantAdmin(Boolean v) { this.isPlantAdmin = v; }
    public Integer getPlantAdminId() { return plantAdminId; }
    public void setPlantAdminId(Integer v) { this.plantAdminId = v; }
    public Integer getPlantId() { return plantId; }
    public void setPlantId(Integer v) { this.plantId = v; }
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer v) { this.locationId = v; }
    public Integer getReportId() { return reportId; }
    public void setReportId(Integer v) { this.reportId = v; }
    public String getReportName() { return reportName; }
    public void setReportName(String v) { this.reportName = v; }
}
