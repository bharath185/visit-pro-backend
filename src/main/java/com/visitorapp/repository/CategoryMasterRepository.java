package com.visitorapp.repository;

import com.visitorapp.entity.CategoryMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryMasterRepository extends JpaRepository<CategoryMaster, Integer> {
    List<CategoryMaster> findByIsActiveAndIsDeleted(Boolean isActive, Boolean isDeleted);
}
