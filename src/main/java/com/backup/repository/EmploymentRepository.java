package com.backup.repository;

import com.backup.model.Employee;
import com.backup.model.Employment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long>, JpaSpecificationExecutor<Employment> {

    List<Employment> findByEmployee(Employee employee);

    List<Employment> findByEmployeeAndStatus(Employee employee, String status);

    Optional<Employment> findByCode(String code);

    boolean existsByCode(String code);

    List<Employment> findByStatus(String status);
}
