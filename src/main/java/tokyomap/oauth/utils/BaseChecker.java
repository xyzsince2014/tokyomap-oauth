package tokyomap.oauth.utils;

import java.util.List;

public abstract class BaseChecker implements Checker {
  /** error status */
  private boolean error;

  /** message */
  private String message;

  /** property name */
  private final String name;

  /** args for check */
  private final Object[] args;

  public BaseChecker(String name, Object ...args) {
    this.error = false;
    this.name = name;
    this.args = args;
  }

  @Override
  public boolean check() {
    return this.error;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  protected String getName() {
    return this.name;
  }

  protected Object[] getArgs() {
    return this.args;
  }

  protected void setError(String message) {
    this.error = true;
    setMessage(message);
  }

  protected void setError(String format, Object ...args) {
    this.error = true;
    setMessage(format, args);
  }

  protected void setMessage(String message) {this.message = message;}

  protected void setMessage(String format, Object ...args) {
    setMessage(String.format(format, args));
  }

  /**
   * check if an Object object is mepty
   * @param input the object to be checked
   * @return the result
   */
  protected boolean isBlank(Object input) {

    if (input == null) {
      return true;
    }

    if (input instanceof List) {
      List<?> values = (List<?>) input;
      if (values.isEmpty()) {
        return true;
      }
      return false;
    }

    if(input instanceof Object[]) {
      Object[] values = (Object[]) input;
      if(values.length <= 0) {
        return true;
      }
      return false;
    }

    String value = String.valueOf(input);
    if(value.length() <= 0) {
      return true;
    }
    return false;
  }

}
