package tokyomap.oauth.common;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.seasar.framework.util.ClassLoaderUtil;
import tokyomap.oauth.utils.ApiRequest;

;

/**
 * the class to hold properties in config/***.properties
 */
public final class ApiProperties {

  /** external properties types */
  public static enum PROPERTIES_TYPE {
    /** message */
    MESSAGE("conf/message.properties"),
    /** fuga */
    ERR_CODE("conf/err_code.properties");

    /** 設定ファイル名 */
    private String file;

    PROPERTIES_TYPE(String file) {this.file = file;}

    /**
     * 設定ファイル名を取得
     * @return 設定ファイル名.
     */
    public String getFile() {
      return file;
    }
  }

  /** logger */
  private static final Logger logger = LoggerFactory.getLogger(ApiProperties.class);

  /** the Map object to hold external configs */
  private static Map<PROPERTIES_TYPE, Properties> propertiesMap = new HashMap<PROPERTIES_TYPE, Properties>();

  static {
    for(PROPERTIES_TYPE propertiesType: PROPERTIES_TYPE.values()) {
      Properties properties = new Properties();
      try {
        InputStreamReader reader = new InputStreamReader(
            ClassLoaderUtil.getClassLoader(ApiProperties.class).getResourceAsStream(propertiesType.getFile()),
            StandardCharsets.UTF_8
        );
        properties.load(reader);
        reader.close();
      } catch (Exception e) {
        logger.error("invalid properties file. - " + propertiesType.getFile());
      }
      if(!properties.isEmpty()) {
        logger.info("load properties file. - " + propertiesType.getFile());
      }
      propertiesMap.put(propertiesType, properties);
    }
  }

  /** the constructor */
  private ApiProperties() {
    // encapsulate the constructor
  }

  /**
   * 指定されたkeyを持つproperty値を返却
   * @param propertiesType {@link ApiProperties.PROPERTIES_TYPE}
   * @param key
   * @return property
   */
  public static String getProperty(PROPERTIES_TYPE propertiesType, String key) {
    return propertiesMap.get(propertiesType).getProperty(key);
  }

  /**
   * 指定されたkeyを持つproperty値を返却
   * @param propertiesType
   * @param key
   * @param apiRequest
   * @return
   */
  public static String getProperty(PROPERTIES_TYPE propertiesType, String key, ApiRequest apiRequest) {
    StringBuilder modKey = new StringBuilder(key).append(".").append(apiRequest.getHoge());
    return getProperty(propertiesType, modKey.toString());
  }

  /**
   * 指定されたキーを持つプロパティ値を返却する.<br />
   * 指定されたキーのプロパティ値が見つからなかった場合は指定されたデフォルト値を返却する.<br />
   * @param kind {@link ApiProperties.PROPERTIES_TYPE}
   * @param key キー
   * @param defaultValue デフォルト値
   * @return プロパティ値
   * @see Properties#getProperty(String, String)
   */
  public static String getProperty(PROPERTIES_TYPE kind, String key, String defaultValue) {
    return propertiesMap.get(kind).getProperty(key, defaultValue);
  }

//  /**
//   * 指定されたキーのメッセージを整形して返却する.<br />
//   * メッセージに含まれる"%s"部を空文字に置き換えて返却する.<br />
//   * @param key キー
//   * @return 整形されたメッセージ
//   */
//  public static String getMessage(String key) {
//    return getMessage(key, "");
//  }

  /**
   * 指定されたキーのメッセージを整形して返却する.<br />
   * メッセージに含まれる"%s"部を指定された詳細メッセージに置き換えて返却する.<br />
   * @param key キー
   * @param args 詳細メッセージ
   * @return 整形されたメッセージ
   */
  public static String getMessage(String key, Object... args) {
    String message = getProperty(PROPERTIES_TYPE.MESSAGE, key);
    if (null != message) {
      return String.format(message, args);
    }
    return null;
  }
}
