package tokyomap.oauth.dtos;

import java.io.Serializable;

public class RegisterClientResponseDto implements Serializable {

  private static final long serialVersionUID = -4801382098708440662L;

  private ResponseClientDto client;

  public RegisterClientResponseDto(ResponseClientDto responseClientDto) {
    this.client = responseClientDto;
  }

  public ResponseClientDto getClient() {
    return client;
  }

  public void setClient(ResponseClientDto responseClientDto) {
    this.client = responseClientDto;
  }
}
