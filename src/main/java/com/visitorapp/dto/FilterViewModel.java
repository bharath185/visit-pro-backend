package com.visitorapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class FilterViewModel {

    private Integer empId;
    private Integer plantId;
    private Date fromDate;
    private Date toDate;
    private String status;
    private String category;

    @JsonProperty("EmpId")
    public Integer getEmpId() { return empId; }
    public void setEmpId(Integer empId) { this.empId = empId; }

    @JsonProperty("PlantId")
    public Integer getPlantId() { return plantId; }
    public void setPlantId(Integer plantId) { this.plantId = plantId; }

    @JsonProperty("FromDate")
    public Date getFromDate() { return fromDate; }
    public void setFromDate(Date fromDate) { this.fromDate = fromDate; }

    @JsonProperty("ToDate")
    public Date getToDate() { return toDate; }
    public void setToDate(Date toDate) { this.toDate = toDate; }

    @JsonProperty("Status")
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @JsonProperty("Category")
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
