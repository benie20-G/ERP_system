package com.backup.service;

import com.backup.dto.EmployeeDTO;
import com.backup.model.Employee;
import com.backup.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return convertToDTO(employee);
    }

    public EmployeeDTO getEmployeeByCode(String code) {
        Employee employee = employeeRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Employee not found with code: " + code));
        return convertToDTO(employee);
    }

    public EmployeeDTO getCurrentEmployee() {
        Employee employee = authService.getCurrentUser();
        if (employee == null) {
            throw new RuntimeException("No authenticated user found");
        }
        return convertToDTO(employee);
    }

    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Check if email is being changed and if it already exists
        if (!employee.getEmail().equals(employeeDTO.getEmail()) && 
                employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check if code is being changed and if it already exists
        if (!employee.getCode().equals(employeeDTO.getCode()) && 
                employeeRepository.existsByCode(employeeDTO.getCode())) {
            throw new RuntimeException("Employee code already exists");
        }

        employee.setCode(employeeDTO.getCode());
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setEmail(employeeDTO.getEmail());

        // Only update password if it's provided
        if (employeeDTO.getPassword() != null && !employeeDTO.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        }

        employee.setRoles(employeeDTO.getRoles());
        employee.setMobile(employeeDTO.getMobile());
        employee.setDateOfBirth(employeeDTO.getDateOfBirth());
        employee.setStatus(employeeDTO.getStatus());

        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDTO(updatedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    @Transactional
    public EmployeeDTO changeRole(Long id, String roles) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setRoles(roles);

        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDTO(updatedEmployee);
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .code(employee.getCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .roles(employee.getRoles())
                .mobile(employee.getMobile())
                .dateOfBirth(employee.getDateOfBirth())
                .status(employee.getStatus())
                .build();
    }
}
