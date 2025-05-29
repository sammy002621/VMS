package com.sammy.enterpriseresourceplanning.controllers;

import com.sammy.enterpriseresourceplanning.dtos.request.deduction.DeductionRequestDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.deduction.DeductionResponseDTO;
import com.sammy.enterpriseresourceplanning.services.IDeductionService;
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
@RequestMapping("/api/v1/deductions")
@RequiredArgsConstructor
@Tag(name = "Deduction Management", description = "APIs for managing deductions")
@SecurityRequirement(name = "bearerAuth")
public class DeductionController {

    private final IDeductionService deductionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create deduction", description = "Creates a new deduction")
    public ResponseEntity<DeductionResponseDTO> createDeduction(@Valid @RequestBody DeductionRequestDTO deductionRequestDTO) {
        DeductionResponseDTO deductionResponseDTO = deductionService.createDeduction(deductionRequestDTO);
        return new ResponseEntity<>(deductionResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get all deductions", description = "Retrieves a list of all deductions")
    public ResponseEntity<List<DeductionResponseDTO>> getAllDeductions() {
        List<DeductionResponseDTO> deductions = deductionService.getAllDeductions();
        return ResponseEntity.ok(deductions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get deduction by ID", description = "Retrieves a deduction by its ID")
    public ResponseEntity<DeductionResponseDTO> getDeductionById(@PathVariable UUID id) {
        DeductionResponseDTO deductionResponseDTO = deductionService.getDeductionById(id);
        return ResponseEntity.ok(deductionResponseDTO);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get deduction by code", description = "Retrieves a deduction by its code")
    public ResponseEntity<DeductionResponseDTO> getDeductionByCode(@PathVariable String code) {
        DeductionResponseDTO deductionResponseDTO = deductionService.getDeductionByCode(code);
        return ResponseEntity.ok(deductionResponseDTO);
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    @Operation(summary = "Get deduction by name", description = "Retrieves a deduction by its name")
    public ResponseEntity<DeductionResponseDTO> getDeductionByName(@PathVariable String name) {
        DeductionResponseDTO deductionResponseDTO = deductionService.getDeductionByName(name);
        return ResponseEntity.ok(deductionResponseDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update deduction", description = "Updates a deduction")
    public ResponseEntity<DeductionResponseDTO> updateDeduction(
            @PathVariable UUID id,
            @Valid @RequestBody DeductionRequestDTO deductionRequestDTO) {
        DeductionResponseDTO deductionResponseDTO = deductionService.updateDeduction(id, deductionRequestDTO);
        return ResponseEntity.ok(deductionResponseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete deduction", description = "Deletes a deduction")
    public ResponseEntity<Void> deleteDeduction(@PathVariable UUID id) {
        deductionService.deleteDeduction(id);
        return ResponseEntity.noContent().build();
    }
}