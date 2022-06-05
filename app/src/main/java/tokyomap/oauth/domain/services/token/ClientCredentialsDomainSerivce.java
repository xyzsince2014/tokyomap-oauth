package tokyomap.oauth.domain.services.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.logics.AuthCodeLogic;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.logics.UsrLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

@Service
public class ClientCredentialsDomainSerivce extends TokenDomainService<CredentialsDto> {

  private final TokenLogic tokenLogic;
  private final Logger logger;

  @Autowired
  public ClientCredentialsDomainSerivce(ClientLogic clientLogic, Decorder decorder, TokenLogic tokenLogic, Logger logger) {
    super(clientLogic, decorder);
    this.tokenLogic = tokenLogic;
    this.logger = logger;
  }

  /**
   * execute validation of request to the token endpoint
   * @return TokenValidationResultDto
   */
  @Override
  public TokenValidationResultDto<CredentialsDto> execValidation(GenerateTokensRequestDto requestDto, String authorization) {

    CredentialsDto credentialsDto = this.validateClient(requestDto, authorization);

    String[] requestedScope = requestDto.getScope();
    // todo:
    //    if (!util.isObjectlncluded(requestedScope, clientScope)) {
    //      throw new Error('invalid scope');
    //    }

    return new TokenValidationResultDto(credentialsDto.getId(), credentialsDto);
  }

  /**
   * generate tokens
   * @param tokenValidationResultDto
   * @return GenerateTokensResponseDto
   */
  @Override
  public GenerateTokensResponseDto generateTokens(TokenValidationResultDto<CredentialsDto> tokenValidationResultDto) {

    try {
      // the Client Credentials Flow should not have a user it's on behalf of
      GenerateTokensResponseDto responseDto = this.tokenLogic.generateTokens(tokenValidationResultDto.getClientId(), null, tokenValidationResultDto.getPayload().getScope(),false, null);

      // todo:
      //      if(!responseDto) {
      //        console.log(`${util.fetchCurrentDatetimeJst()} [issueTokensForClientCredentialsLogic.issueTokens] failed to issue tokens`);
      //        throw new Error('failed to issue tokens');
      //      }

      return responseDto;

    } catch (Exception e) {
      // todo: error handling
      e.printStackTrace();
      return null;
    }
  }
}
