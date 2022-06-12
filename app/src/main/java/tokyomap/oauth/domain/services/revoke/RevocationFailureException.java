package tokyomap.oauth.domain.services.revoke;

public class RevocationFailureException extends RuntimeException {
  public RevocationFailureException(String message) {
    super("RevocationFailureException: " + message);
  }
}
