package tokyomap.oauth.utils;

/**
 * the interface for Checker class that executes validations
 */
public interface Checker {

  /**
   * execute validation
   * @return the result of validations
   */
  boolean check();

  /**
   * get the error message for validation errors
   * @return
   */
  String getMessage();

  /**
   * get the error status
   * @return the error status
   */
  boolean isError();
}
