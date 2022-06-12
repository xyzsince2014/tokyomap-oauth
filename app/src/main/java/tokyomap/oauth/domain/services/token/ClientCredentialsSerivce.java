package tokyomap.oauth.domain.services.token;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

@Service
public class ClientCredentialsSerivce extends TokenService<CredentialsDto> {

  private final TokenLogic tokenLogic;
  private final Logger logger;

  @Autowired
  public ClientCredentialsSerivce(ClientLogic clientLogic, Decorder decorder, TokenLogic tokenLogic, Logger logger) {
    super(clientLogic, decorder, logger);
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
    String[] requestedScopes = requestDto.getScopes();

    if (!Arrays.asList(credentialsDto.getScopes()).containsAll(Arrays.asList(requestedScopes))) {
      throw new InvalidTokenRequestException("invalid scopes");
    }

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
      GenerateTokensResponseDto responseDto = this.tokenLogic.generateTokens(
          tokenValidationResultDto.getClientId(),
          null, tokenValidationResultDto.getPayload().getScopes(),
          false,
          null
      );

      if(responseDto == null) {
        throw new InvalidTokenRequestException("failed to issue tokens");
      }

      return responseDto;

    } catch (Exception e) {
      throw new InvalidTokenRequestException(e.getMessage());
    }
  }
}