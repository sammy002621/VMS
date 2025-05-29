package com.sammy.enterpriseresourceplanning.annotations.employment.salary;

import com.sammy.enterpriseresourceplanning.dtos.request.employment.EmploymentRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SalaryJoiningDateValidator implements ConstraintValidator<ValidSalaryJoiningDate, EmploymentRequestDTO> {

    @Override
    public boolean isValid(EmploymentRequestDTO employmentRequestDTO, ConstraintValidatorContext context) {
        if (employmentRequestDTO == null || 
            employmentRequestDTO.getBaseSalary() == null || 
            employmentRequestDTO.getJoiningDate() == null) {
            return true; // Let @NotNull handle null validation
        }

        Double baseSalary = employmentRequestDTO.getBaseSalary();
        LocalDate joiningDate = employmentRequestDTO.getJoiningDate();

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Calculate days between current date and joining date
        long daysUntilJoining = ChronoUnit.DAYS.between(currentDate, joiningDate);

        // If joining date is in the future, salary should be greater than days until joining
        // This ensures that salary is proportional to how far in the future the joining date is
        if (daysUntilJoining > 0) {
            return baseSalary > daysUntilJoining;
        }

        // If joining date is today or in the past (should be handled by @ValidJoiningDate),
        // any positive salary is valid
        return baseSalary > 0;
    }
}
