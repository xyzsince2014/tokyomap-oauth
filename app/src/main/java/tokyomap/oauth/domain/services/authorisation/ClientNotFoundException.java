package tokyomap.oauth.domain.services.authorisation;

public class ClientNotFoundException extends RuntimeException {
  public ClientNotFoundException() {
    super("No matching client found.");
  }
}
