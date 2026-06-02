package com.visitorapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CategoryMaster")
public class CategoryMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CategoryId") private Integer categoryId;
    @Column(name = "CategoryName") private String categoryName;
    @Column(name = "IsActive") private Boolean isActive;
    @Column(name = "IsDeleted") private Boolean isDeleted;

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer v) { this.categoryId = v; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String v) { this.categoryName = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean v) { this.isDeleted = v; }
}
