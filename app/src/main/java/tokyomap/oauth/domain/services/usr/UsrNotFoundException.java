package tokyomap.oauth.domain.services.usr;

// todo: UsrNotFoundException should be put in this package?
public class UsrNotFoundException extends RuntimeException {
  public UsrNotFoundException(String message) {
    super(message);
  }
}
