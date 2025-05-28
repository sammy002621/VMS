package com.sammy.vehiclemanagementsystem.annotations.vehicle.manufacturedYear;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class ValidManufacturedYearValidator implements ConstraintValidator<ValidManufacturedYear, Integer> {

    private static final int MIN_YEAR = 1900;

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return false; // or true, depending on if nulls are allowed
        int currentYear = Year.now().getValue();
        return value >= MIN_YEAR && value <= currentYear;
    }
}

