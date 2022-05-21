package tokyomap.oauth.application.userInfo;

import java.io.Serializable;

public class AccessTokenDto implements Serializable {

  private static final long serialVersionUID = -6729188072985065794L;

  private String iss;
  private String sub;
  private String[] aud;
  private String lat;
  private String exp;
  private String jti;
  private String[] scope;
  private String clientId;

  public AccessTokenDto(String iss, String sub, String[] aud, String lat, String exp, String jti, String[] scope, String clientId) {
    this.iss = iss;
    this.sub = sub;
    this.aud = aud;
    this.lat = lat;
    this.exp = exp;
    this.jti = jti;
    this.scope = scope;
    this.clientId = clientId;
  }

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

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
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
