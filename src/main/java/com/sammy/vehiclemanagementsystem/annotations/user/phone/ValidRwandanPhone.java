package com.sammy.vehiclemanagementsystem.annotations.phone;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RwandanPhoneValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRwandanPhone {
    String message() default "Invalid Rwandan phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
