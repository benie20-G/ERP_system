package com.backup.service;

import com.backup.dto.DeductionDTO;
import com.backup.model.Deduction;
import com.backup.repository.DeductionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeductionService {

    private final DeductionRepository deductionRepository;

    public List<DeductionDTO> getAllDeductions() {
        return deductionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeductionDTO> getActiveDeductions() {
        return deductionRepository.findByActive(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeductionDTO> getActiveBaseSalaryDeductions() {
        return deductionRepository.findByActiveAndAppliedToBaseSalary(true, true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DeductionDTO getDeductionById(Long id) {
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction not found with id: " + id));
        return convertToDTO(deduction);
    }

    public DeductionDTO getDeductionByCode(String code) {
        Deduction deduction = deductionRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Deduction not found with code: " + code));
        return convertToDTO(deduction);
    }

    public DeductionDTO getDeductionByName(String name) {
        Deduction deduction = deductionRepository.findByDeductionName(name)
                .orElseThrow(() -> new RuntimeException("Deduction not found with name: " + name));
        return convertToDTO(deduction);
    }

    @Transactional
    public DeductionDTO createDeduction(DeductionDTO deductionDTO) {
        if (deductionRepository.existsByCode(deductionDTO.getCode())) {
            throw new RuntimeException("Deduction code already exists");
        }
        
        if (deductionRepository.existsByDeductionName(deductionDTO.getDeductionName())) {
            throw new RuntimeException("Deduction name already exists");
        }
        
        Deduction deduction = Deduction.builder()
                .code(deductionDTO.getCode())
                .deductionName(deductionDTO.getDeductionName())
                .percentage(deductionDTO.getPercentage())
                .active(deductionDTO.getActive())
                .appliedToBaseSalary(deductionDTO.getAppliedToBaseSalary())
                .build();
        
        Deduction savedDeduction = deductionRepository.save(deduction);
        return convertToDTO(savedDeduction);
    }

    @Transactional
    public DeductionDTO updateDeduction(Long id, DeductionDTO deductionDTO) {
        Deduction deduction = deductionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction not found with id: " + id));
        
        // Check if code is being changed and if it already exists
        if (!deduction.getCode().equals(deductionDTO.getCode()) && 
                deductionRepository.existsByCode(deductionDTO.getCode())) {
            throw new RuntimeException("Deduction code already exists");
        }
        
        // Check if name is being changed and if it already exists
        if (!deduction.getDeductionName().equals(deductionDTO.getDeductionName()) && 
                deductionRepository.existsByDeductionName(deductionDTO.getDeductionName())) {
            throw new RuntimeException("Deduction name already exists");
        }
        
        deduction.setCode(deductionDTO.getCode());
        deduction.setDeductionName(deductionDTO.getDeductionName());
        deduction.setPercentage(deductionDTO.getPercentage());
        deduction.setActive(deductionDTO.getActive());
        deduction.setAppliedToBaseSalary(deductionDTO.getAppliedToBaseSalary());
        
        Deduction updatedDeduction = deductionRepository.save(deduction);
        return convertToDTO(updatedDeduction);
    }

    @Transactional
    public void deleteDeduction(Long id) {
        if (!deductionRepository.existsById(id)) {
            throw new RuntimeException("Deduction not found with id: " + id);
        }
        deductionRepository.deleteById(id);
    }

    @Transactional
    public void initializeDefaultDeductions() {
        // Check if deductions already exist
        if (deductionRepository.count() > 0) {
            log.info("Deductions already initialized, skipping...");
            return;
        }
        
        // Create default deductions as specified in the requirements
        createDefaultDeduction("DED001", "Employee Tax", 30.0, true, true);
        createDefaultDeduction("DED002", "Pension", 6.0, true, true);
        createDefaultDeduction("DED003", "Medical Insurance", 5.0, true, true);
        createDefaultDeduction("DED004", "Others", 5.0, true, true);
        createDefaultDeduction("DED005", "Housing", 14.0, true, false);
        createDefaultDeduction("DED006", "Transport", 14.0, true, false);
        
        log.info("Default deductions initialized successfully");
    }
    
    private void createDefaultDeduction(String code, String name, Double percentage, Boolean active, Boolean appliedToBaseSalary) {
        Deduction deduction = Deduction.builder()
                .code(code)
                .deductionName(name)
                .percentage(percentage)
                .active(active)
                .appliedToBaseSalary(appliedToBaseSalary)
                .build();
        
        deductionRepository.save(deduction);
    }

    private DeductionDTO convertToDTO(Deduction deduction) {
        return DeductionDTO.builder()
                .id(deduction.getId())
                .code(deduction.getCode())
                .deductionName(deduction.getDeductionName())
                .percentage(deduction.getPercentage())
                .active(deduction.isActive())
                .appliedToBaseSalary(deduction.isAppliedToBaseSalary())
                .build();
    }
}