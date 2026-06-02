package com.visitorapp.service;

import com.visitorapp.entity.NotificationMaster;
import com.visitorapp.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepo;

    public NotificationService(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public List<NotificationMaster> getMyNotifications(Integer empId) {
        return notificationRepo.findByEmpIdAndIsDeletedOrderByCreatedDateDesc(empId, false);
    }

    public Long getUnreadCount(Integer empId) {
        return notificationRepo.countByEmpIdAndIsReadAndIsDeleted(empId, false, false);
    }

    public List<NotificationMaster> getUnreadNotifications(Integer empId) {
        return notificationRepo.findUnreadByEmpId(empId);
    }

    @Transactional
    public NotificationMaster markAsRead(Integer id) {
        return notificationRepo.findById(id).map(n -> {
            n.setIsRead(true);
            n.setIsActive(true);
            return notificationRepo.save(n);
        }).orElse(null);
    }

    @Transactional
    public void markAllAsRead(Integer empId) {
        List<NotificationMaster> unread = notificationRepo.findUnreadByEmpId(empId);
        for (NotificationMaster n : unread) {
            n.setIsRead(true);
            notificationRepo.save(n);
        }
    }

    @Transactional
    public NotificationMaster toggleStar(Integer id) {
        return notificationRepo.findById(id).map(n -> {
            n.setIsStarred(n.getIsStarred() == null || !n.getIsStarred());
            return notificationRepo.save(n);
        }).orElse(null);
    }

    @Transactional
    public void deleteNotification(Integer id) {
        notificationRepo.findById(id).ifPresent(n -> {
            n.setIsDeleted(true);
            n.setIsActive(false);
            notificationRepo.save(n);
        });
    }

    public NotificationMaster createNotification(Integer empId, String title, String message, String type, Integer relatedId) {
        NotificationMaster n = new NotificationMaster();
        n.setEmpId(empId);
        n.setTitle(title);
        n.setMessage(message);
        n.setType(type);
        n.setRelatedId(relatedId);
        n.setIsRead(false);
        n.setIsStarred(false);
        n.setIsActive(true);
        n.setIsDeleted(false);
        n.setCreatedDate(new Date());
        return notificationRepo.save(n);
    }

    @Transactional
    public void cleanupOldNotifications(int daysOld) {
        Date cutoff = new Date(System.currentTimeMillis() - (long) daysOld * 24 * 60 * 60 * 1000);
        List<NotificationMaster> all = notificationRepo.findAll();
        for (NotificationMaster n : all) {
            if (n.getCreatedDate() != null && n.getCreatedDate().before(cutoff)) {
                n.setIsDeleted(true);
                n.setIsActive(false);
                notificationRepo.save(n);
            }
        }
    }
}