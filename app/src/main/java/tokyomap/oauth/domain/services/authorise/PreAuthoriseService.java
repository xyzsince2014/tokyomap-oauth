package tokyomap.oauth.domain.services.authorise;

import java.util.Arrays;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.entities.redis.PreAuthoriseCache;
import tokyomap.oauth.domain.logics.ClientLogic;
import tokyomap.oauth.dtos.PreAuthoriseResponseDto;
import tokyomap.oauth.utils.Logger;

@Service
public class PreAuthoriseService {

  private final RedisTemplate<String, PreAuthoriseCache> AuthorisationRequestRedisTemplate;
  private final ClientLogic clientLogic;
  private final Logger logger;

  @Autowired
  public PreAuthoriseService(
      RedisTemplate<String, PreAuthoriseCache> AuthorisationRequestRedisTemplate,
      ClientLogic clientLogic,
      Logger logger
  ) {
    this.AuthorisationRequestRedisTemplate = AuthorisationRequestRedisTemplate;
    this.clientLogic = clientLogic;
    this.logger = logger;
  }

  /**
   * validate the given preAuthoriseCache, and cache it if valid
   * @param preAuthoriseCache
   * @return PreAuthoriseResponseDto
   */
  @Transactional
  public PreAuthoriseResponseDto execute(PreAuthoriseCache preAuthoriseCache) {
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
      throw new InvalidPreAuthoriseException("no matching client.");
    }

    if(client.getRedirectUris().indexOf(preAuthoriseCache.getRedirectUri()) == -1) {
      throw new InvalidPreAuthoriseException("invalid redirect uri requested.");
    }

    String[] requestedScopes = preAuthoriseCache.getScopes();

    if(!Arrays.asList(client.getScopes().split(" ")).containsAll(Arrays.asList(requestedScopes))) {
      throw new InvalidPreAuthoriseException("invalid scopes requested.");
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
    this.AuthorisationRequestRedisTemplate.opsForValue().set(requestId, preAuthoriseCache);
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
