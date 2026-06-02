package com.visitorapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GradeMaster")
public class GradeMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GradeId") private Integer gradeId;
    @Column(name = "Grade") private String grade;
    @Column(name = "IsActive") private Boolean isActive;

    public Integer getGradeId() { return gradeId; }
    public void setGradeId(Integer v) { this.gradeId = v; }
    public String getGrade() { return grade; }
    public void setGrade(String v) { this.grade = v; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean v) { this.isActive = v; }
}
