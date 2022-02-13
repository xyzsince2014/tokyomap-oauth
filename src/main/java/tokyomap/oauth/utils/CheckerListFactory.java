package tokyomap.oauth.utils;

import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import org.seasar.framework.util.ClassLoaderUtil;
import org.seasar.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tokyomap.oauth.dtos.RequestBaseInfoDto;

/**
 * the factory class to create the list of Checker that is used by Services#validate
 */
public final class CheckerListFactory {

  private static final Logger logger = LoggerFactory.getLogger(CheckerListFactory.class);

  private CheckerListFactory() {
    // encapsulate the constructor
  }

  /**
   * create a Checker list
   * validations must be set in validate/***.properties
   * @param requestDto request info
   * @return the Checker list
   */
  public static List<Checker> makeCheckerList(RequestBaseInfoDto requestDto) {
    List<Checker> checkerList = new ArrayList<Checker>();

    // read the properties files
    Properties properties = loadValidationProperties(requestDto);

    for(Entry<Object, Object> entry: properties.entrySet()) {
      // dismantle xx_xx.yy_yy=NAME,foo,bar(?|?) to
      // pnames[] = {"xx_xx", "yy_yy"}
      // name = "NAME"
      // checkers[] = {"foo", "bar(?|?)"}
      String [] pnames = ((String) entry.getKey()).split("\\.");
      String [] validations = ((String) entry.getValue()).split(",");
      String name = validations[0];
      String [] checkers = new String[validations.length - 1];

      if (validations.length > 1) {
        System.arraycopy(validations, 1, checkers, 0, (validations.length - 1));
      }

      // ignore items out of validations
      if (checkers.length <= 0) {
        continue;
      }

      try {
        checkerList.addAll(makeCheckList(requestDto, name, pnames, checkers));
      } catch (Exception e) {
        String dtoName = requestDto.getClass().getSimpleName();
        logger.error("failed to create a Checker - dto [" + dtoName + " ]", e);
      }
    }
    return checkerList;
  }

  /**
   * create a Checker
   * @param requestDto request info
   * @param name property name
   * @param pnames properties' names
   * @param checkers Chcker objects to create
   * @return Checker list
   * @throws Exception in case error occurs
   */
  private static List<Checker> makeCheckList(RequestBaseInfoDto requestDto, String name, String[] pnames, String[] checkers) throws Exception {
    return makeCheckList(requestDto, name, pnames, checkers, requestDto, new ArrayList<Integer>());
  }

  /**
   * create a checker list
   * 指定されたdtoからpropertyをreadしてCheckerをcreate
   * propertyがlistを持つならlist件数分recursive処理
   * @param dto the dto to get properties from
   * @param name property name
   * @param pnames properties' names
   * @param checkers checkers to create
   * @param requestDto request info
   * @param idxList indexes for the list
   * @return Checker list
   * @throws Exception in case error occurs
   */
  private static List<Checker> makeCheckList(Object dto, String name, String[] pnames, String[] checkers, RequestBaseInfoDto requestDto, List<Integer> idxList) throws Exception {
    List<Checker> checkerList = new ArrayList<Checker>();

    // get value
    Object value = dto;
    for (int i = 0; i < pnames.length; i++) {
      value = getValue(value, pnames[i]);
      if (value == null) {
        // null if value cannot be fetched
        break;
      }

      if (value instanceof List) {
        // in case the value is of List<Dto>
        String[] recursivePnames = new String[(pnames.length - (i + 1))];
        System.arraycopy(pnames, (i + 1), recursivePnames, 0, (pnames.length - (i + 1)));
        idxList.add(0);
        for (Object recursiveDto: (List<?>) value) {
          checkerList.addAll(makeCheckList(requestDto, name, recursivePnames, checkers, requestDto, idxList));
          idxList.set((idxList.size() - 1), idxList.get(idxList.size() - 1) + 1);
        }
        idxList.remove(idxList.size() - 1);
        return checkerList;
      }
    }

    // create the Checker list
    for (String checker: checkers) {
      checkerList.add(getChecker(checker, value, name, requestDto, idxList));
    }
    return checkerList;
  }

  /**
   * instantiate a Checker class
   * @param checker the name of Checker object to create
   * @param value property's value
   * @param name property name
   * @param requestDto request info
   * @param idxList indexes for the list
   * @return Checker object
   * @throws Exception in case an error occurs
   */
  private static Checker getChecker(String checker, Object value, String name, RequestBaseInfoDto requestDto, List<Integer> idxList) throws Exception {
    // dissolve foo(?|?) to
    // checkName = "foo"
    // params = List<Object> {"?", "?"}
    String[] temp = checker.split("\\."); // [0]: "foo", [1]: null or "?|?)"
    String checkerName = temp[0];
    List<Object> params = new ArrayList<Object>();
    params.add(value);
    if (temp.length > 1) {
      temp = temp[1].substring(0, (temp[1].length() - 1)).split("\\|"); // [*]: "?"
      for (String param: temp) {
        params.add(convertParam(param, requestDto, idxList));
      }
    }

    Class<?> clazz = Class.forName("tokyomap.oauth.utils.checker." + StringUtil.camelize(checkerName) + "Checker");
    Constructor<?> constructor = clazz.getConstructor(new Class[] {String.class, Object.class});
    return (Checker) constructor.newInstance(new Object[] { name, params.toArray(new Object[params.size()]) });
  }

  /**
   * get a property's value
   * @param dto the dto to get the value from
   * @param name property's name
   * @return the property's value
   * @throws Exception in case neither the property or the getter exists
   */
  private static Object getValue(Object dto, String name) throws Exception {
    String getter = (name.indexOf("_") >= 0) ? "get" + StringUtil.camelize(name) : name.substring(0, 1).toUpperCase() + name.substring(1);
    Method method = dto.getClass().getMethod(getter, new Class[0]);
    return method.invoke(dto, new Object[0]);
  }

  /**
   * convert the params to give to Checker
   * @param param parameter string
   * @param requestDto request dto
   * @param idxList indexes for list
   * @return converted param
   * @throws Exception in case an error occurs
   */
  private static Object convertParam(String param, RequestBaseInfoDto requestDto, List<Integer> idxList) throws Exception {
    Object result = param;

    if(param.startsWith("@")) {
      // substitute the property with its value in case the property starts with "@"
      String[] pnames = param.substring(1, param.length()).split("\\.");
      Object value = requestDto;
      int idxListIdx = 0;
      for (String pname: pnames) {
        value = getValue(value, pname);
        if (value == null) {
          break;
        }
        if (value instanceof List) {
          value = ((List<?>) value).get(idxList.get(idxListIdx));
          idxListIdx++;
        }
      }
      result = value;
    }
    return result;
  }

  /**
   * load a validation file
   * create a filename from RequestBaseInfoDto object, and then read the properties file
   * @param requestDto request info
   * @return ApiProperties object
   */
  private static Properties loadValidationProperties(RequestBaseInfoDto requestDto) {
    Properties properties = new Properties();
    String filename = getPropertiesFilename(requestDto);

    try {
      InputStreamReader reader = new InputStreamReader(ClassLoaderUtil.getClassLoader(
          CheckerListFactory.class).getResourceAsStream(filename),  StandardCharsets.UTF_8);
      properties.load(reader);
      reader.close();
    } catch (Exception e) {
      logger.error("invalid properties file - " + filename + e);
    }

    if(!properties.isEmpty()) {
      logger.debug("load properties file - " + filename);
    }

    return properties;
  }

  /**
   * get the validation setting file's name
   * @param requestDto request info
   * @return the validation setting file's name
   */
  private static String getPropertiesFilename(RequestBaseInfoDto requestDto) {
    StringBuffer sb = new StringBuffer();
    sb.append("valdiations/");
    sb.append(StringUtil.decamelize(requestDto.getClass().getSimpleName().toLowerCase()));
    sb.append(".properties");
    return sb.toString();
  }
}
