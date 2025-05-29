package com.backup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pay_slips", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"employee_id", "month", "year"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaySlip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private Double houseAmount;

    @Column(nullable = false)
    private Double transportAmount;

    @Column(nullable = false)
    private Double employeeTaxedAmount;

    @Column(nullable = false)
    private Double pensionAmount;

    @Column(nullable = false)
    private Double medicalInsuranceAmount;

    @Column(nullable = false)
    private Double otherTaxedAmount;

    @Column(nullable = false)
    private Double grossSalary;

    @Column(nullable = false)
    private Double netSalary;

    @Column(nullable = false)
    private String month;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String status; // pending, paid

    @ManyToOne
    @JoinColumn(name = "employment_id")
    private Employment employment;
    
    @Column(nullable = false)
    private Double baseSalary;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private Employee createdBy;
    
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;
    
    @Column(name = "approved_at")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date approvedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new java.util.Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new java.util.Date();
    }
}