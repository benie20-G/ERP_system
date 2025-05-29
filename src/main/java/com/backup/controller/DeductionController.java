package com.backup.controller;

import com.backup.dto.DeductionDTO;
import com.backup.service.DeductionService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Deduction Management", description = "APIs for managing salary deductions")
public class DeductionController {

    private final DeductionService deductionService;

    @Operation(summary = "Get all deductions", description = "Retrieves a list of all deductions in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of deductions",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeductionDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @GetMapping("/manager/deductions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<DeductionDTO>> getAllDeductions() {
        log.info("Request to get all deductions");
        return ResponseEntity.ok(deductionService.getAllDeductions());
    }

    @Operation(summary = "Get active deductions", description = "Retrieves a list of all active deductions in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of active deductions",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeductionDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @GetMapping("/manager/deductions/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<DeductionDTO>> getActiveDeductions() {
        log.info("Request to get all active deductions");
        return ResponseEntity.ok(deductionService.getActiveDeductions());
    }

    @Operation(summary = "Get active base salary deductions", description = "Retrieves a list of all active deductions that are applied to base salary")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of active base salary deductions",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeductionDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @GetMapping("/manager/deductions/active/base-salary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<DeductionDTO>> getActiveBaseSalaryDeductions() {
        log.info("Request to get all active base salary deductions");
        return ResponseEntity.ok(deductionService.getActiveBaseSalaryDeductions());
    }

    @Operation(summary = "Get deduction by ID", description = "Retrieves a deduction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the deduction",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeductionDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "Deduction not found with the given ID")
    })
    @GetMapping("/manager/deductions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionDTO> getDeductionById(
            @Parameter(description = "ID of the deduction to retrieve", required = true)
            @PathVariable Long id) {
        log.info("Request to get deduction with id: {}", id);
        return ResponseEntity.ok(deductionService.getDeductionById(id));
    }

    @Operation(summary = "Get deduction by code", description = "Retrieves a deduction by its unique code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the deduction",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeductionDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "Deduction not found with the given code")
    })
    @GetMapping("/manager/deductions/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionDTO> getDeductionByCode(
            @Parameter(description = "Unique code of the deduction to retrieve", required = true)
            @PathVariable String code) {
        log.info("Request to get deduction with code: {}", code);
        return ResponseEntity.ok(deductionService.getDeductionByCode(code));
    }

    @Operation(summary = "Get deduction by name", description = "Retrieves a deduction by its name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the deduction",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeductionDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "Deduction not found with the given name")
    })
    @GetMapping("/manager/deductions/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionDTO> getDeductionByName(
            @Parameter(description = "Name of the deduction to retrieve", required = true)
            @PathVariable String name) {
        log.info("Request to get deduction with name: {}", name);
        return ResponseEntity.ok(deductionService.getDeductionByName(name));
    }

    @Operation(summary = "Create deduction", description = "Creates a new deduction in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created the deduction",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeductionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "409", description = "Deduction code or name already exists")
    })
    @PostMapping("/manager/deductions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionDTO> createDeduction(
            @Parameter(description = "Deduction details", required = true)
            @Valid @RequestBody DeductionDTO deductionDTO) {
        log.info("Request to create deduction with name: {}", deductionDTO.getDeductionName());
        DeductionDTO result = deductionService.createDeduction(deductionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Update deduction", description = "Updates a deduction's information by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated the deduction",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeductionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "Deduction not found with the given ID"),
        @ApiResponse(responseCode = "409", description = "Deduction code or name already exists")
    })
    @PutMapping("/manager/deductions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<DeductionDTO> updateDeduction(
            @Parameter(description = "ID of the deduction to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated deduction details", required = true)
            @Valid @RequestBody DeductionDTO deductionDTO) {
        log.info("Request to update deduction with id: {}", id);
        return ResponseEntity.ok(deductionService.updateDeduction(id, deductionDTO));
    }

    @Operation(summary = "Delete deduction", description = "Deletes a deduction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted the deduction"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role"),
        @ApiResponse(responseCode = "404", description = "Deduction not found with the given ID")
    })
    @DeleteMapping("/manager/deductions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, String>> deleteDeduction(
            @Parameter(description = "ID of the deduction to delete", required = true)
            @PathVariable Long id) {
        log.info("Request to delete deduction with id: {}", id);
        deductionService.deleteDeduction(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Deduction deleted successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Initialize default deductions", description = "Creates the default set of deductions in the system if they don't already exist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully initialized default deductions"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN or MANAGER role")
    })
    @PostMapping("/manager/deductions/initialize")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Map<String, String>> initializeDefaultDeductions() {
        log.info("Request to initialize default deductions");
        deductionService.initializeDefaultDeductions();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Default deductions initialized successfully");
        return ResponseEntity.ok(response);
    }
}
