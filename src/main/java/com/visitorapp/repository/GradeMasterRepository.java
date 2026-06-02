package com.visitorapp.repository;

import com.visitorapp.entity.GradeMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeMasterRepository extends JpaRepository<GradeMaster, Integer> {
}
