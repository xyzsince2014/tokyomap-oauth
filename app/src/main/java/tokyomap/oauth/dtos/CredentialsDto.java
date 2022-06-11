package tokyomap.oauth.dtos;

import java.io.Serializable;
import org.springframework.lang.Nullable;

public class CredentialsDto implements Serializable {

  private static final long serialVersionUID = 6501945367821634948L;

  @Nullable
  private String id;

  @Nullable
  private String secret;

  @Nullable
  private String[] scopes;

  public CredentialsDto() {}

  public CredentialsDto(@Nullable String id, @Nullable String secret) {
    this.id = id;
    this.secret = secret;
  }

  public CredentialsDto(@Nullable String id, @Nullable String secret, @Nullable String[] scopes) {
    this.id = id;
    this.secret = secret;
    this.scopes = scopes;
  }

  @Nullable
  public String getId() {
    return id;
  }

  public void setId(@Nullable String id) {
    this.id = id;
  }

  @Nullable
  public String getSecret() {
    return secret;
  }

  public void setSecret(@Nullable String secret) {
    this.secret = secret;
  }

  @Nullable
  public String[] getScopes() {
    return scopes;
  }

  public void setScopes(@Nullable String[] scopes) {
    this.scopes = scopes;
  }
}
