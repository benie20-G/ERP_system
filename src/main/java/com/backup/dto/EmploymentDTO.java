package com.backup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDTO {

    private Long id;
    
    @NotBlank(message = "Employment code is required")
    private String code;
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private String employeeCode;
    
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotBlank(message = "Position is required")
    private String position;
    
    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be positive")
    private Double baseSalary;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(active|inactive)$", message = "Status should be either 'active' or 'inactive'")
    private String status;
    
    @NotNull(message = "Joining date is required")
    private Date joiningDate;
    
    // Additional fields for response
    private String employeeName;
    private String employeeEmail;
}