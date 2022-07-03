package tokyomap.oauth.domain.services.common;

public class TokenScrutinyFailureException extends RuntimeException {
  public TokenScrutinyFailureException(String message) {
    super("TokenScrutinyFailureException: " + message);
  }
}
