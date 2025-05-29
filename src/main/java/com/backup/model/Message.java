package com.backup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "month_year", nullable = false)
    private String monthYear;

    @Column(name = "sent_status", nullable = false)
    private String sentStatus; // SENT, PENDING, FAILED

    @ManyToOne
    @JoinColumn(name = "pay_slip_id")
    private PaySlip paySlip;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "sent_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentAt;
    
    @Column(name = "retry_count")
    private Integer retryCount;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        if (retryCount == null) {
            retryCount = 0;
        }
    }
}