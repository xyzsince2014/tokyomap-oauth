package tokyomap.oauth.domain.services.authorise;

public class InvalidRedirectUriException extends RuntimeException {
  public InvalidRedirectUriException() {
    super("Invalid authReqParams.redirectUri.");
  }
}
