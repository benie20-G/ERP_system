package com.backup.repository;

import com.backup.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    Optional<Employee> findByCode(String code);
    
    boolean existsByEmail(String email);
    
    boolean existsByCode(String code);
    
    Optional<Employee> findByVerificationToken(String token);
    
    Optional<Employee> findByResetPasswordToken(String token);
}