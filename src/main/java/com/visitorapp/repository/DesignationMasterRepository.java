package com.visitorapp.repository;

import com.visitorapp.entity.DesignationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DesignationMasterRepository extends JpaRepository<DesignationMaster, Integer> {
    List<DesignationMaster> findByIsDeletedOrderByDesignation(Boolean isDeleted);
    List<DesignationMaster> findByDeptIdAndIsDeleted(Integer deptId, Boolean isDeleted);
    List<DesignationMaster> findByIsActiveAndIsDeleted(Boolean isActive, Boolean isDeleted);
    List<DesignationMaster> findByPlantIdAndIsDeleted(Integer plantId, Boolean isDeleted);
    List<DesignationMaster> findByPlantIdAndIsActiveAndIsDeleted(Integer plantId, Boolean isActive, Boolean isDeleted);
    List<DesignationMaster> findByPlantIdOrderByDesignation(Integer plantId);
}
