package tokyomap.oauth.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.seasar.framework.util.Base64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the class to manipulate Strings
 */
public class StringUtil {
  private static final Logger logger = LoggerFactory.getLogger(String.class);

  private StringUtil() {
    // encapsulate the constructor
  }

  /**
   * behaves like implode() in PHP
   * @param glue delimiter
   * @param pieces elements in an array
   * @return the concatenated string
   */
  public static String implode(String glue, List<String> pieces) {
    if (pieces.isEmpty()){
      return "";
    }

    StringBuilder sb = new StringBuilder();
    sb.append(pieces.get(0));
    for (int i =1; i < pieces.size(); i++) {
      sb.append(glue);
      sb.append(pieces.get(i));
    }
    return sb.toString();
  }

  /**
   * trim the string by at the given length, and append suffix
   * @param str the string to be manipulated
   * @param width the length the string is trimmed at
   * @param marker the suffix to be appended
   * @return the manipulated string
   */
  public static String strimWidth(String str, int width, String marker) {
    String result = str;
    if (!org.seasar.framework.util.StringUtil.isEmpty(str) && width < str.length()) {
      result = str.substring(0, width);
      if(!org.seasar.framework.util.StringUtil.isEmpty(marker)) {
        result = result.concat(marker);
      }
    }
    return result;
  }

  /**
   * encode the string by MIME Base64
   * @param str the string to be encoded
   * @return the encoded string
   * @see Base64Util#encode(byte[])
   */
  public static String base64Encode(String str) {
    String result = str;
    if(!org.seasar.framework.util.StringUtil.isEmpty(str)) {
      byte[] inStrBytes = str.getBytes(StandardCharsets.UTF_8);
      result = Base64Util.encode(inStrBytes);
    }
    return result;
  }

  /**
   * decode the string by MIME Base64
   * @param str the string to be decoded
   * @return the decoded string
   * @see Base64Util#decode(String)
   */
  public static String base64Decode(String str) {
    String result = str;
    if(!org.seasar.framework.util.StringUtil.isEmpty(str)) {
      byte[] inStrBytes = Base64Util.decode(str);
      result = new String(inStrBytes, StandardCharsets.UTF_8);
    }
    return result;
  }

  /**
   * url-endode the string
   * @param str the string to be encoded
   * @return the encoded string
   * @see URLEncoder#encode(String, String)
   */
  public static String urlEncode(String str) {
    String result = str;
    if(!org.seasar.framework.util.StringUtil.isEmpty(str)) {
      try {
        result = URLEncoder.encode(str, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException e) {
        logger.error("StringUtil#urlEncode(): throw URLEncoder#encode() - value [ " + str + " ]", e);
      }
    }
    return result;
  }

  /**
   * url-decode the string
   * @param str the string to be decoded
   * @return the decoded string
   */
  public static String urlDecode(String str) {
    String result = str;
    if(!org.seasar.framework.util.StringUtil.isEmpty(str)) {
      try {
        result = URLDecoder.decode(str, StandardCharsets.UTF_8.name());
      } catch (UnsupportedEncodingException e) {
        logger.error("StringUtil#urlDecode(): throw URLDecoder#decode() - value [ " + str + " ]", e);
      }
    }
    return result;
  }

  /**
   * convert an InputStream object to a List<String> one
   * @param inputStream
   * @param charset
   * @return
   * @throws Exception
   */
  public static List<String> streamToString(InputStream inputStream, String charset) throws Exception {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
    List<String> lines = new ArrayList<String>();
    String buffer;
    while((buffer = reader.readLine()) != null) {
      lines.add(buffer);
    }
    reader.close();
    return lines;
  }
}
