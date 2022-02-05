package tokyomap.oauth.domain.services.reservation;

public class UnavailableReservationException extends RuntimeException {
  public UnavailableReservationException(String message) {
    super(message);
  }
}
