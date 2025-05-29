package com.sammy.enterpriseresourceplanning.controllers;

import com.sammy.enterpriseresourceplanning.dtos.response.message.MessageResponseDTO;
import com.sammy.enterpriseresourceplanning.services.IMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Message Management", description = "APIs for managing messages")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final IMessageService messageService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all messages", description = "Retrieves a list of all messages")
    public ResponseEntity<List<MessageResponseDTO>> getAllMessages() {
        List<MessageResponseDTO> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isEmployeeOfMessage(#id)")
    @Operation(summary = "Get message by ID", description = "Retrieves a message by its ID")
    public ResponseEntity<MessageResponseDTO> getMessageById(@PathVariable UUID id) {
        MessageResponseDTO messageResponseDTO = messageService.getMessageById(id);
        return ResponseEntity.ok(messageResponseDTO);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isCurrentUser(#employeeId)")
    @Operation(summary = "Get messages by employee", description = "Retrieves all messages for a specific employee")
    public ResponseEntity<List<MessageResponseDTO>> getMessagesByEmployee(@PathVariable UUID employeeId) {
        List<MessageResponseDTO> messages = messageService.getMessagesByEmployee(employeeId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/month")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get messages by month", description = "Retrieves all messages for a specific month and year")
    public ResponseEntity<List<MessageResponseDTO>> getMessagesByMonth(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        List<MessageResponseDTO> messages = messageService.getMessagesByMonthAndYear(month, year);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all pending messages", description = "Retrieves all pending messages")
    public ResponseEntity<List<MessageResponseDTO>> getAllPendingMessages() {
        List<MessageResponseDTO> messages = messageService.getAllPendingMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/sent")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all sent messages", description = "Retrieves all sent messages")
    public ResponseEntity<List<MessageResponseDTO>> getAllSentMessages() {
        List<MessageResponseDTO> messages = messageService.getAllSentMessages();
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/send/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Send message", description = "Sends a message")
    public ResponseEntity<MessageResponseDTO> sendMessage(@PathVariable UUID id) {
        MessageResponseDTO messageResponseDTO = messageService.sendMessage(id);
        return ResponseEntity.ok(messageResponseDTO);
    }

    @PutMapping("/send/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Send all pending messages", description = "Sends all pending messages")
    public ResponseEntity<List<MessageResponseDTO>> sendAllPendingMessages() {
        List<MessageResponseDTO> messages = messageService.sendAllPendingMessages();
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete message", description = "Deletes a message")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}