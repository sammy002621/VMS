package com.sammy.enterpriseresourceplanning.controllers;

import com.sammy.enterpriseresourceplanning.dtos.request.employment.EmploymentRequestDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.employment.EmploymentResponseDTO;
import com.sammy.enterpriseresourceplanning.services.IEmploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employments")
@RequiredArgsConstructor
@Tag(name = "Employment Management", description = "APIs for managing employment records")
@SecurityRequirement(name = "bearerAuth")
public class EmploymentController {

    private final IEmploymentService employmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create employment", description = "Creates a new employment record")
    public ResponseEntity<EmploymentResponseDTO> createEmployment(@Valid @RequestBody EmploymentRequestDTO employmentRequestDTO) {
        EmploymentResponseDTO employmentResponseDTO = employmentService.createEmployment(employmentRequestDTO);
        return new ResponseEntity<>(employmentResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all employments", description = "Retrieves a list of all employment records")
    public ResponseEntity<List<EmploymentResponseDTO>> getAllEmployments() {
        List<EmploymentResponseDTO> employments = employmentService.getAllEmployments();
        return ResponseEntity.ok(employments);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isEmployeeOfEmployment(#id)")
    @Operation(summary = "Get employment by ID", description = "Retrieves an employment record by its ID")
    public ResponseEntity<EmploymentResponseDTO> getEmploymentById(@PathVariable UUID id) {
        EmploymentResponseDTO employmentResponseDTO = employmentService.getEmploymentById(id);
        return ResponseEntity.ok(employmentResponseDTO);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employment by code", description = "Retrieves an employment record by its code")
    public ResponseEntity<EmploymentResponseDTO> getEmploymentByCode(@PathVariable String code) {
        EmploymentResponseDTO employmentResponseDTO = employmentService.getEmploymentByCode(code);
        return ResponseEntity.ok(employmentResponseDTO);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isCurrentUser(#employeeId)")
    @Operation(summary = "Get employments by employee", description = "Retrieves all employment records for a specific employee")
    public ResponseEntity<List<EmploymentResponseDTO>> getEmploymentsByEmployee(@PathVariable UUID employeeId) {
        List<EmploymentResponseDTO> employments = employmentService.getEmploymentsByEmployee(employeeId);
        return ResponseEntity.ok(employments);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update employment", description = "Updates an employment record")
    public ResponseEntity<EmploymentResponseDTO> updateEmployment(
            @PathVariable UUID id,
            @Valid @RequestBody EmploymentRequestDTO employmentRequestDTO) {
        EmploymentResponseDTO employmentResponseDTO = employmentService.updateEmployment(id, employmentRequestDTO);
        return ResponseEntity.ok(employmentResponseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete employment", description = "Deletes an employment record")
    public ResponseEntity<Void> deleteEmployment(@PathVariable UUID id) {
        employmentService.deleteEmployment(id);
        return ResponseEntity.noContent().build();
    }
}