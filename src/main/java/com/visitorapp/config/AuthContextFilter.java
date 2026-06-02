package com.visitorapp.config;

import com.visitorapp.entity.EmployeeMaster;
import com.visitorapp.entity.PlantMaster;
import com.visitorapp.repository.EmployeeMasterRepository;
import com.visitorapp.repository.PlantMasterRepository;
import com.visitorapp.repository.SessionMasterRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Filter that parses the JWT, loads the EmployeeMaster from DB,
 * and populates the AuthContext thread-local holder before each request.
 * Runs BEFORE JwtAuthFilter in the SecurityConfig chain.
 */
@Component
public class AuthContextFilter extends OncePerRequestFilter {

    private final SecretKey key;
    private final EmployeeMasterRepository employeeRepo;
    private final SessionMasterRepository sessionRepo;
    private final PlantMasterRepository plantRepo;

    public AuthContextFilter(
            @Value("${app.jwt.secret}") String jwtSecret,
            EmployeeMasterRepository employeeRepo,
            SessionMasterRepository sessionRepo,
            PlantMasterRepository plantRepo) {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.employeeRepo = employeeRepo;
        this.sessionRepo = sessionRepo;
        this.plantRepo = plantRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || authHeader.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            // Support both "Bearer <token>" and raw token
            String token = authHeader;
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            Integer empId = claims.get("empId", Integer.class);
            if (empId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Verify session is still active
            var sessionOpt = sessionRepo.findByTockenIdAndStatus(token, true);
            if (sessionOpt.isEmpty() || Boolean.TRUE.equals(sessionOpt.get().getExpired())) {
                filterChain.doFilter(request, response);
                return;
            }

            // Verify employee exists and is active
            Optional<EmployeeMaster> empOpt = employeeRepo.findById(empId);
            if (empOpt.isEmpty() || !Boolean.TRUE.equals(empOpt.get().getIsActive())
                || Boolean.TRUE.equals(empOpt.get().getIsDeleted())) {
                filterChain.doFilter(request, response);
                return;
            }

            EmployeeMaster emp = empOpt.get();
            AuthContext.set(emp);

            // Determine if user is a PlantAdmin
            boolean isPlantAdmin = false;
            Integer plantId = emp.getPlantId();
            if (plantId != null) {
                Optional<PlantMaster> plantOpt = plantRepo.findById(plantId);
                if (plantOpt.isPresent()) {
                    PlantMaster plant = plantOpt.get();
                    if (plant.getPlantAdminId() != null && plant.getPlantAdminId().equals(emp.getEmpId())) {
                        isPlantAdmin = true;
                    }
                }
            }
            AuthContext.setPlantAdmin(isPlantAdmin);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // Token invalid or expired — still pass through (JwtAuthFilter will handle 401)
            AuthContext.clear();
            filterChain.doFilter(request, response);
        } finally {
            AuthContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/public/")
            || path.equals("/api/auth/login")
            || path.equals("/api/auth/forgot-password")
            || path.equals("/api/auth/reset-password")
            || path.equals("/api/auth/register")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/swagger-ui");
    }
}
