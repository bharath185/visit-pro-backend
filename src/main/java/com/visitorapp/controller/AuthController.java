package com.visitorapp.controller;

import com.visitorapp.dto.LoginViewModel;
import com.visitorapp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginViewModel> login(@RequestBody LoginViewModel model) {
        return ResponseEntity.ok(authService.login(model));
    }

    @PostMapping("/validate")
    public ResponseEntity<LoginViewModel> validateToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @PostMapping("/change-password")
    public ResponseEntity<LoginViewModel> changePassword(@RequestHeader("Authorization") String token,
                                                         @RequestBody ChangePasswordRequest req) {
        LoginViewModel validation = authService.validateToken(token);
        if (!validation.getSuccess()) {
            return ResponseEntity.ok(validation);
        }
        return ResponseEntity.ok(authService.changePassword(validation.getEmpId(), req.getOldPassword(), req.getNewPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.registerAdmin(
            req.getUserName(), req.getPassword(), req.getFirstName(),
            req.getLastName(), req.getEmailId(), req.getMobileNo(),
            req.getEmpCode(), req.getDeptName(), req.getDesignationName()
        ));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        return ResponseEntity.ok(authService.forgotPassword(req.getUserName(), req.getEmailId()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetPasswordRequest req) {
        return ResponseEntity.ok(authService.resetPassword(req.getOtp(), req.getNewPassword()));
    }

    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String v) { this.oldPassword = v; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String v) { this.newPassword = v; }
    }

    public static class RegisterRequest {
        private String userName;
        private String password;
        private String firstName;
        private String lastName;
        private String emailId;
        private String mobileNo;
        private String empCode;
        private String deptName;
        private String designationName;
        public String getUserName() { return userName; }
        public void setUserName(String v) { this.userName = v; }
        public String getPassword() { return password; }
        public void setPassword(String v) { this.password = v; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String v) { this.firstName = v; }
        public String getLastName() { return lastName; }
        public void setLastName(String v) { this.lastName = v; }
        public String getEmailId() { return emailId; }
        public void setEmailId(String v) { this.emailId = v; }
        public String getMobileNo() { return mobileNo; }
        public void setMobileNo(String v) { this.mobileNo = v; }
        public String getEmpCode() { return empCode; }
        public void setEmpCode(String v) { this.empCode = v; }
        public String getDeptName() { return deptName; }
        public void setDeptName(String v) { this.deptName = v; }
        public String getDesignationName() { return designationName; }
        public void setDesignationName(String v) { this.designationName = v; }
    }

    public static class ForgotPasswordRequest {
        private String userName;
        private String emailId;
        public String getUserName() { return userName; }
        public void setUserName(String v) { this.userName = v; }
        public String getEmailId() { return emailId; }
        public void setEmailId(String v) { this.emailId = v; }
    }

    public static class ResetPasswordRequest {
        private String otp;
        private String newPassword;
        public String getOtp() { return otp; }
        public void setOtp(String v) { this.otp = v; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String v) { this.newPassword = v; }
    }
}
