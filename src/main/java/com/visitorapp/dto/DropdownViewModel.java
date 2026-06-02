package com.visitorapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DropdownViewModel {
    @JsonProperty("Id") private Integer id;
    @JsonProperty("Name") private String name;
    @JsonProperty("Code") private String code;

    public DropdownViewModel() {}
    public DropdownViewModel(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
    public Integer getId() { return id; }
    public void setId(Integer v) { this.id = v; }
    public String getName() { return name; }
    public void setName(String v) { this.name = v; }
    public String getCode() { return code; }
    public void setCode(String v) { this.code = v; }
}
