package com.visitorapp.config;

import com.visitorapp.entity.EmployeeMaster;
import com.visitorapp.repository.EmployeeMasterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final EmployeeMasterRepository employeeRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-username:ADMIN}")
    private String adminUsername;

    @Value("${app.seed.admin-password:Admin@123}")
    private String adminPassword;

    @Value("${app.seed.admin-email:admin@company.com}")
    private String adminEmail;

    public DataSeeder(EmployeeMasterRepository employeeRepo, PasswordEncoder passwordEncoder) {
        this.employeeRepo = employeeRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (employeeRepo.count() > 0) {
            log.info("Users exist, skipping seed");
            return;
        }

        log.info("No users found. Creating default admin user...");

        EmployeeMaster admin = new EmployeeMaster();
        admin.setUserName(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setEmpCode(adminUsername);
        admin.setFirstName("Super");
        admin.setLastName("Admin");
        admin.setEmailId(adminEmail);
        admin.setMobileNo("0000000000");
        admin.setDeptName("Administration");
        admin.setDesignationName("Super Admin");
        admin.setIsAdminUser(true);
        admin.setIsActive(true);
        admin.setIsDeleted(false);

        employeeRepo.save(admin);
        log.info("Default admin user created: username={}, password={}", adminUsername, adminPassword);
    }
}
