package com.sammy.enterpriseresourceplanning.annotations.user.phone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RwandanPhoneValidator implements ConstraintValidator<ValidRwandanPhone, String> {

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) return false;

        // Must be 10 digits and start with 078 or 079
        return phone.matches("07[89]\\d{7}");
    }
}

