package com.backup.controller;

import com.backup.dto.PaySlipDTO;
import com.backup.service.PaySlipService;
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
@Tag(name = "Pay Slip Management", description = "APIs for managing employee pay slips")
public class PaySlipController {

    private final PaySlipService paySlipService;

    @Operation(summary = "Get all pay slips", description = "Retrieves a list of all pay slips in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @GetMapping("/manager/payslips")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PaySlipDTO>> getAllPaySlips() {
        log.info("Request to get all pay slips");
        return ResponseEntity.ok(paySlipService.getAllPaySlips());
    }

    @Operation(summary = "Get pay slips by status", description = "Retrieves a list of pay slips with the specified status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @GetMapping("/manager/payslips/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PaySlipDTO>> getPaySlipsByStatus(
            @Parameter(description = "Status of the pay slips to retrieve (pending or paid)", required = true)
            @PathVariable String status) {
        log.info("Request to get pay slips with status: {}", status);
        return ResponseEntity.ok(paySlipService.getPaySlipsByStatus(status));
    }

    @Operation(summary = "Get pay slips by month and year", description = "Retrieves a list of pay slips for the specified month and year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @GetMapping("/manager/payslips/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PaySlipDTO>> getPaySlipsByMonthAndYear(
            @Parameter(description = "Month (1-12)", required = true)
            @PathVariable String month,
            @Parameter(description = "Year (e.g., 2025)", required = true)
            @PathVariable Integer year) {
        log.info("Request to get pay slips for month: {} and year: {}", month, year);
        return ResponseEntity.ok(paySlipService.getPaySlipsByMonthAndYear(month, year));
    }

    @Operation(summary = "Get pay slips by month, year, and status", description = "Retrieves a list of pay slips for the specified month, year, and status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @GetMapping("/manager/payslips/month/{month}/year/{year}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PaySlipDTO>> getPaySlipsByMonthAndYearAndStatus(
            @Parameter(description = "Month (1-12)", required = true)
            @PathVariable String month,
            @Parameter(description = "Year (e.g., 2025)", required = true)
            @PathVariable Integer year,
            @Parameter(description = "Status of the pay slips (pending or paid)", required = true)
            @PathVariable String status) {
        log.info("Request to get pay slips for month: {}, year: {}, and status: {}", month, year, status);
        return ResponseEntity.ok(paySlipService.getPaySlipsByMonthAndYearAndStatus(month, year, status));
    }

    @Operation(summary = "Get pay slip by ID", description = "Retrieves a pay slip by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the pay slip",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "Pay slip not found with the given ID")
    })
    @GetMapping("/manager/payslips/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PaySlipDTO> getPaySlipById(
            @Parameter(description = "ID of the pay slip to retrieve", required = true)
            @PathVariable Long id) {
        log.info("Request to get pay slip with id: {}", id);
        return ResponseEntity.ok(paySlipService.getPaySlipById(id));
    }

    @Operation(summary = "Get pay slips by employee ID", description = "Retrieves a list of pay slips for the specified employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "Employee not found with the given ID")
    })
    @GetMapping("/manager/payslips/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PaySlipDTO>> getPaySlipsByEmployee(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable Long employeeId) {
        log.info("Request to get pay slips for employee with id: {}", employeeId);
        return ResponseEntity.ok(paySlipService.getPaySlipsByEmployee(employeeId));
    }

    @Operation(summary = "Get pay slips by employee ID and status", description = "Retrieves a list of pay slips for the specified employee with the given status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "Employee not found with the given ID")
    })
    @GetMapping("/manager/payslips/employee/{employeeId}/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PaySlipDTO>> getPaySlipsByEmployeeAndStatus(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable Long employeeId,
            @Parameter(description = "Status of the pay slips (pending or paid)", required = true)
            @PathVariable String status) {
        log.info("Request to get pay slips for employee with id: {} and status: {}", employeeId, status);
        return ResponseEntity.ok(paySlipService.getPaySlipsByEmployeeAndStatus(employeeId, status));
    }

    @Operation(summary = "Get pay slip by employee ID, month, and year", description = "Retrieves a specific pay slip for the specified employee, month, and year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the pay slip",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "Pay slip not found for the given employee, month, and year")
    })
    @GetMapping("/manager/payslips/employee/{employeeId}/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<PaySlipDTO> getPaySlipByEmployeeAndMonthAndYear(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable Long employeeId,
            @Parameter(description = "Month (1-12)", required = true)
            @PathVariable String month,
            @Parameter(description = "Year (e.g., 2025)", required = true)
            @PathVariable Integer year) {
        log.info("Request to get pay slip for employee with id: {}, month: {}, and year: {}", employeeId, month, year);
        return ResponseEntity.ok(paySlipService.getPaySlipByEmployeeAndMonthAndYear(employeeId, month, year));
    }

    @Operation(summary = "Get current employee pay slips", description = "Retrieves a list of pay slips for the currently authenticated employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN, MANAGER, or EMPLOYEE role")
    })
    @GetMapping("/employee/payslips")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<PaySlipDTO>> getCurrentEmployeePaySlips() {
        log.info("Request to get pay slips for current employee");
        return ResponseEntity.ok(paySlipService.getCurrentEmployeePaySlips());
    }

    @Operation(summary = "Get current employee pay slips by year", description = "Retrieves a list of pay slips for the currently authenticated employee for the specified year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN, MANAGER, or EMPLOYEE role")
    })
    @GetMapping("/employee/payslips/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<PaySlipDTO>> getCurrentEmployeePaySlipsByYear(
            @Parameter(description = "Year (e.g., 2025)", required = true)
            @PathVariable Integer year) {
        log.info("Request to get pay slips for current employee for year: {}", year);
        return ResponseEntity.ok(paySlipService.getCurrentEmployeePaySlipsByYear(year));
    }

    @Operation(summary = "Generate pay slips", description = "Generates pay slips for all active employees for the specified month and year")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully generated pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input or pay slips already exist for some employees"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "No active employments or deductions found")
    })
    @PostMapping("/manager/payslips/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<PaySlipDTO>> generatePaySlips(
            @Parameter(description = "Month (1-12)", required = true)
            @RequestParam String month,
            @Parameter(description = "Year (e.g., 2025)", required = true)
            @RequestParam Integer year) {
        log.info("Request to generate pay slips for month: {} and year: {}", month, year);
        List<PaySlipDTO> generatedPaySlips = paySlipService.generatePaySlips(month, year);
        return ResponseEntity.status(HttpStatus.CREATED).body(generatedPaySlips);
    }

    @Operation(summary = "Approve pay slip", description = "Approves a specific pay slip by changing its status from 'pending' to 'paid' and sends a notification to the employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully approved the pay slip",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "400", description = "Pay slip is already approved"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Pay slip not found with the given ID")
    })
    @PutMapping("/admin/payslips/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaySlipDTO> approvePaySlip(
            @Parameter(description = "ID of the pay slip to approve", required = true)
            @PathVariable Long id) {
        log.info("Request to approve pay slip with id: {}", id);
        return ResponseEntity.ok(paySlipService.approvePaySlip(id));
    }

    @Operation(summary = "Approve all pay slips for a month and year", description = "Approves all pending pay slips for the specified month and year and sends notifications to the employees")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully approved all pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "No pending pay slips found for the specified month and year")
    })
    @PutMapping("/admin/payslips/approve/month/{month}/year/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaySlipDTO>> approveAllPaySlips(
            @Parameter(description = "Month (1-12)", required = true)
            @PathVariable String month,
            @Parameter(description = "Year (e.g., 2025)", required = true)
            @PathVariable Integer year) {
        log.info("Request to approve all pay slips for month: {} and year: {}", month, year);
        return ResponseEntity.ok(paySlipService.approveAllPaySlips(month, year));
    }

    @Operation(summary = "Get pay slips with filtering and pagination", description = "Retrieves a paginated list of pay slips with filtering options")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the filtered and paginated list of pay slips",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaySlipDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @GetMapping("/manager/payslips/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Page<PaySlipDTO>> getPaySlipsWithFiltering(
            @Parameter(description = "Filter parameters (e.g., employeeId, status, month, year, etc.)")
            @RequestParam Map<String, String> filters,
            @Parameter(description = "Page number (zero-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = "ASC") String sortDir) {
        log.info("Request to get pay slips with filtering: {}", filters);

        Sort.Direction direction = sortDir.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return ResponseEntity.ok(paySlipService.getPaySlipsWithFiltering(filters, pageable));
    }
}
