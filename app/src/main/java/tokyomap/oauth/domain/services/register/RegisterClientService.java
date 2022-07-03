package tokyomap.oauth.domain.services.register;

import java.time.LocalDateTime;
import java.util.Arrays;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.dtos.ClientValidationResultDto;
import tokyomap.oauth.dtos.RequestClientDto;

@Service
public class RegisterClientService extends RegisterService {

  private final ClientLogic clientLogic;

  @Autowired
  public RegisterClientService(ClientLogic clientLogic) {
    this.clientLogic = clientLogic;
  }

  /**
   * register the given client
   * @param requestClientDto
   * @param validationResultDto
   * @return clientRegistered
   */
  @Transactional
  public Client execute(RequestClientDto requestClientDto, ClientValidationResultDto validationResultDto) {

    String clientId = RandomStringUtils.random(8, true, true);
    String clientSecret = Arrays.stream(TOKEN_ENDPOINT_AUTH_METHODS).anyMatch(authMethod -> authMethod.equals(validationResultDto.getTokenEndpointAuthMethod())) ? RandomStringUtils.random(8, true, true) : null;
    String registrationAccessToken = RandomStringUtils.random(8, true, true);
    String registrationClientUri = REGISTRATION_ENDPOINT + "/"  + clientId;
    LocalDateTime now = LocalDateTime.now();

    // merge with requestClientDto
    Client client = new Client(
        clientId,
        clientSecret,
        requestClientDto.getClientName(),
        validationResultDto.getTokenEndpointAuthMethod() != null ? validationResultDto.getTokenEndpointAuthMethod() : requestClientDto.getTokenEndpointAuthMethod(),
        requestClientDto.getClientUri(),
        String.join(" ", requestClientDto.getRedirectUris()),
        validationResultDto.getGrantTypes() != null ? String.join(" ", validationResultDto.getGrantTypes()) : String.join(" ", requestClientDto.getGrantTypes()),
        validationResultDto.getResponseTypes() != null ? String.join(" ", validationResultDto.getResponseTypes()) : String.join(" ", requestClientDto.getResponseTypes()),
        String.join(" ", requestClientDto.getScopes()),
        registrationAccessToken,
        registrationClientUri,
        now.plusDays(90),
        now,
        now
    );

    Client clientRegistered = this.clientLogic.registerClient(client);

    return clientRegistered;
  }
}
