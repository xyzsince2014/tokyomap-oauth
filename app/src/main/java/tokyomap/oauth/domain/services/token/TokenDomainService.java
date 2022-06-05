package tokyomap.oauth.domain.services.token;

import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

public abstract class TokenDomainService<T> {

  private final ClientLogic clientLogic;
  private final Decorder decorder;
  private final Logger logger;

  public TokenDomainService(ClientLogic clientLogic, Decorder decorder, Logger logger) {
    this.clientLogic = clientLogic;
    this.decorder = decorder;
    this.logger = logger;
  }

  /**
   * execute validation of request to the token endpoint
   * @return TokenValidationResultDto
   */
  public abstract TokenValidationResultDto<T> execValidation(GenerateTokensRequestDto requestDto, String authorization);

  /**
   * generate tokens
   * @param tokenValidationResultDto
   * @return GenerateTokensResponseDto
   */
  public abstract GenerateTokensResponseDto generateTokens(TokenValidationResultDto<T> tokenValidationResultDto);

  /**
   * validate client
   * @param requestDto
   * @param authorization
   * @return
   */
  protected CredentialsDto validateClient(GenerateTokensRequestDto requestDto, String authorization) {

    String clientId = "";
    String clientSecret = "";

    // fetch clientId & clientSecret from the authorization header or the post params, then check them
    CredentialsDto credentialsDto = this.decorder.decodeCredentials(authorization);

    clientId = credentialsDto.getId();
    clientSecret = credentialsDto.getSecret();

    if (requestDto.getClientId() != null) {
      if (credentialsDto.getId() != null) {
        // return an error if we've already seen the client's credentials in the authorization header
        // todo: throw new Error('invalid clientId');
      }
      clientId = requestDto.getClientId();
      clientSecret = requestDto.getClientSecret();
    }

    Client client = this.clientLogic.getClientByClientId(clientId);
    // todo: validate client
    //  if (client == null || client.get().getClientSecret() != clientSecret) {
    //    throw new Error('invalid client');
    //  }
    // todo: validation for scope is needed ?


    String[] clientScope = client.getScope().split(" ");

    return new CredentialsDto(clientId, clientSecret, clientScope);
  }
}
