package tokyomap.oauth.application.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.services.register.CheckRegistrationAccessTokenService;
import tokyomap.oauth.domain.services.register.RegisterClientService;
import tokyomap.oauth.domain.services.register.UpdateClientApplicationService;
import tokyomap.oauth.dtos.ClientValidationResultDto;
import tokyomap.oauth.dtos.ReadClientResponseDto;
import tokyomap.oauth.dtos.RegisterClientRequestDto;
import tokyomap.oauth.dtos.RegisterClientResponseDto;
import tokyomap.oauth.dtos.ResponseClientDto;
import tokyomap.oauth.dtos.UnregisterClientRequestDto;
import tokyomap.oauth.dtos.UpdateClientRequestDto;
import tokyomap.oauth.dtos.UpdateClientResponseDto;
import tokyomap.oauth.utils.Logger;

@RestController
@RequestMapping("/register")
public class RegisterRestController {

  private final RegisterClientService registerClientService;
  private final CheckRegistrationAccessTokenService checkRegistrationAccessTokenService;
  private final UpdateClientApplicationService updateClientApplicationService;
  private final UnregisterClientApplicationService unregisterClientApplicationService;
  private final Logger logger;

  @Autowired
  public RegisterRestController(
      RegisterClientService registerClientService,
      CheckRegistrationAccessTokenService checkRegistrationAccessTokenService,
      UpdateClientApplicationService updateClientApplicationService,
      UnregisterClientApplicationService unregisterClientApplicationService,
      Logger logger
  ) {
    this.registerClientService = registerClientService;
    this.checkRegistrationAccessTokenService = checkRegistrationAccessTokenService;
    this.updateClientApplicationService = updateClientApplicationService;
    this.unregisterClientApplicationService = unregisterClientApplicationService;
    this.logger = logger;
  }

  /**
   * get a registered client
   * @param clientId
   * @param authorization
   * @return ReadClientResponseDto
   */
  @RequestMapping(path = "/{clientId}", method = RequestMethod.GET, headers = "Accept=application/json")
  public ReadClientResponseDto readClient(@PathVariable String clientId, @RequestHeader("Authorization") String authorization) {
    ResponseClientDto responseClientDto = this.checkAccessTokenRegistration(clientId, authorization, RequestMethod.GET);
    return new ReadClientResponseDto(responseClientDto);
  }

  /**
   * update the registered client
   * @param clientId
   * @param authorization
   * @param requestDto
   * @return UpdateClientResponseDto
   */
  @RequestMapping(path = "/{clientId}", method = RequestMethod.PUT, headers = {"Accept=application/json", "Content-Type=application/json"})
  public UpdateClientResponseDto updateClient(
      @PathVariable String clientId,
      @RequestHeader("Authorization") String authorization,
      @RequestBody UpdateClientRequestDto requestDto
  ) {
    ResponseClientDto responseClientDto = this.checkAccessTokenRegistration(clientId, authorization, RequestMethod.PUT);
    UpdateClientResponseDto responseDto = this.updateClientApplicationService.execute(responseClientDto, requestDto);
    return responseDto;
  }

  /**
   * unregister the client for the given clientId
   * @param clientId
   * @param authorization
   */
  @RequestMapping(path = "/{clientId}", method = RequestMethod.DELETE, headers = "Accept=application/json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unregisterClient(
      @PathVariable String clientId,
      @RequestHeader("Authorization") String authorization,
      @RequestBody UnregisterClientRequestDto requestDto
      ) {
    ResponseClientDto responseClientDto = this.checkAccessTokenRegistration(clientId, authorization, RequestMethod.DELETE);
    this.unregisterClientApplicationService.execute(responseClientDto.getClientId(), requestDto);
  }

  /**
   * check the given registration access token
   * @param clientId
   * @param authorization
   * @param requestMethod
   * @return ResponseClientDto
   * @throws ResponseStatusException
   */
  private ResponseClientDto checkAccessTokenRegistration(String clientId, String authorization, RequestMethod requestMethod) throws ResponseStatusException {

    try {
      Client clientRegistered = this.checkRegistrationAccessTokenService.checkRegistration(clientId, authorization);
      
      ResponseClientDto responseClientDto = new ResponseClientDto();
      responseClientDto.setClientId(clientRegistered.getClientId());
      responseClientDto.setClientSecret(clientRegistered.getClientSecret());
      responseClientDto.setClientName(clientRegistered.getClientName());
      responseClientDto.setClientUri(clientRegistered.getClientUri());
      responseClientDto.setRedirectUris(clientRegistered.getRedirectUris().split(" "));
      responseClientDto.setGrantTypes(clientRegistered.getGrantTypes().split(" "));
      responseClientDto.setResponseTypes(clientRegistered.getResponseTypes().split(" "));
      responseClientDto.setTokenEndpointAuthMethod(clientRegistered.getTokenEndpointAuthMethod());
      responseClientDto.setScopes(clientRegistered.getScopes().split(" "));
      responseClientDto.setRegistrationAccessToken(clientRegistered.getRegistrationAccessToken());
      responseClientDto.setRegistrationClientUri(clientRegistered.getRegistrationClientUri());
      responseClientDto.setExpiresAt(clientRegistered.getExpiresAt());
      return responseClientDto;

    }catch(Exception e) {
      HttpStatus httpStatus = requestMethod.equals(RequestMethod.DELETE) ? HttpStatus.NO_CONTENT : HttpStatus.INTERNAL_SERVER_ERROR;
      throw new ResponseStatusException(httpStatus);
    }
  }

  /**
   * register the given client
   * @param requestDto
   * @return RegisterClientResponseDto
   */
  @RequestMapping(method = RequestMethod.POST, headers = {"Accept=application/json", "Content-Type=application/json"})
  public RegisterClientResponseDto registerClient(@RequestBody RegisterClientRequestDto requestDto) {

    ClientValidationResultDto resultDto = this.registerClientService.execValidation(requestDto.getClient());
    Client clientRegistered = this.registerClientService.register(requestDto.getClient(), resultDto);
    
    ResponseClientDto responseClientDto = new ResponseClientDto();
    responseClientDto.setClientId(clientRegistered.getClientId());
    responseClientDto.setClientSecret(clientRegistered.getClientSecret());
    responseClientDto.setClientName(clientRegistered.getClientName());
    responseClientDto.setClientUri(clientRegistered.getClientUri());
    responseClientDto.setRedirectUris(clientRegistered.getRedirectUris().split(" "));
    responseClientDto.setGrantTypes(clientRegistered.getGrantTypes().split(" "));
    responseClientDto.setResponseTypes(clientRegistered.getResponseTypes().split(" "));
    responseClientDto.setTokenEndpointAuthMethod(clientRegistered.getTokenEndpointAuthMethod());
    responseClientDto.setScopes(clientRegistered.getScopes().split(" "));
    responseClientDto.setRegistrationAccessToken(clientRegistered.getRegistrationAccessToken());
    responseClientDto.setRegistrationClientUri(clientRegistered.getRegistrationClientUri());
    responseClientDto.setExpiresAt(clientRegistered.getExpiresAt());

    return new RegisterClientResponseDto(responseClientDto);
  }
}
