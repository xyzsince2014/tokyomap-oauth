package tokyomap.oauth.domain.entities.redis;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ProAuthoriseCache implements Serializable {

  private static final long serialVersionUID = -5514415646648723349L;

  private String sub;
  private String[] scopeRequested;
  private PreAuthoriseCache preAuthoriseCache;

  // used to deserialisation by RedisTemplate
  public ProAuthoriseCache() {}

  public ProAuthoriseCache(String sub, String[] scopeRequested, PreAuthoriseCache preAuthoriseCache) {
    this.sub = sub;
    this.scopeRequested = scopeRequested;
    this.preAuthoriseCache = preAuthoriseCache;
  }

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public String[] getScopeRequested() {
    return scopeRequested;
  }

  public void setScopeRequested(String[] scopeRequested) {
    this.scopeRequested = scopeRequested;
  }

  public PreAuthoriseCache getAuthReqParams() {
    return preAuthoriseCache;
  }

  public void setAuthReqParams(PreAuthoriseCache preAuthoriseCache) {
    this.preAuthoriseCache = preAuthoriseCache;
  }

  @Override
  public String toString() {
    return "sub = " + this.sub
        + ", scopeRequested = " + Arrays.stream(this.scopeRequested).collect(Collectors.joining (","))
        + ", preAuthoriseCache = " + this.preAuthoriseCache.toString();
  }
}
