package tokyomap.oauth.domain.services.introspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.AccessToken;
import tokyomap.oauth.domain.entities.postgres.Resource;
import tokyomap.oauth.domain.logics.ResourceLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.services.common.TokenScrutiny;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.utils.Decorder;
import tokyomap.oauth.utils.Logger;

@Service
public class IntrospectService {

  private final TokenScrutiny tokenScrutiny;
  private final Decorder decorder;
  private final ResourceLogic resourceLogic;
  private final TokenLogic tokenLogic;
  private final Logger logger;

  @Autowired
  public IntrospectService(
      TokenScrutiny tokenScrutiny,
      Decorder decorder,
      ResourceLogic resourceLogic,
      TokenLogic tokenLogic,
      Logger logger
  ) {
    this.tokenScrutiny = tokenScrutiny;
    this.decorder = decorder;
    this.resourceLogic = resourceLogic;
    this.tokenLogic = tokenLogic;
    this.logger = logger;
  }

  /**
   * execute introspection of the given access token
   * @param incomingToken
   * @param authorization
   * @return Boolean
   */
  public Boolean execute(String incomingToken, String authorization) {

    CredentialsDto credentialsDto = this.decorder.decodeCredentials(authorization); // fetch resourceId, resourceSecret from the Authorization header
    Resource resource = this.resourceLogic.getResourceByResourceId(credentialsDto.getId());

    if (resource == null || !resource.getResourceSecret().equals(credentialsDto.getSecret())) {
      this.logger.log(
          IntrospectService.class.getName(),
          "resource.getResourceSecret() = " + resource.getResourceSecret() + ", credentialsDto.getSecret() = " + credentialsDto.getSecret()
      );
      return false;
    }

    try {
      this.tokenScrutiny.execute(incomingToken);

      AccessToken accessToken = this.tokenLogic.getAccessToken(incomingToken);
      if (accessToken == null) {
        this.logger.log(IntrospectService.class.getName(), "accessToken is null");
        return false;
      }

      return true;

    } catch(Exception e) {
      return false;
    }
  }
}
