package com.visitorapp.controller;

import com.visitorapp.entity.NotificationMaster;
import com.visitorapp.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/my/{empId}")
    public ResponseEntity<List<NotificationMaster>> getMyNotifications(@PathVariable Integer empId) {
        return ResponseEntity.ok(notificationService.getMyNotifications(empId));
    }

    @GetMapping("/unread-count/{empId}")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable Integer empId) {
        Long count = notificationService.getUnreadCount(empId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/unread/{empId}")
    public ResponseEntity<List<NotificationMaster>> getUnread(@PathVariable Integer empId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(empId));
    }

    @PostMapping("/mark-read/{id}")
    public ResponseEntity<NotificationMaster> markAsRead(@PathVariable Integer id) {
        NotificationMaster result = notificationService.markAsRead(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @PostMapping("/mark-all-read/{empId}")
    public ResponseEntity<Map<String, String>> markAllAsRead(@PathVariable Integer empId) {
        notificationService.markAllAsRead(empId);
        return ResponseEntity.ok(Map.of("Msg", "All notifications marked as read"));
    }

    @PostMapping("/toggle-star/{id}")
    public ResponseEntity<NotificationMaster> toggleStar(@PathVariable Integer id) {
        NotificationMaster result = notificationService.toggleStar(id);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(Map.of("Msg", "Notification deleted"));
    }

    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, String>> cleanup(@RequestBody Map<String, Integer> body) {
        int days = body.getOrDefault("days", 30);
        notificationService.cleanupOldNotifications(days);
        return ResponseEntity.ok(Map.of("Msg", "Old notifications cleaned up"));
    }
}