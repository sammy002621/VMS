package com.sammy.enterpriseresourceplanning.services;

import com.sammy.enterpriseresourceplanning.dtos.request.employment.EmploymentRequestDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.employment.EmploymentResponseDTO;
import com.sammy.enterpriseresourceplanning.models.Employment;

import java.util.List;
import java.util.UUID;

public interface IEmploymentService {
    EmploymentResponseDTO createEmployment(EmploymentRequestDTO employmentRequestDTO);
    EmploymentResponseDTO updateEmployment(UUID id, EmploymentRequestDTO employmentRequestDTO);
    EmploymentResponseDTO getEmploymentById(UUID id);
    EmploymentResponseDTO getEmploymentByCode(String code);
    List<EmploymentResponseDTO> getAllEmployments();
    List<EmploymentResponseDTO> getEmploymentsByEmployee(UUID employeeId);
    void deleteEmployment(UUID id);
    Employment findEmploymentEntity(UUID id);
    Employment findEmploymentEntityByCode(String code);
}