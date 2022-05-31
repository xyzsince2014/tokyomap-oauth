package tokyomap.oauth.dtos;

import java.io.Serializable;

public class UpdateClientResponseDto implements Serializable {

  private static final long serialVersionUID = -2214581005531947697L;

  private ResponseClientDto client;

  public UpdateClientResponseDto(ResponseClientDto responseClientDto) {
    this.client = responseClientDto;
  }

  public ResponseClientDto getClient() {
    return client;
  }

  public void setClient(ResponseClientDto client) {
    this.client = client;
  }
}
