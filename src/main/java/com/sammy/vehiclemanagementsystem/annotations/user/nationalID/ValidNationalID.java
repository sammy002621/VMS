package com.sammy.vehiclemanagementsystem.annotations.user.nationalID;

import com.sammy.vehiclemanagementsystem.annotations.user.nationalID.NationalIDValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NationalIDValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNationalID {
    String message() default "Invalid Rwandan National ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

