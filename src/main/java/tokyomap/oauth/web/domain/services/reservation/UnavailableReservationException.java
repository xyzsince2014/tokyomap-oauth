package tokyomap.oauth.web.domain.services.reservation;

public class UnavailableReservationException extends RuntimeException {
  public UnavailableReservationException(String message) {
    super(message);
  }
}
