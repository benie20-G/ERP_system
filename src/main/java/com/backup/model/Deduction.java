package com.backup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deductions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Deduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(name = "deduction_name", nullable = false, unique = true)
    private String deductionName;

    @Column(nullable = false)
    private Double percentage;
    
    @Column(nullable = false)
    private boolean active;
    
    @Column(nullable = false)
    private boolean appliedToBaseSalary;
}