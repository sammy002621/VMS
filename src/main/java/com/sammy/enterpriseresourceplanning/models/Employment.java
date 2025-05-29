package com.sammy.enterpriseresourceplanning.models;

import com.sammy.enterpriseresourceplanning.enums.EEmploymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employments")
public class Employment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    private String code;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private User employee;

    private String department;
    
    private String position;
    
    private Double baseSalary;
    
    @Enumerated(EnumType.STRING)
    private EEmploymentStatus status;
    
    private LocalDate joiningDate;
}