package com.visitorapp.repository;

import com.visitorapp.entity.VisitorInviteHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitorInviteHistoryRepository extends JpaRepository<VisitorInviteHistory, Integer> {
    Optional<VisitorInviteHistory> findByVisitorIdAndIsDeleted(Integer visitorId, Boolean isDeleted);
    Optional<VisitorInviteHistory> findFirstByVisitorIdAndIsDeletedOrderByLastUpdatedDateDesc(Integer visitorId, Boolean isDeleted);
    Optional<VisitorInviteHistory> findByInviteCodeAndIsDeleted(String inviteCode, Boolean isDeleted);
    Optional<VisitorInviteHistory> findByCheckInCodeAndIsDeleted(String checkInCode, Boolean isDeleted);
    Optional<VisitorInviteHistory> findByCheckoutCodeAndIsDeleted(String checkoutCode, Boolean isDeleted);
}
