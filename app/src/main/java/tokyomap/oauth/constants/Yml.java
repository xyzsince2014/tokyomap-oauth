package tokyomap.oauth.constants;

import java.util.HashMap;
import java.util.Map;
import org.seasar.framework.util.ClassLoaderUtil;
import org.ho.yml.Yaml;

public final class Yml {

  public static enum OAuth {

    /** OAuth. */
    OAUTH("/yml/oauth.yml");

    private String yml;

    OAuth(String yml) {this.yml = yml;}

    public String getYml() {return this.yml;}
  }

  public static enum Miscellaneous {

    /** hoge. */
    HOGE("/yml/hoge.yml"),

    /** fuga. */
    FUGA("/yml/fuga.yml"),

    /** miscellaneous. */
    MISCELLANEOUS("/yml/miscellaneous.yml");

    private String yml;

    Miscellaneous(String yml) {}

    public String getYml() {return this.yml;}

  }

  /** map to have configurations. */
  private static Map<OAuth, Map<String ,Object>> configMap = new HashMap();

  private Yml() {}

  static {
    for (OAuth yml: OAuth.values()) {
      Map<String, Object> map = Yaml.load(ClassLoaderUtil.getClassLoader(OAuth.class).getResourceAsStream(yml.getYml()));
      configMap.put(config, map);
    }
  }

  /**
   * get configs in ymls
   * @param yml {@link Yml.OAuth}
   * @param key
   * @return defined value by key `HOGE.FUGA`
   */
  public static String get(OAuth yml, String key) {
    String[] keys = key.split("\\.");
    return get(yml, keys);
  }

  /**
   * get configs in ymls
   * @param yml {@link Yml.OAuth}
   * @param keys
   * @return defined value by key `HOGE.FUGA`, return null if not found
   */
  public static String get(OAuth yml, String... keys) {
    Map<String, Object> map = configMap.get(yml);
    int i = 0;
    for (; i < (keys.length - 1); i++) {
      map = getMap(keys[i], map);
      if (map == null) {
        return null;
      }
    }
    return getString(keys[i], map);
  }

  /**
   * get config in yml as a map
   * @param yml
   * @param key
   * @return
   */
  public static Map<String, Object> getMap(OAuth yml, String key) {
    return getMap(key, configMap.get(yml));
  }

  /**
   * get an item of map as a String
   * @param key
   * @param map
   * @return
   */
  public static String getString(String key, Map<String, Object> map) {
    Object val = map.get(key);
    return val == null ? null : String.valueOf(val);
  }

  /**
   * get an item of map as a map
   * @param key
   * @param map
   * @return
   */
  public static Map<String, Object> getMap(String key, Map<String , Object> map) {
    Object val = map.get(key);
    return val == null ? null : (Map<String, Object>) val;
  }
}
