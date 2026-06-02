package com.visitorapp.repository;

import com.visitorapp.entity.SessionMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SessionMasterRepository extends JpaRepository<SessionMaster, Integer> {
    Optional<SessionMaster> findByTockenIdAndStatus(String tockenId, Boolean status);
    Optional<SessionMaster> findByUsernameAndStatusAndExpired(String username, Boolean status, Boolean expired);
}
