package com.sammy.vehiclemanagementsystem.annotations.vehicle.manufacturedYear;



import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidManufacturedYearValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidManufacturedYear {

    String message() default "Manufactured year must be between 1900 and the current year";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

