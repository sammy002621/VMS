package com.sammy.enterpriseresourceplanning.annotations.user.joiningDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class JoiningDateValidator implements ConstraintValidator<ValidJoiningDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate joiningDate, ConstraintValidatorContext context) {
        if (joiningDate == null) {
            return true; // Let @NotNull handle null validation
        }
        
        // Check if the joining date is today or in the future
        LocalDate today = LocalDate.now();
        return !joiningDate.isBefore(today);
    }
}