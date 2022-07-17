package tokyomap.oauth.domain.services.token;

import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.GenerateTokensRequestDto;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Decorder;

public abstract class TokenService<T> {

  private final ClientLogic clientLogic;
  private final Decorder decorder;

  public TokenService(ClientLogic clientLogic, Decorder decorder) {
    this.clientLogic = clientLogic;
    this.decorder = decorder;
  }

  /**
   * execute validation of request to the token endpoint
   * @return TokenValidationResultDto
   */
  public abstract TokenValidationResultDto<T> execValidation(GenerateTokensRequestDto requestDto, String authorization) throws InvalidTokenRequestException;

  /**
   * generate tokens
   * @param tokenValidationResultDto
   * @return GenerateTokensResponseDto
   */
  public abstract GenerateTokensResponseDto execute(TokenValidationResultDto<T> tokenValidationResultDto) throws Exception;

  /**
   * validate client
   * @param requestDto
   * @param authorization
   * @return CredentialsDto
   */
  protected CredentialsDto validateClient(GenerateTokensRequestDto requestDto, String authorization) throws InvalidTokenRequestException {

    String clientId = "";
    String clientSecret = "";

    // fetch clientId & clientSecret from the authorization header or the post params, then check them
    CredentialsDto credentialsDto = this.decorder.decodeCredentials(authorization);

    clientId = credentialsDto.getId();
    clientSecret = credentialsDto.getSecret();

    if (requestDto.getClientId() != null) {
      if (credentialsDto.getId() != null) {
        // return an error if we've already seen the client's credentials in the authorization header
        throw new InvalidTokenRequestException("Invalid Client Id");
      }
      clientId = requestDto.getClientId();
      clientSecret = requestDto.getClientSecret();
    }

    Client client = this.clientLogic.getClientByClientId(clientId);
    if (client == null) {
      throw new InvalidTokenRequestException("No Matching Client");
    }
    if (!client.getClientSecret().equals(clientSecret)) {
      throw new InvalidTokenRequestException("Invalid Client Secret");
    }

    String[] clientScope = client.getScopes().split(" ");

    return new CredentialsDto(clientId, clientSecret, clientScope);
  }
}
