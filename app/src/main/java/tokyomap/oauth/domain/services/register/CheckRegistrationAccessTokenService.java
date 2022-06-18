package tokyomap.oauth.domain.services.register;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.utils.Logger;

@Service
public class CheckRegistrationAccessTokenService {

  private final ClientLogic clientLogic;
  private final Logger logger;

  @Autowired
  public CheckRegistrationAccessTokenService(ClientLogic clientLogic, Logger logger) {
    this.clientLogic = clientLogic;
    this.logger=logger;
  }

  /**
   * check whether the given client is registered
   * @param clientId
   * @param authorization
   * @return registered client
   */
  public Client checkRegistration(String clientId, String authorization) {

    Client client = this.clientLogic.getClientByClientId(clientId);

    if (client == null) {
      this.logger.log("CheckRegistrationAccessTokenService", "client not found.");
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (authorization == null || authorization.toLowerCase().indexOf("bearer") == -1) {
      this.logger.log("CheckRegistrationAccessTokenService", "unauthorised.");
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    String token = authorization.substring("bearer ".length());

    if (!token.equals(client.getRegistrationAccessToken())) {
      this.logger.log("CheckRegistrationAccessTokenService", "forbidden.");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    return client;
  }
}
