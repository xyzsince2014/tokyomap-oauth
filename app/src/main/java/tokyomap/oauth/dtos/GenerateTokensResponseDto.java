package tokyomap.oauth.dtos;

import java.io.Serializable;

public class GenerateTokensResponseDto implements Serializable {

  private static final long serialVersionUID = 8788792708297075355L;

  private String tokenType;
  private String accessToken;
  private String refreshToken;
  private String idToken;
  private String scopes;

  public GenerateTokensResponseDto(String tokenType, String accessToken, String refreshToken, String idToken, String scopes) {
    this.tokenType = tokenType;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.idToken = idToken;
    this.scopes = scopes;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public String getIdToken() {
    return idToken;
  }

  public void setIdToken(String idToken) {
    this.idToken = idToken;
  }

  public String getScopes() {
    return scopes;
  }

  public void setScopes(String scope) {
    this.scopes = scopes;
  }
}
