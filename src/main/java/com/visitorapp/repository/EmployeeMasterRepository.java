package com.visitorapp.repository;

import com.visitorapp.entity.EmployeeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeMasterRepository extends JpaRepository<EmployeeMaster, Integer> {
    List<EmployeeMaster> findByIsActiveAndIsDeleted(Boolean isActive, Boolean isDeleted);

    Optional<EmployeeMaster> findByUserNameAndIsActiveAndIsDeleted(String userName, Boolean isActive, Boolean isDeleted);

    Optional<EmployeeMaster> findByEmpCodeAndIsActiveAndIsDeleted(String empCode, Boolean isActive, Boolean isDeleted);

    List<EmployeeMaster> findByDesignationIdAndIsActiveAndIsDeleted(Integer designationId, Boolean isActive, Boolean isDeleted);

    @Query("SELECT e FROM EmployeeMaster e WHERE e.deptName = :deptName AND e.isActive = true AND e.isDeleted = false")
    List<EmployeeMaster> findByDeptName(@Param("deptName") String deptName);

    @Query("SELECT e FROM EmployeeMaster e WHERE e.deptName = :deptName AND e.designationId = :designationId AND e.isActive = true AND e.isDeleted = false")
    List<EmployeeMaster> findByDeptNameAndDesignationId(@Param("deptName") String deptName, @Param("designationId") Integer designationId);

    @Query("SELECT DISTINCT e.deptName FROM EmployeeMaster e WHERE e.deptName IS NOT NULL AND e.isActive = true AND e.isDeleted = false ORDER BY e.deptName")
    List<String> findDistinctDeptNames();

    @Query("SELECT DISTINCT e.deptName FROM EmployeeMaster e WHERE e.compId = :compId AND e.deptName IS NOT NULL AND e.isActive = true AND e.isDeleted = false ORDER BY e.deptName")
    List<String> findDistinctDeptNamesByComp(@Param("compId") Integer compId);

    @Query("SELECT e FROM EmployeeMaster e WHERE e.isActive = true AND e.isDeleted = false")
    List<EmployeeMaster> findAllActive();

    @Query("SELECT e FROM EmployeeMaster e WHERE e.compId = :compId AND e.isActive = true AND e.isDeleted = false")
    List<EmployeeMaster> findByCompId(@Param("compId") Integer compId);

    @Query("SELECT e FROM EmployeeMaster e WHERE e.compId = :compId AND e.deptName = :deptName AND e.isActive = true AND e.isDeleted = false")
    List<EmployeeMaster> findByCompIdAndDeptName(@Param("compId") Integer compId, @Param("deptName") String deptName);

    Optional<EmployeeMaster> findByEmailIdAndIsActive(String emailId, Boolean isActive);

    @Query("SELECT e FROM EmployeeMaster e WHERE e.plantId = :plantId AND e.isActive = true AND e.isDeleted = false ORDER BY e.deptName, e.designationId, e.firstName")
    List<EmployeeMaster> findByPlantId(@Param("plantId") Integer plantId);

    @Query("SELECT e FROM EmployeeMaster e WHERE e.plantId = :plantId AND e.deptName = :deptName AND e.isActive = true AND e.isDeleted = false ORDER BY e.designationId, e.firstName")
    List<EmployeeMaster> findByPlantIdAndDeptName(@Param("plantId") Integer plantId, @Param("deptName") String deptName);

    @Query("SELECT e FROM EmployeeMaster e WHERE e.plantId = :plantId AND e.designationId = :designationId AND e.isActive = true AND e.isDeleted = false ORDER BY e.firstName")
    List<EmployeeMaster> findByPlantIdAndDesignationId(@Param("plantId") Integer plantId, @Param("designationId") Integer designationId);

    @Query("SELECT DISTINCT e.deptName FROM EmployeeMaster e WHERE e.plantId = :plantId AND e.deptName IS NOT NULL AND e.isActive = true AND e.isDeleted = false ORDER BY e.deptName")
    List<String> findDistinctDeptNamesByPlant(@Param("plantId") Integer plantId);
}
