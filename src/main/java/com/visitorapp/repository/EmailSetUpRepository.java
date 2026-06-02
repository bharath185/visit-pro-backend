package com.visitorapp.repository;

import com.visitorapp.entity.EmailSetUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailSetUpRepository extends JpaRepository<EmailSetUp, Integer> {
    Optional<EmailSetUp> findFirstByOrderById();
}
