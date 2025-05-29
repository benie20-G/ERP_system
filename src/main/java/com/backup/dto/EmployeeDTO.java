package com.backup.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class EmployeeDTO {

    private Long id;

    private String code;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;



    private String password;

    private String roles;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Mobile number should be valid")
    private String mobile;

    private Date dateOfBirth;

    @Pattern(regexp = "^(active|disabled)$", message = "Status should be either 'active' or 'disabled'")
    private String status;
}
