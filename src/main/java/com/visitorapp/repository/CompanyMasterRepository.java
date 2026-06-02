package com.visitorapp.repository;

import com.visitorapp.entity.CompanyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompanyMasterRepository extends JpaRepository<CompanyMaster, Integer> {
    List<CompanyMaster> findByIsDeletedOrderByCompany(Boolean isDeleted);
    List<CompanyMaster> findByIsActiveAndIsDeleted(Boolean isActive, Boolean isDeleted);
}
