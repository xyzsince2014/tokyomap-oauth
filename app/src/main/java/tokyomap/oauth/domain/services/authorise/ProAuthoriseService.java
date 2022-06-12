package tokyomap.oauth.domain.services.authorise;

import java.net.URI;
import java.util.Arrays;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.entities.redis.ProAuthoriseCache;
import tokyomap.oauth.domain.entities.redis.PreAuthoriseCache;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.domain.logics.UsrLogic;
import tokyomap.oauth.application.authorise.AuthorisationForm;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;
import tokyomap.oauth.utils.Logger;

@Service
public class ProAuthoriseService {

  private final RedisTemplate<String, PreAuthoriseCache> preAuthoriseCacheRedisTemplate;
  private final RedisTemplate<String, ProAuthoriseCache> proAuthoriseCacheRedisTemplate;
  private final TokenLogic tokenLogic;
  private final UsrLogic usrLogic;
  private final Logger logger;

  @Autowired
  public ProAuthoriseService(
      RedisTemplate<String, PreAuthoriseCache> preAuthoriseCacheRedisTemplate,
      RedisTemplate<String, ProAuthoriseCache> proAuthoriseCacheRedisTemplate,
      TokenLogic tokenLogic,
      UsrLogic usrLogic,
      Logger logger
  ) {
    this.preAuthoriseCacheRedisTemplate = preAuthoriseCacheRedisTemplate;
    this.proAuthoriseCacheRedisTemplate = proAuthoriseCacheRedisTemplate;
    this.tokenLogic = tokenLogic;
    this.usrLogic = usrLogic;
    this.logger = logger;
  }

  /**
   * execute authorisation for the authorisationForm given
   * @param authorisationForm
   * @return redirectUri
   */
  public URI execute(AuthorisationForm authorisationForm) {

    AuthenticationResult authenticationResult = this.authenticate(authorisationForm);

    switch (authenticationResult.getAuthorisationRequest().getResponseType()) {
      case "AUTHORISATION_CODE": { // for the Authorisation Code Flow
        URI redirectUri = this.issueCode(authenticationResult);
        return redirectUri;
      }
      case "TOKEN": { // for the Implicit Flow
        TokenValidationResultDto<ProAuthoriseCache> tokenValidationResultDto = this.execValidation(authenticationResult);
        URI redirectUri = this.generateAccessToken(tokenValidationResultDto, authenticationResult.getAuthorisationRequest());
        return redirectUri;
      }
      default:
        throw new InvalidProAuthoriseException("unsupported responseType.");
    }
  }

  /**
   * authenticate the username and password, and fetch the authReqParams for the requestId
   * @param authorisationForm
   * @return AuthenticationResult
   */
  private AuthenticationResult authenticate(AuthorisationForm authorisationForm) {

    String username = authorisationForm.getUsername();
    String password = authorisationForm.getPassword();
    String requestId = authorisationForm.getRequestId();

    Usr usr = this.usrLogic.getUsrByName(username);

    if (usr == null || usr.getPassword() == password) {
      throw new InvalidProAuthoriseException("authentication failed.");
    }

    PreAuthoriseCache preAuthoriseCache = preAuthoriseCacheRedisTemplate.opsForValue().get(requestId);

    String[] requestedScopes = preAuthoriseCache.getScopes();

    if(!Arrays.asList(usr.getScopes().split(" ")).containsAll(Arrays.asList(requestedScopes))) {
      this.logger.log(
          "ProAuthoriseService",
          "Arrays.asList(usr.getScopes().split(\" \")) = " + Arrays.asList(usr.getScopes().split(" ")) + ", Arrays.asList(requestedScopes) = " + Arrays.asList(requestedScopes)
      );
      throw new InvalidProAuthoriseException("invalid scope requested.");
    }

    return new AuthenticationResult(usr, requestedScopes, preAuthoriseCache);
  }

  /**
   * issue an Authorisation Code and cache the associated info
   * @param authenticationResult
   * @return redirectUri
   */
  private URI issueCode(AuthenticationResult authenticationResult) {
    String sub = authenticationResult.getUsr().getSub();
    String[] requestedScopes = authenticationResult.getScopesRequested();
    PreAuthoriseCache preAuthoriseCache = authenticationResult.getAuthorisationRequest();

    String code = RandomStringUtils.random(8, true, true);
    
    ProAuthoriseCache proAuthoriseCache = new ProAuthoriseCache(sub, requestedScopes, preAuthoriseCache);
    this.proAuthoriseCacheRedisTemplate.opsForValue().set(code, proAuthoriseCache);

    URI redirectUri = UriComponentsBuilder.fromUriString(preAuthoriseCache.getRedirectUri())
        .queryParam("code", code).queryParam("state", preAuthoriseCache.getState())
        .build().toUri();

    return redirectUri;
  }

  /**
   * execute validation for the Implicit Code Flow
   * @param authenticationResult
   * @return TokenValidationResultDto<ProAuthoriseCache>
   */
  private TokenValidationResultDto<ProAuthoriseCache> execValidation(AuthenticationResult authenticationResult) {

    String sub = authenticationResult.getUsr().getSub();
    String[] requestedScopes = authenticationResult.getScopesRequested();
    PreAuthoriseCache preAuthoriseCache = authenticationResult.getAuthorisationRequest();

    // todo: execute validation here ?

    ProAuthoriseCache proAuthoriseCache = new ProAuthoriseCache(sub, requestedScopes, preAuthoriseCache);

    return new TokenValidationResultDto(preAuthoriseCache.getClientId(), proAuthoriseCache);
  }

  /**
   * generate an access token for a requests from a valid Browser Application Client
   * @return
   */
  private URI generateAccessToken(TokenValidationResultDto<ProAuthoriseCache> tokenValidationResultDto, PreAuthoriseCache preAuthoriseCache) {

    try {
      GenerateTokensResponseDto responseDto = this.tokenLogic.generateTokens(
          tokenValidationResultDto.getClientId(), tokenValidationResultDto.getPayload().getSub(),
          tokenValidationResultDto.getPayload().getScopeRequested(),
          true, null
      );

      String fragment = "accessToken=" + responseDto.getAccessToken() + "&idToken=" + responseDto.getIdToken()
          + "&state=" + preAuthoriseCache.getState() + "&scope=" + String.join(" ", preAuthoriseCache.getScopes());

      URI redirectUri = UriComponentsBuilder.fromUriString(preAuthoriseCache.getRedirectUri()).fragment(fragment).build().toUri();

      return redirectUri;

    } catch (Exception e) {
      throw new InvalidProAuthoriseException("failed to generate access token, " + e);
    }
  }

  private class AuthenticationResult {
    private Usr usr;
    private String[] requestedScopes;
    private PreAuthoriseCache preAuthoriseCache;

    AuthenticationResult(Usr usr, String[] requestedScopes, PreAuthoriseCache preAuthoriseCache) {
      this.usr = usr;
      this.requestedScopes = requestedScopes;
      this.preAuthoriseCache = preAuthoriseCache;
    }

    public Usr getUsr() {
      return usr;
    }

    public void setUsr(Usr usr) {
      this.usr = usr;
    }

    public String[] getScopesRequested() {
      return requestedScopes;
    }

    public void setScopesRequested(String[] requestedScopes) {
      this.requestedScopes = requestedScopes;
    }

    public PreAuthoriseCache getAuthorisationRequest() {
      return preAuthoriseCache;
    }

    public void setAuthorisationRequest(PreAuthoriseCache preAuthoriseCache) {
      this.preAuthoriseCache = preAuthoriseCache;
    }
  }
}