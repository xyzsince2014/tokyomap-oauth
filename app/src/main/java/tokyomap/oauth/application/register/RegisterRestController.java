package tokyomap.oauth.application.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.dtos.RegisterClientRequestDto;
import tokyomap.oauth.dtos.RegisterClientResponseDto;
import tokyomap.oauth.utils.Logger;

// todo: @CrossOrigin
@RestController
@RequestMapping("/register")
public class RegisterRestController {

  private final RegisterClientApplicationService registerClientApplicationService;
  private final Logger logger;

  @Autowired
  public RegisterRestController(RegisterClientApplicationService registerClientApplicationService, Logger logger) {
    this.registerClientApplicationService = registerClientApplicationService;
    this.logger = logger;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
  public RegisterClientResponseDto registerClient(@RequestBody RegisterClientRequestDto requestDto) {
    this.logger.log("RegisterRestController", "clientDto.client = " + requestDto.getClient().toString());

    RegisterClientResponseDto responseDto = this.registerClientApplicationService.execute(requestDto);

    this.logger.log("RegisterRestController", "responseDto = " + responseDto.getClient().toString());
    return responseDto;
  }
}
