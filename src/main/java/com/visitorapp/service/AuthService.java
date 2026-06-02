package com.visitorapp.service;

import com.visitorapp.dto.LoginViewModel;
import com.visitorapp.entity.EmployeeMaster;
import com.visitorapp.entity.PlantMaster;
import com.visitorapp.entity.SessionMaster;
import com.visitorapp.repository.EmployeeMasterRepository;
import com.visitorapp.repository.PlantMasterRepository;
import com.visitorapp.repository.SessionMasterRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final SecretKey key;
    private final long jwtExpirationMs;
    private final EmployeeMasterRepository employeeRepo;
    private final SessionMasterRepository sessionRepo;
    private final PlantMasterRepository plantRepo;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Backward compatibility: OTP-based reset for external callers of /reset-password
    private static final ConcurrentHashMap<String, ResetEntry> resetTokens = new ConcurrentHashMap<>();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Brute force protection: track failed login attempts
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    private static final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    // Legacy salt constant for backward-compatible password verification
    private static final String LEGACY_SALT = "RimIndia@123";

    public AuthService(
            @Value("${app.jwt.secret}") String jwtSecret,
            @Value("${app.jwt.expiration-ms}") long jwtExpirationMs,
            EmployeeMasterRepository employeeRepo,
            SessionMasterRepository sessionRepo,
            PlantMasterRepository plantRepo,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
        this.employeeRepo = employeeRepo;
        this.sessionRepo = sessionRepo;
        this.plantRepo = plantRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public LoginViewModel login(LoginViewModel model) {
        LoginViewModel result = new LoginViewModel();
        String username = model.getUsername() != null ? model.getUsername().trim() : "";
        String password = model.getPassword() != null ? model.getPassword().trim() : "";

        if (username.isEmpty() || password.isEmpty()) {
            result.setSuccess(false);
            result.setMsg("Username and password are required");
            return result;
        }

        // Check brute force lockout
        if (isAccountLocked(username)) {
            result.setSuccess(false);
            result.setMsg("Account temporarily locked due to too many failed attempts. Try again later.");
            return result;
        }

        // Find user by username or employee code
        Optional<EmployeeMaster> empOpt = employeeRepo.findByUserNameAndIsActiveAndIsDeleted(username, true, false);
        if (empOpt.isEmpty()) {
            empOpt = employeeRepo.findByEmpCodeAndIsActiveAndIsDeleted(username, true, false);
        }

        if (empOpt.isEmpty()) {
            recordFailedAttempt(username);
            result.setSuccess(false);
            result.setMsg("Invalid username or password");
            return result;
        }

        EmployeeMaster emp = empOpt.get();

        // Verify password - support both BCrypt (new) and legacy Base64 (old) format
        if (!verifyPassword(password, emp.getPassword())) {
            recordFailedAttempt(username);
            result.setSuccess(false);
            result.setMsg("Invalid username or password");
            return result;
        }

        // Clear failed attempts on successful login
        clearFailedAttempts(username);

        // Upgrade legacy password to BCrypt on successful login
        if (!isBcryptHash(emp.getPassword())) {
            String bcryptHash = passwordEncoder.encode(password);
            emp.setPassword(bcryptHash);
            employeeRepo.save(emp);
            log.info("Upgraded password to BCrypt for user: {}", username);
        }

        // Generate JWT with expiration
        long now = System.currentTimeMillis();
        String token = Jwts.builder()
            .claim("user", emp.getUserName() != null ? emp.getUserName() : emp.getEmpCode())
            .claim("empId", emp.getEmpId())
            .claim("iat", now)
            .claim("exp", now + jwtExpirationMs)
            .signWith(key)
            .compact();

        String authKey = UUID.randomUUID().toString();

        SessionMaster session = new SessionMaster();
        session.setUsername(emp.getUserName() != null ? emp.getUserName() : emp.getEmpCode());
        session.setTockenId(token);
        session.setAuthKey(authKey);
        session.setRoleId(0);
        session.setStatus(true);
        session.setExpired(false);
        session.setCreatedDate(new Date());
        session.setIsActive(true);
        session.setIsDeleted(false);
        sessionRepo.save(session);

        String fullName = (emp.getFirstName() != null ? emp.getFirstName() : "");
        if (emp.getMiddleName() != null && !emp.getMiddleName().isEmpty()) fullName += " " + emp.getMiddleName();
        if (emp.getLastName() != null && !emp.getLastName().isEmpty()) fullName += " " + emp.getLastName();

        result.setSuccess(true);
        result.setEmpId(emp.getEmpId());
        result.setEmpCode(emp.getEmpCode());
        result.setCompId(emp.getCompId());
        result.setFirstName(emp.getFirstName());
        result.setMiddleName(emp.getMiddleName());
        result.setLastName(emp.getLastName());
        result.setFullName(fullName.trim());
        result.setEmailId(emp.getEmailId());
        result.setMobileNo(emp.getMobileNo());
        result.setDesignationId(emp.getDesignationId());
        result.setDesignationName(emp.getDesignationName());
        result.setDeptName(emp.getDeptName());

        // Set plant information
        Integer plantId = emp.getPlantId();
        result.setPlantId(plantId);
        result.setIsPlantAdmin(false);
        if (plantId != null) {
            Optional<PlantMaster> plantOpt = plantRepo.findById(plantId);
            if (plantOpt.isPresent()) {
                PlantMaster plant = plantOpt.get();
                result.setPlantName(plant.getPlantName());
                if (plant.getPlantAdminId() != null && plant.getPlantAdminId().equals(emp.getEmpId())) {
                    result.setIsPlantAdmin(true);
                }
            }
        }

        result.setPhoto(emp.getPhoto());
        result.setToken(token);
        result.setIsAdmin(emp.getIsAdminUser() != null && emp.getIsAdminUser());
        result.setIsSecurity(emp.getIsSecurity() != null && emp.getIsSecurity());
        result.setReportId(emp.getReportId());
        result.setMustChangePassword(emp.getIsTempPassword() != null && emp.getIsTempPassword());
        result.setMsg("Login successful");

        // IMPORTANT: Never return the password hash to the client
        result.setPassword(null);

        return result;
    }

    /**
     * Verify a password against the stored hash.
     * Supports both BCrypt (new) and legacy Base64(UTF16LE(password+salt)) format.
     */
    private boolean verifyPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null) return false;

        if (isBcryptHash(storedPassword)) {
            // BCrypt verification
            return passwordEncoder.matches(rawPassword, storedPassword);
        } else {
            // Legacy format: Base64 encoded UTF16LE(password + salt)
            try {
                String legacyHash = Base64.getEncoder().encodeToString(
                    (rawPassword + LEGACY_SALT).getBytes(StandardCharsets.UTF_16LE)
                );
                return legacyHash.equals(storedPassword);
            } catch (Exception e) {
                log.warn("Legacy password verification failed", e);
                return false;
            }
        }
    }

    private boolean isBcryptHash(String password) {
        return password != null && (password.startsWith("$2a$") || password.startsWith("$2b$") || password.startsWith("$2y$"));
    }

    public LoginViewModel changePassword(Integer empId, String oldPassword, String newPassword) {
        LoginViewModel result = new LoginViewModel();
        
        if (oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            result.setSuccess(false);
            result.setMsg("Old password and new password are required");
            return result;
        }

        if (newPassword.length() < 6) {
            result.setSuccess(false);
            result.setMsg("New password must be at least 6 characters");
            return result;
        }

        Optional<EmployeeMaster> empOpt = employeeRepo.findById(empId);
        if (empOpt.isEmpty()) {
            result.setSuccess(false);
            result.setMsg("User not found");
            return result;
        }

        EmployeeMaster emp = empOpt.get();

        if (!verifyPassword(oldPassword, emp.getPassword())) {
            result.setSuccess(false);
            result.setMsg("Current password is incorrect");
            return result;
        }

        // Use BCrypt for new password
        emp.setPassword(passwordEncoder.encode(newPassword));
        emp.setIsTempPassword(false);
        employeeRepo.save(emp);

        result.setSuccess(true);
        result.setMsg("Password changed successfully");
        return result;
    }

    public LoginViewModel validateToken(String token) {
        LoginViewModel result = new LoginViewModel();
        try {
            if (token == null || token.isBlank()) {
                result.setSuccess(false);
                result.setMsg("No token provided");
                return result;
            }

            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            var claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            Integer empId = claims.get("empId", Integer.class);

            // Check token expiration
            Long exp = claims.get("exp", Long.class);
            if (exp != null && System.currentTimeMillis() > exp) {
                result.setSuccess(false);
                result.setMsg("Token has expired");
                return result;
            }

            Optional<SessionMaster> sessionOpt = sessionRepo.findByTockenIdAndStatus(token, true);
            if (sessionOpt.isEmpty() || Boolean.TRUE.equals(sessionOpt.get().getExpired())) {
                result.setSuccess(false);
                result.setMsg("Session expired");
                return result;
            }

            Optional<EmployeeMaster> empOpt = employeeRepo.findById(empId);
            if (empOpt.isEmpty()) {
                result.setSuccess(false);
                result.setMsg("User not found");
                return result;
            }

            EmployeeMaster emp = empOpt.get();
            String fullName = (emp.getFirstName() != null ? emp.getFirstName() : "");
            if (emp.getLastName() != null && !emp.getLastName().isEmpty()) fullName += " " + emp.getLastName();

            result.setSuccess(true);
            result.setEmpId(emp.getEmpId());
            result.setEmpCode(emp.getEmpCode());
            result.setFirstName(emp.getFirstName());
            result.setFullName(fullName.trim());
            result.setEmailId(emp.getEmailId());
            result.setDesignationId(emp.getDesignationId());
            result.setDesignationName(emp.getDesignationName());
            result.setDeptName(emp.getDeptName());
            result.setToken(token);
            return result;
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMsg("Invalid token");
            return result;
        }
    }

    public Map<String, Object> registerAdmin(String userName, String password, String firstName,
                                               String lastName, String emailId, String mobileNo,
                                               String empCode, String deptName, String designationName) {
        Map<String, Object> result = new HashMap<>();

        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            result.put("Success", false);
            result.put("Msg", "Username and password are required");
            return result;
        }

        if (password.trim().length() < 6) {
            result.put("Success", false);
            result.put("Msg", "Password must be at least 6 characters");
            return result;
        }

        Optional<EmployeeMaster> existing = employeeRepo.findByUserNameAndIsActiveAndIsDeleted(userName.trim(), true, false);
        if (existing.isPresent()) {
            result.put("Success", false);
            result.put("Msg", "Username already exists");
            return result;
        }

        if (empCode != null && !empCode.trim().isEmpty()) {
            Optional<EmployeeMaster> existingCode = employeeRepo.findByEmpCodeAndIsActiveAndIsDeleted(empCode.trim(), true, false);
            if (existingCode.isPresent()) {
                result.put("Success", false);
                result.put("Msg", "Employee code already exists");
                return result;
            }
        }

        // Use BCrypt for password storage (not legacy Base64)
        String encodedPassword = passwordEncoder.encode(password.trim());

        EmployeeMaster emp = new EmployeeMaster();
        emp.setUserName(userName.trim());
        emp.setPassword(encodedPassword);
        emp.setEmpCode(empCode != null ? empCode.trim() : userName.trim());
        emp.setFirstName(firstName != null ? firstName.trim() : userName.trim());
        emp.setLastName(lastName != null ? lastName.trim() : "");
        emp.setEmailId(emailId != null ? emailId.trim() : "");
        emp.setMobileNo(mobileNo != null ? mobileNo.trim() : "");
        emp.setDeptName(deptName != null ? deptName.trim() : "Administration");
        emp.setDesignationName(designationName != null ? designationName.trim() : "Admin");
        emp.setIsActive(true);
        emp.setIsDeleted(false);

        employeeRepo.save(emp);

        result.put("Success", true);
        result.put("Msg", "Admin user created successfully");
        result.put("EmpId", emp.getEmpId());
        result.put("UserName", emp.getUserName());
        result.put("EmpCode", emp.getEmpCode());
        result.put("FullName", (emp.getFirstName() != null ? emp.getFirstName() : "") + 
                    (emp.getLastName() != null && !emp.getLastName().isEmpty() ? " " + emp.getLastName() : ""));
        return result;
    }

    public Map<String, Object> forgotPassword(String userName, String emailId) {
        Map<String, Object> result = new HashMap<>();
        if ((userName == null || userName.trim().isEmpty()) && (emailId == null || emailId.trim().isEmpty())) {
            result.put("Success", false);
            result.put("Msg", "Please provide username or email");
            return result;
        }

        Optional<EmployeeMaster> empOpt = Optional.empty();
        if (userName != null && !userName.trim().isEmpty()) {
            empOpt = employeeRepo.findByUserNameAndIsActiveAndIsDeleted(userName.trim(), true, false);
            if (empOpt.isEmpty()) {
                empOpt = employeeRepo.findByEmpCodeAndIsActiveAndIsDeleted(userName.trim(), true, false);
            }
        }
        if (empOpt.isEmpty() && emailId != null && !emailId.trim().isEmpty()) {
            empOpt = employeeRepo.findByEmailIdAndIsActive(emailId.trim(), true);
        }

        if (empOpt.isEmpty()) {
            result.put("Success", false);
            result.put("Msg", "User not found");
            return result;
        }

        EmployeeMaster emp = empOpt.get();

        if (emp.getEmailId() == null || emp.getEmailId().trim().isEmpty()) {
            result.put("Success", false);
            result.put("Msg", "No email registered for this user. Contact your administrator.");
            return result;
        }

        // Generate temp password
        String tempPassword = generateTempPassword();
        emp.setPassword(passwordEncoder.encode(tempPassword));
        emp.setIsTempPassword(true);
        employeeRepo.save(emp);

        // Send email with temp password
        String fullName = (emp.getFirstName() != null ? emp.getFirstName() : emp.getUserName());
        emailService.sendTempPasswordEmail(emp.getEmailId(), fullName, emp.getUserName(), tempPassword);

        result.put("Success", true);
        result.put("Msg", "A temporary password has been sent to your registered email.");
        result.put("Email", emp.getEmailId() != null ? emp.getEmailId().replaceAll("(?<=.{2}).(?=@)", "*") : "");
        return result;
    }

    public Map<String, Object> resetPassword(String otp, String newPassword) {
        Map<String, Object> result = new HashMap<>();
        if (otp == null || otp.trim().isEmpty()) {
            result.put("Success", false);
            result.put("Msg", "OTP is required");
            return result;
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            result.put("Success", false);
            result.put("Msg", "New password is required");
            return result;
        }

        if (newPassword.trim().length() < 6) {
            result.put("Success", false);
            result.put("Msg", "Password must be at least 6 characters");
            return result;
        }

        ResetEntry entry = resetTokens.get(otp.trim());
        if (entry == null) {
            result.put("Success", false);
            result.put("Msg", "Invalid or expired OTP");
            return result;
        }
        if (System.currentTimeMillis() > entry.expiry) {
            resetTokens.remove(otp.trim());
            result.put("Success", false);
            result.put("Msg", "OTP has expired. Please request a new one.");
            return result;
        }

        Optional<EmployeeMaster> empOpt = employeeRepo.findById(entry.empId);
        if (empOpt.isEmpty()) {
            result.put("Success", false);
            result.put("Msg", "User not found");
            return result;
        }

        EmployeeMaster emp = empOpt.get();

        // Use BCrypt for new password
        emp.setPassword(passwordEncoder.encode(newPassword.trim()));
        employeeRepo.save(emp);
        resetTokens.remove(otp.trim());

        result.put("Success", true);
        result.put("Msg", "Password reset successfully. You can now log in with your new password.");
        return result;
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static class ResetEntry {
        Integer empId;
        String emailId;
        long expiry;
        ResetEntry(Integer empId, String emailId, long expiry) {
            this.empId = empId;
            this.emailId = emailId;
            this.expiry = expiry;
        }
    }

    private static class LoginAttempt {
        int count;
        long lastAttempt;
        LoginAttempt() {
            this.count = 0;
            this.lastAttempt = System.currentTimeMillis();
        }
    }

    private boolean isAccountLocked(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        if (attempt == null) return false;
        if (System.currentTimeMillis() - attempt.lastAttempt > LOCKOUT_DURATION_MS) {
            loginAttempts.remove(username);
            return false;
        }
        return attempt.count >= MAX_FAILED_ATTEMPTS;
    }

    private void recordFailedAttempt(String username) {
        LoginAttempt attempt = loginAttempts.computeIfAbsent(username, k -> new LoginAttempt());
        attempt.count++;
        attempt.lastAttempt = System.currentTimeMillis();
        log.warn("Failed login attempt {} for user: {}", attempt.count, username);
        if (attempt.count >= MAX_FAILED_ATTEMPTS) {
            log.warn("Account locked due to brute force: {}", username);
        }
    }

    private void clearFailedAttempts(String username) {
        loginAttempts.remove(username);
    }
}
