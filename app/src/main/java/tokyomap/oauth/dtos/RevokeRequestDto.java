package tokyomap.oauth.dtos;

import java.io.Serializable;
import javax.annotation.Nullable;

public class RevokeRequestDto implements Serializable {

  private static final long serialVersionUID = 4257687656585158577L;

  @Nullable
  private String accessToken;

  @Nullable
  private String refreshToken;

  @Nullable
  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(@Nullable String accessToken) {
    this.accessToken = accessToken;
  }

  @Nullable
  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(@Nullable String refreshToken) {
    this.refreshToken = refreshToken;
  }

  @Override
  public String toString() {
    return "accessToken = " + this.accessToken + ", refreshToken = " + this.refreshToken;
  }
}
