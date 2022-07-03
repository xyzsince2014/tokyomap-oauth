package tokyomap.oauth.domain.services.revoke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.services.common.TokenScrutiny;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.RevokeRequestDto;
import tokyomap.oauth.utils.Decorder;

@Service
public class RevokeService {

  private final TokenScrutiny tokenScrutiny;
  private final ClientLogic clientLogic;
  private final TokenLogic tokenLogic;
  private final Decorder decorder;

  @Autowired
  public RevokeService(
      TokenScrutiny tokenScrutiny,
      ClientLogic clientLogic,
      TokenLogic tokenLogic,
      Decorder decorder
  ) {
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
  public void revoke(RevokeRequestDto requestDto, String authorization) {

    CredentialsDto credentialsDto = this.decorder.decodeCredentials(authorization);

    Client client = this.clientLogic.getClientByClientId(credentialsDto.getId());
    if (client == null || !client.getClientSecret().equals(credentialsDto.getSecret())) {
      throw new RevocationFailureException("invalid client");
    }

    try {
      this.tokenScrutiny.execute(requestDto.getAccessToken());
      this.tokenLogic.revokeTokens(requestDto.getAccessToken(), requestDto.getRefreshToken());
    } catch(Exception e) {
      throw new RevocationFailureException(e.getMessage());
    }
  }
}
