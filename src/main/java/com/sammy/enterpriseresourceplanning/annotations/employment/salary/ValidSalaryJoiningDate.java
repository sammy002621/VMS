package com.sammy.enterpriseresourceplanning.annotations.employment.salary;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SalaryJoiningDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSalaryJoiningDate {
    String message() default "Salary must be greater than the number of days until the joining date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
