package com.sammy.enterpriseresourceplanning.dtos.response.employment;

import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import com.sammy.enterpriseresourceplanning.enums.EEmploymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentResponseDTO {
    private UUID id;
    private String code;
    private UserResponseDTO employee;
    private String department;
    private String position;
    private Double baseSalary;
    private EEmploymentStatus status;
    private LocalDate joiningDate;
}