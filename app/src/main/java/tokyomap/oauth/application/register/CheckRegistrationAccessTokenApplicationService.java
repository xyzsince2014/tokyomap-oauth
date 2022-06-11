package tokyomap.oauth.application.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.services.register.CheckRegistrationAccessTokenDomainService;
import tokyomap.oauth.dtos.ResponseClientDto;

// todo: rename the class or merge the class to another one
@Service
public class CheckRegistrationAccessTokenApplicationService {

  private final CheckRegistrationAccessTokenDomainService checkRegistrationAccessTokenDomainService;

  @Autowired
  public CheckRegistrationAccessTokenApplicationService(CheckRegistrationAccessTokenDomainService checkRegistrationAccessTokenDomainService) {
    this.checkRegistrationAccessTokenDomainService = checkRegistrationAccessTokenDomainService;
  }

  public ResponseClientDto execute(String clientId, String authorization) throws Exception {
    Client clientRegistered = this.checkRegistrationAccessTokenDomainService.checkRegistration(clientId, authorization);
    ResponseClientDto responseClientDto = convertClientEntityToResponseClientDto(clientRegistered);
    return responseClientDto;
  }

  private ResponseClientDto convertClientEntityToResponseClientDto(Client client) {
    ResponseClientDto responseClientDto = new ResponseClientDto();
    responseClientDto.setClientId(client.getClientId());
    responseClientDto.setClientSecret(client.getClientSecret());
    responseClientDto.setClientName(client.getClientName());
    responseClientDto.setClientUri(client.getClientUri());
    responseClientDto.setRedirectUris(client.getRedirectUris().split(" "));
    responseClientDto.setGrantTypes(client.getGrantTypes().split(" "));
    responseClientDto.setResponseTypes(client.getResponseTypes().split(" "));
    responseClientDto.setTokenEndpointAuthMethod(client.getTokenEndpointAuthMethod());
    responseClientDto.setScopes(client.getScopes().split(" "));
    responseClientDto.setRegistrationAccessToken(client.getRegistrationAccessToken());
    responseClientDto.setRegistrationClientUri(client.getRegistrationClientUri());
    responseClientDto.setExpiresAt(client.getExpiresAt());
    return responseClientDto;
  }
}
