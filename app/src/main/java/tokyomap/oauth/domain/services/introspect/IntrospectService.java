package tokyomap.oauth.domain.services.introspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.AccessToken;
import tokyomap.oauth.domain.entities.postgres.Resource;
import tokyomap.oauth.domain.logics.ResourceLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

@Service
public class IntrospectService {

  private final Decorder decorder;
  private final ResourceLogic resourceLogic;
  private final TokenLogic tokenLogic;
  private final Logger logger;

  @Autowired
  public IntrospectService(Decorder decorder, ResourceLogic resourceLogic, TokenLogic tokenLogic, Logger logger) {
    this.decorder = decorder;
    this.resourceLogic = resourceLogic;
    this.tokenLogic = tokenLogic;
    this.logger = logger;
  }

  @Transactional
  public Boolean introspect(String incomingToken, String authorization) {
    CredentialsDto credentialsDto = this.decorder.decodeCredentials(authorization); // fetch resourceId, resourceSecret from the Authorization header
    Resource resource = this.resourceLogic.getResourceByResourceId(credentialsDto.getId());

    if (resource == null || !resource.getResourceSecret().equals(credentialsDto.getSecret())) {
      this.logger.log("IntrospectService", "resource.getResourceSecret() = " + resource.getResourceSecret() + ", credentialsDto.getSecret() = " + credentialsDto.getSecret());
      return false;
    }

  // todo:
  //  if (!jose.jws.JWS.verify(incomingToken, jose.KEYUTIL.getKey(config.rsaPublicKey), [config.rsaPublicKey.alg])) {
  //    return false;
  //  }

    AccessToken accessToken = this.tokenLogic.getAccessToken(incomingToken);

    if (accessToken == null) {
      this.logger.log("IntrospectService", "accessToken is null");
      return false;
    }

    return true;
  }
}
