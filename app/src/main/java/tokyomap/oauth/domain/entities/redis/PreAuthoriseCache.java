package tokyomap.oauth.domain.entities.redis;

import java.io.Serializable;

public class PreAuthoriseCache implements Serializable {

  private static final long serialVersionUID = -2333688999379632926L;

  private String responseType;
  private String[] scopes;
  private String clientId;
  private String redirectUri;
  private String state;
  private String codeChallenge;
  private String codeChallengeMethod;

  // used to deserialise values by RedisTemplate
  PreAuthoriseCache() {}

  public PreAuthoriseCache(
      String responseType, String[] scopes, String clientId, String redirectUri,
      String state, String codeChallenge, String codeChallengeMethod
  ) {
    this.responseType = responseType;
    this.scopes = scopes;
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

  public String[] getScopes() {
    return scopes;
  }

  public void setScopes(String[] scopes) {
    this.scopes = scopes;
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
    return "responseType = " + this.responseType + ", scopes = " + String.join(" ", this.scopes) + ", clientId = " + this.clientId
        + ", redirectUri = " + this.redirectUri + ", state = " + this.state + ", codeChallenge = " + this.codeChallenge
        + ", codeChallengeMethod = " + this.codeChallengeMethod;
  }
}
