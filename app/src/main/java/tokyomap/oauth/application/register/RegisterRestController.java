package tokyomap.oauth.application.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tokyomap.oauth.domain.services.register.UpdateClientApplicationService;
import tokyomap.oauth.dtos.ReadClientResponseDto;
import tokyomap.oauth.dtos.RegisterClientRequestDto;
import tokyomap.oauth.dtos.RegisterClientResponseDto;
import tokyomap.oauth.dtos.ResponseClientDto;
import tokyomap.oauth.dtos.UnregisterClientRequestDto;
import tokyomap.oauth.dtos.UpdateClientRequestDto;
import tokyomap.oauth.dtos.UpdateClientResponseDto;
import tokyomap.oauth.utils.Logger;

@CrossOrigin // todo: handle preflight requests by private void handleCrossDomainRequest()
@RestController
@RequestMapping("/register")
public class RegisterRestController {

  private final RegisterClientApplicationService registerClientApplicationService;
  private final CheckRegistrationAccessTokenApplicationService checkRegistrationAccessTokenApplicationService;
  private final UpdateClientApplicationService updateClientApplicationService;
  private final UnregisterClientApplicationService unregisterClientApplicationService;
  private final Logger logger;

  @Autowired
  public RegisterRestController(
      RegisterClientApplicationService registerClientApplicationService,
      CheckRegistrationAccessTokenApplicationService checkRegistrationAccessTokenApplicationService,
      UpdateClientApplicationService updateClientApplicationService,
      UnregisterClientApplicationService unregisterClientApplicationService,
      Logger logger
  ) {
    this.registerClientApplicationService = registerClientApplicationService;
    this.checkRegistrationAccessTokenApplicationService = checkRegistrationAccessTokenApplicationService;
    this.updateClientApplicationService = updateClientApplicationService;
    this.unregisterClientApplicationService = unregisterClientApplicationService;
    this.logger = logger;
  }

  @RequestMapping(method = RequestMethod.POST, headers = {"Accept=application/json", "Content-Type=application/json"})
  public RegisterClientResponseDto registerClient(@RequestBody RegisterClientRequestDto requestDto) {
    this.logger.log("RegisterRestController", "clientDto.client = " + requestDto.getClient().toString());
    RegisterClientResponseDto responseDto = this.registerClientApplicationService.execute(requestDto);
    this.logger.log("RegisterRestController", "responseDto = " + responseDto.getClient().toString());
    return responseDto;
  }

  /**
   * get a registered client
   * @param clientId
   * @param authorization
   * @return ReadClientResponseDto
   */
  @RequestMapping(path = "/{clientId}", method = RequestMethod.GET, headers = "Accept=application/json")
  public ReadClientResponseDto readClient(@PathVariable String clientId, @RequestHeader("Authorization") String authorization) {
    ResponseClientDto responseClientDto = this.checkRegistrationAccessToken(clientId, authorization, RequestMethod.GET);
    this.logger.log("RegisterRestController", "clientRead = " + responseClientDto.toString());
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
    ResponseClientDto responseClientDto = this.checkRegistrationAccessToken(clientId, authorization, RequestMethod.PUT);
    UpdateClientResponseDto responseDto = this.updateClientApplicationService.execute(responseClientDto, requestDto);
    this.logger.log("RegisterRestController", "clientUpdated = " + responseDto.getClient().toString());
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
    ResponseClientDto responseClientDto = this.checkRegistrationAccessToken(clientId, authorization, RequestMethod.DELETE);
    this.unregisterClientApplicationService.execute(responseClientDto.getClientId(), requestDto);
    this.logger.log("RegisterRestController", "unregistered the client: clientId = " + clientId);
  }

  /**
   * check the given registration access token
   * @param clientId
   * @param authorization
   * @param requestMethod
   * @return ResponseClientDto
   * @throws ResponseStatusException
   */
  private ResponseClientDto checkRegistrationAccessToken(String clientId, String authorization, RequestMethod requestMethod) throws ResponseStatusException {

    try {
      ResponseClientDto responseClientDto = this.checkRegistrationAccessTokenApplicationService.execute(clientId, authorization);
      return responseClientDto;

    }catch(Exception e) {
      if(requestMethod.equals(RequestMethod.DELETE)) {
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
      }
      // todo: res.status(e.getStat(e.length, 3)).end();
      return null;
    }
  }
}
