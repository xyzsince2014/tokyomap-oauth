package tokyomap.oauth.domain.services.authorise;

import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tokyomap.oauth.domain.entities.postgres.Client;
import tokyomap.oauth.domain.entities.redis.AuthReqParams;
import tokyomap.oauth.domain.repositories.postgres.ClientRepository;
import tokyomap.oauth.dtos.userinfo.authorisation.PreAuthoriseResponseDto;

@Service
public class PreAuthoriseDomainService {

  private final RedisTemplate<String, AuthReqParams> authReqParamsRedisTemplate;
  private final ClientRepository clientRepository;

  @Autowired
  public PreAuthoriseDomainService(RedisTemplate<String, AuthReqParams> authReqParamsRedisTemplate, ClientRepository clientRepository) {
    this.authReqParamsRedisTemplate = authReqParamsRedisTemplate;
    this.clientRepository = clientRepository;
  }

  /**
   * validates the given request params, then cache them in the Redis
   * @param authReqParams
   * @return PreAuthoriseResponseDto
   */
  public PreAuthoriseResponseDto execute(AuthReqParams authReqParams) {
    // todo: logging `${util.fetchCurrentDatetimeJst()} [preAuthoriseService.execute]` with aspect
    ValidateAuthReqParamsResult result = this.validateAuthReqParams(authReqParams);
    String requestId = this.registerParams(authReqParams);
    return new PreAuthoriseResponseDto(result.getClient(), requestId, result.getRequestedScope());
  }

  /**
   * validate the authReqParams
   * @param authReqParams
   * @return ValidateAuthReqParamsResult
   */
  // todo: @Transactional
  private ValidateAuthReqParamsResult validateAuthReqParams(AuthReqParams authReqParams) {
    // todo: console.log(`${util.fetchCurrentDatetimeJst()} [preAuthoriseLogic.validateParams] authReqParams = ${JSON.stringify(authReqParams)}`);

    Optional<Client> optionalClient = this.clientRepository.findById(authReqParams.getClientId());

    // todo: handle null more succinctly
    if(optionalClient == null) {
      throw new ClientNotFoundException();
    }

    Client client = optionalClient.get();

    if(client.getRedirectUris().indexOf(authReqParams.getRedirectUri()) == -1) {
      throw new InvalidRedirectUriException();
    }

    String[] requestedScope = authReqParams.getScope().split(" ");
    // todo: String[] scope = client.getScope().split(" ");
    // todo: throw new Error('invalid scope requested'); if requestedScope is not included by scope

    ValidateAuthReqParamsResult result = new ValidateAuthReqParamsResult(client, requestedScope);
    return result;
  }

  /**
   * issue a requestId, and register the authReqParams to Redis with it
   * @param authReqParams
   * @return requestId
   */
  private String registerParams(AuthReqParams authReqParams) {
    String requestId = RandomStringUtils.random(8, true, true);
    this.authReqParamsRedisTemplate.opsForValue().set(requestId, authReqParams);
    return requestId;
  }
}
