package tokyomap.oauth.domain.services.authorisation;

public class InvalidRedirectUriException extends RuntimeException {
  public InvalidRedirectUriException() {
    super("Invalid authReqParams.redirectUri.");
  }
}
