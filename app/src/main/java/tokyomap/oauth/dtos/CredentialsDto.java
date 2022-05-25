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
  private String[] scope;

  public CredentialsDto() {}

  public CredentialsDto(@Nullable String id, @Nullable String secret) {
    this.id = id;
    this.secret = secret;
  }

  public CredentialsDto(@Nullable String id, @Nullable String secret, @Nullable String[] scope) {
    this.id = id;
    this.secret = secret;
    this.scope = scope;
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
  public String[] getScope() {
    return scope;
  }

  public void setScope(@Nullable String[] scope) {
    this.scope = scope;
  }
}
