package tokyomap.oauth.domain.services.register;

public class InvalidRegisterClientException extends RuntimeException {
  public InvalidRegisterClientException(String module, String message) {
    super("InvalidRegisterClientException: [" + module + "] " + message);
  }
}
