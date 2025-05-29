package com.sammy.enterpriseresourceplanning.services;

import com.sammy.enterpriseresourceplanning.dtos.request.deduction.DeductionRequestDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.deduction.DeductionResponseDTO;
import com.sammy.enterpriseresourceplanning.models.Deduction;

import java.util.List;
import java.util.UUID;

public interface IDeductionService {
    DeductionResponseDTO createDeduction(DeductionRequestDTO deductionRequestDTO);
    DeductionResponseDTO updateDeduction(UUID id, DeductionRequestDTO deductionRequestDTO);
    DeductionResponseDTO getDeductionById(UUID id);
    DeductionResponseDTO getDeductionByCode(String code);
    DeductionResponseDTO getDeductionByName(String name);
    List<DeductionResponseDTO> getAllDeductions();
    void deleteDeduction(UUID id);
    Deduction findDeductionEntity(UUID id);
    Deduction findDeductionEntityByCode(String code);
    Deduction findDeductionEntityByName(String name);
}