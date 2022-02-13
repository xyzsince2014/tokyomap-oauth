package tokyomap.oauth.web.domain.services.reservation;

public class AlreadyReservedException extends RuntimeException {
  public AlreadyReservedException(String message) {
    super(message);
  }
}
