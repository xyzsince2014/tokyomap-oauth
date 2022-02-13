package tokyomap.oauth.web.application.reservation;

import java.time.LocalTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ThirtyMinutesUnitValidator implements ConstraintValidator<ThirtyMinutesUnit, LocalTime> {
  @Override
  public void initialize(ThirtyMinutesUnit constraintAnnotation) {}

  @Override
  public boolean isValid(LocalTime localTime, ConstraintValidatorContext context) {
    if(localTime == null) {
      return true;
    }
    return localTime.getMinute() % 30 == 0;
  }
}
