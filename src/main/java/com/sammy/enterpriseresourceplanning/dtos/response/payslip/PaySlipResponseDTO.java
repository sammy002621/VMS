package com.sammy.enterpriseresourceplanning.dtos.response.payslip;

import com.sammy.enterpriseresourceplanning.dtos.response.user.UserResponseDTO;
import com.sammy.enterpriseresourceplanning.enums.EPaySlipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaySlipResponseDTO {
    private UUID id;
    private UserResponseDTO employee;
    private Double houseAmount;
    private Double transportAmount;
    private Double employeeTaxAmount;
    private Double pensionAmount;
    private Double medicalInsuranceAmount;
    private Double otherTaxAmount;
    private Double grossSalary;
    private Double netSalary;
    private Integer month;
    private Integer year;
    private EPaySlipStatus status;
}