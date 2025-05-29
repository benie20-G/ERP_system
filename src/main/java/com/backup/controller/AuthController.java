package com.backup.controller;

import com.backup.dto.AuthRequest;
import com.backup.dto.AuthResponse;
import com.backup.dto.EmployeeDTO;
import com.backup.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Authenticate user", description = "Authenticates a user with email and password and returns a JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "403", description = "Account is not active or not verified")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody AuthRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        AuthResponse response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register new employee", description = "Registers a new employee in the system and sends a verification email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Employee registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email or employee code already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Parameter(description = "Employee details", required = true)
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("Registration request received for email: {}", employeeDTO.getEmail());
        authService.register(employeeDTO);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee registered successfully. Verification email sent.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Verify account", description = "Verifies a user account using the token sent via email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired verification token")
    })
    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyAccount(
            @Parameter(description = "Verification token received via email", required = true)
            @RequestParam String token) {
        log.info("Account verification request received with token: {}", token);
        authService.verifyAccount(token);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Account verified successfully.");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Request password reset", description = "Sends a password reset email to the specified email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent if the email exists"),
        @ApiResponse(responseCode = "404", description = "User not found with the provided email")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Parameter(description = "Email address of the user", required = true)
            @RequestParam String email) {
        log.info("Password reset request received for email: {}", email);
        authService.requestPasswordReset(email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset email sent if the email exists.");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Reset password", description = "Resets the user's password using the token sent via email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired reset token")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Parameter(description = "Reset token received via email", required = true)
            @RequestParam String token,
            @Parameter(description = "New password", required = true)
            @RequestParam String newPassword) {
        log.info("Password reset execution request received with token: {}", token);
        authService.resetPassword(token, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully.");
        return ResponseEntity.ok(response);
    }
}
