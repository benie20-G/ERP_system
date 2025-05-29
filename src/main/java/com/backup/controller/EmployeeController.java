package com.backup.controller;

import com.backup.dto.EmployeeDTO;
import com.backup.service.EmployeeService;
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
@Tag(name = "Employee Management", description = "APIs for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Get all employees", description = "Retrieves a list of all employees in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of employees",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    })
    @GetMapping("/admin/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        log.info("Request to get all employees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Operation(summary = "Get employee by ID", description = "Retrieves an employee by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the employee",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Employee not found with the given ID")
    })
    @GetMapping("/admin/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> getEmployeeById(
            @Parameter(description = "ID of the employee to retrieve", required = true)
            @PathVariable Long id) {
        log.info("Request to get employee with id: {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @Operation(summary = "Get employee by code", description = "Retrieves an employee by their unique code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the employee",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Employee not found with the given code")
    })
    @GetMapping("/admin/employees/code/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> getEmployeeByCode(
            @Parameter(description = "Unique code of the employee to retrieve", required = true)
            @PathVariable String code) {
        log.info("Request to get employee with code: {}", code);
        return ResponseEntity.ok(employeeService.getEmployeeByCode(code));
    }

    @Operation(summary = "Get current employee profile", description = "Retrieves the profile of the currently authenticated employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the employee profile",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN, MANAGER, or EMPLOYEE role")
    })
    @GetMapping("/employee/profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<EmployeeDTO> getCurrentEmployee() {
        log.info("Request to get current employee profile");
        return ResponseEntity.ok(employeeService.getCurrentEmployee());
    }

    @Operation(summary = "Update employee", description = "Updates an employee's information by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the employee",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Employee not found with the given ID"),
        @ApiResponse(responseCode = "409", description = "Email or employee code already exists")
    })
    @PutMapping("/admin/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @Parameter(description = "ID of the employee to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated employee details", required = true)
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("Request to update employee with id: {}", id);
        return ResponseEntity.ok(employeeService.updateEmployee(id, employeeDTO));
    }

    @Operation(summary = "Delete employee", description = "Deletes an employee by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted the employee"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Employee not found with the given ID")
    })
    @DeleteMapping("/admin/employees/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteEmployee(
            @Parameter(description = "ID of the employee to delete", required = true)
            @PathVariable Long id) {
        log.info("Request to delete employee with id: {}", id);
        employeeService.deleteEmployee(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee deleted successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update current employee profile", description = "Updates the profile of the currently authenticated employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the employee profile",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN, MANAGER, or EMPLOYEE role"),
        @ApiResponse(responseCode = "409", description = "Email or employee code already exists")
    })
    @PutMapping("/employee/update-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<EmployeeDTO> updateCurrentEmployee(
            @Parameter(description = "Updated employee details", required = true)
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("Request to update current employee profile");
        EmployeeDTO currentEmployee = employeeService.getCurrentEmployee();
        return ResponseEntity.ok(employeeService.updateEmployee(currentEmployee.getId(), employeeDTO));
    }

    @Operation(summary = "Change employee role", description = "Changes the role of an employee by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully changed the employee role",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Employee not found with the given ID")
    })
    @PutMapping("/admin/employees/{id}/change-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> changeEmployeeRole(
            @Parameter(description = "ID of the employee to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "New role(s) for the employee", required = true)
            @RequestParam String roles) {
        log.info("Request to change role for employee with id: {} to {}", id, roles);
        return ResponseEntity.ok(employeeService.changeRole(id, roles));
    }
}
