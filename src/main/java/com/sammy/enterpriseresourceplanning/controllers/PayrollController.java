package com.sammy.enterpriseresourceplanning.controllers;

import com.sammy.enterpriseresourceplanning.dtos.request.payslip.PaySlipRequestDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.payslip.PaySlipResponseDTO;
import com.sammy.enterpriseresourceplanning.services.IPayrollService;
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
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
@Tag(name = "Payroll Management", description = "APIs for managing payroll and pay slips")
@SecurityRequirement(name = "bearerAuth")
public class PayrollController {

    private final IPayrollService payrollService;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate pay slip", description = "Generates a pay slip for an employee")
    public ResponseEntity<PaySlipResponseDTO> generatePaySlip(@Valid @RequestBody PaySlipRequestDTO paySlipRequestDTO) {
        PaySlipResponseDTO paySlipResponseDTO = payrollService.generatePaySlip(paySlipRequestDTO);
        return new ResponseEntity<>(paySlipResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/generate/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate pay slips for all employees", description = "Generates pay slips for all active employees")
    public ResponseEntity<List<PaySlipResponseDTO>> generatePaySlipsForAllEmployees(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        List<PaySlipResponseDTO> paySlips = payrollService.generatePaySlipsForAllEmployees(month, year);
        return new ResponseEntity<>(paySlips, HttpStatus.CREATED);
    }

    @PutMapping("/approve/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve pay slip", description = "Approves a pay slip")
    public ResponseEntity<PaySlipResponseDTO> approvePaySlip(@PathVariable UUID id) {
        PaySlipResponseDTO paySlipResponseDTO = payrollService.approvePaySlip(id);
        return ResponseEntity.ok(paySlipResponseDTO);
    }

    @PutMapping("/approve/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve all pending pay slips", description = "Approves all pending pay slips")
    public ResponseEntity<List<PaySlipResponseDTO>> approveAllPendingPaySlips() {
        List<PaySlipResponseDTO> paySlips = payrollService.approveAllPendingPaySlips();
        return ResponseEntity.ok(paySlips);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isEmployeeOfPaySlip(#id)")
    @Operation(summary = "Get pay slip by ID", description = "Retrieves a pay slip by its ID")
    public ResponseEntity<PaySlipResponseDTO> getPaySlipById(@PathVariable UUID id) {
        PaySlipResponseDTO paySlipResponseDTO = payrollService.getPaySlipById(id);
        return ResponseEntity.ok(paySlipResponseDTO);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @userSecurity.isCurrentUser(#employeeId)")
    @Operation(summary = "Get pay slips by employee", description = "Retrieves all pay slips for a specific employee")
    public ResponseEntity<List<PaySlipResponseDTO>> getPaySlipsByEmployee(@PathVariable UUID employeeId) {
        List<PaySlipResponseDTO> paySlips = payrollService.getPaySlipsByEmployee(employeeId);
        return ResponseEntity.ok(paySlips);
    }

    @GetMapping("/month")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get pay slips by month", description = "Retrieves all pay slips for a specific month and year")
    public ResponseEntity<List<PaySlipResponseDTO>> getPaySlipsByMonth(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        List<PaySlipResponseDTO> paySlips = payrollService.getPaySlipsByMonth(month, year);
        return ResponseEntity.ok(paySlips);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all pending pay slips", description = "Retrieves all pending pay slips")
    public ResponseEntity<List<PaySlipResponseDTO>> getAllPendingPaySlips() {
        List<PaySlipResponseDTO> paySlips = payrollService.getAllPendingPaySlips();
        return ResponseEntity.ok(paySlips);
    }

    @GetMapping("/paid")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all paid pay slips", description = "Retrieves all paid pay slips")
    public ResponseEntity<List<PaySlipResponseDTO>> getAllPaidPaySlips() {
        List<PaySlipResponseDTO> paySlips = payrollService.getAllPaidPaySlips();
        return ResponseEntity.ok(paySlips);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all pay slips", description = "Retrieves all pay slips")
    public ResponseEntity<List<PaySlipResponseDTO>> getAllPaySlips() {
        List<PaySlipResponseDTO> paySlips = payrollService.getAllPaySlips();
        return ResponseEntity.ok(paySlips);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete pay slip", description = "Deletes a pay slip")
    public ResponseEntity<Void> deletePaySlip(@PathVariable UUID id) {
        payrollService.deletePaySlip(id);
        return ResponseEntity.noContent().build();
    }
}