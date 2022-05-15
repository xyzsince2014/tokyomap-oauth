package tokyomap.oauth.domain.logics;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.entities.redis.AuthCache;

@Component
public class AuthCodeLogic {

  private final RedisTemplate<String, AuthCache> authCodeRedisTemplate;

  public AuthCodeLogic(RedisTemplate<String, AuthCache> authCodeRedisTemplate) {
    this.authCodeRedisTemplate = authCodeRedisTemplate;
  }

  public AuthCache getCacheByCode(String code) {
    return this.authCodeRedisTemplate.opsForValue().get(code);
  }
}
