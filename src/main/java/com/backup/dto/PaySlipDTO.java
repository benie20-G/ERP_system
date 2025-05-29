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
public class PaySlipDTO {

    private Long id;
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private String employeeCode;
    private String employeeName;
    private String employeeEmail;
    
    private Double houseAmount;
    private Double transportAmount;
    private Double employeeTaxedAmount;
    private Double pensionAmount;
    private Double medicalInsuranceAmount;
    private Double otherTaxedAmount;
    private Double grossSalary;
    private Double netSalary;
    
    @NotBlank(message = "Month is required")
    @Pattern(regexp = "^(0?[1-9]|1[0-2])$", message = "Month should be between 1 and 12")
    private String month;
    
    @NotNull(message = "Year is required")
    @Positive(message = "Year must be positive")
    private Integer year;
    
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(pending|paid)$", message = "Status should be either 'pending' or 'paid'")
    private String status;
    
    private Long employmentId;
    private String department;
    private String position;
    
    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be positive")
    private Double baseSalary;
    
    private Date createdAt;
    private Date updatedAt;
    private Long createdById;
    private String createdByName;
    private Long approvedById;
    private String approvedByName;
    private Date approvedAt;
}