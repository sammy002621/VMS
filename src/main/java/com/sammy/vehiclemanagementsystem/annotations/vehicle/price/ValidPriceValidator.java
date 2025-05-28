package com.sammy.vehiclemanagementsystem.annotations.vehicle.price;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPriceValidator implements ConstraintValidator<ValidPrice, Double> {

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return  value > 0;
    }
}

