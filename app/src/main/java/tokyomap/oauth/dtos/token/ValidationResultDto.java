package tokyomap.oauth.dtos.token;

import tokyomap.oauth.domain.entities.redis.AuthCache;

public class ValidationResultDto {
  private String clientId;
  private AuthCache authCache;

  public ValidationResultDto(String clientId, AuthCache authCache) {
    this.clientId = clientId;
    this.authCache = authCache;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public AuthCache getAuthCache() {
    return authCache;
  }

  public void setAuthCache(AuthCache authCache) {
    this.authCache = authCache;
  }
}
