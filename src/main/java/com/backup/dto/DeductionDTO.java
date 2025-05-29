package com.backup.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductionDTO {

    private Long id;
    
    @NotBlank(message = "Deduction code is required")
    private String code;
    
    @NotBlank(message = "Deduction name is required")
    private String deductionName;
    
    @NotNull(message = "Percentage is required")
    @Min(value = 0, message = "Percentage must be at least 0")
    @Max(value = 100, message = "Percentage must be at most 100")
    private Double percentage;
    
    @NotNull(message = "Active status is required")
    private Boolean active;
    
    @NotNull(message = "Applied to base salary status is required")
    private Boolean appliedToBaseSalary;
}