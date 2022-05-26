package tokyomap.oauth.application.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.redis.AuthCache;
import tokyomap.oauth.domain.services.token.AuthorisationCodeFlowDomainSerivice;
import tokyomap.oauth.domain.services.token.RefreshTokenDomainService;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenPayloadDto;
import tokyomap.oauth.dtos.ValidationResultDto;

@Service
public class TokenApplicationService {

  private final AuthorisationCodeFlowDomainSerivice authorisationCodeFlowDomainSerivce;
  private final RefreshTokenDomainService refreshTokenDomainService;

  @Autowired
  public TokenApplicationService(AuthorisationCodeFlowDomainSerivice authorisationCodeFlowDomainSerivce, RefreshTokenDomainService refreshTokenDomainService) {
    this.authorisationCodeFlowDomainSerivce = authorisationCodeFlowDomainSerivce;
    this.refreshTokenDomainService = refreshTokenDomainService;
  }

  /**
   * execute tokens issuance for the given requestDto and authorization header
   * @return IssueTokensResponse
   */
  @Transactional // todo: create a transaction properly
  public GenerateTokensResponseDto execute(GenerateTokensRequestDto requestDto, String authorization) {
    switch (requestDto.getGrantType()) {
      case "AUTHORISATION_CODE": { // todo: use a Constant
        ValidationResultDto<AuthCache> validationResultDto = this.authorisationCodeFlowDomainSerivce.execValidation(requestDto, authorization);
        GenerateTokensResponseDto response = this.authorisationCodeFlowDomainSerivce.generateTokens(validationResultDto);
        return response;
      }
      case "REFRESH_TOKEN": { // todo: use a Constant
        ValidationResultDto<TokenPayloadDto> validationResultDto = this.refreshTokenDomainService.execValidation(requestDto, authorization);
        GenerateTokensResponseDto response = this.refreshTokenDomainService.generateTokens(validationResultDto);
        return response;
      }
      // todo:
//      case "CLIENT_CREDENTIALS": {
//        return this.clientCredentialsDomainSerivce;
//      }
      default: {
        // todo: res.status(statusCodes.BAD_REQUEST).json({error: "unsupported grantType"});
        return null;
      }
    }
  }
}
