package com.sammy.vehiclemanagementsystem.annotations.nationalID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NationalIDValidator implements ConstraintValidator<ValidNationalID, String> {

    @Override
    public boolean isValid(String id, ConstraintValidatorContext context) {
        if (id == null || id.length() != 16) return false;

        try {

            if (id.charAt(0) != '1') return false;

            int year = Integer.parseInt(id.substring(1, 5));
            if (year < 1900 || year > 2025) return false;

            char gender = id.charAt(5);
            if (gender != '7' && gender != '8') return false;

            String serial = id.substring(6, 13);
            if (!serial.matches("\\d{7}")) return false;

            if (id.charAt(13) != '0') return false;

            String checkDigits = id.substring(14);
            if (!checkDigits.matches("\\d{2}")) return false;


            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

