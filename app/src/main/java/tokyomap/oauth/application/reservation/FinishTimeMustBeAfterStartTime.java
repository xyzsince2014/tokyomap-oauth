package tokyomap.oauth.application.reservation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.*;

import javax.validation.*;

@Documented // display @FinishTimeMustBeAfterStartTimeValidator link in an annotated class's Javadoc
@Constraint(validatedBy = {FinishTimeMustBeAfterStartTimeValidator.class}) // validate by FinishTimeMustBeAfterStartTimeValidator class
@Target({TYPE, ANNOTATION_TYPE}) // targets to be annotated, e.g. TYPE: classes, interfaces, annotations, FIELD: fields, enums, etc.
@Retention(RUNTIME) // how long the annotation is to be retained. SOURCE: destroyed on compilation, CLASS: retained on class files but not executed on runtime, RUNTIME: still retained on class files and executed by JVM on runtime
public @interface FinishTimeMustBeAfterStartTime { // an annotation is an interface

  // alert message
  String message() default "{mrs.app.reservation.FinishTimeMustBeAfterStartTime.message}";

  // field to customise validation group
  Class<?>[] groups() default {};

  // field to hold meta info for validated objects
  Class<? extends  Payload>[] payload() default {};

  // ???
  @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
  @Documented
  @Retention(RUNTIME)
  @interface List {
    FinishTimeMustBeAfterStartTime[] value();
  }
}
