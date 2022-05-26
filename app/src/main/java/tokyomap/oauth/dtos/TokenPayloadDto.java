package tokyomap.oauth.dtos;

import java.io.Serializable;

public class TokenPayloadDto implements Serializable {

  private static final long serialVersionUID = -3799224498048917339L;

  private String iss;
  private String sub;
  private String[] aud;
  private String iat;
  private String exp;
  private String jti;
  private String[] scope;
  private String clientId;

  public String getIss() {
    return iss;
  }

  public void setIss(String iss) {
    this.iss = iss;
  }

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public String[] getAud() {
    return aud;
  }

  public void setAud(String[] aud) {
    this.aud = aud;
  }

  public String getIat() {
    return iat;
  }

  public void setIat(String iat) {
    this.iat = iat;
  }

  public String getExp() {
    return exp;
  }

  public void setExp(String exp) {
    this.exp = exp;
  }

  public String getJti() {
    return jti;
  }

  public void setJti(String jti) {
    this.jti = jti;
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
}
