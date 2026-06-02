package com.visitorapp.repository;

import com.visitorapp.entity.NotificationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationMaster, Integer> {

    List<NotificationMaster> findByEmpIdAndIsDeletedOrderByCreatedDateDesc(Integer empId, Boolean isDeleted);

    Long countByEmpIdAndIsReadAndIsDeleted(Integer empId, Boolean isRead, Boolean isDeleted);

    @Query("SELECT n FROM NotificationMaster n WHERE n.empId = :empId AND n.isDeleted = false AND n.type = :type ORDER BY n.createdDate DESC")
    List<NotificationMaster> findByEmpIdAndType(@Param("empId") Integer empId, @Param("type") String type);

    @Query("SELECT n FROM NotificationMaster n WHERE n.empId = :empId AND n.isDeleted = false AND n.isRead = false ORDER BY n.createdDate DESC")
    List<NotificationMaster> findUnreadByEmpId(@Param("empId") Integer empId);
}