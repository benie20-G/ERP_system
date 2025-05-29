package com.backup.controller;

import com.backup.dto.MessageDTO;
import com.backup.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Message Management", description = "APIs for managing notification messages")
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "Get all messages", description = "Retrieves a list of all notification messages in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of messages",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    })
    @GetMapping("/admin/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MessageDTO>> getAllMessages() {
        log.info("Request to get all messages");
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @Operation(summary = "Get messages by status", description = "Retrieves a list of notification messages with the specified status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of messages",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    })
    @GetMapping("/admin/messages/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MessageDTO>> getMessagesByStatus(
            @Parameter(description = "Status of the messages to retrieve (SENT, PENDING, or FAILED)", required = true)
            @PathVariable String status) {
        log.info("Request to get messages with status: {}", status);
        return ResponseEntity.ok(messageService.getMessagesByStatus(status));
    }

    @Operation(summary = "Get message by ID", description = "Retrieves a notification message by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the message",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Message not found with the given ID")
    })
    @GetMapping("/admin/messages/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageDTO> getMessageById(
            @Parameter(description = "ID of the message to retrieve", required = true)
            @PathVariable Long id) {
        log.info("Request to get message with id: {}", id);
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @Operation(summary = "Get messages by employee ID", description = "Retrieves a list of notification messages for the specified employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of messages",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Employee not found with the given ID")
    })
    @GetMapping("/admin/messages/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MessageDTO>> getMessagesByEmployee(
            @Parameter(description = "ID of the employee", required = true)
            @PathVariable Long employeeId) {
        log.info("Request to get messages for employee with id: {}", employeeId);
        return ResponseEntity.ok(messageService.getMessagesByEmployee(employeeId));
    }

    @Operation(summary = "Get current employee messages", description = "Retrieves a list of notification messages for the currently authenticated employee")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of messages",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN, MANAGER, or EMPLOYEE role")
    })
    @GetMapping("/employee/messages")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<MessageDTO>> getCurrentEmployeeMessages() {
        log.info("Request to get messages for current employee");
        return ResponseEntity.ok(messageService.getCurrentEmployeeMessages());
    }

    @Operation(summary = "Retry failed messages", description = "Attempts to resend all failed notification messages")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully initiated retry of failed messages"),
        @ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role")
    })
    @PostMapping("/admin/messages/retry-failed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> retryFailedMessages() {
        log.info("Request to retry failed messages");
        messageService.retryFailedMessages();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Retry of failed messages initiated");
        return ResponseEntity.ok(response);
    }
}
