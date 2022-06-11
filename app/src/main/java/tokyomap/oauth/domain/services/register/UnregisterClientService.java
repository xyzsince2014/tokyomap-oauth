package tokyomap.oauth.domain.services.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.TokenLogic;

@Service
public class UnregisterClientService {

  private final TokenLogic tokenLogic;
  private final ClientLogic clientLogic;

  @Autowired
  public UnregisterClientService(TokenLogic tokenLogic, ClientLogic clientLogic) {
    this.tokenLogic = tokenLogic;
    this.clientLogic = clientLogic;
  }

  /**
   * unregister the client and its tokens fot the given clientId
   * @param clientId
   * @param accessToken
   * @param refreshToken
   */
  public void unregister(String clientId, String accessToken, String refreshToken) {
    this.clientLogic.unregisterClient(clientId);
    this.tokenLogic.revokeTokens(accessToken, refreshToken);
  }
}
