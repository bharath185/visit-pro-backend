package com.visitorapp.repository;

import com.visitorapp.entity.VisitorManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface VisitorManagementRepository extends JpaRepository<VisitorManagement, Integer> {

    List<VisitorManagement> findByIsActiveAndIsDeletedOrderByVisitIdDesc(Boolean isActive, Boolean isDeleted);

    List<VisitorManagement> findByCreatedByAndIsDeletedOrderByVisitDateDesc(Integer createdBy, Boolean isDeleted);

    List<VisitorManagement> findByCreatedByAndIsDeletedOrderByCreatedDateDesc(Integer createdBy, Boolean isDeleted);

    List<VisitorManagement> findByWhomToMeetAndIsDeleted(Integer whomToMeet, Boolean isDeleted);

    @Query("SELECT v FROM VisitorManagement v WHERE v.isDeleted = false AND v.visitDate = :today ORDER BY v.visitId DESC")
    List<VisitorManagement> findTodayVisitors(@Param("today") Date today);

    @Query("SELECT v FROM VisitorManagement v WHERE v.isDeleted = false AND v.visitDate BETWEEN :from AND :to ORDER BY v.visitId DESC")
    List<VisitorManagement> findByDateRange(@Param("from") Date from, @Param("to") Date to);

    @Query("SELECT COUNT(v) FROM VisitorManagement v WHERE v.isDeleted = false AND v.visitDate = :today")
    Long countTodayVisitors(@Param("today") Date today);

    @Query("SELECT COUNT(v) FROM VisitorManagement v WHERE v.isDeleted = false AND v.checkIn IS NOT NULL AND v.visitDate = :today")
    Long countTodayCheckIns(@Param("today") Date today);

    @Query("SELECT COUNT(v) FROM VisitorManagement v WHERE v.isDeleted = false AND v.checkOut IS NOT NULL AND v.visitDate = :today")
    Long countTodayCheckOuts(@Param("today") Date today);

    List<VisitorManagement> findByIsDeletedOrderByVisitIdDesc(Boolean isDeleted);

    List<VisitorManagement> findByVisitDateBeforeAndIsDeleted(Date date, Boolean isDeleted);

    @Query("SELECT v FROM VisitorManagement v WHERE v.plantId = :plantId AND v.isDeleted = false ORDER BY v.visitId DESC")
    List<VisitorManagement> findByPlantId(@Param("plantId") Integer plantId);

    @Query("SELECT v FROM VisitorManagement v WHERE v.plantId = :plantId AND v.createdBy = :createdBy AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitorManagement> findByPlantIdAndCreatedBy(@Param("plantId") Integer plantId, @Param("createdBy") Integer createdBy);

    @Query("SELECT v FROM VisitorManagement v WHERE v.plantId = :plantId AND v.whomToMeet = :whomToMeet AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitorManagement> findByPlantIdAndWhomToMeet(@Param("plantId") Integer plantId, @Param("whomToMeet") Integer whomToMeet);

    @Query("SELECT v FROM VisitorManagement v WHERE v.plantId = :plantId AND v.isDeleted = false AND v.visitDate = :today ORDER BY v.visitId DESC")
    List<VisitorManagement> findTodayVisitorsByPlant(@Param("plantId") Integer plantId, @Param("today") Date today);

    @Query("SELECT COUNT(v) FROM VisitorManagement v WHERE v.plantId = :plantId AND v.isDeleted = false AND v.visitDate = :today")
    Long countTodayVisitorsByPlant(@Param("plantId") Integer plantId, @Param("today") Date today);

    @Query("SELECT COUNT(v) FROM VisitorManagement v WHERE v.plantId = :plantId AND v.isDeleted = false AND v.checkIn IS NOT NULL AND v.visitDate = :today")
    Long countTodayCheckInsByPlant(@Param("plantId") Integer plantId, @Param("today") Date today);

    @Query("SELECT COUNT(v) FROM VisitorManagement v WHERE v.plantId = :plantId AND v.isDeleted = false AND v.checkOut IS NOT NULL AND v.visitDate = :today")
    Long countTodayCheckOutsByPlant(@Param("plantId") Integer plantId, @Param("today") Date today);

    @Query("SELECT v FROM VisitorManagement v WHERE v.createdBy IN :creatorIds AND v.invited = false AND v.isDeleted = false ORDER BY v.createdDate DESC")
    List<VisitorManagement> findPendingApprovals(@Param("creatorIds") List<Integer> creatorIds);

    @Query("SELECT v FROM VisitorManagement v WHERE v.plantId = :plantId AND v.isDeleted = false AND (:createdBy IS NULL OR v.createdBy = :createdBy) AND (:whomToMeet IS NULL OR v.whomToMeet = :whomToMeet) AND (:from IS NULL OR v.visitDate >= :from) AND (:to IS NULL OR v.visitDate <= :to) ORDER BY v.visitDate DESC")
    List<VisitorManagement> findByPlantIdAndFilters(@Param("plantId") Integer plantId, @Param("createdBy") Integer createdBy, @Param("whomToMeet") Integer whomToMeet, @Param("from") Date from, @Param("to") Date to);

    // Direct check-in queries (using @Query because 'directCheckIn' conflicts with JPA 'In' keyword)
    @Query("SELECT v FROM VisitorManagement v WHERE v.whomToMeet = :whomToMeet AND v.directCheckIn = true AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitorManagement> findDirectCheckInsByContact(@Param("whomToMeet") Integer whomToMeet);

    @Query("SELECT v FROM VisitorManagement v WHERE v.whomToMeet = :whomToMeet AND v.directCheckIn = true AND v.approved = :approved AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitorManagement> findDirectCheckInsByContactAndApproved(@Param("whomToMeet") Integer whomToMeet, @Param("approved") Boolean approved);

    @Query("SELECT v FROM VisitorManagement v WHERE v.plantId = :plantId AND v.whomToMeet = :whomToMeet AND v.directCheckIn = true AND v.isDeleted = false ORDER BY v.visitDate DESC")
    List<VisitorManagement> findDirectCheckInsByPlantAndContact(@Param("plantId") Integer plantId, @Param("whomToMeet") Integer whomToMeet);
}
