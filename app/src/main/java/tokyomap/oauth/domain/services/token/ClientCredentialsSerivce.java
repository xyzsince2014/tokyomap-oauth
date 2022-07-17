package tokyomap.oauth.domain.services.token;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Decorder;

@Service
public class ClientCredentialsSerivce extends TokenService<CredentialsDto> {

  private final TokenLogic tokenLogic;

  @Autowired
  public ClientCredentialsSerivce(ClientLogic clientLogic, Decorder decorder, TokenLogic tokenLogic) {
    super(clientLogic, decorder);
    this.tokenLogic = tokenLogic;
  }

  /**
   * execute validation of request to the token endpoint
   * @return TokenValidationResultDto
   */
  @Override
  public TokenValidationResultDto<CredentialsDto> execValidation(GenerateTokensRequestDto requestDto, String authorization) throws InvalidTokenRequestException {

    CredentialsDto credentialsDto = this.validateClient(requestDto, authorization);
    String[] requestedScopes = requestDto.getScopes();

    if (!Arrays.asList(credentialsDto.getScopes()).containsAll(Arrays.asList(requestedScopes))) {
      throw new InvalidTokenRequestException("Invalid Scopes");
    }

    return new TokenValidationResultDto(credentialsDto.getId(), credentialsDto);
  }

  /**
   * generate tokens
   * @param tokenValidationResultDto
   * @return GenerateTokensResponseDto
   */
  @Override
  @Transactional
  public GenerateTokensResponseDto execute(TokenValidationResultDto<CredentialsDto> tokenValidationResultDto) throws Exception {

    // the Client Credentials Flow should not have a user it's on behalf of
    GenerateTokensResponseDto responseDto = this.tokenLogic.generateTokens(
        tokenValidationResultDto.getClientId(),
        null, tokenValidationResultDto.getPayload().getScopes(),
        false,
        null
    );

    return responseDto;
  }
}
