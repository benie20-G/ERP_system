package com.backup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "employments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private Double baseSalary;

    @Column(nullable = false)
    private String status; // active, inactive

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date joiningDate;
}