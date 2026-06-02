package com.visitorapp.repository;

import com.visitorapp.entity.LocationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LocationMasterRepository extends JpaRepository<LocationMaster, Integer> {
    List<LocationMaster> findByIsDeletedOrderByLocationName(Boolean isDeleted);
}
