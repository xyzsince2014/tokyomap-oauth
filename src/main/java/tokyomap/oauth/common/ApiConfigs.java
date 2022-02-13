package tokyomap.oauth.common;

import java.util.HashMap;
import java.util.Map;

import org.seasar.framework.util.ClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ho.yaml.Yaml;

/**
 * the class to hold external configs in config/***.yml
 */
public final class ApiConfigs {

  /** external configuration types */
  public static enum CONFIG_TYPE {
    /** 設定ファイル hoge */
    HOGE("conf/hoge.yml"), // or HOGE("classpath:/conf/hoge.yml")

    /** 設定ファイル fuga */
    FUGA("conf/fuga.yml");

    /** 設置ファイル名 */
    private String file;

    CONFIG_TYPE(String file) {this.file = file;}

    /**
     * 設定ファイル名を取得
     * @return 設定ファイル名.
     */
    public String getFile() {
      return file;
    }
  }

  /** logger */
  private static final Logger logger = LoggerFactory.getLogger(ApiConfigs.class);

  /** the Map object to hold external configs */
  private static Map<CONFIG_TYPE, Map<String, Object>> configMap = new HashMap();

  static {
    for(CONFIG_TYPE configType: CONFIG_TYPE.values()) {
      Map<String, Object> config =
          (Map<String, Object>) Yaml.load(ClassLoaderUtil.getClassLoader(ApiConfigs.class).getResourceAsStream(configType.getFile()));
      configMap.put(configType, config);
      logger.info("load config file. - " + configType.getFile());
    }
  }

  /** the constructor */
  private ApiConfigs() {
    // encapsulate the constructor
  }

  /**
   * get external configs
   * @param configType {@link ApiConfigs.CONFIG_TYPE}
   * @param key "."区切りで階層指定可能
   * @return 指定したkeyの定義値, 定義値がなければnull
   */
  public  static String get(CONFIG_TYPE configType, String key) {
    String[] keys = key.split("\\.");
    return get(configType, keys);
  }

  /**
   * get external configs
   * @param configType {@link ApiConfigs.CONFIG_TYPE}
   * @param keys 可変長引数による階層指定
   * @return 指定したkeyの定義値, 定義値がなければnull
   */
  public static String get(CONFIG_TYPE configType, String ...keys) {
    Map<String, Object> map = configMap.get(configType);
    int index = 0;
    for(; index < (keys.length - 1); index++) {
      map = getMap(keys[index], map);
      if(map == null) {
        return null;
      }
    }
    return getString(keys[index], map);
  }

  /**
   * get external configs
   * @param configType {@link ApiConfigs.CONFIG_TYPE}
   * @param key
   * @return 指定したkeyの定義値, 定義値がなければnull
   */
  public static Map<String, Object> getMap(CONFIG_TYPE configType, String key) {
    return getMap(key, configMap.get(configType));
  }

  /**
   * Map<String, Object>から要素をString変換して取得
   * @param key
   * @param map Map<String, Object>
   * @return String返還した要素
   */
  private static String getString(String key, Map<String, Object> map) {
    Object val = map.get(key);
    if(val == null) {
      return null;
    }
    return String.valueOf(val);
  }

  /**
   * Map<String, Object>から要素をMap<String, Object>として取得
   * @param key
   * @param map　Map<String, Object>
   * @return Map<String, Object>に返還した要素
   */
  private static Map<String, Object> getMap(String key, Map<String, Object> map) {
    Object val = map.get(key);
    if(val == null) {
      return null;
    }
    return (Map<String, Object>) val;
  }
}
