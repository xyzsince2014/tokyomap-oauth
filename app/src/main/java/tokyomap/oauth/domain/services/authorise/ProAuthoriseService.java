package tokyomap.oauth.domain.services.authorise;

import java.net.URI;
import java.util.Arrays;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import tokyomap.oauth.application.authorise.AuthorisationForm;
import tokyomap.oauth.domain.entities.postgres.Usr;
import tokyomap.oauth.domain.entities.redis.PreAuthoriseCache;
import tokyomap.oauth.domain.entities.redis.ProAuthoriseCache;
import tokyomap.oauth.domain.logics.RedisLogic;
import tokyomap.oauth.domain.logics.TokenLogic;
import tokyomap.oauth.dtos.GenerateTokensResponseDto;
import tokyomap.oauth.dtos.TokenValidationResultDto;

@Service
public class ProAuthoriseService {

  private final RedisLogic redisLogic;
  private final TokenLogic tokenLogic;

  @Autowired
  public ProAuthoriseService(RedisLogic redisLogic, TokenLogic tokenLogic) {
    this.redisLogic = redisLogic;
    this.tokenLogic = tokenLogic;
  }

  /**
   * execute authorisation for the authorisationForm given
   * @param
   * @param authorisationForm
   * @return redirectUri
   */
  @Transactional
  public URI execute(Usr resourceOwner, AuthorisationForm authorisationForm) throws InvalidProAuthoriseException {

    AuthenticationResult authenticationResult = this.authenticate(resourceOwner, authorisationForm);

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
        throw new InvalidProAuthoriseException("Invalid Response Type");
    }
  }

  /**
   * authenticate the username and password, and fetch the preAuthoriseCache for the requestId
   * @param authorisationForm
   * @return AuthenticationResult
   */
  private AuthenticationResult authenticate(Usr resourceOwner, AuthorisationForm authorisationForm) {

    String requestId = authorisationForm.getRequestId();

    PreAuthoriseCache preAuthoriseCache = this.redisLogic.getPreAuthoriseCache(requestId);

    if (preAuthoriseCache == null) {
      throw new InvalidProAuthoriseException("Invalid RequestId");
    }

    String[] requestedScopes = preAuthoriseCache.getScopes();

    if(!Arrays.asList(resourceOwner.getScopes().split(" ")).containsAll(Arrays.asList(requestedScopes))) {
      throw new InvalidProAuthoriseException("Invalid Scopes Requested");
    }

    return new AuthenticationResult(resourceOwner, requestedScopes, preAuthoriseCache);
  }

  /**
   * issue an Authorisation Code and cache the associated info
   * @param authenticationResult
   * @return redirectUri
   */
  private URI issueCode(AuthenticationResult authenticationResult) {
    String sub = authenticationResult.getResourceOwner().getSub();
    String[] requestedScopes = authenticationResult.getScopesRequested();
    PreAuthoriseCache preAuthoriseCache = authenticationResult.getAuthorisationRequest();

    String code = RandomStringUtils.random(8, true, true);
    
    ProAuthoriseCache proAuthoriseCache = new ProAuthoriseCache(sub, requestedScopes, preAuthoriseCache);
    this.redisLogic.saveProAuthoriseCache(code, proAuthoriseCache);

    URI redirectUri = UriComponentsBuilder
        .fromUriString(preAuthoriseCache.getRedirectUri())
        .queryParam("code", code)
        .queryParam("state", preAuthoriseCache.getState())
        .build()
        .toUri();

    return redirectUri;
  }

  /**
   * execute validation for the Implicit Code Flow
   * @param authenticationResult
   * @return TokenValidationResultDto<ProAuthoriseCache>
   */
  private TokenValidationResultDto<ProAuthoriseCache> execValidation(AuthenticationResult authenticationResult) {

    String sub = authenticationResult.getResourceOwner().getSub();
    String[] requestedScopes = authenticationResult.getScopesRequested();
    PreAuthoriseCache preAuthoriseCache = authenticationResult.getAuthorisationRequest();

    ProAuthoriseCache proAuthoriseCache = new ProAuthoriseCache(sub, requestedScopes, preAuthoriseCache);

    return new TokenValidationResultDto(preAuthoriseCache.getClientId(), proAuthoriseCache);
  }

  /**
   * generate an access token for a requests from a valid Browser Application Client
   * set to be a protected function because of warning on `@Transactional`
   * @return redirectUri
   */
  @Transactional
  protected URI generateAccessToken(TokenValidationResultDto<ProAuthoriseCache> tokenValidationResultDto, PreAuthoriseCache preAuthoriseCache) {

    try {
      GenerateTokensResponseDto responseDto = this.tokenLogic.generateTokens(
          tokenValidationResultDto.getClientId(), tokenValidationResultDto.getPayload().getSub(),
          tokenValidationResultDto.getPayload().getScopeRequested(), true,
          tokenValidationResultDto.getPayload().getPreAuthoriseCache().getNonce()
      );

      String fragment = "accessToken=" + responseDto.getAccessToken() + "&idToken=" + responseDto.getIdToken()
          + "&state=" + preAuthoriseCache.getState() + "&scopes=" + String.join(" ", preAuthoriseCache.getScopes())
          + "&nonce=" + preAuthoriseCache.getNonce();

      URI redirectUri = UriComponentsBuilder
          .fromUriString(preAuthoriseCache.getRedirectUri())
          .fragment(fragment)
          .build()
          .toUri();

      return redirectUri;

    } catch (Exception e) {
      throw new InvalidProAuthoriseException("failed to generate access token, " + e);
    }
  }

  private class AuthenticationResult {
    private Usr resourceOwner;
    private String[] requestedScopes;
    private PreAuthoriseCache preAuthoriseCache;

    AuthenticationResult(Usr resourceOwner, String[] requestedScopes, PreAuthoriseCache preAuthoriseCache) {
      this.resourceOwner = resourceOwner;
      this.requestedScopes = requestedScopes;
      this.preAuthoriseCache = preAuthoriseCache;
    }

    public Usr getResourceOwner() {
      return resourceOwner;
    }

    public void setResourceOwner(Usr resourceOwner) {
      this.resourceOwner = resourceOwner;
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
