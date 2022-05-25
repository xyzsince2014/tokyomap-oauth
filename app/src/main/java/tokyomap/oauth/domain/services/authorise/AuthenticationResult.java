package tokyomap.oauth.domain.services.authorise;

import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.entities.redis.AuthReqParams;

class AuthenticationResult {
  private Usr usr;
  private String[] scopeRequested;
  private AuthReqParams authReqParams;

  AuthenticationResult(Usr usr, String[] scopeRequested, AuthReqParams authReqParams) {
    this.usr = usr;
    this.scopeRequested = scopeRequested;
    this.authReqParams = authReqParams;
  }

  Usr getUsr() {
    return usr;
  }

  void setUsr(Usr usr) {
    this.usr = usr;
  }

  String[] getScopeRequested() {
    return scopeRequested;
  }

  void setScopeRequested(String[] scopeRequested) {
    this.scopeRequested = scopeRequested;
  }

  AuthReqParams getAuthReqParams() {
    return authReqParams;
  }

  void setAuthReqParams(AuthReqParams authReqParams) {
    this.authReqParams = authReqParams;
  }
}
