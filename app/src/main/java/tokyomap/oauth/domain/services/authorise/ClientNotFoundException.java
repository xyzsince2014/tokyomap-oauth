package tokyomap.oauth.domain.services.authorise;

public class ClientNotFoundException extends RuntimeException {
  public ClientNotFoundException() {
    super("No matching client found.");
  }
}
