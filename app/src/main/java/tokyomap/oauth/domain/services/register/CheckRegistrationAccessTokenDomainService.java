package tokyomap.oauth.domain.services.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;

@Service
public class CheckRegistrationAccessTokenDomainService {

  private final ClientLogic clientLogic;

  @Autowired
  public CheckRegistrationAccessTokenDomainService(ClientLogic clientLogic) {
    this.clientLogic = clientLogic;
  }

  public Client checkRegistration(String clientId, String authorization) {
    Client client = this.clientLogic.getClientByClientId(clientId);

    if (client == null) {
      // todo: throw new Error(statusCodes.NOT_FOUND);
    }

    // todo: // authorization.toLowerCase() may not be necessary
    if (authorization == null || authorization.toLowerCase().indexOf("bearer") == -1) {
      // todo: throw new Error(statusCodes.UNAUTHORIZED);
    }

    // token value itself is case sensitive, hence we slice the original string, not a transformed one
    String token = authorization.substring("bearer ".length());
    if (!token.equals(client.getRegistrationAccessToken())) {
      // todo: throw new Error(statusCodes.FORBIDDEN);
    }

    return client;
  }
}
