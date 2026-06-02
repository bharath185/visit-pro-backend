package com.visitorapp.controller;

import com.visitorapp.config.AuthContext;
import com.visitorapp.entity.EmailSetUp;
import com.visitorapp.repository.EmailSetUpRepository;
import com.visitorapp.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/email-setup")
public class EmailSetupController {

    private final EmailSetUpRepository repository;
    private final EmailService emailService;

    public EmailSetupController(EmailSetUpRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<EmailSetUp> getConfig() {
        if (!AuthContext.isSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        EmailSetUp config = repository.findFirstByOrderById().orElse(new EmailSetUp());
        if (config.getSmtpPassword() != null && !config.getSmtpPassword().isEmpty()) {
            config.setSmtpPassword("********");
        }
        return ResponseEntity.ok(config);
    }

    @GetMapping("/all")
    public ResponseEntity<List<EmailSetUp>> getAll() {
        if (!AuthContext.isSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<EmailSetUp> save(@RequestBody EmailSetUp model) {
        if (!AuthContext.isSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        if (model.getId() != null) {
            EmailSetUp existing = repository.findById(model.getId()).orElse(null);
            if (existing != null) {
                existing.setCompId(model.getCompId());
                existing.setEmailId(model.getEmailId());
                existing.setSmtpServer(model.getSmtpServer());
                existing.setSmtpPort(model.getSmtpPort());
                existing.setSmtpMailId(model.getSmtpMailId());
                if (model.getSmtpPassword() != null && !model.getSmtpPassword().isBlank()) {
                    existing.setSmtpPassword(model.getSmtpPassword());
                }
                return ResponseEntity.ok(repository.save(existing));
            }
        }
        return ResponseEntity.ok(repository.save(model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Integer id) {
        if (!AuthContext.isSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("Msg", "Deleted successfully"));
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> testEmail(@RequestBody Map<String, String> body) {
        if (!AuthContext.isSuperAdmin()) {
            return ResponseEntity.status(403).build();
        }
        String to = body.get("email");
        if (to == null || to.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("Msg", "Email is required"));
        }
        try {
            emailService.sendTestEmail(to);
            return ResponseEntity.ok(Map.of("Msg", "Test email sent successfully to " + to));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("Msg", "Failed: " + e.getMessage()));
        }
    }
}
