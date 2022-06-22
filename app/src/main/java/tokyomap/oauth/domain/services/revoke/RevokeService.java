package tokyomap.oauth.domain.services.revoke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.dtos.RevokeRequestDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

@Service
public class RevokeService {

  private final ClientLogic clientLogic;
  private final TokenLogic tokenLogic;
  private final Logger logger;
  private final Decorder decorder;

  @Autowired
  public RevokeService(ClientLogic clientLogic, TokenLogic tokenLogic, Logger logger, Decorder decorder) {
    this.clientLogic = clientLogic;
    this.tokenLogic = tokenLogic;
    this.logger = logger;
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

    String accessToken = requestDto.getAccessToken();
    String refreshToken = requestDto.getRefreshToken();

    // todo:
    //  const publicKey = jose.KEYUTIL.getKey(config.rsaPublicKey);
    //  if (!jose.jws.JWS.verify(accessToken, publicKey, [config.rsaPublicKey.alg])) {
    //    throw new Error(`[revokeLogic.revoke] incoming accessToken invalid`);
    //  }

    this.tokenLogic.revokeTokens(accessToken, refreshToken);
  }
}
