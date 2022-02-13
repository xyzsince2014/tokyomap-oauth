package tokyomap.oauth.web.application.reservation;

import java.io.Serializable;

import java.time.LocalTime;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@FinishTimeMustBeAfterStartTime(message = "set Start Time before Finish Time.")
public class ReservationForm implements Serializable {
  @NotNull(message = "necessary")
  @ThirtyMinutesUnit(message = "input Start Time in the unit of 30 mins.")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime startTime;

  @NotNull(message = "necessary")
  @ThirtyMinutesUnit(message = "input Finish Time in the unit of 30 mins.")
  @DateTimeFormat(pattern = "HH:mm")
  private LocalTime finishTime;

  public ReservationForm() {}

  public LocalTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalTime startTime) {
    this.startTime = startTime;
  }

  public LocalTime getFinishTime() {
    return finishTime;
  }

  public void setFinishTime(LocalTime finishTime) {
    this.finishTime = finishTime;
  }
}
