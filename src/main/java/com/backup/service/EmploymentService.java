package com.backup.service;

import com.backup.dto.EmploymentDTO;
import com.backup.model.Employee;
import com.backup.model.Employment;
import com.backup.repository.EmployeeRepository;
import com.backup.repository.EmploymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmploymentService {

    private final EmploymentRepository employmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AuthService authService;

    public List<EmploymentDTO> getAllEmployments() {
        return employmentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmploymentDTO> getActiveEmployments() {
        return employmentRepository.findByStatus("active").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmploymentDTO getEmploymentById(Long id) {
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employment not found with id: " + id));
        return convertToDTO(employment);
    }

    public EmploymentDTO getEmploymentByCode(String code) {
        Employment employment = employmentRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Employment not found with code: " + code));
        return convertToDTO(employment);
    }

    public List<EmploymentDTO> getEmploymentsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return employmentRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmploymentDTO> getActiveEmploymentsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        return employmentRepository.findByEmployeeAndStatus(employee, "active").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EmploymentDTO> getCurrentEmployeeEmployments() {
        Employee employee = authService.getCurrentUser();
        if (employee == null) {
            throw new RuntimeException("No authenticated user found");
        }

        return employmentRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EmploymentDTO createEmployment(EmploymentDTO employmentDTO) {
        if (employmentRepository.existsByCode(employmentDTO.getCode())) {
            throw new RuntimeException("Employment code already exists");
        }

        Employee employee = employeeRepository.findById(employmentDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employmentDTO.getEmployeeId()));

        Employment employment = Employment.builder()
                .code(employmentDTO.getCode())
                .employee(employee)
                .department(employmentDTO.getDepartment())
                .position(employmentDTO.getPosition())
                .baseSalary(employmentDTO.getBaseSalary())
                .status(employmentDTO.getStatus())
                .joiningDate(employmentDTO.getJoiningDate())
                .build();

        Employment savedEmployment = employmentRepository.save(employment);
        return convertToDTO(savedEmployment);
    }

    @Transactional
    public EmploymentDTO updateEmployment(Long id, EmploymentDTO employmentDTO) {
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employment not found with id: " + id));

        // Check if code is being changed and if it already exists
        if (!employment.getCode().equals(employmentDTO.getCode()) && 
                employmentRepository.existsByCode(employmentDTO.getCode())) {
            throw new RuntimeException("Employment code already exists");
        }

        // Check if employee is being changed
        if (!employment.getEmployee().getId().equals(employmentDTO.getEmployeeId())) {
            Employee newEmployee = employeeRepository.findById(employmentDTO.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employmentDTO.getEmployeeId()));
            employment.setEmployee(newEmployee);
        }

        employment.setCode(employmentDTO.getCode());
        employment.setDepartment(employmentDTO.getDepartment());
        employment.setPosition(employmentDTO.getPosition());
        employment.setBaseSalary(employmentDTO.getBaseSalary());
        employment.setStatus(employmentDTO.getStatus());
        employment.setJoiningDate(employmentDTO.getJoiningDate());

        Employment updatedEmployment = employmentRepository.save(employment);
        return convertToDTO(updatedEmployment);
    }

    @Transactional
    public void deleteEmployment(Long id) {
        if (!employmentRepository.existsById(id)) {
            throw new RuntimeException("Employment not found with id: " + id);
        }
        employmentRepository.deleteById(id);
    }

    public Page<EmploymentDTO> getEmploymentsWithFiltering(Map<String, String> filters, Pageable pageable) {
        Specification<Employment> spec = buildSpecification(filters);
        Page<Employment> employments = employmentRepository.findAll(spec, pageable);
        return employments.map(this::convertToDTO);
    }

    private Specification<Employment> buildSpecification(Map<String, String> filters) {
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
                            case "code":
                                return criteriaBuilder.equal(root.get("code"), value);
                            case "department":
                                return criteriaBuilder.like(criteriaBuilder.lower(root.get("department")), "%" + value.toLowerCase() + "%");
                            case "position":
                                return criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), "%" + value.toLowerCase() + "%");
                            case "status":
                                return criteriaBuilder.equal(root.get("status"), value);
                            case "minBaseSalary":
                                return criteriaBuilder.greaterThanOrEqualTo(root.get("baseSalary"), Double.valueOf(value));
                            case "maxBaseSalary":
                                return criteriaBuilder.lessThanOrEqualTo(root.get("baseSalary"), Double.valueOf(value));
                            case "fromJoiningDate":
                                return criteriaBuilder.greaterThanOrEqualTo(root.get("joiningDate"), java.sql.Date.valueOf(value));
                            case "toJoiningDate":
                                return criteriaBuilder.lessThanOrEqualTo(root.get("joiningDate"), java.sql.Date.valueOf(value));
                            default:
                                return null;
                        }
                    })
                    .filter(predicate -> predicate != null)
                    .collect(Collectors.toList());

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private EmploymentDTO convertToDTO(Employment employment) {
        Employee employee = employment.getEmployee();
        return EmploymentDTO.builder()
                .id(employment.getId())
                .code(employment.getCode())
                .employeeId(employee.getId())
                .employeeCode(employee.getCode())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeEmail(employee.getEmail())
                .department(employment.getDepartment())
                .position(employment.getPosition())
                .baseSalary(employment.getBaseSalary())
                .status(employment.getStatus())
                .joiningDate(employment.getJoiningDate())
                .build();
    }
}
