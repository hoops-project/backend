package com.zerobase.hoops.gameCreator.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StartTimeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStartTime {
  String message() default "Start time must be on the hour";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
