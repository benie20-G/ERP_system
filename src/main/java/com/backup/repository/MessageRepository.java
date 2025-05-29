package com.backup.repository;

import com.backup.model.Employee;
import com.backup.model.Message;
import com.backup.model.PaySlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByEmployee(Employee employee);
    
    List<Message> findByEmployeeAndMonthYear(Employee employee, String monthYear);
    
    List<Message> findByMonthYear(String monthYear);
    
    List<Message> findBySentStatus(String sentStatus);
    
    List<Message> findByPaySlip(PaySlip paySlip);
    
    boolean existsByPaySlip(PaySlip paySlip);
}