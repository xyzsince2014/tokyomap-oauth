package tokyomap.oauth.domain.services.register;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;

@Service
public class CheckRegistrationAccessTokenService {

  private final ClientLogic clientLogic;

  @Autowired
  public CheckRegistrationAccessTokenService(ClientLogic clientLogic) {
    this.clientLogic = clientLogic;
  }

  public Client checkRegistration(String clientId, String authorization) {

    Client client = this.clientLogic.getClientByClientId(clientId);

    if (client == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (authorization == null || authorization.toLowerCase().indexOf("bearer") == -1) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // token value itself is case sensitive, hence we slice the original string, not a transformed one
    String token = authorization.substring("bearer ".length());
    if (!token.equals(client.getRegistrationAccessToken())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    return client;
  }
}
