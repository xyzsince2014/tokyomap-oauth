package tokyomap.oauth.application.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tokyomap.oauth.dtos.ReadClientResponseDto;
import tokyomap.oauth.dtos.RegisterClientRequestDto;
import tokyomap.oauth.dtos.RegisterClientResponseDto;
import tokyomap.oauth.dtos.ResponseClientDto;
import tokyomap.oauth.utils.Logger;

@RestController
@RequestMapping("/register")
public class RegisterRestController {

  private final RegisterClientApplicationService registerClientApplicationService;
  private final CheckRegistrationAccessTokenApplicationService checkRegistrationAccessTokenApplicationService;
  private final Logger logger;

  @Autowired
  public RegisterRestController(
      RegisterClientApplicationService registerClientApplicationService,
      CheckRegistrationAccessTokenApplicationService checkRegistrationAccessTokenApplicationService,
      Logger logger
  ) {
    this.registerClientApplicationService = registerClientApplicationService;
    this.checkRegistrationAccessTokenApplicationService = checkRegistrationAccessTokenApplicationService;
    this.logger = logger;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE}) // todo: use headers="Content-Type=application/json"
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
  @RequestMapping(path = "/{clientId}", method = RequestMethod.GET)
  public ReadClientResponseDto readClient(@PathVariable String clientId, @RequestHeader("Authorization") String authorization) {
    ResponseClientDto responseClientDto = this.checkRegistrationAccessToken(clientId, authorization, RequestMethod.GET);
    this.logger.log("RegisterRestController", "clientRead = " + responseClientDto.toString());
    return new ReadClientResponseDto(responseClientDto);
  }

  /**
   * check the given access token for registered client
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
