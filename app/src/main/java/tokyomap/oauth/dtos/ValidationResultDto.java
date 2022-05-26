package tokyomap.oauth.dtos;

public class ValidationResultDto<T> {
  private String clientId;
  private T payload;

  public ValidationResultDto(String clientId, T payload) {
    this.clientId = clientId;
    this.payload = payload;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public T getPayload() { return payload; }

  public void setPayload(T payload) { this.payload = payload; }
}
