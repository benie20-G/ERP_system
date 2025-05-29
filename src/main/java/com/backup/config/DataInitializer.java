package com.backup.config;

import com.backup.model.Employee;
import com.backup.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if it doesn't exist
        if (!employeeRepository.existsByEmail("irasubizabella@gmail.com")) {
            Employee admin = Employee.builder()
                    .code(UUID.randomUUID().toString())
                    .firstName("Bella")
                    .lastName("Irasubi")
                    .email("irasubizabella@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .roles("ROLE_ADMIN")
                    .status("active")
                    .verified(true)
                    .build();
            
            employeeRepository.save(admin);
            log.info("Admin user created: {}", admin.getEmail());
        }
        
        // Create manager user if it doesn't exist
        if (!employeeRepository.existsByEmail("irerabrigitte@gmail.com")) {
            Employee manager = Employee.builder()
                    .code(UUID.randomUUID().toString())
                    .firstName("Brigitte")
                    .lastName("Irera")
                    .email("irerabrigitte@gmail.com")
                    .password(passwordEncoder.encode("password"))
                    .roles("ROLE_MANAGER")
                    .status("active")
                    .verified(true)
                    .build();
            
            employeeRepository.save(manager);
            log.info("Manager user created: {}", manager.getEmail());
        }
    }
}