package tokyomap.oauth.domain.services.api.v1.introspect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.AccessToken;
import tokyomap.oauth.domain.entities.postgres.Resource;
import tokyomap.oauth.domain.logics.ResourceLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.services.api.v1.ApiException;
import tokyomap.oauth.domain.services.common.TokenScrutiny;
import tokyomap.oauth.dtos.CredentialsDto;
import tokyomap.oauth.utils.Decorder;

@Service
public class IntrospectService {

  // todo: use global constants
  private static final String ERROR_MESSAGE_INVALID_RESOURCE = "Invalid Resource";
  private static final String ERROR_MESSAGE_INVALID_ACCESS_TOKEN = "Invalid Access Token";
  private static final String ERROR_MESSAGE_NO_AUTHORIZATION_HEADER = "No Authorization Header";

  private final TokenScrutiny tokenScrutiny;
  private final Decorder decorder;
  private final ResourceLogic resourceLogic;
  private final TokenLogic tokenLogic;

  @Autowired
  public IntrospectService(TokenScrutiny tokenScrutiny, Decorder decorder, ResourceLogic resourceLogic, TokenLogic tokenLogic) {
    this.tokenScrutiny = tokenScrutiny;
    this.decorder = decorder;
    this.resourceLogic = resourceLogic;
    this.tokenLogic = tokenLogic;
  }

  /**
   * execute introspection of the given access token
   * @param incomingToken
   * @param authorization
   * @return Boolean
   */
  public Boolean execute(String incomingToken, String authorization) throws Exception {

    // fetch resourceId, resourceSecret from the Authorization header
    CredentialsDto credentialsDto = this.decorder.decodeCredentials(authorization);
    if (credentialsDto == null) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, ERROR_MESSAGE_NO_AUTHORIZATION_HEADER);
    }

    Resource resource = this.resourceLogic.getResourceByResourceId(credentialsDto.getId());
    if (resource == null || !resource.getResourceSecret().equals(credentialsDto.getSecret())) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, ERROR_MESSAGE_INVALID_RESOURCE);
    }

    // todo: handle TokenScrutinyFailureException
    this.tokenScrutiny.execute(incomingToken);

    AccessToken accessToken = this.tokenLogic.getAccessToken(incomingToken);
    if (accessToken == null) {
      throw new ApiException(HttpStatus.UNAUTHORIZED, ERROR_MESSAGE_INVALID_ACCESS_TOKEN);
    }

    return true;
  }
}
