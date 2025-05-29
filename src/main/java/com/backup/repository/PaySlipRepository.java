package com.backup.repository;

import com.backup.model.Employee;
import com.backup.model.Employment;
import com.backup.model.PaySlip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaySlipRepository extends JpaRepository<PaySlip, Long>, JpaSpecificationExecutor<PaySlip> {

    List<PaySlip> findByEmployee(Employee employee);

    List<PaySlip> findByEmployeeAndStatus(Employee employee, String status);

    List<PaySlip> findByStatus(String status);

    List<PaySlip> findByMonthAndYear(String month, Integer year);

    List<PaySlip> findByMonthAndYearAndStatus(String month, Integer year, String status);

    Optional<PaySlip> findByEmployeeAndMonthAndYear(Employee employee, String month, Integer year);

    boolean existsByEmployeeAndMonthAndYear(Employee employee, String month, Integer year);

    List<PaySlip> findByEmployment(Employment employment);

    List<PaySlip> findByEmployeeAndYearOrderByMonthDesc(Employee employee, Integer year);
}
