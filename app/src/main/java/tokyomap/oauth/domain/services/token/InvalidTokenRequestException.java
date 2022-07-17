package tokyomap.oauth.domain.services.token;

public class InvalidTokenRequestException extends RuntimeException {

  private String errorMessage;

  public InvalidTokenRequestException(String errorMessage) {
    super("InvalidTokenRequestException: " + errorMessage);
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }
}
