package tokyomap.oauth.domain.services.token;

public class InvalidTokenRequestException extends RuntimeException {
  public InvalidTokenRequestException(String message) {
    super("InvalidTokenRequestException: " + message);
  }
}
