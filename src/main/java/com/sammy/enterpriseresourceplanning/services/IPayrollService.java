package com.sammy.enterpriseresourceplanning.services;

import com.sammy.enterpriseresourceplanning.dtos.request.payslip.PaySlipRequestDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.payslip.PaySlipResponseDTO;
import com.sammy.enterpriseresourceplanning.models.PaySlip;

import java.util.List;
import java.util.UUID;

public interface IPayrollService {
    PaySlipResponseDTO generatePaySlip(PaySlipRequestDTO paySlipRequestDTO);
    PaySlipResponseDTO approvePaySlip(UUID id);
    PaySlipResponseDTO getPaySlipById(UUID id);
    List<PaySlipResponseDTO> getPaySlipsByEmployee(UUID employeeId);
    List<PaySlipResponseDTO> getPaySlipsByMonth(Integer month, Integer year);
    List<PaySlipResponseDTO> getAllPendingPaySlips();
    List<PaySlipResponseDTO> getAllPaidPaySlips();
    List<PaySlipResponseDTO> getAllPaySlips();
    void deletePaySlip(UUID id);
    PaySlip findPaySlipEntity(UUID id);
    
    // Batch operations
    List<PaySlipResponseDTO> generatePaySlipsForAllEmployees(Integer month, Integer year);
    List<PaySlipResponseDTO> approveAllPendingPaySlips();
}