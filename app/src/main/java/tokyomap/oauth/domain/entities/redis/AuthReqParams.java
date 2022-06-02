package tokyomap.oauth.domain.entities.redis;

import java.io.Serializable;

public class AuthReqParams implements Serializable {

  private static final long serialVersionUID = -2333688999379632926L;

  private String responseType;
  private String[] scope;
  private String clientId;
  private String redirectUri;
  private String state;
  private String codeChallenge;
  private String codeChallengeMethod;

  // used to deserialise values by authReqParamsRedisTemplate
  AuthReqParams() {}

  public AuthReqParams(
      String responseType, String[] scope, String clientId, String redirectUri,
      String state, String codeChallenge, String codeChallengeMethod
  ) {
    this.responseType = responseType;
    this.scope = scope;
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.state = state;
    this.codeChallenge = codeChallenge;
    this.codeChallengeMethod = codeChallengeMethod;
  }

  public String getResponseType() {
    return responseType;
  }

  public void setResponseType(String responseType) {
    this.responseType = responseType;
  }

  public String[] getScope() {
    return scope;
  }

  public void setScope(String[] scope) {
    this.scope = scope;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getRedirectUri() {
    return redirectUri;
  }

  public void setRedirectUri(String redirectUri) {
    this.redirectUri = redirectUri;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCodeChallenge() {
    return codeChallenge;
  }

  public void setCodeChallenge(String codeChallenge) {
    this.codeChallenge = codeChallenge;
  }

  public String getCodeChallengeMethod() {
    return codeChallengeMethod;
  }

  public void setCodeChallengeMethod(String codeChallengeMethod) {
    this.codeChallengeMethod = codeChallengeMethod;
  }

  @Override
  public String toString() {
    return "responseType = " + this.responseType + ", scope = " + this.scope + ", clientId = " + this.clientId
        + ", redirectUri = " + this.redirectUri + ", state = " + this.state + ", codeChallenge = " + this.codeChallenge
        + ", codeChallengeMethod = " + this.codeChallengeMethod;
  }
}
