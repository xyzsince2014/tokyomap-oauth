package tokyomap.oauth.domain.services.register;

public class InvalidClientException extends RuntimeException {
  public InvalidClientException(String message) {
    super("InvalidClientException: " + message);
  }
}
