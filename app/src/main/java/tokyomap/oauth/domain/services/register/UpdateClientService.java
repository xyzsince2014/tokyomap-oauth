package tokyomap.oauth.domain.services.register;

import java.time.LocalDateTime;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.dtos.ClientValidationResultDto;
import tokyomap.oauth.dtos.RequestClientDto;
import tokyomap.oauth.dtos.ResponseClientDto;

@Service
public class UpdateClientService extends RegisterService {

  private final ClientLogic clientLogic;

  @Autowired
  public UpdateClientService(ClientLogic clientLogic) {
    this.clientLogic = clientLogic;
  }

  /**
   * execute additional validation
   * @param requestClientDto
   * @param responseClientDto
   * @return clientNameToUpdate
   */
  public String execAdditionalValidation(RequestClientDto requestClientDto, ResponseClientDto responseClientDto) {
    if (!requestClientDto.getClientId().equals(responseClientDto.getClientId())) {
      throw new InvalidClientException("invalid clientId.");
    }
    if(requestClientDto.getClientSecret() != null && !requestClientDto.getClientSecret().equals(responseClientDto.getClientSecret())) {
      throw new InvalidClientException("invalid clientSecret.");
    }

    return requestClientDto.getClientName();
  }

  /**
   * update the registered client
   * @param clientNameToUpdate
   * @param validationResultDto
   * @return clientUpdated
   */
  public Client execute(String clientNameToUpdate, ClientValidationResultDto validationResultDto, ResponseClientDto responseClientDto) {

    LocalDateTime now = LocalDateTime.now();

    Client clientToBeUpdated = new Client(
        responseClientDto.getClientId(),
        responseClientDto.getClientSecret(),
        clientNameToUpdate,
        validationResultDto.getTokenEndpointAuthMethod() != null ? validationResultDto.getTokenEndpointAuthMethod() : responseClientDto.getTokenEndpointAuthMethod(),
        responseClientDto.getClientUri(),
        String.join(" ", responseClientDto.getRedirectUris()),
        validationResultDto.getGrantTypes() != null ? String.join(" ", validationResultDto.getGrantTypes()) : String.join(" ", responseClientDto.getGrantTypes()),
        validationResultDto.getResponseTypes() != null ? String.join(" ", validationResultDto.getResponseTypes()) : String.join(" ", responseClientDto.getResponseTypes()),
        String.join(" ", responseClientDto.getScopes()),
        RandomStringUtils.random(8, true, true),
        responseClientDto.getRegistrationClientUri(),
        now.plusDays(90),
        responseClientDto.getCreatedAt(),
        now
    );

    Client clientUpdated = this.clientLogic.registerClient(clientToBeUpdated);

    return clientUpdated;
  }
}
