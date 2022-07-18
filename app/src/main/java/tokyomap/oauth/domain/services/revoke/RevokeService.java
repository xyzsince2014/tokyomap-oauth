package tokyomap.oauth.domain.services.revoke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.services.api.v1.ApiException;
import tokyomap.oauth.domain.services.common.TokenScrutiny;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.RevokeRequestDto;
import tokyomap.oauth.utils.Decorder;

@Service
public class RevokeService {

  // todo: use global constants
  private static final String ERROR_MESSAGE_INVALID_CLIENT = "Invalid Client";
  private static final String ERROR_MESSAGE_NO_AUTHORIZATION_HEADER = "No Authorization Header";

  private final TokenScrutiny tokenScrutiny;
  private final ClientLogic clientLogic;
  private final TokenLogic tokenLogic;
  private final Decorder decorder;

  @Autowired
  public RevokeService(TokenScrutiny tokenScrutiny, ClientLogic clientLogic, TokenLogic tokenLogic, Decorder decorder) {
    this.tokenScrutiny = tokenScrutiny;
    this.clientLogic = clientLogic;
    this.tokenLogic = tokenLogic;
    this.decorder = decorder;
  }

  /**
   * revoke the given access and refresh tokens
   * @param requestDto
   * @param authorization
   */
  @Transactional
  public void execute(RevokeRequestDto requestDto, String authorization) throws Exception {

    CredentialsDto credentialsDto = this.decorder.decodeCredentials(authorization);
    if (credentialsDto == null) {
      throw new ApiException(null, ERROR_MESSAGE_NO_AUTHORIZATION_HEADER);
    }

    Client client = this.clientLogic.getClientByClientId(credentialsDto.getId());
    if (client == null || !client.getClientSecret().equals(credentialsDto.getSecret())) {
      throw new ApiException(null, ERROR_MESSAGE_INVALID_CLIENT);
    }

    this.tokenScrutiny.execute(requestDto.getAccessToken());
    this.tokenLogic.revokeTokens(requestDto.getAccessToken(), requestDto.getRefreshToken());
  }
}
