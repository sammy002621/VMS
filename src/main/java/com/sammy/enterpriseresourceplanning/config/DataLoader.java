package com.sammy.enterpriseresourceplanning.config;

import com.sammy.enterpriseresourceplanning.dtos.request.deduction.DeductionRequestDTO;
import com.sammy.enterpriseresourceplanning.enums.ERole;
import com.sammy.enterpriseresourceplanning.models.Role;
import com.sammy.enterpriseresourceplanning.repositories.IRoleRepository;
import com.sammy.enterpriseresourceplanning.services.IDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final IRoleRepository roleRepository;
    private final IDeductionService deductionService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        initRoles();
        
        // Initialize deductions if they don't exist
        initDeductions();
    }
    
    private void initRoles() {
        if (roleRepository.count() == 0) {
            List<Role> roles = Arrays.stream(ERole.values())
                    .map(role -> Role.builder().name(role).build())
                    .toList();
            
            roleRepository.saveAll(roles);
        }
    }
    
    private void initDeductions() {
        try {
            // Check if deductions already exist
            if (deductionService.getAllDeductions().isEmpty()) {
                // Create the required deductions with specified percentages
                createDeduction("EmployeeTax", 30.0);
                createDeduction("Pension", 6.0); // Updated from 3% to 6% as per requirements
                createDeduction("MedicalInsurance", 5.0);
                createDeduction("Others", 5.0);
                createDeduction("Housing", 14.0);
                createDeduction("Transport", 14.0);
            }
        } catch (Exception e) {
            System.err.println("Error initializing deductions: " + e.getMessage());
        }
    }
    
    private void createDeduction(String name, Double percentage) {
        DeductionRequestDTO deductionRequestDTO = DeductionRequestDTO.builder()
                .name(name)
                .percentage(percentage)
                .build();
        
        deductionService.createDeduction(deductionRequestDTO);
    }
}