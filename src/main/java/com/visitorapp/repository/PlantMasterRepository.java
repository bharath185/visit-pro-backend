package com.visitorapp.repository;

import com.visitorapp.entity.PlantMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantMasterRepository extends JpaRepository<PlantMaster, Integer> {
    List<PlantMaster> findByCompIdAndIsActiveAndIsDeleted(Integer compId, Boolean isActive, Boolean isDeleted);
    List<PlantMaster> findByLocationIdAndIsActiveAndIsDeleted(Integer locationId, Boolean isActive, Boolean isDeleted);
    List<PlantMaster> findByCompIdAndLocationIdAndIsActiveAndIsDeleted(Integer compId, Integer locationId, Boolean isActive, Boolean isDeleted);
    Optional<PlantMaster> findByPlantAdminIdAndIsActiveAndIsDeleted(Integer plantAdminId, Boolean isActive, Boolean isDeleted);
    List<PlantMaster> findByIsActiveAndIsDeleted(Boolean isActive, Boolean isDeleted);
}
