package tokyomap.oauth.domain.services.authorise;

import java.util.Arrays;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.entities.redis.PreAuthoriseCache;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.domain.logics.RedisLogic;
import tokyomap.oauth.dtos.PreAuthoriseResponseDto;

@Service
public class PreAuthoriseService {

  private final RedisLogic redisLogic;
  private final ClientLogic clientLogic;

  @Autowired
  public PreAuthoriseService(RedisLogic redisLogic, ClientLogic clientLogic) {
    this.redisLogic = redisLogic;
    this.clientLogic = clientLogic;
  }

  /**
   * validate the given preAuthoriseCache, and cache it if valid
   * @param preAuthoriseCache
   * @return PreAuthoriseResponseDto
   * @exception throws Exception if request is invalid or something wrong takes place
   */
  @Transactional
  public PreAuthoriseResponseDto execute(PreAuthoriseCache preAuthoriseCache) throws InvalidPreAuthoriseException {
    ValidationResult validationResult = this.validateAuthorisationRequest(preAuthoriseCache);
    String requestId = this.cacheAuthorisationRequest(preAuthoriseCache);
    return new PreAuthoriseResponseDto(validationResult.getClient(), requestId, validationResult.getRequestedScope());
  }

  /**
   * validate the preAuthoriseCache
   * @param preAuthoriseCache
   * @return ValidationResult
   */
  private ValidationResult validateAuthorisationRequest(PreAuthoriseCache preAuthoriseCache) {

    Client client = this.clientLogic.getClientByClientId(preAuthoriseCache.getClientId());

    if(client == null) {
      // todo: make a logging aspect for Exceptions thrown
      throw new InvalidPreAuthoriseException("No Matching Client", client.getClientUri());
    }

    if(client.getRedirectUris().indexOf(preAuthoriseCache.getRedirectUri()) == -1) {
      throw new InvalidPreAuthoriseException("Invalid Redirect URI", client.getClientUri());
    }

    String[] requestedScopes = preAuthoriseCache.getScopes();

    if(!Arrays.asList(client.getScopes().split(" ")).containsAll(Arrays.asList(requestedScopes))) {
      throw new InvalidPreAuthoriseException("Invalid Scopes Requested", client.getClientUri());
    }

    if(preAuthoriseCache.getNonce() == null) {
      throw new InvalidPreAuthoriseException("No Nonce Given", client.getClientUri());
    }

    return new ValidationResult(client, requestedScopes);
  }

  /**
   * issue a requestId, and cache the preAuthoriseCache to Redis with it
   * @param preAuthoriseCache
   * @return requestId
   */
  private String cacheAuthorisationRequest(PreAuthoriseCache preAuthoriseCache) {
    String requestId = RandomStringUtils.random(8, true, true);
    this.redisLogic.savePreAuthoriseCache(requestId, preAuthoriseCache);
    return requestId;
  }

  /**
   * validation result
   */
  private class ValidationResult {
    private Client client;
    private String[] requestedScopes;

    ValidationResult(Client client, String[] requestedScopes) {
      this.client = client;
      this.requestedScopes = requestedScopes;
    }

    Client getClient() {
      return client;
    }

    String[] getRequestedScope() {
      return requestedScopes;
    }
  }
}
