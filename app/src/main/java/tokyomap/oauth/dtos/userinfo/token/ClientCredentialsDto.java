package tokyomap.oauth.dtos.userinfo.token;

import java.io.Serializable;
import javax.annotation.Nullable;

public class ClientCredentialsDto implements Serializable {

  private static final long serialVersionUID = -7858738725810070807L;

  @Nullable
  private String clientId;

  @Nullable
  private String clientSecret;

  @Nullable
  private String[] clientScope;

  public ClientCredentialsDto() {}

  public ClientCredentialsDto(@Nullable String clientId, @Nullable String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  public ClientCredentialsDto(@Nullable String clientId, @Nullable String clientSecret, @Nullable String[] clientScope) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.clientScope = clientScope;
  }

  @Nullable
  public String getClientId() {
    return clientId;
  }

  public void setClientId(@Nullable String clientId) {
    this.clientId = clientId;
  }

  @Nullable
  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(@Nullable String clientSecret) {
    this.clientSecret = clientSecret;
  }

  @Nullable
  public String[] getClientScope() {
    return clientScope;
  }

  public void setClientScope(@Nullable String[] clientScope) {
    this.clientScope = clientScope;
  }
}
