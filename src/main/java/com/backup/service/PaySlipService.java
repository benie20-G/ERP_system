package com.backup.service;

import com.backup.dto.PaySlipDTO;
import com.backup.model.*;
import com.backup.repository.DeductionRepository;
import com.backup.repository.EmployeeRepository;
import com.backup.repository.EmploymentRepository;
import com.backup.repository.PaySlipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaySlipService {

    private final PaySlipRepository paySlipRepository;
    private final EmployeeRepository employeeRepository;
    private final EmploymentRepository employmentRepository;
    private final DeductionRepository deductionRepository;
    private final AuthService authService;
    private final MessageService messageService;

    public List<PaySlipDTO> getAllPaySlips() {
        return paySlipRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaySlipDTO> getPaySlipsByStatus(String status) {
        return paySlipRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaySlipDTO> getPaySlipsByMonthAndYear(String month, Integer year) {
        return paySlipRepository.findByMonthAndYear(month, year).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaySlipDTO> getPaySlipsByMonthAndYearAndStatus(String month, Integer year, String status) {
        return paySlipRepository.findByMonthAndYearAndStatus(month, year, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PaySlipDTO getPaySlipById(Long id) {
        PaySlip paySlip = paySlipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pay slip not found with id: " + id));
        return convertToDTO(paySlip);
    }

    public List<PaySlipDTO> getPaySlipsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return paySlipRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaySlipDTO> getPaySlipsByEmployeeAndStatus(Long employeeId, String status) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return paySlipRepository.findByEmployeeAndStatus(employee, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PaySlipDTO getPaySlipByEmployeeAndMonthAndYear(Long employeeId, String month, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        PaySlip paySlip = paySlipRepository.findByEmployeeAndMonthAndYear(employee, month, year)
                .orElseThrow(() -> new RuntimeException("Pay slip not found for employee id: " + employeeId + 
                        ", month: " + month + ", year: " + year));

        return convertToDTO(paySlip);
    }

    public List<PaySlipDTO> getCurrentEmployeePaySlips() {
        Employee employee = authService.getCurrentUser();
        if (employee == null) {
            throw new RuntimeException("No authenticated user found");
        }

        return paySlipRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PaySlipDTO> getCurrentEmployeePaySlipsByYear(Integer year) {
        Employee employee = authService.getCurrentUser();
        if (employee == null) {
            throw new RuntimeException("No authenticated user found");
        }

        return paySlipRepository.findByEmployeeAndYearOrderByMonthDesc(employee, year).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<PaySlipDTO> getPaySlipsWithFiltering(Map<String, String> filters, Pageable pageable) {
        Specification<PaySlip> spec = buildSpecification(filters);
        Page<PaySlip> paySlips = paySlipRepository.findAll(spec, pageable);
        return paySlips.map(this::convertToDTO);
    }

    private Specification<PaySlip> buildSpecification(Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = filters.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                    .map(entry -> {
                        String key = entry.getKey();
                        String value = entry.getValue();

                        switch (key) {
                            case "employeeId":
                                return criteriaBuilder.equal(root.get("employee").get("id"), Long.valueOf(value));
                            case "employeeCode":
                                return criteriaBuilder.equal(root.get("employee").get("code"), value);
                            case "employeeName":
                                String[] names = value.split(" ");
                                if (names.length > 1) {
                                    return criteriaBuilder.or(
                                            criteriaBuilder.like(criteriaBuilder.lower(root.get("employee").get("firstName")), "%" + names[0].toLowerCase() + "%"),
                                            criteriaBuilder.like(criteriaBuilder.lower(root.get("employee").get("lastName")), "%" + names[1].toLowerCase() + "%")
                                    );
                                } else {
                                    return criteriaBuilder.or(
                                            criteriaBuilder.like(criteriaBuilder.lower(root.get("employee").get("firstName")), "%" + value.toLowerCase() + "%"),
                                            criteriaBuilder.like(criteriaBuilder.lower(root.get("employee").get("lastName")), "%" + value.toLowerCase() + "%")
                                    );
                                }
                            case "status":
                                return criteriaBuilder.equal(root.get("status"), value);
                            case "month":
                                return criteriaBuilder.equal(root.get("month"), value);
                            case "year":
                                return criteriaBuilder.equal(root.get("year"), Integer.valueOf(value));
                            case "department":
                                return criteriaBuilder.equal(root.get("employment").get("department"), value);
                            case "position":
                                return criteriaBuilder.equal(root.get("employment").get("position"), value);
                            case "minBaseSalary":
                                return criteriaBuilder.greaterThanOrEqualTo(root.get("baseSalary"), Double.valueOf(value));
                            case "maxBaseSalary":
                                return criteriaBuilder.lessThanOrEqualTo(root.get("baseSalary"), Double.valueOf(value));
                            case "minNetSalary":
                                return criteriaBuilder.greaterThanOrEqualTo(root.get("netSalary"), Double.valueOf(value));
                            case "maxNetSalary":
                                return criteriaBuilder.lessThanOrEqualTo(root.get("netSalary"), Double.valueOf(value));
                            default:
                                return null;
                        }
                    })
                    .filter(predicate -> predicate != null)
                    .collect(Collectors.toList());

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    @Transactional
    public List<PaySlipDTO> generatePaySlips(String month, Integer year) {
        Employee currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }

        // Get all active employments
        List<Employment> activeEmployments = employmentRepository.findByStatus("active");
        if (activeEmployments.isEmpty()) {
            throw new RuntimeException("No active employments found");
        }

        // Get all active deductions
        List<Deduction> activeDeductions = deductionRepository.findByActive(true);
        if (activeDeductions.isEmpty()) {
            throw new RuntimeException("No active deductions found");
        }

        // Separate deductions applied to base salary and those that are not
        List<Deduction> baseSalaryDeductions = activeDeductions.stream()
                .filter(Deduction::isAppliedToBaseSalary)
                .collect(Collectors.toList());

        List<Deduction> nonBaseSalaryDeductions = activeDeductions.stream()
                .filter(d -> !d.isAppliedToBaseSalary())
                .collect(Collectors.toList());

        // Generate pay slips for each active employment
        List<PaySlip> generatedPaySlips = activeEmployments.stream()
                .map(employment -> generatePaySlip(employment, month, year, baseSalaryDeductions, nonBaseSalaryDeductions, currentUser))
                .collect(Collectors.toList());

        return generatedPaySlips.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaySlip generatePaySlip(Employment employment, String month, Integer year, 
                                   List<Deduction> baseSalaryDeductions, List<Deduction> nonBaseSalaryDeductions,
                                   Employee currentUser) {

        // Check if pay slip already exists for this employee, month, and year
        if (paySlipRepository.existsByEmployeeAndMonthAndYear(employment.getEmployee(), month, year)) {
            throw new RuntimeException("Pay slip already exists for employee: " + employment.getEmployee().getCode() + 
                    ", month: " + month + ", year: " + year);
        }

        Double baseSalary = employment.getBaseSalary();

        // Calculate housing and transport amounts (from non-base salary deductions)
        Double housingPercentage = nonBaseSalaryDeductions.stream()
                .filter(d -> d.getDeductionName().equalsIgnoreCase("Housing"))
                .findFirst()
                .map(Deduction::getPercentage)
                .orElse(0.0);

        Double transportPercentage = nonBaseSalaryDeductions.stream()
                .filter(d -> d.getDeductionName().equalsIgnoreCase("Transport"))
                .findFirst()
                .map(Deduction::getPercentage)
                .orElse(0.0);

        Double housingAmount = baseSalary * housingPercentage / 100;
        Double transportAmount = baseSalary * transportPercentage / 100;

        // Calculate gross salary
        Double grossSalary = baseSalary + housingAmount + transportAmount;

        // Calculate deductions applied to base salary
        Double employeeTaxAmount = baseSalaryDeductions.stream()
                .filter(d -> d.getDeductionName().equalsIgnoreCase("Employee Tax"))
                .findFirst()
                .map(d -> baseSalary * d.getPercentage() / 100)
                .orElse(0.0);

        Double pensionAmount = baseSalaryDeductions.stream()
                .filter(d -> d.getDeductionName().equalsIgnoreCase("Pension"))
                .findFirst()
                .map(d -> baseSalary * d.getPercentage() / 100)
                .orElse(0.0);

        Double medicalInsuranceAmount = baseSalaryDeductions.stream()
                .filter(d -> d.getDeductionName().equalsIgnoreCase("Medical Insurance"))
                .findFirst()
                .map(d -> baseSalary * d.getPercentage() / 100)
                .orElse(0.0);

        Double otherTaxedAmount = baseSalaryDeductions.stream()
                .filter(d -> d.getDeductionName().equalsIgnoreCase("Others"))
                .findFirst()
                .map(d -> baseSalary * d.getPercentage() / 100)
                .orElse(0.0);

        // Calculate net salary
        Double totalDeductions = employeeTaxAmount + pensionAmount + medicalInsuranceAmount + otherTaxedAmount;
        Double netSalary = grossSalary - totalDeductions;

        // Create and save pay slip
        PaySlip paySlip = PaySlip.builder()
                .employee(employment.getEmployee())
                .employment(employment)
                .houseAmount(housingAmount)
                .transportAmount(transportAmount)
                .employeeTaxedAmount(employeeTaxAmount)
                .pensionAmount(pensionAmount)
                .medicalInsuranceAmount(medicalInsuranceAmount)
                .otherTaxedAmount(otherTaxedAmount)
                .grossSalary(grossSalary)
                .netSalary(netSalary)
                .month(month)
                .year(year)
                .status("pending")
                .baseSalary(baseSalary)
                .createdAt(new Date())
                .createdBy(currentUser)
                .build();

        return paySlipRepository.save(paySlip);
    }

    @Transactional
    public PaySlipDTO approvePaySlip(Long id) {
        Employee currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }

        PaySlip paySlip = paySlipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pay slip not found with id: " + id));

        if ("paid".equalsIgnoreCase(paySlip.getStatus())) {
            throw new RuntimeException("Pay slip is already approved");
        }

        paySlip.setStatus("paid");
        paySlip.setApprovedBy(currentUser);
        paySlip.setApprovedAt(new Date());

        PaySlip updatedPaySlip = paySlipRepository.save(paySlip);

        // Create and send notification message
        messageService.createSalaryNotificationMessage(updatedPaySlip);

        return convertToDTO(updatedPaySlip);
    }

    @Transactional
    public List<PaySlipDTO> approveAllPaySlips(String month, Integer year) {
        Employee currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("No authenticated user found");
        }

        List<PaySlip> pendingPaySlips = paySlipRepository.findByMonthAndYearAndStatus(month, year, "pending");
        if (pendingPaySlips.isEmpty()) {
            throw new RuntimeException("No pending pay slips found for month: " + month + ", year: " + year);
        }

        List<PaySlip> approvedPaySlips = pendingPaySlips.stream()
                .map(paySlip -> {
                    paySlip.setStatus("paid");
                    paySlip.setApprovedBy(currentUser);
                    paySlip.setApprovedAt(new Date());
                    PaySlip updatedPaySlip = paySlipRepository.save(paySlip);

                    // Create and send notification message
                    messageService.createSalaryNotificationMessage(updatedPaySlip);

                    return updatedPaySlip;
                })
                .collect(Collectors.toList());

        return approvedPaySlips.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaySlipDTO convertToDTO(PaySlip paySlip) {
        Employee employee = paySlip.getEmployee();
        Employment employment = paySlip.getEmployment();

        PaySlipDTO dto = PaySlipDTO.builder()
                .id(paySlip.getId())
                .employeeId(employee.getId())
                .employeeCode(employee.getCode())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeEmail(employee.getEmail())
                .houseAmount(paySlip.getHouseAmount())
                .transportAmount(paySlip.getTransportAmount())
                .employeeTaxedAmount(paySlip.getEmployeeTaxedAmount())
                .pensionAmount(paySlip.getPensionAmount())
                .medicalInsuranceAmount(paySlip.getMedicalInsuranceAmount())
                .otherTaxedAmount(paySlip.getOtherTaxedAmount())
                .grossSalary(paySlip.getGrossSalary())
                .netSalary(paySlip.getNetSalary())
                .month(paySlip.getMonth())
                .year(paySlip.getYear())
                .status(paySlip.getStatus())
                .baseSalary(paySlip.getBaseSalary())
                .createdAt(paySlip.getCreatedAt())
                .updatedAt(paySlip.getUpdatedAt())
                .build();

        if (employment != null) {
            dto.setEmploymentId(employment.getId());
            dto.setDepartment(employment.getDepartment());
            dto.setPosition(employment.getPosition());
        }

        if (paySlip.getCreatedBy() != null) {
            dto.setCreatedById(paySlip.getCreatedBy().getId());
            dto.setCreatedByName(paySlip.getCreatedBy().getFirstName() + " " + paySlip.getCreatedBy().getLastName());
        }

        if (paySlip.getApprovedBy() != null) {
            dto.setApprovedById(paySlip.getApprovedBy().getId());
            dto.setApprovedByName(paySlip.getApprovedBy().getFirstName() + " " + paySlip.getApprovedBy().getLastName());
            dto.setApprovedAt(paySlip.getApprovedAt());
        }

        return dto;
    }
}
