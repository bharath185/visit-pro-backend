package com.visitorapp.repository;

import com.visitorapp.entity.DeptMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeptMasterRepository extends JpaRepository<DeptMaster, Integer> {
    List<DeptMaster> findByIsDeletedOrderByDeptName(Boolean isDeleted);
    List<DeptMaster> findByIsActiveAndIsDeleted(Boolean isActive, Boolean isDeleted);
    List<DeptMaster> findByCompIdAndIsDeleted(Integer compId, Boolean isDeleted);
    List<DeptMaster> findByCompIdAndIsActiveAndIsDeleted(Integer compId, Boolean isActive, Boolean isDeleted);
}
