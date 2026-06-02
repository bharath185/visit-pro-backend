package com.visitorapp.repository;

import com.visitorapp.entity.VisitPurposeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitPurposeMasterRepository extends JpaRepository<VisitPurposeMaster, Integer> {
    List<VisitPurposeMaster> findByIsActiveAndIsDeleted(Boolean isActive, Boolean isDeleted);
    Optional<VisitPurposeMaster> findByPurposeNameAndIsActiveAndIsDeleted(String purposeName, Boolean isActive, Boolean isDeleted);
}
