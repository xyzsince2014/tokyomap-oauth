package tokyomap.oauth.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.LocalDateTime;

public class TokenPayloadDto implements Serializable {

  private static final long serialVersionUID = -3799224498048917339L;

  private String iss;

  private String sub;

  private String[] aud;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.nnn")
  private LocalDateTime iat;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.nnn")
  private LocalDateTime exp;

  private String jti;

  private String[] scopes;

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

  public LocalDateTime getIat() {
    return iat;
  }

  public void setIat(LocalDateTime iat) {
    this.iat = iat;
  }

  public LocalDateTime getExp() {
    return exp;
  }

  public void setExp(LocalDateTime exp) {
    this.exp = exp;
  }

  public String getJti() {
    return jti;
  }

  public void setJti(String jti) {
    this.jti = jti;
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

  @Override
  public String toString() {
    return "iss = " + this.iss + ", sub = " + this.sub + ", String.join(\" \", aud) = " + String.join(" ", this.aud)
        + ", iat = " + this.iat.toString() + ", exp = " + this.exp.toString() + ", jti = " + this.jti
        + ", String.join(\" \", scopes) = " + String.join(" ", this.scopes) + ", clientId = " + this.clientId;
  }
}
