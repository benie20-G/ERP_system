package com.backup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    private Long id;
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    private String employeeCode;
    private String employeeName;
    private String employeeEmail;
    
    @NotBlank(message = "Message content is required")
    private String message;
    
    @NotBlank(message = "Month/Year is required")
    private String monthYear;
    
    @NotBlank(message = "Sent status is required")
    @Pattern(regexp = "^(SENT|PENDING|FAILED)$", message = "Sent status should be either 'SENT', 'PENDING', or 'FAILED'")
    private String sentStatus;
    
    private Long paySlipId;
    
    private Date createdAt;
    private Date sentAt;
    private Integer retryCount;
    private String errorMessage;
}