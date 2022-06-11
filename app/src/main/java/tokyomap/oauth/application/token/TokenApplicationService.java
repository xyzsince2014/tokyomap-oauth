package tokyomap.oauth.application.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.redis.ProAuthoriseCache;
import tokyomap.oauth.domain.services.token.AuthorisationCodeFlowDomainSerivice;
import tokyomap.oauth.domain.services.token.ClientCredentialsDomainSerivce;
import tokyomap.oauth.domain.services.token.RefreshTokenDomainService;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenPayloadDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;

@Service
public class TokenApplicationService {

  private final AuthorisationCodeFlowDomainSerivice authorisationCodeFlowDomainSerivce;
  private final RefreshTokenDomainService refreshTokenDomainService;
  private final ClientCredentialsDomainSerivce clientCredentialsDomainSerivce;

  @Autowired
  public TokenApplicationService(
      AuthorisationCodeFlowDomainSerivice authorisationCodeFlowDomainSerivce,
      RefreshTokenDomainService refreshTokenDomainService,
      ClientCredentialsDomainSerivce clientCredentialsDomainSerivce
  ) {
    this.authorisationCodeFlowDomainSerivce = authorisationCodeFlowDomainSerivce;
    this.refreshTokenDomainService = refreshTokenDomainService;
    this.clientCredentialsDomainSerivce = clientCredentialsDomainSerivce;
  }

  /**
   * execute tokens issuance for the given requestDto and authorization header
   * @return IssueTokensResponse
   */
  @Transactional // todo: create a transaction properly
  public GenerateTokensResponseDto execute(GenerateTokensRequestDto requestDto, String authorization) {
    switch (requestDto.getGrantType()) {
      case "AUTHORISATION_CODE": { // todo: use a Constant
        TokenValidationResultDto<ProAuthoriseCache> tokenValidationResultDto = this.authorisationCodeFlowDomainSerivce.execValidation(requestDto, authorization);
        GenerateTokensResponseDto response = this.authorisationCodeFlowDomainSerivce.generateTokens(tokenValidationResultDto);
        return response;
      }
      case "REFRESH_TOKEN": { // todo: use a Constant
        TokenValidationResultDto<TokenPayloadDto> tokenValidationResultDto = this.refreshTokenDomainService.execValidation(requestDto, authorization);
        GenerateTokensResponseDto response = this.refreshTokenDomainService.generateTokens(tokenValidationResultDto);
        return response;
      }
      case "CLIENT_CREDENTIALS": { // todo: use a Constant
        TokenValidationResultDto<CredentialsDto> tokenValidationResultDto = this.clientCredentialsDomainSerivce.execValidation(requestDto, authorization);
        GenerateTokensResponseDto response = this.clientCredentialsDomainSerivce.generateTokens(tokenValidationResultDto);
        return response;
      }
      default: {
        // todo: res.status(statusCodes.BAD_REQUEST).json({error: "unsupported grantType"});
        return null;
      }
    }
  }
}
