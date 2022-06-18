package tokyomap.oauth.constants;

import tokyomap.oauth.constants.Yml.YAML_FILES;

public final class Constants {

  private Constants() {}

  public static final String HOO = "0";


  public static enum YAMLS {
    // usable by `Constants.YAMLS.FOO.getFuga()
    FOO(Integer.parseInt(Yml.get(YAMLS.HOGE, "HOGE", "FOO"))),
    BOO(Yml.get(YAML_FILES.HOGE, "HOGE", "BOO")));
private String fuga;

YAMLS(String fuga) {this.fuga = fuga;}

    public String getFuga() {return this.fuga;}
  }
  }
}
