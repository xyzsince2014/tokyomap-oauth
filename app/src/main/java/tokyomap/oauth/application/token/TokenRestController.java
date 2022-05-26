package tokyomap.oauth.application.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;

@RestController
@RequestMapping("/token")
public class TokenRestController {

  private final TokenApplicationService tokenApplicationService;

  @Autowired
  public TokenRestController(TokenApplicationService tokenApplicationService) {
    this.tokenApplicationService = tokenApplicationService;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public GenerateTokensResponseDto generateTokens(GenerateTokensRequestDto requestDto, @RequestHeader("Authorization") String authorization) {
    GenerateTokensResponseDto responseDto = tokenApplicationService.execute(requestDto, authorization);
    return responseDto;
  }
}
