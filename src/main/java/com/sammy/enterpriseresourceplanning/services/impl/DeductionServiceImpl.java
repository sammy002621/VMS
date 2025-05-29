package com.sammy.enterpriseresourceplanning.services.impl;

import com.sammy.enterpriseresourceplanning.dtos.request.deduction.DeductionRequestDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.deduction.DeductionResponseDTO;
import com.sammy.enterpriseresourceplanning.exceptions.BadRequestException;
import com.sammy.enterpriseresourceplanning.exceptions.NotFoundException;
import com.sammy.enterpriseresourceplanning.models.Deduction;
import com.sammy.enterpriseresourceplanning.repositories.IDeductionRepository;
import com.sammy.enterpriseresourceplanning.services.IDeductionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeductionServiceImpl implements IDeductionService {

    private final IDeductionRepository deductionRepository;

    @Override
    @Transactional
    public DeductionResponseDTO createDeduction(DeductionRequestDTO deductionRequestDTO) {
        // Check if deduction name already exists
        if (deductionRepository.findByName(deductionRequestDTO.getName()).isPresent()) {
            throw new BadRequestException("Deduction with name " + deductionRequestDTO.getName() + " already exists");
        }
        
        // Generate a unique code if not provided
        String code = deductionRequestDTO.getCode();
        if (code == null || code.isEmpty()) {
            code = "DED-" + UUID.randomUUID().toString().substring(0, 8);
        } else {
            // Check if code is already in use
            if (deductionRepository.findByCode(code).isPresent()) {
                throw new BadRequestException("Deduction code already in use");
            }
        }
        
        Deduction deduction = Deduction.builder()
                .code(code)
                .name(deductionRequestDTO.getName())
                .percentage(deductionRequestDTO.getPercentage())
                .build();
        
        deduction = deductionRepository.save(deduction);
        
        return mapToResponseDTO(deduction);
    }

    @Override
    @Transactional
    public DeductionResponseDTO updateDeduction(UUID id, DeductionRequestDTO deductionRequestDTO) {
        Deduction deduction = findDeductionEntity(id);
        
        // Check if name is being changed and if the new name already exists
        if (!deduction.getName().equals(deductionRequestDTO.getName()) && 
            deductionRepository.findByName(deductionRequestDTO.getName()).isPresent()) {
            throw new BadRequestException("Deduction with name " + deductionRequestDTO.getName() + " already exists");
        }
        
        deduction.setName(deductionRequestDTO.getName());
        deduction.setPercentage(deductionRequestDTO.getPercentage());
        
        deduction = deductionRepository.save(deduction);
        
        return mapToResponseDTO(deduction);
    }

    @Override
    public DeductionResponseDTO getDeductionById(UUID id) {
        Deduction deduction = findDeductionEntity(id);
        return mapToResponseDTO(deduction);
    }

    @Override
    public DeductionResponseDTO getDeductionByCode(String code) {
        Deduction deduction = findDeductionEntityByCode(code);
        return mapToResponseDTO(deduction);
    }

    @Override
    public DeductionResponseDTO getDeductionByName(String name) {
        Deduction deduction = findDeductionEntityByName(name);
        return mapToResponseDTO(deduction);
    }

    @Override
    public List<DeductionResponseDTO> getAllDeductions() {
        return deductionRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDeduction(UUID id) {
        if (!deductionRepository.existsById(id)) {
            throw new NotFoundException("Deduction not found with id: " + id);
        }
        deductionRepository.deleteById(id);
    }

    @Override
    public Deduction findDeductionEntity(UUID id) {
        return deductionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Deduction not found with id: " + id));
    }

    @Override
    public Deduction findDeductionEntityByCode(String code) {
        return deductionRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Deduction not found with code: " + code));
    }

    @Override
    public Deduction findDeductionEntityByName(String name) {
        return deductionRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Deduction not found with name: " + name));
    }
    
    private DeductionResponseDTO mapToResponseDTO(Deduction deduction) {
        return DeductionResponseDTO.builder()
                .id(deduction.getId())
                .code(deduction.getCode())
                .name(deduction.getName())
                .percentage(deduction.getPercentage())
                .build();
    }
}