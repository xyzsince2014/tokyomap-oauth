package tokyomap.oauth.application.reservation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FinishTimeMustBeAfterStartTimeValidator implements ConstraintValidator<FinishTimeMustBeAfterStartTime, ReservationForm> {
  private String message;

  @Override
  public void initialize(FinishTimeMustBeAfterStartTime constraintAnnotation) {
    this.message = constraintAnnotation.message();
  }

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
