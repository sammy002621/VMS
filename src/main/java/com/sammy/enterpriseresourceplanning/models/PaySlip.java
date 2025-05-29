package com.sammy.enterpriseresourceplanning.models;

import com.sammy.enterpriseresourceplanning.enums.EPaySlipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pay_slips")
public class PaySlip {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

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
    
    @Enumerated(EnumType.STRING)
    private EPaySlipStatus status;
}