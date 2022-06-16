package tokyomap.oauth.domain.entities.postgres;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_client")
public class Client implements Serializable {

  private static final long serialVersionUID = 236024034446422970L;

  @Id
  @Column(name = "client_id")
  private String clientId;

  @Column(name = "client_secret")
  private String clientSecret;

  @Column(name = "client_name")
  private String clientName;

  @Column(name = "token_endpoint_auth_method")
  private String tokenEndpointAuthMethod;

  @Column(name = "client_uri")
  private String clientUri;

  @Column(name = "redirect_uris")
  private String redirectUris;

  @Column(name = "grant_types")
  private String grantTypes;

  @Column(name = "response_types")
  private String responseTypes;

  @Column(name = "scopes")
  private String scopes;

  @Column(name = "registration_access_token")
  private String registrationAccessToken;

  @Column(name = "registration_client_uri")
  private String registrationClientUri;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Client() {}

  public Client(
      String clientId, String clientSecret, String clientName, String tokenEndpointAuthMethod, String clientUri,
      String redirectUris, String grantTypes, String responseTypes, String scopes, String registrationAccessToken, String registrationClientUri
  ) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.clientName = clientName;
    this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
    this.clientUri = clientUri;
    this.redirectUris = redirectUris;
    this.grantTypes = grantTypes;
    this.responseTypes = responseTypes;
    this.scopes = scopes;
    this.registrationAccessToken = registrationAccessToken;
    this.registrationClientUri = registrationClientUri;
    LocalDateTime ldt = LocalDateTime.now(); // todo: use JST
    this.expiresAt = ldt; // todo: set properly
    this.createdAt = ldt;
    this.updatedAt = ldt;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getClientName() {
    return clientName;
  }

  public void setClientName(String clientName) {
    this.clientName = clientName;
  }

  public String getTokenEndpointAuthMethod() {
    return tokenEndpointAuthMethod;
  }

  public void setTokenEndpointAuthMethod(String tokenEndpointAuthMethod) {
    this.tokenEndpointAuthMethod = tokenEndpointAuthMethod;
  }

  public String getClientUri() {
    return clientUri;
  }

  public void setClientUri(String clientUri) {
    this.clientUri = clientUri;
  }

  public String getRedirectUris() {
    return redirectUris;
  }

  public void setRedirectUris(String redirectUris) {
    this.redirectUris = redirectUris;
  }

  public String getGrantTypes() {
    return grantTypes;
  }

  public void setGrantTypes(String grantTypes) {
    this.grantTypes = grantTypes;
  }

  public String getResponseTypes() {
    return responseTypes;
  }

  public void setResponseTypes(String responseTypes) {
    this.responseTypes = responseTypes;
  }

  public String getScopes() {
    return scopes;
  }

  public void setScopes(String scopes) {
    this.scopes = scopes;
  }

  public String getRegistrationAccessToken() {
    return registrationAccessToken;
  }

  public void setRegistrationAccessToken(String registrationAccessToken) {
    this.registrationAccessToken = registrationAccessToken;
  }

  public String getRegistrationClientUri() {
    return registrationClientUri;
  }

  public void setRegistrationClientUri(String registrationClientUri) {
    this.registrationClientUri = registrationClientUri;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(LocalDateTime expiresAt) {
    this.expiresAt = expiresAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "clientId = " + this.clientId + ", cilentSecret = " + this.clientSecret
        + ", clientName = " + this.clientName + ", tokenEndpointAuthMethod = " + this.tokenEndpointAuthMethod
        + ", clientUri = " + this.clientUri + ", redirectUris = " + this.redirectUris
        + ", responseTypes = " + this.responseTypes + ", scopes = " + this.scopes
        + ", registrationAccessToken = " + this.registrationAccessToken
        + ", registrationClientUri = " + this.registrationClientUri;
  }
}
