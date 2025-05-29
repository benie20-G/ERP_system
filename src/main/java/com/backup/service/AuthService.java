package com.backup.service;

import com.backup.dto.AuthRequest;
import com.backup.dto.AuthResponse;
import com.backup.dto.EmployeeDTO;
import com.backup.model.Employee;
import com.backup.repository.EmployeeRepository;
import com.backup.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthResponse authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Employee employee = (Employee) authentication.getPrincipal();

        if (!employee.isEnabled()) {
            throw new RuntimeException("Account is not active or not verified");
        }

        String token = jwtUtil.generateToken(employee);

        return AuthResponse.builder()
                .token(token)
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .roles(employee.getRoles())
                .code(employee.getCode())
                .status(employee.getStatus())
                .build();
    }

    @Transactional
    public void register(EmployeeDTO employeeDTO) {
        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Generate UUID for code
        String code = UUID.randomUUID().toString();

        // Set default values
        String roles = "ROLE_EMPLOYEE";
        String status = "active";

        Employee employee = Employee.builder()
                .code(code)
                .firstName(employeeDTO.getFirstName())
                .lastName(employeeDTO.getLastName())
                .email(employeeDTO.getEmail())
                .password(passwordEncoder.encode(employeeDTO.getPassword()))
                .roles(roles)
                .mobile(employeeDTO.getMobile())
                .dateOfBirth(employeeDTO.getDateOfBirth())
                .status(status)
                .verified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationTokenExpiry(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours
                .build();

        employeeRepository.save(employee);

        emailService.sendVerificationEmail(
                employee.getEmail(),
                employee.getFirstName(),
                employee.getVerificationToken()
        );
    }

    @Transactional
    public void verifyAccount(String token) {
        Employee employee = employeeRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (employee.getVerificationTokenExpiry().before(new Date())) {
            throw new RuntimeException("Verification token has expired");
        }

        employee.setVerified(true);
        employee.setVerificationToken(null);
        employee.setVerificationTokenExpiry(null);

        employeeRepository.save(employee);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();
        employee.setResetPasswordToken(token);
        employee.setResetPasswordTokenExpiry(new Date(System.currentTimeMillis() + 60 * 60 * 1000)); // 1 hour

        employeeRepository.save(employee);

        emailService.sendPasswordResetEmail(
                employee.getEmail(),
                employee.getFirstName(),
                token
        );
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        Employee employee = employeeRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (employee.getResetPasswordTokenExpiry().before(new Date())) {
            throw new RuntimeException("Reset token has expired");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setResetPasswordToken(null);
        employee.setResetPasswordTokenExpiry(null);

        employeeRepository.save(employee);
    }

    public Employee getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Employee) {
            return (Employee) principal;
        }

        return null;
    }
}
