package com.backup.service;

import com.backup.dto.MessageDTO;
import com.backup.model.Employee;
import com.backup.model.Message;
import com.backup.model.PaySlip;
import com.backup.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final EmailService emailService;
    private final AuthService authService;

    public List<MessageDTO> getAllMessages() {
        return messageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getMessagesByStatus(String status) {
        return messageRepository.findBySentStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MessageDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
        return convertToDTO(message);
    }

    public List<MessageDTO> getMessagesByEmployee(Long employeeId) {
        Employee employee = authService.getCurrentUser();
        if (employee == null || !employee.getId().equals(employeeId)) {
            throw new RuntimeException("Unauthorized access to employee messages");
        }
        
        return messageRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MessageDTO> getCurrentEmployeeMessages() {
        Employee employee = authService.getCurrentUser();
        if (employee == null) {
            throw new RuntimeException("No authenticated user found");
        }
        
        return messageRepository.findByEmployee(employee).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO createSalaryNotificationMessage(PaySlip paySlip) {
        if (messageRepository.existsByPaySlip(paySlip)) {
            log.info("Message already exists for pay slip id: {}", paySlip.getId());
            return null;
        }
        
        Employee employee = paySlip.getEmployee();
        String monthYear = getMonthName(paySlip.getMonth()) + " " + paySlip.getYear();
        
        String messageContent = String.format(
                "Dear %s Your salary of %s from %s %s has been credited to your %s account successfully.",
                employee.getFirstName(),
                monthYear,
                "Rwanda Government", // This could be parameterized or retrieved from a configuration
                String.format("%.2f", paySlip.getNetSalary()),
                employee.getCode()
        );
        
        Message message = Message.builder()
                .employee(employee)
                .message(messageContent)
                .monthYear(monthYear)
                .sentStatus("PENDING")
                .paySlip(paySlip)
                .createdAt(new Date())
                .retryCount(0)
                .build();
        
        Message savedMessage = messageRepository.save(message);
        
        // Send email asynchronously
        sendEmailNotification(savedMessage);
        
        return convertToDTO(savedMessage);
    }

    @Async
    public void sendEmailNotification(Message message) {
        try {
            Employee employee = message.getEmployee();
            PaySlip paySlip = message.getPaySlip();
            
            emailService.sendSalaryNotification(
                    employee.getEmail(),
                    employee.getFirstName(),
                    getMonthName(paySlip.getMonth()),
                    paySlip.getYear().toString(),
                    "Rwanda Government", // This could be parameterized
                    paySlip.getNetSalary(),
                    employee.getCode()
            );
            
            message.setSentStatus("SENT");
            message.setSentAt(new Date());
            messageRepository.save(message);
            
            log.info("Email notification sent successfully to: {}", employee.getEmail());
        } catch (Exception e) {
            log.error("Failed to send email notification", e);
            message.setSentStatus("FAILED");
            message.setErrorMessage(e.getMessage());
            message.setRetryCount(message.getRetryCount() + 1);
            messageRepository.save(message);
        }
    }

    @Transactional
    public void retryFailedMessages() {
        List<Message> failedMessages = messageRepository.findBySentStatus("FAILED");
        
        for (Message message : failedMessages) {
            if (message.getRetryCount() < 3) { // Limit retry attempts
                sendEmailNotification(message);
            }
        }
    }

    private String getMonthName(String monthNumber) {
        try {
            int month = Integer.parseInt(monthNumber);
            String[] months = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", 
                              "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};
            return months[month - 1];
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return monthNumber;
        }
    }

    private MessageDTO convertToDTO(Message message) {
        Employee employee = message.getEmployee();
        
        MessageDTO dto = MessageDTO.builder()
                .id(message.getId())
                .employeeId(employee.getId())
                .employeeCode(employee.getCode())
                .employeeName(employee.getFirstName() + " " + employee.getLastName())
                .employeeEmail(employee.getEmail())
                .message(message.getMessage())
                .monthYear(message.getMonthYear())
                .sentStatus(message.getSentStatus())
                .createdAt(message.getCreatedAt())
                .sentAt(message.getSentAt())
                .retryCount(message.getRetryCount())
                .errorMessage(message.getErrorMessage())
                .build();
        
        if (message.getPaySlip() != null) {
            dto.setPaySlipId(message.getPaySlip().getId());
        }
        
        return dto;
    }
}