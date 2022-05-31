package tokyomap.oauth.application.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.services.register.RegisterClientDomainService;
import tokyomap.oauth.dtos.ClientValidationResultDto;
import tokyomap.oauth.dtos.RegisterClientRequestDto;
import tokyomap.oauth.dtos.RegisterClientResponseDto;
import tokyomap.oauth.dtos.ResponseClientDto;

@Service
public class RegisterClientApplicationService {

  private final RegisterClientDomainService registerClientDomainService;

  @Autowired
  public RegisterClientApplicationService(RegisterClientDomainService registerClientDomainService) {
    this.registerClientDomainService = registerClientDomainService;
  }

  public RegisterClientResponseDto execute(RegisterClientRequestDto requestDto) {
    ClientValidationResultDto resultDto = this.registerClientDomainService.execValidation(requestDto.getClient());
    Client clientRegistered = this.registerClientDomainService.register(requestDto.getClient(), resultDto);
    return this.convertClientEntityToResponseDto(clientRegistered);
  }

  private RegisterClientResponseDto convertClientEntityToResponseDto(Client client) {
    ResponseClientDto responseClientDto = new ResponseClientDto();
    responseClientDto.setClientId(client.getClientId());
    responseClientDto.setClientSecret(client.getClientSecret());
    responseClientDto.setClientName(client.getClientName());
    responseClientDto.setClientUri(client.getClientUri());
    responseClientDto.setRedirectUris(client.getRedirectUris().split(" "));
    responseClientDto.setGrantTypes(client.getGrantTypes().split(" "));
    responseClientDto.setResponseTypes(client.getResponseTypes().split(" "));
    responseClientDto.setTokenEndpointAuthMethod(client.getTokenEndpointAuthMethod());
    responseClientDto.setScope(client.getScope().split(" "));
    responseClientDto.setRegistrationAccessToken(client.getRegistrationAccessToken());
    responseClientDto.setRegistrationClientUri(client.getRegistrationClientUri());
    responseClientDto.setExpiresAt(client.getExpiresAt());
    return new RegisterClientResponseDto(responseClientDto);
  }
}
