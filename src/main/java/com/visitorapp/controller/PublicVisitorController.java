package com.visitorapp.controller;

import com.visitorapp.dto.VisitorManagementViewModel;
import com.visitorapp.service.VisitorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicVisitorController {

    private final VisitorService visitorService;

    public PublicVisitorController(VisitorService visitorService) {
        this.visitorService = visitorService;
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Object> verifyOTP(@RequestBody Map<String, String> body) {
        String otp = body.getOrDefault("OTP", "").trim();
        if (otp.isEmpty() || otp.length() > 10) {
            return ResponseEntity.badRequest().body(Map.of("Msg", "Invalid OTP format"));
        }
        return ResponseEntity.ok(visitorService.verifyOTP(otp));
    }

    @PostMapping("/verify-checkin-otp")
    public ResponseEntity<Object> verifyCheckInOTP(@RequestBody Map<String, String> body) {
        String otp = body.getOrDefault("OTP", "").trim();
        if (otp.isEmpty() || otp.length() > 10) {
            return ResponseEntity.badRequest().body(Map.of("Msg", "Invalid OTP format"));
        }
        return ResponseEntity.ok(visitorService.verifyCheckInOTP(otp));
    }

    @PostMapping("/accept-invite")
    public ResponseEntity<VisitorManagementViewModel> acceptInvite(@RequestBody VisitorManagementViewModel model) {
        if (model == null) {
            return ResponseEntity.badRequest().body(new VisitorManagementViewModel());
        }
        return ResponseEntity.ok(visitorService.acceptInvite(model));
    }

    @PostMapping("/self-checkin-verify")
    public ResponseEntity<Object> selfCheckInVerify(@RequestBody Map<String, String> body) {
        String otp = body.getOrDefault("OTP", "").trim();
        if (otp.isEmpty() || otp.length() > 10) {
            return ResponseEntity.badRequest().body(Map.of("Msg", "Invalid OTP format"));
        }
        return ResponseEntity.ok(visitorService.selfCheckInVerify(otp));
    }
}
