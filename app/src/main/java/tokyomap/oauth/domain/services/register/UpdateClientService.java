package tokyomap.oauth.domain.services.register;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.dtos.ClientValidationResultDto;
import tokyomap.oauth.dtos.RequestClientDto;
import tokyomap.oauth.dtos.ResponseClientDto;

@Service
public class UpdateClientService extends ClientService {

  private final ClientLogic clientLogic;

  @Autowired
  public UpdateClientService(ClientLogic clientLogic) {
    this.clientLogic = clientLogic;
  }

  /**
   * execute additional validation
   * @param requestClientDto
   * @param responseClientDto
   */
  public void execAdditionalValidation(RequestClientDto requestClientDto, ResponseClientDto responseClientDto) {
    if (!requestClientDto.getClientId().equals(responseClientDto.getClientId())) {
      throw new InvalidClientException("invalid clientId.");
    }
    if(requestClientDto.getClientSecret() != null && !requestClientDto.getClientSecret().equals(responseClientDto.getClientSecret())) {
      throw new InvalidClientException("invalid clientSecret.");
    }
  }

  /**
   * update the registered client
   * @param requestClientDto
   * @param validationResultDto
   * @return clientUpdated
   */
  public Client update(RequestClientDto requestClientDto, ClientValidationResultDto validationResultDto) {

    Client clientToBeUpdated = new Client(
        requestClientDto.getClientId(),
        requestClientDto.getClientSecret(),
        requestClientDto.getClientName(),
        validationResultDto.getTokenEndpointAuthMethod() != null ? validationResultDto.getTokenEndpointAuthMethod() : requestClientDto.getTokenEndpointAuthMethod(),
        requestClientDto.getClientUri(),
        String.join(" ", requestClientDto.getRedirectUris()),
        validationResultDto.getGrantTypes() != null ? String.join(" ", validationResultDto.getGrantTypes()) : String.join(" ", requestClientDto.getGrantTypes()),
        validationResultDto.getResponseTypes() != null ? String.join(" ", validationResultDto.getResponseTypes()) : String.join(" ", requestClientDto.getResponseTypes()),
        String.join(" ", requestClientDto.getScopes()),
        RandomStringUtils.random(8, true, true),
        requestClientDto.getRegistrationClientUri()
    );

    Client clientUpdated = this.clientLogic.registerClient(clientToBeUpdated);

    return clientUpdated;
  }
}
