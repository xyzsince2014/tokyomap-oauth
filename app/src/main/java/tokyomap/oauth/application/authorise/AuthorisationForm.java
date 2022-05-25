package tokyomap.oauth.application.authorise;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

public class AuthorisationForm implements Serializable {

  private static final long serialVersionUID = 2928719149463279194L;

  @NotNull
  private String requestId;

  @NotNull
  private String username;

  @NotNull
  private String password;

  public AuthorisationForm() {
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
