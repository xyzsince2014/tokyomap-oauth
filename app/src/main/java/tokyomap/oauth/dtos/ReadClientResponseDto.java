package tokyomap.oauth.dtos;

import java.io.Serializable;

public class ReadClientResponseDto implements Serializable {

  private static final long serialVersionUID = 966095348686127071L;

  private ResponseClientDto client;

  public ReadClientResponseDto(ResponseClientDto responseClientDto) {
    this.client = responseClientDto;
  }

  public ResponseClientDto getClient() {
    return client;
  }

  public void setClient(ResponseClientDto client) {
    this.client = client;
  }
}
