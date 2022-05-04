package tokyomap.oauth.dtos;

import java.io.Serializable;

public class PreAuthoriseDto implements Serializable {

  private static final long serialVersionUID = -3731207544869794764L;

  private String client;
  private String requestId;
  private String requestedScope;

  public PreAuthoriseDto(String client, String requestId, String requestedScope) {
    this.client = client;
    this.requestId = requestId;
    this.requestedScope = requestedScope;
  }

  public String getClient() {
    return client;
  }

  public void setClient(String client) {
    this.client = client;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getRequestedScope() {
    return requestedScope;
  }

  public void setRequestedScope(String requestedScope) {
    this.requestedScope = requestedScope;
  }
}
