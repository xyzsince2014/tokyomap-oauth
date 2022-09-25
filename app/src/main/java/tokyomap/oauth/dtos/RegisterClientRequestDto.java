package tokyomap.oauth.dtos;

import java.io.Serializable;

public class RegisterClientRequestDto implements Serializable {

  private static final long serialVersionUID = 3386017190943027997L;

  private RequestClientDto client;

  public RequestClientDto getClient() {
    return client;
  }

  public void setClient(RequestClientDto requestClientDto) {
    this.client = requestClientDto;
  }
}
