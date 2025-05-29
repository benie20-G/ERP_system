package com.backup.controller;

import com.backup.dto.EmploymentDTO;
import com.backup.service.EmploymentService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employment Management", description = "APIs for managing employee employments")
public class EmploymentController {

    private final EmploymentService employmentService;

    @GetMapping("/manager/employments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmploymentDTO>> getAllEmployments() {
        log.info("Request to get all employments");
        return ResponseEntity.ok(employmentService.getAllEmployments());
    }

    @GetMapping("/manager/employments/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmploymentDTO>> getActiveEmployments() {
        log.info("Request to get all active employments");
        return ResponseEntity.ok(employmentService.getActiveEmployments());
    }

    @GetMapping("/manager/employments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentDTO> getEmploymentById(@PathVariable Long id) {
        log.info("Request to get employment with id: {}", id);
        return ResponseEntity.ok(employmentService.getEmploymentById(id));
    }

    @GetMapping("/manager/employments/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentDTO> getEmploymentByCode(@PathVariable String code) {
        log.info("Request to get employment with code: {}", code);
        return ResponseEntity.ok(employmentService.getEmploymentByCode(code));
    }

    @GetMapping("/manager/employments/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmploymentDTO>> getEmploymentsByEmployee(@PathVariable Long employeeId) {
        log.info("Request to get employments for employee with id: {}", employeeId);
        return ResponseEntity.ok(employmentService.getEmploymentsByEmployee(employeeId));
    }

    @GetMapping("/manager/employments/employee/{employeeId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<EmploymentDTO>> getActiveEmploymentsByEmployee(@PathVariable Long employeeId) {
        log.info("Request to get active employments for employee with id: {}", employeeId);
        return ResponseEntity.ok(employmentService.getActiveEmploymentsByEmployee(employeeId));
    }

    @GetMapping("/employee/employments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<EmploymentDTO>> getCurrentEmployeeEmployments() {
        log.info("Request to get employments for current employee");
        return ResponseEntity.ok(employmentService.getCurrentEmployeeEmployments());
    }

    @PostMapping("/manager/employments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentDTO> createEmployment(@Valid @RequestBody EmploymentDTO employmentDTO) {
        log.info("Request to create employment for employee with id: {}", employmentDTO.getEmployeeId());
        EmploymentDTO result = employmentService.createEmployment(employmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/manager/employments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<EmploymentDTO> updateEmployment(
            @PathVariable Long id,
            @Valid @RequestBody EmploymentDTO employmentDTO) {
        log.info("Request to update employment with id: {}", id);
        return ResponseEntity.ok(employmentService.updateEmployment(id, employmentDTO));
    }

    @DeleteMapping("/manager/employments/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, String>> deleteEmployment(@PathVariable Long id) {
        log.info("Request to delete employment with id: {}", id);
        employmentService.deleteEmployment(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Employment deleted successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get employments with filtering and pagination", description = "Retrieves a paginated list of employments with filtering options")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the filtered and paginated list of employments",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmploymentDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @GetMapping("/manager/employments/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<EmploymentDTO>> getEmploymentsWithFiltering(
            @Parameter(description = "Filter parameters (e.g., employeeId, department, position, status, etc.)")
            @RequestParam Map<String, String> filters,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = "ASC") String sortDir) {
        log.info("Request to get employments with filtering: {}", filters);

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(employmentService.getEmploymentsWithFiltering(filters, pageable));
    }
}
