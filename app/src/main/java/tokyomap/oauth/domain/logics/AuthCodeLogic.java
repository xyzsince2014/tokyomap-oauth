package tokyomap.oauth.domain.logics;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.entities.redis.ProAuthoriseCache;

@Component
public class AuthCodeLogic {

  private final RedisTemplate<String, ProAuthoriseCache> authCodeRedisTemplate;

  public AuthCodeLogic(RedisTemplate<String, ProAuthoriseCache> authCodeRedisTemplate) {
    this.authCodeRedisTemplate = authCodeRedisTemplate;
  }

  public ProAuthoriseCache getCacheByCode(String code) {
    return this.authCodeRedisTemplate.opsForValue().get(code);
  }
}
