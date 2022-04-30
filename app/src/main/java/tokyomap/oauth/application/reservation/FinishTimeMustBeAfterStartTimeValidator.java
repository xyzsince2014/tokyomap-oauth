package tokyomap.oauth.application.reservation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

// implements ConstraintValidator<constraint annotation class, class to be validated>
public class FinishTimeMustBeAfterStartTimeValidator implements ConstraintValidator<FinishTimeMustBeAfterStartTime, ReservationForm> {
  private String message;

  // initialize(constraint annotation class obj)
  @Override
  public void initialize(FinishTimeMustBeAfterStartTime constraintAnnotation) {
    this.message = constraintAnnotation.message();
  }

  // isValid(to-be-validated class obj, ConstraintValidatorContext class obj)
  @Override
  public boolean isValid(ReservationForm reservationForm, ConstraintValidatorContext context) {
    if(reservationForm.getStartTime() == null || reservationForm.getFinishTime() == null) {
      return true;
    }
    
    boolean isFinishTimeMustBeAfterStartTime = reservationForm.getFinishTime().isAfter(reservationForm.getStartTime());
    if(!isFinishTimeMustBeAfterStartTime) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(this.message).addPropertyNode("finishTime").addConstraintViolation();
    }

    return isFinishTimeMustBeAfterStartTime;
  }
}
