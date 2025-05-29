package com.sammy.enterpriseresourceplanning.services.impl;

import com.sammy.enterpriseresourceplanning.dtos.request.employment.EmploymentRequestDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.employment.EmploymentResponseDTO;
import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import com.sammy.enterpriseresourceplanning.exceptions.BadRequestException;
import com.sammy.enterpriseresourceplanning.exceptions.NotFoundException;
import com.sammy.enterpriseresourceplanning.models.Employment;
import com.sammy.enterpriseresourceplanning.models.User;
import com.sammy.enterpriseresourceplanning.repositories.IEmploymentRepository;
import com.sammy.enterpriseresourceplanning.services.IEmploymentService;
import com.sammy.enterpriseresourceplanning.services.IUserService;
import com.sammy.enterpriseresourceplanning.utils.CodeGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmploymentServiceImpl implements IEmploymentService {

    private final IEmploymentRepository employmentRepository;
    private final IUserService userService;

    @Override
    @Transactional
    public EmploymentResponseDTO createEmployment(EmploymentRequestDTO employmentRequestDTO) {
        User employee = userService.findUserById(employmentRequestDTO.getEmployeeId());

        // Check if employee already has an active employment
        List<Employment> existingEmployments = employmentRepository.findByEmployee(employee);
        if (!existingEmployments.isEmpty()) {
            throw new BadRequestException("Employee already has an employment record");
        }

        // Generate a unique code if not provided
        String code = employmentRequestDTO.getCode();
        if (code == null || code.isEmpty()) {
            // Generate a unique code using the CodeGenerator utility
            code = CodeGenerator.generateEmployeeCode();
            // Ensure the generated code is unique
            while (employmentRepository.findByCode(code).isPresent()) {
                code = CodeGenerator.generateEmployeeCode();
            }
        } else {
            // Check if code is already in use
            if (employmentRepository.findByCode(code).isPresent()) {
                throw new BadRequestException("Employment code already in use");
            }
        }

        Employment employment = Employment.builder()
                .code(code)
                .employee(employee)
                .department(employmentRequestDTO.getDepartment())
                .position(employmentRequestDTO.getPosition())
                .baseSalary(employmentRequestDTO.getBaseSalary())
                .status(employmentRequestDTO.getStatus())
                .joiningDate(employmentRequestDTO.getJoiningDate())
                .build();

        employment = employmentRepository.save(employment);

        return mapToResponseDTO(employment);
    }

    @Override
    @Transactional
    public EmploymentResponseDTO updateEmployment(UUID id, EmploymentRequestDTO employmentRequestDTO) {
        Employment employment = findEmploymentEntity(id);
        User employee = userService.findUserById(employmentRequestDTO.getEmployeeId());

        employment.setEmployee(employee);
        employment.setDepartment(employmentRequestDTO.getDepartment());
        employment.setPosition(employmentRequestDTO.getPosition());
        employment.setBaseSalary(employmentRequestDTO.getBaseSalary());
        employment.setStatus(employmentRequestDTO.getStatus());
        employment.setJoiningDate(employmentRequestDTO.getJoiningDate());

        employment = employmentRepository.save(employment);

        return mapToResponseDTO(employment);
    }

    @Override
    public EmploymentResponseDTO getEmploymentById(UUID id) {
        Employment employment = findEmploymentEntity(id);
        return mapToResponseDTO(employment);
    }

    @Override
    public EmploymentResponseDTO getEmploymentByCode(String code) {
        Employment employment = findEmploymentEntityByCode(code);
        return mapToResponseDTO(employment);
    }

    @Override
    public List<EmploymentResponseDTO> getAllEmployments() {
        return employmentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmploymentResponseDTO> getEmploymentsByEmployee(UUID employeeId) {
        User employee = userService.findUserById(employeeId);
        return employmentRepository.findByEmployee(employee).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEmployment(UUID id) {
        if (!employmentRepository.existsById(id)) {
            throw new NotFoundException("Employment not found with id: " + id);
        }
        employmentRepository.deleteById(id);
    }

    @Override
    public Employment findEmploymentEntity(UUID id) {
        return employmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employment not found with id: " + id));
    }

    @Override
    public Employment findEmploymentEntityByCode(String code) {
        return employmentRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Employment not found with code: " + code));
    }

    private EmploymentResponseDTO mapToResponseDTO(Employment employment) {
        return EmploymentResponseDTO.builder()
                .id(employment.getId())
                .code(employment.getCode())
                .employee(new UserResponseDTO(employment.getEmployee()))
                .department(employment.getDepartment())
                .position(employment.getPosition())
                .baseSalary(employment.getBaseSalary())
                .status(employment.getStatus())
                .joiningDate(employment.getJoiningDate())
                .build();
    }
}
