package tokyomap.oauth.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validator {
  private static final Logger logger = LoggerFactory.getLogger(Validator.class);

  /** host name */
  private static final String HOST_NAME;
  static {
    String hostName;
    try {
      hostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      hostName = "unknown host";
    }
    HOST_NAME = hostName;
  }

  /** error flag */
  private boolean error;

  /** message */
  private String message;

  /**
   * execute validations
   * @param checkerList list of Checker objects
   * @param params paramters
   * @return the result of validations
   */
  public boolean execute(List<?> checkerList, Object params) {
    for (Object obj: checkerList) {
      if (obj instanceof List) {
        if (!execute((List<?>) obj, params)) {
          return !isError();
        }
      } else {
        Checker checker = (Checker) obj;
        if (!checker.check()) {
          setError(checker.getMessage());
          logger.error(makeLogString(checker.getMessage(), params));
          return !isError();
        }
      }
    }
    return !isError();
  }

  public boolean isError() {return this.error;}

  public String getMessage() {return this.message;}

  private void setError(String message) {
    this.error = true;
    this.message = message;
  }

  /**
   * make an error log string
   * @param message
   * @param params
   * @return an error log string
   */
  private String makeLogString(String message, Object params) {
    StringBuilder sb = new StringBuilder();
    sb.append("message = {")
        .append(message)
        .append("},")
        .append(System.lineSeparator())
        .append("hostName = {")
        .append(HOST_NAME)
        .append("},")
        .append(System.lineSeparator())
        .append("params = {")
        .append(makeParamsString(params))
        .append("}");
    return sb.toString();
  }

  /**
   * make a parameter string
   * @param params parameters
   * @return a parameter string
   */
  private String makeParamsString(Object params) {
    if (params == null) {
      return "null";
    }

    if (params instanceof Object[]) {
      StringBuilder sb = new StringBuilder(System.lineSeparator());
      int i = 1;
      for (Object param: (Object[]) params) {
        sb.append("[")
            .append(String.valueOf(i))
            .append("] => [")
            .append(makeParamsString(params))
            .append("]")
            .append(System.lineSeparator());
        i++;
      }
      return sb.toString();
    }

    if (params instanceof List) {
      StringBuilder sb = new StringBuilder(System.lineSeparator());
      int i = 1;
      for (Object param: (List<?>) params) {
        sb.append("[")
            .append(String.valueOf(i))
            .append("] => [")
            .append(makeParamsString(param))
            .append("]")
            .append(System.lineSeparator());
        i++;
      }
      return sb.toString();
    }

    return params.toString();
  }
}
