package tokyomap.oauth.common;

public final class ApiConstants {
  private ApiConstants() {
    // encapsulate
  }

  /** 正常終了 */
  public static final String SUCCESS = "0";

  /** Application Error */
  public static final String APPLICATION_ERROR = "8000";

  /** System Error */
  public static final String SYSTEM_ERROR = "9000";

  /** true flag */
  public static final Integer FLAG_TRUE = 1;

  /** false flag */
  public static final Integer FLAG_FALSE = 0;

  /** Redirect flag */
  public static enum REDIRECT_FLG {
    REDIRECT(FLAG_TRUE),
    NOT_REDIRECT(FLAG_FALSE);

    private int flg;

    REDIRECT_FLG(int flg) {this.flg = flg;}

    public int getFlg() {
      return this.flg;
    }
  }
}
