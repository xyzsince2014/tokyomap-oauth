package tokyomap.oauth.domain.logics;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tokyomap.oauth.domain.entities.redis.PreAuthoriseCache;
import tokyomap.oauth.domain.entities.redis.ProAuthoriseCache;

@Component
public class RedisLogic {

  private final RedisTemplate<String, PreAuthoriseCache> preAuthoriseCacheRedisTemplate;
  private final RedisTemplate<String, ProAuthoriseCache> proAuthoriseCacheRedisTemplate;

  public RedisLogic(
      RedisTemplate<String, PreAuthoriseCache> preAuthoriseCacheRedisTemplate,
      RedisTemplate<String, ProAuthoriseCache> proAuthoriseCacheRedisTemplate
  ) {
    this.preAuthoriseCacheRedisTemplate = preAuthoriseCacheRedisTemplate;
    this.proAuthoriseCacheRedisTemplate = proAuthoriseCacheRedisTemplate;
  }

  public PreAuthoriseCache getPreAuthoriseCache(String key) {
    return this.preAuthoriseCacheRedisTemplate.opsForValue().get(key);
  }

  public void savePreAuthoriseCache(String key, PreAuthoriseCache preAuthoriseCache) {
    this.preAuthoriseCacheRedisTemplate.opsForValue().set(key, preAuthoriseCache);
  }

  public ProAuthoriseCache getProAuthoriseCache(String key) {
    return this.proAuthoriseCacheRedisTemplate.opsForValue().get(key);
  }

  public void saveProAuthoriseCache(String key, ProAuthoriseCache proAuthoriseCache) {
    this.proAuthoriseCacheRedisTemplate.opsForValue().set(key, proAuthoriseCache);
    this.proAuthoriseCacheRedisTemplate.expire(key, 10, TimeUnit.MINUTES);
  }
}
