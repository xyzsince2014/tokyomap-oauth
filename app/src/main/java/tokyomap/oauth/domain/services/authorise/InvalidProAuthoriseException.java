package tokyomap.oauth.domain.services.authorise;

public class InvalidProAuthoriseException extends RuntimeException {
  public InvalidProAuthoriseException(String message) {
    super("InvalidProAuthoriseException: " + message);
  }
}
