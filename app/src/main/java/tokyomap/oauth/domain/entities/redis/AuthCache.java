package tokyomap.oauth.domain.entities.redis;

import java.io.Serializable;

public class AuthCache implements Serializable {

  private static final long serialVersionUID = -5514415646648723349L;

  private String sub;
  private String[] scopeRequested;
  private AuthReqParams authReqParams;

  // used to deserialisation by authCodeRedisTemplate
  public AuthCache() {}

  public AuthCache(String sub, String[] scopeRequested, AuthReqParams authReqParams) {
    this.sub = sub;
    this.scopeRequested = scopeRequested;
    this.authReqParams = authReqParams;
  }

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public String[] getScopeRequested() {
    return scopeRequested;
  }

  public void setScopeRequested(String[] scopeRequested) {
    this.scopeRequested = scopeRequested;
  }

  public AuthReqParams getAuthReqParams() {
    return authReqParams;
  }

  public void setAuthReqParams(AuthReqParams authReqParams) {
    this.authReqParams = authReqParams;
  }
}
