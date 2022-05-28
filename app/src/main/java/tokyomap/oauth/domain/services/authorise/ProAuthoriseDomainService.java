package tokyomap.oauth.domain.services.authorise;

import java.net.URI;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.entities.redis.AuthCache;
import tokyomap.oauth.domain.entities.redis.AuthReqParams;
import tokyomap.oauth.domain.repositories.postgres.UsrRepository;
import tokyomap.oauth.application.authorise.AuthorisationForm;

@Service
public class ProAuthoriseDomainService {

  private final RedisTemplate<String, AuthReqParams> authReqParamsRedisTemplate;
  private final RedisTemplate<String, AuthCache> authCodeRedisTemplate;
  private final UsrRepository usrRepository;

  @Autowired
  public ProAuthoriseDomainService(RedisTemplate<String, AuthReqParams> authReqParamsRedisTemplate, RedisTemplate<String, AuthCache> authCodeRedisTemplate, UsrRepository usrRepository) {
    this.authReqParamsRedisTemplate = authReqParamsRedisTemplate;
    this.authCodeRedisTemplate = authCodeRedisTemplate;
    this.usrRepository = usrRepository;
  }

  /**
   * execute authorisation for the authorisationForm given
   * @param authorisationForm
   * @return redirectUri
   */
  public URI execute(AuthorisationForm authorisationForm) {
    // todo: console.log(`${util.fetchCurrentDatetimeJst()} [proAuthoriseService.execute]`);

    AuthenticationResult result = this.authenticate(authorisationForm.getUsername(), authorisationForm.getPassword(), authorisationForm.getRequestId());

    URI redirectUri = null;

    switch (result.getAuthReqParams().getResponseType()) {
      case "AUTHORISATION_CODE":
        // for the Authorisation Code Flow
        redirectUri = issueCode(result.getUsr().getSub(), result.getScopeRequested(), result.getAuthReqParams());
        break;
      // todo:
      //  case "TOKEN":
      //    url = issueTokenForBrowerApp(result.getUsr().getSub(), result.getScopeRequested(), result.getAuthReqParams()); // for the Implicit Flow
      //    break;
      default:
        // todo: throw new Error('Unsupported responseType');
    }

    return redirectUri;
  }

  /**
   * authenticate the username and password, and fetch the authReqParams for the requestId
   * @param username
   * @param password
   * @param requestId
   * @return AuthenticationResult
   */
  private AuthenticationResult authenticate(String username, String password, String requestId) {

    Usr usr = this.usrRepository.findByName(username);
    if (usr == null || usr.getPassword() == password) {
      // todo: throw new Error('authentication failed');
    }

    AuthReqParams authReqParams = authReqParamsRedisTemplate.opsForValue().get(requestId);

    String[] scopeRequested = authReqParams.getScope().split(" ");
    // todo:
    //    if (!util.isObjectlncluded(scopeRequested, user.scope)) { // the requested scope must be included by the user's scope
    //      throw new Error(`invalid scope requested: scopeRequested = ${JSON.stringify(scopeRequested)}, user.scope = ${JSON.stringify(user.scope)}`);
    //    }

    return new AuthenticationResult(usr, scopeRequested, authReqParams);
  }

  /**
   * issue an Authorisation Code and cache the associated info
   * @param sub
   * @param scopeRequested
   * @param authReqParams
   * @return redirectUri
   */
  private URI issueCode(String sub, String[] scopeRequested, AuthReqParams authReqParams) {
    String code = RandomStringUtils.random(8, true, true);

    // todo: error handling for registration
    AuthCache authCache = new AuthCache(sub, scopeRequested, authReqParams);
    this.authCodeRedisTemplate.opsForValue().set(code, authCache);

    URI redirectUri = UriComponentsBuilder.fromUriString(authReqParams.getRedirectUri())
        .queryParam("code", code).queryParam("state", authReqParams.getState())
        .build().toUri();

    return redirectUri;
  }
}
