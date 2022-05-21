package tokyomap.oauth.application.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tokyomap.oauth.dtos.userinfo.token.IssueTokensRequestDto;
import tokyomap.oauth.dtos.userinfo.token.IssueTokensResponseDto;

@RestController
@RequestMapping("/token")
public class TokenRestController {

  private final TokenApplicationService tokenApplicationService;

  @Autowired
  public TokenRestController(TokenApplicationService tokenApplicationService) {
    this.tokenApplicationService = tokenApplicationService;
  }

  @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public IssueTokensResponseDto issueTokens(IssueTokensRequestDto requestDto, @RequestHeader("Authorization") String authorization) {
    IssueTokensResponseDto responseDto = tokenApplicationService.execute(requestDto, authorization);
    return responseDto;
  }
}
