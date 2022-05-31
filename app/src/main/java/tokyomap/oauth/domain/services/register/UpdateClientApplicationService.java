package tokyomap.oauth.domain.services.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.dtos.ClientValidationResultDto;
import tokyomap.oauth.dtos.ResponseClientDto;
import tokyomap.oauth.dtos.UpdateClientRequestDto;
import tokyomap.oauth.dtos.UpdateClientResponseDto;

@Service
public class UpdateClientApplicationService {

  private final UpdateClientDomainService updateClientDomainService;

  @Autowired
  public UpdateClientApplicationService(UpdateClientDomainService updateClientDomainService) {
    this.updateClientDomainService = updateClientDomainService;
  }

  public UpdateClientResponseDto execute(ResponseClientDto responseClientDto, UpdateClientRequestDto requestDto) {
    // todo: rename to ValidateRequestClientDtoResultDto
    ClientValidationResultDto validationResultDto = this.updateClientDomainService.execValidation(requestDto.getClient());
    Client clientUpdated = this.updateClientDomainService.update(requestDto.getClient(), responseClientDto, validationResultDto);
    return this.convertClientEntityToResponseDto(clientUpdated);
  }

  private UpdateClientResponseDto convertClientEntityToResponseDto(Client client) {
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

    return new UpdateClientResponseDto(responseClientDto);
  }
}
