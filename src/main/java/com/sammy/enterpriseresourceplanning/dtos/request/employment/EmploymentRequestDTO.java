package com.sammy.enterpriseresourceplanning.dtos.request.employment;

import com.sammy.enterpriseresourceplanning.annotations.employment.salary.ValidSalaryJoiningDate;
import com.sammy.enterpriseresourceplanning.annotations.user.joiningDate.ValidJoiningDate;
import com.sammy.enterpriseresourceplanning.enums.EEmploymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@ValidSalaryJoiningDate
public class EmploymentRequestDTO {

    private String code;

    @NotNull(message = "Employee ID is required")
    private UUID employeeId;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Position is required")
    private String position;

    @NotNull(message = "Base salary is required")
    @Positive(message = "Base salary must be positive")
    private Double baseSalary;

    @NotNull(message = "Status is required")
    private EEmploymentStatus status;

    @NotNull(message = "Joining date is required")
    @ValidJoiningDate
    private LocalDate joiningDate;
}
