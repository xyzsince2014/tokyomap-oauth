package tokyomap.oauth.web.application.reservation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import javax.validation.*;

@Documented
@Constraint(validatedBy = {FinishTimeMustBeAfterStartTimeValidator.class})
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface FinishTimeMustBeAfterStartTime {
  String message() default "{mrs.app.reservation.FinishTimeMustBeAfterStartTime.message}";

  Class<?>[] groups() default {};

  Class<? extends  Payload>[] payload() default {};

  @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
  @Retention(RUNTIME)
  @Documented
  public @interface List {
    FinishTimeMustBeAfterStartTime[] value();
  }
}
