package tokyomap.oauth.dtos;

import java.io.Serializable;

public class ReadClientResponseDto implements Serializable {

  private static final long serialVersionUID = 966095348686127071L;

  private ResponseClientDto clientRegistered;

  public ReadClientResponseDto(ResponseClientDto responseClientDto) {
    this.clientRegistered = responseClientDto;
  }

  public ResponseClientDto getClientRegistered() {
    return clientRegistered;
  }

  public void setClientRegistered(ResponseClientDto registeredClient) {
    this.clientRegistered = registeredClient;
  }
}
