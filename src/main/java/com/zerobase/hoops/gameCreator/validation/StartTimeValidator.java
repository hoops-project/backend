package com.zerobase.hoops.gameCreator.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartTimeValidator implements
    ConstraintValidator<ValidStartTime, LocalDateTime> {
  @Override
  public void initialize(ValidStartTime constraintAnnotation) {
  }

  @Override
  public boolean isValid(LocalDateTime dateTime, ConstraintValidatorContext context) {
    if (dateTime == null) {
      return true; // Null 값은 다른 검증에서 처리하도록 허용
    }
    return dateTime.getMinute() == 0 && dateTime.getSecond() == 0;
  }
}
