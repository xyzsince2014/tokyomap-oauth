package tokyomap.oauth.application.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.services.token.TokenDomainService;
import tokyomap.oauth.dtos.userinfo.token.IssueTokensRequestDto;
import tokyomap.oauth.dtos.userinfo.token.IssueTokensResponseDto;
import tokyomap.oauth.dtos.userinfo.token.ValidationResultDto;

@Service
public class TokenApplicationService {

  private final TokenDomainService tokenDomainService;

  @Autowired
  public TokenApplicationService(TokenDomainService tokenDomainService) {
    this.tokenDomainService = tokenDomainService;
  }

  /**
   * execute token issuance for the given requestDto and authorization header
   * @return IssueTokensResponse
   */
  @Transactional // todo: create a transaction in tokenDomainService
  public IssueTokensResponseDto execute(IssueTokensRequestDto requestDto, String authorization) {
    ValidationResultDto validationResultDto = tokenDomainService.execValidation(requestDto, authorization);
    IssueTokensResponseDto response = tokenDomainService.issueTokens(validationResultDto);
    return response;
  }
}
