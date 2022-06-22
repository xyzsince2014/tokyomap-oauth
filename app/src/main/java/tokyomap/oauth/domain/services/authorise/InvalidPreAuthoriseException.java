package tokyomap.oauth.domain.services.authorise;

public class InvalidPreAuthoriseException extends RuntimeException {
  public InvalidPreAuthoriseException(String message) {
    super("InvalidPreAuthoriseException: " + message);
  }
}
